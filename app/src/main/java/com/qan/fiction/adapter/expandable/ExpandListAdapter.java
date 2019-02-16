package com.qan.fiction.adapter.expandable;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.qan.fiction.R;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.storage.StoryUtils;
import com.qan.fiction.util.storage.entries.Entry;

import java.util.ArrayList;
import java.util.List;

public class ExpandListAdapter extends BaseExpandableListAdapter {

    private Context context;
    public ArrayList<ListGroup> groups;

    public ExpandListAdapter(Context context, ArrayList<ListGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ListChild> chList = groups.get(groupPosition).getChildren();
        return chList.get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return hash(((ListChild) getChild(groupPosition, childPosition)).getEntry().file);
    }


    public long hash(String string) {
        long h = 1125899906842597L;
        int len = string.length();

        for (int i = 0; i < len; i++) {
            h = 31 * h + string.charAt(i);
        }
        return h;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convert,
                             ViewGroup parent) {
        ListChild child = (ListChild) getChild(groupPosition, childPosition);
        View v = convert;
        if (v == null) {
            if (Settings.isLightTheme(context))
                v = View.inflate(context, R.layout.list_multi, null);
            else
                v = View.inflate(context, R.layout.list_multi_dark, null);
        }
        TextView r = (TextView) v.findViewById(R.id.row1);
        TextView s = (TextView) v.findViewById(R.id.row2);
        TextView t = (TextView) v.findViewById(R.id.row3);
        r.setVisibility(View.VISIBLE);
        s.setVisibility(View.VISIBLE);
        t.setVisibility(View.VISIBLE);

        boolean enabled = Settings.getDescriptionStyle(context);
        String text;
        if (enabled) {
            text = child.getEntry().description;
        } else {
            text = context.getString(R.string.description_hidden);
        }
        r.setText(Html.fromHtml(child.getEntry().title));
        s.setText(Html.fromHtml(text));
        if (child.getNote().length() == 0)
            t.setVisibility(View.GONE);
        else
            t.setText(child.getNote());
        if (!Settings.isRead(context, child.getEntry().file)) {
/*            if (Settings.isLightTheme(context))
                v.setBackgroundResource(R.drawable.highlight);
            else
                v.setBackgroundResource(R.drawable.highlight_dark);*/
        } else {
            v.setBackgroundResource(0);
        }

        final ImageView b = (ImageView) v.findViewById(R.id.starred);
        final boolean[] state = new boolean[]{Settings.isStarred(context, child.getEntry().file)};
        if (Settings.isLightTheme(context))
            if (state[0])
                b.setImageResource(R.drawable.star_color);
            else
                b.setImageResource(R.drawable.star);
        else {
            if (state[0])
                b.setImageResource(R.drawable.star_color_dark);
            else
                b.setImageResource(R.drawable.star_dark);
        }

        final String file = child.getEntry().file;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state[0] = !state[0];
                Settings.setStarred(context, file, state[0]);
                if (state[0])
                    b.setImageResource(R.drawable.star_color);
                else
                    b.setImageResource(R.drawable.star);
                notifyDataSetChanged();
            }
        });
        return v;
    }

    public int getChildrenCount(int groupPosition) {
        ArrayList<ListChild> chList = groups.get(groupPosition).getChildren();

        return chList.size();

    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        ListGroup group = (ListGroup) getGroup(groupPosition);
        return hash(group.getName());
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convert,
                             ViewGroup parent) {
        ListGroup group = (ListGroup) getGroup(groupPosition);
        View v = convert;
        if (v == null) {
            v = View.inflate(context, R.layout.list_filtering, null);
        }
        String s = group.getName();
        if (group.getFilterType() == StoryUtils.FILTER_AUTHOR)
            s = s.substring(0, s.lastIndexOf('@'));
        TextView a = (TextView) v.findViewById(R.id.title);
        a.setText(Html.fromHtml(s));
        TextView b = (TextView) v.findViewById(R.id.hint);
        if (group.getNumberUnread() > 0)
            b.setText(group.getNumberUnread() + " unread");
        else
            b.setText("");
        if (groupPosition == getGroupCount() - 1 || isExpanded) {
            v.findViewById(R.id.divider).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.divider).setVisibility(View.VISIBLE);
        }

        if (isExpanded)
            ((ImageView) v.findViewById(R.id.indicator)).setImageResource(R.drawable.expand);
        else
            ((ImageView) v.findViewById(R.id.indicator)).setImageResource(R.drawable.collapse);
        return v;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public void setGroups(ArrayList<ListGroup> groups) {
        this.groups = groups;
    }

    /**
     * @return All the entries currently backed by the adapter.
     */
    public List<Entry> getEntries() {
        ArrayList<Entry> all = new ArrayList<Entry>();
        for (ListGroup group : groups) {
            for (ListChild child : group.getChildren()) {
                all.add(child.getEntry());
            }
        }
        return all;
    }
}