package com.qan.fiction.util.storage.entries;

import com.qan.fiction.util.constants.Constants;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Entry implements Serializable {

    public String file, site, author, title, description, category, genre;
    public int words, favorites, reviews, follows, chapters;
    public long publish, update, time, authorId;
    public boolean complete;

    public static final String[] exceptions;

    static {
        exceptions = new String[]{"Rosario + Vampire"};
    }


    public Entry(String file, String site, String author, String title, String description, String category, String genre,
                 int words, long publish, long update, long authorId, long time,
                 int reviews, int favorites, int follows, int chapters, boolean complete) {
        this.file = file;
        this.site = site;
        this.author = author.trim();
        this.authorId = authorId;
        this.title = title.trim();
        this.description = description.trim();
        this.category = category.trim();
        this.reviews = reviews;
        this.favorites = favorites;
        this.follows = follows;
        this.words = words;
        this.genre = genre;
        this.chapters = chapters;
        this.publish = publish;
        this.update = update;
        this.complete = complete;
        this.time = time;
    }

    protected Entry() {
    }

    public String info() {
        DecimalFormat d = new DecimalFormat();
        Calendar c = new GregorianCalendar();
        c.setTimeZone(TimeZone.getDefault());
        c.setTimeInMillis(publish);
        Calendar b = new GregorianCalendar();
        b.setTimeZone(TimeZone.getDefault());
        b.setTimeInMillis(time);

        StringBuilder s = new StringBuilder();
        s.append("<b>Site: </b>").append(Constants.siteNAme.get(site))
                .append("<br/><b>Author: </b> ").append(getAuthor())
                .append(getCategoryString())
                .append("<br/><b>").append(genreName()).append("</b>").append(getGenreString())
                .append("<br/><b>Chapters: </b>").append(chapters)
                .append("<br/><b>").append(reviewName()).append("</b>").append(d.format(reviews))
                .append("<br/><b>").append(favoritesName()).append("</b>").append(d.format(favorites))
                .append("<br/><b>").append(followsName()).append("</b>").append(d.format(follows))
                .append("<br/><b>Words: </b>").append(d.format(words));
        if (publish != -1)
            s.append("<br/><b>Publish Date: </b>").append(new SimpleDateFormat("MM-dd-yyyy").format(c.getTime()));
        if (update != -1) {
            c.setTimeInMillis(update);
            s.append("<br/><b>Update Date: </b>").append(new SimpleDateFormat("MM-dd-yyyy").format(c.getTime()));
        }
        return s.append("<br/><b>Sync Date: </b>").append(new SimpleDateFormat("MM-dd-yyyy").format(b.getTime()))
                .append("<br/><b>Status: </b>").append(complete ? "Complete" : "In-Progress").toString();
    }

    public String modInfo() {
        DecimalFormat d = new DecimalFormat();
        Calendar c = new GregorianCalendar();
        c.setTimeZone(TimeZone.getDefault());
        c.setTimeInMillis(publish);
        Calendar b = new GregorianCalendar();
        b.setTimeZone(TimeZone.getDefault());
        b.setTimeInMillis(time);

        StringBuilder s = new StringBuilder();
        s.append("<b>Site: </b>").append(Constants.siteNAme.get(site))
                .append("<br/><b>Author: </b> ").append(getAuthor())
                .append(getCategoryString())
                .append("<br/><b>").append(genreName()).append("</b>").append(getGenreString())
                .append("<br/><b>Chapters: </b>").append(chapters)
                .append("<br/><b>").append(reviewName()).append("</b>").append(d.format(reviews))
                .append("<br/><b>").append(favoritesName()).append("</b>").append(d.format(favorites))
                .append("<br/><b>").append(followsName()).append("</b>").append(d.format(follows))
                .append("<br/><b>Words: </b>").append(d.format(words));
        if (publish != -1)
            s.append("<br/><b>Publish Date: </b>").append(new SimpleDateFormat("MM-dd-yyyy").format(c.getTime()));
        if (update != -1) {
            c.setTimeInMillis(update);
            s.append("<br/><b>Update Date: </b>").append(new SimpleDateFormat("MM-dd-yyyy").format(c.getTime()));
        }
        return s.append("<br/><b>Status: </b>").append(complete ? "Complete" : "In-Progress").toString();
    }

    public abstract String followsName();

    public abstract String favoritesName();

    public abstract String reviewName();

    public abstract String genreName();

    public String getId() {
        return Entry.getId(file, site);
    }

    public static String getId(String file, String site) {
        return file.substring(site.length() + 1);
    }

    public String getAuthor() {
        return author.substring(0, author.lastIndexOf('@'));
    }

    public List<String> getCategories() {
        String cat = category;
        for (String s : exceptions) {
            String repr = s.replace(" ", "");
            cat = cat.replace(s, repr);
        }
        String[] split = cat.split(" \\+ ");
        for (int i = 0; i < split.length; i++) {
            for (String t : exceptions) {
                String repr = t.replace(" ", "");
                split[i] = split[i].replace(repr, t);
            }
        }
        return Arrays.asList(split);
    }


    public abstract String getCategoryString();

    public Object getGenreString() {
        return genre.replace("/", ", ");
    }


    public abstract String wordsName();
}


