package com.iLoong.launcher.MList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.Toast;
import cool.sdk.download.manager.dl_info;
import cool.sdk.log.CoolLog;

public class MeApkDlNotifyManager {

	// Class<?> mActivityClass[] = { Main_FirstActivity.class ,
	// Main_SecondActivity.class , Main_ThreeActivity.class ,
	// Main_FourthActicity.class };
	private static MeApkDlNotifyManager instance = null;
	// 以微入口为单位的ID初始值，M77 E69 ID 1-4
	private static int meEntryNotifyID = 77690;
	// 以APK为单位的ID初始值，M77 E69 ID 1-4 apkinfoID 1-999
	private static int meApkNotifyID = 77690000;
	MyR MeR = null;
	Context context = null;

	public static MeApkDlNotifyManager getInstance(Context context) {
		synchronized (MeApkDlNotifyManager.class) {
			if (instance == null) {
				instance = new MeApkDlNotifyManager(context);
			}
		}
		return instance;
	}

	public MeApkDlNotifyManager(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		MeR = MyR.getMyR(context);
		if (MeR == null) {
			MELOG.e("ME_RTFSC",
					" Can not get ME source  at MeApkDlNotifyManager");
		}
	}

	public MeApkDownloadManager GetMeApkMgr(int entryID) {
		return MeApkDlMgrBuilder.GetMeApkDownloadManager(entryID);
	}

	private String getApkIconPathByPkgname(MeApkDownloadManager CurMeDlMgr,
			String pkgName) {
		String ImgPath = null;
		if (null != CurMeDlMgr.GetSdkIconMgr().IconGetInfo(pkgName)) {
			ImgPath = CurMeDlMgr.GetSdkIconMgr().IconGetInfo(pkgName)
					.getFilePath();
		}
		return ImgPath;
	}

	// 通过 包名获取 相应的ICON，如果获取不到则使用相对于应的微入口ICON
	private Bitmap getApkIconByPkgname(MeApkDownloadManager CurMeDlMgr,
			String pkgName, Resources res, int entryID) {
		Bitmap iconBitmap = null;
		if (entryID == 10009) {
			entryID = 5;
		}
		int[] MeBigIconArry = { MeR.drawable.cool_ml_notify,
				MeR.drawable.cool_ml_wonderful_game,
				MeR.drawable.cool_ml_software, MeR.drawable.cool_ml_ku_store,
				MeR.drawable.cool_ml_know, MeR.drawable.cool_ml_you_may_love };

		if (null != CurMeDlMgr.GetSdkIconMgr().IconGetInfo(pkgName)) {
			String ImgPath = CurMeDlMgr.GetSdkIconMgr().IconGetInfo(pkgName)
					.getFilePath();
			if (null != ImgPath && ImgPath.length() > 3) {
				iconBitmap = BitmapFactory.decodeFile(ImgPath);
			}
		}
		if (null == iconBitmap) {
			iconBitmap = BitmapFactory.decodeResource(context.getResources(),
					MeBigIconArry[entryID]);
		}
		return iconBitmap;
	}

	private Bitmap getApkIconByPath(String ImgPath) {
		Bitmap iconBitmap = null;
		if (null != ImgPath && ImgPath.length() > 3) {
			iconBitmap = BitmapFactory.decodeFile(ImgPath);
		}
		return iconBitmap;
	}

	private boolean StartActivityByPackageName(String pkgName, Context mContect) {
		PackageManager packageManager = mContect.getPackageManager();
		Intent intent = null;
		try {
			intent = packageManager.getLaunchIntentForPackage(pkgName);
		} catch (Exception e) {
			intent = null;
		}
		if (null != intent) {
			mContect.startActivity(intent);
			return true;
		} else {
			return false;
		}
	}

