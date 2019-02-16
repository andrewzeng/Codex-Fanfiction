package com.qan.fiction.util.storage.entries;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FF_Entry extends Entry {
    public FF_Entry(String file, String site, String author, String title, String description, String category, String genre,
                    int words, long publish, long update, long authorId, long time, int reviews, int favorites,
                    int follows, int chapters, boolean complete) {
        super(file, site, author, title, description, category, genre,
                words, publish, update, authorId, time, reviews, favorites, follows, chapters, complete);
    }

    public FF_Entry() {
    }

    @Override
    public String followsName() {
        return "Follows: ";
    }

    @Override
    public String favoritesName() {
        return "Favorites: ";
    }

    @Override
    public String reviewName() {
        return "Reviews: ";
    }

    @Override
    public String genreName() {
        return "Genre: ";
    }


    public String getCategoryString() {
        String cat = category;
        for (String s : Entry.exceptions) {
            String repr = s.replace(" ", "");
            cat = cat.replace(s, repr);
        }
        Pattern p = Pattern.compile(" \\+ ");
        Matcher matcher = p.matcher(cat);
        int count = 0;
        while (matcher.find())
            count++;
        for (String s : exceptions) {
            String repr = s.replace(" ", "");
            cat = cat.replace(repr, s);
        }
        if (count >= 1)
            return "<br/><b>Categories: </b>" + cat.replaceAll(" \\+ ", ", ");
        else
            return "<br/><b>Category: </b>" + cat;
    }

    @Override
    public String wordsName() {
        return "Words: ";
    }

}
