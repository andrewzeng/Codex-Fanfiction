package com.qan.fiction.util.web.reader_data_manager;

import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.download.AO3_Extract;

public class AO3DataManager extends DataManager {


    public AO3DataManager() {
        super();
    }

    @Override
    public String getContent() {
        return getDocument().select("#chapters .userstuff p").outerHtml();
    }

    @Override
    public int getChapterCount() {
        int size = getDocument().select("#selected_id option").size();
        return size == 0 ? 1 : size;
    }

    @Override
    public String getUrl() {
        return AO3_Extract.getPageUrl(getDocument(), getStoryId(), getPage());
    }

    @Override
    public String getTitle() {
        return getDocument().select("h2.title").text();
    }

    @Override
    public int getSite() {
        return Settings.AO3;
    }

    @Override
    public String getWebUrl() {
        if (getDocument() != null)
            return getUrl();
        else
            return getFirstPage();

    }
}
