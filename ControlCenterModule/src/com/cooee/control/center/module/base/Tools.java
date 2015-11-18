/***/
package com.cooee.control.center.module.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author gaominghui 2015年6月20日
 */
public class Tools {

	/**
	 * 获取近期任务列表
	 */
	private static final int NUM_BUTTONS = 7;
	private static final int MAX_RECENT_TASKS = NUM_BUTTONS * 2;

	public static ArrayList<AppInfo> recentTasks(Context context) {
		ArrayList<AppInfo> appInfoList = new ArrayList<AppInfo>();
		final PackageManager pm = context.getPackageManager();
		final ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 拿到最近使用的应用的信息列表
		final List<ActivityManager.RecentTaskInfo> recentTasks = am
				.getRecentTasks(MAX_RECENT_TASKS,
						ActivityManager.RECENT_IGNORE_UNAVAILABLE);
		// 自制一个home activity info，用来区分
		ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(
				Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);
		int index = 0;
		int numTasks = recentTasks.size();
		// 开始初始化每个任务的信息
		for (int i = 0; i < numTasks && (index < NUM_BUTTONS); ++i) {
			final ActivityManager.RecentTaskInfo info = recentTasks.get(i);
			// 复制一个任务的原始Intent
			Intent intent = new Intent(info.baseIntent);
			if (info.origActivity != null) {
				intent.setComponent(info.origActivity);
			}
			// 跳过home activity
			if (homeInfo != null) {
				if (homeInfo.packageName.equals(intent.getComponent()
						.getPackageName())
						&& homeInfo.name.equals(intent.getComponent()
								.getClassName())) {
					continue;
				}
			}

			if (intent.getComponent().getPackageName()
					.equals("com.cooee.hybridlocker")) {
				continue;
			}
			intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
			if (resolveInfo != null) {
				final ActivityInfo activityInfo = resolveInfo.activityInfo;
				final String title = activityInfo.loadLabel(pm).toString();
				Drawable icon = activityInfo.loadIcon(pm);
				if (title != null && title.length() > 0 && icon != null) {
					AppInfo appInfo = new AppInfo();
					appInfo.appName = activityInfo.loadLabel(pm).toString();
					appInfo.appIntent = intent;
					appInfo.appIcon = activityInfo.loadIcon(pm);
					appInfoList.add(appInfo);
					++index;
				}
			}
		}
		return appInfoList;
	}

	public static Bitmap createIconBitmap(Drawable icon) {
		Bitmap bitmap = null;
		if (icon instanceof BitmapDrawable) {
			// Ensure the bitmap has a density.
			BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
			bitmap = bitmapDrawable.getBitmap();
		} else {
			int sourceWidth = icon.getIntrinsicWidth();
			int sourceHeight = icon.getIntrinsicHeight();
			Bitmap bitmapCanves = Bitmap.createBitmap(sourceWidth,
					sourceHeight, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas();
			canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
					Paint.FILTER_BITMAP_FLAG));
			canvas.setBitmap(bitmapCanves);
			Rect oldBounds = new Rect();
			oldBounds.set(icon.getBounds());
			icon.setBounds(0, 0, sourceWidth, sourceHeight);
			icon.draw(canvas);
			icon.setBounds(oldBounds);
			canvas.setBitmap(null);
			bitmap = bitmapCanves;
		}
		return bitmap;
	}

	public static String bitmapToBase64(Bitmap bitmap) {
		String result = "";
		ByteArrayOutputStream bos = null;
		try {
			if (null != bitmap) {
				bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 100, bos);// 将bitmap放入字节数组流中

				bos.flush();// 将bos流缓存在内存中的数据全部输出，清空缓存
				bos.close();

				byte[] bitmapByte = bos.toByteArray();
				result = Base64.encodeToString(bitmapByte, Base64.DEFAULT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Bitmap base64ToBitmap(String base64String) {
		byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bitmap;
	}

	public static HashMap<String, String> setTime(Context context, Date date) {
		date.setTime(System.currentTimeMillis());
		HashMap<String, String> time = new HashMap<String, String>();
		String hour = null;
		String minute = null;
		String week = null;
		String strDate = null;
		String ampm = null;
		if (DateFormat.is24HourFormat(context)) {
			hour = String.format("%tH", date);
			ampm = " " + " ";
		} else {
			hour = String.format("%tI", date);
			ampm = String.format(Locale.US, "%tp", date);
		}
		minute = String.format("%tM", date);
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		SimpleDateFormat format = null;
		if (language.endsWith("zh")) {
			format = new SimpleDateFormat("MM月dd日");
		} else {
			format = new SimpleDateFormat("MM - dd");
		}
		strDate = format.format(date);

		week = String.format("%tA", date);
		time.put("hour", hour);
		time.put("minute", minute);
		time.put("ampm", ampm);
		time.put("date", strDate);
		time.put("week", week);
		return time;
	}
}
