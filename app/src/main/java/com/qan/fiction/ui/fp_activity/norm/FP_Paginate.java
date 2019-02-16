package com.qan.fiction.ui.fp_activity.norm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.qan.fiction.R;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AlertDialog;
import com.qan.fiction.ui.abs_web_activity.Paginate;
import com.qan.fiction.ui.abs_web_activity.adapters.BookAdapter;
import com.qan.fiction.ui.abs_web_activity.adapters.StreamAdapter;
import com.qan.fiction.ui.fp_activity.FP_ReaderFragment;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.download.FF_Extract;
import com.qan.fiction.util.download.StoryDownload;
import com.qan.fiction.util.storage.SerPair;
import com.qan.fiction.util.storage.entries.Entry;
import com.qan.fiction.util.storage.entries.PlaceHolderEntry;
import com.qan.fiction.util.web.Web;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.qan.fiction.util.constants.Conversion.dp;

public class FP_Paginate extends Paginate<Entry> {


    private final List<String> order = Arrays.asList("genreid1", "languageid", "sortid",
            "length", "statusid", "p", "timerange");


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
        put("p", "p");
    }};

    @Override
    public Entry getPlaceHolderEntry() {
        return new PlaceHolderEntry();
    }

    @Override
    public StreamAdapter<Entry> createAdapter(int size) {
        return new BookAdapter(getActivity(), list, total, this, size);
    }


    @Override
    public ArrayList<Entry> getListItems(Document doc) {
        return FF_Extract.getEntries(doc, category, getSite());
    }

    @Override
    public int getTotal(Document doc) {
        return FF_Extract.getTotalEntries(doc);
    }

    @Override
    public ArrayList<String> order(Document doc) {
        return FF_Extract.getOrder(doc);
    }

    @Override
    public HashMap<String, ArrayList<SerPair<String, String>>> getFields(Document doc) {
        return FF_Extract.getFields(doc);
    }

    public String url(String url, int page) {
        url = new StringWrapperGet(url, getArgumentFilter())
                .appendAll()
                .toString();
        return url;
    }

    protected HashMap<String, String> getArgumentFilter() {
        return filter;
    }

    protected List<String> getArgumentOrder() {
        return order;
    }

    @Override
    protected ArrayList<String> getExclude(Document doc) {
        return FF_Extract.getExclude(doc);
    }

    public String url_mobile(String url, int page) {
        return url.replace("www.fictionpress.com", "m.fictionpress.com");
    }

    @Override
    protected StreamAdapter<Entry> getAdapter() {
        return (BookAdapter) getListAdapter();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDialog(((BookAdapter) getListAdapter()).getItem(position));
    }

    private void listDialog(final Entry e) {

        final AlertBuilder builder = new AlertBuilder(getActivity());
        builder.setTitle(R.string.options);
        ListView v = getPopupListView();
        builder.setView(v);
        final AlertDialog dialog = builder.create();

        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    String url;
                    if (dualPane)
                        url = "https://fictionpress.com/s/" + getId(e.site, e.file) + "/1";
                    else
                        url = "https://m.fictionpress.com/s/" + getId(e.site, e.file) + "/1";
                    Intent i = Web.web_intent(url);
                    startActivity(i);
                } else if (position == 1) {
                    messageDialog(e.description, getString(R.string.descr), descr());
                } else if (position == 2) {
                    Intent intent = new Intent(getActivity(), StoryDownload.class);
                    intent.putExtra("id", Integer.parseInt(e.getId()));
                    intent.putExtra("site", e.site);
                    intent.putExtra("download", true);
                    getViewListener().startDownloadService(intent);
                }

                dialog.cancel();
            }


            private int descr() {
                if (Settings.isLightTheme(getActivity()))
                    return R.drawable.descr;
                else
                    return R.drawable.descr_dark;
            }
        });
        dialog.show();
    }

    private void messageDialog(String message, String title, int icon) {
        final AlertBuilder builder = new AlertBuilder(getActivity());
        builder.setTitle(title);
        builder.setIcon(icon);
        TextView r = new TextView(getActivity());
        r.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        r.setText(Html.fromHtml(message));
        Context c = getActivity();
        r.setPadding(dp(c, 15), dp(c, 15), dp(c, 15), dp(c, 15));
        ScrollView sv = new ScrollView(getActivity());
        sv.addView(r);
        builder.setView(sv);
        builder.setPositiveButton(getString(R.string.ok), null);
        AlertDialog d = builder.create();
        d.show();
    }

    private void showDialog(final Entry e) {
        final AlertBuilder builder = new AlertBuilder(getActivity());
        TextView t = new TextView(getActivity());
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        t.setLineSpacing(1.0f, 1.1f);
        t.setText(Html.fromHtml(e.modInfo()));
        t.setPadding(10, 0, 0, 0);
        ScrollView sv = new ScrollView(getActivity());
        sv.addView(t);
        builder.setTitle(e.title);
        builder.setView(sv);
        builder.setPositiveButton(getString(R.string.read), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle b = new Bundle();
                b.putString("url", "https://m.fictionpress.com/s/" + e.getId());
                b.putInt("chapters", e.chapters);
                b.putInt("id", Integer.parseInt(e.getId()));
                b.putString("site", Constants.FP_COM_S);
                b.putString("title", e.title);
                getViewListener().openFragment(new FP_ReaderFragment(), b);

            }
        });
        builder.setNeutralButton(getString(R.string.more), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                listDialog(e);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        final AlertDialog d = builder.create();
        d.show();
    }


    public String getId(String site, String file) {
        return Entry.getId(file, site);
    }


    public String getSite() {
        return Constants.FP_COM_S;
    }
}
