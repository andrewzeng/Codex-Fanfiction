package com.qan.fiction.ui.abs_web_activity.adapters;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.qan.fiction.R;
import com.qan.fiction.util.storage.SerPair;

import java.util.ArrayList;

public class PairAdapter extends ArrayAdapter<SerPair<String, Integer>> {

    public PairAdapter(Context context, ArrayList<SerPair<String, Integer>> pairs) {
        super(context, android.R.layout.simple_list_item_1, pairs);
    }

    @Override
    public View getView(int pos, View convert, ViewGroup parent) {
        View v = convert;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.category_row, null);
        }
        TextView s = (TextView) v.findViewById(R.id.title);
        TextView t = (TextView) v.findViewById(R.id.hint);
        s.setText(Html.fromHtml(getItem(pos).first));
        int val = getItem(pos).second;
        t.setText(val + (val == 1 ? " search" : " searches"));
        return v;
    }
}
