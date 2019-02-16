package com.qan.fiction.ui.abs_web_activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.qan.fiction.R;
import com.qan.fiction.adapter.MainAdapter;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AppCompatListFragment;
import com.qan.fiction.ui.abs_web_activity.adapters.StreamAdapter;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.download.Connector;
import com.qan.fiction.util.misc.listeners.Loadable;
import com.qan.fiction.util.misc.listeners.ViewListener;
import com.qan.fiction.util.storage.SerPair;

import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.qan.fiction.util.constants.Constants.SELECTED_STRING;
import static com.qan.fiction.util.constants.Conversion.sp;

public abstract class Paginate<T extends Serializable> extends AppCompatListFragment implements Loadable {


    private ViewListener viewListener;

    protected String category;
    protected boolean dualPane;
    protected HashMap<String, ArrayList<SerPair<String, String>>> fields;
    protected ArrayList<String> exclude;
    protected ArrayList<String> order;
    protected ArrayList<ViewBundle> bundles;
    protected HashMap<String, String> data;
    protected String url;
    protected int page;
    protected int total;
    protected int size;
    protected int next;
    protected ArrayList<T> list;
    private Download d;
    private ArrayList<T> tempList;
    private HashMap<String, String> tempData;


    public ListView getPopupListView() {
        ListView v = new ListView(getActivity());
        String[] names = getResources().getStringArray(R.array.popup_browser);
        TypedArray draw;
        if (Settings.isLightTheme(getActivity()))
            draw = getResources().obtainTypedArray(R.array.popup_drawables_browser);
        else
            draw = getResources().obtainTypedArray(R.array.popup_drawables_browser_dark);

        Drawable[] d = new Drawable[names.length];
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pix = sp(getActivity(), 32);
        for (int i = 0; i < d.length; i++) {
            d[i] = draw.getDrawable(i);
            d[i].setBounds(0, 0, pix, pix);
        }
        v.setAdapter(new MainAdapter(getActivity(), names, d));
        return v;
    }

    public ViewListener getViewListener() {
        return viewListener;
    }

    public void setViewListener(ViewListener viewListener) {
        this.viewListener = viewListener;
    }

    protected class ViewBundle {
        public String info;
        public String name;
        public View view;

        public ViewBundle() {

        }
    }

    protected class StringWrapperUrl {
        public StringBuilder value;
        public HashMap<String, String> filter;

        public StringWrapperUrl append(String s) {
            value = value.append(get(s)).append("/");
            return this;
        }

        public StringWrapperUrl appendAll(List<String> list) {
            for (String s : list)
                append(s);
            return this;
        }

        public StringWrapperUrl appendAll() {
            for (String s : data.keySet())
                append(s);
            return this;
        }


        public StringWrapperUrl(String s, HashMap<String, String> filter) {
            value = new StringBuilder(s);
            this.filter = filter;
        }

        public String get(String s) {
            return data.get(s) == null ? "0" : data.get(s);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    protected class StringWrapperGet extends StringWrapperUrl {

        public boolean first = true;

        public StringWrapperGet(String s, HashMap<String, String> filter) {
            super(s + "?", filter);
        }

        public StringWrapperUrl append(String s) {
            String name = s;
            if (filter != null && filter.get(s) != null)
                name = filter.get(s);
            if (!first)
                value = value.append("&").append(name).append("=").append(get(s));
            else {
                value = value.append(name).append("=").append(get(s));
                first = false;
            }
            return this;
        }
    }

    private class Download extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... params) {
            try {
                putPage(data, String.valueOf(next));
                return Connector.getUrl(url(params[0], next));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (d != null)
                d.cancel(true);
            d = this;

        }

