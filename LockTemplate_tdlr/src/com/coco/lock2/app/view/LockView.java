package com.coco.lock2.app.view;

import com.coco.lock2.app.info.Variables;
import com.coco.lock2.app.locktemplate.LockTemplateView1;
import com.coco.lock2.app.locktemplate.TempWrap;
import com.coco.lock2.app.locktemplate.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class LockView implements IBaseView {

	private UnlockAnim anim = null;
	private Runnable animRunnable = null;
	private Context mContext;
	private Handler mHandlerMain = new Handler();
	private Paint mImagePaint;
	public boolean mIsPressed = false;
	public boolean mIsMove = false;
	protected Bitmap mLock;
	protected float mMoveX = 0.0F;
	protected float mMoveY = 0.0F;
	private float mPressX;
	private float mPressY;
	private View mView;
	private int mStartX;
	private int mStartY;
	private int mEndDis;

	public LockView(Context paramContext, IViewInfo paramIViewInfo) {
		this.mContext = paramContext;
		this.mView = paramIViewInfo.getView();
		this.mImagePaint = new Paint();
		this.mImagePaint.setAntiAlias(true);
	}

	private void mDrawLock(Canvas paramCanvas) {
		if (this.mIsMove) {
			if (this.anim != null) {
				this.anim.drawAnim(paramCanvas);
			} else {
				paramCanvas.drawBitmap(
						this.mLock,
						mStartX + mMoveX,
						this.mView.getHeight() - mStartY
								- this.mLock.getHeight() + mMoveY,
						this.mImagePaint);
			}
		} else {
			paramCanvas.drawBitmap(this.mLock, mStartX, this.mView.getHeight()
					- mStartY - this.mLock.getHeight(), this.mImagePaint);
		}
	}

	public void Create() {
	}

	public void Destroy() {
	}

	public void InitInfo() {
		mLock = ((BitmapDrawable) this.mView.getResources().getDrawable(
				R.drawable.unlock)).getBitmap();
		if (Variables.Unlock_horizontal_align.equals("left")) {
			mStartX = 0;
		} else if (Variables.Unlock_horizontal_align.equals("center")) {
			mStartX = (Variables.screen_width - this.mLock.getWidth()) / 2;
		} else if (Variables.Unlock_horizontal_align.equals("right")) {
			mStartX = Variables.screen_width - this.mLock.getWidth();
		}else {
			mStartX = (int) (Variables.Start_x * Variables.getScreenScaleX());
		}
		mStartY = (int) (Variables.Start_y * Variables.getScreenScaleY());
		mEndDis = (int) (Variables.End_dis * Variables.getScreenScaleY());
		if (this.anim != null) {
			this.anim.InitInfo();
		}
	}

	public void Pause() {
		if (this.animRunnable != null) {
			this.mHandlerMain.removeCallbacks(this.animRunnable);
		}
	}

	public void Resume() {
		this.mIsPressed = false;
		this.mIsMove = false;
		this.mMoveX = 0.0F;
		this.mMoveY = 0.0F;
		if (this.animRunnable != null) {
			this.mHandlerMain.postDelayed(this.animRunnable,
					this.anim.delayMillis);
		}
	}

	public boolean TouchEvent(MotionEvent paramMotionEvent) {
		float f1 = paramMotionEvent.getX();
		float f2 = paramMotionEvent.getY();
		if (paramMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			if (Tools.mCanClickArea(f1, f2, mStartX, this.mView.getHeight()
					- mStartY - this.mLock.getHeight(), this.mLock.getWidth(),
					this.mLock.getHeight())) {
				TempWrap.resetLight();
				this.mIsPressed = true;
				this.mPressY = f2;
				this.mPressX = f1;
			}
		} else if (paramMotionEvent.getAction() == MotionEvent.ACTION_MOVE) {
			if (this.mIsPressed) {
				if (!this.mIsMove) {
					this.mIsMove = true;
				}
				if (Variables.Unlock_style == 1) {
					this.mMoveY = (f2 - this.mPressY);
					if (this.mMoveY >= 0.0F) {
						this.mMoveY = 0.0F;
					} else if (this.mMoveY <= -mEndDis) {
						this.mMoveY = (-mEndDis);
					}
				} else if (Variables.Unlock_style == 2) {
					this.mMoveY = (f2 - this.mPressY);
					if (this.mMoveY <= 0.0F) {
						this.mMoveY = 0.0F;
					} else if (this.mMoveY >= mEndDis) {
						this.mMoveY = mEndDis;
					}
				} else if (Variables.Unlock_style == 3) {
					this.mMoveX = (f1 - this.mPressX);
					if (this.mMoveX >= 0.0F) {
						this.mMoveX = 0.0F;
					} else if (this.mMoveX <= -mEndDis) {
						this.mMoveX = (-mEndDis);
					}
				} else if (Variables.Unlock_style == 4) {
					this.mMoveX = (f1 - this.mPressX);
					if (this.mMoveX <= 0.0F) {
						this.mMoveX = 0.0F;
					} else if (this.mMoveX >= mEndDis) {
						this.mMoveX = mEndDis;
					}
				}
			}
		} else if (paramMotionEvent.getAction() == MotionEvent.ACTION_UP) {
			if ((Math.abs(this.mMoveY) >= mEndDis)
					|| (Math.abs(this.mMoveX) >= mEndDis)) {
				((LockTemplateView1) this.mView).mExitLock();
				return true;
			}
			this.mIsPressed = false;
			this.mIsMove = false;
			this.mMoveX = 0.0F;
			this.mMoveY = 0.0F;
		}
		this.mView.postInvalidate();
		return true;
	}

	public void drawContent(Canvas paramCanvas, String paramString) {
		mDrawLock(paramCanvas);
	}

	public boolean isMove() {
		return Math.abs(this.mMoveY) + Math.abs(this.mMoveX) > 0.05D * this.mView
				.getWidth();
	}

	public float getAlpha() {
		float a = (float) (255 - 255 * (2.0 * (Math.abs(mMoveY) + Math
				.abs(mMoveX)) / mEndDis));
		return a > 0 ? a : 0;
	}

	public void setAnim(UnlockAnim paramUnlockAnim) {
		this.anim = paramUnlockAnim;
		this.anim.setmContext(this.mContext);
		this.anim.setLockView(this);
		if (this.anim.frame_num > 1)
			this.animRunnable = new Runnable() {
				public void run() {
					LockView.this.anim.mIndex++;
					if (LockView.this.anim.mIndex >= LockView.this.anim.frame_num)
						LockView.this.anim.mIndex = 0;
					LockView.this.mHandlerMain.postDelayed(this,
							LockView.this.anim.delayMillis);
					LockView.this.mView.postInvalidate();
				}
			};
	}
}
