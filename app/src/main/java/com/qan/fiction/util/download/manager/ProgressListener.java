package com.qan.fiction.util.download.manager;

public interface ProgressListener extends DownloadListener {

    void onInfoRetrieved(boolean successful);

    void onFailure(String message);

    void onComplete(String message);

    void onStopped(String message);
}
