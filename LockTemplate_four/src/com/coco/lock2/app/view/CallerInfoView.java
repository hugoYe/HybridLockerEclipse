package com.coco.lock2.app.view;

import com.coco.lock2.app.info.CallNotify;
import com.coco.lock2.app.info.MessageNotify;
import com.coco.lock2.app.info.Variables;
import com.coco.lock2.app.locktemplate.R;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class CallerInfoView implements IBaseView {

	private Paint mCallerPaint;
	private int mCallerPaintHeight;
	private Context mContext;
	private Bitmap mImageCaller;
	private Bitmap mImageMessage;
	private View mView;
	private int mCallerX;
	private int mCallerY;
	private int mMessageX;
	private int mMessageY;
	private MessageNotify msgNotify = null;
	private CallNotify callNotify = null;
	private int missCallNum = 0;
	private int unreadMessageNum = 0;

	public CallerInfoView(Context paramContext, IViewInfo paramIViewInfo) {
		this.mContext = paramContext;
		this.mView = paramIViewInfo.getView();

		msgNotify = new MessageNotify();
		msgNotify.registerObserver(paramContext, msgObserver);
		callNotify = new CallNotify();
		callNotify.registerObserver(paramContext, callObserver);
		new CallChangeThread().start();
		new SmsChangeThread().start();
	}

	ContentObserver msgObserver = new ContentObserver(null) {

		@Override
		public void onChange(boolean selfChange) {
			new SmsChangeThread().start();
		}
	};
	ContentObserver callObserver = new ContentObserver(null) {

		@Override
		public void onChange(boolean selfChange) {
			new CallChangeThread().start();
		}
	};

	public class CallChangeThread extends Thread {

		public CallChangeThread() {
			super();
		}

		@Override
		public void run() {
			missCallNum = callNotify.getMissedCallCount(mContext);
			// missCallNum = 1;
			if (missCallNum > 0) {
				mView.postInvalidate();
			}
		}
	}

	public class SmsChangeThread extends Thread {

		public SmsChangeThread() {
			super();
		}

		@Override
		public void run() {
			unreadMessageNum = msgNotify.getUnreadCount(mContext);
			// unreadMessageNum = 1;
			if (unreadMessageNum > 0) {
				mView.postInvalidate();
			}
		}
	}

	@Override
	public void Create() {

	}

	@Override
	public void Destroy() {
		if (msgNotify != null) {
			msgNotify.unregisterObserver(mContext, msgObserver);
		}
		if (callNotify != null) {
			callNotify.unregisterObserver(mContext, callObserver);
		}
	}

	@Override
	public void InitInfo() {
		this.mCallerPaint = new Paint();
		this.mCallerPaint.setAntiAlias(true);
		this.mCallerPaint.setShadowLayer(1.0F, 2.0F, 2.0F, 0x88000000);
		this.mCallerPaint.setColor((int) Long.parseLong(
				Variables.Phone_color.substring(1,
						Variables.Phone_color.length()), 16));
		this.mCallerPaint
				.setTextSize(Variables.Phone_fontSize *  Variables.getScreenScaleX());
		Paint.FontMetrics localFontMetrics = this.mCallerPaint.getFontMetrics();
		this.mCallerPaintHeight = (2 + (int) Math.ceil(localFontMetrics.descent
				- localFontMetrics.top));

		mImageCaller = ((BitmapDrawable) this.mView.getResources().getDrawable(
				R.drawable.phone)).getBitmap();
		mCallerX = (int) (Variables.Call_image_x * Variables.getScreenScaleX());
		if (Variables.Call_vertical_align.equals("bottom")) {
			mCallerY = Variables.screen_height
					- mImageCaller.getHeight()
					- (int) (Variables.Call_image_y * Variables
							.getScreenScaleY());
		} else if (Variables.Call_vertical_align.equals("top")) {
			mCallerY = (int) (Variables.Call_image_y * Variables
					.getScreenScaleY());
		}

		mImageMessage = ((BitmapDrawable) this.mView.getResources()
				.getDrawable(R.drawable.messages)).getBitmap();
		mMessageX = (int) (Variables.Message_image_x * Variables
				.getScreenScaleX());
		if (Variables.Message_vertical_align.equals("bottom")) {
			mMessageY = Variables.screen_height
					- mImageMessage.getHeight()
					- (int) (Variables.Message_image_y * Variables
							.getScreenScaleY());
		} else if (Variables.Message_vertical_align.equals("top")) {
			mMessageY = (int) (Variables.Message_image_y * Variables
					.getScreenScaleY());
		}
	}

	@Override
	public void Pause() {

	}

	@Override
	public void Resume() {

	}

	private void mDrawCallerInfo(Canvas paramCanvas) {
		if (missCallNum > 0) {
			paramCanvas.drawBitmap(this.mImageCaller, mCallerX, mCallerY,
					this.mCallerPaint);
			paramCanvas
					.drawText(
							Integer.toString(missCallNum),
							mCallerX + this.mImageCaller.getWidth(),
							mCallerY
									+ (this.mImageCaller.getHeight() - this.mCallerPaintHeight / 4),
							this.mCallerPaint);
		}
		if (unreadMessageNum > 0) {
			paramCanvas.drawBitmap(this.mImageMessage, mMessageX, mMessageY,
					this.mCallerPaint);
			paramCanvas
					.drawText(
							Integer.toString(unreadMessageNum),
							mMessageX + this.mImageMessage.getWidth(),
							mMessageY
									+ (this.mImageMessage.getHeight() - this.mCallerPaintHeight / 4),
							this.mCallerPaint);
		}
	}

	@Override
	public void drawContent(Canvas paramCanvas, String paramString) {
		mDrawCallerInfo(paramCanvas);
	}

}
