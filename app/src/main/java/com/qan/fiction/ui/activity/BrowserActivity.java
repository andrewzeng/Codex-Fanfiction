package com.qan.fiction.ui.activity;

import android.os.Bundle;
import com.qan.fiction.R;
import com.qan.fiction.ui.fragment.reader_web_direct.AO3_BrowserReader;
import com.qan.fiction.ui.fragment.reader_web_direct.FF_BrowserReader;
import com.qan.fiction.ui.fragment.reader_web_direct.FP_BrowserReader;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.storage.entries.Entry;
import com.qan.fiction.util.web.WebUtils;

import static com.qan.fiction.ui.fragment.ReaderFragment.ReaderFragmentListener;


public class BrowserActivity extends BaseActivity implements ReaderFragmentListener {


    public void onCreate(Bundle saved) {
        super.onCreate(saved);

        if (saved == null) {
            String url = getIntent().getData().toString();
            Bundle b = WebUtils.web_parse(this, url).getExtras();
            b.putInt("chapters", 0);
            b.putString("title", getString(R.string.loading_page));
            String site = b.getString("site");
            if (site.equals(Constants.FF_NET_S)) {
                openFragment(new FF_BrowserReader(), b, false);
            } else if (site.equals(Constants.FP_COM_S)) {
                openFragment(new FP_BrowserReader(), b, false);
            } else if (site.equals(Constants.AO3_S)) {
                openFragment(new AO3_BrowserReader(), b, false);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onReview(Entry e) {
    }
}