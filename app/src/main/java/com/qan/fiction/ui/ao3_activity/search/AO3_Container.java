package com.qan.fiction.ui.ao3_activity.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.SearchFragmentContainer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AO3_Container extends SearchFragmentContainer {
    public String getUrl(CharSequence query, String type, boolean dual) {
        String url = "http://archiveofourown.org/search";
        try {
            if (query.length() > 0)
                url = "http://archiveofourown.org/search?utf8=%E2%9C%93&work_search[query]=" + URLEncoder.encode(query.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        onSubmit(getList().get(position).first);
    }

    public void onSubmit(String query) {
        if (query.length() != 0) {
            Bundle b = new Bundle();
            b.putString("name", getString(R.string.search) + ": " + query);
            b.putString("url", "http://archiveofourown.org/search");
            b.putString("query", query);
            b.putString("type", "story");
            b.putString("utf8", "%E2%9C%93");
            addTerm(query);
            callback.openFragment(new AO3_SearchFragment(), b);
        }
    }
}
