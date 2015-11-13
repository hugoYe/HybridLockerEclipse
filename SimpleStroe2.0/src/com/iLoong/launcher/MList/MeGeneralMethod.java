package com.iLoong.launcher.MList;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cool.sdk.MicroEntry.MicroEntry;
import cool.sdk.download.CoolDLMgr;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public class MeGeneralMethod {

	// static TimerTask taskkillProcess = null;
	// static Timer KillProcessTimer = null;
	// public static void KillProcessIfNeed(
	// Context context )
	// {
	// if( !IsDownloadTaskRunning( context ) )
	// {
	// MELOG.v( "ME_RTFSC" , "KillProcessIfNeed schedule " );
	// MeGeneralMethod.stopMeDLProtectionService(
	// context.getApplicationContext() );
	// taskkillProcess = new KillProcessTimerTask( context );
	// KillProcessTimer = new Timer();
	// KillProcessTimer.schedule( taskkillProcess , 6000 );
	// }
	// }
	//
	// public static void CanelKillProcess()
	// {
	// MELOG.v( "ME_RTFSC" , "CanelKillProcess   " );
	// if( null != taskkillProcess && null != KillProcessTimer )
	// {
	// taskkillProcess.cancel();
	// KillProcessTimer.cancel();
	// taskkillProcess = null;
	// KillProcessTimer = null;
	// }
	// }
	public static boolean InstallApk(Context context, String apkPath) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(apkPath)),
					"application/vnd.android.package-archive");
			context.startActivity(intent);

			return true;
		} catch (Exception ex) {
			// ex.printStackTrace();
			return false;
		}
	}

	public static boolean IsForegroundRunning(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> mRunningService = mActivityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo amService : mRunningService) {
			if (amService.pid == android.os.Process.myPid()) {
				MELOG.v("ME_RTFSC ",
						"---IsForegroundRunning:"
								+ (amService.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND));
				return amService.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
			}
		}
		MELOG.v("ME_RTFSC ", "---IsForegroundRunning:false");
		return false;
	}

	public static boolean IsDownloadTaskRunning(Context context) {
		for (int i = 0; i <= 4; i++) {
			CoolDLMgr dlmgr = MicroEntry.CoolDLMgr(context, "M", i);
			if (dlmgr.dl_mgr.getTaskCount() > 0) {
				MELOG.v("ME_RTFSC ", "---IsDownloadTaskRunning:true");
				return true;
			}
		}
		MELOG.v("ME_RTFSC ", "---IsDownloadTaskRunning:false");
		return false;
	}

	public static boolean IsWifiConnected(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo info = connMgr.getActiveNetworkInfo();
		if (info != null && info.isAvailable()
				&& info.getType() == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected();
		}
		return false;
	}
	// public static void startMeDLProtectionService(
	// Context mContext )
	// {
	// Intent mIntent = new Intent( mContext , MeDLProtectionService.class );
	// mContext.startService( mIntent );
	// }
	//
	// public static void stopMeDLProtectionService(
	// Context mContext )
	// {
	// Intent mIntent = new Intent( mContext , MeDLProtectionService.class );
	// mContext.stopService( mIntent );
	// }
}
