package com.qan.fiction.ui.ff_activity.crossover;

import com.qan.fiction.ui.ff_activity.norm.FF_Paginate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FF_Crossover_List extends FF_Paginate {

    private final List<String> order = Arrays.asList("genreid1", "languageid", "sortid",
            "length", "characterid1", "characterid2", "statusid", "verseid1", "verseid2", "p", "timerange");

    private static final HashMap<String, String> filter = new HashMap<String, String>() {{
        put("sortid", "srt");
        put("genreid1", "g1");
        put("genreid2", "g2");
        put("_genreid1", "_g1");
        put("languageid", "lan");
        put("censorid", "r");
        put("length", "len");
        put("timerange", "t");
        put("statusid", "s");
        put("characterid1", "c1");
        put("characterid2", "c2");
        put("characterid3", "c3");
        put("characterid4", "c4");
        put("_characterid1", "_c1");
        put("_characterid2", "_c2");
        put("verseid1", "v1");
        put("_verseid1", "_v1");
        put("verseid2", "v2");
        put("_verseid2", "_v2");
        put("p", "p");
    }};

    @Override
    protected List<String> getArgumentOrder() {
        return order;
    }

    @Override
    protected HashMap<String, String> getArgumentFilter() {
        return filter;
    }
}
