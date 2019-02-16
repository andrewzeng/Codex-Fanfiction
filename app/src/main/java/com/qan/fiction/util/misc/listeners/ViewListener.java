package com.qan.fiction.util.misc.listeners;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public interface ViewListener {
    public void startDownloadService(Intent i);

    public void openFragment(Fragment fragment, Bundle b);

    public void openTab(Fragment fragment, FragmentTransaction ft);

    /**
     * Sets the content for the tabs which are opened.
     */
    public void setContent();
}
