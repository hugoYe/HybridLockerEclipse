package com.cooeeui.cordova.plugins.mobiledata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.channels.NonWritableChannelException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cooeeui.cordova.plugins.UnlockListener;

/**
 * Created by Hugo.ye on 2015/10/29.
 */
public class MobileDataWizard extends CordovaPlugin {

    private static final String TAG = "MobileDataWizard";

    private static final String ACTION_IS_MOBILE_DATA_ENABLED = "isMobileDataEnabled";
    private static final String ACTION_SET_MOBILE_DATA_ENABLED = "setMobileDataEnabled";
    private static final String ACTION_ENTRY_MOBILE_DATA_SETTINGS = "entryMobileDataSettings";

    private Context mContext;
    private ConnectivityManager mConnectivityManager;

    private static UnlockListener sUnlockListener;

    public static void setOnUnlockListener(UnlockListener unlockListener) {
        sUnlockListener = unlockListener;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.e(TAG, "action = " + action);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

                try {
                    Method getMethod = mConnectivityManager.getClass()
                        .getMethod("getMobileDataEnabled");
                    getMethod.setAccessible(true);
                    boolean isEnabled = (Boolean) getMethod
                        .invoke(mConnectivityManager);
                    // sendJS("javascript:onMobileDataStateChanged(isEnabled);");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // 对于没有SIM卡的手机做相应的处理
                    TelephonyInfo telephonyInfo = TelephonyInfo
                        .getInstance(mContext);
                    if (telephonyInfo.isDualSIM()) {
                        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
                        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
                        if (isSIM1Ready || isSIM2Ready) {
                            return;
                        } else {
                            // sendJS("javascript:onMobileDataStateChanged(true);");
                        }
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        if (cordova.getActivity() != null) {
            this.mContext = cordova.getActivity();
        } else if (cordova.getContext() != null) {
            this.mContext = cordova.getContext();
        }

        this.mConnectivityManager = (ConnectivityManager) mContext
            .getSystemService(Context.CONNECTIVITY_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        filter.addAction("android.intent.action.ANY_DATA_STATE");
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.setPriority(1000);
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_IS_MOBILE_DATA_ENABLED)) {
            this.isMobileDataEnabled(callbackContext);
            return true;
        } else if (action.equals(ACTION_SET_MOBILE_DATA_ENABLED)) {
            this.setMobileDataEnabled(args);
            return true;
        } else if (action.equals(ACTION_ENTRY_MOBILE_DATA_SETTINGS)) {
            if (cordova.getActivity() != null) {
                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entryMobileDataSettings();
                    }
                });
            } else if (cordova.getCordovaWrap() != null) {
                cordova.getCordovaWrap().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entryMobileDataSettings();
                    }
                });
            }
            return true;
        }

        return false;
    }

    public boolean isMobileDataEnabled(CallbackContext callbackContext) {
        boolean isEnabled = false;
        try {
            // 对于没有SIM卡的手机做相应的处理
            TelephonyManager tm = null;
            if (mContext != null) {
                tm = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            }
            if (tm == null
                || TelephonyManager.SIM_STATE_UNKNOWN == tm.getSimState()
                || tm.getNetworkOperatorName().equals("")
                || tm.getNetworkType() == 0) {
                callbackContext.success(isEnabled ? "1" : "0");
                return isEnabled;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            Method getMethod = mConnectivityManager.getClass().getMethod(
                "getMobileDataEnabled");
            getMethod.setAccessible(true);
            isEnabled = (Boolean) getMethod.invoke(mConnectivityManager);
            callbackContext.success(isEnabled ? "1" : "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isEnabled;
    }

    public void entryMobileDataSettings() {
        // 对于5.0以上的点击mobile 直接进入移动数据统计界面
        if (Build.VERSION.SDK_INT > 20) {
            try {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.android.settings",
                                                      "com.android.settings.Settings$DataUsageSummaryActivity"));
                intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mContext != null) {
                    mContext.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mContext != null) {
                    mContext.startActivity(new Intent(
                        Settings.ACTION_DATA_ROAMING_SETTINGS));
                }
            }

            if (sUnlockListener != null) {
                sUnlockListener.onUnlock();
            }
        }

    }

    /**
     * js中设置数据流量的开关
     *
     * @param state [boolean] (args.getString(0)) : 数据流量开关状态
     */
    public void setMobileDataEnabled(JSONArray args) {
        if (mConnectivityManager == null) {
            return;
        }
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(mContext);
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
        if (!(isSIM1Ready || isSIM2Ready)) {
            return;
        }

        if (Build.VERSION.SDK_INT > 20) {
            if (cordova.getActivity() != null) {
                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entryMobileDataSettings();
                    }
                });
            } else if (cordova.getCordovaWrap() != null) {
                cordova.getCordovaWrap().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entryMobileDataSettings();
                    }
                });
            }

            return;
        }

        // 对于5.0以下的点击mobile 直接设置移动数据
        try {

            boolean state = args.getBoolean(0);

            final Class conmanClass = Class.forName(mConnectivityManager
                                                        .getClass().getName());
            final Field iConnectivityManagerField = conmanClass
                .getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField
                .get(mConnectivityManager);
            final Class iConnectivityManagerClass = Class
                .forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendJS(final String js) {
        if (cordova.getActivity() != null) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(js);
                }
            });
        } else if (cordova.getCordovaWrap() != null) {
            cordova.getCordovaWrap().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(js);
                }
            });
        }

    }

}
