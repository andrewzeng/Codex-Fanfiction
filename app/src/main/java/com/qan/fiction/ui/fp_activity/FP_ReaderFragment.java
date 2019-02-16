package com.qan.fiction.ui.fp_activity;

import com.qan.fiction.ui.fragment.OnlineReaderFragment;
import com.qan.fiction.util.web.reader_data_manager.DataManager;
import com.qan.fiction.util.web.reader_data_manager.FPDataManager;

public class FP_ReaderFragment extends OnlineReaderFragment {


    @Override
    public DataManager getManagerInstance() {
        return new FPDataManager();
    }
}
