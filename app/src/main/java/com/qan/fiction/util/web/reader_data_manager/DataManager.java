package com.qan.fiction.util.web.reader_data_manager;

import org.jsoup.nodes.Document;

public abstract class DataManager {


    private Document document;

    private String firstPage;

    private int storyId, page;

    public DataManager(Document document, String firstPage, int storyId, int page) {
        this.document = document;
        this.firstPage = firstPage;
        this.storyId = storyId;
        this.page = page;
    }

    public DataManager() {

    }

    /**
     * @return The text body, in HTML.
     */
    public abstract String getContent();

    /**
     * @return The number of chapters.
     */
    public abstract int getChapterCount();

    /**
     * @return The url to download the data for the current story.
     */
    public abstract String getUrl();

    /**
     * @return The title of the managed story.
     */
    public abstract String getTitle();

    /**
     * The unique ID for the site of the manged story.
     */
    public abstract int getSite();

    /**
     * Gets the URL for web viewing. Different from the download target returned in {@link DataManager#getUrl()} ()}
     * since it doesn't need to return the page being viewed or the page being used to
     * download (for bandwidth constraints).
     */
    public abstract String getWebUrl();

    /**
     * @return The page intended to be displayed
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the current page for the viewed story.
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * @return The page to be viewed.
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the page to be viewed.
     */
    public void setPage(int page) {
        this.page = page;
    }

    /*
    *
    * */
    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    /**
     * @return The story id of the story.
     */
    public int getStoryId() {
        return storyId;
    }


    public String getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }
}
