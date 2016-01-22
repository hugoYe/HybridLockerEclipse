package com.coco.lock2.app.view;

import java.io.IOException;

import com.coco.lock2.app.info.Variables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class AnimView implements IBaseView {

	private Runnable[] animRunnable;
	private LockView lockview = null;
	private Bitmap[][] mAnim;
	private Paint mAnimPaint;
	private Context mContext;
	private Handler mHandlerMain = new Handler();
	private int[] mIndex;
	private View mView;
	private Matrix matrix = null;
	private boolean onceOperating = false;
	private IViewInfo viewInfo;
	private int[] animX;
	private int[] animY;

	public AnimView(Context paramContext, IViewInfo paramIViewInfo) {
		mContext = paramContext;
		viewInfo = paramIViewInfo;
		mView = paramIViewInfo.getView();
		mAnimPaint = new Paint();
		mAnimPaint.setAntiAlias(true);
		mAnim = new Bitmap[Variables.Anim_num][];
		mIndex = new int[Variables.Anim_num];
		animRunnable = new Runnable[Variables.Anim_num];
		for (int i = 0; i < Variables.Anim_num; i++) {
			final int j = i;
			this.animRunnable[i] = new Runnable() {
				public void run() {
					mIndex[j]++;
					if (mIndex[j] >= Variables.Anim_frame_num[j])
						mIndex[j] = 0;
					mHandlerMain.postDelayed(this,
							Variables.Anim_delayMillis[j]);
					mView.postInvalidate();
				}
			};
		}
	}

	@Override
	public void Create() {

	}

	@Override
	public void Destroy() {

	}

	@Override
	public void InitInfo() {
		if (Variables.Anim_num > 0) {
			this.matrix = new Matrix();
			this.matrix.setScale(Variables.getScreenScaleX(),
					Variables.getScreenScaleX());
			animX = new int[Variables.Anim_num];
			animY = new int[Variables.Anim_num];
			for (int j = 0; j < Variables.Anim_num; j++) {
				if (Variables.Anim_frame_num[j] > 0) {
					this.mAnim[j] = new Bitmap[Variables.Anim_frame_num[j]];
					mAnim[j][0] = getImage(j, 0);
					if (Variables.Anim_horizontal_align[j].equals("left")) {
						animX[j] = 0;
					} else if (Variables.Anim_horizontal_align[j]
							.equals("center")) {
						animX[j] = ((Variables.screen_width - this.mAnim[j][0]
								.getWidth()) / 2);
					} else if (Variables.Anim_horizontal_align[j]
							.equals("right")) {
						animX[j] = (Variables.screen_width - this.mAnim[j][0]
								.getWidth());
					} else {
						animX[j] = (int) (Variables.Anim_x[j] * Variables
								.getScreenScaleX());
					}
					if (Variables.Anim_vertical_align[j].equals("bottom")) {
						animY[j] = (int) (Variables.screen_height
								- mAnim[j][0].getHeight() - Variables.Anim_y[j]
								* Variables.getScreenScaleY());
					} else if (Variables.Anim_vertical_align[j].equals("top")) {
						animY[j] = (int) (Variables.Anim_y[j] * Variables
								.getScreenScaleY());
					}
				}
			}
		}
	}

	@Override
	public void Pause() {
		for (int i = 0; i < Variables.Anim_num; i++) {
			this.mHandlerMain.removeCallbacks(this.animRunnable[i]);
		}
	}

	@Override
	public void Resume() {
		for (int i = 0; i < Variables.Anim_num; i++) {
			if (Variables.Anim_delayMillis[i] != 0) {
				this.mHandlerMain.removeCallbacks(this.animRunnable[i]);
				this.mHandlerMain.postDelayed(this.animRunnable[i], 100L);
			}
		}
	}

	@Override
	public void drawContent(Canvas paramCanvas, String paramString) {
		mDrawFrameAnim(paramCanvas, paramString);
	}

	private void mDrawFrameAnim(Canvas paramCanvas, String paramString) {
		for (int i = 0; i < Variables.Anim_num; i++) {
			if (Variables.Anim_frame_num[i] > 0) {
				if ((Variables.Anim_name[i].equals(paramString))
						&& ((Variables.getAnimType(i) == 0)
								|| ((Variables.getAnimType(i) == 1) && (!this.viewInfo
										.IsPressed()))
								|| ((Variables.getAnimType(i) / 10 == 2) && (this.viewInfo
										.IsPressed()))
								|| ((Variables.getAnimType(i) == 4) && (ChargeView.mIsChargeState
										.equals("充电中"))) || ((Variables
								.getAnimType(i) == 3) && (this.viewInfo
								.IsPressed())))) {
					if (((Variables.getAnimType(i) == 3) && (this.onceOperating))
							|| ((Variables.getAnimType(i) == 20) && (this.lockview
									.isMove()))) {
						return;
					}
					if (Variables.getAnimType(i) == 21) {
						mAnimPaint.setAlpha((int) lockview.getAlpha());
					}
					if ((Variables.getAnimType(i) == 3)
							&& (this.mIndex[i] == -1
									+ Variables.Anim_frame_num[i])) {
						this.onceOperating = true;
					}

					if (mAnim[i][mIndex[i]] == null) {
						mAnim[i][mIndex[i]] = getImage(i, mIndex[i]);
					}
					paramCanvas.drawBitmap(mAnim[i][mIndex[i]], animX[i],
							animY[i], mAnimPaint);
				}
			}
		}
	}

	public boolean TouchEvent(MotionEvent paramMotionEvent) {

		if (paramMotionEvent.getAction() == 0) {
			for (int i = 0; i < Variables.Anim_num; i++) {
				if ((Variables.getAnimType(i) == 1)
						|| (Variables.getAnimType(i) == 3)
						|| (Variables.getAnimType(i) / 10 == 2))
					this.mIndex[i] = 0;
			}
		} else if ((paramMotionEvent.getAction() == 1)) {
			this.onceOperating = false;
			for (int i = 0; i < Variables.Anim_num; i++) {
				if ((Variables.getAnimType(i) != 1)
						&& (Variables.getAnimType(i) != 3))
					this.mIndex[i] = 0;
			}
		}
		return true;
	}

	public Bitmap getImage(int paramInt1, int paramInt2) {
		Bitmap bitmap = null;
		Bitmap ret = null;
		String animFrameName = String.format(Variables.Anim_name[paramInt1]
				+ "_%s", paramInt2);
		try {
			bitmap = BitmapFactory.decodeStream(this.mContext.getAssets().open(
					"anim/" + animFrameName + ".png"));
			ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), this.matrix, true);
			bitmap = null;
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return ret;
	}

	public void setLockview(LockView paramLockView) {
		this.lockview = paramLockView;
	}

}
