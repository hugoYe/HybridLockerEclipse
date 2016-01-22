package com.coco.lock2.app.view;

import java.io.IOException;

import com.coco.lock2.app.info.Variables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class UnlockAnim {

	public int delayMillis;
	public int frame_num;
	public String horizontal_align;
	private LockView lockView = null;
	private Bitmap[] mAnim;
	private Paint mAnimPaint;
	private Context mContext = null;
	public int mIndex = 0;
	private Matrix matrix;
	public String name;
	public String vertical_align;
	public int x;
	public int y;
	private int unlockAnimX;
	private int unlockAnimY;

	public void InitInfo() {
		if (this.frame_num > 0) {
			matrix = new Matrix();
			matrix.setScale(Variables.getScreenScaleX(),
					Variables.getScreenScaleX());
			mAnimPaint = new Paint();
			mAnimPaint.setAntiAlias(true);

			this.mAnim = new Bitmap[this.frame_num];
			this.mAnim[this.mIndex] = getImage(this.mIndex);
			if (this.horizontal_align.equals("left")) {
				unlockAnimX = 0;
			} else if (this.horizontal_align.equals("right")) {
				unlockAnimX = (Variables.screen_width - this.mAnim[this.mIndex]
						.getWidth());
			} else if (this.horizontal_align.equals("center")) {
				unlockAnimX = ((Variables.screen_width - this.mAnim[this.mIndex]
						.getWidth()) / 2);
			} else {
				unlockAnimX = (int) (x * Variables.getScreenScaleX());
			}
			if (vertical_align.equals("bottom")) {
				unlockAnimY = (Variables.screen_height
						- mAnim[this.mIndex].getHeight() - (int) (y * Variables
						.getScreenScaleY()));
			} else if (vertical_align.equals("top")) {
				unlockAnimY = (int) (y * Variables.getScreenScaleY());
			}
		}
	}

	public void drawAnim(Canvas paramCanvas) {
		if (frame_num > 0) {
			if (this.mAnim[this.mIndex] == null)
				this.mAnim[this.mIndex] = getImage(this.mIndex);
			if (this.mAnim[this.mIndex] != null) {
				paramCanvas.drawBitmap(this.mAnim[this.mIndex], unlockAnimX
						+ this.lockView.mMoveX, unlockAnimY
						+ this.lockView.mMoveY, this.mAnimPaint);
			}
		}
	}

	public Bitmap getImage(int paramInt) {
		Bitmap bitmap = null;
		Bitmap ret = null;
		String animFrameName = String.format(name + "_%s", paramInt);
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

	public void setLockView(LockView paramLockView) {
		this.lockView = paramLockView;
	}

	public void setmContext(Context paramContext) {
		this.mContext = paramContext;
	}
}
