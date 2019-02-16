package com.qan.fiction.ui.fp_activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.AbsWebActivity;
import com.qan.fiction.ui.fp_activity.community.FP_Community;
import com.qan.fiction.ui.fp_activity.norm.FP_Browser;
import com.qan.fiction.ui.fp_activity.recent.FP_Recent;
import com.qan.fiction.util.storage.entries.Entry;


public class FP_Activity extends AbsWebActivity {
    @Override
    public void setContent() {

        ActionBar bar = getSupportActionBar();
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                String s = tab.getText().toString();
                if (s.equals("Categories")) {
                    openTab(new FP_Browser(), ft);
                } else if (s.equals("Communities")) {
                    openTab(new FP_Community(), ft);
                } else {
                    Bundle b = new Bundle();
                    b.putString("url", "https://www.fictionpress.com/j/");
                    b.putString("name", getString(R.string.fp_com));
                    openTab(new FP_Recent(), ft, b);
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
        addTab("Communities", tabListener);
        addTab("Just In", tabListener);
    }

    @Override
    public void onReview(Entry e) {

    }
}