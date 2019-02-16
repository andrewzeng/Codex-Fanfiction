package com.qan.fiction.util.constants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.qan.fiction.R;
import com.qan.fiction.util.storage.DatabaseHandler;
import com.qan.fiction.util.storage.ReaderWrapper;

public class Settings {


    public static final int FF_NET = 0;
    public static final int FP_COM = 1;
    public static final int AO3 = 2;
    public static final int ALL = 3;
    public static final int OFFLINE = 3;

    public static final int TEXT_SMALL = 0;
    public static final int TEXT_MED = 1;
    public static final int TEXT_LARGE = 2;
    public static final int TEXT_XL = 3;


    public static final int AUTO = 0;
    public static final int LANDSCAPE = 1;
    public static final int PORTRAIT = 2;

    public static final int POPULARITY = 0;
    public static final int TITLE = 1;


    public static void setSiteFiltering(Context context, int site) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.site_sorting), site);
        editor.commit();
    }

    public static void setStatusFiltering(Context context, int status) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.filter_status), status);
        editor.commit();
    }

    public static String site(int site) {
        if (site == FF_NET)
            return Constants.FF_NET_S;
        if (site == FP_COM)
            return Constants.FP_COM_S;
        if (site == AO3)
            return Constants.AO3_S;
        return null;
    }

    public static int getSiteFiltering(Context context) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getInt(context.getString(R.string.site_sorting), ALL);
        }
        return ALL;
    }

    public static int getTextDimension(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int a = pref.getInt(context.getString(R.string.text_size), TEXT_MED);
        if (a == TEXT_SMALL)
            return R.dimen.text_small;
        else if (a == TEXT_MED)
            return R.dimen.text_med;
        else if (a == TEXT_LARGE)
            return R.dimen.text_large;
        else
            return R.dimen.text_ultra;
    }


    /*public static int getUrl(Context context, String file) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(file + "page", 1);
    }

    public static void setPage(Context context, String file, int page) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(file + "page", page);
        editor.commit();
    }

    public static int getOffset(Context context, String file) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(file + "offset", 0);
    }

    public static void setOffset(Context context, String file, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(file + "offset", value);
        editor.commit();
    }
    */
    public static int getPage(Context context, String file) {
        DatabaseHandler db = new DatabaseHandler(context);
        int value = db.getKey(file + "page", 1);
        db.close();
        return value;
    }

    public static void setPage(Context context, String file, int page) {
        DatabaseHandler db = new DatabaseHandler(context);
        db.addKey(file + "page", page);
    }

    public static int getOffset(Context context, String file) {
        DatabaseHandler db = new DatabaseHandler(context);
        int value = db.getKey(file + "offset", 0);
        db.close();
        return value;
    }

    public static void setOffset(Context context, String file, int value) {
        DatabaseHandler db = new DatabaseHandler(context);
        db.addKey(file + "offset", value);
    }


    public static void setFiltering(Context context, int selection) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.filtering), selection);
        editor.commit();
    }

    public static void setFile(Context context, String file) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(context.getString(R.string.current), file);
        editor.commit();
    }

    public static void setSorting(Context context, int selection) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.sorting), selection);
        editor.commit();
    }

    public static void setRead(Context context, String file, boolean res) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(file + "read", res);
        editor.commit();
    }


    public static void setOnlineInfo(Context context, ReaderWrapper info) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(context.getString(R.string.online_info) + "_loc", info.loc);
        editor.putString(context.getString(R.string.online_info) + "_title", info.title);
        editor.putInt(context.getString(R.string.online_info) + "_id", info.id);
        editor.putInt(context.getString(R.string.online_info) + "_chap", info.chapters);
        editor.commit();
    }

    public static void setLastSite(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.last_site), value);
        editor.commit();
    }


    public static void setStarred(Context context, String file, boolean res) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(file + "starred", res);
        editor.commit();
    }

    public static void setSortingStyle(Context context, int style) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.sorting_style), style);
        editor.commit();
    }


    public static void deleteFile(Context context, String file) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        DatabaseHandler db = new DatabaseHandler(context);
        db.deleteData(file);
        db.close();
        editor.remove(file + "read");
        editor.remove(file + "starred");
        editor.commit();
    }


    public static String getFile(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(context.getString(R.string.current), null);
    }

    public static boolean getDescriptionStyle(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.description_style), true);
    }

    public static boolean groupCrossovers(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.group_crossovers), true);
    }

    public static int getOrientation(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(context.getString(R.string.orientation), 0);
    }

    public static int getLastSite(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(context.getString(R.string.last_site), Settings.ALL);
    }

    public static int getFiltering(Context context) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getInt(context.getString(R.string.filtering), 0);
        }
        return 0;
    }

    public static int getSorting(Context context) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getInt(context.getString(R.string.sorting), 0);
        }
        return 0;
    }

    public static boolean isRead(Context context, String file) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(file + "read", false);
    }

    public static boolean isLightTheme(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.light_theme), true);
    }


    public static Bundle getOnlineInfo(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Bundle b = new Bundle();
        b.putString("url", pref.getString(context.getString(R.string.online_info) + "_loc", null));
        b.putString("title", pref.getString(context.getString(R.string.online_info) + "_title", null));
        b.putInt("id", pref.getInt(context.getString(R.string.online_info) + "_id", 0));
        b.putInt("chapters", pref.getInt(context.getString(R.string.online_info) + "_chap", 0));
        return b;
    }


    public static boolean isStarred(Context context, String file) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(file + "starred", false);
    }

    public static int getSortingStyle(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(context.getString(R.string.sorting_style), POPULARITY);
    }

    public static int getDialogStyle(Context context) {
        if (isLightTheme(context))
            return AlertDialog.THEME_HOLO_LIGHT;
        else
            return AlertDialog.THEME_HOLO_DARK;
    }

    public static int getStatusFiltering(Context context) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getInt(context.getString(R.string.filter_status), 0);
        }
        return 0;
    }

    public static boolean getScreenSetting(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.keep_screen_on), true);
    }
}
