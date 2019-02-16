package com.qan.fiction.ui.ao3_activity;

import com.qan.fiction.ui.fragment.OnlineReaderFragment;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.download.Connector;
import com.qan.fiction.util.web.reader_data_manager.AO3DataManager;
import com.qan.fiction.util.web.reader_data_manager.DataManager;

public class AO3_ReaderFragment extends OnlineReaderFragment {

    protected class AO3_Page extends OnlinePage {
        @Override
        protected CharSequence doInBackground(Void... params) {
            if (getDocument() == null)
                try {
                    setDocument(Connector.getUrl(getLocation()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return Constants.EX_SIGNAL;
                }
            return super.doInBackground(params);
        }
    }


    @Override
    public Page getDownloadInstance() {
        return new AO3_Page();
    }

    @Override
    public DataManager getManagerInstance() {
        return new AO3DataManager();
    }

}
