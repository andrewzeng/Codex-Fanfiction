package com.qan.fiction.util.storage;

import android.content.Context;
import android.os.Environment;

import com.qan.fiction.R;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.storage.entries.Entry;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

public class BookExport {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String writeToDisk(Context context, Entry e) {
        if (!isExternalStorageWritable())
            return context.getString(R.string.cannot_write);
        String s = stripIllegalFileCharacters(e.title);
        // TODO: writing the epub seems to be broken, doesn't show up even though the write succeeds
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() +
                        "/Books/Codex_Reader", s + ".epub");
        file.mkdirs();
        try {
            Book book = new Book();
            setMetadata(e, book);
            for (int i = 0; i < e.chapters; i++) {
                Resource r = new Resource(context.openFileInput(e.file + "_" + (i + 1)),
                        "chapter_" + (i + 1) + ".xhtml");
                book.addSection("Chapter " + (i + 1), r);
            }
            EpubWriter writer = new EpubWriter();
            file.delete();
            writer.write(book, new FileOutputStream(file));
        } catch (Exception ex) {
            ex.printStackTrace();
            return context.getString(R.string.write_failed);
        }
        return context.getString(R.string.saved_ebook);
    }

    public static String stripIllegalFileCharacters(String file) {
        String s = file.replaceAll("[^a-zA-Z0-9.-]", "_");
        if (s.length() > 128)
            return s.substring(0, 128);
        return s;
    }

    private static String getExtension(String site) {
        if (site.equals(Constants.FF_NET_S) || site.equals(Constants.FP_COM_S))
            return ".jpeg";
        return ".jpeg";
    }

    private static void setMetadata(Entry e, Book book) {
        book.getMetadata().addTitle(e.title);
        book.getMetadata().setAuthors(Arrays.asList(new Author(e.getAuthor(), "")));
        book.getMetadata().addDescription(e.description);
        setTime(e.publish, Date.Event.PUBLICATION, book);
        setTime(e.update, Date.Event.MODIFICATION, book);
        setTime(System.currentTimeMillis(), Date.Event.CREATION, book);
        book.getMetadata().addPublisher(Constants.siteNAme.get(e.site));
    }

    private static void setTime(long time, Date.Event event, Book book) {
        Calendar c = new GregorianCalendar();
        c.setTimeZone(TimeZone.getDefault());
        c.setTimeInMillis(time);
        book.getMetadata().addDate(new Date(c.getTime(), event));
    }
}
