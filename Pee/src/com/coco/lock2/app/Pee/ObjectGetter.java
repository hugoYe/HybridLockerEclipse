package com.coco.lock2.app.Pee;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;

public class ObjectGetter {
	private static View mView;
	private static Paint mBitmapPaint = null;
	private static Handler mhandler;

	public static View getView() {
		return mView;
	}

	public static Paint getBitmapPaint() {
		return mBitmapPaint;
	}

	public static Handler getHandler() {
		return mhandler;
	}

	public static void Init(Context c, View v, Handler handler) {
		mView = v;
		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
		mhandler = handler;
	}

	public static Bitmap getBitmap(int imageId) {
		return ((BitmapDrawable) mView.getResources().getDrawable(imageId))
				.getBitmap();
	}

	public static Bitmap[] getBitmaps(int[] imageIDs) {
		Bitmap[] images = new Bitmap[imageIDs.length];
		for (int i = 0; i < imageIDs.length; i++) {
			images[i] = ((BitmapDrawable) mView.getResources().getDrawable(
					imageIDs[i])).getBitmap();
		}
		return images;
	}

	public static Bitmap[] ImageEnlarge(Bitmap[] image, float size) {
		Bitmap[] images = new Bitmap[image.length];
		int scaleWidth, scaleHeight;
		scaleWidth = image[0].getWidth();
		scaleHeight = image[0].getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(size, size);
		for (int i = 0; i < image.length; i++) {
			images[i] = Bitmap.createBitmap(image[i], 0, 0, scaleWidth,
					scaleHeight, matrix, true);
		}
		return images;
	}
}
