package com.qan.fiction.ui.ff_activity.community;

import android.support.v4.app.ListFragment;

import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.Browser;

public class FF_Community extends Browser {

    @Override
    public int appendResource() {
        return R.array.ff_net_append_community;
    }

    @Override
    public int categoryResource() {
        return R.array.ff_net_categories_community;
    }


    @Override
    public ListFragment getNextFragment(int position) {
        if (position == 0)
            return new FF_Community_List();
        else
            return new FF_Community_Categories();
    }

    @Override
    public String getAddress(int position) {
        return "https://www.fanfiction.net/communities/" + append[position];
    }


    public String getTitle() {
        return getString(R.string.ff_net);
    }

    public String getMobileUrl() {
        return "https://m.fanfiction.net/communities/";
    }

    public String getUrl() {
        return "https://www.fanfiction.net/communities/";
    }
}
