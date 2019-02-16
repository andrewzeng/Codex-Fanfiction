package com.qan.fiction.util.download.manager;

import android.content.Context;
import com.qan.fiction.R;
import com.qan.fiction.util.storage.DatabaseHandler;
import com.qan.fiction.util.storage.entries.Entry;

public class UpdateStrategy extends TransferStrategy {

    private final int chapters;

    public UpdateStrategy(Context context, DatabaseHandler db, TransferManager manager, int chapters) {
        super(context, db, manager);
        this.chapters = chapters;
    }

    @Override
    public boolean isDuplicate() {
        return false;
    }

    @Override
    public String getStopMessage() {
        return getContext().getString(R.string.update_stopped);
    }

    @Override
    public String getFailureMessage() {
        return getContext().getString(R.string.update_failed);
    }

    @Override
    public String getCompletionMessage() {
        return getContext().getString(R.string.update_complete);
    }

    @Override
    public String getCompletionMessage(Entry e) {
        return "Update of " + e.title + " complete.";
    }

    @Override
    public boolean isNewDownload() {
        return getManager().getStoryInfo().chapters != chapters;
    }

    @Override
    public int getChapterStart() {
        return chapters + 1;
    }
}
