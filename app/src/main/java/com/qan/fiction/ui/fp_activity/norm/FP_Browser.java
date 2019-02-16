package com.qan.fiction.ui.fp_activity.norm;

import android.support.v4.app.ListFragment;

import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.Browser;

public class FP_Browser extends Browser {

    @Override
    public int appendResource() {
        return R.array.fp_com_append_norm;
    }

    @Override
    public int categoryResource() {
        return R.array.fp_com_categories;
    }


    @Override
    public ListFragment getNextFragment(int position) {
        return new FP_Categories();
    }

    @Override
    public String getAddress(int position) {
        return "https://www.fictionpress.com/" + append[position];
    }


    public String getTitle() {
        return getString(R.string.fp_com);
    }

    public String getMobileUrl() {
        return "https://m.fictionpress.com/";
    }

    public String getUrl() {
        return "https://www.fictionpress.com/";
    }
}
