package com.cooee.control.center.module.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;

public class UpdateManager {
	private Context mContext;
	private final String texUrl = "http://www.coolauncher.cn/locker/h5s01version.txt";

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 检查软件是否有更新版本
	 */
	public boolean isUpdate() {
		SharedPreferences sharedPrefer = mContext.getSharedPreferences(
				"Update", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefer.edit();
		int curVersion = sharedPrefer.getInt("version", 2);
		HttpURLConnection conn = null;
		InputStream inStream = null;
		try {
			URL url = new URL(texUrl);
			conn = (HttpURLConnection) url.openConnection();
			inStream = conn.getInputStream();
			String strVersion = getString(inStream);
			if (strVersion != null && !strVersion.equals("")) {
				int updateVersion = Integer.parseInt(strVersion);
				if (updateVersion > curVersion) {
					editor.putInt("version", updateVersion);
					editor.commit();
					return true;
				}
			} else {
				return false;
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
		return false;
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
