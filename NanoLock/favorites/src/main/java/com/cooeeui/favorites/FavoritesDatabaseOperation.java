package com.cooeeui.favorites;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.TrafficStats;
import android.provider.BaseColumns;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class FavoritesDatabaseOperation {

    private static final String TAG = "FavoritesProvider";
    private Context mContext;
    public static final int NO_ID = -1;
    private DatabaseHelper mOpenHelper;
    /**
     * 随着AndroidManifest中meta-data：providerAuthority动态变化
     */
    public static String AUTHORITY = "com.coco.lock2.app.Pee.FavoritesProvider";

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_FAVORITE = "favorites";

    private static final String FAVORITES_PREFERENCES_KEY = "com.lock.favorites.prefs";
    private static final String EMPTY_TABLE_APPS_CREATED = "EMPTY_TABLE_APPS_CREATED";

    /**
     * Favorites BaseColumns.
     */
    public static class Favorites implements BaseColumns {

        public static final String PACKAGE_NAME = "packageName";
        /**
         * The Intent URL of the gesture, describing what it points to. This
         * value is given to
         * {@link android.content.Intent#parseUri(String, int)} to create an
         * Intent that can be launched.
         * <P>
         * Type: TEXT
         * </P>
         */
        // public static final String INTENT = "intent";
        /**
         * The launch times of app, a factor to decide favorite. <p> Type: INTEGER </p>
         */
        public static final String LAUNCH_TIMES = "launchTimes";

        /**
         * The content:// style URL for a given row, identified by its id.
         *
         * @param id
         *            The row id.
         * @param notify
         *            True to send a notification is the content changes.
         * @return The unique content URL for the specified row.
         */
//		public static Uri getContentUri(long id) {
//			return Uri
//					.parse("content://" + AUTHORITY + "/favorites" + "/" + id);
//		}
    }

    public FavoritesDatabaseOperation(Context context) {
        mContext = context;
        mOpenHelper = new DatabaseHelper(mContext);
    }

    public Cursor query(String table, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

//		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(table);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor result = qb.query(db, projection, selection, selectionArgs, null,
                                 null, sortOrder);

        return result;
    }

    public long insert(String table, ContentValues initialValues) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = dbInsertAndCheck(mOpenHelper, db, table, null,
                                            initialValues);

        return rowId;
    }

    public int delete(String table, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.delete(table, selection, selectionArgs);

        return count;
    }

    public int update(String table, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(table, values, selection, selectionArgs);
        return count;
    }

    synchronized public void loadDefaultFavoritesIfNecessary(
        ArrayList<FavoritesAppInfo> allApps) {
        SharedPreferences sp = mContext.getSharedPreferences(
            FAVORITES_PREFERENCES_KEY, Context.MODE_PRIVATE);
        if (sp.getBoolean(EMPTY_TABLE_APPS_CREATED, false)) {
            // Populate shortcuts table with initial shortcuts
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(EMPTY_TABLE_APPS_CREATED);
            mOpenHelper.loadDefaultFavorites(allApps,
                                             mOpenHelper.getWritableDatabase(),
                                             R.xml.default_favorites);
            editor.commit();
        }
    }

    /**
     * Insert values to database with _id column check.
     */
    private static long dbInsertAndCheck(DatabaseHelper helper,
                                         SQLiteDatabase db, String table, String nullColumnHack,
                                         ContentValues values) {
        if (!values.containsKey(FavoritesDatabaseOperation.Favorites._ID)) {
            throw new RuntimeException(
                "Error: attempting to add item without specifying an id");
        }
        return db.insert(table, nullColumnHack, values);
    }

    public long generateNewAppId() {
        return mOpenHelper.generateNewAppId();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private static final String TAG_DEFAULT_FAVORITES = "default_favorites";
        private static final String TAG_FAVORITE = "favorite";
        private long mMaxAppId = -1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            initializeItemsId(getWritableDatabase());
        }

        private void initializeItemsId(SQLiteDatabase db) {
            // In the case where neither onCreate nor onUpgrade gets called, we
            // read the maxId from
            // the DB here
            if (mMaxAppId == -1) {
                mMaxAppId = initializeMaxAppId(db);
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            StringBuilder sqlString = new StringBuilder();
            sqlString.append("CREATE TABLE IF NOT EXISTS ");
            sqlString.append(TABLE_FAVORITE);
            sqlString.append("(");
            sqlString.append(Favorites._ID);
            sqlString.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
            sqlString.append(FavoritesDatabaseOperation.Favorites.PACKAGE_NAME);
            sqlString.append(" TEXT,");
            sqlString.append(Favorites.LAUNCH_TIMES);
            sqlString.append(" INTEGER");
            sqlString.append(");");
            db.execSQL(sqlString.toString());
            setFlagEmptyTablesCreated();
        }

        private void setFlagEmptyTablesCreated() {
            SharedPreferences sp = mContext.getSharedPreferences(
                FAVORITES_PREFERENCES_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(EMPTY_TABLE_APPS_CREATED, true);
            editor.commit();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            StringBuilder sqlString = new StringBuilder();
            sqlString.append("DROP TABLE IF EXISTS ");
            sqlString.append(TABLE_FAVORITE);
            db.execSQL(sqlString.toString());
            onCreate(db);
        }

        private long initializeMaxAppId(SQLiteDatabase db) {
            Cursor c = db.rawQuery("SELECT MAX(_id) FROM " + TABLE_FAVORITE,
                                   null);
            // get the result
            final int maxIdIndex = 0;
            long id = -1;
            if (c != null && c.moveToNext()) {
                id = c.getLong(maxIdIndex);
            }
            if (c != null) {
                c.close();
            }
            if (id == -1) {
                throw new RuntimeException("Error: could not query max app id");
            }
            return id;
        }

        private void loadDefaultFavorites(ArrayList<FavoritesAppInfo> allApps,
                                          SQLiteDatabase db, int workspaceResourceId) {
            ContentValues values = new ContentValues();
            String packageName;
            HashMap<String, Long> trafficMap = getTrafficInfos();
            ArrayList<String> names = new ArrayList<String>();
            try {
                XmlResourceParser parser = mContext.getResources().getXml(
                    workspaceResourceId);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                beginDocument(parser, TAG_DEFAULT_FAVORITES);
                final int depth = parser.getDepth();
                int type;
                int favoriteCount = 0;
                while (((type = parser.next()) != XmlPullParser.END_TAG || parser
                                                                               .getDepth() > depth)
                       && type != XmlPullParser.END_DOCUMENT) {
                    if (type != XmlPullParser.START_TAG) {
                        continue;
                    }
                    final String name = parser.getName();
                    TypedArray a = mContext.obtainStyledAttributes(attrs,
                                                                   R.styleable.Default);
                    // default_favorites.xml 热门apk 和 手机中流量使用较多的apk 算法量化后排序
                    if (TAG_FAVORITE.equals(name)
                        && favoriteCount < FavoritesModel.DEFAULT_FAVORITE_NUM) {
                        packageName = a
                            .getString(
                                R.styleable.Default_packageName);
                        long num = 0;
                        String pn = null;
                        for (FavoritesAppInfo app : allApps) {
                            // 手机中安装了此apk，同时又在热门列表里有
                            if (app.packageName.equals(packageName)) {
                                pn = app.packageName;
                                // 热门apk量化成num
                                num = (FavoritesModel.DEFAULT_FAVORITE_NUM - favoriteCount) * 3;
                                if (trafficMap.containsKey(pn)) {
                                    num += trafficMap.get(pn);
                                    trafficMap.put(pn, num);
                                } else {
                                    trafficMap.put(pn, num);
                                }
                                favoriteCount++;
                                break;
                            }
                        }
                    }
                    a.recycle();
                }
                for (String key : trafficMap.keySet()) {
                    names.add(key);
                }
                Collections.sort(names, new TrafficComparator(trafficMap));
                int num = 0;
                int count = 0;
                String pn = null;
                for (int i = 0; i < names.size(); i++) {
                    if (count >= FavoritesModel.DEFAULT_FAVORITE_NUM) {
                        break;
                    }
                    for (FavoritesAppInfo app : allApps) {

                        pn = app.packageName;
                        if (pn.equals(names.get(i))) {
                            num = (FavoritesModel.DEFAULT_FAVORITE_NUM - count) * 2;
                            values.clear();
                            values.put(FavoritesDatabaseOperation.Favorites._ID,
                                       generateNewAppId());
                            values.put(
                                FavoritesDatabaseOperation.Favorites.PACKAGE_NAME,
                                pn);
                            values.put(
                                FavoritesDatabaseOperation.Favorites.LAUNCH_TIMES,
                                num);
                            dbInsertAndCheck(this, db, TABLE_FAVORITE, null,
                                             values);
                            count++;
                            break;
                        }
                    }
                }
            } catch (XmlPullParserException e) {
                Log.w(TAG, "Got exception parsing default favorite.", e);
            } catch (IOException e) {
                Log.w(TAG, "Got exception parsing default favorite.", e);
            } catch (RuntimeException e) {
                Log.w(TAG, "Got exception parsing default favorite.", e);
            }
            // Update the max favorite id after we have loaded the database
            if (mMaxAppId == -1) {
                mMaxAppId = initializeMaxAppId(db);
            }
        }

        public long generateNewAppId() {
            if (mMaxAppId < 0) {
                throw new RuntimeException(
                    "Error: max app id was not initialized");
            }
            mMaxAppId += 1;
            return mMaxAppId;
        }

        private static final int TRAFFIC_MAX_NUM = 50;

        private HashMap<String, Long> getTrafficInfos() {
            final PackageManager pm = mContext.getPackageManager();
            List<PackageInfo> packinfos = pm
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
            HashMap<String, Long> trafficMap = new HashMap<String, Long>();
            HashSet<Integer> uidSet = new HashSet<Integer>();
            ArrayList<String> names = new ArrayList<String>();
            for (PackageInfo packinfo : packinfos) {
                String[] permissions = packinfo.requestedPermissions;
                if (permissions != null && permissions.length > 0) {
                    for (String permission : permissions) {
                        if ("android.permission.INTERNET".equals(permission)) {
                            int uid = packinfo.applicationInfo.uid;
                            long num = TrafficStats.getUidRxBytes(uid);
                            if (num > 150000 && !uidSet.contains(uid)) {
                                uidSet.add(uid);
                                trafficMap.put(packinfo.packageName, num);
                            }
                            break;
                        }
                    }
                }
            }
            for (String n : trafficMap.keySet()) {

                names.add(n);
            }
            Collections.sort(names, new TrafficComparator(trafficMap));
            trafficMap.clear();
            for (int i = 0; i < names.size(); i++) {
                if (i >= TRAFFIC_MAX_NUM) {
                    break;
                }
                // 流量比重
                trafficMap.put(names.get(i), (long) (TRAFFIC_MAX_NUM - i) * 7);// *
                // 7
            }
            return trafficMap;
        }

        private class TrafficComparator implements Comparator<String> {

            private HashMap<String, Long> mMap;

            public TrafficComparator(HashMap<String, Long> map) {
                mMap = map;
            }

            @Override
            public int compare(String lhs, String rhs) {
                if (mMap.get(lhs) < mMap.get(rhs)) {
                    return 1;
                }
                if (mMap.get(lhs) > mMap.get(rhs)) {
                    return -1;
                }
                return 0;
            }
        }

        private final void beginDocument(XmlPullParser parser,
                                         String firstElementName) throws XmlPullParserException,
                                                                         IOException {
            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG
                   && type != XmlPullParser.END_DOCUMENT) {
                ;
            }
            if (type != XmlPullParser.START_TAG) {
                throw new XmlPullParserException("No start tag found");
            }
            if (!parser.getName().equals(firstElementName)) {
                throw new XmlPullParserException("Unexpected start tag: found "
                                                 + parser.getName() + ", expected "
                                                 + firstElementName);
            }
        }
    }
}
