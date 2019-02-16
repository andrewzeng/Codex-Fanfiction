package com.qan.fiction.ui.service_connection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import static com.qan.fiction.util.download.StoryDownload.MSG_REGISTER_CLIENT;

public class ConnectionManager {

    private Messenger service;
    private Messenger messenger;
    private ServiceConnection connection;

    public void send(Message msg) throws RemoteException {
        service.send(msg);
    }

    public static interface OnMessageReceivedListener {
        public void onReceive(Message msg);
    }


    public ConnectionManager(OnMessageReceivedListener listener) {
        messenger = new Messenger(new IncomingHandler(listener));
        connection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                ConnectionManager.this.service = new Messenger(service);
                try {
                    Message msg = Message.obtain(null, MSG_REGISTER_CLIENT);
                    msg.replyTo = messenger;
                    ConnectionManager.this.service.send(msg);
                } catch (RemoteException ignore) {
                }
            }

            public void onServiceDisconnected(ComponentName className) {
                service = null;
            }
        };
    }


    public ServiceConnection getConnection() {
        return connection;
    }

    public Messenger getService() {
        return service;
    }

    public Messenger getMessenger() {
        return messenger;
    }


}
