package com.cooeeui.lock.core.api;

import android.content.Context;
import android.content.Intent;

import com.cooeeui.lock.core.services.LockService;
import com.cooeeui.lock.core.services.MonitorService;


/**
 * Singleton
 */
public class LockViewManager {

    private static final String TAG = LockViewManager.class.getSimpleName();

    private static LockViewManager mInstance;

    private LockViewManager() {
    }

    public static LockViewManager getInstance() {

        if (mInstance == null) {
            mInstance = new LockViewManager();
        }

        return mInstance;
    }

    public void launchLock(Context context) {
        context.startService(new Intent(context, MonitorService.class));
    }

    public void unlock(Context context) {
        LockService.stopService(context);
    }

    public void destoryLock(Context context) {
        MonitorService.stopService(context);
    }

}
