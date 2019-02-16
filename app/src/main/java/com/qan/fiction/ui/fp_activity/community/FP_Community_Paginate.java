package com.qan.fiction.ui.fp_activity.community;

import com.qan.fiction.ui.fp_activity.norm.FP_Paginate;

import java.util.Arrays;
import java.util.List;

public class FP_Community_Paginate extends FP_Paginate {
    private final static List<String> order = Arrays.asList("censorid", "s", "p", "genreid",
            "len", "statusid", "timeid");

    public String url(String url, int page) {
        if (data.size() == 1)
            return url + "3/0/" + page + "/0/0/0/0/";
        url = new StringWrapperUrl(url, null)
                .appendAll(getArgumentOrder())
                .toString();
        return url;
    }

    protected List<String> getArgumentOrder() {
        return order;
    }

    @Override
    public String url_mobile(String url, int page) {
        return url(url.replace("www.fictionpress.com", "m.fictionpress.com"), page);
    }
}
