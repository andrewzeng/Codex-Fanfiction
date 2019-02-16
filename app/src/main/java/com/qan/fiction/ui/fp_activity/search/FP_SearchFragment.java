package com.qan.fiction.ui.fp_activity.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.info.SearchInfo;
import com.qan.fiction.ui.fp_activity.norm.FP_Paginate;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.download.FF_Extract;
import com.qan.fiction.util.storage.SerPair;
import com.qan.fiction.util.storage.entries.Entry;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class FP_SearchFragment extends FP_Paginate {
    private String search;
    private String type;
    private ArrayList<SearchInfo> info;

    protected class StringWrapperCustom extends StringWrapperGet {

        public StringWrapperCustom(String s, HashMap<String, String> filter) {
            super(s, filter);
        }

        @Override
        public StringWrapperCustom appendAll() {
            for (String s : data.keySet())
                if (!s.equals("pcategoryid") || data.get("categoryid").equals("0"))
                    append(s);
            return this;
        }
    }

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        try {
            search = URLEncoder.encode(getArguments().getString("query"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        type = getArguments().getString("type");
        getSupportActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restore(Bundle saved) {
        info = (ArrayList<SearchInfo>) saved.getSerializable("info");
        super.restore(saved);
    }

    @Override
    public String url(String url, int page) {
        extra_data(data);
        url = new StringWrapperCustom(url, getArgumentFilter())
                .appendAll()
                .toString();
        return url;
    }

    @Override
    public String url_mobile(String url, int page) {
        return url(url, page).replace("fictionpress.com", "m.fictionpress.com");
    }


    @Override
    public int getTotal(Document doc) {
        return FF_Extract.getTotalSearchEntries(doc);
    }

    @Override
    protected String getString(ArrayList<CharSequence> key, String item) {
        if (item.equals("match"))
            return "Match:";
        else if (item.equals("sort"))
            return "Sort:";
        else
            return super.getString(key, item);
    }

    @Override
    public ArrayList<Entry> getListItems(Document doc) {
        return FF_Extract.getEntries(doc, getSite());
    }

    @Override
    public void finishDownload(Document doc) {
        super.finishDownload(doc);
        ArrayList<SearchInfo> info = FF_Extract.getSearchedInfo(doc);
        if (getFields(doc).size() > 0)
            this.info = info;
    }

    @Override
    public HashMap<String, ArrayList<SerPair<String, String>>> getFields(Document doc) {
        return super.getFields(doc);
    }

    @Override
    public ArrayList<String> order(Document doc) {
        return super.order(doc);
    }


    private static final HashMap<String, String> filter = new HashMap<String, String>() {{
        put("pcategoryid", "categoryid");
        put("categoryid", "categoryid");
        put("languageid", "languageid");
        put("genreid", "genreid1");
        put("genreid2", "genreid2");
        put("statusid", "statusid");
        put("censorid", "censorid");
        put("words", "words");
        put("statusid", "statusid");
        put("characterid", "characterid1");
        put("characterid2", "characterid2");
        put("characterid3", "characterid3");
        put("characterid4", "characterid4");
        put("p", "ppage");
        put("ready", "ready");
        put("keywords", "keywords");
        put("type", "type");
        put("sort", "sort");
        put("match", "match");
    }};

    @Override
    protected HashMap<String, String> getArgumentFilter() {
        return filter;
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        if (getListAdapter() != null)
            out.putSerializable("info", info);
    }

    @Override
    protected void makeExtras(ArrayList<ViewBundle> viewBundles, LinearLayout layout) {
        super.makeExtras(viewBundles, layout);
        for (SearchInfo searchInfo : info) {
            String url = searchInfo.ref;
            url = "&" + url.substring(url.indexOf("?") + 1);
            String value = "0";
            String name = "dummy";
            boolean found = false;
            for (String s : filter.keySet()) {
                String v = data.get(s);
                //Page is always reset to 0 (but is initially 1)
                if (v != null && !v.equals("0") &&
                        !s.equals("p") && !url.contains("&" + filter.get(s))) {
                    if (!found || !s.equals("statusid")) {
                        value = v;
                        name = s;
                        found = true;
                    }
                }

            }
            final ViewBundle bundle = new ViewBundle();
            bundle.name = name;
            bundle.info = value;
            //Make View
            if (Settings.isLightTheme(getActivity()))
                bundle.view = View.inflate(getActivity(), R.layout.text_search, null);
            else
                bundle.view = View.inflate(getActivity(), R.layout.text_search_dark, null);
            TextView t = (TextView) bundle.view.findViewById(R.id.section);
            t.setText(searchInfo.name);
            TextView u = (TextView) bundle.view.findViewById(R.id.title);
            u.setText(searchInfo.title);
            ImageView v = (ImageView) bundle.view.findViewById(R.id.image);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bundle.view.setVisibility(View.GONE);
                    bundle.info = "0";
                }
            });
            layout.addView(bundle.view);
            viewBundles.add(bundle);
        }
    }

    public void extra_data(HashMap<String, String> data) {
        data.put("ready", "1");
        data.put("keywords", search);
        data.put("type", type);
    }
}
