package com.cooeeui.favorites;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * for FavoritesService，后台统计应用使用频率， 为FavoritesService所在的进程服务。
 *
 * @author cuiqian 2015-11-13
 */
public class FavoritesData {

    public static ArrayList<FavoritesAppInfo> datas = new ArrayList<FavoritesAppInfo>();
    public static ArrayList<FavoritesAppInfo> mAppsAll = new ArrayList<FavoritesAppInfo>();
    public static ArrayList<AppInfo> mFavoritesApps = new ArrayList<AppInfo>();
    public static ArrayList<String> mFavoritesPackageName = new ArrayList<String>();
    public static boolean isUpdate = false;
    public static boolean isNewAdd = false;

    public static FavoritesAppInfo getAppInfo(String name) {
        for (FavoritesAppInfo app : mAppsAll) {
            String pn = app.packageName;
            if (pn.equals(name)) {
                return app;
            }
        }
        return null;
    }

    //
    public static void filterApps(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(
            mainIntent, 0);
        if (apps == null || apps.isEmpty()) {
            return;
        }
        for (int i = 0; i < apps.size(); i++) { // filter home app
            ResolveInfo app = apps.get(i);
            if (app.activityInfo != null) {
                String pkgName = app.activityInfo.applicationInfo.packageName;
                FavoritesAppInfo info = getAppInfo(pkgName);
                if (info != null) {
                    mAppsAll.remove(info);
                }
            }
        }
        for (FavoritesAppInfo appInfo : mAppsAll) {// filter notice app
            if (appInfo.packageName != null
                && FavoritesModel.isNoticeApp(appInfo.packageName)) {
                mAppsAll.remove(appInfo);
                break;
            }
        }
        for (FavoritesAppInfo appInfo : mAppsAll) {
            if (appInfo.packageName != null
                && FavoritesModel.isIconUIApp(appInfo.packageName)) {
                mAppsAll.remove(appInfo);
                break;
            }
        }

        for (FavoritesAppInfo appInfo : mAppsAll) {// filter own
            if (appInfo.packageName != null
                && FavoritesModel.isOwnApp(appInfo.packageName)) {
                mAppsAll.remove(appInfo);
                break;
            }
        }
    }

    //
    public static FavoritesAppInfo getDatasApp(String name) {
        for (FavoritesAppInfo app : datas) {
            String pn = app.packageName;
            if (pn.equals(name)) {
                return app;
            }
        }
        return null;
    }

    private static boolean isNew() {
        int num = datas.size();
        if (num <= FavoritesModel.DEFAULT_FAVORITE_NUM) {
            return false;
        }

        for (int i = FavoritesModel.DEFAULT_FAVORITE_NUM; i < num; i++) {
            if (datas.get(i).launchTimes > datas
                .get(FavoritesModel.DEFAULT_FAVORITE_NUM - 1).launchTimes) {
                return true;
            }
        }

        return false;
    }

    public static void updateTimes(String name) {
        FavoritesAppInfo app = getDatasApp(name);
        if (app == null) {
            app = getAppInfo(name);
            if (app != null) {
                app.launchTimes++;
                if (datas.size() < FavoritesModel.DEFAULT_FAVORITE_NUM) {
                    isNewAdd = true;
                }
                datas.add(app);
            }
        } else {
            app.launchTimes++;
            if (isNew()) {
                isNewAdd = true;
            }
            isUpdate = true;
        }
    }

    public static void add(FavoritesAppInfo app) {
        if (getDatasApp(app.packageName) == null) {
            datas.add(app);
        }
    }

    public static void clear() {
        datas.clear();
        isUpdate = false;
        isNewAdd = false;
    }

    public static void dayDecrease() {
        FavoritesAppInfo app = null;
        int num = datas.size();
        int max = FavoritesModel.DEFAULT_FAVORITE_NUM + 1;
        if (num < FavoritesModel.DEFAULT_FAVORITE_NUM) {
            max = num + 1;
        }
        for (int i = 0; i < num; i++) {
            app = datas.get(i);
            app.launchTimes -= 3;
            if (i < FavoritesModel.DEFAULT_FAVORITE_NUM) {
                if (app.launchTimes < max - i) {
                    app.launchTimes = max - i;
                }
            } else if (app.launchTimes < 1) {
                app.launchTimes = 1;
            }
        }
    }

    public static void saveFavoritesToDatabase(Context context) {
        ArrayList<FavoritesAppInfo> apps = new ArrayList<FavoritesAppInfo>();
        for (FavoritesAppInfo app : mAppsAll) {
            if (app.launchTimes > 0) {
                apps.add(app);
            }
        }
        FavoritesModel.saveFavoritesToDatabase(context, apps);
    }

    /**
     * max num is 8
     */
    public static ArrayList<AppInfo> getFavorityAppInfo(int num) {
        mFavoritesApps.clear();
        for (int i = 0; i < datas.size() && i < num; i++) {
            mFavoritesApps.add(datas.get(i));
        }
        return mFavoritesApps;
    }

    /**
     * max num is 8
     */
    public static List<String> getFavorityPackageInfo() {
        mFavoritesPackageName.clear();
        for (int i = 0; i < datas.size() && i < FavoritesModel.DEFAULT_FAVORITE_NUM; i++) {
            mFavoritesPackageName.add(datas.get(i).packageName);
        }
        return mFavoritesPackageName;
    }

    public static void sort() {
        Collections.sort(datas, new DatasComparator());
    }

    public static class DatasComparator implements Comparator<FavoritesAppInfo> {

        @Override
        public int compare(FavoritesAppInfo lhs, FavoritesAppInfo rhs) {
            if (lhs.launchTimes > rhs.launchTimes) {
                return -1;
            } else if (lhs.launchTimes < rhs.launchTimes) {
                return 1;
            }
            return 0;
        }
    }
}
