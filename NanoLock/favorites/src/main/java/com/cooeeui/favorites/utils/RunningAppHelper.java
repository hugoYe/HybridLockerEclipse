package com.cooeeui.favorites.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;

public class RunningAppHelper {

    private static ActivityManager manager = null;

    public static String getTopAppPckageName(Context context) {
        if (manager == null) {
            manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        }
        String mPackageName = null;
        if (Build.VERSION.SDK_INT >= 21) {
            // 取出第一个正在运行的进程
            RunningAppProcessInfo runningAppProcessInfo = manager
                .getRunningAppProcesses().get(0);
            // TODO 目前仅通过importance确定该进程是否正在前台运行，后期寻找被隐藏的flags字段的获取方法，进一步确定
            if (runningAppProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                // 获取该进程包含的第一个包名
                mPackageName = runningAppProcessInfo.pkgList[0];
            }
        } else {
            mPackageName = manager.getRunningTasks(1).get(0).topActivity
                .getPackageName();
        }
        return mPackageName;
    }

    // 判断是否在桌面
    // public static boolean isAtZenLauncherHomeScreen(Context context) {
    // String packageName = context.getApplicationContext().getPackageName();
    // String topAppName = getTopAppPckageName(context);
    // if (topAppName == null) {
    // return false;
    // }
    // return packageName.equals(topAppName);
    // }

}
