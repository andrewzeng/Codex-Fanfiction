package com.qan.fiction.util.storage;

import android.content.Context;
import android.util.SparseArray;
import com.qan.fiction.adapter.expandable.ListChild;
import com.qan.fiction.adapter.expandable.ListGroup;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.storage.entries.Entry;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class StoryUtils {


    public static final int FILTER_ALL = 0;
    public static final int FILTER_CATEGORY = 1;
    public static final int FILTER_AUTHOR = 2;
    public static final int FILTER_STARRED = 3;
    public static final int FILTER_UNREAD = 4;

    public static final int COMPLETE = 1;
    public static final int IN_PROGRESS = 2;

    public static final int SORT_DOWNLOAD = 0;
    public static final int SORT_UPDATE = 1;
    public static final int SORT_PUBLISH = 2;
    public static final int SORT_REVIEWS = 3;
    public static final int SORT_FAVS = 4;
    public static final int SORT_FOLLOWS = 5;
    private static final int SORT_WORDS = 6;


    public static final SparseArray<Comparator<Entry>> compare;

    static {
        compare = new SparseArray<Comparator<Entry>>();
        compare.put(SORT_DOWNLOAD, new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return -Long.valueOf(a.time).compareTo(b.time);
            }
        });
        compare.put(SORT_UPDATE, new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return -Long.valueOf(a.update).compareTo(b.update);
            }
        });
        compare.put(SORT_PUBLISH, new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return -Long.valueOf(a.publish).compareTo(b.publish);
            }
        });
        compare.put(SORT_REVIEWS, new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return -Integer.valueOf(a.reviews).compareTo(b.reviews);
            }
        });
        compare.put(SORT_FAVS, new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return -Integer.valueOf(a.favorites).compareTo(b.favorites);
            }
        });
        compare.put(SORT_FOLLOWS, new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return -Integer.valueOf(a.follows).compareTo(b.follows);
            }
        });
        compare.put(SORT_WORDS, new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return -Integer.valueOf(a.words).compareTo(b.words);
            }
        });
    }


    public static void delete(Context context, String file) {
        DatabaseHandler db = new DatabaseHandler(context);
        Entry e = db.getStory(file);
        if (e == null) {
            db.close();
            return;//If we start to finalize update, cannot delete
        }
        for (int i = 1; i <= e.chapters; i++)
            context.deleteFile(file + "_" + i);
        db.deleteEntry(e);
        Settings.deleteFile(context, file);
        db.close();
    }

    public static void deleteEntry(Context context, String file) {
        DatabaseHandler db = new DatabaseHandler(context);
        Entry e = db.getStory(file);
        if (e != null)
            db.deleteEntry(e);
        db.close();
    }


    public static List<Entry> getStories(Context context) {
        int site = Settings.getSiteFiltering(context);
        DatabaseHandler db = new DatabaseHandler(context);
        List<Entry> list = db.getAllStories(site);
        db.close();
        return list;
    }

    public static ArrayList<ListGroup> getGroups(Context context, List<Entry> entries,
                                                 int site, int filter, int sort, int status) {
        if (entries.isEmpty())
            return new ArrayList<ListGroup>();
        if (status == COMPLETE || status == IN_PROGRESS) {
            boolean complete = status == COMPLETE;
            for (int i = 0; i < entries.size(); i++)
                if (entries.get(i).complete != complete)
                    entries.remove(i--);
        }

        TreeMap<String, List<Entry>> map = new TreeMap<String, List<Entry>>();
        if (filter == FILTER_ALL) {
            if (site == Settings.ALL)
                map.put("All", entries);
            else
                map.put(Constants.siteNAme.get(Settings.site(site)), entries);
        } else if (filter == FILTER_CATEGORY) {
            for (Entry e : entries) {
                List<String> list = e.getCategories();
                if (!Settings.groupCrossovers(context) || list.size() == 1)
                    for (String category : list) {
                        if (!map.containsKey(category))
                            map.put(category, new ArrayList<Entry>());
                        map.get(category).add(e);
                    }
                else {
                    if (!map.containsKey("Crossovers"))
                        map.put("Crossovers", new ArrayList<Entry>());
                    map.get("Crossovers").add(e);
                }
            }
        } else if (filter == FILTER_AUTHOR) {
            for (Entry e : entries) {
                if (!map.containsKey(e.author))
                    map.put(e.author, new ArrayList<Entry>());
                map.get(e.author).add(e);
            }
        } else if (filter == FILTER_STARRED) {
            ArrayList<Entry> list = new ArrayList<Entry>();
            for (Entry a : entries)
                if (Settings.isStarred(context, a.file))
                    list.add(a);
            if (list.isEmpty())
                return new ArrayList<ListGroup>();
            map.put("Starred", list);
        } else if (filter == FILTER_UNREAD) {
            ArrayList<Entry> list = new ArrayList<Entry>();
            for (Entry a : entries)
                if (!Settings.isRead(context, a.file))
                    list.add(a);
            if (list.isEmpty())
                return new ArrayList<ListGroup>();
            map.put("Unread", list);
        }

        ArrayList<ListGroup> list = new ArrayList<ListGroup>();
        for (String s : map.keySet()) {
            Collections.sort(map.get(s), compare.get(sort));
            ArrayList<ListChild> child = new ArrayList<ListChild>();
            int unread = 0;
            for (Entry e : map.get(s))
                if (!Settings.isRead(context, e.file))
                    unread++;

            ListGroup group = new ListGroup(s, unread, child, filter);
            list.add(group);
            for (Entry e : map.get(s))
                child.add(new ListChild(e, message(sort, e)));
        }


        return list;
    }

    public static String message(int sort, Entry value) {
        if (sort == SORT_DOWNLOAD) {
            Calendar c = new GregorianCalendar();
            c.setTimeZone(TimeZone.getDefault());
            c.setTimeInMillis(value.time);
            return new SimpleDateFormat("'Last Sync:' MM-dd-yy HH:mm").format(c.getTime());
        } else if (sort == SORT_UPDATE) {
            if (value.update != -1) {
                Calendar c = new GregorianCalendar();
                c.setTimeZone(TimeZone.getDefault());
                c.setTimeInMillis(value.update);
                return new SimpleDateFormat("'Updated:' MM-dd-yy").format(c.getTime());
            } else
                return "No updates since publication.";
        } else if (sort == SORT_PUBLISH) {
            Calendar c = new GregorianCalendar();
            c.setTimeZone(TimeZone.getDefault());
            c.setTimeInMillis(value.publish);
            return new SimpleDateFormat("'Published:' MM-dd-yy").format(c.getTime());
        } else if (sort == SORT_REVIEWS) {
            return value.reviewName() + new DecimalFormat().format(value.reviews);
        } else if (sort == SORT_FAVS) {
            return value.favoritesName() + new DecimalFormat().format(value.favorites);
        } else if (sort == SORT_FOLLOWS) {
            return value.followsName() + new DecimalFormat().format(value.follows);
        } else if (sort == SORT_WORDS) {
            return value.wordsName() + new DecimalFormat().format(value.words);
        } else
            throw new IllegalArgumentException("Sorting Code: " + sort + " not implemented.");
    }


}

