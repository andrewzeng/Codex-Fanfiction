package com.qan.fiction.ui.ff_activity.community;

import android.support.v4.app.ListFragment;

import com.qan.fiction.ui.abs_web_activity.Categories;
import com.qan.fiction.ui.abs_web_activity.info.CategoryInfo;
import com.qan.fiction.util.download.FF_Extract;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class FF_Community_Categories extends Categories {
    @Override
    public ArrayList<CategoryInfo> getItems(Document doc) {
        return FF_Extract.getCommunities(doc);
    }

    @Override
    public ListFragment getNextFragment(int position) {
        return new FF_Community_List();
    }

    @Override
    public String getUrl() {
        return "https://fanfiction.net/communities/" + getArguments().getString("append");
    }

    @Override
    public String getMobileUrl() {
        return "https://m.fanfiction.net/communities/" + getArguments().getString("append");
    }
}
