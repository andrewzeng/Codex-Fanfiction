package com.qan.fiction.ui.ff_activity.crossover;

import android.support.v4.app.ListFragment;

import com.qan.fiction.ui.abs_web_activity.Categories;
import com.qan.fiction.ui.abs_web_activity.info.CategoryInfo;
import com.qan.fiction.util.download.FF_Extract;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class FF_Crossover_Categories2 extends Categories {

    @Override
    public ArrayList<CategoryInfo> getItems(Document doc) {
        return FF_Extract.getCategories(doc);
    }


    @Override
    public ListFragment getNextFragment(int position) {
        return new FF_Crossover_List();
    }


    @Override
    public String getUrl() {
        return getArguments().getString("url");
    }

    @Override
    protected String getName(CategoryInfo a) {
        return getArguments().getString("name") + " + " + a.name;
    }

    @Override
    public String getMobileUrl() {
        return getUrl().replace("www.fanfiction.net", "m.fanfiction.net");
    }


}
