package com.cooee.statistics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * 
 * 扩展统计api
 * 
 */
public class StatisticsExpandNew {

	// Action String
	// sn String
	// appid String
	// shellid String
	// producttype int
	// productname String
	// opversion String
	private static final String CONFIG_FILE_NAME = "config.ini";

	private static class AppidAndSn {
		private String sn;
		private String appid;

		public String getSn() {
			return sn;
		}

		public void setSn(String sn) {
			this.sn = sn;
		}

		public String getAppid() {
			return appid;
		}

		public void setAppid(String appid) {
			this.appid = appid;
		}

	}

	private static String readTextFile(InputStream inputStream) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte buf[] = new byte[1024];

		int len;

		try {

			while ((len = inputStream.read(buf)) != -1) {

				outputStream.write(buf, 0, len);

			}
			outputStream.close();

			inputStream.close();

		} catch (IOException e) {

		}

		return outputStream.toString();

	}

	private static AppidAndSn procSNandAppid(Context context, int producttype,
			String productname) {
		if (producttype != 2)
			return null;

		String temSn = null;
		String temAppid = null;
		Context mContext = null;
		JSONObject jObject = null;
		InputStream inputStream = null;
		try {
			mContext = context.createPackageContext(productname,
					Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		AssetManager assetManager = mContext.getAssets();
		try {
			inputStream = assetManager.open(CONFIG_FILE_NAME);
			String config = readTextFile(inputStream);
			try {
				jObject = new JSONObject(config);

				JSONObject jRes = new JSONObject(jObject.getString("config"));
				temSn = jRes.getString("serialno");
				temAppid = jRes.getString("app_id");

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}

		} catch (IOException e) {
			Log.e("tag", e.getMessage());
			return null;
		}
		AppidAndSn appidsn = new AppidAndSn();
		appidsn.setAppid(temAppid);
		appidsn.setSn(temSn);
		return appidsn;
	}

	public static void register(Context context, String sn, String appid,
			String shellid, int producttype, String productname,
			String opversion) {
		AppidAndSn appidsn = procSNandAppid(context, producttype, productname);
		if (appidsn != null) {
			sn = appidsn.getSn();
			appid = appidsn.getAppid();
		}
		Log.v("UIbase--", "register:appid=" + appid + "sn=" + sn);

		String eventId = StatisticsBaseNew.ACTION_REGISTER;
		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion;
		StatisticsBaseNew.onEvent(context, eventId, params);
	}

	public static void dailyAttendance(Context context, String sn,
			String appid, String shellid, int producttype, String productname,
			String opversion) {
		AppidAndSn appidsn = procSNandAppid(context, producttype, productname);
		if (appidsn != null) {
			sn = appidsn.getSn();
			appid = appidsn.getAppid();
		}

		String eventId = StatisticsBaseNew.ACTION_DAILY_ATTENDANCE;
		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion;
		StatisticsBaseNew.onEvent(context, eventId, params);
	}

	public static void use(Context context, String sn, String appid,
			String shellid, int producttype, String productname,
			String opversion) {
		AppidAndSn appidsn = procSNandAppid(context, producttype, productname);
		if (appidsn != null) {
			sn = appidsn.getSn();
			appid = appidsn.getAppid();
		}
		Log.v("UIbase--", "use:appid=" + appid + "sn=" + sn);
		String eventId = StatisticsBaseNew.ACTION_USE;
		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion + "#" + "1";
		StatisticsBaseNew.onEvent(context, eventId, params);
	}

	public static void onEvent(Context context, String eventid, String sn, String appid,
			String shellid, int producttype, String productname,
			String opversion) {
		AppidAndSn appidsn = procSNandAppid(context, producttype, productname);
		if (appidsn != null) {
			sn = appidsn.getSn();
			appid = appidsn.getAppid();
		}
		Log.v("UIbase--", "use:appid=" + appid + "sn=" + sn);
		String eventId = eventid;
		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion + "#" + "1";
		StatisticsBaseNew.onEvent(context, eventId, params);
	}
	
	public static void startUp(Context context, String sn, String appid,
			String shellid, int producttype, String productname,
			String opversion) {
		AppidAndSn appidsn = procSNandAppid(context, producttype, productname);
		if (appidsn != null) {
			sn = appidsn.getSn();
			appid = appidsn.getAppid();
		}

		String eventId = StatisticsBaseNew.ACTION_START_UP;
		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion + "#" + "1";
		StatisticsBaseNew.onEvent(context, eventId, params);
	}

	public static void configUpdate(Context context, String sn, String appid,
			String shellid, int producttype, String productname,
			String opversion) {
		AppidAndSn appidsn = procSNandAppid(context, producttype, productname);
		if (appidsn != null) {
			sn = appidsn.getSn();
			appid = appidsn.getAppid();
		}

		String eventId = StatisticsBaseNew.ACTION_CONFIG_UPDATE;
		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion;
		StatisticsBaseNew.onEvent(context, eventId, params);
	}

	public static void startDownload(Context context, String sn, String appid,
			String shellid, int producttype, String productname,
			String opversion, String resid, String respackname,
			String versioncode, String versionname) {
		AppidAndSn appidsn = procSNandAppid(context, producttype, productname);
		if (appidsn != null) {
			sn = appidsn.getSn();
			appid = appidsn.getAppid();
		}

		String eventId = StatisticsBaseNew.ACTION_START_DOWNLOAD;
		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion + "#"
				+ resid + "#" + respackname + "#" + versioncode + "#"
				+ versionname;
		StatisticsBaseNew.onEvent(context, eventId, params);
	}

	public static void install(Context context, String sn, String appid,
			String shellid, int producttype, String productname,
			String opversion, String resid, String respackname,
			String versioncode, String versionname) {
		AppidAndSn appidsn = procSNandAppid(context, producttype, productname);
		if (appidsn != null) {
			sn = appidsn.getSn();
			appid = appidsn.getAppid();
		}

		String eventId = StatisticsBaseNew.ACTION_INSTALL;
		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion + "#"
				+ resid + "#" + respackname + "#" + versioncode + "#"
				+ versionname;
		StatisticsBaseNew.onEvent(context, eventId, params);
	}

	public static void register_default_theme(Context context, String sn,
			String appid, String shellid, int producttype, String productname,
			String opversion, boolean isChange) {
		String eventId = StatisticsBaseNew.ACTION_DEFAULT_THEME_REGISTER;
		if (isChange) {
			eventId = StatisticsBaseNew.ACTION_DEFAULT_THEME_CHANGE;
		}

		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion;
		StatisticsBaseNew.onEvent(context, eventId, params);
	}

	public static void useDefaultTheme(Context context, String sn,
			String appid, String shellid, int producttype, String productname,
			String opversion) {
		String eventId = StatisticsBaseNew.ACTION_DEFAULT_THEME_USE;
		String params = sn + "#" + appid + "#" + shellid + "#" + ""
				+ producttype + "#" + productname + "#" + opversion + "#" + "1";
		StatisticsBaseNew.onEvent(context, eventId, params);
	}

	public static void setStatiisticsLogEnable(boolean enable) {
		StatisticsBaseNew.enable_Statistics_LOG = enable;
	}

	public static String md5Picture(Context context, String fileName) {
		return StatisticsBaseNew.md5Picture(context, fileName);
	}
}
