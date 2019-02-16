package com.qan.fiction.ui.ff_activity.crossover;

import android.support.v4.app.ListFragment;

import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.Browser;

public class FF_Crossover extends Browser {

    @Override
    public int appendResource() {
        return R.array.ff_net_append_norm;
    }

    @Override
    public int categoryResource() {
        return R.array.ff_net_categories;
    }


    @Override
    public ListFragment getNextFragment(int position) {
        return new FF_Crossover_Categories();
    }

    @Override
    public String getAddress(int position) {
        return "https://www.fanfiction.net/crossovers/" + append[position];
    }


    public String getTitle() {
        return getString(R.string.ff_net);
    }

    public String getMobileUrl() {
        return "https://m.fanfiction.net/";
    }

    public String getUrl() {
        return "https://www.fanfiction.net/";
    }
}
