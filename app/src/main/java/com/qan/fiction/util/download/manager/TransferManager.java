package com.qan.fiction.util.download.manager;

import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.download.AO3_Extract;
import com.qan.fiction.util.download.Connector;
import com.qan.fiction.util.download.FF_Extract;
import com.qan.fiction.util.storage.entries.Entry;
import com.qan.fiction.util.web.WebUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransferManager {

    /**
     * The current download state.
     */
    private Document download;

    private String site;

    private int storyId;

    private Entry storyInfo;

    private List<DownloadListener> listeners;

    private String[] chapters;

    public TransferManager(String site, int storyId) {
        this.site = site;
        this.storyId = storyId;
        listeners = new ArrayList<DownloadListener>();
    }

    /**
     * Returns whether or not the download succeeded.
     */
    public boolean downloadInfo() {
        try {
            download = Connector.getUrl(WebUtils.format(Constants.siteInfo.get(site), storyId));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean extractInfo() {
        try {
            storyInfo = getInfo();
        } catch (Exception e) {
            return false;
        }
        return storyInfo != null;
    }


    public boolean downloadChapters(Collection<String> stoppedDownloads, int chapterStart, int chapterEnd) {
        chapters = new String[storyInfo.chapters];

        for (int currentChapter = chapterStart; currentChapter <= chapterEnd &&
                !stoppedDownloads.contains(storyInfo.file); currentChapter++) {
            try {
                download(currentChapter);
                notifyListeners(currentChapter, true);
            } catch (Exception e) {
                notifyListeners(currentChapter, false);
                return false;
            }
        }

        return true;
    }

    public void registerDownloadListener(DownloadListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(int chapter, boolean successful) {
        for (DownloadListener listener : listeners)
            listener.onChapterDownloadCompleted(chapter, successful);
    }

    private void download(int chapter) throws Exception {
        String next = getPage(chapter);
        download = Connector.getUrl(next);
        chapters[chapter - 1] = getText(download.getElementById(Constants.elementID.get(site)), site);
    }


    private String getText(Element text, String site) {
        if (site.equals(Constants.FF_NET_S) || site.equals(Constants.FP_COM_S)) {
            return text.html();
        } else if (site.equals(Constants.AO3_S)) {
            return text.select(".userstuff p").outerHtml();
        }
        return null;
    }


    private String getPage(int chapter) {
        if (site.equals(Constants.FF_NET_S) || site.equals(Constants.FP_COM_S)) {
            return WebUtils.format(Constants.download.get(site), storyId, chapter);
        } else if (site.equals(Constants.AO3_S)) {
            return AO3_Extract.getPageUrl(download, storyId, chapter);
        }
        return null;
    }

    private Entry getInfo() {
        if (site.equals(Constants.FF_NET_S) || site.equals(Constants.FP_COM_S)) {
            return FF_Extract.extract(download, site, storyId);
        } else if (site.equals(Constants.AO3_S)) {
            return AO3_Extract.extract(download, site, storyId);
        }
        return null;
    }

    public Document getDocument() {
        return download;
    }


    public String getSite() {
        return site;
    }


    public int getStoryId() {
        return storyId;
    }


    public Entry getStoryInfo() {
        return storyInfo;
    }

    /**
     * Return the chapters which were downloaded
     *
     * @return An array with each index containing the HTML for the each chapter, or {@code null} if it was not downloaded.
     */
    public String[] getDownloadedChapters() {
        return chapters;
    }
}
