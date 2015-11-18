package com.coco.lock2.app.Pee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class Prompt extends Base {

	private Context mContext;
	private int width;
	private int height;
	private float scale;
	private int index = 0;
	private Drawable[] arrows;
	private boolean showflag = true;
	private boolean touchflag = false;
	private Handler mHandler;
	private View mView;

	public Prompt(Context context, int w, int h) {
		super();
		mContext = context;
		width = w;
		height = h;
		scale = w/480f;
		createDrawable();
	}

	public void setView(View view, Handler handler) {
		mView = view;
		mHandler = handler;
	}
	
	public void setViewHeight(int h){
		if (arrows != null) {
			for (int i = 0; i < arrows.length; i++) {
				arrows[i].setBounds((int) (width - 183 * scale), (int) (h - 30
						* scale - arrows[i].getIntrinsicHeight()*1.5f), (int) (width
						- 183 * scale + arrows[i].getIntrinsicWidth()*1.5f),
						(int) (h - 30 * scale));
			}
		}
	}
	
	private void createDrawable() {
		arrows = new Drawable[4];
		for (int i = 0; i < arrows.length; i++) {
			arrows[i] = mContext.getResources().getDrawable(
					R.drawable.arrow001 + i);
		}
	}

	@Override
	public synchronized void onDraw(Canvas canvas) {
		if (!showflag)
			return;
		arrows[index].draw(canvas);
	}

	private Runnable animRunnable = new Runnable() {
		
		@Override
		public void run() {
			index ++;
			if (index > 3) {
				index = 0;
			}
			mHandler.postDelayed(this, 300);
			mView.postInvalidate();
		}
	};
	
	public boolean isShowflag() {
		return showflag;
	}

	public void setShowflag(boolean showflag) {
		this.showflag = showflag;
	}

	public void onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touchflag = true;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (touchflag) {
				setShowflag(false);
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			setShowflag(true);
			touchflag = false;
		}
	}

	private void freedDrawable(Drawable drawable) {
		if (drawable != null) {
			drawable.setCallback(null);
			drawable = null;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (arrows != null) {
			for (int i = 0; i < arrows.length; i++) {
				freedDrawable(arrows[i]);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		index = 0;
		touchflag = false;
		setShowflag(true);
		mHandler.postDelayed(animRunnable, 100);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(animRunnable);
	}
}
