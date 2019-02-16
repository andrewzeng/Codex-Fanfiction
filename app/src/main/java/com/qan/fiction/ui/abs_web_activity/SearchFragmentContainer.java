package com.qan.fiction.ui.abs_web_activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.qan.fiction.R;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AppCompatListFragment;
import com.qan.fiction.custom.SavedSearchView;
import com.qan.fiction.ui.abs_web_activity.adapters.PairAdapter;
import com.qan.fiction.ui.activity.Global;
import com.qan.fiction.util.misc.listeners.ViewListener;
import com.qan.fiction.util.storage.DatabaseHandler;
import com.qan.fiction.util.storage.SerPair;
import com.qan.fiction.util.web.Web;

import java.util.ArrayList;

public abstract class SearchFragmentContainer extends AppCompatListFragment {

    protected ViewListener callback;
    private SavedSearchView view;
    private DatabaseHandler db;
    private Task task;
    private ArrayList<SerPair<String, Integer>> list;
    private CursorAdapter adapter;

    private class Task extends AsyncTask<Void, Void, ArrayList<SerPair<String, Integer>>> {

        @Override
        protected ArrayList<SerPair<String, Integer>> doInBackground(Void... params) {
            return db.topSearches();
        }

        @Override
        protected void onPostExecute(ArrayList<SerPair<String, Integer>> list) {
            super.onPostExecute(list);
            setListAdapter(new PairAdapter(getActivity(), list));
            setList(list);
        }
    }


    private class Term extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            DatabaseHandler handler = ((Global) getActivity().getApplication()).getDatabase();
            handler.increment(params[0]);
            return null;
        }
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = ((Global) getActivity().getApplication()).getDatabase();
        getSupportActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getSupportActivity().getSupportActionBar().setTitle(R.string.search);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null)
            adapter.getCursor().close();
        if (task != null)
            task.cancel(true);
    }

    @Override
    public abstract void onListItemClick(ListView l, View v, int position, long id);

    @Override
    public void onStart() {
        super.onStart();
        task = new Task();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        view = new SavedSearchView(getActivity(), menuItem);
        view.setSuggestionsAdapter(adapter = getSearchAdapter());
        view.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Cursor c = (Cursor) view.getSuggestionsAdapter().getItem(position);
                view.setQuery(c.getString(c.getColumnIndex(DatabaseHandler.KEY_STRING)), false);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor c = (Cursor) view.getSuggestionsAdapter().getItem(position);
                view.setQuery(c.getString(c.getColumnIndex(DatabaseHandler.KEY_STRING)), false);
                return true;
            }
        });
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSubmit(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        menuItem.setActionView(view);
    }

    private CursorAdapter getSearchAdapter() {

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line,
                null, new String[]{DatabaseHandler.KEY_STRING}, new int[]{android.R.id.text1}, 0);
        adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                int index = cursor.getColumnIndex(DatabaseHandler.KEY_STRING);
                return cursor.getString(index);
            }
        });
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint != null) {
                    String name = constraint.toString().toLowerCase();
                    return db.filterSearch(name);
                }
                return null;
            }
        });
        return adapter;
    }

    public abstract void onSubmit(String query);

    public abstract String getUrl(CharSequence query, String type, boolean dual);

    public void addTerm(String term) {
        term = term.toLowerCase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
            new Term().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, term);
        else
            new Term().execute(term);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_web_icon:
                Intent i = Web.web_intent(getUrl(view.getQuery(), "story", getArguments().containsKey("dual")));
                startActivity(i);
                return true;
            case R.id.clear_search:
                clearDialog();
                return true;

            default:
                return false;
        }
    }

    private void clearDialog() {
        AlertBuilder builder = new AlertBuilder(getActivity());
        builder.setTitle("Clear All Searches?");
        builder.setMessage("Are you sure you want to continue?");
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.clearSearches();
                setListAdapter(new PairAdapter(getActivity(), new ArrayList<SerPair<String, Integer>>()));
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();


    }


    public void setList(ArrayList<SerPair<String, Integer>> list) {
        this.list = list;
    }

    public ArrayList<SerPair<String, Integer>> getList() {
        return list;
    }
}

