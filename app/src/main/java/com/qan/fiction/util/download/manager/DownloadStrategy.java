package com.qan.fiction.util.download.manager;

import android.content.Context;
import com.qan.fiction.R;
import com.qan.fiction.util.storage.DatabaseHandler;
import com.qan.fiction.util.storage.FileFormatter;
import com.qan.fiction.util.storage.entries.Entry;

public class DownloadStrategy extends TransferStrategy {

    private boolean newDownload;


    public DownloadStrategy(Context context, DatabaseHandler db, TransferManager manager, boolean newDownload) {
        super(context, db, manager);
        this.newDownload = newDownload;
    }


    @Override
    public boolean isDuplicate() {
        String file = FileFormatter.formatFilePrefix(getManager().getSite(), getManager().getStoryId());
        return newDownload && getDatabase().exists(file);
    }

    @Override
    public String getStopMessage() {
        return getContext().getString(R.string.download_stopped);
    }

    @Override
    public String getFailureMessage() {
        return getContext().getString(R.string.download_failed);
    }

    @Override
    public String getCompletionMessage() {
        return getContext().getString(R.string.download_complete);
    }

    @Override
    public String getCompletionMessage(Entry e) {
        return "Download of " + e.title + " complete.";
    }

    @Override
    public boolean isNewDownload() {
        return newDownload;
    }

    @Override
    public int getChapterStart() {
        return 1;
    }
}
