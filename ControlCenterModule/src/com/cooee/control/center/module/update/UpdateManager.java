package com.cooee.control.center.module.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import cool.sdk.common.CoolHttpClient;
import cool.sdk.common.JsonUtil;
import cool.sdk.common.CoolHttpClient.ResultEntity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UpdateManager {
	private Context mContext;
	private final String texUrl = "http://www.coolauncher.cn/locker/h5s02version.txt";
	private String downUrl = "http://www.coolauncher.cn/locker/h5style02.zip";

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	public static final String urls[] = new String[] { "192.168.1.222:85",
			"192.168.1.222:86", "192.168.1.222:87", "192.168.1.222:88" };

	public static final String getDataServerUrl() {
		// return "http://" + urls[new Random().nextInt(urls.length)]
		// + "/iloong/pui/ServicesEngineV1/DataService";
		return "http://uifolder.coolauncher.com.cn/iloong/pui/ServicesEngineV1/DataService";
	}

	public synchronized String checkVersion() {
		JSONObject reqJson = null;
		try {
			reqJson = JsonUtil.NewRequestJSON(mContext, 4, "uiupdate");
			reqJson.put("Action", "3006");
			reqJson.put("p1", 0);
			reqJson.put("p2", 0);// 1:用户主动更新，0：后台自动更新
			reqJson.put("p3", Locale.getDefault().toString());
			reqJson.put("p4", 0);
			Log.v("web", "proxy req:" + reqJson.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (reqJson != null) {
			ResultEntity result = CoolHttpClient.postEntity(getDataServerUrl(),
					reqJson.toString());
			if (result.exception != null) {
				Log.v("web", "proxy rsp:(error)" + result.httpCode + " "
						+ result.exception);
				return null;
			}
			Log.v("web", "proxy rsp:" + result.httpCode + " " + result.content);
			JSONObject resJson = null;
			try {
				resJson = new JSONObject(result.content);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (resJson == null)
				return null;
			int retcode = resJson.optInt("retcode");
			Log.v("web", "proxy resJson retcode=" + retcode);
			if (retcode == 0) {
				SharedPreferences sharedPrefer = mContext.getSharedPreferences(
						"Update", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPrefer.edit();
				String update = resJson.optString("update");
				try {
					JSONObject resJsonUpdate = new JSONObject(update);
					String config = resJsonUpdate.optString("config");
					if (config != null && !config.equals("")) {
						JSONObject resJsonConfig = new JSONObject(config);
						if (resJsonConfig != null) {
							String isopen = resJsonConfig.optString("isopen");
							if (isopen != null && isopen.equals("1")) {
								String acttime = resJsonConfig
										.optString("acttime");
								if (acttime != null && !acttime.equals("")) {
									editor.putLong("acttime",
											Long.parseLong(acttime) * 1000);
								}
								String is3g = resJsonConfig.optString("is3g");
								if (is3g != null && !is3g.equals("")) {
									editor.putString("is3g", is3g);
								}

								String jar = resJsonUpdate.optString("jar");
								JSONObject resJsonJar = new JSONObject(jar);
								String jarUrl = resJsonJar.optString("url");
								String jarSize = resJsonJar.optString("size");
								editor.putString("jar_url", jarUrl);
								editor.putLong("jar_size",
										Long.parseLong(jarSize));

								String apk = resJsonUpdate.optString("apk");
								JSONObject resJsonApk = new JSONObject(apk);
								String apkUrl = resJsonApk.optString("url");
								String apkSize = resJsonApk.optString("size");
								editor.putString("apk_url", apkUrl);
								editor.putLong("apk_size",
										Long.parseLong(apkSize));

								String zipData = null;
								String zip = resJsonUpdate.optString("zip");
								JSONObject resJsonZip = new JSONObject(zip);
								if (resJsonZip != null) {
									String url = resJsonZip.optString("url");
									String vcode = resJsonZip
											.optString("vcode");
									String size = resJsonZip.optString("size");
									if (vcode != null && !vcode.equals("")) {
										long version = Long.parseLong(vcode);
										if (version > sharedPrefer.getLong(
												"zip_version", 0)) {
											editor.putLong("zip_version",
													version);
											zipData = url + ";" + size;
										}
									}
								}
								editor.commit();
								return zipData;
							}
						}
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

			}
			return null;
		}
		return null;
	}

	/**
	 * 检查软件是否有更新版本
	 */
	public String isUpdate() {
		SharedPreferences sharedPrefer = mContext.getSharedPreferences(
				"Update", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefer.edit();
		int curVersion = sharedPrefer.getInt("h5version", 10);
		HttpURLConnection conn = null;
		InputStream inStream = null;
		try {
			URL url = new URL(texUrl);
			conn = (HttpURLConnection) url.openConnection();
			inStream = conn.getInputStream();
			String strVersion[] = getString(inStream).split(",");
			if (strVersion != null) {
				if (strVersion.length > 0) {
					int updateVersion = Integer.parseInt(strVersion[0]);
					if (updateVersion > curVersion) {
						editor.putInt("h5version", updateVersion);
						editor.commit();
						return downUrl;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}

	private String getString(InputStream inputStream) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "gbk");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				// sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
