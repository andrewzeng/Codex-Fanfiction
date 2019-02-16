package com.qan.fiction.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


public class ForwardActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global app = (Global) getApplication();
        if (app.getRunning() > 0) {
            //The app is alive, we just need to broadcast and restart
            //Worst case, memory problems cause onDestroy() not to be called, so this ends up always being run,
            // so nothing happens
            Intent intent = getIntent();
            Intent forwardIntent = new Intent(intent.getStringExtra("action"));
            forwardIntent.putExtras(intent);
            forwardIntent.removeExtra("action");
            sendOrderedBroadcast(forwardIntent, null);
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            Bundle extras = getIntent().getExtras();
            if (Build.VERSION.SDK_INT >= 11 || extras != null && extras.containsKey("cancel"))
                intent.putExtras(getIntent().getExtras());
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        }
    }
}