	private void showOnMeApkDlStartNotify(int entryID, String moudleName,
			int downlodingCount) {
		if (entryID == 10009) {
			entryID = 5;
		}
		int notifyID = meEntryNotifyID + entryID;
		int[] MeIconArry = { MeR.drawable.cool_ml_notify_small,
				MeR.drawable.cool_ml_wonderful_game_small,
				MeR.drawable.cool_ml_software_small,
				MeR.drawable.cool_ml_ku_store_small,
				MeR.drawable.cool_ml_know_small,
				MeR.drawable.cool_ml_you_may_love_small };
		int[] MeBigIconArry = { MeR.drawable.cool_ml_notify,
				MeR.drawable.cool_ml_wonderful_game,
				MeR.drawable.cool_ml_software, MeR.drawable.cool_ml_ku_store,
				MeR.drawable.cool_ml_know, MeR.drawable.cool_ml_you_may_love };
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(context, MEServiceActivity.class);
		notificationIntent.putExtra("MeServiceType",
				MeServiceType.MEApkOnDownloading);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.putExtra("moudleName", moudleName);
		notificationIntent.putExtra("entryId", entryID);
		PendingIntent contentItent = PendingIntent.getActivity(context,
				entryID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		int icon = 0;
		int iconBig = 0;
		if (entryID == 10009) {
			icon = MeIconArry[5];
			iconBig = MeBigIconArry[5];
		} else {
			icon = MeIconArry[entryID];
			iconBig = MeBigIconArry[entryID];
		}
		if (Integer.parseInt(VERSION.SDK) >= 11) {
			Notification.Builder builder = new Notification.Builder(context)
					.setSmallIcon(icon)
					// 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
					// icon)
					.setLargeIcon(
							BitmapFactory.decodeResource(
									context.getResources(), iconBig))// setLargeIcon(BitmapFactory.decodeResource(res,
																		// R.drawable.i5))
					.setTicker(
							downlodingCount
									+ context
											.getString(MeR.string.cool_ml_dl_ing))// 设置在status
																					// bar上显示的提示文字
					.setContentTitle(
							downlodingCount
									+ context
											.getString(MeR.string.cool_ml_dl_ing))// 设置在下拉status
																					// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
					.setContentText(
							context.getString(MeR.string.cool_ml_dl_ing_text))// TextView中显示的详细内容
					.setContentIntent(contentItent); // 关联PendingIntent
			// .build(); //需要注意build()是在API level 16增加的，可以使用 getNotificatin()来替代
			Notification notification = builder.getNotification();
			notification.flags |= Notification.FLAG_NO_CLEAR;
			notificationManager.notify(notifyID, notification);
		} else {
			Notification notification = new Notification(iconBig,
					downlodingCount
							+ context.getString(MeR.string.cool_ml_dl_ing),
					System.currentTimeMillis());
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					MeR.layout.cool_ml_dwonload_notification);
			contentView.setImageViewResource(MeR.id.cool_ml_notification_image,
					icon);
			contentView.setTextViewText(
					MeR.id.cool_ml_notification_title,
					downlodingCount
							+ context.getString(MeR.string.cool_ml_dl_ing));
			contentView.setTextViewText(MeR.id.cool_ml_notification_text,
					context.getString(MeR.string.cool_ml_dl_ing_text));
			notification.contentView = contentView;
			notification.contentIntent = contentItent;
			notification.flags |= Notification.FLAG_NO_CLEAR;
			notificationManager.notify(notifyID, notification);
		}
		MELOG.v("ME_RTFSC", "notifyID:" + notifyID + "entryId:" + entryID);
	}

