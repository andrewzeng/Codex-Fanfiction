package com.qan.fiction.util.storage;

import java.io.Serializable;

public class ReaderWrapper implements Serializable {
    public String loc;
    public int chapters;
    public int id;
    public String title;

    public ReaderWrapper(String location, String title, int id, int chapters) {
        this.loc = location;
        this.title = title;
        this.id = id;
        this.chapters = chapters;
    }
}