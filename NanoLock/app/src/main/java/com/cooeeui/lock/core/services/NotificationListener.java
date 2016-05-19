package com.cooeeui.lock.core.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {

    private static final String TAG = NotificationListener.class.getSimpleName();

    private NLServiceReceiver nlservicereciver;

    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "**********  onNotificationPosted");
        Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn
            .getPackageName());
        Intent i = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event", "onNotificationPosted :" + sbn.getPackageName() + "\n");
        sendBroadcast(i);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNOtificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn
            .getPackageName());
        Intent i = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event", "onNotificationRemoved :" + sbn.getPackageName() + "\n");

        sendBroadcast(i);

    }


    class NLServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra("command").equals("clearall")) {
                NotificationListener.this.cancelAllNotifications();

            } else if (intent.getStringExtra("command").equals("list")) {
                Intent i1 = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event", "=====================");
                sendBroadcast(i1);

                int i = 1;
                for (StatusBarNotification sbn : NotificationListener.this
                    .getActiveNotifications()) {
                    Intent i2 = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event", i + " " + sbn.getPackageName() + "\n");
                    Log.e(TAG, "######## sbn.getId() = " + sbn.getId());
                    Log.e(TAG, "######## sbn.getKey() = " + sbn.getKey());
                    Log.e(TAG, "######## sbn.getTag() = " + sbn.getTag());
                    Log.e(TAG, "######## sbn.getPostTime() = " + sbn.getPostTime());
                    Log.e(TAG, "######## sbn.getUser() = " + sbn.getUser());
                    Log.e(TAG, "######## sbn.getNotification() = " + sbn.getNotification());
                    Log.e(TAG, "######## Notification.icon = " + sbn.getNotification().icon);
                    Log.e(TAG, "######## Notification.number = " + sbn.getNotification().number);
                    Log.e(TAG,
                          "######## Notification.tickerText = " + sbn.getNotification().tickerText);
                    Log.e(TAG, "######## Notification.when = " + sbn.getNotification().when);
                    Log.e(TAG, "######## Notification.extras = " + sbn.getNotification().extras);
                    Log.e(TAG, "######## Notification.extras.icon = " + sbn.getNotification().extras
                        .getInt(Notification.EXTRA_SMALL_ICON));
                    Log.e(TAG,
                          "######## Notification.extras.title = " + sbn.getNotification().extras
                              .getString(Notification.EXTRA_TITLE));
                    Log.e(TAG,
                          "######## Notification.extras.text = " + sbn.getNotification().extras
                              .getString(Notification.EXTRA_TEXT));
                    Log.e(TAG,
                          "######## Notification.extras.summaryText = " + sbn
                              .getNotification().extras.getString(Notification.EXTRA_SUMMARY_TEXT));
                    sendBroadcast(i2);
                    i++;
                }

                Intent i3 = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event", "===== Notification List ====");
                sendBroadcast(i3);
            }
        }
    }

}
