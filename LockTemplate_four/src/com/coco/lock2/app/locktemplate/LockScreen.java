package com.coco.lock2.app.locktemplate;

import com.coco.lock2.app.info.Variables;
import com.cooee.control.center.module.base.IBaseView;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class LockScreen extends FrameLayout implements IBaseView {

	private LockTemplateView1 lockTemplateView1;

	public LockScreen(Context context) {
		super(context);
		lockTemplateView1 = new LockTemplateView1(context);
		addView(lockTemplateView1);
	}

	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
		Variables.screen_width = Math.min(paramInt1, paramInt2);
		Variables.screen_height = Math.max(paramInt1, paramInt2);
		Log.v("LockTemplate", "screen_width = " + Variables.screen_width
				+ " screen_height = " + Variables.screen_height);
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		super.onTouchEvent(paramMotionEvent);
		lockTemplateView1.TouchEvent(paramMotionEvent);
		return true;
	}

	public void setExitFunction(Runnable run) {
		lockTemplateView1.setExitFunction(run);
	}

	@Override
	public void onViewResume() {
		if (lockTemplateView1 != null)
			lockTemplateView1.onViewResume();
	}

	@Override
	public void onViewPause() {
		if (lockTemplateView1 != null)
			lockTemplateView1.onViewPause();
	}

	@Override
	public void onViewDestroy() {
		if (lockTemplateView1 != null)
			lockTemplateView1.onViewDestroy();
	}

}
