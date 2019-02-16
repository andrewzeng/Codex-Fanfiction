package com.qan.fiction.ui.ao3_activity.norm;

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
import com.qan.fiction.ui.ao3_activity.AO3_ReaderFragment;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.download.AO3_Extract;
import com.qan.fiction.util.download.StoryDownload;
import com.qan.fiction.util.storage.SerPair;
import com.qan.fiction.util.storage.entries.Entry;
import com.qan.fiction.util.storage.entries.PlaceHolderEntry;
import com.qan.fiction.util.web.Web;
import com.qan.fiction.util.web.WebUtils;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;

import static com.qan.fiction.util.constants.Conversion.dp;

public class AO3_Paginate extends Paginate<Entry> {

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
        return AO3_Extract.getEntries(doc, category, getSite());
    }

    @Override
    public int getTotal(Document doc) {
        return AO3_Extract.getTotal(doc);
    }

    @Override
    public ArrayList<String> order(Document doc) {
        return AO3_Extract.getOrder(doc);
    }

    @Override
    public String getString(ArrayList<CharSequence> key, String item) {
        if (item.equals("work_search[sort_column]"))
            return "Sort By:";
        else if (item.equals("work_search[language_id]"))
            return "Language:";
        else if (item.equals("work_search[rating_ids]"))
            return "Ratings:";
        return null;
    }

    @Override
    public HashMap<String, String> transferData() {
        HashMap<String, String> transfer = super.transferData();
        HashMap<String, String> result = new HashMap<String, String>();
        for (String key : transfer.keySet()) {
            if (transfer.get(key).length() != 0)
                result.put(key, transfer.get(key));
        }
        return result;
    }

    @Override
    public HashMap<String, ArrayList<SerPair<String, String>>> getFields(Document doc) {
        return AO3_Extract.getFields(doc);
    }

    public String url(String url, int page) {
        StringWrapperGet wrapper = new StringWrapperGet(url, null);
        if (data.containsKey("work_search[sort_column]"))
            wrapper.append("work_search[sort_column]");
        if (data.containsKey("work_search[language_id]"))
            wrapper.append("work_search[language_id]");
        if (data.containsKey("work_search[rating_ids]"))
            wrapper.append("work_search[rating_ids]");
        wrapper.append("page");
        return wrapper.toString();
    }

    @Override
    public String url_mobile(String url, int page) {
        return url(url, page);
    }

    @Override
    protected StreamAdapter<Entry> getAdapter() {
        return (BookAdapter) getListAdapter();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDialog(((BookAdapter) getListAdapter()).getItem(position));
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
                b.putString("url", WebUtils.format(Constants.normalView.get(Constants.AO3_S), getId(e.site, e.file)));
                b.putInt("chapters", e.chapters);
                b.putInt("id", Integer.parseInt(e.getId()));
                b.putString("site", getSite());
                b.putString("title", e.title);
                getViewListener().openFragment(new AO3_ReaderFragment(), b);

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

    private void listDialog(final Entry e) {

        final AlertBuilder builder = new AlertBuilder(getActivity());
        builder.setTitle(R.string.options);
        ListView v = getPopupListView();
        builder.setView(v);
        final AlertDialog dialog = builder.create();

        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    String url = WebUtils.format(Constants.normalView.get(Constants.AO3_S), getId(e.site, e.file));
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

    public String getId(String site, String file) {
        return Entry.getId(file, site);
    }

    @Override
    public String getSite() {
        return Constants.AO3_S;
    }

    @Override
    protected ArrayList<String> getExclude(Document doc) {
        return new ArrayList<String>();
    }

    @Override
    public void putPage(HashMap<String, String> data, String s) {
        data.put("page", s);
    }
}
