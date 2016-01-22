package com.coco.lock2.app.view;

import com.coco.lock2.app.info.Variables;
import com.coco.lock2.app.locktemplate.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.BatteryManager;
import android.view.View;

public class ChargeView implements IBaseView {

	private Context mContext;
	private int mPower;
	private View mView;
	private IViewInfo viewInfo;
	public static String mIsChargeState = "未充电";
	private Paint mBatteryPaint;
	private int mBatteryPaintHeight;
	private int mBatteryX;
	private int mBatteryY;
	private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
		public void onReceive(Context paramContext, Intent paramIntent) {
			String action = paramIntent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				ChargeView.this.mPower = paramIntent.getIntExtra("level", 0);
				int i = paramIntent.getIntExtra("status", 1);
				if (i == BatteryManager.BATTERY_STATUS_CHARGING) {
					ChargeView.mIsChargeState = "充电中";
				} else if (i == BatteryManager.BATTERY_STATUS_FULL) {
					ChargeView.mIsChargeState = "充满电";
				} else if (i == BatteryManager.BATTERY_STATUS_FULL) {
					ChargeView.mIsChargeState = "充满电";
				} else {
					ChargeView.mIsChargeState = "未充电";
				}
				ChargeView.this.mView.postInvalidate();
			} else if (action.equals(Intent.ACTION_TIME_TICK)) {
				ChargeView.this.mView.postInvalidate();
			}
		}
	};

	public ChargeView(Context paramContext, IViewInfo paramIViewInfo) {
		this.mContext = paramContext;
		this.viewInfo = paramIViewInfo;
		this.mView = this.viewInfo.getView();
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		localIntentFilter.addAction(Intent.ACTION_TIME_TICK);
		this.mContext
				.registerReceiver(this.mBatteryReceiver, localIntentFilter);
	}

	private void mDrawBatteryPrompt(Canvas paramCanvas) {
		String str1 = "";
		if (mIsChargeState.equals("充电中")) {
			str1 = mContext.getResources().getString(R.string.charging) + "("
					+ mPower + ")";

		} else if (mIsChargeState.equals("充满电")) {
			str1 = this.mContext.getResources()
					.getString(R.string.battery_full);
		} else if (this.mPower <= 10) {
			str1 = this.mContext.getResources().getString(R.string.battery_low);
		}
		if (!mIsChargeState.equals("未充电")) {
			if (Variables.Battery_horizontal_align.equals("center")) {
				mBatteryX = (this.mView.getWidth() - (int) this.mBatteryPaint
						.measureText(str1)) / 2;
			} else if (Variables.Battery_horizontal_align.equals("left")) {
				mBatteryX = 0;
			} else if (Variables.Battery_horizontal_align.equals("right")) {
				mBatteryX = this.mView.getWidth()
						- (int) this.mBatteryPaint.measureText(str1);
			}
			paramCanvas.drawText(str1, mBatteryX, mBatteryY,
					this.mBatteryPaint);
		}
	}

	public void Create() {
	}

	public void Destroy() {
		if (mBatteryReceiver != null) {
			this.mContext.unregisterReceiver(this.mBatteryReceiver);
			mBatteryReceiver = null;
		}
	}

	public void InitInfo() {
		this.mBatteryPaint = new Paint();
		this.mBatteryPaint.setAntiAlias(true);
		this.mBatteryPaint.setShadowLayer(1.0F, 2.0F, 2.0F, 0x88000000);
		this.mBatteryPaint.setColor((int) Long.parseLong(
				Variables.Battery_color.substring(1,
						Variables.Battery_color.length()), 16));
		this.mBatteryPaint.setTextSize(30 * Variables.getScreenScaleX());

		Paint.FontMetrics localFontMetrics = this.mBatteryPaint
				.getFontMetrics();
		this.mBatteryPaintHeight = (2 + (int) Math
				.ceil(localFontMetrics.descent - localFontMetrics.top));

		mBatteryX = (int) (Variables.Battery_x * Variables.getScreenScaleX());
		if (Variables.Battery_vertical_align.equals("bottom")) {
			mBatteryY = Variables.screen_height
					- (int) (Variables.Battery_y * Variables.getScreenScaleY());
		} else if (Variables.Battery_vertical_align.equals("top")) {
			mBatteryY = (int) (Variables.Battery_y * Variables
					.getScreenScaleY()) + this.mBatteryPaintHeight;
		}
	}

	public void Pause() {
	}

	public void Resume() {
	}

	public void drawContent(Canvas paramCanvas, String paramString) {
		mDrawBatteryPrompt(paramCanvas);
	}
}
