package com.cooeelock.plugin.example;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class PluginTest {

	public static void print(String action, final JSONArray args) {

		String arg0 = "";
		long agr1 = -1;
		String arg2 = "";

		try {
			arg0 = args.getString(0);
			agr1 = args.getLong(1);
			arg2 = args.getString(2);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.e("PluginTest", "######## action = " + action + ", args[0] = "
				+ arg0 + ", args[1] = " + agr1 + ", args[2] = " + arg2);
	}
}
