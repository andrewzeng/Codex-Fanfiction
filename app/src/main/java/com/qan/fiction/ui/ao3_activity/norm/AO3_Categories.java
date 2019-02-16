package com.qan.fiction.ui.ao3_activity.norm;

import android.support.v4.app.ListFragment;

import com.qan.fiction.ui.abs_web_activity.Categories;
import com.qan.fiction.ui.abs_web_activity.info.CategoryInfo;
import com.qan.fiction.util.download.AO3_Extract;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class AO3_Categories extends Categories {
    @Override
    public ArrayList<CategoryInfo> getItems(Document doc) {
        return AO3_Extract.getCategories(doc);
    }

    @Override
    public ListFragment getNextFragment(int position) {
        return new AO3_Paginate();
    }

    @Override
    public String getUrl() {
        return getArguments().getString("url");
    }

    @Override
    public String getMobileUrl() {
        return getUrl();
    }
}
