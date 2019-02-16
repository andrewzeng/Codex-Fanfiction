package com.qan.fiction.ui.abs_web_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qan.fiction.R;
import com.qan.fiction.custom.AppCompatListFragment;
import com.qan.fiction.util.misc.listeners.ViewListener;
import com.qan.fiction.util.web.Web;

public abstract class Browser extends AppCompatListFragment implements Browsable {
    protected ViewListener callback;
    protected String[] append;
    protected String[] names;

    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        append = getResources().getStringArray(appendResource());
        names = getResources().getStringArray(categoryResource());
        setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                names));
        setHasOptionsMenu(true);

    }

    public abstract int appendResource();

    public abstract int categoryResource();

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

    public void onListItemClick(ListView l, View v, int position, long id) {
        Bundle b = new Bundle();
        b.putString("url", getAddress(position));
        b.putString("name", names[position]);
        b.putString("append", append[position]);
        callback.openFragment(getNextFragment(position), b);
    }

    /**
     * Gets the next fragment to be opened
     *
     * @param position The position of the click in the list
     * @return The corresponding {@link ListFragment} to open
     */
    public abstract ListFragment getNextFragment(int position);

    /**
     * Returns an absolute URL for the location of the link click
     *
     * @param position The position of the clicked item
     * @return The absolute URL
     */
    public abstract String getAddress(int position);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        android.support.v7.app.ActionBar bar = getSupportActivity().getSupportActionBar();
        if (bar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
            if (bar.getTabCount() == 0)
                callback.setContent();
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
        if (getArguments().getString("name") == null)
            bar.setTitle(getTitle());
        else
            bar.setTitle(getArguments().getString("name"));
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public abstract String getTitle();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.web_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_web_icon:
                web_action();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void web_action() {
        String url;
        if (getArguments().getBoolean("dual"))
            url = getUrl();
        else
            url = getMobileUrl();
        Intent i = Web.web_intent(url);
        startActivity(i);
    }

    public abstract String getMobileUrl();

    public abstract String getUrl();
}
