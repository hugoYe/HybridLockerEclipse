package com.coco.lock2.app.Pee;

import android.graphics.Canvas;
import android.os.Handler;

public abstract class Base {
	
	private Runnable Refresh_handler=null;
	private Handler mhandler;
	public Handler getMhandler() {
		return mhandler;
	}
	private int delaytime=0;
	
	public Base()
	{
		mhandler=ObjectGetter.getHandler();
	}
	
	protected void setRefresh(Runnable r,int time)
	{
		Refresh_handler=r;
		delaytime=time;
	}
	
	protected void startThread()
	{
		mhandler.postDelayed(Refresh_handler, delaytime);
	}
	
	public void onResume() {
		if(Refresh_handler!=null)
		{
			mhandler.removeCallbacks(Refresh_handler);
			mhandler.postDelayed(Refresh_handler, delaytime);
		}
	}

	public void onPause() {
		if(Refresh_handler!=null)
		{
			mhandler.removeCallbacks(Refresh_handler);
		}
	}

	public void onDestroy(){
		
	}
	
	public void InitUI(int w,int h)
	{
		
		startThread();
	}
	public abstract void onDraw(Canvas canvas);
	public  void delayHandle(){
		
	}
}
