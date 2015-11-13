package com.iLoong.launcher.MList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class Main_SecondActivity extends Activity {

	int app_id = 2;

	public int getId() {
		return app_id;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// MeGeneralMethod.CanelKillProcess();
		super.onCreate(savedInstanceState);

		if (MainActivity.instance != null
				&& MainActivity.instance.getId() != app_id) {
			MainActivity.instance.finish();
		}
		MELOG.v("ME_RTFSC", "==== Main_SecondActivity  onCreate ====");
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), MainActivity.class);
		intent.putExtra("APP_ID", getId());
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
