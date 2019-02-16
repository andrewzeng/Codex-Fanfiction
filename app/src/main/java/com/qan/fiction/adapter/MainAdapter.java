package com.qan.fiction.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.qan.fiction.R;

public class MainAdapter extends ArrayAdapter<String> {
    private String[] items;
    private Drawable[] images;

    public MainAdapter(Context context, String[] items, Drawable[] images) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.items = items;
        this.images = images;
    }

    @Override
    public View getView(int pos, View convert, ViewGroup parent) {
        View v = convert;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_row, null);
        }
        TextView t = (TextView) v.findViewById(R.id.row);
        t.setText(items[pos]);
        t.setCompoundDrawables(images[pos], null, null, null);
        return v;
    }
}
