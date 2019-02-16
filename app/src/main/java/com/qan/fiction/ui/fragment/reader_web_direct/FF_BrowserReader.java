package com.qan.fiction.ui.fragment.reader_web_direct;

import android.os.Bundle;
import com.qan.fiction.ui.ff_activity.FF_ReaderFragment;

public class FF_BrowserReader extends FF_ReaderFragment {
    public void initialize(Bundle b, Bundle saved) {
        super.initialize(b, saved);
        getSupportActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
