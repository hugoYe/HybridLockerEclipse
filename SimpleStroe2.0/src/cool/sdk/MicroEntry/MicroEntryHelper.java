package cool.sdk.MicroEntry;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.R.string;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;

import com.iLoong.launcher.MList.LoadURL;
import com.iLoong.launcher.MList.MELOG;
import com.iLoong.launcher.MList.MainActivity;
import com.iLoong.launcher.MList.Main_FirstActivity;
import com.iLoong.launcher.MList.Main_FourthActicity;
import com.iLoong.launcher.MList.Main_SecondActivity;
import com.iLoong.launcher.MList.Main_ThreeActivity;
import com.iLoong.launcher.MList.MeServiceType;
import com.iLoong.launcher.MList.MyR;

import cool.sdk.MicroEntry.MicroEntryLog.MicroEntryLogItem;
import cool.sdk.common.UrlUtil;
import cool.sdk.download.CoolDLMgr;
import cool.sdk.download.manager.DlMethod;

public class MicroEntryHelper extends MicroEntryUpdate {

	// 取消不必要的循环，加快运行速度
	public static final String First = "29A8D8E48F72F2324F619555AA82D91C8D86BF22704AB5D93AE8BF8F682D2409";
	public static final String Second = "29A8D8E48F72F2324F619555AA82D91CACBFE1C609D78D92AB22B4FB312A0018";
	public static final String Three = "29A8D8E48F72F2324F619555AA82D91C631F9E1D0BBB09F53AE8BF8F682D2409";
	public static final String Fourth = "29A8D8E48F72F2324F619555AA82D91C847F327C0E268D3F2BC7876968DB9E2A";
	public static final String[] MEAction = { First, Second, Three, Fourth };
	private static boolean HasSetDisable = false;
	Class<?> mActivityClass[] = { Main_FirstActivity.class,
			Main_SecondActivity.class, Main_ThreeActivity.class,
			Main_FourthActicity.class };
	// 在无效（0）状态下，五次显示次数后，睡眠的时间，睡眠这个时间后，再来一组（TimeShow * 5次），就不再显示了
	long TimeWakeUpShow = 60 * 60 * 24 * 3L;
	// 每次显示notify的时间间隔（显示 5次）
	long TimeShow = 60 * 60 * 24 * 1L;
	// public static final long TimeWakeUpShow = 60L;
	// public static final long TimeShow = 15L;
	boolean IsMeUpdateDisclaimer = true;
	int NotifyID = 4321;
	MyR RR = null;
	Context mContext = null;

