package com.qan.fiction.ui.abs_web_activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.qan.fiction.R;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AppCompatListFragment;
import com.qan.fiction.custom.SavedSearchView;
import com.qan.fiction.ui.abs_web_activity.adapters.CategoriesAdapter;
import com.qan.fiction.ui.abs_web_activity.info.CategoryInfo;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.download.Connector;
import com.qan.fiction.util.misc.listeners.ViewListener;
import com.qan.fiction.util.web.Web;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Categories extends AppCompatListFragment implements Browsable {

    private Download d;
    private ViewListener callback;
    protected boolean dualPane;
    private SearchView view;
    protected ArrayList<CategoryInfo> items;

    private class Download extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Document d = Connector.getUrl(params[0]);
                items = getItems(d);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
            return 1;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (result == 0) {
                AlertBuilder builder = new AlertBuilder(getActivity());
                builder.setTitle(R.string.alert);
                if (Connector.isNetworkAvailable(getActivity()))
                    builder.setMessage(R.string.loading_failed);
                else
                    builder.setMessage(getString(R.string.no_internet));
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStack();
                    }
                });
                builder.create().show();
            } else {
                setAdapter();
            }
        }
    }

    public abstract ArrayList<CategoryInfo> getItems(Document doc);

    private void setAdapter() {
        if (getListAdapter() == null) {
            setListAdapter(new CategoriesAdapter(getActivity(), items));
            if (Settings.getSortingStyle(getActivity()) == Settings.POPULARITY)
                sort_popular();
            else
                sort_title();
            if (!view.getQuery().equals(""))
                ((CategoriesAdapter) getListAdapter()).getFilter().filter(view.getQuery());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        CategoryInfo a = ((CategoriesAdapter) getListAdapter()).getItem(position);
        openNext(position, a);
    }

    private void openNext(int position, CategoryInfo a) {
        Bundle b = new Bundle();
        b.putString("url", a.ref);
        b.putString("name", getName(a));
        callback.openFragment(getNextFragment(position), b);
    }

    protected String getName(CategoryInfo a) {
        return a.name;
    }

    public abstract ListFragment getNextFragment(int position);

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setRetainInstance(true);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.categories, menu);
        MenuItem item = menu.findItem(R.id.search);
        view = new SavedSearchView(getActivity(), item);
        if (items != null)
            setAdapter();
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (getListAdapter() != null && getListAdapter().getCount() == 1) {
                    CategoryInfo e = (CategoryInfo) getListAdapter().getItem(0);
                    openNext(0, e);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (getListAdapter() != null) {
                    ((CategoriesAdapter) getListAdapter()).getFilter().filter(newText.replace("/", " "));
                    getListView().setSelection(0);
                }
                return true;
            }
        });
        item.setActionView(view);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        getListView().setFastScrollEnabled(true);
        dualPane = getArguments().getBoolean("dual");
        ActionBar bar = getSupportActivity().getSupportActionBar();
        Bundle b = getArguments();
        String page = b.getString("url");
        String name = b.getString("name");
        if (saved == null || !saved.containsKey("items")) {
            bar.setTitle(name);
            if (d != null)
                d.cancel(true);
            d = new Download();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
                d.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page);
            else
                d.execute(page);
        } else {
            items = (ArrayList<CategoryInfo>) saved.getSerializable("items");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (d != null)
            d.cancel(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        try {
            callback = (ViewListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ViewListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        if (items != null)
            out.putSerializable("items", items);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_web_icon:
                web_action();
                return true;
            case R.id.sort_popular: {
                Settings.setSortingStyle(getActivity(), Settings.POPULARITY);
                if (getListAdapter() == null)
                    return true;
                sort_popular();
                return true;
            }
            case R.id.sort_title: {
                Settings.setSortingStyle(getActivity(), Settings.TITLE);
                if (getListAdapter() == null)
                    return true;
                sort_title();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void web_action() {
        String url;
        if (dualPane)
            url = getUrl();
        else
            url = getMobileUrl();
        Intent i = Web.web_intent(url);
        startActivity(i);
    }

    public abstract String getUrl();

    public abstract String getMobileUrl();

    public void sort_title() {
        ((CategoriesAdapter) getListAdapter()).sort(new Comparator<CategoryInfo>() {
            @Override
            public int compare(CategoryInfo a, CategoryInfo b) {
                return a.name.compareTo(b.name);
            }
        });
        ((CategoriesAdapter) getListAdapter()).getFilter().filter(view.getQuery());
    }

    public void sort_popular() {
        ((CategoriesAdapter) getListAdapter()).sort(new Comparator<CategoryInfo>() {
            @Override
            public int compare(CategoryInfo a, CategoryInfo b) {
                return -a.val.compareTo(b.val);
            }
        });
        ((CategoriesAdapter) getListAdapter()).getFilter().filter(view.getQuery());

    }
}



