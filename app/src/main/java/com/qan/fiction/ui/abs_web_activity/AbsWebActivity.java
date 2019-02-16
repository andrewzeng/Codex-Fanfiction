package com.qan.fiction.ui.abs_web_activity;

import android.content.*;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.TextView;
import com.qan.fiction.R;
import com.qan.fiction.custom.AlertBuilder;
import com.qan.fiction.custom.AlertDialog;
import com.qan.fiction.ui.activity.CoreActivity;
import com.qan.fiction.ui.fragment.ReaderFragment;
import com.qan.fiction.util.constants.Settings;
import com.qan.fiction.util.misc.listeners.ViewListener;

import static com.qan.fiction.ui.fragment.ReaderFragment.ReaderFragmentListener;
import static com.qan.fiction.util.download.StoryDownload.MSG_STOP;


public abstract class AbsWebActivity extends CoreActivity implements ViewListener, ReaderFragmentListener {

    private BroadcastReceiver notificationReceiver;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        registerReceiver();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (saved == null) {
            setContent();
            onNewIntent(getIntent());
        }

    }


    private void registerReceiver() {
        notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null)
                    onNewIntent(intent);
                abortBroadcast();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.broadcast_action));
        filter.setPriority(2);
        registerReceiver(notificationReceiver, filter);
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
            openFragment(new ReaderFragment(), new Bundle(args));
            args.remove("file");
        }
    }


    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    return true;
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void openFragment(Fragment fragment, Bundle b) {
        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        openFragment(fragment, b, true);
    }

    @Override
    public void openTab(Fragment fragment, FragmentTransaction ft) {
        openTab(fragment, ft, new Bundle());
    }

    public void openTab(Fragment fragment, FragmentTransaction ft, Bundle b) {
        android.app.FragmentManager fm = getFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (findViewById(R.id.fragment_container) != null) {
            fragment.setArguments(b);
            ft.replace(R.id.fragment_container, fragment);
        } else {
            fragment.setArguments(b);
            b.putBoolean("dual", true);
            ft.replace(R.id.fragment_bigger, fragment);
        }
    }


    protected void addTab(String s, ActionBar.TabListener t) {
        ActionBar bar = getSupportActionBar();
        ActionBar.Tab tab = bar.newTab().setText(s).setTabListener(t);
        bar.addTab(tab);
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
                        msg.replyTo = getConnectionManager().getMessenger();
                        getConnectionManager().send(msg);
                    } catch (RemoteException ignore) {
                    }
                }
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        AlertDialog d = builder.create();
        d.show();
    }


    public abstract void setContent();

}