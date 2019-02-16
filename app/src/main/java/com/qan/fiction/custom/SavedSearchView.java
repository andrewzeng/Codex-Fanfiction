package com.qan.fiction.custom;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

public class SavedSearchView extends SearchView {
    private CharSequence restore;
    private String text;

    public SavedSearchView(Context context, MenuItem item) {
        super(context);
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                restore = getQuery();
                return true;
            }
        });
        setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQuery(restore, false);
            }
        });
    }

    @Override
    public void setOnQueryTextListener(final OnQueryTextListener listener) {
        super.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return listener.onQueryTextSubmit(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                restore = text;
                text = newText;
                return listener.onQueryTextChange(newText);
            }
        });
    }
}
