package com.qan.fiction.ui.ao3_activity.search;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.qan.fiction.R;
import com.qan.fiction.ui.ao3_activity.norm.AO3_Paginate;
import com.qan.fiction.util.download.AO3_Extract;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class AO3_SearchFragment extends AO3_Paginate {

    private String search;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        try {
            search = URLEncoder.encode(getArguments().getString("query"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.removeItem(R.id.menu_settings);
    }

    @Override
    public String url(String url, int page) {
        extra_data(data);
        url = new StringWrapperGet(url, null)
                .appendAll()
                .toString();
        return url;
    }

    @Override
    public int getTotal(Document doc) {
        return AO3_Extract.getSearchTotal(doc);
    }

    @Override
    public void extra_data(HashMap<String, String> data) {
        data.put("work_search[query]", search);
        data.put("utf8", "%E2%9C%93");
    }
}
