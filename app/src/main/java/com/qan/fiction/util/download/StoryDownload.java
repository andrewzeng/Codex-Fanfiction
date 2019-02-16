package com.qan.fiction.util.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import com.qan.fiction.R;
import com.qan.fiction.ui.activity.ForwardActivity;
import com.qan.fiction.ui.activity.Global;
import com.qan.fiction.util.download.manager.DownloadStrategy;
import com.qan.fiction.util.download.manager.ProgressListener;
import com.qan.fiction.util.download.manager.TransferManager;
import com.qan.fiction.util.download.manager.UpdateStrategy;
import com.qan.fiction.util.storage.DatabaseHandler;
import com.qan.fiction.util.storage.FileFormatter;
import com.qan.fiction.util.storage.entries.Entry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.lang.System.currentTimeMillis;

public class StoryDownload extends Service {
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_RESULT = 3;
    public static final int MSG_STOP = 4;
    public static final int MSG_TOAST = 5;
    /**
     * This is here to stop simultaneous downloads of the same same files
     */
    private HashSet<String> stoppedDownloads = new HashSet<String>();
    private Set<Messenger> clients = new CopyOnWriteArraySet<Messenger>();

    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.
    private DatabaseHandler db;


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    class IncomingHandler extends Handler { // Handler of incoming messages from clients.

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    clients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    clients.remove(msg.replyTo);
                    break;
                case MSG_STOP:
                    String file = msg.getData().getString("file");
                    stoppedDownloads.add(file);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void toast(String message) {
        List<Messenger> remove = new ArrayList<Messenger>();
        for (Messenger client : clients) {
            try {
                Message m = Message.obtain(null, MSG_TOAST);
                Bundle b = new Bundle();
                b.putString("toast", message);
                m.setData(b);
                client.send(m);
            } catch (RemoteException e) {
                remove.add(client);
            }
        }

        clients.removeAll(remove);
    }

    /**
     * Called when the story information retrieval is done, regardless of it it succeeded or not.
     */
    private void stopProgress() {
        List<Messenger> remove = new ArrayList<Messenger>();
        for (Messenger client : clients) {
            try {
                client.send(Message.obtain(null, MSG_STOP));

            } catch (RemoteException e) {
                remove.add(client);
            }
        }
        clients.removeAll(remove);
    }

    private void done() {
        List<Messenger> remove = new ArrayList<Messenger>();
        for (Messenger client : clients) {
            try {
                // Send data as an Integer
                client.send(Message.obtain(null, MSG_RESULT));

            } catch (RemoteException e) {
                remove.add(client);
            }
        }

        clients.removeAll(remove);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = ((Global) getApplication()).getDatabase();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent == null)
            return START_NOT_STICKY;
        final int id = intent.getExtras().getInt("id");
        final String site = intent.getExtras().getString("site");
        // TODO: properly stop the service, as currently it kind of just runs on forever pre-Oreo
        if (intent.getExtras().containsKey("download")) {
            new Thread() {
                public void run() {
                    Looper.prepare();
                    download(site, id);
                }
            }.start();
        } else if (intent.getExtras().containsKey("update")) {
            final int chapters = db.getStory(FileFormatter.formatFilePrefix(site, id)).chapters;
            new Thread() {
                public void run() {
                    Looper.prepare();
                    update(site, id, chapters);
                }
            }.start();
        } else if (intent.getExtras().containsKey("redownload")) {
            new Thread() {
                public void run() {
                    Looper.prepare();
                    redownload(id, site);
                }
            }.start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void redownload(int id, String site) {
        download(site, id, false);
    }

    private void download(String site, int id) {
        download(site, id, true);
    }


    /**
     * Downloads the given story ID from the given site.
     */
    public void download(String site, int id, boolean isNewDownload) {
        final TransferManager manager = new TransferManager(site, id);
        DatabaseHandler handler = new DatabaseHandler(this);
        final DownloadStrategy downloadManager = new DownloadStrategy(this, handler, manager, isNewDownload);
        ProgressListener downloadListener = new ProgressListener() {
            @Override
            public void onInfoRetrieved(boolean successful) {
                stopProgress();
            }

            @Override
            public void onFailure(String message) {
                Entry e = manager.getStoryInfo();
                toast(message);
                if (e != null)
                    showNotification(currentTimeMillis(), 0, -1, e.title, null, message);
                else
                    showNotification(currentTimeMillis(), 0, -1, getString(R.string.unknown_title), null, message);
                stopProgress();
            }

            @Override
            public void onComplete(String message) {
                Entry e = manager.getStoryInfo();
                showNotification(currentTimeMillis(), e.chapters + 1, e.chapters + 1,
                        e.title, e.file, message);
                done();
            }

            @Override
            public void onStopped(String message) {
                Entry e = manager.getStoryInfo();
                toast(message);
                showNotification(currentTimeMillis(), 0, -1, e.title, null, message);
            }

            @Override
            public void onChapterDownloadCompleted(int chapter, boolean successful) {
                if (successful) {
                    Entry e = manager.getStoryInfo();
                    showNotification(System.currentTimeMillis(), e.chapters + 1, chapter, e.title,
                            e.file, chapter + " of " + e.chapters + " downloaded.");
                }
            }
        };

        downloadManager.registerDownloadListener(downloadListener);
        downloadManager.startTransfer(stoppedDownloads);
        handler.close();

    }

    private void update(String site, int id, final int chapters) {
        final TransferManager manager = new TransferManager(site, id);

        DatabaseHandler handler = new DatabaseHandler(this);
        final UpdateStrategy updateManager = new UpdateStrategy(this, handler, manager, chapters);
        ProgressListener updateListener = new ProgressListener() {
            @Override
            public void onInfoRetrieved(boolean successful) {
                if (successful) {
                    Entry e = manager.getStoryInfo();
                    if (e.chapters != chapters)
                        toast(getString(R.string.updates_found));
                }
                stopProgress();
            }

            @Override
            public void onFailure(String message) {
                Entry e = manager.getStoryInfo();
                toast(message);
                String title = e == null ? getString(R.string.unknown_title) : e.title;
                showNotification(currentTimeMillis(), 0, -1, title, null, message);
            }

            @Override
            public void onComplete(String message) {
                Entry e = manager.getStoryInfo();
                toast(updateManager.getCompletionMessage(e));
                showNotification(currentTimeMillis(), e.chapters + 1, e.chapters + 1,
                        e.title, e.file, message);
                done();
            }

            @Override
            public void onStopped(String message) {
                Entry e = manager.getStoryInfo();
                toast(message);
                showNotification(currentTimeMillis(), 0, -1, e.title, null, message);
            }

            @Override
            public void onChapterDownloadCompleted(int chapter, boolean successful) {
                if (successful) {
                    Entry e = manager.getStoryInfo();
                    showNotification(System.currentTimeMillis(), e.chapters + 1 - chapters, chapter - chapters, e.title,
                            e.file, (chapter - chapters) + " of " + (e.chapters - chapters) + " updated.");
                }
            }
        };

        updateManager.registerDownloadListener(updateListener);
        updateManager.startTransfer(stoppedDownloads);
        handler.close();


    }


    private void showNotification(long start, int chapters, int cur, String title, String file, String content) {
        NotificationCompat.Builder builder;

        if (cur == -1) {
            builder = getBuilder(start, title, content);
            builder.setAutoCancel(true);
            notify(title, builder, new Bundle());
        } else if (cur < chapters) {
            builder = getBuilder(start, title, content);
            builder.setProgress(chapters, cur, false);
            Bundle res;
            res = new Bundle();
            res.putBoolean("cancel", true);
            res.putString("file", file);
            res.putString("title", title);
            notify(title, builder, res);
        } else {
            builder = getBuilder(start, title, content);
            builder.setAutoCancel(true);
            Bundle res;
            res = new Bundle();
            res.putString("file", file);
            notify(title, builder, res);
        }
    }

    private void notify(String title, NotificationCompat.Builder builder, Bundle extras) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        Intent intent = new Intent(this, ForwardActivity.class);
        intent.putExtra("action", getString(R.string.broadcast_action));
        intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        stackBuilder.addNextIntent(intent);
        PendingIntent pending = PendingIntent.getActivity(this, title.hashCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pending);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("download_channel", getString(R.string.download_channel), NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);
        }
        manager.notify(title.hashCode(), builder.build());

    }

    private NotificationCompat.Builder getBuilder(long start, String title, String contentText) {
        return new NotificationCompat.Builder(this, "download_channel")
                .setContentTitle(Html.fromHtml(title))
                .setSmallIcon(R.drawable.down_dark)
                .setContentText(contentText)
                .setWhen(start);
    }


}