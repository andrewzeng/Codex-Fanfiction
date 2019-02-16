package com.qan.fiction.custom;

import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;

public class AppCompatListFragment extends ListFragment  {
    public AppCompatActivity getSupportActivity() {
        return ((AppCompatActivity) getActivity());
    }
}

