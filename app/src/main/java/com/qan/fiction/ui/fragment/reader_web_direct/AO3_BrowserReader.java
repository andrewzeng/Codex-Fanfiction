package com.qan.fiction.ui.fragment.reader_web_direct;

import android.os.Bundle;
import com.qan.fiction.ui.ao3_activity.AO3_ReaderFragment;

public class AO3_BrowserReader extends AO3_ReaderFragment {
    public void initialize(Bundle b, Bundle saved) {
        super.initialize(b, saved);
        getSupportActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


}
