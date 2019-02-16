package com.qan.fiction.util.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.storage.entries.AO3_Entry;
import com.qan.fiction.util.storage.entries.Entry;
import com.qan.fiction.util.storage.entries.FF_Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    public static final int DATABASE_VERSION = 3;

    // Database Name
    public static final String DATABASE_NAME = "fiction_reader";

    // Table names
    public static final String TABLE_STORY = "story";
    public static final String TABLE_WEB = "web";
    public static final String TABLE_SEARCH = "search";

    // Contacts Table Columns names
    public static final String KEY_FILE = "file";
    public static final String KEY_SITE = "site";
    public static final String KEY_AUTHOR_ID = "author_id";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_WORDS = "words";
    public static final String KEY_PUBLISH = "publish_date";
    public static final String KEY_UPDATE = "update_date";
    public static final String KEY_REVIEWS = "reviews";
    public static final String KEY_FAVS = "favorites";
    public static final String KEY_FOLLOWS = "follows";
    public static final String KEY_COMPLETE = "complete";
    public static final String KEY_CHAPTERS = "chapters";
    public static final String KEY_CREATE = "last_time";
    public static final String KEY_GENRE = "genre";

    public static final String KEY_STRING = "string";
    public static final String KEY_VALUE = "value";

    public static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_STORY + "("
            + KEY_FILE + " TEXT PRIMARY KEY," + KEY_SITE + " TEXT,"
            + KEY_AUTHOR + " TEXT," + KEY_AUTHOR_ID + " INT,"
            + KEY_TITLE + " TEXT," + KEY_DESCRIPTION + " TEXT,"
            + KEY_CATEGORY + " TEXT,"
            + KEY_WORDS + " INT," + KEY_PUBLISH + " INT,"
            + KEY_UPDATE + " INT," + KEY_REVIEWS + " INT,"
            + KEY_FAVS + " INT," + KEY_FOLLOWS + " INT,"
            + KEY_CHAPTERS + " INT," + KEY_COMPLETE + " INT,"
            + KEY_CREATE + " INT," + KEY_GENRE + " TEXT" + ")";

    public static final String CREATE_WEB_TABLE = "CREATE TABLE " + TABLE_WEB + "("
            + KEY_STRING + " TEXT PRIMARY KEY," + KEY_VALUE + " INT" + ")";

    public static final String CREATE_SEARCH_TABLE = "CREATE TABLE " + TABLE_SEARCH + "("
            + KEY_STRING + " TEXT PRIMARY KEY," + KEY_VALUE + " INT" + ")";
    private final Context context;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_WEB_TABLE);
        db.execSQL(CREATE_SEARCH_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_WEB_TABLE);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Map<String, ?> map = preferences.getAll();
            for (String s : map.keySet()) {
                if (s.startsWith("http:") || s.endsWith("page") || s.endsWith("offset")) {
                    int a = (Integer) map.get(s);
                    ContentValues values = new ContentValues();
                    values.put(KEY_STRING, s);
                    values.put(KEY_VALUE, a);
                    db.insert(TABLE_WEB, null, values);
                }
            }
        }
        if (oldVersion < 3) {
            db.execSQL(CREATE_SEARCH_TABLE);
        }

    }

    public void increment(String key) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(true, TABLE_SEARCH, new String[]{KEY_VALUE}, KEY_STRING + "=?", new String[]{key},
                null, null, null, null);
        ContentValues cv = new ContentValues();
        cv.put(KEY_STRING, key);

        if (c.getCount() == 0) {
            cv.put(KEY_VALUE, 1);
        } else {
            c.moveToFirst();
            cv.put(KEY_VALUE, c.getInt(0) + 1);
        }
        db.insertWithOnConflict(TABLE_SEARCH, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

    }


    public void addKey(String key, int value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STRING, key);
        values.put(KEY_VALUE, value);
        db.insertWithOnConflict(TABLE_WEB, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Cursor filterSearch(String key) {
        SQLiteDatabase db = getReadableDatabase();
        String a = key + "%";
        String b = " " + key + "%";
        return db.rawQuery("SELECT rowid _id, " + KEY_STRING + " FROM " + TABLE_SEARCH + " WHERE " +
                KEY_STRING + " LIKE ?  OR " + KEY_STRING + " LIKE ? ORDER BY " + KEY_STRING + " ASC",
                new String[]{a, b});
    }

    public void clearSearches() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SEARCH, null, null);
        db.close();
    }

    public ArrayList<SerPair<String, Integer>> topSearches() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(true, TABLE_SEARCH, null, null, null,
                null, null, KEY_VALUE + " DESC", "50");
        ArrayList<SerPair<String, Integer>> list = new ArrayList<SerPair<String, Integer>>();
        while (c.moveToNext())
            list.add(new SerPair<String, Integer>(c.getString(0), c.getInt(1)));
        c.close();
        db.close();
        return list;
    }

    public int getKey(String key, int def) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_WEB, new String[]{KEY_VALUE}, KEY_STRING + "=?", new String[]{key},
                null, null, null);
        if (c.getCount() == 0) {
            c.close();
            return def;
        }

        c.moveToNext();
        int val = c.getInt(0);
        c.close();
        db.close();
        return val;
    }


    public boolean exists(String file) {
        return getStory(file) != null;
    }

    public void addStory(Entry e) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (getStory(e.file) != null) {
            return;
        }
        ContentValues values = putAll(e);
        // Inserting Row
        db.insert(TABLE_STORY, null, values);
        db.close();
    }

    private ContentValues putAll(Entry e) {
        ContentValues values = new ContentValues();
        values.put(KEY_FILE, e.file);
        values.put(KEY_SITE, e.site);
        values.put(KEY_AUTHOR, e.author);
        values.put(KEY_AUTHOR_ID, e.authorId);
        values.put(KEY_TITLE, e.title);
        values.put(KEY_DESCRIPTION, e.description);
        values.put(KEY_CATEGORY, e.category);
        values.put(KEY_WORDS, e.words);
        values.put(KEY_PUBLISH, e.publish);
        values.put(KEY_UPDATE, e.update);
        values.put(KEY_FAVS, e.favorites);
        values.put(KEY_REVIEWS, e.reviews);
        values.put(KEY_FOLLOWS, e.follows);
        values.put(KEY_CHAPTERS, e.chapters);
        values.put(KEY_COMPLETE, e.complete ? 1 : 0);
        values.put(KEY_CREATE, e.time);
        values.put(KEY_GENRE, e.genre);
        return values;
    }

    public Entry getStory(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_STORY, null, KEY_FILE + "=?",
                new String[]{id}, null, null, null, null);
        if (c.getCount() == 0) {
            c.close();
            return null;
        }
        c.moveToNext();
        Entry e = nextEntry(c);
        c.close();
        db.close();
        return e;
    }

    public List<Entry> getAllStories(int site) {
        SQLiteDatabase db = getReadableDatabase();
        if (site == Settings.ALL) {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_STORY, null);
            ArrayList<Entry> list = new ArrayList<Entry>();
            while (c.moveToNext())
                list.add(nextEntry(c));
            c.close();
            db.close();
            return list;
        } else {
            Cursor c = db.query(TABLE_STORY, null, KEY_SITE + "=?",
                    new String[]{Settings.site(site)}, null, null, null, null);
            ArrayList<Entry> list = new ArrayList<Entry>();
            while (c.moveToNext())
                list.add(nextEntry(c));
            c.close();
            db.close();
            return list;
        }
    }

    public void deleteEntry(Entry e) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STORY, KEY_FILE + "=?", new String[]{e.file});
        db.close();
    }

    private Entry nextEntry(Cursor c) {
        String file = c.getString(0);
        String site = c.getString(1);
        String author = c.getString(2);
        int author_id = c.getInt(3);
        String title = c.getString(4);
        String description = c.getString(5);
        String category = c.getString(6);
        int words = c.getInt(7);
        long publish = c.getLong(8);
        long update = c.getLong(9);
        int reviews = c.getInt(10);
        int favorites = c.getInt(11);
        int follows = c.getInt(12);
        int chapters = c.getInt(13);
        boolean complete = c.getInt(14) == 1;
        long time = c.getLong(15);
        String genre = c.getString(16);
        if (site.equals(Constants.FF_NET_S) || site.equals(Constants.FP_COM_S))
            return new FF_Entry(file, site, author, title, description,
                    category, genre, words, publish, update, author_id, time, reviews,
                    favorites, follows, chapters, complete);
        else if (site.equals(Constants.AO3_S))
            return new AO3_Entry(file, site, author, title, description,
                    category, genre, words, publish, update, author_id, time, reviews,
                    favorites, follows, chapters, complete);
        return null;
    }

    public void deleteData(String file) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_WEB, KEY_STRING + "=?", new String[]{file + "offset"});
        db.delete(TABLE_WEB, KEY_STRING + "=?", new String[]{file + "page"});
        db.close();
    }
}