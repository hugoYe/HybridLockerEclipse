package com.cooeeui.core.plugin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cooeeui.core.utils.FileUtils;
import com.cooeeui.core.utils.Tools;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import cool.sdk.common.CoolHttpClient;
import cool.sdk.common.CoolHttpClient.ResultEntity;
import cool.sdk.common.JsonUtil;
import cool.sdk.common.UrlUtil;
import cool.sdk.download.manager.DlMethod;

public class JarExecuteService extends Service {

    private final String TAG = "JarExecuteService";
    private static final String HTML_JAR_ROOT_DIR = "h5";
    private static final String HTML_JAR_ROOT_DIR_NEW = "h51";
    private static final String SP_HTML_JAR_ROOT_DIR = "jar_dir";
    private static final String SDCARD_FILES_DIR = Environment
                                                       .getExternalStorageDirectory()
                                                       .getAbsolutePath() + "/h5lock/";
    private final String updateJar = "success_jar";
    private String htmlJarDir = "";
    public final static String ACTION_COPY_JAR_SDCARD_TO_DATA = "com.cooee.copy.jar.sdcard.to.data";
    public final static String ACTION_COPY_JAR_ASSETS_TO_DATA = "com.cooee.copy.jar.assets.to.data";
    public final static String
        ACTION_COPY_JAR_TO_DATA_SUCCESS =
        "com.cooee.copy.jar.to.data.success";
    public final static String ACTION_LOAD_WEBVIEW = "com.cooee.load.webview";


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
//		File file = getFilesDir();
//		Log.e(TAG, "######## onCreate file = " + file);
//		if (file != null) {
//			String destDir = getFilesDir().getAbsolutePath();
//			Log.e(TAG, "######## onCreate destDir = " + destDir);
//			try {
//				FileUtils.copyAssetDirToFiles(destDir, this, "h5");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "######## onStartCommand");
        String action = intent.getAction();
        boolean loadRst = JarPluginProxyManager.getInstance().loadProxy(this);
//		if (loadRst) {
//			JarPluginProxyManager.getInstance().setLockAuthority(
//					getPackageName());
//			JarPluginProxyManager.getInstance().startTask();
//		}
        if (action.equals(ACTION_COPY_JAR_ASSETS_TO_DATA)) {
            if (!loadRst) {
                Log.v(TAG, "ACTION_COPY_JAR_ASSETS_TO_DATA");
                try {
                    copyJarAssetsToData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                sendBroadcast(new Intent(ACTION_COPY_JAR_TO_DATA_SUCCESS));
            }
        } else if (action.equals(ACTION_COPY_JAR_SDCARD_TO_DATA)) {
            Log.v(TAG, "ACTION_COPY_JAR_SDCARD_TO_DATA");
            copyJarSdcardToData();
        } else if (action.equals(ACTION_LOAD_WEBVIEW)) {
            Log.v(TAG, "ACTION_LOAD_WEBVIEW");
            if (loadRst) {
                sendBroadcast(new Intent(ACTION_COPY_JAR_TO_DATA_SUCCESS));
            } else {
                Log.e(TAG, "######## JarPluginProxyManager load failed ! ");
                String path = Environment.getExternalStorageDirectory()
                                  .getAbsolutePath() + "/h5lock/" + getPackageName();
                if (Tools.isHaveInternet(this) && Tools.getSdcardState(path)) {
                    new JarDownloadTask(this).execute();
                } else {
                    Log.v(TAG, "ACTION_COPY_JAR_ASSETS_TO_DATA  loadRst = " + loadRst);
                    try {
                        copyJarAssetsToData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (action.equals("com.cooee.jar.onpause")) {
            JarPluginProxyManager.getInstance().onPause(intent.getBooleanExtra("key_value", false));
        } else if (action.equals("com.cooee.jar.onresume")) {
            JarPluginProxyManager.getInstance()
                .onResume(intent.getBooleanExtra("key_value", false));
        } else if (action.equals("com.cooee.jar.onstart")) {
            JarPluginProxyManager.getInstance().onStart();
        } else if (action.equals("com.cooee.jar.onstop")) {
            JarPluginProxyManager.getInstance().onStop();
        } else if (action.equals("com.cooee.jar.onnewintent")) {
            JarPluginProxyManager.getInstance()
                .onNewIntent((Intent) (intent.getParcelableExtra("key_value")));
        } else if (action.equals("com.cooee.jar.ondestroy")) {
            JarPluginProxyManager.getInstance().onDestroy();
        } else if (action.equals("com.cooee.jar.onactivityresult")) {
            JarPluginProxyManager.getInstance()
                .onActivityResult(intent.getIntExtra("key_value1", 0),
                                  intent.getIntExtra("key_value2", 0),
                                  (Intent) (intent.getParcelableExtra("key_value3")));
        }
        return START_NOT_STICKY;
    }

    private void copyJarAssetsToData() throws IOException {
        String destDir = getFilesDir().getAbsolutePath();
        Log.v("%&&**%**&*%*%", "copyJarAssetsToData   " + destDir);
        FileUtils.copyAssetDirToFiles(destDir, this, HTML_JAR_ROOT_DIR);
        SharedPreferences sp = PreferenceManager
            .getDefaultSharedPreferences(this);
        sp.edit().putString(SP_HTML_JAR_ROOT_DIR, HTML_JAR_ROOT_DIR).commit();
        sendBroadcast(new Intent(ACTION_COPY_JAR_TO_DATA_SUCCESS));
        boolean loadRst = JarPluginProxyManager.getInstance().loadProxy(this);
        if (loadRst) {
            JarPluginProxyManager.getInstance().setLockAuthority(
                getPackageName());
            JarPluginProxyManager.getInstance().execute("com.cooee.jar.update.data", null);
        }
    }

    private void copyJarSdcardToData() {
        File sdJar = new File(SDCARD_FILES_DIR + "/" + getPackageName() + "/"
                              + updateJar);
        Log.v("%&&**%**&*%*%", "copyJarSdcardToData   " + sdJar.toString());
        if (sdJar.exists()) {
            Log.v("%&&**%**&*%*%", "copyJarSdcardToData  sdJar.exists() ");
            SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
            String jarDir = sp.getString(SP_HTML_JAR_ROOT_DIR,
                                         HTML_JAR_ROOT_DIR);
            if (jarDir != null && jarDir.equals(HTML_JAR_ROOT_DIR)) {
                htmlJarDir = HTML_JAR_ROOT_DIR_NEW;
            } else {
                htmlJarDir = HTML_JAR_ROOT_DIR;
            }
            File dir = getFilesDir();
            // 先删除原有目录下所有文件
            FileUtils.deleteFile(new File(dir + "/" + jarDir));
            String path = SDCARD_FILES_DIR + getPackageName() + "/"
                          + HTML_JAR_ROOT_DIR;
            String destDir = getFilesDir().getAbsolutePath() + "/" + htmlJarDir;
            Log.v("%&&**%**&*%*%", "copyJarSdcardToData   " + destDir);
            FileUtils.copySDDirToFiles(path, destDir);
            sp.edit().putString(SP_HTML_JAR_ROOT_DIR, htmlJarDir).commit();
        }
        File sdFile = new File(SDCARD_FILES_DIR + getPackageName());
        if (sdFile.exists()) {
            FileUtils.deleteFile(sdFile);
        }
        sendBroadcast(new Intent(ACTION_COPY_JAR_TO_DATA_SUCCESS));

        boolean loadRst = JarPluginProxyManager.getInstance().loadProxy(this);
        if (loadRst) {
            JarPluginProxyManager.getInstance().setLockAuthority(
                getPackageName());
            JarPluginProxyManager.getInstance().execute("com.cooee.jar.update.data", null);
        }
    }

    public class JarDownloadTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private String downUrl;
        private final String jarName = "proxydex.jar";

        public JarDownloadTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (doRequestConfig() && downUrl != null && !downUrl.equals("")) {
                    String path = Environment.getExternalStorageDirectory()
                                      .getAbsolutePath()
                                  + "/h5lock/"
                                  + mContext.getPackageName();
                    boolean finish = downloadFile(path);
                    if (finish) {
                        new File(path + "/success_jar").createNewFile();
                        copyJarSdcardToData();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private boolean downloadFile(String path) throws Exception {
            // 判断文件目录是否存在
            File file = new File(path + "/h5/proxy");
            if (!file.exists()) {
                file.mkdir();
            }
            File updateFile = new File(file + "/" + jarName);
            HttpURLConnection conn = null;
            InputStream is = null;
            FileOutputStream fos = null;
            int readsize = 0;
            boolean isFinish = false;
            try {
                URL url = new URL(downUrl);
                // 创建连接
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                // 创建输入流
                is = conn.getInputStream();
                fos = new FileOutputStream(updateFile);
                // 缓存
                byte buf[] = new byte[1024];
                // 写入到文件中
                while ((readsize = is.read(buf)) > 0) {
                    // 写入文件
                    fos.write(buf, 0, readsize);
                }
                isFinish = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return isFinish;
        }

        private synchronized boolean doRequestConfig() throws Exception {
            if (!DlMethod.IsNetworkAvailable(mContext)) {
                return false;
            }
            JSONObject reqJson = JsonUtil.NewRequestJSON(mContext, 4, "uiupdate");
            reqJson.put("Action", "3004");
            reqJson.put("p1", 0);
            reqJson.put("p2", 0);
            reqJson.put("p3", Locale.getDefault().toString());
            reqJson.put("p4", 0);
            Log.v("COOL", "UiUpdate req:" + reqJson.toString());
            ResultEntity
                result =
                CoolHttpClient.postEntity(UrlUtil.getDataServerUrl(), reqJson.toString());
            if (result.exception != null) {
                Log.v("COOL", "UiUpdate rsp:(error)" + result.httpCode + " " + result.exception);
                return false;
            }
            Log.v("COOL", "UiUpdate rsp:" + result.httpCode + " " + result.content);
            JSONObject resJson = new JSONObject(result.content);
            int retcode = resJson.optInt("retcode");
            Log.v("COOL", "doRequestConfig resJson retcode=" + retcode);
            if (retcode == 0) {
                downUrl = resJson.optString("r5");
                return true;
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        boolean loadRst = JarPluginProxyManager.getInstance().loadProxy(this);
        if (loadRst) {
            JarPluginProxyManager.getInstance().onDestroy();
        }
        super.onDestroy();
    }
}
