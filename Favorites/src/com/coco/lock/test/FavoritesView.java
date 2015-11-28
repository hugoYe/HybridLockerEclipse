package com.coco.lock.test;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class FavoritesView extends View {

	public FavoritesView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(0x88FF0000);
		super.onDraw(canvas);
	}
}
