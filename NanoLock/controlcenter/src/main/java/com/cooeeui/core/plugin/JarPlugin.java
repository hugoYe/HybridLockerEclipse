package com.cooeeui.core.plugin;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.cooeeui.cordova.plugins.UnlockListener;
import com.cooeeui.core.utils.ThreadUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class JarPlugin extends CordovaPlugin {

    private final String TAG = "JarPlugin";

    private final int EXECUTE_RESULT_NOT_HANDLE = -1;
    private final int EXECUTE_RESULT_HANDLE_FAIL = 0;
    private final int EXECUTE_RESULT_HANDLE_SUCCESS = 1;

    private final String ACTION_JS_DATA = "com.cooeelock.core.jarplugin.jsdata";

    private Context mContext;

    private int mHandleRst;
    private CallbackContext mCallbackContext;
    private final String ACTION_UNLOCK = "unlock";
    private static UnlockListener sUnlockListener;

    private BroadcastReceiver mJsdataReceiver;
    public final static String ACTION_COPY_JAR_SDCARD_TO_DATA = "com.cooee.copy.jar.sdcard.to.data";
    public final static String ACTION_COPY_JAR_ASSETS_TO_DATA = "com.cooee.copy.jar.assets.to.data";
    public final static String
        ACTION_COPY_JAR_TO_DATA_SUCCESS =
        "com.cooee.copy.jar.to.data.success";
    public final static String ACTION_LOAD_WEBVIEW = "com.cooee.load.webview";

    class JsdataBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            Log.e(TAG, "######## JsdataBroadcastReceiver action = " + action);
            if (action.equals(ACTION_JS_DATA)) {
                Log.e(TAG,
                      "######## JsdataBroadcastReceiver " + intent.getStringExtra("key_js_label")
                      + "   " + intent.getStringExtra("key_js_data"));
//				sendJS(intent.getStringExtra("key_js_data"));
                saveData(intent.getStringExtra("key_js_label"),
                         intent.getStringExtra("key_js_data"));
                deleteJsData(context);
            }

            boolean unlock = intent.getBooleanExtra("key_unlock", false);
            if (unlock) {
                if (sUnlockListener != null) {
                    sUnlockListener.onUnlock();
                }
            }

            if (action.equals(ACTION_COPY_JAR_TO_DATA_SUCCESS)) {
                Log.e(TAG,
                      "######## JsdataBroadcastReceiver action = ACTION_COPY_JAR_TO_DATA_SUCCESS");
                getUpdateData();
            }
        }
    }

    private void saveData(final String name, final String data) {
        ThreadUtil.execute(new Runnable() {

            @Override
            public void run() {
                File file = new File(mContext.getFilesDir() + "/www/json/"
                                     + name + ".json");
                if (file.exists()) {
                    file.delete();
                }
                if (!file.getParentFile().exists()) {// 判断父文件是否存在，如果不存在则创建
                    file.getParentFile().mkdirs();
                }
                PrintStream out = null; // 打印流
                try {
                    out = new PrintStream(new FileOutputStream(file)); // 实例化打印流对象
                    out.print(data); // 输出数据
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) { // 如果打印流不为空，则关闭打印流
                        out.close();
                    }
                }
                sendJS("javascript:getData();");
            }
        });
    }

    public static void setOnUnlockListener(UnlockListener unlockListener) {
        sUnlockListener = unlockListener;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        // TODO Auto-generated method stub
        super.initialize(cordova, webView);
        if (cordova.getActivity() != null) {
            mContext = cordova.getActivity();
        } else if (cordova.getContext() != null) {
            mContext = cordova.getContext();
        }

        getJsData();

        mJsdataReceiver = new JsdataBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_JS_DATA);
        mContext.registerReceiver(mJsdataReceiver, filter);
    }

    private void deleteJsData(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri contentUri = Uri.parse("content://" + context.getPackageName() + ".plugin/jarplugin");
        resolver.delete(contentUri, null, null);
    }

    private void getJsData() {
        ThreadUtil.execute(new Runnable() {

            @Override
            public void run() {
                String data = "";
                String name = "";
                Cursor csr = null;
                try {
                    ContentResolver resolver = mContext.getContentResolver();
                    Uri
                        contentUri =
                        Uri.parse("content://" + mContext.getPackageName() + ".plugin/jarplugin");
                    Log.v("###********######", "getJsData contentUri" + contentUri.toString());
                    csr = resolver.query(contentUri, null, null, null, null);
                    Log.v("###********######", "getJsData csr = " + csr.getCount());
                    if (csr != null) {
                        if (csr.getCount() > 0) {
                            while (csr.moveToNext()) {
                                data = csr.getString(csr.getColumnIndexOrThrow("data"));
                                name = csr.getString(csr.getColumnIndexOrThrow("label"));
                                File file = null;
//								new File(mContext.getFilesDir() + "/www/json/"
////										+ name + ".json");
                                if (cordova.getRemoteContext() != null) {
                                    SharedPreferences sp = cordova
                                        .getRemoteContext()
                                        .getSharedPreferences(
                                            mContext.getPackageName(),
                                            Context.MODE_PRIVATE);
                                    file = new File(cordova.getRemoteContext()
                                                        .getFilesDir()
                                                    + "/"
                                                    + mContext.getPackageName()
                                                    + "/"
                                                    + sp.getString("html_dir", "www")
                                                    + "/json/" + name + ".json");
                                }
                                Log.v("###********######",
                                      "cordova.getRemoteContext() " + cordova.getRemoteContext());
                                Log.v("###********######", "file " + file.toString());
                                if (file.exists()) {
                                    file.delete();
                                }
                                if (!file.getParentFile().exists()) {// 判断父文件是否存在，如果不存在则创建
                                    file.getParentFile().mkdirs();
                                }
                                PrintStream out = null; // 打印流
                                try {
                                    out = new PrintStream(new FileOutputStream(file)); // 实例化打印流对象
                                    out.print(data); // 输出数据
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (out != null) { // 如果打印流不为空，则关闭打印流
                                        out.close();
                                    }
                                }
                            }
                            sendJS("javascript:getData();");
                            resolver.delete(contentUri, null, null);
                        }
                    }
                } catch (Exception e) {
                    Log.v("###********######", "Exception!!!!!!!!!!");
                    e.printStackTrace();
                } finally {
                    if (csr != null) {
                        csr.close();
                    }
                }
            }
        });
    }

    @Override
    public boolean execute(final String action, final JSONArray args,
                           final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;

        Log.e(TAG, "########  JSONArray = " + args.toString());
//		int version = args.getInt(0);
//		if (isJarPluginNeedUpdate(version)) {
//			// 更新apk
//			Log.i(TAG, "######## 更新Jar !!!!!!");
//			sendJS("javascript:showTip();");
//			Log.e(TAG, "######## startService");
//			return false;
//		}

        if (action.equals(ACTION_UNLOCK)) {
            if (sUnlockListener != null) {
                sUnlockListener.onUnlock();
            }
            return true;
        }

        Intent it = new Intent("com.cooee.get.data");
        it.setClassName(mContext.getPackageName(),
                        "JarExecuteService");
        mContext.startService(it);

//		getUpdateData();
        return true;
    }

    private void getUpdateData() {
        Intent intent = new Intent("com.cooee.jar.update.data");
        intent.setClassName(mContext.getPackageName(),
                            "JarExecuteService");
        mContext.startService(intent);
    }

    private boolean isJarPluginNeedUpdate(int version) {
        // if (version > 0) {
        // return true;
        // }

        return false;
    }

    private void handleExecute(final String action, final JSONArray args) {
        mHandleRst = JarPluginProxyManager.getInstance().execute(action, args);

        switch (mHandleRst) {
            case EXECUTE_RESULT_NOT_HANDLE:
                mCallbackContext.error(EXECUTE_RESULT_NOT_HANDLE);
                break;

            case EXECUTE_RESULT_HANDLE_FAIL:
                mCallbackContext.error(EXECUTE_RESULT_HANDLE_FAIL);
                break;

            case EXECUTE_RESULT_HANDLE_SUCCESS:
                mCallbackContext.success(EXECUTE_RESULT_HANDLE_SUCCESS);
                break;
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

    /**
     * Called when the system is about to start resuming a previous activity.
     *
     * @param multitasking Flag indicating if multitasking is turned on for app
     */
    public void onPause(boolean multitasking) {
        Intent intent = new Intent("com.cooee.jar.onpause");
        intent.setClassName(mContext.getPackageName(),
                            "JarExecuteService");
        intent.putExtra("key_value", multitasking);
        mContext.startService(intent);
//		JarPluginProxyManager.getInstance().onPause(multitasking);
    }

    /**
     * Called when the activity will start interacting with the user.
     *
     * @param multitasking Flag indicating if multitasking is turned on for app
     */
    public void onResume(boolean multitasking) {
        Intent intent = new Intent("com.cooee.jar.onresume");
        intent.setClassName(mContext.getPackageName(),
                            "JarExecuteService");
        intent.putExtra("key_value", multitasking);
        mContext.startService(intent);
//		JarPluginProxyManager.getInstance().onResume(multitasking);
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    public void onStart() {
        Intent intent = new Intent("com.cooee.jar.onstart");
        intent.setClassName(mContext.getPackageName(),
                            "JarExecuteService");
        mContext.startService(intent);
//		JarPluginProxyManager.getInstance().onStart();
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    public void onStop() {
        Intent intent = new Intent("com.cooee.jar.onstop");
        intent.setClassName(mContext.getPackageName(),
                            "JarExecuteService");
        mContext.startService(intent);
//		JarPluginProxyManager.getInstance().onStop();
    }

    /**
     * Called when the activity receives a new intent.
     */
    public void onNewIntent(Intent intent) {
        Intent intent1 = new Intent("com.cooee.jar.onnewintent");
        intent1.setClassName(mContext.getPackageName(),
                             "JarExecuteService");
        intent1.putExtra("key_value", intent);
        mContext.startService(intent1);
//		JarPluginProxyManager.getInstance().onNewIntent(intent);
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    public void onDestroy() {
        Intent intent = new Intent("com.cooee.jar.ondestroy");
        intent.setClassName(mContext.getPackageName(),
                            "JarExecuteService");
        mContext.startService(intent);
//		JarPluginProxyManager.getInstance().onDestroy();

        if (mJsdataReceiver != null) {
            mContext.unregisterReceiver(mJsdataReceiver);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Intent intent1 = new Intent("com.cooee.jar.onactivityresult");
        intent1.setClassName(mContext.getPackageName(),
                             "JarExecuteService");
        intent1.putExtra("key_value1", requestCode);
        intent1.putExtra("key_value2", resultCode);
        intent1.putExtra("key_value3", intent);
        mContext.startService(intent1);
//		JarPluginProxyManager.getInstance().onActivityResult(requestCode,
//				resultCode, intent);
    }

    public Boolean shouldAllowRequest(String url) {
        return JarPluginProxyManager.getInstance().shouldAllowRequest(url);
    }

    /**
     * Hook for blocking navigation by the Cordova WebView. This applies both to top-level and
     * iframe navigations.
     *
     * This will be called when the WebView's needs to know whether to navigate to a new page.
     * Return false to block the navigation: if any plugin returns false, Cordova will block the
     * navigation. If all plugins return null, the default policy will be enforced. It at least one
     * plugin returns true, and no plugins return false, then the navigation will proceed.
     */
    public Boolean shouldAllowNavigation(String url) {
        return JarPluginProxyManager.getInstance().shouldAllowNavigation(url);
    }

    /**
     * Hook for blocking the launching of Intents by the Cordova application.
     *
     * This will be called when the WebView will not navigate to a page, but could launch an intent
     * to handle the URL. Return false to block this: if any plugin returns false, Cordova will
     * block the navigation. If all plugins return null, the default policy will be enforced. If at
     * least one plugin returns true, and no plugins return false, then the URL will be opened.
     */
    public Boolean shouldOpenExternalUrl(String url) {
        return JarPluginProxyManager.getInstance().shouldOpenExternalUrl(url);
    }

    /**
     * Allows plugins to handle a link being clicked. Return true here to cancel the navigation.
     *
     * @param url The URL that is trying to be loaded in the Cordova webview.
     * @return Return true to prevent the URL from loading. Default is false.
     */
    public boolean onOverrideUrlLoading(String url) {
        return JarPluginProxyManager.getInstance().onOverrideUrlLoading(url);
    }

    /**
     * Hook for redirecting requests. Applies to WebView requests as well as requests made by
     * plugins. To handle the request directly, return a URI in the form:
     *
     * cdvplugin://pluginId/...
     *
     * And implement handleOpenForRead(). To make this easier, use the toPluginUri() and
     * fromPluginUri() helpers:
     *
     * public Uri remapUri(Uri uri) { return toPluginUri(uri); }
     *
     * public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri uri) throws IOException {
     * Uri origUri = fromPluginUri(uri); ... }
     */
    public Uri remapUri(Uri uri) {
        return JarPluginProxyManager.getInstance().remapUri(uri);
    }

    /**
     * Called by the system when the device configuration changes while your activity is running.
     *
     * @param newConfig The new device configuration
     */
    public void onConfigurationChanged(Configuration newConfig) {
        JarPluginProxyManager.getInstance().onConfigurationChanged(newConfig);
    }
}
