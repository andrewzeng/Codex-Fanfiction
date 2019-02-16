package com.qan.fiction.ui.abs_web_activity;

public interface Browsable {

    /**
     * The action to perform when a browser is opened.
     */
    public void web_action();

    /**
     * @return An absolute URL
     */
    public String getUrl();

    /**
     * @return An absolute URL for mobile sites. May be the same as {@link #getUrl()}.
     */
    public String getMobileUrl();

}
