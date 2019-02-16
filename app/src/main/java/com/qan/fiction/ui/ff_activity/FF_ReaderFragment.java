package com.qan.fiction.ui.ff_activity;

import com.qan.fiction.ui.fragment.OnlineReaderFragment;
import com.qan.fiction.util.web.reader_data_manager.DataManager;
import com.qan.fiction.util.web.reader_data_manager.FFDataManager;

public class FF_ReaderFragment extends OnlineReaderFragment {

    @Override
    public DataManager getManagerInstance() {
        return new FFDataManager();
    }
}
