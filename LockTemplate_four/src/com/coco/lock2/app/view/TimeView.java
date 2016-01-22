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

	private Paint mAMPMPaint;
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
		this.mImagePaint.setColor((int) Long.parseLong(Variables.Time_color
				.substring(1, Variables.Time_color.length()), 16));
		this.mImagePaint.setTextSize(Variables.Time_fontSize
				* Variables.getScreenScaleX());
		this.mAMPMPaint = new Paint();
		this.mAMPMPaint.setAntiAlias(true);
		this.mAMPMPaint.setColor((int) Long.parseLong(
				Variables.Am_pm_color.substring(1,
						Variables.Am_pm_color.length()), 16));
		this.mAMPMPaint.setTextSize(Variables.Am_pm_fontSize
				* Variables.getScreenScaleX());
	}

	private int getTextHeight(Paint paramPaint) {
		Paint.FontMetrics localFontMetrics = paramPaint.getFontMetrics();
		return (int) Math.ceil(localFontMetrics.bottom
				- localFontMetrics.ascent);
	}

	private void mDrawTimeFont(Canvas paramCanvas) {
		currDate.setTime(System.currentTimeMillis());
		String str1 = "";
		String str2 = "";
		if (mTimeFormat) {
			str1 = String.format("%tR", currDate);

		} else {
			str1 = String.format("%tr", currDate).substring(0, 5);
			str2 = String.format("%tp", currDate);
		}
		if (Variables.Time_horizontal_align.equals("center")) {
			timeX = (int) (mView.getWidth() - mImagePaint
					.measureText(str1)) / 2;
		} else if (Variables.Time_horizontal_align.equals("left")) {
			timeX = 0;
		} else if (Variables.Time_horizontal_align.equals("right")) {
			timeX = (int) (mView.getWidth() - mImagePaint
					.measureText(str1));
		}
		paramCanvas.drawText(str1, timeX, timeY, mImagePaint);

		if (!mTimeFormat) {
			if (Variables.Horizontal_align.equals("center")) {
				timeAmX = (int) (mView.getWidth() - mAMPMPaint
						.measureText(str2)) / 2;
			} else if (Variables.Horizontal_align.equals("left")) {
				timeAmX = 0;
			} else if (Variables.Horizontal_align.equals("right")) {
				timeAmX = (int) (mView.getWidth() - mAMPMPaint
						.measureText(str2));
			}
			
			paramCanvas.drawText(str2, timeAmX, timeAmY,
					mAMPMPaint);
		}
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
		paramCanvas.drawBitmap(mTime[hour % 10], timeX
				+ mTime[hour / 10].getWidth(),
				timeY - mTime[hour % 10].getHeight(), mImagePaint);
		paramCanvas.drawBitmap(mTimeDot,
				timeX + mTime[hour / 10].getWidth()
						+ mTime[hour % 10].getWidth(),
						timeY - mTimeDot.getHeight(), mImagePaint);
		paramCanvas.drawBitmap(mTime[minute / 10], timeX
				+ mTime[hour / 10].getWidth() + mTime[hour % 10].getWidth()
				+ mTimeDot.getWidth(), timeY - mTime[minute / 10].getHeight(),
				mImagePaint);
		paramCanvas.drawBitmap(mTime[minute % 10], timeX
				+ mTime[hour / 10].getWidth() + mTime[hour % 10].getWidth()
				+ mTimeDot.getWidth() + mTime[minute / 10].getWidth(), timeY
				- mTime[minute % 10].getHeight(), mImagePaint);
		if (!mTimeFormat) {
			paramCanvas.drawBitmap(mTimeAm, timeAmX,
					timeAmY, mImagePaint);
		}
	}

	public void Create() {
	}

	public void Destroy() {
	}

	public void InitInfo() {
		timeX = (int) (Variables.Time_x * Variables.getScreenScaleX());
		this.mTime = new Bitmap[10];
		timeHeightMax = new int[11];
		for (int i = 0; i < this.mTime.length; i++) {
			this.mTime[i] = ((BitmapDrawable) this.mView.getResources()
					.getDrawable(R.drawable.time_0 + i)).getBitmap();
			timeHeightMax[i] = this.mTime[i].getHeight();
		}
		mTimeDot = ((BitmapDrawable) this.mView.getResources()
				.getDrawable(R.drawable.time_dot)).getBitmap();
		timeHeightMax[10] = this.mTimeDot.getHeight();
		mTimeAm = ((BitmapDrawable) this.mView.getResources()
				.getDrawable(R.drawable.time_am)).getBitmap();
		mTimePm = ((BitmapDrawable) this.mView.getResources()
				.getDrawable(R.drawable.time_pm)).getBitmap();
		this.mMaxHeight = Tools.maxHeight(this.timeHeightMax, 11);
		
		if (Variables.Time_useImage) {
			if (Variables.Time_vertical_align.equals("top")) {
				timeY = (int) (Variables.Time_y * Variables.getScreenScaleY())
						+ mMaxHeight;
			} else if (Variables.Time_vertical_align.equals("bottom")) {
				timeY = Variables.screen_height
						- (int) (Variables.Time_y * Variables.getScreenScaleY());
			}
			if (Variables.Horizontal_align.equals("center")) {
				timeAmX = (Variables.screen_width - mTimePm.getWidth()) / 2;
			} else if (Variables.Horizontal_align.equals("left")) {
				timeAmX = 0;
			} else if (Variables.Horizontal_align.equals("right")) {
				timeAmX = Variables.screen_width - mTimePm.getWidth();
			}else {
				timeAmX = (int) (Variables.Am_pm_x * Variables.getScreenScaleX());
			}
			if (Variables.Vertical_align.equals("bottom")) {
				timeAmY = Variables.screen_height - mTimePm.getHeight()
						- (int) (Variables.Am_pm_y * Variables.getScreenScaleY());
			} else if (Variables.Vertical_align.equals("top")) {
				timeAmY = (int) (Variables.Am_pm_y * Variables.getScreenScaleY());
			}
		}else {
			if (Variables.Time_vertical_align.equals("top")) {
				timeY = Variables.Time_y + getTextHeight(mImagePaint);
			} else if (Variables.Time_vertical_align.equals("bottom")) {
				timeY = Variables.screen_height - Variables.Time_y;
			}
			timeAmX = (int) (Variables.Am_pm_x * Variables.getScreenScaleX());
			if (Variables.Vertical_align.equals("top")) {
				timeAmY = Variables.Am_pm_y + getTextHeight(mAMPMPaint);
			} else if (Variables.Vertical_align.equals("bottom")) {
				timeAmY = Variables.screen_height - Variables.Am_pm_y;
			}
		}
	}

	public void Pause() {
	}

	public void Resume() {
	}

	public void drawContent(Canvas paramCanvas, String paramString) {
		if (Variables.Time_useImage) {
			mDrawTime(paramCanvas);
		} else {
			mDrawTimeFont(paramCanvas);
		}
	}

}
