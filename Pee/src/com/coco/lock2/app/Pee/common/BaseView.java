package com.coco.lock2.app.Pee.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.coco.lock2.app.Pee.ViewWrap;
import com.cooee.control.center.module.base.IBaseView;

public class BaseView extends View implements IBaseView {

	protected final String LOG_TAG = "BaseView";

	public BaseView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	protected ViewWrap wrap;

	public void setWrap(ViewWrap w) {
		this.wrap = w;
	}

	private Runnable mExitFunc = null;

	public void setExitFunction(Runnable run) {
		mExitFunc = run;
	}

	public void exitLock() {
		if (mExitFunc != null) {
			Log.d(LOG_TAG, "mExitFunc!=null");
			mExitFunc.run();
		} else {
			Context context = getContext();
			Log.d(LOG_TAG, "mExitFunc ==null");
			if (context instanceof Activity) {
				((Activity) context).finish();
			}
		}
	}

	public void onViewCreate() {
		Log.d(LOG_TAG, "BaseView.onViewCreate");
	}

	public void onViewResume() {
		Log.d(LOG_TAG, "BaseView.onViewResume");
	}

	public void onViewPause() {
		Log.d(LOG_TAG, "BaseView.onViewPause");
	}

	public void onViewDestroy() {
		Log.d(LOG_TAG, "BaseView.onViewDestroy");
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(LOG_TAG, String.format(
				"BaseView.onSizeChanged,w=%d,h=%d,l=%d,r=%d,t=%d,b=%d",
				getWidth(), getHeight(), getLeft(), getRight(), getTop(),
				getBottom()));
		super.onSizeChanged(w, h, oldw, oldh);
	}

	protected void gotoDialog() {
		Intent intent1 = new Intent(Intent.ACTION_DIAL);
		getContext().startActivity(intent1);
		exitLock();
	}

	protected void gotoMSG() {
		Intent intent2 = new Intent(Intent.ACTION_MAIN);
		intent2.setType("vnd.android-dir/mms-sms");
		getContext().startActivity(intent2);
		exitLock();
	}

	protected void gotoCamera() {
		Intent cameraIntent = new Intent(Intent.ACTION_CAMERA_BUTTON);
		KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_CAMERA);
		cameraIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
		getContext().sendBroadcast(cameraIntent);
		exitLock();
	}

	protected void gotoBrowser() {
		Intent intent1 = new Intent();
		intent1.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse("http://www.google.com");
		intent1.setData(content_url);
		getContext().startActivity(intent1);
		exitLock();
	}

}
