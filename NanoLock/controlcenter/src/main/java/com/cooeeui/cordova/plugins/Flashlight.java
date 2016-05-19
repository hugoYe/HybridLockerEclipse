package com.cooeeui.cordova.plugins;

import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class Flashlight extends CordovaPlugin {

    private static final String TAG = "FlashlightPlugin";

    private static final String ACTION_AVAILABLE = "available";
    private static final String ACTION_SWITCH_ON = "switchOn";
    private static final String ACTION_SWITCH_OFF = "switchOff";

    private CallbackContext mCallbackContext;
    private static Boolean capable;
    private boolean releasing;
    private Camera mCamera;
    private boolean mIsOn;

    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "Plugin Called: " + action);
        mCallbackContext = callbackContext;
        try {
            if (action.equals(ACTION_SWITCH_ON)) {
                // When switching on immediately after checking for isAvailable,
                // the release method may still be running, so wait a bit.
                while (releasing) {
                    Thread.sleep(10);
                }
                mCamera = Camera.open();
                if (Build.VERSION.SDK_INT >= 11) { // honeycomb
                    // required for (at least) the Nexus 5
                    mCamera.setPreviewTexture(new SurfaceTexture(0));
                }
                switchOn();

                // 内置锁屏方案 begin
//                if (this.cordova.getActivity() != null) {
//                    startFlashlightService(this.cordova.getActivity(), true);
//                } else if (this.cordova.getContext() != null) {
//                    startFlashlightService(this.cordova.getContext(), true);
//                }
                // 内置锁屏方案 end

                return true;
            } else if (action.equals(ACTION_SWITCH_OFF)) {
                switchOff();
                // 内置锁屏方案 begin
//                if (this.cordova.getActivity() != null) {
//                    startFlashlightService(this.cordova.getActivity(), false);
//                } else if (this.cordova.getContext() != null) {
//                    startFlashlightService(this.cordova.getContext(), false);
//                }
                // 内置锁屏方案 end
                return true;
            } else if (action.equals(ACTION_AVAILABLE)) {
                if (capable == null) {
                    mCamera = Camera.open();
                    capable = isCapable();
                    releaseCamera();
                }
                callbackContext.success(capable ? 1 : 0);
                return true;
            } else {
                callbackContext.error("flashlight." + action
                                      + " is not a supported function.");
                return false;
            }
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsOn) {
            switchOff();
        }
    }

    private void switchOn() {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                toggleTorch(true, mCallbackContext);
            }
        });

    }

    private void switchOff() {
        mIsOn = false;
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                toggleTorch(false, mCallbackContext);
                releaseCamera();
            }
        });
    }


    private void startFlashlightService(Context context, boolean b) {
        Intent intent = new Intent();
        intent.putExtra("open_flash", b);
        intent.setClassName(context.getPackageName(),
                            "com.cooee.control.center.module.base.FlashlightService");
        context.startService(intent);
    }


    private boolean isCapable() {
        PackageManager packageManager = null;

        if (this.cordova.getActivity() != null) {
            packageManager = this.cordova.getActivity().getPackageManager();
        } else if (this.cordova.getContext() != null) {
            packageManager = this.cordova.getContext().getPackageManager();
        }

        for (final FeatureInfo feature : packageManager
            .getSystemAvailableFeatures()) {
            if (PackageManager.FEATURE_CAMERA_FLASH
                .equalsIgnoreCase(feature.name)) {
                return true;
            }
        }
        return false;
    }


    private void toggleTorch(final boolean switchOn, final CallbackContext callbackContext) {

        final Camera.Parameters mParameters = mCamera.getParameters();
        if (isCapable()) {
            mParameters.setFlashMode(switchOn ? Camera.Parameters.FLASH_MODE_TORCH
                                              : Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
            mCamera.startPreview();
            callbackContext.success();
            if (switchOn) {
                mIsOn = true;
            }
        } else {
            callbackContext.error(
                "Device is not capable of using the flashlight. Please test with flashlight.available()");
        }

    }


    private void releaseCamera() {
        releasing = true;
        // we need to release the camera, so other apps can use it
//        new Thread(new Runnable() {
//            public void run() {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        releasing = false;
//            }
//        }).start();
    }
}
