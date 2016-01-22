package com.coco.lock2.app.view;

import com.coco.lock2.app.info.Variables;
import com.coco.lock2.app.locktemplate.LockTemplateView1;
import com.coco.lock2.app.locktemplate.R;
import com.coco.lock2.app.locktemplate.TempWrap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
	protected Bitmap[] mEnter;
	private int mEnterAlpha = 0;
	protected boolean[] mEnterBoolean;
	protected Rect[] mEnterRect;
	private Runnable[] mEnter_animRunnable = null;
	private boolean mIsHaveInEnter = false;
	private int mStartX;
	private int mStartY;
	private int mEndDis;
	private int mEnterX[];
	private int mEnterY[];

	public LockView(Context paramContext, IViewInfo paramIViewInfo) {
		this.mContext = paramContext;
		this.mView = paramIViewInfo.getView();
		this.mImagePaint = new Paint();
		this.mImagePaint.setAntiAlias(true);
		if (Variables.Unlock_enter) {
			this.mEnter_animRunnable = new Runnable[Variables.Enter_num];
		}
	}

	public void setEnterAnim() {
		for (int i = 0; i < Variables.Enter_num; i++) {
			final int i_ = i;
			Variables.Enter_unlockAnim[i].setmContext(mContext);
			Variables.Enter_unlockAnim[i].setLockView(this);
			if (Variables.Enter_unlockAnim[i].frame_num > 1)
				mEnter_animRunnable[i] = new Runnable() {
					@Override
					public void run() {
						Variables.Enter_unlockAnim[i_].mIndex++;
						if (Variables.Enter_unlockAnim[i_].mIndex >= Variables.Enter_unlockAnim[i_].frame_num) {
							Variables.Enter_unlockAnim[i_].mIndex = 0;
						}
						mHandlerMain.postDelayed(this,
								Variables.Enter_unlockAnim[i_].delayMillis);
						mView.postInvalidate();
					}
				};
		}
	}

	private Runnable mRunnableFlush = new Runnable() {
		public void run() {
			if (Variables.Unlock_enter && Variables.Enter_num > 0
					&& Variables.Enter_type[0].equals("1")) {
				if (mIsMove) {
					mEnterAlpha += 20;
					if (mEnterAlpha >= 255) {
						mEnterAlpha = 255;
					} else {
						LockView.this.mHandlerMain.postDelayed(this, 30L);
					}
				} else {
					mEnterAlpha -= 50;
					if (mEnterAlpha <= 0) {
						mEnterAlpha = 0;
					} else {
						LockView.this.mHandlerMain.postDelayed(this, 30L);
					}
				}
				LockView.this.mView.postInvalidate();
			}
		}
	};

	private void mDrawLock(Canvas paramCanvas) {
		if (Variables.Unlock_enter && Variables.Enter_num > 0) {
			if (6 == (Variables.Unlock_style)) {
				for (int i = 0; i < Variables.Enter_num; i++) {
					if (mEnterBoolean[i]) {
						if (Variables.Enter_unlockAnim[i].frame_num > 0) {
							Variables.Enter_unlockAnim[i]
									.drawAnimEnter(paramCanvas);
						} else {
							paramCanvas.drawBitmap(
									mEnter[i],
									mEnterX[i],
									Variables.screen_height
											- mEnter[i].getHeight()
											- mEnterY[i], null);
						}
					} else {
						if ("0".equals(Variables.Enter_type[i])) {
							paramCanvas.drawBitmap(
									mEnter[i],
									mEnterX[i],
									Variables.screen_height
											- mEnter[i].getHeight()
											- mEnterY[i], null);
						} else {
							mImagePaint.setAlpha(mEnterAlpha);
							paramCanvas.drawBitmap(
									mEnter[i],
									mEnterX[i],
									Variables.screen_height
											- mEnter[i].getHeight()
											- mEnterY[i], mImagePaint);
						}
					}
				}
			}
		}
		if (!mIsHaveInEnter) {
			if (this.mIsMove) {
				if (this.anim != null) {
					this.anim.drawAnim(paramCanvas);
				} else {
					mImagePaint.setAlpha(255);
					paramCanvas.drawBitmap(
							this.mLock,
							mStartX + mMoveX,
							this.mView.getHeight() - mStartY
									- this.mLock.getHeight() + mMoveY,
							this.mImagePaint);
				}
			} else {
				mImagePaint.setAlpha(255);
				paramCanvas.drawBitmap(
						this.mLock,
						mStartX,
						this.mView.getHeight() - mStartY
								- this.mLock.getHeight(), this.mImagePaint);
			}
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
		} else {
			mStartX = (int) (Variables.Start_x * Variables.getScreenScaleX());
		}
		mStartY = (int) (Variables.Start_y * Variables.getScreenScaleY());
		mEndDis = (int) (Variables.End_dis * Variables.getScreenScaleY());
		if (this.anim != null) {
			this.anim.InitInfo();
		}

		if (Variables.Unlock_enter) {
			mEnter = new Bitmap[Variables.Enter_num];
			mEnterRect = new Rect[Variables.Enter_num];
			mEnterBoolean = new boolean[Variables.Enter_num];
			mEnterX = new int[Variables.Enter_num];
			mEnterY = new int[Variables.Enter_num];
			for (int i = 0; i < Variables.Enter_num; i++) {
				int id = mContext.getResources().getIdentifier(
						Variables.Enter_name[i], "drawable",
						mContext.getPackageName());
				mEnter[i] = ((BitmapDrawable) mView.getResources().getDrawable(
						id)).getBitmap();
				int wuchax = mEnter[i].getWidth() / 2;
				int wuchay = mEnter[i].getHeight() / 2;
				mEnterX[i] = (int) (Variables.Enter_x[i] * Variables
						.getScreenScaleX());
				mEnterY[i] = (int) (Variables.Enter_y[i] * Variables
						.getScreenScaleY());
				mEnterRect[i] = new Rect(mEnterX[i] - wuchax,
						Variables.screen_height - mEnter[i].getHeight()
								- mEnterY[i] - wuchay, mEnterX[i]
								+ mEnter[i].getWidth() + wuchax,
						Variables.screen_height - mEnterY[i] + wuchay);
				mEnterBoolean[i] = false;
				Variables.Enter_unlockAnim[i].InitInfo();

			}
		}
	}

	public void Pause() {
		if (this.animRunnable != null) {
			this.mHandlerMain.removeCallbacks(this.animRunnable);
		}
		if (mHandlerMain != null) {
			this.mHandlerMain.removeCallbacks(mRunnableFlush);
		}
		if (mEnter_animRunnable != null) {
			for (int i = 0; i < mEnter_animRunnable.length; i++) {
				if (mHandlerMain != null) {
					this.mHandlerMain.removeCallbacks(mEnter_animRunnable[i]);
				}
			}
		}
	}

	public void Resume() {
		this.mIsPressed = false;
		this.mIsMove = false;
		this.mMoveX = 0.0F;
		this.mMoveY = 0.0F;
		mEnterAlpha = 0;
		if (this.animRunnable != null) {
			this.mHandlerMain.postDelayed(this.animRunnable,
					this.anim.delayMillis);
		}
		if (Variables.Enter_unlockAnim != null) {
			for (int i = 0; i < Variables.Enter_unlockAnim.length; i++) {
				Variables.Enter_unlockAnim[i].mIndex = 0;
			}
		}
	}

	private void setPass(float paramFloat1, float paramFloat2) {
		float f1 = mEndDis;
		float f3 = paramFloat2 - this.mPressY;
		float f4 = paramFloat1 - this.mPressX;
		float f5 = (int) (180.0D * Math.atan(f3 / f4) / 3.141590118408203D);
		if ((f3 < 0.0F) && (f4 < 0.0F))
			f5 -= 180.0F;
		if ((f3 > 0.0F) && (f4 < 0.0F))
			f5 -= 180.0F;
		float f6 = f5 + 360.0F;
		if (f4 * f4 + f3 * f3 >= f1 * f1) {
			this.mMoveY = (float) (f1 * Math.sin(f6 * 3.14159F / 180.0F));
			this.mMoveX = (float) (f1 * Math.cos(f6 * 3.14159F / 180.0F));
			return;
		}
		this.mMoveX = (paramFloat1 - this.mPressX);
		this.mMoveY = (paramFloat2 - this.mPressY);
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
					mHandlerMain.removeCallbacks(mRunnableFlush);
					mHandlerMain.postDelayed(mRunnableFlush, 30);
					this.mIsMove = true;
				}
				if (6 == (Variables.Unlock_style)) {
					mIsHaveInEnter = false;
					setPass(f1, f2);
					for (int i = 0; i < Variables.Enter_num; i++) {
						if (mEnterRect[i].contains((int) (mMoveX + mPressX),
								(int) (mMoveY + mPressY))) {
							mEnterBoolean[i] = true;
							mMoveX = (mEnterRect[i].centerX() - mPressX);
							mMoveY = (mEnterRect[i].centerY() - mPressY);
							mIsHaveInEnter = true;
							mHandlerMain
									.removeCallbacks(mEnter_animRunnable[i]);
							mHandlerMain
									.postDelayed(mEnter_animRunnable[i], 30);
						} else {
							mEnterBoolean[i] = false;
							mHandlerMain
									.removeCallbacks(mEnter_animRunnable[i]);
						}
					}
				}
			}
		} else if (paramMotionEvent.getAction() == MotionEvent.ACTION_UP) {
			if (Variables.Unlock_enter && Variables.Enter_num > 0) {
				for (int i = 0; i < Variables.Enter_num; i++) {
					if (mEnterBoolean[i]) {
						mMoveX = (mEnterRect[i].centerX() - mPressX);
						mMoveY = (mEnterRect[i].centerY() - mPressY);
						if (!"null".equals(Variables.Enter_PackageName[i])
								&& !"null".equals(Variables.Enter_ClassName[i])) {
							try {
								Intent intent = new Intent();
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
										| Intent.FLAG_ACTIVITY_CLEAR_TASK);
								ComponentName componentName = new ComponentName(
										Variables.Enter_PackageName[i],
										Variables.Enter_ClassName[i]);
								intent.setComponent(componentName);
								mContext.startActivity(intent);
							} catch (Exception e) {
								if (Variables.Enter_name[i]
										.equals("enter_message")) {
									Intent intent1 = new Intent(
											Intent.ACTION_MAIN);
									intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent1.setType("vnd.android-dir/mms-sms");
									mContext.startActivity(intent1);
								} else if (Variables.Enter_name[i]
										.equals("enter_camera")) {
									Intent intent1 = new Intent();
									intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
											| Intent.FLAG_ACTIVITY_CLEAR_TASK);
									intent1.setAction("android.media.action.STILL_IMAGE_CAMERA");
									mContext.startActivity(intent1);
								} else if (Variables.Enter_name[i]
										.equals("enter_phone")) {
									Intent intent1 = new Intent(
											Intent.ACTION_VIEW);
									intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent1.setType("vnd.android.cursor.dir/calls");
									mContext.startActivity(intent1);
								}
							}
						}
						((LockTemplateView1) mView).mExitLock();
						return true;
					}
				}
				mHandlerMain.removeCallbacks(mRunnableFlush);
				mHandlerMain.postDelayed(mRunnableFlush, 30);
			} else {
				if ((Math.abs(this.mMoveY) >= mEndDis)
						|| (Math.abs(this.mMoveX) >= mEndDis)) {
					((LockTemplateView1) this.mView).mExitLock();
					return true;
				}
			}
			this.mIsPressed = false;
			this.mIsMove = false;
			this.mMoveX = 0.0F;
			this.mMoveY = 0.0F;
			for (int i = 0; i < Variables.Enter_num; i++) {
				mEnterBoolean[i] = false;
			}
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
