package com.coco.lock2.app.Pee.common;




import com.coco.lock2.app.Pee.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class LoadLockBoxActivity extends Activity {
	private Dialog noticeDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 閸樼粯甯�弽鍥暯閺嶏拷
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		if (StaticClass.isLockBoxInstalled(this)) {
			
//			PackageManager p = getPackageManager();
//			p.setComponentEnabledSetting(new ComponentName(this,
//					LoadLockBoxActivity.class),
//					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//					PackageManager.DONT_KILL_APP);
			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setPackage(StaticClass.LOCKBOX_PACKAGE_NAME);
			this.startActivity(intent);
			
			finish();
		} else {
			// 鐟欙綁鏀ｉ惄鎺戠摍娑撳秴鐡ㄩ崷锟�			showNoticeDialog();
		}
	}

	/**
	 * 閼惧嘲褰嘇PK娑撳娴囬惃鍓坮l
	 */
	private String getApkUrl() {
		String url = "http://ku01.coomoe.com/uiv2/getApp.ashx?p01=com.coco.lock2.lockbox&p06=1&"
				+ getPhoneParams();
		return url;
	}

	/**
	 * 閼惧嘲褰囬幍瀣簚閻ㄥ嫬鍙炬禒鏍︿繆閹拷
	 */

	private String getPhoneParams() {

		QueryStringBuilder builder = new QueryStringBuilder();
		builder.add("a01", Build.MODEL).add("a02", Build.DISPLAY)
				.add("a05", Build.PRODUCT).add("a06", Build.DEVICE)
				.add("a07", Build.BOARD).add("a08", Build.MANUFACTURER)
				.add("a09", Build.BRAND).add("a12", Build.HARDWARE)
				.add("a14", Build.VERSION.RELEASE)
				.add("a15", Build.VERSION.SDK_INT);

		{
			WindowManager winMgr = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display display = winMgr.getDefaultDisplay();
			int scrWidth = display.getWidth();
			int scrHeight = display.getHeight();

			builder.add("a04", String.format("%dX%d", scrWidth, scrHeight));
		}

		{
			TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			if (telMgr != null) {
				builder.add("u01", telMgr.getSubscriberId())
				// IMSI
						.add("u03", telMgr.getDeviceId())
						// IMEI
						.add("u04", telMgr.getSimSerialNumber())
						// ICCID
						.add("u05", telMgr.getLine1Number());
			}
		}

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (netInfo != null) {
			// 閺堫剚婧�崣椋庣垳
			builder.add("u07", netInfo.getTypeName());
		}

		return builder.toString();
	}

	/**
	 * 閺勫墽銇氭潪顖欐閺囧瓨鏌婄�纭呯樈濡楋拷
	 */
	public void showNoticeDialog() {
		// 閺嬪嫰锟界�纭呯樈濡楋拷
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.download_title);
		builder.setMessage(R.string.download_info);
		// 閺囧瓨鏌�
		builder.setPositiveButton(R.string.download, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.setClass(LoadLockBoxActivity.this, DownloadService.class);
				String fileName = getResources().getString(
						R.string.server_download_file_name);
				intent.putExtra(DownloadService.DOWNLOAD_FILE_NAME, fileName);
				String url = getApkUrl();
				intent.putExtra(DownloadService.DOWNLOAD_URL_KEY, url);
				startService(intent);
				finish();
			}
		});
		// 缁嬪秴鎮楅弴瀛樻煀
		builder.setNegativeButton(R.string.download_later,
				new OnClickListener() {
				
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				finish();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public static int getProxyPort(Context context) {
		return Proxy.getPort(context);
	}

	public static boolean isCWWAPConnect(Context context) {
		boolean result = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			if (Proxy.getDefaultHost() != null
					|| Proxy.getHost(context) != null) {
				result = true;
			}
		}
		return result;
	}

	public static int getNetWorkType(Context context) {
		int netType = -1;

		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = manager.getSimOperator();
		if (simOperator != null) {
			if (simOperator.startsWith("46000")
					|| simOperator.startsWith("46002")) {
				netType = DownloadService.NETTYPE_MOBILE;
			} else if (simOperator.startsWith("46001")) {
				netType = DownloadService.NETTYPE_UNICOM;
			} else if (simOperator.startsWith("46003")) {
				netType = DownloadService.NETTYPE_TELECOM;
			}
		}
		return netType;
	}

	public static String getProxyHost(Context context) {
		return Proxy.getHost(context);
	}
}