        protected void onPostExecute(Document doc) {
            super.onPostExecute(doc);

            if (doc == null) {
                AlertBuilder builder = new AlertBuilder(getActivity());
                builder.setTitle(R.string.alert);
                if (Connector.isNetworkAvailable(getActivity()))
                    builder.setMessage(R.string.loading_failed);
                else
                    builder.setMessage(getString(R.string.no_internet));
                final boolean go = list == null && tempList == null;
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (go)
                            getFragmentManager().popBackStack();
                    }
                });
                next = page;
                if (list == null) {
                    list = tempList;
                    data = tempData;
                }
                if (list != null)
                    setListAdapter(createAdapter(size = list.size()));
                setListShown(true);
                builder.create().show();
            } else {
                finishDownload(doc);
            }
        }
    }

    public void finishDownload(Document doc) {
        page = next;
        if (list != null)
            list.remove(list.size() - 1);
        else
            list = new ArrayList<T>();
        total = getTotal(doc);
        list.addAll(getListItems(doc));
        StreamAdapter<T> adapter = getAdapter();
        if (total != list.size())
            list.add(getPlaceHolderEntry());
        if (list.size() == 0) {
            AlertBuilder builder = new AlertBuilder(getActivity());
            builder.setTitle(R.string.alert);
            builder.setMessage(getString(R.string.no_stories_found));
            builder.setPositiveButton(getString(R.string.ok), null);
            builder.create().show();
        }
        HashMap<String, ArrayList<SerPair<String, String>>> fields = getFields(doc);
        if (fields.size() != 0) {
            this.fields = fields;
            exclude = getExclude(doc);
            order = order(doc);
        }
        if (adapter == null) {
            setListAdapter(createAdapter(size = list.size()));
        } else {
            adapter.notifyDataSetChanged();
        }
        if (isVisible())
            setListShown(true);
    }

    protected abstract ArrayList<String> getExclude(Document doc);

    public void putPage(HashMap<String, String> data, String s) {
        data.put("p", s);
    }

    public abstract T getPlaceHolderEntry();

    public abstract StreamAdapter<T> createAdapter(int size);

    public abstract ArrayList<T> getListItems(Document doc);

    public abstract int getTotal(Document doc);

    public abstract ArrayList<String> order(Document doc);

    public abstract HashMap<String, ArrayList<SerPair<String, String>>> getFields(Document doc);

    protected abstract String url(String url, int page);

    public abstract String url_mobile(String url, int page);

    protected abstract StreamAdapter<T> getAdapter();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            setViewListener((ViewListener) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ViewListener");
        }
    }

    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        Bundle b = getArguments();
        category = b.getString("name");
        url = b.getString("url");
        setHasOptionsMenu(true);
        data = new HashMap<String, String>();
        if (saved == null || !saved.containsKey("entries") || saved.getSerializable("entries") == null) {
            page = 0;
            next = 0;
            loadNextPage();
        } else {
            restore(saved);
        }
        dualPane = getArguments().getBoolean("dual");
    }


    @SuppressWarnings("unchecked")
    public void restore(Bundle saved) {
        page = saved.getInt("page");
        next = page;
        total = saved.getInt("total");
        exclude = saved.getStringArrayList("exclude");
        data = (HashMap<String, String>) saved.getSerializable("data");
        list = (ArrayList<T>) saved.getSerializable("entries");
        fields = (HashMap<String, ArrayList<SerPair<String, String>>>) saved.getSerializable("fields");
        order = saved.getStringArrayList("order");
        size = saved.getInt("size");
        setListAdapter(createAdapter(size));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getSupportActivity().getSupportActionBar().setTitle(getTitle());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pagination, menu);
    }

    private String getTitle() {
        return category;
    }

    @Override
    public abstract void onListItemClick(ListView l, View v, int position, long id);


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_web_icon:
                web_action();
                return true;
            case R.id.menu_settings:
                if (fields != null && getListAdapter() != null) {
                    makeSettings();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makeSettings() {
        AlertBuilder builder = new AlertBuilder(getActivity());
        builder.setTitle(R.string.sorting_filtering);
        ScrollView view = new ScrollView(getActivity());
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        bundles = new ArrayList<ViewBundle>();
        makeExtras(bundles, layout);
        for (String s : order) {
            ViewBundle bundle = makeSpinner(s);
            bundles.add(bundle);
            layout.addView(bundle.view);
        }
        view.addView(layout);
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, String> transfer;
                transfer = transferData();
                extra_data(transfer);
                putPage(transfer, "1");
                if (transfer.hashCode() != data.hashCode()) {
                    tempData = data;
                    data = transfer;
                    setListAdapter(null);
                    setListShown(false);
                    tempList = list;
                    list = null;
                    //Though overall odds of there being a collision among all possibilities is ~100%,
                    // it's not likely per user.
                    page = 0;
                    next = 0;
                    loadNextPage();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();
    }

    public HashMap<String, String> transferData() {
        HashMap<String, String> transfer = new HashMap<String, String>();
        for (ViewBundle bundle : bundles) {
            int count = 1;
            while (transfer.containsKey(bundle.name + (count == 1 ? "" : count)))
                count++;
            transfer.put(bundle.name + (count == 1 ? "" : count), bundle.info);
        }
        return transfer;
    }


    public void extra_data(HashMap<String, String> data) {
    }

    protected void makeExtras(ArrayList<ViewBundle> bundles, LinearLayout layout) {

    }

    public ViewBundle makeSpinner(String item) {
        final ViewBundle bundle = new ViewBundle();
        bundle.name = item;
        ArrayList<SerPair<String, String>> map = new ArrayList<SerPair<String, String>>(fields.get(item));
        String selected = null;
        for (int i = 0; i < map.size(); i++)
            if (map.get(i).first.equals(SELECTED_STRING)) {
                selected = map.remove(i).second;
                break;
            }
        final ArrayList<CharSequence> key = new ArrayList<>();
        final ArrayList<String> values = new ArrayList<>();
        for (SerPair<String, String> s : map) {
            key.add(s.first);
            values.add(s.second);
        }

        {
            LayoutInflater inflater = getLayoutInflater(null);
            LinearLayout l = (LinearLayout) inflater.inflate(R.layout.spinner_normal, null);
            bundle.view = l;
            Spinner s = (Spinner) l.findViewById(R.id.spinner);
            TextView t = (TextView) l.findViewById(R.id.text);
            s.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, key));
            ((ArrayAdapter) s.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    bundle.info = values.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            bundle.info = values.get(0);
            String text = getString(key, item);
            if (text.contains(":"))
                text = text.substring(0, text.indexOf(":") + 1);
            if (exclude.contains(item)) {
                t.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                t.setText("Exclude " + text);
            } else
                t.setText(text);
            if (selected != null) {
                for (int i = 0; i < key.size(); i++) {
                    if (key.get(i).equals(selected)) {
                        s.setSelection(i);
                        break;
                    }
                }
            }
        }
        return bundle;
    }

    protected String getString(ArrayList<CharSequence> key, String item) {
        return key.get(0).toString().replace("All", "").trim() + ":";
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putInt("page", page);
        ArrayList<T> list;
        HashMap<String, String> data;
        if (this.list == null) {
            list = tempList;
            data = tempData;
        } else {
            list = this.list;
            data = this.data;
        }
        out.putSerializable("data", data);
        out.putStringArrayList("exclude", exclude);
        out.putInt("total", total);
        out.putSerializable("entries", list);
        out.putSerializable("fields", fields);
        out.putStringArrayList("order", order);
        out.putInt("size", size);
    }

    protected void web_action() {
        StreamAdapter adapter = (StreamAdapter) getListAdapter();
        int reader_page = adapter == null ? 1 : adapter.getPage();
        putPage(data, String.valueOf(reader_page));
        String s;
        if (dualPane)
            s = url(url, reader_page);
        else
            s = url_mobile(url, reader_page);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
        startActivity(i);

    }

    public void loadNextPage() {
        if (next == page) {
            next = page + 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
                new Download().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
            else
                new Download().execute(url);
        }
    }


    public void onDestroy() {
        super.onDestroy();
        if (d != null)
            d.cancel(true);
    }

    /**
     * @return The site string provided in {@link Settings}
     */
    public abstract String getSite();
}
