package com.qan.fiction.ui.abs_web_activity.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.qan.fiction.R;
import com.qan.fiction.ui.abs_web_activity.info.CommunityInfo;
import com.qan.fiction.ui.abs_web_activity.info.CommunityPlaceholder;
import com.qan.fiction.util.misc.listeners.Loadable;

import java.util.List;

public class CommunityAdapter extends StreamAdapter<CommunityInfo> {

    public CommunityAdapter(Context context, List<CommunityInfo> entries, int totalPages, Loadable l, int size) {
        super(context, entries, totalPages, l, size);
    }

    @Override
    protected void makeView(View v, CommunityInfo info) {
        TextView r = (TextView) v.findViewById(R.id.row1);
        TextView s = (TextView) v.findViewById(R.id.row2);
        TextView t = (TextView) v.findViewById(R.id.row3);
        r.setText(info.name);
        s.setText(info.summary);
        t.setText(info.result);
        v.findViewById(R.id.starred).setVisibility(View.GONE);
        v.findViewById(R.id.divider).setVisibility(View.GONE);

    }

    @Override
    public boolean isPlaceholder(CommunityInfo info) {
        return info instanceof CommunityPlaceholder;
    }
}
