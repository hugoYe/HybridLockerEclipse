package com.cooeelock.core.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cooee.control.center.module.base.FileUtils;
import com.cooee.control.center.module.base.StaticClass;
import com.cooee.control.center.module.base.Tools;

public class JarExecuteService extends Service {

	private final String TAG = "JarExecuteService";
	private static final String HTML_JAR_ROOT_DIR = "h5";
	private static final String HTML_JAR_ROOT_DIR_NEW = "h51";
	private static final String SP_HTML_JAR_ROOT_DIR = "jar_dir";
	private static final String SDCARD_FILES_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/h5lock/";
	private final String updateJar = "success_jar";
	private String htmlJarDir = "";
	public final static String ACTION_COPY_JAR_SDCARD_TO_DATA = "com.cooee.copy.jar.sdcard.to.data";
	public final static String ACTION_COPY_JAR_ASSETS_TO_DATA = "com.cooee.copy.jar.assets.to.data";
	public final static String ACTION_LOAD_WEBVIEW = "com.cooee.load.webview";
	private static final int MAX_LIST_SERVICE = 50;
	private boolean copyJarFinish = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		stopServiceRunning(getApplicationContext(),
				"com.cooeelock.core.plugin.JarExecuteService");
	}

	private void stopServiceRunning(Context context, String serviceName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = activityManager
				.getRunningServices(MAX_LIST_SERVICE);
		String packageName = "";
		String className = "";
		for (int i = 0; i < list.size(); i++) {
			className = list.get(i).service.getClassName();
			if (serviceName.equals(className)) {
				packageName = list.get(i).service.getPackageName();
				if (!(packageName.equals(context.getPackageName()))) {
					Log.i(TAG, "stop plugin service Belong to package: "
							+ packageName);
					Intent intent = new Intent();
					intent.setClassName(packageName, className);
					context.stopService(intent);
				}
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "######## onStartCommand");
		String action = intent.getAction();
		boolean loadRst = JarPluginProxyManager.getInstance().loadProxy(this);
		if (action.equals(ACTION_COPY_JAR_ASSETS_TO_DATA)) {
			if (!loadRst) {
				Log.v(TAG, "ACTION_COPY_JAR_ASSETS_TO_DATA");
				try {
					copyJarAssetsToData();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (action.equals(ACTION_COPY_JAR_SDCARD_TO_DATA)) {
			Log.v(TAG, "ACTION_COPY_JAR_SDCARD_TO_DATA");
			copyJarSdcardToData();
		} else if (action.equals(ACTION_LOAD_WEBVIEW)) {
			Log.v(TAG, "ACTION_LOAD_WEBVIEW");
			if (!loadRst) {
				Log.e(TAG, "######## JarPluginProxyManager load failed ! ");
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/h5lock/" + getPackageName();
				if (Tools.isHaveInternet(this) && Tools.getSdcardState(path)) {
					new JarDownloadTask(this).execute();
				}
			}
		}else {
			if (loadRst) {
				String strAction = intent.getStringExtra("jar_action");
				String strArgs = intent.getStringExtra("jar_args");
				try {
					if (strArgs != null) {
						JSONArray args = new JSONArray(strArgs);
						JarPluginProxyManager.getInstance().copyFinish(copyJarFinish);
						JarPluginProxyManager.getInstance().execute(strAction,
								args);
						copyJarFinish = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		if (action.equals("com.cooee.jar.onpause")) {
			JarPluginProxyManager.getInstance().onPause(
					intent.getBooleanExtra("key_value", false));
		} else if (action.equals("com.cooee.jar.onresume")) {
			JarPluginProxyManager.getInstance().onResume(
					intent.getBooleanExtra("key_value", false));
		} else if (action.equals("com.cooee.jar.onstart")) {
			JarPluginProxyManager.getInstance().onStart();
		} else if (action.equals("com.cooee.jar.onstop")) {
			JarPluginProxyManager.getInstance().onStop();
		} else if (action.equals("com.cooee.jar.onnewintent")) {
			JarPluginProxyManager.getInstance().onNewIntent(
					(Intent) (intent.getParcelableExtra("key_value")));
		} else if (action.equals("com.cooee.jar.ondestroy")) {
			JarPluginProxyManager.getInstance().onDestroy();
		} else if (action.equals("com.cooee.jar.onactivityresult")) {
			JarPluginProxyManager.getInstance().onActivityResult(
					intent.getIntExtra("key_value1", 0),
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
		
		copyJarFinish = true;
//		sendBroadcast(new Intent(StaticClass.ACTION_JAR_COPY_SUCCESS));
//		boolean loadRst = JarPluginProxyManager.getInstance().loadProxy(this);
//		if (loadRst) {
//			JarPluginProxyManager.getInstance().setLockAuthority(
//					getPackageName());
//			JarPluginProxyManager.getInstance().execute(
//					ACTION_UPDATE_DATA, null);
//		}
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
			File sdFile = new File(path);
			if (sdFile.exists()) {
				FileUtils.deleteFile(sdFile);
			}
			sdJar.delete();
			copyJarFinish = true;
//			sendBroadcast(new Intent(StaticClass.ACTION_JAR_COPY_SUCCESS));
		}
//		File sdFile = new File(SDCARD_FILES_DIR + getPackageName() + "/"
//				+ HTML_JAR_ROOT_DIR);
//		if (sdFile.exists()) {
//			FileUtils.deleteFile(sdFile);
//		}
//		boolean loadRst = JarPluginProxyManager.getInstance().loadProxy(this);
//		if (loadRst) {
//			JarPluginProxyManager.getInstance().setLockAuthority(
//					getPackageName());
//			JarPluginProxyManager.getInstance().execute(
//					ACTION_UPDATE_DATA, null);
//		}
	}

	public class JarDownloadTask extends AsyncTask<Void, Void, Void> {

		private Context mContext;
		private String downUrl;
		private long downSize;
		private final String jarName = "proxydex.jar";

		public JarDownloadTask(Context context) {
			mContext = context;
			SharedPreferences sharedPreferences = mContext.getSharedPreferences(
					"Update", Context.MODE_PRIVATE);
			downUrl = sharedPreferences.getString("jar_url", "");
			downSize = sharedPreferences.getLong("jar_size", 0);
			Log.v(TAG, "downUrl = "+ downUrl +"   downSize = "+downSize);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (downUrl != null && !downUrl.equals("")) {
					String path = Environment.getExternalStorageDirectory()
							.getAbsolutePath()
							+ "/h5lock/"
							+ mContext.getPackageName();
					boolean finish = downloadFile(path);
					if (finish) {
						new File(path + "/" + updateJar).createNewFile();
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
				file.mkdirs();
			}
			File updateFile = new File(file + "/" + jarName);
			HttpURLConnection conn = null;
			InputStream is = null;
			FileOutputStream fos = null;
			int readsize = 0;
			long totalSize = 0;
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
					totalSize += readsize;
				}
				if (totalSize >= downSize) {
					isFinish = true;
				}
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
	}

}
