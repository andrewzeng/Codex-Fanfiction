package com.qan.fiction.ui.activity;

import android.app.Application;
import com.qan.fiction.util.storage.DatabaseHandler;


public class Global extends Application {


    private int running;

    private DatabaseHandler db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHandler(getApplicationContext());
    }

    public DatabaseHandler getDatabase() {
        return db;
    }

    public int getRunning() {
        return running;
    }

    public void increment() {
        running++;
    }

    public void decrement() {
        running--;
    }
}
