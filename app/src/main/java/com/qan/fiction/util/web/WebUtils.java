package com.qan.fiction.util.web;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.qan.fiction.R;
import com.qan.fiction.util.download.StoryDownload;

import static com.qan.fiction.util.constants.Constants.*;

public class WebUtils {

    public static String format(String url, String... params) {
        for (String param : params) {
            url = url.replaceFirst("\\?", param);
        }

        return url;
    }

    public static String format(String url, int... params) {
        for (int param : params) {
            url = url.replaceFirst("\\?", String.valueOf(param));
        }

        return url;
    }

    public static Intent web_parse(Context context, String link) {
        int id;
        String site;
        String url;
        if (link.matches(".*?fanfiction\\.net/s/(\\d*)/.*")) {
            id = Integer.parseInt(link.replaceAll(".*?fanfiction\\.net/s/(\\d*)/.*", "$1"));
            url = format(normalView.get(FF_NET_S), id, 1);
            site = FF_NET_S;
        } else if (link.matches(".*?fictionpress\\.com/s/(\\d*)/.*")) {
            id = Integer.parseInt(link.replaceAll(".*?fictionpress\\.com/s/(\\d*)/.*", "$1"));
            url = format(normalView.get(FP_COM_S), id, 1);
            site = FP_COM_S;
        } else if (link.matches(".*?archiveofourown.org/works/(\\d+).*")) {
            id = Integer.parseInt(link.replaceAll(".*?archiveofourown.org/works/(\\d+).*", "$1"));
            url = format(normalView.get(AO3_S), id);
            site = AO3_S;
        } else {
            Toast.makeText(context, context.getString(R.string.cannot_save), Toast.LENGTH_SHORT).show();
            return null;
        }
        Intent i = new Intent(context, StoryDownload.class);
        i.putExtra("url", url);
        i.putExtra("id", id);
        i.putExtra("download", true);
        i.putExtra("site", site);
        return i;
    }
}
