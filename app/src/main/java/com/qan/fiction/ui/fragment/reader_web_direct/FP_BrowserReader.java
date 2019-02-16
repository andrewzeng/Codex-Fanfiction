package com.qan.fiction.ui.fragment.reader_web_direct;

import android.os.Bundle;
import com.qan.fiction.ui.fp_activity.FP_ReaderFragment;

public class FP_BrowserReader extends FP_ReaderFragment {
    public void initialize(Bundle b, Bundle saved) {
        super.initialize(b, saved);
        getSupportActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
