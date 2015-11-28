package com.coco.lock.favorites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * for FavoritesApi， 锁屏进程访问。为 创建FavoritesApi的进程 服务
 * FavoritesService, FavoritesData运行在锁屏应用进程，负责后台统计数据。
 * FavoritesApi，FavoritesDataKeyguard在无移植包的情况下运行在锁屏应用进程，负责最终使用的数据。
 * FavoritesApi，FavoritesDataKeyguard在有移植包的情况下运行在锁屏android keyguard进程，负责最终使用的数据。
 * FavoritesData， FavoritesDataKeyguard在有移植包的情况下，属于跨进程运行。
 * @author cuiqian 2015-11-13
 */
public class FavoritesDataKeyguard {
	public static ArrayList<FavoritesAppInfo> datas = new ArrayList<FavoritesAppInfo>();
	public static ArrayList<AppInfo> mAppsFavorites = new ArrayList<AppInfo>();

	public static FavoritesAppInfo getDatasApp(String name) {
		for (FavoritesAppInfo app : datas) {
			String pn = app.packageName;
			if (pn.equals(name)) {
				return app;
			}
		}
		return null;
	}

	public static void add(FavoritesAppInfo app) {
		if (getDatasApp(app.packageName) == null) {
			datas.add(app);
		}
	}

	public static void clear() {
		datas.clear();
	}

	/**
	 * max num is FavoritesModel.DEFAULT_FAVORITE_NUM
	 * */
	public static ArrayList<AppInfo> getFavorityAppInfo(int num) {
		mAppsFavorites.clear();
		for (int i = 0; i < datas.size() && i < num; i++) {
			mAppsFavorites.add(datas.get(i));
		}
		return mAppsFavorites;
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
