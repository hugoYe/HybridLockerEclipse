package com.cooeeui.nanolock;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cooeeui.lock.core.api.LockViewManager;
import com.cooeeui.lock.nanolock.R;

public class LockActivity extends Activity {

    private Button mStopLock;
    private Button mNotificationAccess;
    private LockViewManager mLockViewManager;

    private TextView txtView;
    private NotificationReceiver nReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mStopLock = (Button) findViewById(R.id.btn_stopLock);
        mStopLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLockViewManager.destoryLock(getApplicationContext());
                finish();
            }
        });

        mNotificationAccess = (Button) findViewById(R.id.bt_entry_notification_access);
        mNotificationAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });

        mLockViewManager = LockViewManager.getInstance();
        mLockViewManager.launchLock(this);

        txtView = (TextView) findViewById(R.id.textView);
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    private int count = 0;

    public void buttonClicked(View v) {

        if (v.getId() == R.id.btnCreateNotify) {

            NotificationManager
                nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
            ncomp.setContentTitle("My Notification");
            count++;
            ncomp.setContentText("Notification Listener Service Example" + count);
            ncomp.setTicker("Notification Listener Service Example " + count);
            ncomp.setSmallIcon(R.mipmap.ic_launcher);
            ncomp.setAutoCancel(true);
            nManager.notify((int) System.currentTimeMillis(), ncomp.build());

        } else if (v.getId() == R.id.btnClearNotify) {

            Intent i = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command", "clearall");
            sendBroadcast(i);

        } else if (v.getId() == R.id.btnListNotify) {
            Intent i = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command", "list");
            sendBroadcast(i);
        }
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            txtView.setText(temp);
        }
    }


}
