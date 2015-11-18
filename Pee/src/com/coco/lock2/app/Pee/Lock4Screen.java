package com.coco.lock2.app.Pee;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.coco.lock2.app.Pee.common.BaseView;

public class Lock4Screen extends BaseView {
	private static final String TAG = "Lock4ScreenPee";
	private Drawable bgImg;
	private Context mContext;
	private TimeShow time;
	private Child child;
	private Prompt prompt;
	private boolean statusbarupdate = true;
	static private AppConfig mAppConfig;
	private int width;
	private int height;
	private boolean touched = false;
	private Handler mHandler = new Handler();
	private boolean setBounds = true;

	public Lock4Screen(Context context) {
		super(context);
		mContext = context;

		Resources res = mContext.getResources();
		int widthPixels = res.getDisplayMetrics().widthPixels;
		int heightPixels = res.getDisplayMetrics().heightPixels;
		height = Math.max(heightPixels, widthPixels);
		width = Math.min(widthPixels, heightPixels);

		if (mAppConfig == null) {
			mAppConfig = AppConfig.getInstance(context);
		}
		time = new TimeShow(context, width, height);
		child = new Child(context, width, height);
		child.setView(this, mHandler);
		prompt = new Prompt(context, width, height);
		prompt.setView(this, mHandler);
		statusbarupdate = mAppConfig.statusbarupdate();

		bgImg = getResources().getDrawable(R.drawable.bg);
	}
	
	public void hidePrompt(){
		prompt.setShowflag(false);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {

		super.onWindowFocusChanged(hasWindowFocus);
		if (!statusbarupdate)
			return;
		Log.v(TAG, "onWindowFocusChanged  " + hasWindowFocus);
		if (hasWindowFocus) {
			Log.v(TAG, "intent.statusbar.update onWindowFocusChanged true");
			Intent intent = new Intent("intent.statusbar.update");
			intent.putExtra("cooeelock", true);
			mContext.sendBroadcast(intent);
		} else {
			Log.v(TAG, "intent.statusbar.update onWindowFocusChanged false");
			Intent intent = new Intent("intent.statusbar.update");
			intent.putExtra("cooeelock", false);
			mContext.sendBroadcast(intent);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		bgImg.setBounds(0, getHeight() - height, width, getHeight());
		bgImg.draw(canvas);
		time.onDraw(canvas);
		if (setBounds) {
			child.setViewHeight(getHeight());
			prompt.setViewHeight(getHeight());
			setBounds = false;
		}
		child.onDraw(canvas);
		prompt.onDraw(canvas);

	}

	private boolean is_in_Range(float y) {
		boolean ret = false;
		if (y > height * 0.6) {
			ret = true;
		}
		return ret;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			ViewWrap.resetLight();
			if (is_in_Range(event.getY())) {
				touched = true;
			} else {
				touched = false;
			}
		}
		if (touched) {
			prompt.onTouchEvent(event);
			child.onTouchEvent(event);
		}
		return true;
	}

	@Override
	public void onViewResume() {
		super.onViewResume();
		time.onResume();
		child.onResume();
		prompt.onResume();
	}

	@Override
	public void onViewPause() {
		time.onPause();
		child.onPause();
		prompt.onPause();
		super.onViewPause();
	}

	@Override
	public void onViewDestroy() {
		super.onViewDestroy();
		if (bgImg != null) {
			bgImg.setCallback(null);
			bgImg = null;
		}
		time.onDestroy();
		child.onDestroy();
		prompt.onDestroy();
	}

}
