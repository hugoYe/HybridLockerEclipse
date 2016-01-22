package com.coco.lock2.app.view;

import android.graphics.Canvas;

public abstract interface IBaseView {

	public abstract void Create();

	public abstract void Destroy();

	public abstract void InitInfo();

	public abstract void Pause();

	public abstract void Resume();

	public abstract void drawContent(Canvas paramCanvas, String paramString);

}
