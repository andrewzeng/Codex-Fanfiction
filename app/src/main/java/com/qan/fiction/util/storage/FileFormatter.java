package com.qan.fiction.util.storage;

public class FileFormatter {

    public static final String SEPARATOR = "_";

    public static String formatFilePrefix(String site, int id) {
        return site + SEPARATOR + id;
    }

    public static String formatFile(String file, int page) {
        return file + SEPARATOR + page;
    }

}
