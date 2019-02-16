package com.qan.fiction.custom;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class AppCompatFragment extends Fragment {
    public AppCompatActivity getSupportActivity() {
        return ((AppCompatActivity) getActivity());
    }
}
