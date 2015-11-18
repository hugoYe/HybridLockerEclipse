package com.cooee.control.center.module.base;

import android.content.Intent;
import android.util.Log;

public class PluginTest {

	public void print(String contextPackage, String title, Intent intent) {
		Log.e("PluginTest", "######## contextPackage = " + contextPackage
				+ ", title = " + title + ", intent = " + intent.toString());
	}
}
