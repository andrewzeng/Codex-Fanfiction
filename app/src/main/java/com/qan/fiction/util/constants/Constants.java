package com.qan.fiction.util.constants;

import java.util.HashMap;

public class Constants {
    public static final HashMap<String, String> elementID;
    public static final HashMap<String, String> siteInfo, mobileReader;
    public static final HashMap<String, String> siteNAme;
    public static final HashMap<String, String> download;
    public static final HashMap<String, String> normalSite, mobileSite, normalView, mobileView;


    public static final int SWIPE_THRESHOLD_VELOCITY = 200;
    public static final int MIN_DIST = 140;

    public static final String EX_SIGNAL = "Exception Caught@@@";
    public static final String SELECTED_STRING = "selected_string";


    public static final String AO3_S = "ao3_org";
    public static final String FP_COM_S = "fp_com";
    public static final String FF_NET_S = "ff_net";

    static {
        siteInfo = new HashMap<String, String>();
        mobileReader = new HashMap<String, String>();
        elementID = new HashMap<String, String>();
        siteNAme = new HashMap<String, String>();
        download = new HashMap<String, String>();
        normalSite = new HashMap<String, String>();
        mobileSite = new HashMap<String, String>();
        normalView = new HashMap<String, String>();
        mobileView = new HashMap<String, String>();
        //Where to extract the info
        siteInfo.put(FF_NET_S, "https://www.fanfiction.net/s/?/1/");
        siteInfo.put(FP_COM_S, "https://www.fictionpress.com/s/?/1/");
        siteInfo.put(AO3_S, "http://archiveofourown.org/works/??view_adult=true");
        //id for the story text. It's most likely tagged directly, or close enough
        elementID.put(FF_NET_S, "storycontent");
        elementID.put(FP_COM_S, "storycontent");
        elementID.put(AO3_S, "chapters");
        //Site names
        siteNAme.put(FF_NET_S, "FanFiction.net");
        siteNAme.put(FP_COM_S, "FictionPress.com");
        siteNAme.put(AO3_S, "Archive of Our Own");
        //Download location. Maybe be faster.
        download.put(FF_NET_S, "https://m.fanfiction.net/s/?/?");
        download.put(FP_COM_S, "https://m.fictionpress.com/s/?/?");
        download.put(AO3_S, "http://archiveofourown.org/works/?/chapters/??view_adult=true");
        //main page
        normalSite.put(FF_NET_S, "https://www.fanfiction.net");
        normalSite.put(FP_COM_S, "https://www.fictionpress.com/");
        normalSite.put(AO3_S, "https://www.archiveofourown.org");
        //mobile site main page
        mobileSite.put(FF_NET_S, "https://m.fanfiction.net/");
        mobileSite.put(FP_COM_S, "https://m.fictionpress.com/");
        mobileSite.put(AO3_S, "https://www.archiveofourown.org");
        //Where to view the stories
        normalView.put(FF_NET_S, "https://www.fanfiction.net/s/?/?");
        normalView.put(FP_COM_S, "https://www.fictionpress.com/s/?/?");
        normalView.put(AO3_S, "http://archiveofourown.org/works/??view_adult=true");
        //mobile site info
        mobileView.put(FF_NET_S, "https://m.fanfiction.net/s/?/?");
        mobileView.put(FP_COM_S, "https://m.fictionpress.com/s/?/?");
        mobileView.put(AO3_S, "http://archiveofourown.org/works/??view_adult=true");

    }
}
