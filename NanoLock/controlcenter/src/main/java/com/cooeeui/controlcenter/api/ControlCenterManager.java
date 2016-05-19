package com.cooeeui.controlcenter.api;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.cooeeui.cordova.plugins.AppsApi;
import com.cooeeui.cordova.plugins.BluetoothStatus;
import com.cooeeui.cordova.plugins.UnlockListener;
import com.cooeeui.cordova.plugins.WifiWizard;
import com.cooeeui.cordova.plugins.camera.CameraLauncher;
import com.cooeeui.cordova.plugins.mobiledata.MobileDataWizard;
import com.cooeeui.core.plugin.ApkPlugin;
import com.cooeeui.core.plugin.JarPlugin;
import com.cooeeui.favorites.api.FavoritesApi;

import org.apache.cordova.CordovaWrap;


public final class ControlCenterManager implements UnlockListener {


    private CordovaWrap mCordovaWrap;
    private WebView mWebView;
    private UnlockListener mUnlockListener;
    private Context mContext;


    public ControlCenterManager(Context context) {
        mContext = context;

        AppsApi.setOnUnlockListener(this);
        CameraLauncher.setOnUnlockListener(this);
        BluetoothStatus.setOnUnlockListener(this);
        WifiWizard.setOnUnlockListener(this);
        MobileDataWizard.setOnUnlockListener(this);
        ApkPlugin.setOnUnlockListener(this);
        JarPlugin.setOnUnlockListener(this);

        FavoritesApi.getInstance().init(context);
    }

    public void setUnlockListener(UnlockListener listener) {
        mUnlockListener = listener;
    }

    public View getControlCenter() {
        if (mCordovaWrap == null) {
            mCordovaWrap = new CordovaWrap(mContext, null);
            mCordovaWrap.onCreate(null);
        }
        if (mWebView == null) {
            mWebView = (WebView) mCordovaWrap.loadWebViewUrl(mCordovaWrap.launchUrl);
        }

        return mWebView;
    }

    public void onResume() {
        if (mCordovaWrap != null) {
            mCordovaWrap.onResume();
            mCordovaWrap = null;
        }
    }

    public void onPause() {
        if (mCordovaWrap != null) {
            mCordovaWrap.onPause();
            mCordovaWrap = null;
        }
    }

    public void onDestroy() {
        if (mCordovaWrap != null) {
            mCordovaWrap.onDestroy();
            mCordovaWrap = null;
        }
    }


    @Override
    public void onUnlock() {
        if (mUnlockListener != null) {
            mUnlockListener.onUnlock();
        }
    }
}
