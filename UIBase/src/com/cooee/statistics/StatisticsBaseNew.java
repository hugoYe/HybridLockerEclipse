package com.cooee.statistics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cooee.statistics.databaseNew.ConfigDBNew;
import com.cooee.statistics.databaseNew.JournalDBNew;

/**
 * 
 * 基本统计Api
 * 
 */
public class StatisticsBaseNew {

	public static boolean enable_Statistics_LOG = false;
	private static Context mContext;
	public static final String LOG_URL = "http://uilog.coolauncher.com.cn/iloong/pui/LogEngine/DataService";
	public static final String LOG_URL_TEST = "http://58.246.135.237/iloong/pui/LogEngine/DataService";
	public static final String DEFAULT_KEY = "f24657aafcb842b185c98a9d3d7c6f4725f6cc4597c3a4d531c70631f7c7210fd7afd2f8287814f3dfa662ad82d1b02268104e8ab3b2baee13fab062b3d27bff";
	private static String DEFAULT_ERRTIME = "YYYYMMDDHHMMSS";
	private static final String ERRTIME = "ErrorTime";
	private static final String ERRCOUNT = "ErrorCount";
	private static final String SUCCESSTIME = "SuccessTime";
	private static String errtime = "YYYYMMDDHHMMSS";
	private static ArrayList<Integer> idSet = new ArrayList<Integer>();

	private enum TYPE {
		RETRYINTERVAL, // 重试
		ONEDAYINTERVAL // 一天后重试
	};

	private final static String TAG = "StatisticsBaseNew";
	public static String PATH_ENABLE_LOG = "enablelog.log";
	private static SharedPreferences preferences;
	public static final String ACTION_REGISTER = "0010";
	public static final String ACTION_DAILY_ATTENDANCE = "0011";
	public static final String ACTION_USE = "0012";
	public static final String ACTION_START_UP = "0013";
	public static final String ACTION_CONFIG_UPDATE = "0014";
	public static final String ACTION_START_DOWNLOAD = "0015";
	public static final String ACTION_INSTALL = "0016";
	public static final String ACTION_DEFAULT_THEME_REGISTER = "0021";
	public static final String ACTION_DEFAULT_THEME_USE = "0022";
	public static final String ACTION_DEFAULT_THEME_CHANGE = "0023";
	public static final String ACTION_OPEN_CONTROL_CENTER = "0036";
	public static final String ACTION_CLICK_HEART_APP = "0037";
	public static final String PREFERENCE_KEY = "statisticsNew";
	public static final String NEXT_0011_TIME_KEY = "next0011Time";
	private static boolean have_action_0011 = false;
	private static String IMSI = "";
	private static String default_theme_register = "";
	private static String default_theme_change = "";
	private static String register_id = "";

	private static Service service;

	public static void onEvent(Context context, String eventId, String params) {
		if (!isNeedLog(eventId)) {
			return;
		}
		if (enable_Statistics_LOG)
			Log.v("UIBase - StatisticsNew", TAG + "\n=onEvent" + "\n--eventId:"
					+ eventId + "\n--params :" + params);
		Intent intent = new Intent();
		intent.setClass(context, StatisticsServiceNew.class);
		intent.putExtra(StatisticsServiceNew.EXTRA_EVENT_ID, eventId);
		intent.putExtra(StatisticsServiceNew.EXTRA_PARAMS, params);
		context.startService(intent);
	}

