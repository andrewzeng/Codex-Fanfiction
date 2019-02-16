package com.qan.fiction.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qan.fiction.R;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AlertDialog;
import com.qan.fiction.ui.fragment.LibraryFragment;
import com.qan.fiction.ui.fragment.MainFragment;
import com.qan.fiction.ui.fragment.ReaderFragment;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.misc.listeners.ViewListener;
import com.qan.fiction.util.storage.entries.Entry;
import com.qan.fiction.util.web.Web;

import static com.qan.fiction.util.download.StoryDownload.MSG_STOP;
import static com.qan.fiction.util.download.StoryDownload.MSG_UNREGISTER_CLIENT;

public class MainActivity extends BaseActivity
        implements ViewListener, LibraryFragment.LibraryFragmentListener,
        ReaderFragment.ReaderFragmentListener {

    private boolean bound;
    private BroadcastReceiver intentReceiver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceivers();//Need
        if (savedInstanceState == null) {
            if (findViewById(R.id.fragment_container) != null) {
                MainFragment firstFragment = new MainFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, firstFragment).commit();

            } else {
                MainFragment firstFragment = new MainFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_bigger, firstFragment).commit();
            }

            onNewIntent(getIntent());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void registerReceivers() {
        intentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null)
                    onNewIntent(intent);
                abortBroadcast();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.broadcast_action));
        filter.setPriority(1);
        registerReceiver(intentReceiver, filter);
        filter = new IntentFilter();
        filter.addAction(getString(R.string.broadcast_theme));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Bundle args = intent.getExtras();
        if (args == null)
            return;
        if (args.containsKey("cancel")) {
            cancelDialog(new Bundle(args));
            args.remove("cancel");
            args.remove("file");
        } else if (args.containsKey("file")) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            openFragment(new LibraryFragment(), new Bundle());
            openFragment(new ReaderFragment(), new Bundle(args));
            args.remove("file");
        }
    }

    public void cancelDialog(final Bundle arg) {
        final AlertBuilder builder = new AlertBuilder(this);
        builder.setTitle(R.string.download);
        if (Settings.isLightTheme(this))
            builder.setIcon(R.drawable.down);
        else
            builder.setIcon(R.drawable.down_dark);
        TextView r = new TextView(this);
        r.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        r.setText("Cancel download of " + arg.getString("title") + "?");
        r.setPadding(15, 15, 15, 15);
        builder.setView(r);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (getConnectionManager().getService() != null) {
                    try {
                        Message msg = Message.obtain(null, MSG_STOP);
                        Bundle bundle = new Bundle();
                        bundle.putString("file", arg.getString("file"));
                        msg.setData(bundle);
                        msg.replyTo = getManager().getMessenger();
                        getManager().send(msg);
                    } catch (RemoteException ignore) {
                    }
                }
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        AlertDialog d = builder.create();
        d.show();
    }

    public void onDestroy() {
        super.onDestroy();
        if (bound) {
            if (getManager().getService() != null) {
                try {
                    Message msg = Message.obtain(null, MSG_UNREGISTER_CLIENT);
                    msg.replyTo = getManager().getMessenger();
                    getManager().send(msg);
                } catch (RemoteException ignore) {
                }
            }
            unbindService(getManager().getConnection());
            bound = false;
        }
        unregisterReceiver(intentReceiver);
        intentReceiver = null;
    }

    public void onLibraryRead(Entry e) {
        final Bundle b = new Bundle();
        b.putString("file", e.file);
        openFragment(new ReaderFragment(), b);
    }

    @Override
    public void onOnlineRead(String site, String file) {
        FrameLayout layout = (FrameLayout) findViewById(R.id.fragment_bigger);
        String id = Entry.getId(file, site);
        String url;
        if (layout != null) {
            url = Constants.normalView.get(site).replaceFirst("\\?", id);
            url = modify(this, url, site, file);
        } else {
            url = Constants.mobileView.get(site).replaceFirst("\\?", id);
            url = modify(this, url, site, file);
        }

        Intent i = Web.web_intent(url);
        startActivity(i);

    }

    public static String modify(Context c, String url, String site, String file) {
        int page = Settings.getPage(c, file);
        if (site.equals(Constants.FF_NET_S) || site.equals(Constants.FP_COM_S))
            return url.substring(0, url.length() - 2) + page;
        else
            return url;
    }


    @Override
    public void openTab(Fragment fragment, FragmentTransaction ft) {
        //Nothing do do here. We have no tabs
    }

    @Override
    public void setContent() {
        //No tabs top create
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    return true;
                } else
                    return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onReview(Entry e) {
    }

}