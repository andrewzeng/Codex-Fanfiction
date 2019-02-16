package com.qan.fiction.ui.ff_activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.AbsWebActivity;
import com.qan.fiction.ui.ff_activity.community.FF_Community;
import com.qan.fiction.ui.ff_activity.crossover.FF_Crossover;
import com.qan.fiction.ui.ff_activity.norm.FF_Browser;
import com.qan.fiction.ui.ff_activity.recent.FF_Recent;
import com.qan.fiction.util.storage.entries.Entry;


public class FF_Activity extends AbsWebActivity  {


    public void setContent() {
        ActionBar bar = getSupportActionBar();
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                String s = tab.getText().toString();
                if (s.equals("Categories")) {
                    openTab(new FF_Browser(), ft);
                } else if (s.equals("X-Overs")) {
                    openTab(new FF_Crossover(), ft);
                } else if (s.equals("Communities")) {
                    openTab(new FF_Community(), ft);
                } else {
                    Bundle b = new Bundle();
                    b.putString("url", "https://www.fanfiction.net/j/");
                    b.putString("name", getString(R.string.ff_net));
                    openTab(new FF_Recent(), ft, b);
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
        addTab("Categories", tabListener);
        addTab("X-Overs", tabListener);
        addTab("Communities", tabListener);
        addTab("Just In", tabListener);

    }

    @Override
    public void onReview(Entry e) {
    }
}