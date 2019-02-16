package com.qan.fiction.ui.ff_activity.recent;

import com.qan.fiction.ui.ff_activity.norm.FF_Paginate;

import java.util.Arrays;
import java.util.List;

public class FF_Recent extends FF_Paginate {


    private final List<String> order = Arrays.asList("categoryid", "sortid", "languageid");

    @Override
    protected List<String> getArgumentOrder() {
        return order;
    }

    public String url(String url, int page) {
        if (data.size() == 1)
            return url;
        url = new StringWrapperUrl(url, null)
                .appendAll(getArgumentOrder())
                .toString();
        return url;
    }
}
