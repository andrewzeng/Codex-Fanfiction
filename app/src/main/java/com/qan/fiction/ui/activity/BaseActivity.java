package com.qan.fiction.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.qan.fiction.R;
import com.qan.fiction.ui.service_connection.ConnectionManager;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.download.StoryDownload;
import com.qan.fiction.util.misc.listeners.ViewListener;

import static com.qan.fiction.util.download.StoryDownload.MSG_STOP;
import static com.qan.fiction.util.download.StoryDownload.MSG_UNREGISTER_CLIENT;

public abstract class BaseActivity extends AppCompatActivity implements ViewListener {

    private ProgressDialog d;
    private boolean bound;
    private ConnectionManager manager;
    private BroadcastReceiver themeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Settings.isLightTheme(this))
            setTheme(R.style.Theme_Styled);
        else
            setTheme(R.style.Theme_Styled_Dark);
        super.onCreate(savedInstanceState);
        // Mint.initAndStartSession(this, "INSERT API KEY HERE");
        setContentView(R.layout.standard_menu);


        setConnectionManager(new ConnectionManager(new ConnectionManager.OnMessageReceivedListener() {
            @Override
            public void onReceive(Message msg) {
                switch (msg.what) {
                    case MSG_STOP:
                        if (d != null)
                            d.cancel();
                        break;
                }
            }
        }));
        bound = bindService(new Intent(this, StoryDownload.class), getConnectionManager().getConnection(),
                Context.BIND_AUTO_CREATE);


        makeThemeReceiver();
    }

    private void makeThemeReceiver() {
        themeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    recreate();
                } else {
                    finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.broadcast_theme));
        registerReceiver(themeReceiver, filter);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onDestroy() {
        super.onDestroy();
        if (isBound()) {
            if (getConnectionManager().getService() != null) {
                try {
                    Message msg = Message.obtain(null, MSG_UNREGISTER_CLIENT);
                    msg.replyTo = getConnectionManager().getMessenger();
                    getConnectionManager().send(msg);
                } catch (RemoteException ignore) {
                }
            }
            unbindService(getConnectionManager().getConnection());
            bound = false;
        }
        unregisterReceiver(themeReceiver);
    }


    @Override
    public void startDownloadService(Intent i) {
        Bundle b = i.getExtras();
        int id = b.getInt("id");
        String site = b.getString("site");
        Message msg = Message.obtain(null, MSG_STOP);
        Bundle bundle = new Bundle();
        bundle.putString("file", site + "_" + id);
        msg.setData(bundle);
        msg.replyTo = getConnectionManager().getMessenger();
        try {
            getConnectionManager().send(msg);
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        d = ProgressDialog.show(this, getString(R.string.downloading), getString(R.string.geting_info), true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // TODO: properly support background downloads on O+ devices by starting foreground
            // service.
            startService(i);
        } else {
            startService(i);
        }
    }

    public void openFragment(Fragment fragment, Bundle b) {
        openFragment(fragment, b, true);
    }

    public void openFragment(Fragment fragment, Bundle b, boolean keepHistory) {
        FrameLayout layout = (FrameLayout) findViewById(R.id.fragment_bigger);
        int id;
        if (layout != null) {
            id = R.id.fragment_bigger;
            b.putBoolean("dual", true);
        } else {
            id = R.id.fragment_container;
        }
        fragment.setArguments(b);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id, fragment);
        if (keepHistory)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void openTab(Fragment fragment, FragmentTransaction ft) {
        //Nothing do do here. We have no tabs
    }

    @Override
    public void setContent() {
        //No tabs top create
    }

    public ConnectionManager getConnectionManager() {
        return getManager();
    }

    public void setConnectionManager(ConnectionManager manager) {
        this.setManager(manager);
    }

    public boolean isBound() {
        return bound;
    }

    public ConnectionManager getManager() {
        return manager;
    }

    public void setManager(ConnectionManager manager) {
        this.manager = manager;
    }
}