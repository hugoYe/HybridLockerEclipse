package com.cooee.control.center.module.base;

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

import com.coco.lock.favorites.FavoritesModel;
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
		mCordovaWrap.onPause();
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
			if (msg.what == MSG_COPY_FILE_SUCCESS) {
				loadWebview();
			}
		}
	};

	private void loadWebview() {
		SharedPreferences sp;
		if (mRemoteContext != null) {
			sp = mRemoteContext.getSharedPreferences(mContext.getPackageName(),
					Context.MODE_PRIVATE);
		} else {
			sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		htmlFilesDir = sp
				.getString(SP_HTML_FILES_ROOT_DIR, HTML_FILES_ROOT_DIR);

		if (mRemoteContext != null) {
			mCordovaWrap.launchUrl = "file:///" + mRemoteContext.getFilesDir()
					+ "/" + mContext.getPackageName() + "/" + htmlFilesDir
					+ "/index.html";
		} else {
			mCordovaWrap.launchUrl = "file:///" + mContext.getFilesDir() + "/"
					+ htmlFilesDir + "/index.html";
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

	/**
	 * 创建webview
	 * */
	private void createWebview() {
		mCordovaWrap = new CordovaWrap(mContext, mRemoteContext);
		mCordovaWrap.onCreate(null);
		SharedPreferences sp;
		if (mRemoteContext != null) {
			sp = mRemoteContext.getSharedPreferences(mContext.getPackageName(),
					Context.MODE_PRIVATE);
		} else {
			sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		File sdFile = new File(SDCARD_FILES_DIR + "/"
				+ mContext.getPackageName() + "/" + updateFile);

		// 首次启动将assets目录下的html站点文件拷贝到data/data/paceagename/files/...目录下
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
							sp = mRemoteContext.getSharedPreferences(
									mContext.getPackageName(),
									Context.MODE_PRIVATE);
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
			// 如果SD卡目录下有最新下载的html站点文件，则拷贝到data/data/paceagename/files/...目录下
			ThreadUtil.execute(new Runnable() {

				@Override
				public void run() {
					SharedPreferences sp;
					if (mRemoteContext != null) {
						sp = mRemoteContext.getSharedPreferences(
								mContext.getPackageName(), Context.MODE_PRIVATE);
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
					File sdFile = new File(SDCARD_FILES_DIR);

					if (sdFile.exists()) {
						FileUtils.deleteFile(sdFile);
					}
					mHandler.sendEmptyMessage(MSG_COPY_FILE_SUCCESS);

				}
			});
		} else {
			// 直接加载html站点文件
			loadWebview();
		}
	}

	/**
	 * 开启服务进行统计事件
	 * */
	private void startStatisticsService() {
		StatisticsBaseNew.setApplicationContext(mContext);
		StatisticsExpandNew.setStatiisticsLogEnable(true);
		Intent it = new Intent();
		it.setClassName(mContext.getPackageName(),
				"com.cooee.lock.statistics.StaticClass");
		it.putExtra("EventType", "");
		mContext.startService(it);
	}

	/**
	 * 开启服务进行html站点更新
	 * */
	@SuppressLint("NewApi")
	private void startUpdateService() {
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

		// 开启服务进行html站点更新
		if (IsHaveInternet(mContext) && sdcardState) {
			Intent intent = new Intent();
			intent.setClassName(mContext.getPackageName(),
					"com.cooee.control.center.module.update.UpdateService");
			mContext.startService(intent);
		}
	}

	public void setupViews(View customView) {
		// 先布局锁屏界面
		mLockView = customView;
		addView(customView);

		// 创建控制中心的webview
		createWebview();

		// 启动常用应用服务
		startFavoritesService();

		// 加入统计代码
		startStatisticsService();

		// 开启服务进行html站点更新
		startUpdateService();

	}

	private void startFavoritesService() {

		Intent intent = new Intent();
		intent.setClassName(mContext.getPackageName(),
				FavoritesModel.FAVORITES_SERVICE_NEME);
		mContext.startService(intent);
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
