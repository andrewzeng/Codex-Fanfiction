package com.qan.fiction.ui.service_connection;

import android.os.Handler;
import android.os.Message;

public class IncomingHandler extends Handler {
    private ConnectionManager.OnMessageReceivedListener listener;

    public IncomingHandler(ConnectionManager.OnMessageReceivedListener listener) {
        this.listener = listener;
    }

    @Override
    public void handleMessage(Message msg) {
        listener.onReceive(msg);
    }
}
