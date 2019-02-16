package com.qan.fiction.ui.fp_activity.norm;

import android.support.v4.app.ListFragment;

import com.qan.fiction.ui.abs_web_activity.Categories;
import com.qan.fiction.ui.abs_web_activity.info.CategoryInfo;
import com.qan.fiction.util.download.FF_Extract;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class FP_Categories extends Categories {
    @Override
    public ArrayList<CategoryInfo> getItems(Document doc) {
        return FF_Extract.getCategories(doc);
    }


    @Override
    public ListFragment getNextFragment(int position) {
        return new FP_Paginate();
    }


    @Override
    public String getUrl() {
        return "https://www.fictionpress.com/" + getArguments().getString("append");
    }

    @Override
    public String getMobileUrl() {
        return "https://m.fictionpress.com/" + getArguments().getString("append");
    }


}
