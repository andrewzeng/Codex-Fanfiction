package com.qan.fiction.util.download.manager;

import android.content.Context;
import com.qan.fiction.R;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.storage.DatabaseHandler;
import com.qan.fiction.util.storage.entries.Entry;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public abstract class TransferStrategy implements DownloadListener {

    private final Context context;
    private final DatabaseHandler db;
    private final TransferManager manager;
    private List<ProgressListener> listeners;

    public TransferStrategy(Context context, DatabaseHandler db, TransferManager manager) {
        this.context = context;
        this.db = db;
        this.manager = manager;
        listeners = new ArrayList<ProgressListener>();
    }

    public void startTransfer(Collection<String> stoppedDownloads) {
        if (!getInfo(manager))
            return;

        Entry e = manager.getStoryInfo();

        onChapterDownloadCompleted(0, true);
        manager.registerDownloadListener(this);
        stoppedDownloads.remove(e.title);

        if (!download(stoppedDownloads, manager, e, getChapterStart()))
            return;
        if (isNewDownload())
            Settings.setRead(context, e.file, false);

        output(manager);
        notifyComplete(getCompletionMessage());
    }


    private void output(TransferManager manager) {
        Entry info = manager.getStoryInfo();
        db.deleteEntry(info);
        Pattern p = Pattern.compile("<img.+?>");
        String[] chapters = manager.getDownloadedChapters();
        try {
            String head = getHeader().toString();
            for (int i = 1; i <= info.chapters; i++) {
                if (chapters[i - 1] != null) {
                    String fileName = info.file + "_" + i;
                    FileOutputStream stream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                    PrintWriter out = new PrintWriter(stream);
                    out.println(head);
                    out.println(p.matcher(chapters[i - 1]).replaceAll("").replace("</img>", ""));
                    out.println("</body></html>");
                    out.flush();
                    out.close();
                }
            }
        } catch (IOException e) {
            notifyFailure(context.getString(R.string.read_write_failed));
        }
        db.addStory(info);
    }


    private StringBuilder getHeader() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("head.html")));
        StringBuilder build = new StringBuilder();
        String d;
        while ((d = br.readLine()) != null)
            build.append(d);
        return build;
    }

    private boolean download(Collection<String> stoppedDownloads, TransferManager manager, Entry e, int start) {
        stoppedDownloads.remove(manager.getStoryInfo().file);
        boolean downloadSuccess = false;
        if (!manager.downloadChapters(stoppedDownloads, start, e.chapters))
            notifyFailure(getFailureMessage());
        else if (stoppedDownloads.contains(e.file))
            stopDownload(stoppedDownloads, e, getStopMessage());
        else
            downloadSuccess = true;
        return downloadSuccess;
    }


    private void stopDownload(Collection<String> stoppedDownloads, Entry e, String msg) {
        stoppedDownloads.remove(e.file);
        notifyStopped(msg);
    }


    /**
     * Gets the info for the story.
     *
     * @return true iff the retrieval was successful.
     */
    private boolean getInfo(TransferManager manager) {
        boolean infoSuccess = false;
        if (isDuplicate()) {
            notifyFailure(context.getString(R.string.already_downloaded));
        } else if (!manager.downloadInfo()) {
            notifyFailure(context.getString(R.string.retrieve_info_failed));
        } else if (!manager.extractInfo()) {
            if (isDeleted(manager.getDocument(), manager.getSite()))
                notifyFailure(context.getString(R.string.story_deleted));
            else
                notifyFailure(context.getString(R.string.retrieve_info_failed));
        } else {
            infoSuccess = true;
        }
        notifyRetrieval(infoSuccess);
        return infoSuccess;
    }


    private boolean isDeleted(Document doc, String site) {
        if (site.equals(Constants.FF_NET_S) || site.equals(Constants.FP_COM_S))
            return doc.select("#content_wrapper_inner div.panel_warning").text().contains("Story Not Found");
        else if (site.equals(Constants.AO3_S))
            return doc.getElementById("chapters") == null;
        return false;
    }


    public void registerDownloadListener(ProgressListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onChapterDownloadCompleted(int chapter, boolean successful) {
        for (DownloadListener listener : listeners)
            listener.onChapterDownloadCompleted(chapter, successful);
    }


    private void notifyStopped(String msg) {
        for (ProgressListener listener : listeners)
            listener.onStopped(msg);
    }

    private void notifyRetrieval(boolean success) {
        for (ProgressListener listener : listeners)
            listener.onInfoRetrieved(success);
    }

    private void notifyFailure(String message) {
        for (ProgressListener listener : listeners)
            listener.onFailure(message);
    }

    private void notifyComplete(String message) {
        for (ProgressListener listener : listeners)
            listener.onComplete(message);
    }

    public Context getContext() {
        return context;
    }

    public DatabaseHandler getDatabase() {
        return db;
    }

    public TransferManager getManager() {
        return manager;
    }

    public abstract boolean isDuplicate();

    public abstract String getStopMessage();

    public abstract String getFailureMessage();

    public abstract String getCompletionMessage();

    public abstract String getCompletionMessage(Entry e);

    public abstract boolean isNewDownload();

    public abstract int getChapterStart();


}
