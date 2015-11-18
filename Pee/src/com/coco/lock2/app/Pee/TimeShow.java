package com.coco.lock2.app.Pee;

import java.sql.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class TimeShow extends Base {

	private final static int space1 = 86;
	private final static int space2 = 17;
	private Date curDate = new Date(System.currentTimeMillis());
	private boolean mTimeFormat;
	private Context mContext;
	private Drawable timeDrawables[];
	private Drawable dotDrawable;
	private Drawable dateDrawables[];
	private int width;
	private int height;

	private final int[] dateIdZh = { R.drawable.img0, R.drawable.img1,
			R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5,
			R.drawable.img6, R.drawable.img7, R.drawable.img8, R.drawable.img9,
			R.drawable.yue, R.drawable.zhou, R.drawable.zhou7,
			R.drawable.zhou1, R.drawable.zhou2, R.drawable.zhou3,
			R.drawable.zhou4, R.drawable.zhou5, R.drawable.zhou6 };

	private final int[] dateIdEn = { R.drawable.img0, R.drawable.img1,
			R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5,
			R.drawable.img6, R.drawable.img7, R.drawable.img8, R.drawable.img9,
			R.drawable.jan, R.drawable.feb, R.drawable.mar, R.drawable.apr,
			R.drawable.may, R.drawable.jun, R.drawable.jul, R.drawable.aug,
			R.drawable.sept, R.drawable.oct, R.drawable.nov, R.drawable.dec,
			R.drawable.sun, R.drawable.mon, R.drawable.tues, R.drawable.wed,
			R.drawable.thur, R.drawable.fri, R.drawable.sat };

	private final String weekString[] = { "Sun", "Mon", "Tues", "Wed", "Thur",
			"Fri", "Sat" };

	public TimeShow(Context context, int w, int h) {
		super();
		mContext = context;
		width = w;
		height = h;
		mTimeFormat = android.text.format.DateFormat.is24HourFormat(context);
		createDrawable();
	}

	private void createDrawable() {
		timeDrawables = new Drawable[10];
		for (int i = 0; i < timeDrawables.length; i++) {
			timeDrawables[i] = mContext.getResources().getDrawable(
					R.drawable.large0 + i);
		}
		dotDrawable = mContext.getResources().getDrawable(R.drawable.fenhao);

		if (isZh()) {
			dateDrawables = new Drawable[19];
			for (int i = 0; i < dateDrawables.length; i++) {
				dateDrawables[i] = mContext.getResources().getDrawable(
						dateIdZh[i]);
			}
		} else {
			dateDrawables = new Drawable[29];
			for (int i = 0; i < dateDrawables.length; i++) {
				dateDrawables[i] = mContext.getResources().getDrawable(
						dateIdEn[i]);
			}
		}

	}

	private void drawLockImage(Canvas canvas, int x, int y, Drawable drawable) {
		drawable.setBounds(x, y, x + drawable.getIntrinsicWidth(),
				y + drawable.getIntrinsicHeight());
		drawable.draw(canvas);
	}

	private int getWeek(Date curDate) {
		int weekNum = 0;
		String week = String.format(Locale.ENGLISH, "%ta", curDate);
		for (int i = 0; i < 7; i++) {
			if (week.equals(weekString[i])) {
				return i;
			}
		}
		return weekNum;
	}

	private void updateTime(Canvas canvas) {
		curDate.setTime(System.currentTimeMillis());
		int hour = 0;
		int minute = 0;
		if (mTimeFormat) {
			hour = Integer.parseInt(String.format("%tH", curDate));
		} else {
			hour = Integer.parseInt(String.format("%tI", curDate));
		}
		minute = Integer.parseInt(String.format("%tM", curDate));

		int x = (width - timeDrawables[0].getIntrinsicWidth() * 4 - dotDrawable
				.getIntrinsicWidth()) / 2;
		int y = (int) (space1 * height / 800f);
		drawLockImage(canvas, x, y, timeDrawables[hour / 10]);
		drawLockImage(canvas, x + timeDrawables[0].getIntrinsicWidth(), y,
				timeDrawables[hour % 10]);
		drawLockImage(canvas, x + timeDrawables[0].getIntrinsicWidth() * 2, y,
				dotDrawable);
		drawLockImage(canvas, x + timeDrawables[0].getIntrinsicWidth() * 2
				+ dotDrawable.getIntrinsicWidth(), y,
				timeDrawables[minute / 10]);
		drawLockImage(canvas, x + timeDrawables[0].getIntrinsicWidth() * 3
				+ dotDrawable.getIntrinsicWidth(), y,
				timeDrawables[minute % 10]);

		int month = Integer.parseInt(String.format("%tm", curDate));
		int day = Integer.parseInt(String.format("%td", curDate));
		int week = getWeek(curDate);

		if (isZh()) {
			x = (width - dateDrawables[0].getIntrinsicWidth() * 4
					- dateDrawables[10].getIntrinsicWidth() * 4 - 30) / 2;
			y = (int) ((space1 + space2) * height / 800f + timeDrawables[0]
					.getIntrinsicHeight());
			drawLockImage(canvas, x, y, dateDrawables[month / 10]);
			drawLockImage(canvas, x + dateDrawables[0].getIntrinsicWidth(), y,
					dateDrawables[month % 10]);
			drawLockImage(canvas, x + dateDrawables[0].getIntrinsicWidth() * 2,
					y, dateDrawables[10]);
			drawLockImage(canvas, x + dateDrawables[0].getIntrinsicWidth() * 2
					+ dateDrawables[10].getIntrinsicWidth(), y,
					dateDrawables[day / 10]);
			drawLockImage(canvas, x + dateDrawables[0].getIntrinsicWidth() * 3
					+ dateDrawables[10].getIntrinsicWidth(), y,
					dateDrawables[day % 10]);
			drawLockImage(canvas, x + dateDrawables[0].getIntrinsicWidth() * 4
					+ dateDrawables[10].getIntrinsicWidth(), y,
					dateDrawables[12]);
			drawLockImage(canvas, x + dateDrawables[0].getIntrinsicWidth() * 4
					+ dateDrawables[10].getIntrinsicWidth() * 2 + 30, y,
					dateDrawables[11]);
			drawLockImage(canvas, x + dateDrawables[0].getIntrinsicWidth() * 4
					+ dateDrawables[10].getIntrinsicWidth() * 3 + 30, y,
					dateDrawables[12 + week]);
		} else {
			x = (width - dateDrawables[0].getIntrinsicWidth() * 2
					- dateDrawables[10].getIntrinsicWidth() * 2 - 30) / 2;
			y = (int) ((space1 + space2) * height / 800f + timeDrawables[0]
					.getIntrinsicHeight());
			drawLockImage(canvas, x, y, dateDrawables[10 + month / 10]);
			drawLockImage(canvas, x + dateDrawables[10].getIntrinsicWidth(), y,
					dateDrawables[day / 10]);
			drawLockImage(canvas, x + dateDrawables[0].getIntrinsicWidth()
					+ dateDrawables[10].getIntrinsicWidth(), y,
					dateDrawables[day % 10]);
			drawLockImage(canvas, x + dateDrawables[0].getIntrinsicWidth() * 2
					+ dateDrawables[10].getIntrinsicWidth() + 30, y,
					dateDrawables[23 + week]);
		}

	}

	@Override
	public synchronized void onDraw(Canvas canvas) {
		updateTime(canvas);
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
		if (timeDrawables != null) {
			for (int i = 0; i < timeDrawables.length; i++) {
				freedDrawable(timeDrawables[i]);
			}
		}
		freedDrawable(dotDrawable);
		if (dateDrawables != null) {
			for (int i = 0; i < dateDrawables.length; i++) {
				freedDrawable(dateDrawables[i]);
			}
		}
	}

	private boolean isZh() {
		Locale locale = mContext.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("zh"))
			return true;
		else
			return false;
	}
}
