package com.qan.fiction.ui.activity;

import android.os.Bundle;


/**
 * These are the "main" application activities (separate from the one started from the browser which actually count towards
 * the activities which can receive intents (for practical purposes).
 */
public class CoreActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Global) getApplication()).increment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((Global) getApplication()).decrement();
    }
}