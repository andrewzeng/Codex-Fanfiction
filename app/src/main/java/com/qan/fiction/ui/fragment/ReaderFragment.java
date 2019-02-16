package com.qan.fiction.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.qan.fiction.R;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AlertDialog;
import com.qan.fiction.custom.AppCompatFragment;
import com.qan.fiction.custom.ReaderView;
import com.qan.fiction.ui.activity.Global;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.storage.DatabaseHandler;
import com.qan.fiction.util.storage.entries.Entry;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.qan.fiction.util.constants.Constants.EX_SIGNAL;
import static com.qan.fiction.util.constants.Constants.MIN_DIST;
import static com.qan.fiction.util.constants.Constants.SWIPE_THRESHOLD_VELOCITY;

public class ReaderFragment extends AppCompatFragment {

    private ScrollView scrollView;
    private ReaderView textView;
    private String location;
    private Spinner spinner;
    private int chapters;
    private int page;
    private GestureDetector detector;
    protected boolean scrollToPosition;
    private ReaderFragmentListener callback;
    private boolean ignore;

    public int getChapters() {
        return chapters;
    }

    public void setChapters(int chapters) {
        this.chapters = chapters;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public interface ReaderFragmentListener {

        public void onReview(Entry e);
    }

    private ArrayList<Page> tasks = new ArrayList<Page>();


    private class CustomDetector extends GestureDetector.SimpleOnGestureListener {

        MotionEvent e;

        @Override
        public boolean onFling(MotionEvent a, MotionEvent b, float vx, float vy) {
            if (a == null)
                a = e;
            if (a == null || b == null)
                return false;

            if (dx(a, b) > MIN_DIST && diff(a, b) > 0 &&
                    Math.abs(vx) > SWIPE_THRESHOLD_VELOCITY && getPage() < getChapters()) {
                next();
            } else if (dx(b, a) > MIN_DIST && diff(a, b) > 0 &&
                    Math.abs(vx) > SWIPE_THRESHOLD_VELOCITY && getPage() > 1) {
                prev();
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            this.e = e;
            return super.onDown(e);
        }

        private float dx(MotionEvent a, MotionEvent b) {
            return a.getX() - b.getX();
        }

        private float dy(MotionEvent a, MotionEvent b) {
            return a.getY() - b.getY();
        }

        private float diff(MotionEvent a, MotionEvent b) {
            return Math.abs(dx(a, b)) - 2 * Math.abs(dy(a, b));
        }

    }

    protected class Page extends AsyncTask<Void, Void, CharSequence> {

        private ProgressDialog d;

        @Override
        protected CharSequence doInBackground(Void... params) {
            String f = getLocation() + "_" + getPage();
            String text;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getActivity().openFileInput(f)));
                StringBuilder buf = new StringBuilder();
                while ((text = br.readLine()) != null)
                    buf.append(text);
                text = buf.toString();
                br.close();
                return Html.fromHtml(text);
            } catch (FileNotFoundException e) {
                Looper.prepare();
                setPage(1);
                Settings.setPage(getActivity(), getLocation(), getPage());
                setContent();
                spinner.setSelection(getPage() - 1);
                Looper.myLooper().quit();
                return EX_SIGNAL;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return EX_SIGNAL;
        }

        @Override
        protected void onPreExecute() {
            d = ProgressDialog.show(getActivity(), getString(R.string.loading), getString(R.string.loading_page), true);
            scrollView.fling(0);
            tasks.add(this);
        }

        protected void onPostExecute(CharSequence s) {
            if (s.equals(EX_SIGNAL)) {
                makeError();
                int p = Settings.getPage(getActivity(), getLocation());
                spinner.setSelection(p - 1);
                if (d != null)
                    d.cancel();
                return;
            }
            try {
                textView.setText(s);
            } catch (IndexOutOfBoundsException e) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
                    textView.setTag(s.toString());
                } else
                    throw e;
            }
            if (d != null)
                d.cancel();
            Settings.setPage(getActivity(), getLocation(), getPage());
            spinner.setSelection(getPage() - 1);
            try {
                if (scrollToPosition) {
                    scrollToPosition = false;
                    scroll(Settings.getOffset(getActivity(), getLocation()));
                } else {
                    scroll(0);
                }
            } catch (Exception ignore) {
            }
            tasks.remove(this);//All tasks are unique, so equals() doesn't need to be implemented
        }

        protected void makeError() {
            AlertBuilder builder = new AlertBuilder(getActivity());
            builder.setTitle(R.string.alert);
            builder.setMessage(R.string.failed_load_page);
            builder.setPositiveButton(getString(R.string.ok), null);
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.create().show();
        }
    }

    private void scroll(final int height) {
        scrollView.postDelayed(new Runnable() {
            public void run() {
                if (textView.getLayout() == null) {
                    ViewTreeObserver vto = textView.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            scrollView.scrollTo(0, textView.getLine(height));
                        }
                    });
                } else
                    scrollView.scrollTo(0, textView.getLine(height));
            }

        }, 100);
    }

    private void prev() {
        if (getPage() > 1) {
            setPage(getPage() - 1);
            Settings.setPage(getActivity(), getLocation(), getPage());
            setContent();
        }
    }

    private void next() {
        if (getPage() < getChapters()) {
            setPage(getPage() + 1);
            Settings.setPage(getActivity(), getLocation(), getPage());
            setContent();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        for (Page p : tasks)
            p.cancel(true);
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (ReaderFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ReaderFragmentListener");
        }
        int a = Settings.getOrientation(activity);
        if (a == Settings.LANDSCAPE)
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else if (a == Settings.PORTRAIT)
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        super.onCreateView(inflater, container, saved);
        ignore = false;
        View v;
        v = inflater.inflate(R.layout.reader, container, false);
        default_view(v);

        return v;
    }


    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        Bundle b = getArguments();
        initialize(saved, b);
        setHasOptionsMenu(true);
    }

    public void initialize(Bundle saved, Bundle b) {
        if (getSupportActivity().getSupportActionBar() != null) {
            getSupportActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (b != null) {
            if (run(b, saved))
                return;
        } else if (saved != null) {
            //Does this ever run?
            if (run(saved, saved))
                return;
        }
        if (getLocation() != null) {
            Settings.setRead(getActivity(), getLocation(), true);
            Settings.setFile(getActivity(), getLocation());
            Settings.setLastSite(getActivity(), Settings.OFFLINE);
        }
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Settings.getScreenSetting(getActivity()))
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public boolean run(Bundle b, Bundle saved) {
        setLocation(b.getString("file"));
        setPage(Settings.getPage(getActivity(), getLocation()));
        DatabaseHandler db = ((Global) getActivity().getApplication()).getDatabase();
        Entry e = db.getStory(getLocation());
        if (e == null) {
            //Story deleted before reading
            ignore = true;
            Settings.setFile(getActivity(), null);
            showWarningDialog();
            return true;
        } else {
            setChapters(e.chapters);
            getSupportActivity().getSupportActionBar().setTitle(e.title);
        }


        if (saved == null || !reset(saved)) {
            //The story is just being opened
            int x = getResources().getConfiguration().orientation;
            int y = getActivity().getRequestedOrientation();
            if (y == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && x != Configuration.ORIENTATION_LANDSCAPE ||
                    y == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && x != Configuration.ORIENTATION_PORTRAIT)
                return true;
            scrollToPosition = true;
            setContent();
        }
        return false;
    }

    private void showWarningDialog() {
        final AlertBuilder build = new AlertBuilder(getActivity());
        build.setTitle(getString(R.string.error));
        build.setMessage(getString(R.string.file_not_found));
        if (Settings.isLightTheme(getActivity()))
            build.setIcon(R.drawable.alert);
        else
            build.setIcon(R.drawable.alert_dark);
        build.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFragmentManager().popBackStack();
            }
        });
        AlertDialog d = build.create();
        d.show();
        d.setCancelOperation(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().popBackStack();
            }
        });
        getActivity().invalidateOptionsMenu();
    }

    protected void set_listeners() {
        detector = new GestureDetector(getActivity(), new CustomDetector());
        View.OnTouchListener touch = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        };
        scrollView.setOnTouchListener(touch);
    }

    private void default_view(View container) {
        scrollView = (ScrollView) container.findViewById(R.id.reader_scroll);
        textView = (ReaderView) container.findViewById(R.id.text_body);
    }


    protected void setContent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
            getDownloadInstance().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            getDownloadInstance().execute();
        set_listeners();
    }

    protected boolean reset(Bundle savedInstanceState) {
        if (!savedInstanceState.containsKey("text"))
            return false;
        textView.setText(savedInstanceState.getCharSequence("text"));
        scroll(Settings.getOffset(getActivity(), getLocation()));
        set_listeners();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (textView.getText().length() != 0 && textView.getLayout() != null)
            Settings.setOffset(getActivity(), getLocation(), textView.getOffset(scrollView.getScrollY()));
    }


    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        if (textView != null && textView.getText().length() != 0) { //Quit during progress bar
            try {
                Settings.setOffset(getActivity(), getLocation(), textView.getOffset(scrollView.getScrollY()));
                out.putCharSequence("text", textView.getText());
                out.putString("file", getLocation());
            } catch (Exception ignore) {
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                next();
                return true;
            case R.id.prev:
                prev();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onCreateOptionsMenu(final Menu menu, MenuInflater m) {
        super.onCreateOptionsMenu(menu, m);
        inflateMenu(menu, m, R.menu.reader);
    }

    protected void inflateMenu(Menu menu, MenuInflater m, int menuId) {
        if (!ignore) {
            m.inflate(menuId, menu);
            spinner = (Spinner) menu.findItem(R.id.chapter).getActionView();
            spinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_text, getStrings()));
            if (getChapters() == 1)
                menu.removeItem(R.id.chapter);
            else {
                spinner.setSelection(getPage() - 1);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        onSpinnerSelected(position);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        }
    }

    private void onSpinnerSelected(int position) {
        setPage(position + 1);
        if (Settings.getPage(getActivity(), getLocation()) != getPage()) {
            scrollToPosition = false;
            setContent();
        }
    }

    public Page getDownloadInstance() {
        return new Page();
    }


    public ArrayList<String> getStrings() {
        ArrayList<String> s = new ArrayList<String>();
        for (int i = 1; i <= getChapters(); i++) {
            s.add("Chapter " + i);
        }
        return s;
    }
}
