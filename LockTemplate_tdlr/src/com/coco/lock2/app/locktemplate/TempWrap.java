package com.coco.lock2.app.locktemplate;

import com.cooee.control.center.module.api.LockWrapApi;
import android.content.Context;
import android.os.Handler.Callback;
import android.view.View;

public class TempWrap extends LockWrapApi {

	private LockScreen lockView = null;
	public TempWrap(Context context) {
		super(context);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public View getView() {
		return super.getView();
	}

	@Override
	public void setKernelCallback(Callback callback) {
		super.setKernelCallback(callback);
	}

	@Override
	public Callback getAppService() {
		return super.getAppService();
	}

	@Override
	public View createLockView() {
		lockView = new LockScreen(context);
		lockView.setExitFunction(new Runnable() {
			@Override
			public void run() {
				onUnlock();
			}
		});
		return lockView;
	}
}
