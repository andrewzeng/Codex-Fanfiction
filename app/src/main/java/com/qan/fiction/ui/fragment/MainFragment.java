package com.qan.fiction.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qan.fiction.R;
import com.qan.fiction.adapter.MainAdapter;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AlertDialog;
import com.qan.fiction.custom.AppCompatListFragment;
import com.qan.fiction.ui.activity.SettingsActivityModern;
import com.qan.fiction.ui.ao3_activity.AO3_Activity;
import com.qan.fiction.ui.ao3_activity.search.AO3_Container;
import com.qan.fiction.ui.ff_activity.FF_Activity;
import com.qan.fiction.ui.ff_activity.FF_ReaderFragment;
import com.qan.fiction.ui.ff_activity.search.FF_Container;
import com.qan.fiction.ui.fp_activity.FP_Activity;
import com.qan.fiction.ui.fp_activity.search.FP_Container;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.misc.listeners.ViewListener;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MainFragment extends AppCompatListFragment {

    ViewListener callback;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] elem = getResources().getStringArray(com.qan.fiction.R.array.main_menu_list);
        TypedArray draw;
        if (Settings.isLightTheme(getActivity()))
            draw = getResources().obtainTypedArray(R.array.main_menu_drawables);
        else
            draw = getResources().obtainTypedArray(R.array.main_menu_drawables_dark);
        Drawable[] d = new Drawable[elem.length];
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pix = (int) (24 * metrics.scaledDensity);
        for (int i = 0; i < d.length; i++) {
            d[i] = draw.getDrawable(i);
            d[i].setBounds(0, 0, pix, pix);
        }
        setListAdapter(new MainAdapter(getActivity(), elem, d));
        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (ViewListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ViewListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            callback.openFragment(new LibraryFragment(), new Bundle());
        } else if (position == 1) {
            int last = Settings.getLastSite(getActivity());
            if (last == Settings.OFFLINE) {
                String current = Settings.getFile(getActivity());
                if (current != null) {
                    final Bundle b = new Bundle();
                    b.putString("file", current);
                    callback.openFragment(new ReaderFragment(), b);
                }
            } else if (last == Settings.FF_NET) {
                Bundle b = Settings.getOnlineInfo(getActivity());
                b.putString("site", Constants.FF_NET_S);
                callback.openFragment(new FF_ReaderFragment(), b);
            } else if (last == Settings.FP_COM) {
                Bundle b = Settings.getOnlineInfo(getActivity());
                b.putString("site", Constants.FP_COM_S);
                callback.openFragment(new FF_ReaderFragment(), b);
            } else if (last == Settings.AO3) {
                Bundle b = Settings.getOnlineInfo(getActivity());
                b.putString("site", Constants.AO3_S);
                callback.openFragment(new FF_ReaderFragment(), b);
            }
        } else if (position == 2) {
            changeBrowse();
        } else if (position == 3) {
            search();
        } else if (position == 4) {
            openSettings();
        } else if (position == 5) {
            aboutDialog();
        }
    }

    private void aboutDialog() {
        final AlertBuilder builder = new AlertBuilder(getActivity());
        if (Settings.isLightTheme(getActivity()))
            builder.setIcon(R.drawable.about);
        else
            builder.setIcon(R.drawable.about_dark);
        builder.setTitle(getString(R.string.about));
        try {
            ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo(
                    getActivity().getPackageName(), 0);
            ZipFile zip = new ZipFile(info.sourceDir);
            ZipEntry entry = zip.getEntry("classes.dex");
            long time = entry.getTime();
            String date = SimpleDateFormat.getDateInstance().format(new java.util.Date(time));
            String build = "Built on " + date + ". \n\nJSoup, and slf4j used under MIT License." +
                    "\n\nLauncher Icon based on work by Double-J Design, CC Attribution 3.0" +
                    "\n\nepublib by Paul Siegmann used, source can be found at https://github.com/psiegman/epublib.";
            builder.setMessage(build);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPositiveButton(getString(R.string.ok), null);
        AlertDialog d = builder.create();
        d.show();
    }


    private void openSettings() {
        Intent i = new Intent(getActivity(), SettingsActivityModern.class);
        startActivity(i);
    }


    private void changeBrowse() {
        final AlertBuilder builder = new AlertBuilder(getActivity());
        builder.setTitle(getString(R.string.select_site));
        builder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog d = builder.create();
        d.setView(getPopupBrowser(d), 0, 0, 0, 0);
        d.show();
    }

    private ListView getPopupBrowser(final AlertDialog d) {
        ListView v = new ListView(getActivity());
        String[] names = getResources().getStringArray(R.array.sites_web);
        v.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names));
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                d.cancel();
                openBrowser(Settings.site(position));
            }
        });

        return v;
    }

    private void search() {
        final AlertBuilder builder = new AlertBuilder(getActivity());
        builder.setTitle(getString(R.string.select_site));
        builder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog d = builder.create();
        d.setView(searchDialog(d), 0, 0, 0, 0);
        d.show();
    }

    private ListView searchDialog(final AlertDialog d) {
        ListView v = new ListView(getActivity());
        String[] names = getResources().getStringArray(R.array.sites_web);
        v.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, names));
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                d.cancel();
                openSearch(Settings.site(position));
            }
        });

        return v;
    }

    private void openBrowser(String site) {
        Class next = null;
        if (site.equals(Constants.FF_NET_S))
            next = FF_Activity.class;
        else if (site.equals(Constants.FP_COM_S))
            next = FP_Activity.class;
        else if (site.equals(Constants.AO3_S))
            next = AO3_Activity.class;
        Intent i = new Intent(getActivity(), next);
        startActivity(i);
    }

    private void openSearch(String site) {
        if (site.equals(Constants.FF_NET_S))
            callback.openFragment(new FF_Container(), new Bundle());
        else if (site.equals(Constants.FP_COM_S))
            callback.openFragment(new FP_Container(), new Bundle());
        else if (site.equals(Constants.AO3_S))
            callback.openFragment(new AO3_Container(), new Bundle());
    }

}