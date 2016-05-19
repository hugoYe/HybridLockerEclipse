package com.cooeeui.core.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final public class ApkPluginProxyManager implements IPluginProxy {

    private final String TAG = "ApkPluginProxyManager";

    public static final String PLUGINS_PACKAGE_NAME = "com.cooeeui.lock.apk.plugins";
    private final String PLUGINS_CLASS_PROXY_NAME = "com.cooeelock.core.PluginProxy";

    private static ApkPluginProxyManager instance;

    private boolean mLoadSuccessed;
    private Context mRemoteContext;

    private Object obj;
    private Context mContext;
    private Class<?> mProxyClass;
    private Constructor<?> mProxyConstructor;
    private Method methodSetLockAuthority;
    private Method methodExecute;
    private Method methodOnPause;
    private Method methodOnResume;
    private Method methodOnStart;
    private Method methodOnStop;
    private Method methodOnNewIntent;
    private Method methodOnDestroy;
    private Method methodOnLauncherLoadFinish;
    private Method methodOnActivityResult;
    private Method methodShouldAllowRequest;
    private Method methodShouldAllowNavigation;
    private Method methodShouldOpenExternalUrl;
    private Method methodOnOverrideUrlLoading;
    private Method methodRemapUri;
    private Method methodOnConfigurationChanged;
    private Method methodOnPageFinishedLoading;

    public static ApkPluginProxyManager getInstance() {
        if (instance == null) {
            synchronized (ApkPluginProxyManager.class) {
                if (instance == null) {
                    instance = new ApkPluginProxyManager();
                }
            }
        }
        return instance;
    }

    public ApkPluginProxyManager() {
    }

    private boolean loadContext(Context remoteContext) {
        try {
            mContext = remoteContext.createPackageContext(PLUGINS_PACKAGE_NAME,
                                                          Context.CONTEXT_INCLUDE_CODE
                                                          | Context.CONTEXT_IGNORE_SECURITY);
            Log.e(TAG, "######## loadContext success , mContext = " + mContext);
            return true;
        } catch (NameNotFoundException e) {
            Log.e(TAG, "######## loadContext error = " + e);
            e.printStackTrace();
            return false;
        }
    }

    private boolean loadClassType() {
        try {
            mProxyClass = Class.forName(PLUGINS_CLASS_PROXY_NAME, true,
                                        mContext.getClassLoader());
            mProxyConstructor = mProxyClass.getDeclaredConstructor(
                Context.class, Context.class);
            methodSetLockAuthority = mProxyClass.getDeclaredMethod(
                "setLockAuthority", String.class);
            methodExecute = mProxyClass.getDeclaredMethod("execute",
                                                          String.class, JSONArray.class);
            methodOnPause = mProxyClass.getDeclaredMethod("onPause",
                                                          Boolean.class);
            methodOnResume = mProxyClass.getDeclaredMethod("onResume",
                                                           Boolean.class);
            methodOnStart = mProxyClass.getDeclaredMethod("onStart");
            methodOnStop = mProxyClass.getDeclaredMethod("onStop");
            methodOnNewIntent = mProxyClass.getDeclaredMethod("onNewIntent",
                                                              Intent.class);
            methodOnDestroy = mProxyClass.getDeclaredMethod("onDestroy");
            methodOnLauncherLoadFinish = mProxyClass
                .getDeclaredMethod("onLauncherLoadFinish");
            methodOnActivityResult = mProxyClass.getDeclaredMethod(
                "onActivityResult", Integer.class, Integer.class,
                Intent.class);
            methodShouldAllowRequest = mProxyClass.getDeclaredMethod(
                "shouldAllowRequest", String.class);
            methodShouldAllowNavigation = mProxyClass.getDeclaredMethod(
                "shouldAllowNavigation", String.class);
            methodShouldOpenExternalUrl = mProxyClass.getDeclaredMethod(
                "shouldOpenExternalUrl", String.class);
            methodOnOverrideUrlLoading = mProxyClass.getDeclaredMethod(
                "onOverrideUrlLoading", String.class);
            methodRemapUri = mProxyClass.getDeclaredMethod("remapUri",
                                                           Uri.class);
            methodOnConfigurationChanged = mProxyClass.getDeclaredMethod(
                "onConfigurationChanged", Configuration.class);
            methodOnPageFinishedLoading = mProxyClass
                .getDeclaredMethod("onPageFinishedLoading");
            Log.e(TAG, "######## loadClassType success !!");
            return true;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "######## loadClassType error = " + e);
            e.printStackTrace();
            return false;
        } catch (SecurityException e) {
            Log.e(TAG, "######## loadClassType error = " + e);
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "######## loadClassType error = " + e);
            e.printStackTrace();
            return false;
        }
    }

    private boolean loadInstance() {
        try {
            obj = mProxyConstructor.newInstance(mRemoteContext, mContext);
            Log.e(TAG, "######## loadInstance success = " + obj);
            return true;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "######## loadInstance error = " + e);
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            Log.e(TAG, "######## loadInstance error = " + e);
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "######## loadInstance error = " + e);
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            Log.e(TAG, "######## loadInstance error = " + e);
            e.printStackTrace();
            return false;
        }
    }

    public void setLockAuthority(String packagename) {
        if (methodSetLockAuthority != null) {
            try {
                methodSetLockAuthority.invoke(obj, packagename);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean loadProxy(Context remoteContext) {

        if (mLoadSuccessed) {
            Log.i(TAG, "######## proxy had loaded successed !!!");
            return true;
        }

        if (remoteContext == null) {
            Log.e(TAG, "######## remoteContext is null !");
            return false;
        }

        mRemoteContext = remoteContext;
        Log.i(TAG, "######## mRemoteContext = " + mRemoteContext);

        if (loadContext(remoteContext) && loadClassType() && loadInstance()) {
            Log.e(TAG, "######## loadProxy success !");
            mLoadSuccessed = true;
            return true;
        }

        return false;

    }

    @Override
    public int execute(String action, final JSONArray args) {
        // TODO Auto-generated method stub
        if (methodExecute != null) {
            try {
                methodExecute.invoke(obj, action, args);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return -1;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return -1;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    @Override
    public void onPause(Boolean multitasking) {
        // TODO Auto-generated method stub
        if (methodOnPause != null) {
            try {
                methodOnPause.invoke(obj, multitasking);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume(Boolean multitasking) {
        // TODO Auto-generated method stub
        if (methodOnResume != null) {
            try {
                methodOnResume.invoke(obj, multitasking);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        if (methodOnStart != null) {
            try {
                methodOnStart.invoke(obj);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        if (methodOnStop != null) {
            try {
                methodOnStop.invoke(obj);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        if (methodOnNewIntent != null) {
            try {
                methodOnNewIntent.invoke(obj, intent);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (methodOnDestroy != null) {
            try {
                methodOnDestroy.invoke(obj);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLauncherLoadFinish() {
        // TODO Auto-generated method stub
        if (methodOnLauncherLoadFinish != null) {
            try {
                methodOnLauncherLoadFinish.invoke(obj);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(Integer requestCode, Integer resultCode,
                                 Intent intent) {
        // TODO Auto-generated method stub
        if (methodOnActivityResult != null) {
            try {
                methodOnActivityResult.invoke(obj, requestCode, requestCode,
                                              intent);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Boolean shouldAllowRequest(String url) {
        // TODO Auto-generated method stub
        if (methodShouldAllowRequest != null) {
            try {
                methodShouldAllowRequest.invoke(obj, url);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public Boolean shouldAllowNavigation(String url) {
        // TODO Auto-generated method stub
        if (methodShouldAllowNavigation != null) {
            try {
                methodShouldAllowNavigation.invoke(obj, url);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public Boolean shouldOpenExternalUrl(String url) {
        // TODO Auto-generated method stub
        if (methodShouldOpenExternalUrl != null) {
            try {
                methodShouldOpenExternalUrl.invoke(obj, url);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean onOverrideUrlLoading(String url) {
        // TODO Auto-generated method stub
        if (methodOnOverrideUrlLoading != null) {
            try {
                methodOnOverrideUrlLoading.invoke(obj, url);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public Uri remapUri(Uri uri) {
        // TODO Auto-generated method stub
        if (methodRemapUri != null) {
            try {
                methodRemapUri.invoke(obj, uri);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        if (methodOnConfigurationChanged != null) {
            try {
                methodOnConfigurationChanged.invoke(obj, newConfig);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPageFinishedLoading() {
        // TODO Auto-generated method stub
        if (methodOnPageFinishedLoading != null) {
            try {
                methodOnPageFinishedLoading.invoke(obj);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
