package com.coco.lock2.app.locktemplate;

import com.coco.lock2.app.info.ParaseXml;
import com.coco.lock2.app.info.Variables;
import com.coco.lock2.app.locktemplate.R;
import com.coco.lock2.app.view.AnimView;
import com.coco.lock2.app.view.BgView;
import com.coco.lock2.app.view.CallerInfoView;
import com.coco.lock2.app.view.ChargeView;
import com.coco.lock2.app.view.DataView;
import com.coco.lock2.app.view.IViewInfo;
import com.coco.lock2.app.view.LockView;
import com.coco.lock2.app.view.TimeView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;

public class LockTemplateView1 extends View implements IViewInfo {

	private AnimView animView;
	private BgView bgView;
	private CallerInfoView callerInfoView;
	private ChargeView chargeView;
	private DataView dataView;
	private boolean isPressed = false;
	private LockView lockView;
	private Bitmap mImageBg;
	private ParaseXml paraseXml;
	private TimeView timeView;

	public LockTemplateView1(Context paramContext) {
		super(paramContext);
		this.paraseXml = new ParaseXml(paramContext);
		this.paraseXml.PullParseXML("lockscreen.xml");
		this.animView = new AnimView(paramContext, this);
		this.bgView = new BgView(paramContext, this);
		this.callerInfoView = new CallerInfoView(paramContext, this);
		this.chargeView = new ChargeView(paramContext, this);
		this.dataView = new DataView(paramContext, this);
		this.lockView = new LockView(paramContext, this);
		this.timeView = new TimeView(paramContext, this);
		if (this.paraseXml.getUnlockAnim().frame_num > 0) {
			this.lockView.setAnim(this.paraseXml.getUnlockAnim());
		}
		this.animView.setLockview(this.lockView);
		
		mCreateImage();
	}

	private void mCreateImage() {
		this.mImageBg = ((BitmapDrawable) getResources().getDrawable(
				R.drawable.bg)).getBitmap();
	}

	private void mDraw(Canvas paramCanvas, String paramString1,
			String paramString2) {
		if (paramString1.equals("time")) {
			this.timeView.drawContent(paramCanvas, paramString2);
		} else if (paramString1.equals("date")) {
			this.dataView.drawContent(paramCanvas, paramString2);
		} else if (paramString1.equals("charging")) {
			this.chargeView.drawContent(paramCanvas, paramString2);
		} else if (paramString1.equals("unlock")) {
			this.lockView.drawContent(paramCanvas, paramString2);
		} else if (paramString1.equals("animation")) {
			this.animView.drawContent(paramCanvas, paramString2);
		} else if (paramString1.equals("incoming")) {
			this.callerInfoView.drawContent(paramCanvas, paramString2);
		}
	}

	public boolean IsPressed() {
		if (this.lockView != null)
			return this.lockView.mIsPressed;
		return this.isPressed;
	}

	public boolean TouchEvent(MotionEvent paramMotionEvent) {
		if (paramMotionEvent.getAction() == 0) {
			
		} else if (paramMotionEvent.getAction() == 2) {
			this.isPressed = true;
		} else if (paramMotionEvent.getAction() == 1) {
			this.isPressed = false;
		}
		this.lockView.TouchEvent(paramMotionEvent);
		this.animView.TouchEvent(paramMotionEvent);
		return true;
	}

	public Bitmap getBg() {
		return this.mImageBg;
	}

	public View getView() {
		return this;
	}

	private Runnable mExitFunc = null;

	public void setExitFunction(Runnable run) {
		mExitFunc = run;
	}

	public void mExitLock() {
		if (mExitFunc != null) {
			mExitFunc.run();
		} else {
			Context context = getContext();
			if (context instanceof Activity) {
				((Activity) context).finish();
			}
		}
	}

	protected void onDraw(Canvas paramCanvas) {
		super.onDraw(paramCanvas);
		this.bgView.drawContent(paramCanvas, "");
		for (int i = 0; i < Variables.Layer_num; i++) {
			mDraw(paramCanvas, Variables.Layer_type[i], Variables.Layer_name[i]);
		}
	}

	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
		animView.InitInfo();
		bgView.InitInfo();
		callerInfoView.InitInfo();
		chargeView.InitInfo();
		dataView.InitInfo();
		lockView.InitInfo();
		timeView.InitInfo();
	}

	protected void onViewDestroy() {
		animView.Destroy();
		bgView.Destroy();
		callerInfoView.Destroy();
		chargeView.Destroy();
		dataView.Destroy();
		lockView.Destroy();
		timeView.Destroy();
	}

	protected void onViewPause() {
		animView.Pause();
		bgView.Pause();
		callerInfoView.Pause();
		chargeView.Pause();
		dataView.Pause();
		lockView.Pause();
		timeView.Pause();
	}

	protected void onViewResume() {
		this.isPressed = false;
		animView.Resume();
		bgView.Resume();
		callerInfoView.Resume();
		chargeView.Resume();
		dataView.Resume();
		lockView.Resume();
		timeView.Resume();
	}

}
