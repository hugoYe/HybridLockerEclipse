package com.cooeeui.lock.core.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.cooeeui.lock.nanolock.R;


public class LockService extends Service {

    private final String TAG = LockService.class.getSimpleName();


    private final int WINDOW_FLAG = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                    | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;

    private final int WINDOW_FULL_SCREEN_FLAG = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                                                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                                                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

    // 3847
    private final int SYSTEM_UI_FLAG1 = View.SYSTEM_UI_FLAG_LOW_PROFILE
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE;

    // 5383
    private final int SYSTEM_UI_FLAG2 = View.SYSTEM_UI_FLAG_LOW_PROFILE
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;
    private Context mContext;
    private LockBroadcastReceiver mReceiver;
    private View mLockView;
    private LayoutInflater mInflater;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLockView != null) {
                mLockView.setSystemUiVisibility(SYSTEM_UI_FLAG2);
            }
        }
    };

    public static void stopService(Context context) {
        context.stopService(new Intent(context, LockService.class));
    }

    public LockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);

            mReceiver = new LockBroadcastReceiver();
            registerReceiver(mReceiver, filter);
        }

        createFloatWindow();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mLockView != null) {
            mWindowManager.removeViewImmediate(mLockView);
            mLockView = null;
        }
        System.gc();
    }


    private void createFloatWindow() {

        mInflater = LayoutInflater.from(this);
        mLockView = mInflater.inflate(R.layout.lockview, null);
        if (mLockView != null) {
            initWmParams();

            mLockView.setSystemUiVisibility(SYSTEM_UI_FLAG2);
            mLockView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        mLockView.setSystemUiVisibility(SYSTEM_UI_FLAG1);
                        mHandler.postDelayed(mRunnable, 100l);
                    }
                });

            mWindowManager.addView(mLockView, wmParams);
        } else {
            stopSelf();
            throw new RuntimeException("" + TAG + ", Lock view is null ! Please setup lock view !");
        }
    }

    private void initWmParams() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        wmParams = new WindowManager.LayoutParams();
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        if (Build.VERSION.SDK_INT >= 23) {  // android 6.0
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
//        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;// 华为的样机采用这个类型可以遮盖掉虚拟导航栏,这是全屏显示模式，所以需要配套WINDOW_FULL_SCREEN_FLAG
//        wmParams.flags = WINDOW_FULL_SCREEN_FLAG;
        wmParams.flags = WINDOW_FLAG;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.windowAnimations = 2131165496;
        wmParams.screenOrientation = 1;
    }


    class LockBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                if (mLockView != null) {
                    mLockView.setSystemUiVisibility(SYSTEM_UI_FLAG1);
                }
                mHandler.postDelayed(mRunnable, 100l);
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                if (mLockView != null) {
                    mLockView.setSystemUiVisibility(SYSTEM_UI_FLAG1);
                }
                mHandler.postDelayed(mRunnable, 100l);
            } else if (action.equals(Intent.ACTION_USER_PRESENT)) {

            }

        }
    }
}
