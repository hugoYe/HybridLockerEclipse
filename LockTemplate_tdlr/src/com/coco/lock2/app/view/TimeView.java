package com.coco.lock2.app.view;

import java.util.Date;

import com.coco.lock2.app.info.Variables;
import com.coco.lock2.app.locktemplate.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateFormat;
import android.view.View;

public class TimeView implements IBaseView {

	private Paint mImagePaint;
	private int mMaxHeight;
	private Bitmap[] mTime;
	private Bitmap mTimeAm;
	private Bitmap mTimeDot;
	private boolean mTimeFormat;
	private Bitmap mTimePm;
	private View mView;
	private int[] timeHeightMax;
	private IViewInfo viewInfo;
	private Date currDate = new Date();
	private int timeX;
	private int timeY;
	private int timeAmX;
	private int timeAmY;

	public TimeView(Context paramContext, IViewInfo paramIViewInfo) {
		this.viewInfo = paramIViewInfo;
		this.mView = ((View) this.viewInfo);
		this.mTimeFormat = DateFormat.is24HourFormat(paramContext);
		this.mImagePaint = new Paint();
		this.mImagePaint.setAntiAlias(true);
	}

	private void mDrawTime(Canvas paramCanvas) {
		currDate.setTime(System.currentTimeMillis());
		int hour;
		int minute;
		if (mTimeFormat) {
			hour = Integer.parseInt(String.format("%tH", currDate));
		} else {
			hour = Integer.parseInt(String.format("%tI", currDate));
		}
		minute = Integer.parseInt(String.format("%tM", currDate));
		if (Variables.Time_horizontal_align.equals("center")) {
			timeX = (mView.getWidth() - mTime[hour / 10].getWidth()
					- mTime[hour % 10].getWidth() - mTimeDot.getWidth()
					- mTime[minute / 10].getWidth() - mTime[minute % 10]
					.getWidth()) / 2;
		} else if (Variables.Time_horizontal_align.equals("left")) {
			timeX = 0;
		} else if (Variables.Time_horizontal_align.equals("right")) {
			timeX = mView.getWidth() - mTime[hour / 10].getWidth()
					- mTime[hour % 10].getWidth() - mTimeDot.getWidth()
					- mTime[minute / 10].getWidth()
					- mTime[minute % 10].getWidth();
		}

		paramCanvas.drawBitmap(mTime[hour / 10], timeX, timeY
				- mTime[hour / 10].getHeight(), mImagePaint);
		paramCanvas.drawBitmap(mTime[hour % 10],
				timeX + mTime[hour / 10].getWidth(),
				timeY - mTime[hour % 10].getHeight(), mImagePaint);
		paramCanvas.drawBitmap(mTimeDot, timeX + mTime[hour / 10].getWidth()
				+ mTime[hour % 10].getWidth(), timeY - mTimeDot.getHeight(),
				mImagePaint);
		paramCanvas.drawBitmap(
				mTime[minute / 10],
				timeX + mTime[hour / 10].getWidth()
						+ mTime[hour % 10].getWidth() + mTimeDot.getWidth(),
				timeY - mTime[minute / 10].getHeight(), mImagePaint);
		paramCanvas.drawBitmap(
				mTime[minute % 10],
				timeX + mTime[hour / 10].getWidth()
						+ mTime[hour % 10].getWidth() + mTimeDot.getWidth()
						+ mTime[minute / 10].getWidth(), timeY
						- mTime[minute % 10].getHeight(), mImagePaint);
		if (!mTimeFormat) {
			if (Variables.Horizontal_align.equals("center")) {
				timeAmX = (mView.getWidth() - mTimePm.getWidth()) / 2;
			} else if (Variables.Horizontal_align.equals("left")) {
				timeAmX = 0;
			} else if (Variables.Horizontal_align.equals("right")) {
				timeAmX = mView.getWidth() - mTimePm.getWidth();
			}

			paramCanvas.drawBitmap(mTimeAm, timeAmX, timeAmY, mImagePaint);
		}
	}

	public void Create() {
	}

	public void Destroy() {
	}

	public void InitInfo() {
		timeX = (int) (Variables.Time_x * Variables.getScreenScaleX());
		timeAmX = (int) (Variables.Am_pm_x * Variables.getScreenScaleX());
		this.mTime = new Bitmap[10];
		timeHeightMax = new int[11];
		for (int i = 0; i < this.mTime.length; i++) {
			this.mTime[i] = ((BitmapDrawable) this.mView.getResources()
					.getDrawable(R.drawable.time_0 + i)).getBitmap();
			timeHeightMax[i] = this.mTime[i].getHeight();
		}
		mTimeDot = ((BitmapDrawable) this.mView.getResources().getDrawable(
				R.drawable.time_dot)).getBitmap();
		timeHeightMax[10] = this.mTimeDot.getHeight();
		mTimeAm = ((BitmapDrawable) this.mView.getResources().getDrawable(
				R.drawable.time_am)).getBitmap();
		mTimePm = ((BitmapDrawable) this.mView.getResources().getDrawable(
				R.drawable.time_pm)).getBitmap();
		this.mMaxHeight = Tools.maxHeight(this.timeHeightMax, 11);

		if (Variables.Time_vertical_align.equals("top")) {
			timeY = (int) (Variables.Time_y * Variables.getScreenScaleY())
					+ mMaxHeight;
		} else if (Variables.Time_vertical_align.equals("bottom")) {
			timeY = Variables.screen_height
					- (int) (Variables.Time_y * Variables.getScreenScaleY());
		}

		if (Variables.Vertical_align.equals("bottom")) {
			timeAmY = Variables.screen_height - mTimePm.getHeight()
					- (int) (Variables.Am_pm_y * Variables.getScreenScaleY());
		} else if (Variables.Vertical_align.equals("top")) {
			timeAmY = (int) (Variables.Am_pm_y * Variables.getScreenScaleY());
		}
	}

	public void Pause() {
	}

	public void Resume() {
	}

	public void drawContent(Canvas paramCanvas, String paramString) {
		mDrawTime(paramCanvas);
	}

}
