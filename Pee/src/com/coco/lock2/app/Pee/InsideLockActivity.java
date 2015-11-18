package com.coco.lock2.app.Pee;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.cooee.control.center.module.base.IWrap;

public class InsideLockActivity extends Activity {

	private ViewWrap wrap = null;

	@SuppressLint("JavascriptInterface")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		wrap = new ViewWrap(this);
		wrap.setKernelCallback(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case IWrap.KERNEL_EXIT:
					finish();
					return true;
				}
				return false;
			}
		});
		wrap.onCreate();
		setContentView(wrap.getView());
	}

	// 屏蔽下拉
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (Build.VERSION.SDK_INT < 14) {
			try {
				Object service = getSystemService("statusbar");
				Class<?> statusbarManager = Class
						.forName("android.app.StatusBarManager");
				Method test = statusbarManager.getMethod("collapse");
				test.invoke(service);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	// 屏蔽掉Home
	public void onAttachedToWindow() {
		if (Build.VERSION.SDK_INT < 14) {
			this.getWindow().setType(
					WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		}
		super.onAttachedToWindow();
	}

	// 屏蔽掉Back
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (wrap != null) {
			wrap.onResume();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (wrap != null) {
			wrap.onDestroy();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wrap != null) {
			wrap.onPause();
		}
	}

}
