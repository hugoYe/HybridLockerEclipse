package com.coco.lock2.local.app.base;

import java.io.File;
import java.io.IOException;

import org.apache.cordova.CordovaWrap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.cooee.cordova.plugins.TouchEventPrevent;
import com.cooee.statistics.StatisticsBaseNew;
import com.cooee.statistics.StatisticsExpandNew;

final public class LockViewContainer extends FrameLayout implements IBaseView {

	private static final int MSG_COPY_FILE_SUCCESS = 0;
	private static final String SP_KEY_COPY_FILE_SUCCESS = "copyFileSuccess";
	private static final String SP_HTML_FILES_ROOT_DIR = "html_dir";
	private String htmlFilesDir = "";
	private static final String HTML_FILES_ROOT_DIR = "www";
	private static final String HTML_FILES_ROOT_DIR_NEW = "www1";
	private static final String SDCARD_FILES_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/h5lock/";
	private final String updateFile = "success";
	private Context mContext;
	private Context mRemoteContext;
	private CordovaWrap mCordovaWrap;
	private WebView mWebView;
	private View mLockView;

	public LockViewContainer(Context context, Context remoteContext) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mRemoteContext = remoteContext;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mLockView.onTouchEvent(ev);
		}
		if (TouchEventPrevent.preventWebTouchEvent) {
			mLockView.onTouchEvent(ev);
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				TouchEventPrevent.preventWebTouchEvent = false;
			}
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void onViewResume() {
		// TODO Auto-generated method stub
		mCordovaWrap.onResume();
		if (mLockView instanceof IBaseView) {
			((IBaseView) mLockView).onViewResume();
		}
	}

	@Override
	public void onViewPause() {
		// TODO Auto-generated method stub
		Log.v("**************", "mCordovaWrap onPause begin");
		mCordovaWrap.onPause();
		Log.v("**************", "mCordovaWrap onPause end");
		if (mLockView instanceof IBaseView) {
			((IBaseView) mLockView).onViewPause();
		}
	}

	@Override
	public void onViewDestroy() {
		// TODO Auto-generated method stub
		mCordovaWrap.onDestroy();
		if (mLockView instanceof IBaseView) {
			((IBaseView) mLockView).onViewDestroy();
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.e("LockView", "######## handleMessage 111");
			if (msg.what == MSG_COPY_FILE_SUCCESS) {
				initialWebview();
				Log.e("LockView", "######## handleMessage 222");
			}
		}
	};

	private void initialWebview() {
		SharedPreferences sp;
		if (mRemoteContext != null) {
			sp = PreferenceManager.getDefaultSharedPreferences(mRemoteContext);
		} else {
			sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		htmlFilesDir = sp
				.getString(SP_HTML_FILES_ROOT_DIR, HTML_FILES_ROOT_DIR);
		Log.v("initialWebview", htmlFilesDir);
		if (mRemoteContext != null) {
			mCordovaWrap.launchUrl = "file:///" + mRemoteContext.getFilesDir()
					+ "/" + mContext.getPackageName() + "/" + htmlFilesDir
					+ "/index.html";
		} else {
			mCordovaWrap.launchUrl = "file:///" + mContext.getFilesDir() + "/"
					+ htmlFilesDir + "/index.html";
			Log.v("initialWebview", mCordovaWrap.launchUrl);
		}
		mWebView = (WebView) mCordovaWrap
				.loadWebViewUrl(mCordovaWrap.launchUrl);
		mWebView.setBackgroundColor(Color.TRANSPARENT);
		if (mRemoteContext != null) {
			if (DeviceUtils.HasNavigationBar(mRemoteContext)) {
				mWebView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, DeviceUtils
								.getScreenPixelsHeight(mRemoteContext)));
			}
		}
		addView(mWebView);
	}

	@SuppressLint("NewApi")
	public void setupViews(View customView) {
		mLockView = customView;
		addView(customView);
		mCordovaWrap = new CordovaWrap(mContext, mRemoteContext);
		mCordovaWrap.onCreate(null);
		SharedPreferences sp;
		if (mRemoteContext != null) {
			sp = PreferenceManager.getDefaultSharedPreferences(mRemoteContext);
		} else {
			sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		File sdFile = new File(SDCARD_FILES_DIR + "/"
				+ mContext.getPackageName() + "/" + updateFile);
		Log.e("LockView", "######## sdFile path = " + sdFile.getAbsolutePath());
		if (!sp.getBoolean(SP_KEY_COPY_FILE_SUCCESS, false)) {
			ThreadUtil.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						if (mRemoteContext != null) {
							File dir = new File(mRemoteContext.getFilesDir()
									+ "/" + mContext.getPackageName());
							dir.mkdir();
							String destDir = dir.getAbsolutePath();
							Log.e("LockView",
									"######## SP_KEY_COPY_FILE_SUCCESS = "
											+ destDir);
							FileUtils.copyAssetDirToFiles(destDir, mContext,
									HTML_FILES_ROOT_DIR);
						} else {
							String destDir = mContext.getFilesDir()
									.getAbsolutePath();
							FileUtils.copyAssetDirToFiles(destDir, mContext,
									HTML_FILES_ROOT_DIR);
						}
						SharedPreferences sp;
						if (mRemoteContext != null) {
							sp = PreferenceManager
									.getDefaultSharedPreferences(mRemoteContext);
						} else {
							sp = PreferenceManager
									.getDefaultSharedPreferences(mContext);
						}
						sp.edit().putBoolean(SP_KEY_COPY_FILE_SUCCESS, true)
								.commit();
						sp.edit()
								.putString(SP_HTML_FILES_ROOT_DIR,
										HTML_FILES_ROOT_DIR).commit();
						mHandler.sendEmptyMessage(MSG_COPY_FILE_SUCCESS);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} else if (sdFile.exists()) {
			ThreadUtil.execute(new Runnable() {

				@Override
				public void run() {
					SharedPreferences sp;
					if (mRemoteContext != null) {
						sp = PreferenceManager
								.getDefaultSharedPreferences(mRemoteContext);
					} else {
						sp = PreferenceManager
								.getDefaultSharedPreferences(mContext);
					}
					String filesDir = sp.getString(SP_HTML_FILES_ROOT_DIR,
							HTML_FILES_ROOT_DIR);
					if (filesDir.equals(HTML_FILES_ROOT_DIR)) {
						htmlFilesDir = HTML_FILES_ROOT_DIR_NEW;
					} else {
						htmlFilesDir = HTML_FILES_ROOT_DIR;
					}
					Log.e("LockView", "######## sdFile.exists() = true");
					if (mRemoteContext != null) {
						Log.v("update_complete", "true");
						File dir = new File(mRemoteContext.getFilesDir() + "/"
								+ mContext.getPackageName());
						// 先删除原有目录下所有文件
						FileUtils.deleteFile(new File(dir + "/" + filesDir));
						dir.mkdir();
						String destDir = dir.getAbsolutePath() + "/"
								+ htmlFilesDir;
						String path = SDCARD_FILES_DIR
								+ mContext.getPackageName() + "/"
								+ HTML_FILES_ROOT_DIR;
						Log.v("LockView", "######## path " + path);
						Log.v("LockView", "######## destDir " + destDir);
						FileUtils.copySDDirToFiles(path, destDir);
					} else {
						File dir = mContext.getFilesDir();
						// 先删除原有目录下所有文件
						FileUtils.deleteFile(new File(dir + "/" + filesDir));
						String path = SDCARD_FILES_DIR
								+ mContext.getPackageName() + "/"
								+ HTML_FILES_ROOT_DIR;
						String destDir = mContext.getFilesDir()
								.getAbsolutePath() + "/" + htmlFilesDir;
						FileUtils.copySDDirToFiles(path, destDir);
					}
					sp.edit().putString(SP_HTML_FILES_ROOT_DIR, htmlFilesDir)
							.commit();
					File sdFile = new File(SDCARD_FILES_DIR + "/"
							+ mContext.getPackageName() + "/" + updateFile);
					Log.e("LockView",
							"######## sdFile path = "
									+ sdFile.getAbsolutePath());
					if (sdFile.exists()) {
						sdFile.delete();
					}
					mHandler.sendEmptyMessage(MSG_COPY_FILE_SUCCESS);
					Log.e("LockView",
							"######## mHandler.sendEmptyMessage(MSG_COPY_FILE_SUCCESS)");
				}
			});
		} else {
			Log.v("LockView", "######## initialWebview !!!");
			initialWebview();
		}
		StatisticsBaseNew.setApplicationContext(mContext);
		StatisticsExpandNew.setStatiisticsLogEnable(true);
		Intent it = new Intent();
		it.setClassName(mContext.getPackageName(),
				"com.cooee.lock.statistics.StaticClass");
		it.putExtra("EventType", "");
		mContext.startService(it);
		String path = SDCARD_FILES_DIR + mContext.getPackageName();
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		boolean sdcardState;
		if (Build.VERSION.SDK_INT >= 19) {
			if (Environment.getStorageState(file).equals(
					Environment.MEDIA_MOUNTED)) {
				sdcardState = true;
			} else {
				sdcardState = false;
			}
		} else {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				sdcardState = true;
			} else {
				sdcardState = false;
			}
		}

		// if (IsHaveInternet(mContext) && sdcardState) {
		// Log.v("LockView", "######## startService !!!");
		// Intent intent = new Intent();
		// intent.setClassName(mContext.getPackageName(),
		// "com.cooee.control.center.module.update.UpdateService");
		// mContext.startService(intent);
		// }

	}

	/**
	 * 判断是否联网
	 */
	public boolean IsHaveInternet(final Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manger.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			return false;
		}
	}
}
