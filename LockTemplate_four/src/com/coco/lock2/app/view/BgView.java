package com.coco.lock2.app.view;

import com.coco.lock2.app.info.Variables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class BgView implements IBaseView {

	private Bitmap mImageBg = null;
	private View mView;
	private IViewInfo viewInfo;

	public BgView(Context paramContext, IViewInfo paramIViewInfo) {
		this.viewInfo = paramIViewInfo;
		this.mView = this.viewInfo.getView();
	}

	@Override
	public void Create() {

	}

	@Override
	public void Destroy() {

	}

	@Override
	public void InitInfo() {
		this.mImageBg = this.viewInfo.getBg();
	}

	@Override
	public void Pause() {

	}

	@Override
	public void Resume() {

	}

	@Override
	public void drawContent(Canvas paramCanvas, String paramString) {
		if (this.mImageBg != null) {
			if (Variables.mAlign.equals("top")) {
				paramCanvas.drawBitmap(this.mImageBg, 0.0F, 0.0F, null);
			} else if (Variables.mAlign.equals("center")) {
				paramCanvas
						.drawBitmap(this.mImageBg, 0.0F, (this.mView
								.getHeight() - this.mImageBg.getHeight()) / 2,
								null);
			} else if (Variables.mAlign.equals("bottom")) {
				paramCanvas.drawBitmap(this.mImageBg, 0.0F,
						this.mView.getHeight() - this.mImageBg.getHeight(),
						null);
			}
		}
	}

}
