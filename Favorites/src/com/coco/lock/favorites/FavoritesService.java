package com.coco.lock.favorites;

import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.coco.lock.utils.RunningAppHelper;

import com.coco.lock.favorites.aidl.*;

public class FavoritesService extends Service {

	private static String TAG = "FavoritesService";
	private static Context mContext;
	private PowerManager mPm = null;
	/**
	 * for FavoritesService thread
	 */
	private static boolean isRun;
	/**
	 * run process package name, the last moment
	 */
	private static String lastName = null;
	/**
	 * load all app info when first start
	 */
	private static boolean mAllAppsLoaded = false;
	/**
	 * the timer, add per seconds. link TIME_INTERVAL_FLUSH
	 */
	private static int times = 0;
	/**
	 * String: packageName, Long: times
	 */
	public static HashMap<String, Long> countMap = new HashMap<String, Long>();
	/**
	 * data changed
	 */
	public static boolean isDataChanged = false;
	/**
	 * last packageName equals current packageName launchTimes++ per 600s
	 */
	private static final int TIME_INTERVAL_ONCE = 600;
	/**
	 * reset the timer
	 */
	private static final int TIME_INTERVAL_FLUSH = 600;// 配置开关 600
	/**
	 * Database operation and BroadcastReceiver
	 */
	private FavoritesModel mFavoritesModel = null;

	private static final int MAX_LIST_SERVICE = 50;
	
	@Override
	public IBinder onBind(Intent intent) {
		return new FavoritesServiceAIDLImpl();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mContext = getApplicationContext();
		Log.i(TAG, "mContext: " + mContext);
		//停止其他锁屏apk的常用服务
		stopServiceRunning(mContext, FavoritesModel.FAVORITES_SERVICE_NEME);
		
		mPm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		
		mFavoritesModel = new FavoritesModel(mContext);
		registerReceivers();
		
		isRun = true;
		// 使服务成为前台进程，这样减少应用在后台被kill的几率
		// startForeground(410402, new Notification());//?
		if (!mAllAppsLoaded) {
			loadAllApps();
			mAllAppsLoaded = true;
		}
		
		if (isDataChanged) {
			countMap.clear();
			isDataChanged = false;
		}
		new Thread("FavoritesService thread") {

			@Override
			public void run() {
				while (isRun) {
					// screen off to sleep
					if (!mPm.isScreenOn()) {
						if (lastName != null
								&& FavoritesData.getAppInfo(lastName) != null) {
							lastName = null;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							//
						}
						continue;
					}

					// launchTimes

					String pn = RunningAppHelper.getTopAppPckageName(mContext);

					if (pn == null) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							//
						}
						continue;
					}
					if (pn != null) {
						if (FavoritesData.getAppInfo(pn) != null) {
							if (pn.equals(lastName)) {
								updateTimes(pn);
							} else {
								FavoritesData.updateTimes(pn);
							}
						}
						lastName = pn;
						// launchTimes change
						if (FavoritesData.isNewAdd) {
							Intent intent = new Intent(
									FavoritesModel.ACTION_FAVOTITE_UPDATE);
							sendBroadcast(intent);
							FavoritesData.isNewAdd = false;
						}
						if (times > TIME_INTERVAL_FLUSH) {
							times = 0;
							if (FavoritesData.isUpdate) {
								Intent intent = new Intent(
										FavoritesModel.ACTION_FAVOTITE_UPDATE);
								sendBroadcast(intent);
								FavoritesData.isUpdate = false;
							}
						}
						// run Once per 1000 ms
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							continue;
						}
						// the Timer
						times++;
					}
					// day or month or year change
					if (isDataChanged) {
						countMap.clear();
						isDataChanged = false;
					}
				}
			}
		}.start();
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
				if (!(packageName.equals(mContext.getPackageName()))){
					Log.i(TAG, "stop favorites service Belong to package: " + packageName);				
					Intent intent = new Intent();
					intent.setClassName(packageName, className);
					context.stopService(intent);
				}				
			}
		}
	}

	private void registerReceivers() {
		// action for save to database
		IntentFilter filter = new IntentFilter();
		filter.addAction(FavoritesModel.ACTION_FAVOTITE_UPDATE);
		mContext.registerReceiver(mFavoritesModel, filter);
		filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		// T卡？
		filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
		filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		filter.addDataScheme("package");
		mContext.registerReceiver(mFavoritesModel, filter);
	}

	/**
	 * 1. 获取手机中所有app 2. 解析xml中配置的热门app 3. 解析数据库中的app
	 * 
	 * @author cuiqian 2015-10-28
	 */
	private void loadAllApps() {
		// load all apps
		final PackageManager packageManager = mContext.getPackageManager();
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> apps = packageManager.queryIntentActivities(
				mainIntent, 0);
		// Fail if we don't have any apps
		if (apps == null || apps.isEmpty()) {
			return;
		}
		// Create the ApplicationInfos
		FavoritesData.mAppsAll.clear();
		for (int i = 0; i < apps.size(); i++) {
			ResolveInfo app = apps.get(i);
			if (app != null) {
				final ActivityInfo activityInfo = app.activityInfo;
				FavoritesAppInfo appInfo = new FavoritesAppInfo();
				appInfo.packageName = activityInfo.packageName;
				appInfo.appName = activityInfo.loadLabel(packageManager)
						.toString();
				appInfo.appIntent = packageManager
						.getLaunchIntentForPackage(activityInfo.packageName);
				appInfo.appIcon = activityInfo.loadIcon(packageManager);
				FavoritesData.mAppsAll.add(appInfo);
			}
		}
		FavoritesData.filterApps(mContext);
		// get app from R.xml.default_favorites, quantization change to
		// launchTimes, then save to Db
		FavoritesModel.getFavoritesProvider().loadDefaultFavoritesIfNecessary(
				FavoritesData.mAppsAll);
		// Db data to FavoritesData.datas
		FavoritesModel.loadFavoritesFromDb();

		Intent intent = new Intent(FavoritesModel.ACTION_LOAD_DATABASE_SUCCESS);
		mContext.sendBroadcast(intent);
	}

	

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		if(mFavoritesModel != null){
			mContext.unregisterReceiver(mFavoritesModel);
			mFavoritesModel = null;
		}
		
		stopForeground(true);
		super.onDestroy();
		isRun = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	public void updateTimes(String name) {
		if (countMap.containsKey(name)) {
			long c = countMap.get(name);
			c++;
			if (c % TIME_INTERVAL_ONCE == 0) {
				FavoritesData.updateTimes(name);
			}
			countMap.put(name, c);
		} else {
			countMap.put(name, 1l);
		}
	}
	
	public class FavoritesServiceAIDLImpl extends IFavoritesService.Stub{

		@Override
		public List<String> getFavoritesMap() throws RemoteException {
			return FavoritesData.getFavorityPackageInfo();
		}
		
	}
}