	static void packageOnEvent(Service service, String eventId, String params) {
		StatisticsBaseNew.service = service;
		String order = eventId + "#" + params;
		boolean mFind12or13 = false;
		boolean mFind10or11 = false;
		if (mContext == null) {
			if (enable_Statistics_LOG)
				Log.v("UIBase - StatisticsNew",
						TAG
								+ "\n=packageOnEvent"
								+ "\n--[(mContext == null)] --------------------- return");
			return;
		}
		if (enable_Statistics_LOG)
			Log.v("UIBase - StatisticsNew", TAG + "\n=packageOnEvent"
					+ "\n--order:" + order);
		JournalDBNew journalDB = new JournalDBNew(mContext);
		journalDB.open();
		if ((eventId.equals(ACTION_REGISTER))
				|| (eventId.equals(ACTION_DAILY_ATTENDANCE))
				|| (eventId.equals(ACTION_OPEN_CONTROL_CENTER))
				|| (eventId.equals(ACTION_CLICK_HEART_APP))
				|| (eventId.equals(ACTION_USE))
				|| (eventId.equals(ACTION_START_UP)
						|| (eventId.equals(ACTION_DEFAULT_THEME_REGISTER))
						|| (eventId.equals(ACTION_DEFAULT_THEME_CHANGE)) || (eventId
							.equals(ACTION_DEFAULT_THEME_USE)))) {
			Cursor cursor = journalDB.getAllTitles();
			while (cursor.moveToNext()) {
				String id = cursor.getString(cursor
						.getColumnIndexOrThrow("_id"));
				String logtext = cursor.getString(cursor
						.getColumnIndexOrThrow("logtext"));
				String uploadtime = cursor.getString(cursor
						.getColumnIndexOrThrow("uploadtime"));
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew", TAG + "\n=packageOnEvent"
							+ "\n--id = " + Integer.valueOf(id)
							+ "  logText = " + logtext);
				String[] itemsTemp = logtext.split("#");
				String[] parmsTemp = params.split("#");
				if (itemsTemp.length >= 7) {
					if (((eventId.equals(ACTION_OPEN_CONTROL_CENTER))
							|| (eventId.equals(ACTION_CLICK_HEART_APP))
							|| (eventId.equals(ACTION_USE))
							|| (eventId.equals(ACTION_START_UP)) || (eventId
								.equals(ACTION_DEFAULT_THEME_USE)))
							&& (itemsTemp[0].equals(eventId))) {
						if (parmsTemp[4].equals(itemsTemp[5])) {
							mFind12or13 = true;
							int countInt = Integer.parseInt(itemsTemp[7]);
							if (parmsTemp[3].equals("2")) {// 2说明是附带内容产品，一天只需要统计一次
								if (mHandler != null && idSet.size() > 0) {// 存在上传线程
									mFind12or13 = false;
									break;
								} else {
									countInt++;
								}
							} else
								countInt++;
							String countStr = "" + countInt;
							long idLong = Long.parseLong(id);
							logtext = itemsTemp[0] + "#" + itemsTemp[1] + "#"
									+ itemsTemp[2] + "#" + itemsTemp[3] + "#"
									+ itemsTemp[4] + "#" + itemsTemp[5] + "#"
									+ itemsTemp[6] + "#" + countStr;
							journalDB.updateTitle(idLong, logtext);
							break;
						}
					} else if (((eventId.equals(ACTION_REGISTER)) || (eventId
							.equals(ACTION_DAILY_ATTENDANCE)
							|| (eventId.equals(ACTION_DEFAULT_THEME_REGISTER)) || (eventId
								.equals(ACTION_DEFAULT_THEME_CHANGE))))
							&& (itemsTemp[0].equals(eventId) && itemsTemp[5]
									.equals(parmsTemp[4]))// 包名
					) {
						mFind10or11 = true;
						break;
					}
				}
			}
			cursor.close();
		} else {
			journalDB.insertTitle(order);
		}
		if (((eventId.equals(ACTION_OPEN_CONTROL_CENTER))
				|| (eventId.equals(ACTION_CLICK_HEART_APP))
				|| (eventId.equals(ACTION_USE))
				|| (eventId.equals(ACTION_START_UP)) || (eventId
					.equals(ACTION_DEFAULT_THEME_USE))) && (!mFind12or13)) {
			journalDB.insertTitle(order);
		}
		if (((eventId.equals(ACTION_REGISTER)) || (eventId
				.equals(ACTION_DAILY_ATTENDANCE)
				|| (eventId.equals(ACTION_DEFAULT_THEME_REGISTER)) || (eventId
					.equals(ACTION_DEFAULT_THEME_CHANGE))))
				&& (!mFind10or11)) {
			journalDB.insertTitle(order);
		}
		journalDB.close();
		if (mHandler != null) {
			if (enable_Statistics_LOG)
				Log.v("UIBase - StatisticsNew", TAG + "\n=packageOnEvent"
						+ "\n--存在上传线程 ---------------------return");
			return;
		}
		checkThread();
		if (!exitErrTime()) {
			if (enable_Statistics_LOG)
				Log.v("UIBase - StatisticsNew", TAG + "\n=packageOnEvent"
						+ "\n--不存在错误时间");
			if (SuccessTimeTransfinite()) {
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew", TAG + "\n=packageOnEvent"
							+ "\n--成功时间超过时间间隔 ==========================");
				// 上传数据
				mHandler.post(UploadRun);
			} else {
				exitThread();
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew",
							TAG
									+ "\n=packageOnEvent"
									+ "\n--exitThread成功时间没有超过时间间隔 ---------------------exitThread--------------------");
			}
		} else {
			if (enable_Statistics_LOG)
				Log.v("UIBase - StatisticsNew", TAG + "\n=packageOnEvent"
						+ "\n--存在错误时间");
			if (getErrTimes() > 3 && ErrTimeTransfinite(TYPE.ONEDAYINTERVAL)) {
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew", TAG + "\n=packageOnEvent"
							+ "\n--错误时间超过一小时 ==========================");
				// 更新失败次数&失败时间
				recordErrCount(1);
				recordErrTime();
				// 上传数据
				mHandler.post(UploadRun);
			} else if (getErrTimes() <= 3
					&& ErrTimeTransfinite(TYPE.RETRYINTERVAL)) {
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew", TAG + "\n=packageOnEvent"
							+ "\n--错误时间间隔超过1分钟 ========================== ");
				// 上传数据
				mHandler.post(UploadRun);
			} else {
				exitThread();
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew",
							TAG
									+ "\n=packageOnEvent"
									+ "\n--exitThread错误时间没有超过时间间隔 ---------------------exitThread-------------------- ");
			}
		}
	}

	/**
	 * 判断错误时间是否到达时间间隔
	 */
	private static boolean ErrTimeTransfinite(TYPE type) {
		if (errtime.equals(DEFAULT_ERRTIME)) {
			if (enable_Statistics_LOG)
				Log.v("UIBase - StatisticsNew", TAG + "\n=ErrTimeTransfinite"
						+ "\n--DEFAULT_ERRTIME ");
			return false;
		} else {
			int errtime1 = Integer.parseInt(errtime.substring(4, 12));
			int curtime = Integer.parseInt(getCurTime().substring(4, 12));
			if (enable_Statistics_LOG)
				Log.v("UIBase - StatisticsNew", TAG + "\n=ErrTimeTransfinite"
						+ "\n--errtime1:" + errtime1 + "\n--curtime :"
						+ curtime);
			if (type == TYPE.RETRYINTERVAL) {
				if (curtime - errtime1 > 1 || curtime - errtime1 < 0) {
					return true;
				}
			} else if (type == TYPE.ONEDAYINTERVAL) {
				if (curtime - errtime1 > 10000 || curtime - errtime1 < 0) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 判断成功后是否到达时间间隔
	 */
	private static boolean SuccessTimeTransfinite() {
		if (mContext == null) {
			return false;
		}
		ConfigDBNew configdb = new ConfigDBNew(mContext);
		configdb.open();
		String successtime = configdb.getTitle(SUCCESSTIME);
		configdb.close();
		if (successtime.equals(DEFAULT_ERRTIME)) {
			return true;
		} else {
			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(df.parse(successtime));
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(df.parse(getCurTime()));
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew", TAG
							+ "\n=SuccessTimeTransfinite" + "\n--successtime1:"
							+ successtime + "\n--curtime     :" + getCurTime());
				long delta = calendar1.getTimeInMillis()
						- calendar.getTimeInMillis();
				if (delta > StaticClassNew.DELAY * 60 * 1000 || delta < 0) {
					return true;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	}

	private static HandlerThread handlerThread;
	private static Handler mHandler;
	private static Object threadSync = new Object();

	private static void checkThread() {
		synchronized (threadSync) {
			if (handlerThread == null) {
				handlerThread = new HandlerThread("handlerThread");
				handlerThread.start();
				mHandler = new Handler(handlerThread.getLooper());
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew", TAG + "\n=checkThread");
			}
		}
	}

	private static void exitThread() {
		synchronized (threadSync) {
			if (handlerThread != null) {
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew", TAG + "\n=exitThread");
				handlerThread.quit();
				handlerThread = null;
				mHandler = null;
				if (service != null)
					service.stopSelf();
				// System.exit( 0 );
			}
		}
	}

	/**
	 * 设置应用的context
	 * 
	 */
	public static void setApplicationContext(Context context) {
		// String sdpath = getSDPath();
		// if (sdpath!=null)
		// {
		// PATH_ENABLE_LOG= sdpath + File.separator + "enablelog.log";
		// File dir = new File(PATH_ENABLE_LOG);
		// if (dir.exists()) {
		// enable_Statistics_LOG = true;
		// }
		// }
		preferences = context.getSharedPreferences(PREFERENCE_KEY,
				Activity.MODE_PRIVATE);
		new CustomerHttpClientNew(context);
	}

	public static String getSDPath() {
		File SDdir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			SDdir = Environment.getExternalStorageDirectory();
		}
		if (SDdir != null) {
			return SDdir.toString();
		} else {
			return null;
		}
	}

	/**
	 * 内部设置appContext
	 * 
	 * @param context
	 */
	static void packageSetAppContext(Context context) {
		if (context != null) {
			mContext = context.getApplicationContext();
		} else {
			mContext = context;
		}
	}

	/**
	 * 获取应用的context
	 * 
	 */
	public static Context getApplicationContext() {
		return mContext;
	}

	// 上传线程
	private static Runnable UploadRun = new Runnable() {

		public void run() {
			mHandler.removeCallbacks(UploadRun);
			if (enable_Statistics_LOG)
				Log.v("UIBase - StatisticsNew", TAG + "\n=UploadRun"
						+ "\n--上传线程");
			// 判断是否联网
			if (!IsHaveInternet(mContext)) {
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew", TAG + "\n=UploadRun"
							+ "\n--没有线程");
				exitThread();
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew",
							TAG
									+ "\n=UploadRun"
									+ "\n--没有网退出线程"
									+ "---------------------exitThread--------------------");
				return;
			} else {
				// if (longTimeOutLine()) {// 长时间没联网
				// // 删除七天前的数据
				// deleteUnuseData();
				// if(enable_Statistics_LOG)
				// Log.v("UIBase - StatisticsNew",
				// TAG
				// + "\n=UploadRun"
				// + "\n--长时间没联网 - 删除七天前的数据"
				// );
				// }
				String params = getLogInformation();
				String url = LOG_URL;
				if (params != null) {
					String[] res = CustomerHttpClientNew.post(url, params,
							mContext);
					if (res != null) {
						String content = res[0];
						JSONObject json = null;
						try {
							json = new JSONObject(content);
							int retCode = json.getInt("retcode");
							if (retCode == 0) {
								if (have_action_0011) {
									setNextLog0011Time();
								}
								// IMSI为空，不删除0010在logTable中的记录，下次再传
								if (IMSI.equals("")) {
									if (!register_id.equals("")) {
										idSet.remove(Integer
												.valueOf(register_id));
										register_id = "";
									}
									if (!default_theme_register.equals("")) {
										idSet.remove(Integer
												.valueOf(default_theme_register));
										default_theme_register = "";
									}
									if (!default_theme_change.equals("")) {
										idSet.remove(Integer
												.valueOf(default_theme_change));
										default_theme_change = "";
									}
								}
								delAllData();
								clearErrTimeAndCount();
								recordSuccessTime();
								if (enable_Statistics_LOG)
									Log.v("UIBase - StatisticsNew",
											TAG
													+ "\n=UploadRun"
													+ "\n--上传成功 =============ok===================="
													+ "\n--params    = "
													+ params);
							} else {
								int errcount = getErrTimes();
								recordErrCount(++errcount);
								recordErrTime();
								if (enable_Statistics_LOG)
									Log.v("UIBase - StatisticsNew",
											TAG
													+ "\n=UploadRun"
													+ "\n--上传失败[0]  错误次数 = "
													+ errcount
													+ "=============not ok===================="
													+ "\n--params    = "
													+ params);
							}
						} catch (JSONException e) {
							int errcount = getErrTimes();
							recordErrCount(++errcount);
							recordErrTime();
							if (enable_Statistics_LOG)
								Log.v("UIBase - StatisticsNew",
										TAG
												+ "\n=UploadRun"
												+ "\n--上传失败[1]  错误次数 = "
												+ errcount
												+ "=============not ok===================="
												+ "\n--params    = " + params);
							e.printStackTrace();
						}
					}
				}
				if (enable_Statistics_LOG)
					Log.v("UIBase - StatisticsNew", TAG + "\n=UploadRun"
							+ "\n--关闭上传线程");
				exitThread();
			}
		}
	};

	private static boolean exitErrTime() {
		if (mContext == null) {
			return true;
		}
		ConfigDBNew configdb = new ConfigDBNew(mContext);
		configdb.open();
		errtime = configdb.getTitle(ERRTIME);
		configdb.close();
		if (errtime.equals(DEFAULT_ERRTIME)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 清除错误时间和次数
	 */
	private static void clearErrTimeAndCount() {
		if (mContext != null) {
			ConfigDBNew configdb = new ConfigDBNew(mContext);
			configdb.open();
			configdb.updateTitle(ERRTIME, DEFAULT_ERRTIME);
			configdb.updateTitle(ERRCOUNT, "0");
			configdb.close();
		}
	}

	/**
	 * 记录上传失败超三次的时间
	 */
	private static void recordErrTime() {
		if (mContext != null) {
			ConfigDBNew configdb = new ConfigDBNew(mContext);
			configdb.open();
			configdb.updateTitle(ERRTIME, getCurTime());
			configdb.close();
		}
	}

	/**
	 * 记录上传成功的时间
	 */
	private static void recordSuccessTime() {
		if (mContext != null) {
			ConfigDBNew configdb = new ConfigDBNew(mContext);
			configdb.open();
			configdb.updateTitle(SUCCESSTIME, getCurTime());
			configdb.close();
		}
	}

	/**
	 * 记录上传错误的次数
	 */
	private static void recordErrCount(int count) {
		if (mContext != null) {
			ConfigDBNew configdb = new ConfigDBNew(mContext);
			configdb.open();
			configdb.updateTitle(ERRCOUNT, Integer.toString(count));
			configdb.close();
		}
	}

	/**
	 * 清除日志信息
	 */
	private static void delAllData() {
		if (mContext != null) {
			JournalDBNew journalDB = new JournalDBNew(mContext);
			journalDB.open();
			for (int i = 0; i < idSet.size(); i++) {
				journalDB.deleteTitle(idSet.get(i));
			}
			idSet.clear();
			journalDB.close();
		}
	}

	/**
	 * 获取错误次数
	 */
	private static int getErrTimes() {
		if (mContext == null) {
			return 0;
		}
		ConfigDBNew configdb = new ConfigDBNew(mContext);
		configdb.open();
		int count = Integer.parseInt(configdb.getTitle(ERRCOUNT));
		if (enable_Statistics_LOG)
			Log.v("UIBase - StatisticsNew", TAG + "\n=getErrTimes"
					+ "\n--geterrtimes:" + count);
		configdb.close();
		return count;
	}

	/**
	 * 判断是否联网
	 */
	private static boolean IsHaveInternet(final Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manger.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取<=50条日志信息并拼接
	 */
	private static String getLogInformation() {
		String text = "";
		if (mContext == null) {
			return null;
		}
		have_action_0011 = false;
		JournalDBNew journalDB = new JournalDBNew(mContext);
		journalDB.open();
		Cursor cursor = journalDB.getAllTitles();
		boolean isSingleLog = false;
		String log0017 = null;
		String log0017List = null;
		for (int i = 0; i < StaticClassNew.DATANUM && cursor.moveToNext(); i++) {
			String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
			String logtext = cursor.getString(cursor
					.getColumnIndexOrThrow("logtext"));
			// 获得0010在logTable中的id
			String[] itemsTemp = logtext.split("#");
			if (itemsTemp[0].equals(ACTION_REGISTER)) {
				register_id = id;
			}
			if (itemsTemp[0].equals(ACTION_DEFAULT_THEME_REGISTER)) {
				default_theme_register = id;
			}
			if (itemsTemp[0].equals(ACTION_DEFAULT_THEME_CHANGE)) {
				default_theme_change = id;
			}
			idSet.add(Integer.valueOf(id));
			if (enable_Statistics_LOG)
				Log.v("UIBase - StatisticsNew", TAG + "\n=getLogInformation"
						+ "\n--id = " + Integer.valueOf(id) + "  logText = "
						+ logtext);
			if (cursor.getCount() == 1) {
				text = getParams(logtext, true);
				isSingleLog = true;
				break;
			} else {
				if (log0017 == null) {
					log0017 = getParams0017NoMd5(logtext);
				}
				text = getParams(logtext, false);
				log0017List = getParams0017List(log0017List, text);
			}
		}
		cursor.close();
		journalDB.close();
		if (!isSingleLog) {
			if ((log0017 != null) && (log0017List != null)) {
				text = getParams0017WithMd5(log0017, log0017List);
			} else {
				text = null;
			}
		}
		if (enable_Statistics_LOG)
			Log.v("UIBase - StatisticsNew", TAG + "\n=getLogInformation"
					+ "\n--informationsize = " + idSet.size() + "\n--text = "
					+ text);
		return text;
	}

	/**
	 * 获取当前的时间
	 */
	private static String getCurTime() {
		String time = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		time = formatter.format(curDate);
		return time;
	}

	private static String getParams(String logtext, boolean isAddMd5) {
		/*
		 * // logtext: // Action String // sn String // appid String // shellid
		 * String // producttype int // productname String // opversion String
		 */
		String[] itemsTemp = logtext.split("#");
		if (itemsTemp.length < 7) {
			return null;
		}
		String action = itemsTemp[0];
		String sn = itemsTemp[1];
		String appid = itemsTemp[2];
		String shellid = itemsTemp[3];
		int producttype = Integer.parseInt(itemsTemp[4]);
		String productname = itemsTemp[5];
		String opversion = itemsTemp[6];
		// if(
		// (appid.equals(""))
		// || (sn.equals(""))
		// || (shellid.equals(""))
		// )
		// {
		// return null;
		// }
		if (action.equals(ACTION_DAILY_ATTENDANCE)) {
			have_action_0011 = true;
		}
		int count = 0;
		String param1 = "";
		String param2 = "";
		String param3 = "";
		String param4 = "";
		if ((action.equals(ACTION_OPEN_CONTROL_CENTER))
				|| (action.equals(ACTION_CLICK_HEART_APP))
				|| (action.equals(ACTION_USE))
				|| (action.equals(ACTION_START_UP))
				|| (action.equals(ACTION_DEFAULT_THEME_USE))) {
			if (itemsTemp.length == 8) {
				count = Integer.parseInt(itemsTemp[7]);
			}
		} else if ((action.equals(ACTION_START_DOWNLOAD))
				|| (action.equals(ACTION_INSTALL))) {
			if (itemsTemp.length == 11) {
				param1 = itemsTemp[7];
				param2 = itemsTemp[8];
				param3 = itemsTemp[9];
				param4 = itemsTemp[10];
			}
		}
		int networktype = -1;
		int networksubtype = -1;
		ConnectivityManager connMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (netInfo != null) {
			networktype = netInfo.getType();
			networksubtype = netInfo.getSubtype();
		}
		PackageManager pm;
		JSONObject res;
		pm = mContext.getPackageManager();
		res = new JSONObject();
		try {
			res.put("Action", action);
			if (isAddMd5) {
				res.put("packname", mContext.getPackageName());
				res.put("versioncode",
						pm.getPackageInfo(mContext.getPackageName(), 0).versionCode);
				res.put("versionname",
						pm.getPackageInfo(mContext.getPackageName(), 0).versionName);
				res.put("sn", sn);
				// res.put("appid", appid);
				// res.put("shellid", shellid);
				res.put("uuid", getMyUUID(mContext));
				TelephonyManager mTelephonyMgr = (TelephonyManager) mContext
						.getSystemService(Context.TELEPHONY_SERVICE);
				IMSI = mTelephonyMgr.getSubscriberId() == null ? ""
						: mTelephonyMgr.getSubscriberId();
				res.put("imsi", IMSI);
				res.put("iccid",
						mTelephonyMgr.getSimSerialNumber() == null ? ""
								: mTelephonyMgr.getSimSerialNumber());
				res.put("imei", mTelephonyMgr.getDeviceId());
				res.put("phone", mTelephonyMgr.getLine1Number() == null ? ""
						: mTelephonyMgr.getLine1Number());
				res.put("localtime", getCurTime());
				res.put("model", Build.MODEL);
				res.put("display", Build.DISPLAY);
				res.put("product", Build.PRODUCT);
				res.put("device", Build.DEVICE);
				res.put("board", Build.BOARD);
				res.put("manufacturer", Build.MANUFACTURER);
				res.put("brand", Build.BRAND);
				res.put("hardware", Build.HARDWARE);
				res.put("buildversion", Build.VERSION.RELEASE);
				res.put("sdkint", Build.VERSION.SDK_INT);
				res.put("androidid", android.provider.Settings.Secure
						.getString(mContext.getContentResolver(),
								android.provider.Settings.Secure.ANDROID_ID));
				res.put("buildtime", Build.TIME);
				res.put("heightpixels", mContext.getResources()
						.getDisplayMetrics().heightPixels);
				res.put("widthpixels", mContext.getResources()
						.getDisplayMetrics().widthPixels);
				res.put("networktype", networktype);
				res.put("networksubtype", networksubtype);
				res.put("opversion", opversion);
			}
			res.put("appid", appid);
			res.put("shellid", shellid);
			res.put("producttype", producttype);
			res.put("productname", productname);
			res.put("count", count);
			if ((action.equals(ACTION_START_DOWNLOAD))
					|| (action.equals(ACTION_INSTALL))) {
				res.put("param1", param1);
				res.put("param2", param2);
				res.put("param3", param3);
				res.put("param4", param4);
			}
			String content = res.toString();
			String params = null;
			if (isAddMd5) {
				String md5_res = getMD5EncruptKey(content + DEFAULT_KEY);
				// res.put("md5", md5_res);
				String newContent = content.substring(0,
						content.lastIndexOf('}'));
				params = newContent + ",\"md5\":\"" + md5_res + "\"}";
			} else {
				params = content;
			}
			return params;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String getMyUUID(Context context) {
		String androidId;
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), androidId.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}

	private static String getMD5EncruptKey(String logInfo) {
		String res = null;
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		messagedigest.update(logInfo.getBytes());
		res = bufferToHex(messagedigest.digest());
		// Log.v("http", "getMD5EncruptKey res =  " + res);
		return res;
	}

	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4]; // 取字节中高 4 位的数字转换, >>>
												// 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
		char c1 = hexDigits[bt & 0xf]; // 取字节中低 4 位的数字转换
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	private static String getParams0017NoMd5(String logtext) {
		/*
		 * // logtext: // Action String // sn String // appid String // shellid
		 * String // producttype int // productname String // opversion String
		 */
		String[] itemsTemp = logtext.split("#");
		if (itemsTemp.length < 7) {
			return null;
		}
		String action = "0017";
		String sn = itemsTemp[1];
		String appid = itemsTemp[2];
		String shellid = itemsTemp[3];
		int producttype = Integer.parseInt(itemsTemp[4]);
		String productname = itemsTemp[5];
		String opversion = itemsTemp[6];
		int count = 0;
		int networktype = -1;
		int networksubtype = -1;
		ConnectivityManager connMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (netInfo != null) {
			networktype = netInfo.getType();
			networksubtype = netInfo.getSubtype();
		}
		PackageManager pm;
		JSONObject res;
		pm = mContext.getPackageManager();
		res = new JSONObject();
		try {
			res.put("Action", action);
			res.put("packname", mContext.getPackageName());
			res.put("versioncode",
					pm.getPackageInfo(mContext.getPackageName(), 0).versionCode);
			res.put("versionname",
					pm.getPackageInfo(mContext.getPackageName(), 0).versionName);
			res.put("sn", sn);
			res.put("appid", appid);
			res.put("shellid", shellid);
			res.put("uuid", getMyUUID(mContext));
			TelephonyManager mTelephonyMgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			IMSI = mTelephonyMgr.getSubscriberId() == null ? "" : mTelephonyMgr
					.getSubscriberId();
			res.put("imsi", IMSI);
			res.put("iccid", mTelephonyMgr.getSimSerialNumber() == null ? ""
					: mTelephonyMgr.getSimSerialNumber());
			res.put("imei", mTelephonyMgr.getDeviceId() == null ? ""
					: mTelephonyMgr.getDeviceId());
			res.put("phone", mTelephonyMgr.getLine1Number() == null ? ""
					: mTelephonyMgr.getLine1Number());
			res.put("localtime", getCurTime());
			res.put("model", Build.MODEL);
			res.put("display", Build.DISPLAY);
			res.put("product", Build.PRODUCT);
			res.put("device", Build.DEVICE);
			res.put("board", Build.BOARD);
			res.put("manufacturer", Build.MANUFACTURER);
			res.put("brand", Build.BRAND);
			res.put("hardware", Build.HARDWARE);
			res.put("buildversion", Build.VERSION.RELEASE);
			res.put("sdkint", Build.VERSION.SDK_INT);
			res.put("androidid", android.provider.Settings.Secure.getString(
					mContext.getContentResolver(),
					android.provider.Settings.Secure.ANDROID_ID));
			res.put("buildtime", Build.TIME);
			res.put("heightpixels",
					mContext.getResources().getDisplayMetrics().heightPixels);
			res.put("widthpixels",
					mContext.getResources().getDisplayMetrics().widthPixels);
			res.put("networktype", networktype);
			res.put("networksubtype", networksubtype);
			res.put("producttype", producttype);
			res.put("productname", productname);
			res.put("count", count);
			res.put("opversion", opversion);
			String content = res.toString();
			String params = content;
			return params;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String getParams0017List(String logtext, String listItem) {
		JSONArray array = null;
		if (logtext == null) {
			array = new JSONArray();
		} else {
			try {
				array = new JSONArray(logtext);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JSONObject res = null;
		try {
			res = new JSONObject(listItem);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		array.put(res);
		return array.toString();
	}

	private static String getParams0017WithMd5(String logtext, String listItems) {
		JSONArray array = null;
		try {
			array = new JSONArray(listItems);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject res = null;
		try {
			res = new JSONObject(logtext);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			res.put("list", array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String content = res.toString();
		String md5_res = getMD5EncruptKey(content + DEFAULT_KEY);
		String newContent = content.substring(0, content.lastIndexOf('}'));
		String params = newContent + ",\"md5\":\"" + md5_res + "\"}";
		return params;
	}

	private static boolean isNeedLog(String eventId) {
		if (eventId.equals(ACTION_DAILY_ATTENDANCE)) {
			long next0011Time = preferences.getLong(NEXT_0011_TIME_KEY, 0);
			if (next0011Time != 0) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - next0011Time < (1000 * 3600 * 24) /* 大于一天，触发统计 */) {
					return false;
				}
			}
		}
		return true;
	}

	private static void setNextLog0011Time() {
		if (preferences == null) {
			preferences = mContext.getSharedPreferences(PREFERENCE_KEY,
					Activity.MODE_PRIVATE);
		}
		preferences.edit()
				.putLong(NEXT_0011_TIME_KEY, System.currentTimeMillis())
				.commit();
	}

	public static String md5Picture(Context context, String fileName) {
		InputStream inputStream = null;
		String res = null;
		byte buf[] = new byte[1024];
		int len = 0;
		MessageDigest messagedigest;
		AssetManager assetManager = context.getAssets();
		try {
			inputStream = assetManager.open(fileName);
			messagedigest = MessageDigest.getInstance("MD5");
			while ((len = inputStream.read(buf)) > 0) {
				messagedigest.update(buf, 0, len);
			}
			res = bufferToHex(messagedigest.digest());
		} catch (IOException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.v("UIBase - StatisticsNew", TAG + "\n--text = " + res);
		return res;
	}
}
