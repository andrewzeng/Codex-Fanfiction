package com.qan.fiction.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import com.qan.fiction.R;
import com.qan.fiction.adapter.expandable.ExpandListAdapter;
import com.qan.fiction.adapter.expandable.ListChild;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AlertDialog;
import com.qan.fiction.ui.service_connection.ConnectionManager;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.download.StoryDownload;
import com.qan.fiction.util.storage.StoryUtils;
import com.qan.fiction.util.storage.entries.Entry;

import java.util.List;

import static com.qan.fiction.util.download.StoryDownload.*;
import static com.qan.fiction.util.storage.StoryUtils.getStories;

public class LibraryFragment extends UILibrary {
    private ExpandListAdapter adapter;
    private boolean bound;
    private ExpandableListView listView;
    private List<Entry> updateList;
    private ProgressDialog m;
    private int total;


    public interface LibraryFragmentListener {
        public void onLibraryRead(Entry read);

        public void onOnlineRead(String site, String id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setManager(new ConnectionManager(new ConnectionManager.OnMessageReceivedListener() {
            @Override
            public void onReceive(Message msg) {
                switch (msg.what) {
                    case MSG_STOP:
                        if (getProgress() != null)
                            getProgress().cancel();
                        break;
                    case MSG_RESULT:
                        if (getActivity() != null)
                            update();
                        if (updateList != null) {
                            if (updateList.size() > 0) {
                                int last = updateList.size() - 1;
                                makeUpdate(updateList.get(last), false);
                                int count = total - updateList.size();
                                updateList.remove(last);
                                if (getActivity() != null) {
                                    m.setMessage("Updated " + count + " of " + total + ".");
                                    m.setProgress(count);
                                }
                            } else {
                                if (getActivity() != null)
                                    m.cancel();
                            }
                        }
                        break;
                }

            }
        }));
        getActivity().bindService(new Intent(getActivity(), StoryDownload.class), getManager().getConnection(),
                Context.BIND_AUTO_CREATE);
        bound = true;
        setHasOptionsMenu(true);
        getSupportActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        int site = Settings.getSiteFiltering(getActivity());
        int filter = Settings.getFiltering(getActivity());
        int sorting = Settings.getSorting(getActivity());
        int status = Settings.getStatusFiltering(getActivity());
        adapter = new ExpandListAdapter(getActivity(),
                StoryUtils.getGroups(getActivity(), getStories(getActivity()), site, filter, sorting, status));
        showNoStoriesMessage();
        listView = (ExpandableListView) inflater.inflate(R.layout.list_expand, null);
        listView.setAdapter(adapter);
        listView.setSelector(android.R.drawable.list_selector_background);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Entry e = ((ListChild) adapter.getChild(groupPosition, childPosition)).getEntry();
                showDialog(e);
                return true;
            }
        });
        listView.setDividerHeight(0);
        listView.setGroupIndicator(getResources().getDrawable(android.R.color.transparent));
        int count = listView.getExpandableListAdapter().getGroupCount();
        for (int i = 0; i < count; i++)
            listView.expandGroup(i);
        getSupportActivity().getSupportActionBar().setTitle(getString(R.string.library));

        return listView;
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

    }

    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            setCallback((LibraryFragmentListener) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LibraryFragmentListener");
        }
    }


    public void update() {
        int site = Settings.getSiteFiltering(getActivity());
        int filter = Settings.getFiltering(getActivity());
        int sorting = Settings.getSorting(getActivity());
        int status = Settings.getStatusFiltering(getActivity());
        adapter.setGroups(StoryUtils.getGroups(getActivity(), getStories(getActivity()), site, filter, sorting, status));
        adapter.notifyDataSetChanged();
        showNoStoriesMessage();

    }

    private void showNoStoriesMessage() {
        if (adapter.getGroupCount() == 0) {
            AlertBuilder builder = new AlertBuilder(getActivity());
            builder.setTitle(R.string.alert);
            builder.setMessage(getString(R.string.no_stories_found));
            builder.setPositiveButton(getString(R.string.ok), null);
            builder.create().show();
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.lib, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_down:
                download();
                return true;
            case R.id.menu_settings:
                settings();
                return true;
            case R.id.update_all:
                updateAll();
                return true;
            case R.id.collapse_all:
                collapseAll();
                return true;
            case R.id.expand_all:
                expandAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void collapseAll() {
        for (int i = 0; i < adapter.getGroupCount(); i++)
            listView.collapseGroup(i);
    }

    private void expandAll() {
        for (int i = 0; i < adapter.getGroupCount(); i++)
            listView.expandGroup(i);

    }

    public void onDestroy() {
        super.onDestroy();
        if (bound) {
            if (getManager().getService() != null) {
                try {
                    Message msg = Message.obtain(null, MSG_UNREGISTER_CLIENT);
                    msg.replyTo = getManager().getMessenger();
                    getManager().send(msg);
                } catch (RemoteException ignore) {
                }
            }
            getActivity().unbindService(getManager().getConnection());
            bound = false;
        }
    }

    private void settings() {
        final AlertBuilder builder = new AlertBuilder(getActivity());
        final int filter = Settings.getFiltering(getActivity());
        final int sorting = Settings.getSorting(getActivity());
        final int site = Settings.getSiteFiltering(getActivity());
        final int status = Settings.getStatusFiltering(getActivity());
        builder.setTitle(getString(R.string.sorting_filtering));
        if (Settings.isLightTheme(getActivity()))
            builder.setIcon(R.drawable.settings);
        else
            builder.setIcon(R.drawable.setting_dark);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (site != Settings.getSiteFiltering(getActivity())
                        || filter != Settings.getFiltering(getActivity())
                        || sorting != Settings.getSorting(getActivity())
                        || status != Settings.getStatusFiltering(getActivity()))
                    update();
                if (filter != Settings.getFiltering(getActivity())
                        || site != Settings.getSiteFiltering(getActivity())
                        || status != Settings.getStatusFiltering(getActivity())) {
                    int count = listView.getExpandableListAdapter().getGroupCount();
                    for (int i = 0; i < count; i++)
                        listView.expandGroup(i);
                }

            }
        });
        View v;
        {
            v = LayoutInflater.from(getActivity()).inflate(R.layout.sorting_new, null);
            Spinner a = (Spinner) v.findViewById(R.id.filtering);
            Spinner b = (Spinner) v.findViewById(R.id.sorting);
            Spinner c = (Spinner) v.findViewById(R.id.sites);
            Spinner d = (Spinner) v.findViewById(R.id.status);
            initSpinner(a, R.array.filtering);
            initSpinner(b, R.array.sorting);
            initSpinner(c, R.array.sites);
            initSpinner(d, R.array.status);
        }
        builder.setView(v);
        AlertDialog d = builder.create();
        d.setCancelOperation(new Runnable() {
            @Override
            public void run() {
                if (filter != Settings.getFiltering(getActivity())
                        || sorting != Settings.getSorting(getActivity())
                        || site != Settings.getSiteFiltering(getActivity()))
                    update();
            }
        });
        d.show();

    }

    private void initSpinner(Spinner a, final int array) {
        a.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(array)));
        ((ArrayAdapter) a.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (array == R.array.filtering)
            a.setSelection(Settings.getFiltering(getActivity()));
        else if (array == R.array.sorting)
            a.setSelection(Settings.getSorting(getActivity()));
        else if (array == R.array.sites)
            a.setSelection(Settings.getSiteFiltering(getActivity()));
        else if (array == R.array.status)
            a.setSelection(Settings.getStatusFiltering(getActivity()));
        a.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeSettings(array, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void makeSettings(int array, int position) {
        if (array == R.array.filtering)
            Settings.setFiltering(getActivity(), position);
        else if (array == R.array.sorting)
            Settings.setSorting(getActivity(), position);
        else if (array == R.array.sites)
            Settings.setSiteFiltering(getActivity(), position);
        else if (array == R.array.status)
            Settings.setStatusFiltering(getActivity(), position);
    }

    public void updateAll() {
        if (updateList == null || updateList.size() == 0) {
            updateList = adapter.getEntries();
            total = updateList.size();
            if (updateList.size() > 0) {
                m = new ProgressDialog(getActivity());
                m.setMessage(getString(R.string.updating));
                m.setCancelable(true);
                m.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        updateList.clear();
                    }
                });
                m.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                m.setProgress(0);
                m.setMax(total);
                m.show();
            }
            int max = 2;
            while (updateList.size() > 0 && max > 0) {
                int last = updateList.size() - 1;
                makeUpdate(updateList.get(last), false);
                max--;
                updateList.remove(last);
            }
        }
    }


}