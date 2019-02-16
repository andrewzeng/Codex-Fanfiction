package com.qan.fiction.ui.abs_web_activity.adapters;


import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.qan.fiction.R;
import com.qan.fiction.util.misc.listeners.Loadable;
import com.qan.fiction.util.storage.entries.Entry;
import com.qan.fiction.util.storage.entries.PlaceHolderEntry;

import java.util.List;

public class BookAdapter extends StreamAdapter<Entry> {


    public BookAdapter(Context context, List<Entry> entries, int total, Loadable activity, int size) {
        super(context, entries, total, activity, size);
    }

    @Override
    public boolean isPlaceholder(Entry entry) {
        return entry instanceof PlaceHolderEntry;
    }


    @Override
    protected void makeView(View v, Entry e) {
        TextView r = (TextView) v.findViewById(R.id.row1);
        TextView s = (TextView) v.findViewById(R.id.row2);
        TextView t = (TextView) v.findViewById(R.id.row3);
        r.setText(Html.fromHtml(e.title));
        s.setText(Html.fromHtml(e.description));
        t.setText("by " + Html.fromHtml(e.getAuthor()));
        v.findViewById(R.id.starred).setVisibility(View.GONE);
        v.findViewById(R.id.divider).setVisibility(View.GONE);
    }


}
