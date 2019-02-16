package com.qan.fiction.ui.ao3_activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.AbsWebActivity;
import com.qan.fiction.ui.ao3_activity.norm.AO3_Browser;
import com.qan.fiction.ui.ao3_activity.recent.AO3_Recent;
import com.qan.fiction.util.storage.entries.Entry;

public class AO3_Activity extends AbsWebActivity {
    @Override
    public void setContent() {
        ActionBar bar = getSupportActionBar();
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                String s = tab.getText().toString();
                if (s.equals("Fandoms")) {
                    openTab(new AO3_Browser(), ft);
                } else {
                    Bundle b = new Bundle();
                    b.putString("name", getString(R.string.ao3));
                    b.putString("url", "https://www.archiveofourown.org/works");
                    openTab(new AO3_Recent(), ft, b);
                }
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        addTab("Fandoms", tabListener);
        addTab("Recent Works", tabListener);
    }

    @Override
    public void onReview(Entry e) {

    }
}
