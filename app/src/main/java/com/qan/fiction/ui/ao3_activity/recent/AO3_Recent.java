package com.qan.fiction.ui.ao3_activity.recent;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qan.fiction.R;
import com.qan.fiction.ui.ao3_activity.norm.AO3_Paginate;

public class AO3_Recent extends AO3_Paginate {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.removeItem(R.id.menu_settings);
    }

    @Override
    public String url(String url, int page) {
        return "http://www.archiveofourown.org/works";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        ActionBar bar = getSupportActivity().getSupportActionBar();
        if (bar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
            if (bar.getTabCount() == 0)
                getViewListener().setContent();
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
        return v;
    }
}
