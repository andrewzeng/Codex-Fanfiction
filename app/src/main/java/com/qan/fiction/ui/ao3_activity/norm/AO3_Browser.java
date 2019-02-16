package com.qan.fiction.ui.ao3_activity.norm;

import android.support.v4.app.ListFragment;

import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.Browser;

public class AO3_Browser extends Browser {
    @Override
    public int appendResource() {
        return R.array.ao3_org_append;
    }

    @Override
    public int categoryResource() {
        return R.array.ao3_org_categories;
    }

    @Override
    public ListFragment getNextFragment(int position) {
        return new AO3_Categories();
    }

    @Override
    public String getAddress(int position) {
        return "http://www.archiveofourown.org/media/" + append[position];
    }

    @Override
    public String getTitle() {
        return getString(R.string.ao3);
    }

    @Override
    public String getMobileUrl() {
        return getUrl();
    }

    @Override
    public String getUrl() {
        return "http://www.archiveofourown.org/";
    }
}
