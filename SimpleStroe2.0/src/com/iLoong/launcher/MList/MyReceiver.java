package com.iLoong.launcher.MList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cool.sdk.MicroEntry.MicroEntry;
import cool.sdk.MicroEntry.MicroEntryHelper;
import cool.sdk.download.CoolDLMgr;
import cool.sdk.download.manager.dl_info;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Message;
import android.webkit.WebView;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

	Context mContent = null;
	String pkgName = null;

	public boolean IsMeForeground(Context context) {
		int mypid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> mRunningService = mActivityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo amService : mRunningService) {
			// modify for check if myself presses by pid
			// if( "com.cooee.unilauncher".equals( amService.pid ) )
			MELOG.v("ME_RTFSC", "mypid:" + mypid + "  amService.pid:"
					+ amService.pid + "  amService.importance:"
					+ amService.importance);
			if (mypid == amService.pid) {
				return amService.importance != RunningAppProcessInfo.IMPORTANCE_EMPTY;
			}
		}
		return false;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContent = context;
		MELOG.v("ME_RTFSC", intent.getAction()
				+ "====MyReceiver onReceive =====  ");
		// MELOG.v( "ME_RTFSC" , "IsMeForeground = " + IsMeForeground( context )
		// );
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
				|| intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
			// 判断是不是微入口的APK安装成功
			pkgName = intent.getDataString().substring(8);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MicroEntryHelper microEntryHelper = MicroEntryHelper
							.getInstance(mContent);
					ArrayList<Integer> PkgAddedEntryIDList = new ArrayList<Integer>();
					// 微入口从1开始到4
					for (int i = 0; i <= 4; i++) {
						int EntryId = microEntryHelper.getInt(pkgName
								+ MeServiceType.MEApkOnSucess + i, -1);
						if (-1 != EntryId) {
							PkgAddedEntryIDList.add(EntryId);
							microEntryHelper.setValue(pkgName
									+ MeServiceType.MEApkOnSucess + i, -1);
						}
					}
					MELOG.v("ME_RTFSC", "PkgAddedEntryIDList:"
							+ PkgAddedEntryIDList);
					if (!PkgAddedEntryIDList.isEmpty()
							|| IsMeForeground(mContent)) {
						// 通过MeServiceType 处理安装成功事件
						microEntryHelper.setValue(pkgName
								+ MeServiceType.MeApkOnInstalled, "TRUE");
						Intent MePkgAddedIntent = new Intent(mContent,
								MEServiceActivity.class);
						MePkgAddedIntent
								.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MePkgAddedIntent.putExtra("MeServiceType",
								MeServiceType.MeApkOnPkgInstalled);
						MePkgAddedIntent.putIntegerArrayListExtra(
								"PkgAddedEntryIDList", PkgAddedEntryIDList);
						MePkgAddedIntent.putExtra("PkgName", pkgName);
						mContent.startActivity(MePkgAddedIntent);
					}
				}
			}).start();
		}
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
			pkgName = intent.getDataString().substring(8);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MicroEntryHelper microEntryHelper = MicroEntryHelper
							.getInstance(mContent);
					if ("TRUE".equals(microEntryHelper.getString(pkgName
							+ MeServiceType.MeApkOnInstalled))
							|| IsMeForeground(mContent)) {
						microEntryHelper.setValue(pkgName
								+ MeServiceType.MeApkOnInstalled, "FALSE");
						Intent MePkgRemoveIntent = new Intent(mContent,
								MEServiceActivity.class);
						MePkgRemoveIntent
								.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MePkgRemoveIntent.putExtra("MeServiceType",
								MeServiceType.MeApkOnPkgUninstall);
						MePkgRemoveIntent.putExtra("PkgName", pkgName);
						mContent.startActivity(MePkgRemoveIntent);
					}
				}
			}).start();
		}
		if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!MeGeneralMethod.IsWifiConnected(mContent)) {
						return;
					}

					ArrayList<Integer> entryIDLsit = new ArrayList<Integer>();
					HashMap<Integer, ArrayList<String>> Listmap = new HashMap<Integer, ArrayList<String>>();

					for (int i = 0; i <= 4; i++) {
						CoolDLMgr dlmgr = MicroEntry
								.CoolDLMgr(mContent, "M", i);
						List<dl_info> DlInfoList = dlmgr
								.ResGetTaskListNeedDownload();
						ArrayList<String> pkgNameLsit = new ArrayList<String>();

						// MELOG.v( "ME_RTFSC" ,
						// " DownloadApkNeedDownload 2222 " );
						if (null != DlInfoList && !DlInfoList.isEmpty()) {
							for (dl_info dl_info : DlInfoList) {
								String pkgName = null;
								if (null != (String) dl_info.getValue("r4")) {
									pkgName = (String) dl_info.getValue("r4");
								} else {
									pkgName = (String) dl_info.getValue("p2");
								}
								if (null != pkgName) {

									// MeApkDlMgr.ReStartDownload( curShowType ,
									// pkgName , WebMainApkDownloadCallBack );
									pkgNameLsit.add(pkgName);
								}
							}
						}
						if (null != pkgNameLsit && !pkgNameLsit.isEmpty()) {
							MELOG.v("ME_RTFSC",
									"DownloadApkNeedDownload: pkgNameLsit:"
											+ pkgNameLsit);
							entryIDLsit.add(i);
							Listmap.put(i, pkgNameLsit);
						}
					}
					if (null != entryIDLsit && null != Listmap
							&& !entryIDLsit.isEmpty() && !Listmap.isEmpty()) {
						Intent mIntent = new Intent(mContent,
								MEServiceActivity.class);
						mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mIntent.putExtra("MeServiceType",
								MeServiceType.MEApkReStartAll);
						mIntent.putExtra("moudleName", "M");
						mIntent.putIntegerArrayListExtra("entryIDList",
								entryIDLsit);
						for (int entryID : entryIDLsit) {
							mIntent.putStringArrayListExtra("PkgNameList"
									+ entryID, Listmap.get(entryID));
						}
						mContent.startActivity(mIntent);
					}

				}
			}).start();
		}
	}
}
