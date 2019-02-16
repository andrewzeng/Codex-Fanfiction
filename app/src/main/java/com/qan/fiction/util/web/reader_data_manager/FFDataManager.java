package com.qan.fiction.util.web.reader_data_manager;

import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.web.WebUtils;
import org.jsoup.select.Elements;

import static com.qan.fiction.util.constants.Constants.*;

public class FFDataManager extends DataManager {

    public FFDataManager() {
        super();
    }

    @Override
    public String getContent() {
        return getDocument().getElementById(elementID.get(FF_NET_S)).html();
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public int getChapterCount() {
        Elements e = getDocument().select("body > div[align=center] > a");
        if (e.size() == 1)
            return 1;
        for (int i = 0; i < e.size(); i++)
            if (e.get(i).text().matches("\\d+"))
                return Integer.parseInt(e.get(i).text());
        return 1;
    }

    @Override
    public String getUrl() {
        return WebUtils.format(download.get(FF_NET_S), getStoryId(), getPage());
    }

    @Override
    public String getWebUrl() {
        return WebUtils.format(normalView.get(FF_NET_S), getStoryId(), getPage());
    }

    @Override
    public String getTitle() {
        return getDocument().select("#content b").get(0).text();
    }

    @Override
    public int getSite() {
        return Settings.FF_NET;
    }


}
