package com.qan.fiction.adapter.expandable;

import java.util.ArrayList;

public class ListGroup {
    private String name;
    private int numberUnread;
    private int filterType;
    private ArrayList<ListChild> children;

    public ListGroup(String name, int unread, ArrayList<ListChild> children, int filterType) {
        this.setName(name);
        this.setNumberUnread(unread);
        this.setChildren(children);
        this.setFilterType(filterType);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberUnread() {
        return numberUnread;
    }

    public void setNumberUnread(int numberUnread) {
        this.numberUnread = numberUnread;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public ArrayList<ListChild> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ListChild> children) {
        this.children = children;
    }
}
