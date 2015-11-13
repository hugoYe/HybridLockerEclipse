package com.coco.lock2.app.Pee;

import android.content.Context;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.View;
import com.coco.lock2.local.app.base.LockWrapBase;

public class ViewWrap extends LockWrapBase {

	private static final String LOG_TAG = "DirectorWrap";

	private Lock4Screen lockView = null;

	public ViewWrap(Context context) {
		super( context );
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {	
		Log.d(LOG_TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.d(LOG_TAG, "onPause");
		super.onPause();
		
	}

	@Override
	public View getView() {		
		return super.getView();
	}

	@Override
	public void setKernelCallback(Callback callback) {
		super.setKernelCallback( callback );
	}

	@Override
	public Callback getAppService() {
		return super.getAppService();
	}


	@Override
	public View createLockView()
	{
		Log.d(LOG_TAG, "onCreate,t=1.0.5");
		lockView = new Lock4Screen(context);
		lockView.setWrap(this);
		lockView.setExitFunction(new Runnable() {
			@Override
			public void run() {
				unLock();
				Log.d(LOG_TAG, "Exit");
			}
		});
		return lockView;
	}
}
