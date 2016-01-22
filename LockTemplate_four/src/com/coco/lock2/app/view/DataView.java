package com.coco.lock2.app.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.coco.lock2.app.info.Variables;
import com.coco.lock2.app.locktemplate.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class DataView implements IBaseView {

	private int[] dateHeightMax;
	private Bitmap[] mDate;
	private Bitmap mDateDot;
	private int mDateY;
	private int mDateX;
	private int mDateDis;
	private Paint mImagePaint;
	private int mMaxDateHeight;
	private View mView;
	private Bitmap[] mWeek;
	private SimpleDateFormat weekFormat;
	private SimpleDateFormat dateFormat;
	private Date currDate = new Date();
	private IViewInfo viewInfo;

	public DataView(Context paramContext, IViewInfo paramIViewInfo) {
		this.viewInfo = paramIViewInfo;
		this.mView = this.viewInfo.getView();
		this.mImagePaint = new Paint();
		this.mImagePaint.setAntiAlias(true);
		dateFormat = new SimpleDateFormat(mView.getResources().getString(
				R.string.date_format));
		weekFormat = new SimpleDateFormat(mView.getResources().getString(
				R.string.week_format));
	}

	private int getTextHeight(Paint paramPaint) {
		Paint.FontMetrics localFontMetrics = paramPaint.getFontMetrics();
		return (int) Math.ceil(localFontMetrics.descent
				- localFontMetrics.ascent);
	}

	private void mDrawDateAndWeekFont(Canvas paramCanvas) {
		currDate.setTime(System.currentTimeMillis());
		String str1 = dateFormat.format(currDate);
		String str2 = weekFormat.format(currDate);
		int i = (int) (mImagePaint.measureText(str1)
				+ mImagePaint.measureText(str2) + Variables.Date_dis);
		if (Variables.Date_horizontal_align.equals("center")) {
			Variables.Date_x = (this.mView.getWidth() - i) / 2;
		} else if (Variables.Date_horizontal_align.equals("left")) {
			Variables.Date_x = 0;
		} else if (Variables.Date_horizontal_align.equals("right")) {
			Variables.Date_x = this.mView.getWidth() - i;
		}
		paramCanvas.drawText(str1, Variables.Date_x, this.mDateY,
				this.mImagePaint);
		paramCanvas.drawText(str2,
				Variables.Date_x + mImagePaint.measureText(str1)
						+ Variables.Date_dis, this.mDateY, this.mImagePaint);
	}

	private void mDrawDateAndWeek(Canvas paramCanvas) {
		currDate.setTime(System.currentTimeMillis());
		String str1 = String.format("%tm", currDate);
		String str2 = String.format("%td", currDate);
		int i = Integer.parseInt(str1);
		int j = Integer.parseInt(str2);
		int k = Calendar.getInstance().get(7);
		if (Variables.Date_horizontal_align.equals("center")) {
			mDateX = (this.mView.getWidth() - this.mDate[(i / 10)].getWidth()
					- this.mDate[(i % 10)].getWidth()
					- this.mDateDot.getWidth()
					- this.mDate[(j / 10)].getWidth()
					- this.mDate[(j % 10)].getWidth() - mDateDis - this.mWeek[(k - 1)]
					.getWidth()) / 2;
		} else if (Variables.Date_horizontal_align.equals("left")) {
			mDateX = 0;
		} else if (Variables.Date_horizontal_align.equals("right")) {
			mDateX = this.mView.getWidth() - this.mDate[(i / 10)].getWidth()
					- this.mDate[(i % 10)].getWidth()
					- this.mDateDot.getWidth()
					- this.mDate[(j / 10)].getWidth()
					- this.mDate[(j % 10)].getWidth() - mDateDis
					- this.mWeek[(k - 1)].getWidth();
		}

		boolean bool = Locale.getDefault().getLanguage().equals("zh");
		int m = 0;
		if (!bool) {
			m = this.mWeek[(k - 1)].getWidth() + mDateDis;
			paramCanvas.drawBitmap(this.mWeek[(k - 1)], mDateX, mDateY
					- this.mWeek[(k - 1)].getHeight(), this.mImagePaint);
		} else {
			paramCanvas
					.drawBitmap(this.mWeek[(k - 1)],
							mDateX + this.mDate[(i / 10)].getWidth()
									+ this.mDate[(i % 10)].getWidth()
									+ this.mDate[(j / 10)].getWidth()
									+ this.mDate[(j % 10)].getWidth()
									+ this.mDateDot.getWidth() + mDateDis,
							mDateY - this.mWeek[(k - 1)].getHeight(),
							this.mImagePaint);
		}
		paramCanvas.drawBitmap(this.mDate[(i / 10)], m + mDateX, mDateY
				- this.mDate[(i / 10)].getHeight(), this.mImagePaint);
		paramCanvas.drawBitmap(this.mDate[(i % 10)], m + mDateX
				+ this.mDate[(i / 10)].getWidth(), mDateY
				- this.mDate[(i % 10)].getHeight(), this.mImagePaint);
		paramCanvas.drawBitmap(this.mDateDot,
				m + mDateX + this.mDate[(i / 10)].getWidth()
						+ this.mDate[(i % 10)].getWidth(), mDateY
						- this.mDateDot.getHeight(), this.mImagePaint);
		paramCanvas.drawBitmap(
				this.mDate[(j / 10)],
				m + mDateX + this.mDate[(i / 10)].getWidth()
						+ this.mDate[(i % 10)].getWidth()
						+ this.mDateDot.getWidth(), mDateY
						- this.mDate[(j / 10)].getHeight(), this.mImagePaint);
		paramCanvas.drawBitmap(
				this.mDate[(j % 10)],
				m + mDateX + this.mDate[(i / 10)].getWidth()
						+ this.mDate[(i % 10)].getWidth()
						+ this.mDate[(j / 10)].getWidth()
						+ this.mDateDot.getWidth(), mDateY
						- this.mDate[(j % 10)].getHeight(), this.mImagePaint);
	}

	public void Create() {
	}

	public void Destroy() {
	}

	public void InitInfo() {
		mDateX = (int) (Variables.Date_x * Variables.getScreenScaleX());
		mDateDis = (int) (Variables.Date_dis * Variables.getScreenScaleX());
		this.mDate = new Bitmap[10];
		this.mWeek = new Bitmap[7];
		String str = Locale.getDefault().getLanguage();
		dateHeightMax = new int[18];
		for (int i = 0; i < this.mDate.length; i++) {
			this.mDate[i] = ((BitmapDrawable) this.mView.getResources()
					.getDrawable(R.drawable.date_0 + i)).getBitmap();
			dateHeightMax[i] = this.mDate[i].getHeight();
		}
		this.mDateDot = ((BitmapDrawable) this.mView.getResources()
				.getDrawable(R.drawable.date_dot)).getBitmap();
		dateHeightMax[10] = this.mDateDot.getHeight();
		for (int i = 0; i < mWeek.length; i++) {
			if (str.equals("zh")) {
				this.mWeek[i] = ((BitmapDrawable) this.mView.getResources()
						.getDrawable(R.drawable.week_ch_0 + i)).getBitmap();
			} else {
				this.mWeek[i] = ((BitmapDrawable) this.mView.getResources()
						.getDrawable(R.drawable.week_en_0 + i)).getBitmap();
			}
			dateHeightMax[11 + i] = this.mWeek[0].getHeight();
		}
		this.mMaxDateHeight = Tools.maxHeight(this.dateHeightMax, 18);

		if (Variables.Data_useImage) {
			if (Variables.Date_vertical_align.equals("top")) {
				mDateY = ((int) (Variables.Date_y * Variables.getScreenScaleY()) + this.mMaxDateHeight);
			} else if (Variables.Date_vertical_align.equals("bottom")) {
				mDateY = (Variables.screen_height - (int) (Variables.Date_y * Variables
						.getScreenScaleY()));
			}
		} else {
			mImagePaint.setTextSize(Variables.Date_fontSize
					* Variables.getScreenScaleX());
			mImagePaint.setColor((int) Long.parseLong(
					Variables.Date_color.substring(1,
							Variables.Date_color.length()), 16));
			if (Variables.Date_vertical_align.equals("top")) {
				mDateY = ((int) (Variables.Date_y * Variables.getScreenScaleY()) + getTextHeight(mImagePaint));
			} else if (Variables.Date_vertical_align.equals("bottom")) {
				mDateY = (Variables.screen_height - (int) (Variables.Date_y * Variables
						.getScreenScaleY()));
			}
		}
	}

	public void Pause() {
	}

	public void Resume() {
	}

	public void drawContent(Canvas paramCanvas, String paramString) {
		if (Variables.Data_useImage) {
			mDrawDateAndWeek(paramCanvas);
		} else {
			mDrawDateAndWeekFont(paramCanvas);
		}
	}

}
