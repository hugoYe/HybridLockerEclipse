package com.cooeeui.lock.core.services;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

public class MonitorService extends Service {

    private static final String TAG = MonitorService.class.getSimpleName();

    private Context mContext;
    private MonitorBroadcastReceiver mReceiver;
 
    private KeyguardManager mKeyguardManager;
    private KeyguardManager.KeyguardLock mKeyguardLock;
    private final Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mKeyguardLock == null) {
                mKeyguardLock = mKeyguardManager.newKeyguardLock(getPackageName());
            }
            mKeyguardLock.disableKeyguard();
        }
    };

    public static void stopService(Context context) {
        LockService.stopService(context);
        context.stopService(new Intent(context, MonitorService.class));
    }

    public MonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // 使该服务成为前台进程，这样减少应用在后台被kill的几率
        startForeground(410401, new Notification());
        DaemonService.startDaemon(this);

        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        rennableKeyguard(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;

        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);

            mReceiver = new MonitorBroadcastReceiver();
            registerReceiver(mReceiver, filter);

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        rennableKeyguard(true);
    }

    private void rennableKeyguard(boolean able) {
        if (able) {
            if (mKeyguardLock != null) {
                mHandler.removeCallbacks(mRunnable);
                mKeyguardLock.reenableKeyguard();
                mKeyguardLock = null;
            }
        } else {
            mHandler.postDelayed(mRunnable, 300);
        }
    }


    class MonitorBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
//                System.gc();
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                rennableKeyguard(false);
                Intent it = new Intent();
                it.setClass(mContext, LockService.class);
                mContext.startService(it);
            } else if (action.equals(Intent.ACTION_USER_PRESENT)) {

            }
        }
    }
}
