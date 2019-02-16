package com.qan.fiction.ui.ff_activity.community;

import com.qan.fiction.ui.ff_activity.norm.FF_Paginate;

import java.util.Arrays;
import java.util.List;

public class FF_Community_Paginate extends FF_Paginate {
    private final static List<String> order = Arrays.asList("censorid", "s", "p", "genreid",
            "len", "statusid", "timeid");

    protected List<String> getArgumentOrder() {
        return order;
    }

    public String url(String url, int page) {
        if (data.size() == 1)
            return url + "3/0/" + page + "/0/0/0/0/";
        url = new StringWrapperUrl(url, null)
                .appendAll(getArgumentOrder())
                .toString();
        return url;
    }

    @Override
    public String url_mobile(String url, int page) {
        return url(url.replace("www.fanfiction.net", "m.fanfiction.net"), page);
    }
}
