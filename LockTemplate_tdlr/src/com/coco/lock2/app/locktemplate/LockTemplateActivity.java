package com.coco.lock2.app.locktemplate;

import java.lang.reflect.Method;

import com.cooee.control.center.module.base.IWrap;

import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.KeyEvent;
import android.view.WindowManager;

public class LockTemplateActivity extends Activity {

	private TempWrap wrap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wrap = new TempWrap(this);
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

	// 屏蔽掉Back键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		AudioManager audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_CAMERA:
		case KeyEvent.KEYCODE_MENU:
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (audio.isMusicActive()) {
				audio.adjustSuggestedStreamVolume(AudioManager.ADJUST_RAISE,
						AudioManager.STREAM_MUSIC, 0);
			}
			audio.adjustSuggestedStreamVolume(AudioManager.ADJUST_RAISE,
					AudioManager.STREAM_RING, 0);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (audio.isMusicActive()) {
				audio.adjustSuggestedStreamVolume(AudioManager.ADJUST_LOWER,
						AudioManager.STREAM_MUSIC, 0);
			}
			audio.adjustSuggestedStreamVolume(AudioManager.ADJUST_LOWER,
					AudioManager.STREAM_RING, 0);
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			return true;
		case KeyEvent.KEYCODE_CAMERA:
		case KeyEvent.KEYCODE_MENU:
			return true;
		default:
			return super.onKeyUp(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (wrap != null) {
			wrap.onDestroy();
		}
		System.gc();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wrap != null) {
			wrap.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (wrap != null) {
			wrap.onResume();
		}
	}
}