	protected MicroEntryHelper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		RR = MyR.getMyR(context);
		if (RR == null) {
			MELOG.e("ME_RTFSC", "MicroEntryHelper:Can not get ME RESOURCE!!!");
		}
	}

	static MicroEntryHelper instance = null;

	public static MicroEntryHelper getInstance(Context context) {
		synchronized (MicroEntryHelper.class) {
			if (instance == null) {
				instance = new MicroEntryHelper(context);
			}
		}
		return instance;
	}

	boolean[] visableInDesktop = { false, false, false, false };

	@Override
	public void OnDataChange() throws Exception {
		// TODO Auto-generated method stub
		String resJson = getListString();
		MELOG.v("ME_RTFSC", "MicroEntryHelper: OnDataChange:" + resJson);
		boolean[] visible = { false, false, false, false };
		for (int i = 0; i < visableInDesktop.length; i++) {
			visableInDesktop[i] = false;
		}
		if (resJson != null) {
			try {
				JSONObject list = new JSONObject(resJson);
				Iterator<?> keys = (Iterator<?>) list.keys();
				String key;
				while (keys.hasNext()) {
					key = (String) keys.next();
					JSONObject item = list.getJSONObject(key);
					// r1 list 数字 入口ID [入口ID是客户端标示或者定位入口的唯一ID。]
					// r2 list 对象，字符 英文名称
					// r3 list 对象，字符 中文名称
					// r4 list 对象，字符 繁体名称
					// r5 list 对象，数字 应用程序列表 0:不显示 1:显示
					// r6 list 对象，数字 桌面 0:不显示 1:显示
					// r7 list 对象，字符 图标地址url
					// r8 list 对象，字符 入口url
					// r9 list 对象，数字 快捷方式显示屏幕位置x
					// r10 list 对象，数字 快捷方式显示屏幕位置y
					int r1 = item.getInt("r1");
					String r2 = item.getString("r2");
					String r3 = item.getString("r3");
					String r4 = item.getString("r4");
					int r5 = item.getInt("r5");
					int r6 = item.getInt("r6");
					String r7 = item.getString("r7");
					String r8 = item.getString("r8");
					int r9 = item.getInt("r9");
					int r10 = item.getInt("r10");
					setValue(r1 + "r5", r5);
					setValue(r1 + "r8", r8);
					if (r1 >= 1 && r1 <= 4) {
						if (1 == r5) {
							visible[r1 - 1] = true;
						}
						if (1 == r6) {
							visableInDesktop[r1 - 1] = true;
						}
					}
				}
			} catch (Exception e) {
			}
		}
		// =========================================================
		if (IsMeUpdateDisclaimer) {
			updateME(visible);
		} else {
			UpdateActiveItemList(visible, 2);
			UpdateDeleteItemListByConfig(visible);
			for (int i = 0; i < 4; i++) {
				setEnabled(context, mActivityClass[i], visible[i],
						visableInDesktop[i], i);
			}
		}
	}

	private void updateME(boolean[] visible) {
		MELOG.v("ME_RTFSC", "=== updateME ===  R001_00UITEST");
		UpdateDeleteItemListByConfig(visible);
		MELOG.v("ME_RTFSC", "EEE UpdateDeleteItemListByConfig EEE");
		// 先把需要隐藏的微入口处理掉
		HasSetDisable = false;
		for (int i = 0; i < 4; i++) {
			if (false == visible[i]) {
				setEnabled(context, mActivityClass[i], visible[i],
						visableInDesktop[i], i);
			}
		}
		int c0 = getInt("c0", -1);
		MELOG.v("ME_RTFSC", "c0:" + c0);
		for (int i = 0; i < 4; i++) {
			MELOG.v("ME_RTFSC", mActivityClass[i] + ":" + visible[i]);
			// 服务器是否要求显示
			if (true == visible[i]) {
				// 在别的携带ME的产品中,此入口是否已显示
				if (true == checkActionCanEnable(context, i)) {
					MELOG.v("ME_RTFSC", "checkAction false ");
					// 强制显示(只显示，不启动)
					if (1 == c0) {
						UpdateActiveItemList(visible, 2);
						updateMEStateForce(visible);
						break;
					} else {
						int NotifyCount = getInt("ME_SHOW_COUNT", 0);
						MELOG.v("ME_RTFSC", "NotifyCount:" + NotifyCount
								+ " c0:" + c0);
						// 钝化后强制显示
						if (2 == c0 && NotifyCount >= 5) {
							UpdateActiveItemList(visible, 3);
							updateMEStateForce(visible);
							break;
						}
						// 如果是普通状态，且消息弹出过10次以上，就不在处理了
						if (0 == c0 && NotifyCount > 10) {
							break;
						} else {
							long CurTime = getServerCurTime();
							MELOG.v("ME_RTFSC", "CurTime:" + CurTime);
							// 时间获取不到，不做操作
							if (0L == CurTime) {
								continue;
							} else {
								long PerTime = (long) getLong(
										"ME_SHOW_SERVE_PERTIME", 0L);
								long TimeDiff = CurTime - PerTime;
								MELOG.v("ME_RTFSC", "PerTime:" + PerTime
										+ "   TimeDiff:" + TimeDiff);
								if (TimeDiff > TimeWakeUpShow) {
									if (true == HasSetDisable) {
										ShowNotifcationPer(visible, i, CurTime,
												NotifyCount, c0);
									} else {
										ShowMENotification(visible, i, CurTime,
												NotifyCount, c0);
									}
									break;
								} else if (TimeDiff > TimeShow
										&& 5 != NotifyCount) {
									if (true == HasSetDisable) {
										ShowNotifcationPer(visible, i, CurTime,
												NotifyCount, c0);
									} else {
										ShowMENotification(visible, i, CurTime,
												NotifyCount, c0);
									}
									break;
								} else {
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	private void ShowMENotification(boolean[] visible, int MEindex,
			long CurTime, int NotifyCount, int NoitfType) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "ShowMENotification");
		setValue("ME_SHOW_SERVE_PERTIME", CurTime);
		setValue("ME_SHOW_COUNT", NotifyCount + 1);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(RR.drawable.cool_ml_know,
				context.getString(RR.string.cool_ml_new_content),
				System.currentTimeMillis());
		Intent notificationIntent = new Intent(context,
				com.iLoong.launcher.MList.MEServiceActivity.class);
		notificationIntent.putExtra("MeServiceType", MeServiceType.MEShowType);
		notificationIntent.putExtra("NOTIFY_ME_SHOW_ARRY", visible);
		notificationIntent.putExtra("NOTIFY_ME_SHOW_ID", MEindex);
		notificationIntent.putExtra("NOTIFY_ME_SHOW_TYPE", NoitfType);
		PendingIntent contentItent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification
				.setLatestEventInfo(context,
						context.getString(RR.string.cool_ml_new_content),
						context.getString(RR.string.cool_ml_more_content),
						contentItent);
		notificationManager.notify(NotifyID, notification);
		for (int i = 0; i < visableInDesktop.length; i++) {
			if (true == visableInDesktop[i]) {
				setValue("shortcut_visible" + i, 1);
			} else {
				setValue("shortcut_visible" + i, 0);
			}
			MELOG.v("ME_RTFSC", "ShowMENotification ShortcurtVisable " + i
					+ " " + visableInDesktop[i]);
		}
	}

	private void ShowNotifcationPer(boolean[] visible, int MEindex,
			long CurTime, int NotifyCount, int NoitfType) {
		// TODO Auto-generated method stub
		for (int i = 0; i < visible.length; i++) {
			if (true == visible[i]) {
				setValue("notifcation_visible" + i, 1);
			} else {
				setValue("notifcation_visible" + i, 0);
			}
		}
		for (int i = 0; i < visableInDesktop.length; i++) {
			if (true == visableInDesktop[i]) {
				setValue("shortcut_visible" + i, 1);
			} else {
				setValue("shortcut_visible" + i, 0);
			}
			MELOG.v("ME_RTFSC", "ShowNotifcationPer ShortcurtVisable " + i
					+ " " + visableInDesktop[i]);
		}
		setValue("notifcation_MEindex", MEindex);
		setValue("notifcation_CurTime", CurTime);
		setValue("notifcation_NotifyCount", NotifyCount);
		setValue("notifcation_NoitfType", NoitfType);
	}

	public void ShowNotifcation() {
		// TODO Auto-generated method stub
		boolean[] visible = { false, false, false, false };
		int MEindex = -1;
		long CurTime = 0L;
		int NotifyCount = -1;
		int NoitfType = -1;
		MEindex = getInt("notifcation_MEindex", -1);
		if (-1 != MEindex) {
			for (int i = 0; i < visible.length; i++) {
				if (1 == getInt("notifcation_visible" + i, 0)) {
					visible[i] = true;
				}
			}
			CurTime = getLong("notifcation_CurTime", 0L);
			NotifyCount = getInt("notifcation_NotifyCount", -1);
			NoitfType = getInt("notifcation_NoitfType", -1);
			ShowMENotification(visible, MEindex, CurTime, NotifyCount,
					NoitfType);
		}
		setValue("notifcation_MEindex", -1);
	}

	private long getServerCurTime() {
		// TODO Auto-generated method stub
		long CurTime = 0L;
		// CurTime = System.currentTimeMillis() / 1000;
		try {
			HttpURLConnection conn = DlMethod.HttpGet(context,
					UrlUtil.urlGetTime);
			CurTime = Long.parseLong(new String(DlMethod.bytesFromStream(conn
					.getInputStream())));
			conn.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
			CurTime = 0L;
		}
		return CurTime;
	}

	public void UpdateMeStateUserConfirm(boolean visible[], int index) {
		MELOG.v("ME_RTFSC", "UpdateMeStateUserConfirm");
		boolean[] ShortcurtVisable = { false, false, false, false };
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NotifyID);
		setValue("ME_SHOW_SERVE_PERTIME", 0L);
		setValue("ME_SHOW_COUNT", 0);
		Intent ActivityIntent = new Intent();
		ActivityIntent.setClass(context, MainActivity.class);
		ActivityIntent.putExtra("APP_ID", index + 1);
		ActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(ActivityIntent);
		UpdateActiveItemList(visible, 1);
		for (int i = 0; i < ShortcurtVisable.length; i++) {
			if (1 == getInt("shortcut_visible" + i, 0)) {
				ShortcurtVisable[i] = true;
			} else {
				ShortcurtVisable[i] = false;
			}
			MELOG.v("ME_RTFSC", "ShortcurtVisable " + i + " "
					+ ShortcurtVisable[i]);
		}
		for (int i = 0; i < 4; i++) {
			MELOG.v("ME_RTFSC", "setEnabled " + i + " " + visible[i]);
			if (true == visible[i]) {
				setEnabled(context, mActivityClass[i], visible[i],
						ShortcurtVisable[i], i);
			}
		}
	}

	private void updateMEStateForce(boolean visible[]) {
		// TODO Auto-generated method stub
		Log.v("ME_RTFSC", "updateMEStateForce ");
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NotifyID);
		// notificationManager.cancelAll();
		setValue("ME_SHOW_SERVE_PERTIME", 0L);
		setValue("ME_SHOW_COUNT", 0);
		for (int i = 0; i < 4; i++) {
			Log.v("ME_RTFSC", "setEnabled " + i + " " + visible[i]);
			// 减去不必要的循环
			if (true == visible[i]) {
				setEnabled(context, mActivityClass[i], visible[i],
						visableInDesktop[i], i);
			}
		}
	}

	public void SetDesktopShotCutIcon(Context context, Class<?> mClass,
			boolean isEnable, int i) {
		// MELOG.v( "ME_RTFSC" , "SetDesktopShotCutIcon:" + mClass + ":" +
		// isEnable );
		int[] shortCutNameID = { RR.string.cool_ml_app_name1,
				RR.string.cool_ml_app_name2, RR.string.cool_ml_app_name3,
				RR.string.cool_ml_app_name4 };
		int[] shortCutIconID = { RR.drawable.cool_ml_wonderful_game,
				RR.drawable.cool_ml_software, RR.drawable.cool_ml_ku_store,
				RR.drawable.cool_ml_know };
		if (true == isEnable) {
			Intent addintent = new Intent(context, mClass);
			Intent shortcutIntent = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, addintent);
			shortcutIntent.putExtra("duplicate", false); // 不允许重复创建
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					context.getString(shortCutNameID[i]));
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(context,
							shortCutIconID[i]));
			MELOG.v("ME_RTFSC",
					"INSTALL_SHORTCUT: shortCutName:"
							+ context.getString(shortCutNameID[i])
							+ "shortCutIconID:" + shortCutIconID[i]);
			context.sendBroadcast(shortcutIntent);
			// MeLauncherInterface.getInstance().InstallShortcut( shortcutIntent
			// );
		} else if (false == isEnable) {
			Intent delintent = new Intent(context, mClass);
			Intent shortcutIntent = new Intent(
					"com.android.launcher.action.UNINSTALL_SHORTCUT");
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, delintent);
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					context.getString(shortCutNameID[i]));
			// shortcutIntent.putExtra( Intent.EXTRA_SHORTCUT_ICON_RESOURCE ,
			// Intent.ShortcutIconResource.fromContext( context ,
			// shortCutIconID[i] ) );
			MELOG.v("ME_RTFSC",
					"UNINSTALL_SHORTCUT: shortCutName:"
							+ context.getString(shortCutNameID[i])
							+ "shortCutIconID:" + shortCutIconID[i]);
			context.sendBroadcast(shortcutIntent);
			// MeLauncherInterface.getInstance().UninstallShortcut(
			// shortcutIntent );
		}
	}

	public void setEnabled(Context context, Class<?> mClass, boolean isenable,
			boolean isDesktopEnable, int i) {
		MELOG.v("ME_RTFSC", "setEnabled: " + isenable + isDesktopEnable);
		if (!isenable && true == checkActionCanDisable(context, i)
				&& 1 != getInt(mClass.toString(), 0)) {
			setComponentEnabled(context, new ComponentName(context, mClass),
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
			if (!context.getApplicationContext().getPackageName()
					.equals("com.cooee.oilauncher")
					&& !context.getApplicationContext().getPackageName()
							.equals("com.cooee.unilauncher")) {
				SetDesktopShotCutIcon(context, mClass, false, i);
			}
		} else if (isenable && true == checkActionCanEnable(context, i)) {
			setComponentEnabled(context, new ComponentName(context, mClass),
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
			if (true == isDesktopEnable
					&& !context.getApplicationContext().getPackageName()
							.equals("com.cooee.oilauncher")
					&& !context.getApplicationContext().getPackageName()
							.equals("com.cooee.unilauncher")) {
				SetDesktopShotCutIcon(context, mClass, isDesktopEnable, i);
			}
		}
	}

	public void setEnableByCfg(int i) {
		if (true == checkActionCanEnable(mContext, i)) {
			setComponentEnabled(mContext, new ComponentName(mContext,
					mActivityClass[i]),
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
		}
	}

	private boolean checkActionCanEnable(Context context, int i) {
		Intent in = new Intent();
		boolean hasEnable = false;
		List<ResolveInfo> listActivity;
		try {
			in.setAction(LoadURL.hexStr2Str(MEAction[i]));
			listActivity = context.getPackageManager().queryIntentActivities(
					in, 0);
			if (listActivity.isEmpty()) {
				hasEnable = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 如果此时图标都被手动卸载（隐藏）了，那就不响应显示、消失操作了
		if (true == hasEnable) {
			// 判断是否被手动卸载了（COOEE Launcher）
			ComponentName componentName = new ComponentName(context
					.getApplicationContext().getPackageName(),
					mActivityClass[i].getName());
			if (true == PreferenceManager.getDefaultSharedPreferences(
					context.getApplicationContext()).getBoolean(
					"HIDE:" + componentName.toString(), false)) {
				hasEnable = false;
			}
			// 判断是否被手动卸载了（原生态 Launcher）
			if (true == PreferenceManager.getDefaultSharedPreferences(
					context.getApplicationContext()).getBoolean(
					"ME_HIDE:" + mActivityClass[i].getName(), false)) {
				hasEnable = false;
			}
		}

		MELOG.v("ME_RTFSC",
				"checkActionCanEnable:" + LoadURL.hexStr2Str(MEAction[i])
						+ "====" + hasEnable);
		return hasEnable;
	}

	private boolean checkActionCanDisable(Context context, int i) {
		Intent in = new Intent();
		boolean hasDisable = false;
		List<ResolveInfo> listActivity = null;
		try {
			in.setAction(LoadURL.hexStr2Str(MEAction[i]));
			listActivity = context.getPackageManager().queryIntentActivities(
					in, 0);
			if (!listActivity.isEmpty()) {
				for (ResolveInfo resolveInfo : listActivity) {
					if (resolveInfo.activityInfo.packageName.equals(context
							.getPackageName())) {
						hasDisable = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 如果此时图标都被手动卸载（隐藏）了，那就不响应显示、消失操作了
		if (true == hasDisable) {
			// 判断是否被手动卸载了（COOEE Launcher）
			ComponentName componentName = new ComponentName(context
					.getApplicationContext().getPackageName(),
					mActivityClass[i].getName());
			if (true == PreferenceManager.getDefaultSharedPreferences(
					context.getApplicationContext()).getBoolean(
					"HIDE:" + componentName.toString(), false)) {
				hasDisable = false;
			}
			// 判断是否被手动卸载了（原生态 Launcher）
			if (true == PreferenceManager.getDefaultSharedPreferences(
					context.getApplicationContext()).getBoolean(
					"ME_HIDE:" + mActivityClass[i].getName(), false)) {
				hasDisable = false;
			}
		}
		// 如果是用户指定打开的微入口，是不受服务器的控制而关闭的
		if (true == hasDisable) {
			if (true == PreferenceManager.getDefaultSharedPreferences(
					context.getApplicationContext()).getBoolean(
					"meconfig:" + mActivityClass[i].getName(), false)) {
				hasDisable = false;
			}
		}

		MELOG.v("ME_RTFSC",
				"checkActionDisable:" + LoadURL.hexStr2Str(MEAction[i])
						+ "====" + hasDisable);
		return hasDisable;
	}

	private static void setComponentEnabled(Context context,
			ComponentName compName, int newStat) {
		try {
			PackageManager pkgMgr = context.getPackageManager();
			MELOG.v("ME_RTFSC", compName.toShortString() + ":" + newStat
					+ pkgMgr.getComponentEnabledSetting(compName));
			if (pkgMgr.getComponentEnabledSetting(compName) != newStat) {
				pkgMgr.setComponentEnabledSetting(compName, newStat,
						PackageManager.DONT_KILL_APP);
				if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == newStat
						&& false == HasSetDisable) {
					HasSetDisable = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean shouldExit(Context context) {
		for (int i = 0; i <= 4; i++) {
			CoolDLMgr dlmgr = MicroEntry.CoolDLMgr(context, "M", i);
			if (dlmgr.dl_mgr.getTaskCount() > 0) {
				return false;
			}
		}
		return true;
	}

	public void UpdateDeleteItemByHide(int type, String className) {
		MELOG.v("ME_RTFSC", "===UpdateDeleteItemListByHide===  className:"
				+ className + "  type:" + type);
		String[] name = { context.getString(RR.string.cool_ml_app_name1),
				context.getString(RR.string.cool_ml_app_name2),
				context.getString(RR.string.cool_ml_app_name3),
				context.getString(RR.string.cool_ml_app_name4) };
		for (int i = 0; i < 4; i++) {
			if (className.endsWith(mActivityClass[i].getName())) {
				MicroEntryLogItem myItem = new MicroEntryLogItem();
				myItem.id = i + 1;
				myItem.type = type;
				myItem.name = name[i];
				MELOG.v("ME_RTFSC", "myItem" + myItem.name);
				MicroEntryLog.LogDelete(context, myItem);
				break;
			}
		}
	}

	private void UpdateDeleteItemListByConfig(boolean[] visible) {
		MELOG.v("ME_RTFSC", "=== UpdateDeleteItemListByConfig ===");
		List<MicroEntryLogItem> DeleteItemList = null;
		int[] MENameID = { RR.string.cool_ml_app_name1,
				RR.string.cool_ml_app_name2, RR.string.cool_ml_app_name3,
				RR.string.cool_ml_app_name4 };
		DeleteItemList = new ArrayList<MicroEntryLog.MicroEntryLogItem>();
		for (int i = 0; i < 4; i++) {
			if (false == visible[i]
					&& true == checkActionCanDisable(context, i)) {
				MicroEntryLogItem myItem = new MicroEntryLogItem();
				myItem.id = i + 1;
				myItem.type = 3;
				myItem.name = context.getString(MENameID[i]);
				DeleteItemList.add(myItem);
				MELOG.v("ME_RTFSC", "DeleteItemList:name" + myItem.name);
			}
		}
		if (DeleteItemList.size() >= 1) {
			MELOG.v("ME_RTFSC", "DeleteItemList.size()" + DeleteItemList.size());
			MicroEntryLog.LogDelete(context, DeleteItemList);
		}
	}

	private void UpdateActiveItemList(boolean[] visible, int type) {
		MELOG.v("ME_RTFSC", "===UpdateActiveItemList===");
		List<MicroEntryLogItem> ActiveItemList = null;
		MyR RR = MyR.getMyR(context);
		if (RR == null) {
			return;
		}
		int[] MENameID = { RR.string.cool_ml_app_name1,
				RR.string.cool_ml_app_name2, RR.string.cool_ml_app_name3,
				RR.string.cool_ml_app_name4 };
		// TODO Auto-generated method stub
		ActiveItemList = new ArrayList<MicroEntryLog.MicroEntryLogItem>();
		for (int i = 0; i < 4; i++) {
			if (true == visible[i] && true == checkActionCanEnable(context, i)) {
				MicroEntryLogItem myItem = new MicroEntryLogItem();
				myItem.id = i + 1;
				myItem.type = type;
				myItem.name = context.getString(MENameID[i]);
				ActiveItemList.add(myItem);
				MELOG.v("ME_RTFSC", "ActiveItemList:name" + myItem.name);
			}
		}
		if (ActiveItemList.size() >= 1) {
			MELOG.v("ME_RTFSC", "ActiveItemList.size()" + ActiveItemList.size());
			MicroEntryLog.LogActive(context, ActiveItemList);
		}
	}

	@Override
	public String getEntryID() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
