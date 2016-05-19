package com.cooeeui.lock.core.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class DaemonService extends Service {

    public DaemonService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(410401, new Notification());
        stopForeground(true);

        stopSelf();

        return START_STICKY;
    }

    public static void startDaemon(Context paramContext) {
        Intent localIntent = new Intent(paramContext, DaemonService.class);
        paramContext.startService(localIntent);
    }
}
