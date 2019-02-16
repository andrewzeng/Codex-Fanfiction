package com.qan.fiction.ui.abs_web_activity.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.qan.fiction.R;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.misc.listeners.Loadable;

import java.util.List;

public abstract class StreamAdapter<T> extends ArrayAdapter<T> {
    protected int page;
    protected int total;
    protected int size;
    protected Loadable activity;
    protected boolean finite;

    public StreamAdapter(Context context, List<T> entries, int total, Loadable activity, int size) {
        super(context, android.R.layout.simple_list_item_1, entries);
        this.total = total;
        this.activity = activity;
        this.size = size;
    }

    /**
     * Sets if the stream is fixed, so we know when to stop.
     */
    public void setFinite(boolean finite) {
        this.finite = finite;
    }

    public int getPage() {
        return page;
    }


    @Override
    public void add(T object) {
        if (!isPlaceholder(object) || !finite)
            super.add(object);
    }

    @Override
    public View getView(int pos, View convert, ViewGroup parent) {
        View v = convert;
        T e = getItem(pos);

        if (!isPlaceholder(e)) {
            if (v == null || v.findViewById(R.id.row1) == null) {
                if (Settings.isLightTheme(getContext()))
                    v = View.inflate(getContext(), R.layout.list_multi_mod, null);
                else
                    v = View.inflate(getContext(), R.layout.list_multi_mod_dark, null);
            }
            page = pos / size + 1;
            makeView(v, e);
        } else {
            v = View.inflate(getContext(), R.layout.progress_circle, null);
            if (page * size < total) {
                activity.loadNextPage();
            } else {
                v.setVisibility(View.GONE);
            }
        }
        return v;

    }

    protected abstract void makeView(View v, T e);

    public abstract boolean isPlaceholder(T entry);

}