	// 当每没有下载项的时候，需要调用这个函数取消“正在下载”的notify
	private void CanelOnMeApkDlStartNotify(int entryID) {
		int notifyID = meEntryNotifyID + entryID;
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notifyID);
	}

	public void onMeApkDlStart(int entryID, String moudleName, String PkgName) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "onMeApkDlStart:" + PkgName + "  entryID:"
				+ entryID);
		MeApkDownloadManager CurMeDlMgr = MeApkDlMgrBuilder
				.GetMeApkDownloadManager(entryID);
		dl_info info = CurMeDlMgr.GetInfoByPkgName(PkgName);
		int notifyID = meApkNotifyID + entryID * 1000 + info.getID();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notifyID);
		int downlodingCount = CurMeDlMgr.GetDownLoadingApkCount();
		if (downlodingCount > 0) {
			// showOnMeApkDlStartNotify( entryID , moudleName , downlodingCount
			// );
		}
	}

	public void onMeApkInstalled(int entryID, String moudleName, String PkgName) {
		// TODO Auto-generated method stub
		try {
			int[] MeIconArry = { MeR.drawable.cool_ml_notify_small,
					MeR.drawable.cool_ml_wonderful_game_small,
					MeR.drawable.cool_ml_software_small,
					MeR.drawable.cool_ml_ku_store_small,
					MeR.drawable.cool_ml_know_small,
					MeR.drawable.cool_ml_you_may_love_small };
			MeApkDownloadManager CurMeDlMgr = MeApkDlMgrBuilder
					.GetMeApkDownloadManager(entryID);
			dl_info info = CurMeDlMgr.GetInfoByPkgName(PkgName);
			int notifyID = meApkNotifyID + PkgName.hashCode();
			String appName = (String) info.getValue("p101");
			Bitmap iconBitmap = getApkIconByPkgname(CurMeDlMgr, PkgName,
					context.getResources(), entryID);
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			MELOG.v("ME_RTFSC", "onMeApkInstalled notifyID =" + notifyID);
			PackageManager packageManager = context.getPackageManager();
			Intent notificationIntent = null;
			try {
				notificationIntent = packageManager
						.getLaunchIntentForPackage(PkgName);
			} catch (Exception e) {
				notificationIntent = null;
			}
			if (null != notificationIntent) {
				PendingIntent contentItent = PendingIntent.getActivity(context,
						notifyID, notificationIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				int icon = 0;
				if (entryID == 10009) {
					icon = MeIconArry[5];
				} else {
					icon = MeIconArry[entryID];
				}
				if (Integer.parseInt(VERSION.SDK) >= 11) {
					Notification.Builder builder = new Notification.Builder(
							context)
							.setSmallIcon(icon)
							// 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
							// icon)
							.setLargeIcon(iconBitmap)
							.setTicker(
									appName
											+ context
													.getString(MeR.string.cool_ml_dl_installed))// 设置在status
																								// bar上显示的提示文字
							.setContentTitle(
									appName
											+ context
													.getString(MeR.string.cool_ml_dl_installed))// 设置在下拉status
																								// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
							.setContentText(
									context.getString(MeR.string.cool_ml_dl_installed_text))// TextView中显示的详细内容
							.setContentIntent(contentItent); // 关联PendingIntent
					// .build(); //需要注意build()是在API level 16增加的，可以使用
					// getNotificatin()来替代
					Notification notification = builder.getNotification();
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					MELOG.v("ME_RTFSC", "Send onMeApkInstalled Notification:"
							+ notifyID);
					notificationManager.notify(notifyID, notification);
				} else {
					Notification notification = new Notification(
							icon,
							appName
									+ context
											.getString(MeR.string.cool_ml_dl_installed),
							System.currentTimeMillis());
					RemoteViews contentView = new RemoteViews(
							context.getPackageName(),
							MeR.layout.cool_ml_dwonload_notification);
					contentView.setImageViewBitmap(
							MeR.id.cool_ml_notification_image, iconBitmap);
					contentView
							.setTextViewText(
									MeR.id.cool_ml_notification_title,
									appName
											+ context
													.getString(MeR.string.cool_ml_dl_installed));
					contentView
							.setTextViewText(
									MeR.id.cool_ml_notification_text,
									context.getString(MeR.string.cool_ml_dl_installed_text));
					notification.contentView = contentView;
					notification.contentIntent = contentItent;
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					MELOG.v("ME_RTFSC", "Send onMeApkDlStop Notification:"
							+ notifyID);
					notificationManager.notify(notifyID, notification);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onMeApkUninstallCanel(String pkgName) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		int notifyID = meApkNotifyID + pkgName.hashCode();
		MELOG.v("ME_RTFSC", "onMeApkUninstallCanel notifyID =" + notifyID);
		notificationManager.cancel(notifyID);
	}

	public void onMeApkDlSucess(int entryID, String moudleName, String PkgName,
			dl_info info) {
		// 先清除meApkNotifyID + entryID * 1000 + info.getID()格式的 notifyID
		int notifyID = meApkNotifyID + entryID * 1000 + info.getID();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notifyID);
		// 创建meApkNotifyID + PkgName.hashCode(); 格式的 notifyID
		notifyID = meApkNotifyID + PkgName.hashCode();
		int[] MeIconArry = { MeR.drawable.cool_ml_notify_small,
				MeR.drawable.cool_ml_wonderful_game_small,
				MeR.drawable.cool_ml_software_small,
				MeR.drawable.cool_ml_ku_store_small,
				MeR.drawable.cool_ml_know_small,
				MeR.drawable.cool_ml_you_may_love_small };
		MeApkDownloadManager CurMeDlMgr = MeApkDlMgrBuilder
				.GetMeApkDownloadManager(entryID);
		String appName = (String) info.getValue("p101");

		Intent notificationIntent = new Intent(context, MEServiceActivity.class);
		notificationIntent.putExtra("MeServiceType",
				MeServiceType.MEApkOnSucess);
		notificationIntent.putExtra("moudleName", moudleName);
		notificationIntent.putExtra("PkgName", PkgName);
		notificationIntent.putExtra("entryID", entryID);
		notificationIntent.putExtra("appName", appName);
		notificationIntent.putExtra("notifyID", notifyID);
		notificationIntent.putExtra("apkFilePath", info.getFilePath());
		notificationIntent.putExtra("apkIconPath",
				getApkIconPathByPkgname(CurMeDlMgr, PkgName));

		Bitmap iconBitmap = getApkIconByPkgname(CurMeDlMgr, PkgName,
				context.getResources(), entryID);

		PendingIntent contentItent = PendingIntent
				.getActivity(context, notifyID, notificationIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		int icon = 0;
		if (entryID == 10009) {
			icon = MeIconArry[5];
		} else {
			icon = MeIconArry[entryID];
		}
		if (Integer.parseInt(VERSION.SDK) >= 11) {
			Notification.Builder builder = new Notification.Builder(context)
					.setSmallIcon(icon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
										// icon)
					.setLargeIcon(iconBitmap)
					.setTicker(
							appName
									+ context
											.getString(MeR.string.cool_ml_dl_sucess))// 设置在status
																						// bar上显示的提示文字
					.setContentTitle(
							appName
									+ context
											.getString(MeR.string.cool_ml_dl_sucess))// 设置在下拉status
																						// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
					.setContentText(
							context.getString(MeR.string.cool_ml_dl_sucess_text))// TextView中显示的详细内容
					.setContentIntent(contentItent); // 关联PendingIntent
			// .build(); //需要注意build()是在API level 16增加的，可以使用 getNotificatin()来替代
			Notification myNotify = builder.getNotification();
			myNotify.flags |= Notification.FLAG_NO_CLEAR;
			MELOG.v("ME_RTFSC", "Send onMeApkDlSucess Notification:" + notifyID);
			notificationManager.notify(notifyID, myNotify);
		} else {
			Notification notification = new Notification(icon,
					appName + appName
							+ context.getString(MeR.string.cool_ml_dl_sucess),
					System.currentTimeMillis());
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					MeR.layout.cool_ml_dwonload_notification);
			contentView.setImageViewBitmap(MeR.id.cool_ml_notification_image,
					iconBitmap);
			contentView.setTextViewText(MeR.id.cool_ml_notification_title,
					appName + context.getString(MeR.string.cool_ml_dl_sucess));
			contentView.setTextViewText(MeR.id.cool_ml_notification_text,
					context.getString(MeR.string.cool_ml_dl_sucess_text));
			notification.contentView = contentView;
			notification.contentIntent = contentItent;
			MELOG.v("ME_RTFSC", "Send onMeApkDlStop Notification:" + notifyID);
			notification.flags |= Notification.FLAG_NO_CLEAR;
			notificationManager.notify(notifyID, notification);
		}
		int downlodingCount = CurMeDlMgr.GetDownLoadingApkCount();
		if (downlodingCount > 0) {
			// showOnMeApkDlStartNotify( entryID , moudleName , downlodingCount
			// );
		} else {
			CanelOnMeApkDlStartNotify(entryID);
		}
	}

	public void onMeApkDlSucessEx(int entryID, int notifyID, String moudleName,
			String PkgName, String appName, String apkFilePath,
			String apkIconPath) {
		NotificationManager notificationManager = (NotificationManager) this.context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notifyID);
		int[] MeIconArry = { MeR.drawable.cool_ml_notify_small,
				MeR.drawable.cool_ml_wonderful_game_small,
				MeR.drawable.cool_ml_software_small,
				MeR.drawable.cool_ml_ku_store_small,
				MeR.drawable.cool_ml_know_small,
				MeR.drawable.cool_ml_you_may_love_small };
		Bitmap iconBitmap = getApkIconByPath(apkIconPath);
		Intent notificationIntent = new Intent(context, MEServiceActivity.class);
		notificationIntent.putExtra("MeServiceType",
				MeServiceType.MEApkOnSucessEx);
		notificationIntent.putExtra("moudleName", moudleName);
		notificationIntent.putExtra("apkFilePath", apkFilePath);
		notificationIntent.putExtra("notifyID", notifyID);
		PendingIntent contentItent = PendingIntent
				.getActivity(context, notifyID, notificationIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		int icon = 0;
		if (entryID == 10009) {
			icon = MeIconArry[5];
		} else {
			icon = MeIconArry[entryID];
		}
		if (Integer.parseInt(VERSION.SDK) >= 11) {
			Notification.Builder builder = new Notification.Builder(context)
					.setSmallIcon(icon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
										// icon)
					.setLargeIcon(iconBitmap)
					.setTicker(
							appName
									+ context
											.getString(MeR.string.cool_ml_dl_sucess))// 设置在status
																						// bar上显示的提示文字
					.setContentTitle(
							appName
									+ context
											.getString(MeR.string.cool_ml_dl_sucess))// 设置在下拉status
																						// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
					.setContentText(
							context.getString(MeR.string.cool_ml_dl_sucess_text))// TextView中显示的详细内容
					.setContentIntent(contentItent); // 关联PendingIntent
			// .build(); //需要注意build()是在API level 16增加的，可以使用 getNotificatin()来替代
			Notification myNotify = builder.getNotification();
			MELOG.v("ME_RTFSC", "Send onMeApkDlSucess Notification:" + notifyID);
			notificationManager.notify(notifyID, myNotify);
		} else {
			Notification notification = new Notification(icon,
					appName + appName
							+ context.getString(MeR.string.cool_ml_dl_sucess),
					System.currentTimeMillis());
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					MeR.layout.cool_ml_dwonload_notification);
			contentView.setImageViewBitmap(MeR.id.cool_ml_notification_image,
					iconBitmap);
			contentView.setTextViewText(MeR.id.cool_ml_notification_title,
					appName + context.getString(MeR.string.cool_ml_dl_sucess));
			contentView.setTextViewText(MeR.id.cool_ml_notification_text,
					context.getString(MeR.string.cool_ml_dl_sucess_text));
			notification.contentView = contentView;
			notification.contentIntent = contentItent;
			MELOG.v("ME_RTFSC", "Send onMeApkDlStop Notification:" + notifyID);
			notificationManager.notify(notifyID, notification);
		}
	}

	public void onMeApkDlDel(int entryID, String moudleName, String PkgName) {
		MeApkDownloadManager CurMeDlMgr = MeApkDlMgrBuilder
				.GetMeApkDownloadManager(entryID);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		dl_info info = CurMeDlMgr.GetInfoByPkgName(PkgName);
		int notifyID = meApkNotifyID + entryID * 1000 + info.getID();
		notificationManager.cancel(notifyID);
		int downlodingCount = CurMeDlMgr.GetDownLoadingApkCount();
		if (downlodingCount > 0) {
			// showOnMeApkDlStartNotify( entryID , moudleName , downlodingCount
			// );
		} else {
			CanelOnMeApkDlStartNotify(entryID);
		}
	}

	public void onMeApkDlStop(int entryID, String moudleName, String PkgName) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "onMeApkDlStop:" + PkgName + "  entryID:" + entryID);

		int[] MeIconArry = { MeR.drawable.cool_ml_notify_small,
				MeR.drawable.cool_ml_wonderful_game_small,
				MeR.drawable.cool_ml_software_small,
				MeR.drawable.cool_ml_ku_store_small,
				MeR.drawable.cool_ml_know_small,
				MeR.drawable.cool_ml_you_may_love_small };
		MeApkDownloadManager CurMeDlMgr = MeApkDlMgrBuilder
				.GetMeApkDownloadManager(entryID);
		dl_info info = CurMeDlMgr.GetInfoByPkgName(PkgName);
		int notifyID = meApkNotifyID + entryID * 1000 + info.getID();
		String appName = (String) info.getValue("p101");

		Intent notificationIntent = new Intent(context, MEServiceActivity.class);
		notificationIntent.putExtra("MeServiceType",
				MeServiceType.MEApkOnNotifyReStart);
		notificationIntent.putExtra("moudleName", moudleName);
		notificationIntent.putExtra("entryID", entryID);
		notificationIntent.putExtra("PkgName", PkgName);

		Bitmap iconBitmap = getApkIconByPkgname(CurMeDlMgr, PkgName,
				context.getResources(), entryID);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		PendingIntent contentItent = PendingIntent
				.getActivity(context, notifyID, notificationIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		int icon = 0;
		if (entryID == 10009) {
			icon = MeIconArry[5];
		} else {
			icon = MeIconArry[entryID];
		}
		if (Integer.parseInt(VERSION.SDK) >= 11) {
			Notification.Builder builder = new Notification.Builder(context)
					.setSmallIcon(icon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
										// icon)
					.setLargeIcon(iconBitmap)
					.setTicker(
							appName
									+ context
											.getString(MeR.string.cool_ml_dl_stop))// 设置在status
																					// bar上显示的提示文字
					.setContentTitle(
							appName
									+ context
											.getString(MeR.string.cool_ml_dl_stop))// 设置在下拉status
																					// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
					.setContentText(
							context.getString(MeR.string.cool_ml_dl_stop_text))// TextView中显示的详细内容
					.setContentIntent(contentItent); // 关联PendingIntent
			// .build(); //需要注意build()是在API level 16增加的，可以使用 getNotificatin()来替代
			Notification notification = builder.getNotification();
			MELOG.v("ME_RTFSC", "Send onMeApkDlStop Notification:" + notifyID);
			notificationManager.notify(notifyID, notification);
		} else {
			Notification notification = new Notification(icon, appName
					+ context.getString(MeR.string.cool_ml_dl_stop),
					System.currentTimeMillis());
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					MeR.layout.cool_ml_dwonload_notification);
			contentView.setImageViewBitmap(MeR.id.cool_ml_notification_image,
					iconBitmap);
			contentView.setTextViewText(MeR.id.cool_ml_notification_title,
					appName + context.getString(MeR.string.cool_ml_dl_stop));
			contentView.setTextViewText(MeR.id.cool_ml_notification_text,
					context.getString(MeR.string.cool_ml_dl_stop_text));
			notification.contentView = contentView;
			notification.contentIntent = contentItent;
			MELOG.v("ME_RTFSC", "Send onMeApkDlStop Notification:" + notifyID);
			notificationManager.notify(notifyID, notification);
		}
		int downlodingCount = CurMeDlMgr.GetDownLoadingApkCount();
		if (downlodingCount > 0) {
			// showOnMeApkDlStartNotify( entryID , moudleName , downlodingCount
			// );
		} else {
			CanelOnMeApkDlStartNotify(entryID);
		}
	}

	public void onMeApkDlFailed(int entryID, String moudleName, String PkgName,
			dl_info info) {
		MELOG.v("ME_RTFSC", "onMeApkDlFailed:" + PkgName + "  entryID:"
				+ entryID);
		new CoolLog(context).v("ME_RTFSC", "onMeApkDlFailed:" + PkgName
				+ "  entryID:" + entryID);
		new Thread(new Runnable() {

			@Override
			public void run() {
				int failedInfoID = MeR.string.cool_ml_download_failed;
				// TODO: handle exception
				if (false == JSClass.IsNetworkAvailableLocal(context
						.getApplicationContext())) {
					failedInfoID = MeR.string.cool_ml_network_not_available;
				} else if (false == JSClass.IsStorageCanUsed()) {
					failedInfoID = MeR.string.cool_ml_storage_not_available;
				} else {
					failedInfoID = MeR.string.cool_ml_download_failed;
				}
				Looper.prepare();
				Toast.makeText(context.getApplicationContext(), failedInfoID,
						Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}).start();
		if (info != null) {
			MeApkDownloadManager CurMeDlMgr = MeApkDlMgrBuilder
					.GetMeApkDownloadManager(entryID);
			int notifyID = meApkNotifyID + entryID * 1000 + info.getID();
			int[] MeIconArry = { MeR.drawable.cool_ml_notify_small,
					MeR.drawable.cool_ml_wonderful_game_small,
					MeR.drawable.cool_ml_software_small,
					MeR.drawable.cool_ml_ku_store_small,
					MeR.drawable.cool_ml_know_small,
					MeR.drawable.cool_ml_you_may_love_small };

			String appName = (String) info.getValue("p101");

			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			Intent notificationIntent = new Intent(context,
					MEServiceActivity.class);
			notificationIntent.putExtra("MeServiceType",
					MeServiceType.MEApkOnNotifyReStart);
			notificationIntent.putExtra("moudleName", moudleName);
			notificationIntent.putExtra("entryID", entryID);
			notificationIntent.putExtra("PkgName", PkgName);

			Bitmap iconBitmap = getApkIconByPkgname(CurMeDlMgr, PkgName,
					context.getResources(), entryID);

			PendingIntent contentItent = PendingIntent.getActivity(context,
					notifyID, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			int icon = 0;
			if (entryID == 10009) {
				icon = MeIconArry[5];
			} else {
				icon = MeIconArry[entryID];
			}
			if (Integer.parseInt(VERSION.SDK) >= 11) {
				Notification.Builder builder = new Notification.Builder(context)
						.setSmallIcon(icon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
											// icon)
						.setLargeIcon(iconBitmap)
						.setTicker(
								appName
										+ context
												.getString(MeR.string.cool_ml_dl_failed))// 设置在status
																							// bar上显示的提示文字
						.setContentTitle(
								appName
										+ context
												.getString(MeR.string.cool_ml_dl_failed))// 设置在下拉status
																							// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
						.setContentText(
								context.getString(MeR.string.cool_ml_dl_failed_text))// TextView中显示的详细内容
						.setContentIntent(contentItent); // 关联PendingIntent
				// .build(); //需要注意build()是在API level 16增加的，可以使用
				// getNotificatin()来替代
				Notification myNotify = builder.getNotification();
				// myNotify.flags |= Notification.FLAG_NO_CLEAR;
				notificationManager.notify(notifyID, myNotify);
			} else {
				Notification notification = new Notification(icon, appName
						+ context.getString(MeR.string.cool_ml_dl_failed),
						System.currentTimeMillis());
				RemoteViews contentView = new RemoteViews(
						context.getPackageName(),
						MeR.layout.cool_ml_dwonload_notification);
				contentView.setImageViewBitmap(
						MeR.id.cool_ml_notification_image, iconBitmap);
				contentView
						.setTextViewText(
								MeR.id.cool_ml_notification_title,
								appName
										+ context
												.getString(MeR.string.cool_ml_dl_failed));
				contentView.setTextViewText(MeR.id.cool_ml_notification_text,
						context.getString(MeR.string.cool_ml_dl_failed_text));
				notification.contentView = contentView;
				notification.contentIntent = contentItent;
				MELOG.v("ME_RTFSC", "Send onMeApkDlStop Notification:"
						+ notifyID);
				new CoolLog(context).v("ME_RTFSC",
						"Send onMeApkDlStop Notification:" + notifyID);
				notificationManager.notify(notifyID, notification);
			}
			MELOG.v("ME_RTFSCX",
					"notifyID:" + notifyID + "filepath:" + info.getFilePath());
			new CoolLog(context).v("ME_RTFSCX", "notifyID:" + notifyID
					+ "filepath:" + info.getFilePath());
			int downlodingCount = CurMeDlMgr.GetDownLoadingApkCount();
			if (downlodingCount > 0) {
				// showOnMeApkDlStartNotify( entryID , moudleName ,
				// downlodingCount );
			} else {
				CanelOnMeApkDlStartNotify(entryID);
			}
		}
	}
}
