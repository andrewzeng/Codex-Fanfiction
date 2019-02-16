package com.qan.fiction.util.download;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

public class Connector {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";

    public static final int DEFAULT_REPEAT = 3;

    /**
     * Attempts to connect to the given URL with a GET request.
     *
     * @param url The url to be access
     * @return A {@link Document} if the page loads successfully, or null otherwise
     */
    public static Document getUrl(String url) throws Exception {
        return getUrl(url, DEFAULT_REPEAT);
    }

    /**
     * Attempts to connect to the given URL multiple times with a GET request.
     *
     * @param url    The url to be access
     * @param repeat The number of times to check the url, in case of web problems
     * @return A {@link Document} if the page loads successfully, or null otherwise
     */
    public static Document getUrl(String url, int repeat) throws Exception {

        return getUrl(url, repeat, Connection.Method.GET);

    }


    /**
     * Attempts to connect to the given URL multiple times wth POST method.
     *
     * @param data   The POST and GET data to be sent.
     * @param url    The url to be access
     * @param repeat The number of times to check the url, in case of web problems
     * @return A {@link Document} if the page loads successfully, or null otherwise
     */
    public static Document postUrl(String url, Map<String, String> data, int repeat) throws Exception {
        while (repeat > 0) {
            try {
                repeat--;
                return Jsoup.connect(url)
                        .method(Connection.Method.POST)
                        .data(data)
                        .userAgent("Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.15")
                        .execute()
                        .parse();

            } catch (Exception ex) {
                if (repeat != 0)
                    ex.printStackTrace();
                else
                    throw ex;
            }
        }
        //Should never reach this point.
        return null;
    }


    /**
     * Attempts to connect to the given URL multiple times, with the given method
     *
     * @param url    The url to be access
     * @param repeat The number of times to check the url, in case of web problems
     * @param method The GET or POST HTTP method to use.
     * @return A {@link Document} if the page loads successfully, or throws {@link Exception} otherwise
     */
    public static Document getUrl(String url, int repeat, Connection.Method method) throws Exception {

        while (repeat > 0) {
            try {
                repeat--;
                Document d = Jsoup.connect(url)
                        .method(method)
                        .userAgent(USER_AGENT)
                        .execute()
                        .parse();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    d.select("em").tagName("i");
                }
                return d;
            } catch (Exception ex) {
                if (repeat != 0)
                    ex.printStackTrace();
                else
                    throw ex;
            }
        }
        //Should never reach this point.
        return null;
    }

    public static byte[] getImage(String url, int repeat) throws Exception {
        while (repeat > 0) {
            try {
                repeat--;
                return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; rv:21.0) Gecko/20100101 Firefox/21.0")
                        .ignoreContentType(true)
                        .execute()
                        .bodyAsBytes();

            } catch (Exception ex) {
                if (repeat != 0)
                    ex.printStackTrace();
                else
                    throw ex;
            }
        }
        //Should never reach this point.
        return null;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
