package com.qan.fiction.adapter.expandable;

import com.qan.fiction.util.storage.entries.Entry;

public class ListChild {
    private Entry entry;
    private String note;

    public ListChild(Entry entry, String note) {
        this.setEntry(entry);
        this.setNote(note);
    }

    public int hashCode() {
        return getEntry().file.hashCode();
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
