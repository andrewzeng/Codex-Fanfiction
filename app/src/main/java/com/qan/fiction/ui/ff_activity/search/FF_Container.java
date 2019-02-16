package com.qan.fiction.ui.ff_activity.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.SearchFragmentContainer;

public class FF_Container extends SearchFragmentContainer {
    public String getUrl(CharSequence query, String type, boolean dual) {
        String url;
        if (query.length() == 0)
            url = "https://www.fanfiction.net/search.php?ready=0";
        else
            url = "https://www.fanfiction.net/search.php?ready=1&type=" + type + "&keywords=" + query.toString().replace(" ", "+");
        if (!dual)
            url = url.replace("fanfiction.net", "m.fanfiction.net");
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
            b.putString("url", "https://www.fanfiction.net/search.php");
            b.putString("query", query);
            b.putString("type", "story");
            addTerm(query);
            callback.openFragment(new FF_SearchFragment(), b);
        }
    }
}
