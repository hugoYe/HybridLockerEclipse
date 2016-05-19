package com.cooeeui.favorites;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class FavoritesModel extends BroadcastReceiver {

    private static String TAG = "FavoritesModel";
    public static int DEFAULT_FAVORITE_NUM = 16;
    public static String NOTICE_PACKAGE_NAME = "com.cooeeui.notificationservice";
    public static final String ACTION_FAVOTITE_UPDATE = "com.cooee.lock.favorite.update";
    public static String OWN_PACKAGE_NAME = "com.coco.lock2.app.Pee";
    private static FavoritesDatabaseOperation mFavoritesDbOperation = null;

    public static String FAVORITES_SERVICE_NEME = "com.coco.lock.favorites.FavoritesService";

    public static final String ACTION_LOAD_DATABASE_SUCCESS = "com.cooee.load.database.success";
    public static final String ACTION_LOAD_FAVOTITE_SUCCESS = "com.cooee.load.favorite.success";

    private static Handler mHandler = new Handler();
    private Context mContext = null;
    private int mYear;
    private int mMonth;
    private int mDay;

    public FavoritesModel(Context context) {
        mContext = context;
        OWN_PACKAGE_NAME = mContext.getPackageName();
        mFavoritesDbOperation = new FavoritesDatabaseOperation(mContext);
    }

    public static FavoritesDatabaseOperation getFavoritesProvider() {
        return mFavoritesDbOperation;
    }

    public static void loadFavoritesFromDb() {
        FavoritesData.clear();
        String where = " launchTimes > 0 ";
        final Cursor c = mFavoritesDbOperation
            .query(FavoritesDatabaseOperation.TABLE_FAVORITE, null, where, null,
                   null);
        try {
            final int idIndex = c
                .getColumnIndexOrThrow(FavoritesDatabaseOperation.Favorites._ID);
            final int packageIndex = c
                .getColumnIndexOrThrow(FavoritesDatabaseOperation.Favorites.PACKAGE_NAME);
            final int launchTimesIndex = c
                .getColumnIndexOrThrow(FavoritesDatabaseOperation.Favorites.LAUNCH_TIMES);
            FavoritesAppInfo appInfo;
            String packageDescription;
            long id;
            while (c.moveToNext()) {
                try {
                    id = c.getLong(idIndex);
                    packageDescription = c.getString(packageIndex);
                    appInfo = FavoritesData.getAppInfo(packageDescription);
                    if (appInfo != null) {
                        appInfo.id = id;
                        appInfo.launchTimes = c.getLong(launchTimesIndex);
                        FavoritesData.add(appInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static void saveFavoritesToDatabase(Context context,
                                               final ArrayList<FavoritesAppInfo> items) {
        for (int i = 0; i < items.size(); i++) {
            FavoritesAppInfo appInfo = items.get(i);
            if (appInfo.id == FavoritesDatabaseOperation.NO_ID) {
                addItemToDatabase(context, appInfo);
            } else {
                updateItemInDatabase(context, appInfo);
            }
        }
    }

    /**
     * Add the specified item to the database .
     */
    public static void addItemToDatabase(Context context,
                                         final FavoritesAppInfo appinfo) {
        if (appinfo == null || getFavoritesProvider() == null) {
            return;
        }
        final ContentValues values = new ContentValues();
        appinfo.id = mFavoritesDbOperation.generateNewAppId();
        values.put(FavoritesDatabaseOperation.Favorites._ID, appinfo.id);
        values.put(FavoritesDatabaseOperation.Favorites.PACKAGE_NAME,
                   appinfo.packageName);
        values.put(FavoritesDatabaseOperation.Favorites.LAUNCH_TIMES,
                   appinfo.launchTimes);
        Runnable r = new Runnable() {

            @Override
            public void run() {
                mFavoritesDbOperation.insert(FavoritesDatabaseOperation.TABLE_FAVORITE, values);
            }
        };
        mHandler.post(r);
    }

    /**
     * Update an item to the database in a specified container.
     */
    public static void updateItemInDatabase(Context context,
                                            final FavoritesAppInfo appinfo) {
        if (appinfo == null || getFavoritesProvider() == null) {
            return;
        }
        final ContentValues values = new ContentValues();
        values.put(FavoritesDatabaseOperation.Favorites.LAUNCH_TIMES,
                   appinfo.launchTimes);
        Runnable r = new Runnable() {

            public void run() {
                mFavoritesDbOperation.update(FavoritesDatabaseOperation.TABLE_FAVORITE, values,
                                             FavoritesDatabaseOperation.Favorites._ID + " = ?",
                                             new String[]{Long.toString(appinfo.id)});
            }
        };
        mHandler.post(r);
    }

    /**
     * Removes the specified item from the database
     */
    public static void deleteItemFromDatabase(Context context,
                                              final FavoritesAppInfo item) {
        if (item == null || getFavoritesProvider() == null) {
            return;
        }
        Runnable r = new Runnable() {

            public void run() {
                mFavoritesDbOperation.delete(FavoritesDatabaseOperation.TABLE_FAVORITE,
                                             FavoritesDatabaseOperation.Favorites._ID + " = ?",
                                             new String[]{Long.toString(item.id)});
            }
        };
        mHandler.post(r);
    }

    public static boolean isNoticeApp(String name) {
        if (NOTICE_PACKAGE_NAME.equals(name)) {
            return true;
        }
        return false;
    }

    public static boolean isOwnApp(String name) {
        if (OWN_PACKAGE_NAME.equals(name)) {
            return true;
        }
        return false;
    }

    public static String ICONUI_PACKAGE_NAME = "com.cooeeui.iconui";

    public static boolean isIconUIApp(String name) {
        if (ICONUI_PACKAGE_NAME.equals(name)) {
            return true;
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
            || Intent.ACTION_PACKAGE_REMOVED.equals(action)
            || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            Log.i(TAG, "action: " + action);
            final String packageName = intent.getData().getSchemeSpecificPart();
            final boolean replacing = intent.getBooleanExtra(
                Intent.EXTRA_REPLACING, false);
            int op = PackageUpdatedTask.OP_NONE;
            if (packageName == null || packageName.length() == 0) {
                // they sent us a bad intent
                return;
            }
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                op = PackageUpdatedTask.OP_UPDATE;
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                if (!replacing) {
                    op = PackageUpdatedTask.OP_REMOVE;
                }
            } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                if (!replacing) {
                    op = PackageUpdatedTask.OP_ADD;
                } else {
                    op = PackageUpdatedTask.OP_UPDATE;
                }
            }
            mHandler.post(new PackageUpdatedTask(op,
                                                 new String[]{packageName}));
        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
            Log.i(TAG, "ACTION_EXTERNAL_APPLICATIONS_AVAILABLE");
            String[] packages = intent
                .getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            int op = PackageUpdatedTask.OP_ADD;
            mHandler.post(new PackageUpdatedTask(op, packages));
        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE
            .equals(action)) {
            Log.i(TAG, "ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE");
            String[] packages = intent
                .getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            int op = PackageUpdatedTask.OP_REMOVE;
            mHandler.post(new PackageUpdatedTask(op, packages));
        } else if (Intent.ACTION_TIME_TICK.equals(action)
                   || Intent.ACTION_TIME_CHANGED.equals(action)
                   || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
            timeChanged();
        } else if (FavoritesModel.ACTION_FAVOTITE_UPDATE.equals(action)) {
            Log.i(TAG, "ACTION_FAVOTITE_UPDATE");
            FavoritesData.saveFavoritesToDatabase(mContext);
            FavoritesData.sort();
        }
    }

    private void timeChanged() {
        boolean isChanged = false;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (mYear != year || mMonth != month || mDay != day) {
            mYear = year;
            mMonth = month;
            mDay = day;
            isChanged = true;
            FavoritesService.isDataChanged = true;
        }
        if (isChanged) {
            FavoritesData.dayDecrease();
        }
    }

    private class PackageUpdatedTask implements Runnable {

        int mOp;
        String[] mPackages;
        public static final int OP_NONE = 0;
        public static final int OP_ADD = 1;
        public static final int OP_UPDATE = 2;
        public static final int OP_REMOVE = 3; // uninstlled
        private PackageManager mPackageManager = null;

        public PackageUpdatedTask(int op, String[] packages) {
            mOp = op;
            mPackages = packages;
            mPackageManager = mContext.getPackageManager();
        }

        public void run() {
            final String[] packages = mPackages;
            final int N = packages.length;
            switch (mOp) {
                case OP_ADD:
                    for (int i = 0; i < N; i++) {
                        if (isOwnApp(packages[i])) {
                            continue;
                        }
                        addApp(packages, i);
                    }
                    break;
                case OP_UPDATE:
                    for (int i = 0; i < N; i++) {
                        if (isOwnApp(packages[i])) {
                            continue;
                        }
                        updateApp(packages, i);
                    }
                    break;
                case OP_REMOVE:
                    for (int i = 0; i < N; i++) {
                        if (isOwnApp(packages[i])) {
                            continue;
                        }
                        removeApp(packages, i);
                    }
            }
        }

        private FavoritesAppInfo removeApp(final String[] packages, int i) {
            FavoritesAppInfo appInfo = FavoritesData.getAppInfo(packages[i]);
            if (appInfo != null) {
                FavoritesData.mAppsAll.remove(appInfo);
            }
            appInfo = FavoritesData.getDatasApp(packages[i]);
            if (appInfo != null) {
                FavoritesData.datas.remove(appInfo);
            }
            deleteItemFromDatabase(mContext, appInfo);
            return appInfo;
        }

        private void updateApp(final String[] packages, int i) {
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = mPackageManager.getApplicationInfo(
                    packages[i], 0);
                FavoritesAppInfo appInfo = FavoritesData
                    .getAppInfo(packages[i]);
                if (appInfo != null) {
                    appInfo.appName = applicationInfo
                        .loadLabel(mPackageManager).toString();
                    appInfo.appIntent = mPackageManager
                        .getLaunchIntentForPackage(applicationInfo.packageName);
                    appInfo.appIcon = applicationInfo.loadIcon(mPackageManager);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void addApp(final String[] packages, int i) {
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = mPackageManager.getApplicationInfo(
                    packages[i], 0);
                FavoritesAppInfo appInfo = new FavoritesAppInfo();
                appInfo.packageName = applicationInfo.packageName;
                appInfo.appName = applicationInfo.loadLabel(mPackageManager)
                    .toString();
                appInfo.appIntent = mPackageManager
                    .getLaunchIntentForPackage(applicationInfo.packageName);
                appInfo.appIcon = applicationInfo.loadIcon(mPackageManager);
                FavoritesData.mAppsAll.add(appInfo);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
