package com.qan.fiction.util.storage.entries;

public class PlaceHolderEntry extends Entry {
    @Override
    public String followsName() {
        return null;
    }

    @Override
    public String favoritesName() {
        return null;
    }

    @Override
    public String reviewName() {
        return null;
    }

    @Override
    public String genreName() {
        return null;
    }

    @Override
    public String getCategoryString() {
        return null;
    }

    @Override
    public String wordsName() {
        return null;
    }

    public boolean equals(Object o) {
        return o instanceof PlaceHolderEntry;
    }
}
