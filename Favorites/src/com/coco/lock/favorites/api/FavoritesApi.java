package com.coco.lock.favorites.api;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.coco.lock.favorites.AppInfo;
import com.coco.lock.favorites.FavoritesAppInfo;
import com.coco.lock.favorites.FavoritesModel;
import com.coco.lock.favorites.aidl.IFavoritesService;

public class FavoritesApi {
	private static String TAG = "FavoritesApi";

	private Context mContext = null;
	private ArrayList<AppInfo> mAppInfoList = new ArrayList<AppInfo>();
	private PackageManager mPackageManager = null;
    private IFavoritesService iFavoritesService =null;  
    
    private ServiceConnection serviceConnection = new ServiceConnection() {  
        
        @Override  
        public void onServiceDisconnected(ComponentName name) { 
              Log.i(TAG, "onServiceDisconnected");
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) { 
        	Log.i(TAG, "onServiceConnected");
        	iFavoritesService = IFavoritesService.Stub.asInterface(service); 
        	refreshList();	
        } 
    };  
	private void refreshList() {
		if (iFavoritesService == null){
			Log.i(TAG, "refreshList iFavoritesService null");
			return;
		}
		try {
			List<String> favoritesPackageList = iFavoritesService.getFavoritesMap();
			mAppInfoList.clear();
			for (String packageName : favoritesPackageList){
				ApplicationInfo applicationInfo;
				try {
					applicationInfo = mPackageManager.getApplicationInfo(
							packageName, 0);
					FavoritesAppInfo appInfo = new FavoritesAppInfo();
					appInfo.packageName = applicationInfo.packageName;
					appInfo.appName = applicationInfo.loadLabel(mPackageManager)
							.toString();
					appInfo.appIntent = mPackageManager
							.getLaunchIntentForPackage(applicationInfo.packageName);
					appInfo.appIcon = applicationInfo.loadIcon(mPackageManager);
					mAppInfoList.add(appInfo);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		Intent intent = new Intent(
				FavoritesModel.ACTION_LOAD_FAVOTITE_SUCCESS);
		mContext.sendBroadcast(intent);
	} 
	public FavoritesApi(Context context) {
		mContext = context;
		mPackageManager = mContext.getPackageManager();
		
		String actionFavorites = mContext.getPackageName() + ".favorites";
		
		mContext.bindService(new Intent(actionFavorites), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	public void init() {

		IntentFilter filter = new IntentFilter();
		filter.addAction(FavoritesModel.ACTION_LOAD_DATABASE_SUCCESS);
		mContext.registerReceiver(mFavoriteReceiver, filter);
	}

	public void onDestroy() {
		if (mFavoriteReceiver != null) {
			mContext.unregisterReceiver(mFavoriteReceiver);
			mFavoriteReceiver = null;
		}
		Log.i("cuiqian", "pee onDestroy");
		mContext.unbindService(serviceConnection);
	}

	private BroadcastReceiver mFavoriteReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			refreshList();
		}
	};

	public ArrayList<AppInfo> getFavoriteApp() {
		return mAppInfoList;
	}
}
