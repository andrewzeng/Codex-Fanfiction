package com.qan.fiction.ui.ff_activity.community;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.qan.fiction.ui.abs_web_activity.Paginate;
import com.qan.fiction.ui.abs_web_activity.adapters.CommunityAdapter;
import com.qan.fiction.ui.abs_web_activity.adapters.StreamAdapter;
import com.qan.fiction.ui.abs_web_activity.info.CommunityInfo;
import com.qan.fiction.ui.abs_web_activity.info.CommunityPlaceholder;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.download.FF_Extract;
import com.qan.fiction.util.storage.SerPair;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FF_Community_List extends Paginate<CommunityInfo> {


    private final static List<String> order = Arrays.asList("l", "s", "p");

    @Override
    public CommunityInfo getPlaceHolderEntry() {
        return new CommunityPlaceholder();
    }

    @Override
    public StreamAdapter<CommunityInfo> createAdapter(int size) {
        return new CommunityAdapter(getActivity(), list, total, this, size);
    }

    @Override
    public ArrayList<CommunityInfo> getListItems(Document doc) {
        return FF_Extract.getCommunityInfo(doc);
    }

    @Override
    public int getTotal(Document doc) {
        return FF_Extract.getTotalEntries(doc);
    }

    @Override
    public ArrayList<String> order(Document doc) {
        return FF_Extract.getOrder(doc);
    }

    @Override
    public HashMap<String, ArrayList<SerPair<String, String>>> getFields(Document doc) {
        return FF_Extract.getFields(doc);
    }

    public String url(String url, int page) {
        if (data.size() == 1)
            return url + "0/3/" + page;
        url = new StringWrapperUrl(url, null)
                .appendAll(order)
                .toString();
        return url;
    }

    public String url_mobile(String url, int page) {
        return url(url.replace("www.fanfiction.net", "m.fanfiction.net"), page);
    }

    @Override
    protected StreamAdapter<CommunityInfo> getAdapter() {
        return (CommunityAdapter) getListAdapter();
    }


    @Override
    protected ArrayList<String> getExclude(Document doc) {
        return new ArrayList<String>();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Bundle b = new Bundle();
        CommunityInfo info = getAdapter().getItem(position);
        b.putString("url", info.url);
        b.putString("name", info.name);
        getViewListener().openFragment(new FF_Community_Paginate(), b);
    }

    public String getSite() {
        return Constants.FF_NET_S;
    }
}
