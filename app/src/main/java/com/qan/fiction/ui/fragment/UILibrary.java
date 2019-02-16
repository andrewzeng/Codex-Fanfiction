package com.qan.fiction.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qan.fiction.R;
import com.qan.fiction.adapter.MainAdapter;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AlertDialog;
import com.qan.fiction.custom.AppCompatFragment;
import com.qan.fiction.ui.service_connection.ConnectionManager;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.download.StoryDownload;
import com.qan.fiction.util.storage.BookExport;
import com.qan.fiction.util.storage.StoryUtils;
import com.qan.fiction.util.storage.entries.Entry;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static com.qan.fiction.util.constants.Conversion.dp;
import static com.qan.fiction.util.constants.Conversion.sp;
import static com.qan.fiction.util.download.StoryDownload.MSG_STOP;

public class UILibrary extends AppCompatFragment {
    private static final int PERMISSIONS_REQUEST_CODE = 0;
    private ProgressDialog d;
    private LibraryFragment.LibraryFragmentListener callback;
    private ConnectionManager manager;

    public void showDialog(final Entry e) {
        final AlertBuilder builder = new AlertBuilder(getActivity());
        TextView t = new TextView(getActivity());
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        t.setLineSpacing(1.0f, 1.1f);
        t.setText(Html.fromHtml(e.info()));
        t.setPadding(10, 0, 0, 0);
        ScrollView sv = new ScrollView(getActivity());
        sv.addView(t);
        builder.setTitle(e.title);
        builder.setView(sv);
        builder.setPositiveButton(getString(R.string.read), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getCallback().onLibraryRead(e);
            }
        });
        builder.setNeutralButton(getString(R.string.more), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                listDialog(e);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        final AlertDialog d = builder.create();
        d.show();
    }

    private void listDialog(final Entry e) {

        final AlertBuilder builder = new AlertBuilder(getActivity());
        builder.setTitle(R.string.options);
        ListView v = getPopupListView();
        builder.setView(v);
        final AlertDialog dialog = builder.create();

        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getCallback().onOnlineRead(e.site, e.file);
                } else if (position == 1) {
                    messageDialog(e.description, getString(R.string.descr), getDescr());
                } else if (position == 2) {
                    //Stop download of the file.
                    makeUpdate(e);
                } else if (position == 3) {
                    //Stop update before we re-download
                    Message msg = Message.obtain(null, MSG_STOP);
                    Bundle bundle = new Bundle();
                    bundle.putString("file", e.file);
                    msg.setData(bundle);
                    msg.replyTo = getManager().getMessenger();
                    try {
                        getManager().send(msg);
                    } catch (RemoteException ignore) {
                    }
                    int link = Integer.parseInt(e.getId());
                    Intent i = new Intent(getActivity(), StoryDownload.class);
                    i.putExtra("id", link);
                    i.putExtra("redownload", true);
                    i.putExtra("site", e.site);
                    setD(ProgressDialog.show(getActivity(), getString(R.string.downloading),
                            getString(R.string.geting_info)));
                    getActivity().startService(i);

                    update();
                } else if (position == 4) {
                    //Stop update
                    Message msg = Message.obtain(null, MSG_STOP);
                    Bundle bundle = new Bundle();
                    bundle.putString("file", e.file);
                    msg.setData(bundle);
                    msg.replyTo = getManager().getMessenger();
                    try {
                        getManager().send(msg);
                    } catch (RemoteException ignore) {
                    }
                    if (checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED &&
                            checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), BookExport.writeToDisk(getActivity(), e),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_CODE);
                    }
                } else if (position == 5) {
                    //Stop update
                    Message msg = Message.obtain(null, MSG_STOP);
                    Bundle bundle = new Bundle();
                    bundle.putString("file", e.file);
                    msg.setData(bundle);
                    msg.replyTo = getManager().getMessenger();
                    try {
                        getManager().send(msg);
                    } catch (RemoteException ignore) {
                    }
                    StoryUtils.delete(getActivity(), e.file);

                    update();
                }

                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // TODO: actually export the e-book after permission is granted.
        }
    }

    private int getDescr() {
        if (Settings.isLightTheme(getActivity()))
            return R.drawable.descr;
        else
            return R.drawable.descr_dark;
    }

    private void makeUpdate(Entry e) {
        makeUpdate(e, true);
    }

    public void makeUpdate(Entry e, boolean showDialog) {
        Message msg = Message.obtain(null, MSG_STOP);
        Bundle bundle = new Bundle();
        bundle.putString("file", e.file);
        msg.setData(bundle);
        msg.replyTo = getManager().getMessenger();
        try {
            getManager().send(msg);
        } catch (RemoteException ignore) {
        }
        Intent intent = new Intent(getActivity(), StoryDownload.class);
        intent.putExtra("id", Integer.parseInt(e.getId()));
        intent.putExtra("site", e.site);
        intent.putExtra("update", true);
        if (showDialog)
            setD(ProgressDialog.show(getActivity(), getString(R.string.downloading),
                    getString(R.string.geting_info)));
        getActivity().startService(intent);
    }

    public void update() {
    }

    protected void messageDialog(String message, String title, int icon) {
        final AlertBuilder builder = new AlertBuilder(getActivity());
        builder.setTitle(title);
        builder.setIcon(icon);
        TextView r = new TextView(getActivity());
        r.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        r.setText(Html.fromHtml(message));
        Context c = getActivity();
        r.setPadding(dp(c, 15), dp(c, 15), dp(c, 15), dp(c, 15));
        ScrollView sv = new ScrollView(getActivity());
        sv.addView(r);
        builder.setView(sv);
        builder.setPositiveButton(getString(R.string.ok), null);
        AlertDialog d = builder.create();
        d.show();
    }

    private ListView getPopupListView() {
        ListView v = new ListView(getActivity());
        String[] names = getResources().getStringArray(R.array.popup);
        TypedArray draw;
        if (Settings.isLightTheme(getActivity()))
            draw = getResources().obtainTypedArray(R.array.popup_drawables);
        else
            draw = getResources().obtainTypedArray(R.array.popup_drawables_dark);

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

    public void download() {
        final AlertBuilder builder = new AlertBuilder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.edit_dialog_new, null);
        final String[] value = {Settings.site(0)};
        final EditText t;

        t = (EditText) v.findViewById(R.id.file_id);
        Spinner s = (Spinner) v.findViewById(R.id.spinner_id);
        String[] names = getResources().getStringArray(R.array.sites_web);
        s.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                names));
        ((ArrayAdapter) s.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value[0] = Settings.site(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        builder.setTitle(getString(R.string.download));
        builder.setIcon(getDownIcon());
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int id = Integer.parseInt(t.getEditableText().toString());
                    Intent i = new Intent(getActivity(), StoryDownload.class);
                    i.putExtra("id", id);
                    i.putExtra("download", true);
                    i.putExtra("site", value[0]);
                    setD(ProgressDialog.show(getActivity(), getString(R.string.downloading),
                            getString(R.string.geting_info)));
                    getActivity().startService(i);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    messageDialog("Input should be numeric.", getString(R.string.download),
                            getDownIcon()); //This line should never run
                }
            }
        });
        AlertDialog d = builder.create();
        d.setView(v, 5, 0, 0, 0);
        d.show();
    }

    private int getDownIcon() {
        if (Settings.isLightTheme(getActivity()))
            return R.drawable.down;
        else
            return R.drawable.down_dark;
    }

    public ProgressDialog getProgress() {
        return d;
    }

    public void setD(ProgressDialog d) {
        this.d = d;
    }

    public LibraryFragment.LibraryFragmentListener getCallback() {
        return callback;
    }

    public void setCallback(LibraryFragment.LibraryFragmentListener callback) {
        this.callback = callback;
    }

    public ConnectionManager getManager() {
        return manager;
    }

    public void setManager(ConnectionManager manager) {
        this.manager = manager;
    }
}
