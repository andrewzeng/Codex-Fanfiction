package com.qan.fiction.ui.fp_activity.community;

import android.support.v4.app.ListFragment;

import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.Browser;

public class FP_Community extends Browser {

    @Override
    public int appendResource() {
        return R.array.fp_com_append_community;
    }

    @Override
    public int categoryResource() {
        return R.array.fp_com_community;
    }


    @Override
    public ListFragment getNextFragment(int position) {
        if (position == 0)
            return new FP_Community_List();
        else
            return new FP_Community_Categories();
    }

    @Override
    public String getAddress(int position) {
        return "https://www.fictionpress.com/communities/" + append[position];
    }


    public String getTitle() {
        return getString(R.string.fp_com);
    }

    public String getMobileUrl() {
        return "https://m.fictionpress.com/communities/";
    }

    public String getUrl() {
        return "https://www.fictionpress.com/communities/";
    }
}
