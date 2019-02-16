package com.qan.fiction.ui.fp_activity.recent;

import com.qan.fiction.ui.fp_activity.norm.FP_Paginate;

import java.util.Arrays;
import java.util.List;

public class FP_Recent extends FP_Paginate {
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
