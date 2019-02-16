package com.qan.fiction.util.web;

import android.content.Intent;
import android.net.Uri;

public class Web {
    public static Intent web_intent(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }
}
