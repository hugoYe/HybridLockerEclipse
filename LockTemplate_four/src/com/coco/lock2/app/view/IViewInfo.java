package com.coco.lock2.app.view;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;

public abstract interface IViewInfo {

	public abstract boolean IsPressed();

	public abstract Bitmap getBg();

	public abstract Matrix getMatrix();

	public abstract View getView();
}
