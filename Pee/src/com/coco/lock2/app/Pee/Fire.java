package com.coco.lock2.app.Pee;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Message;
import android.util.Log;

public class Fire extends Base {
	
	

	private static final int[] fireImageId={
		R.drawable.fire001,
		R.drawable.fire002,
		R.drawable.fire003,
		R.drawable.fire004,
		R.drawable.fire005,
		R.drawable.fire006,
		R.drawable.fire007,
		R.drawable.fire008
	};
	
	private static final int[] ExtinguishImageId={
		R.drawable.extinguish001,
		R.drawable.extinguish002,
		R.drawable.extinguish003,
		R.drawable.extinguish004,
		R.drawable.extinguish005,
		R.drawable.extinguish006,
		R.drawable.extinguish007,
		R.drawable.extinguish008,
		R.drawable.extinguish009,
		R.drawable.extinguish010,
		//R.drawable.extinguish011,
		R.drawable.extinguish012,
		R.drawable.extinguish013,
		//R.drawable.extinguish014,
		R.drawable.extinguish015,
		//R.drawable.extinguish016,
		R.drawable.extinguish017,
		//R.drawable.extinguish018,
		R.drawable.extinguish019,
		//R.drawable.extinguish020,
	};
	private int firestate=0;
	
	private int refreshstate=0;
	private final static int DELAYTIME=1000/20;
	private Bitmap[] fires;
	private Bitmap[] extinguish;
	private final static int space1=15;
	private final static int space2=17;
	private float Fire_x;
	private float Fire_y;
	public boolean closeflag=false;
	public Fire(){
		super();
		setRefresh(Refresh_handler,DELAYTIME);
		Bitmap[] fireold=ObjectGetter.getBitmaps(fireImageId);
		fires=ObjectGetter.ImageEnlarge(fireold,2);
		
		Bitmap[] extinguishold=ObjectGetter.getBitmaps(ExtinguishImageId);
		extinguish=ObjectGetter.ImageEnlarge(extinguishold,2);
		
	}
	
	private Runnable Refresh_handler= new Runnable(){
		public void run() {
			
			if(firestate==0)
				refreshstate=(++refreshstate)%fireImageId.length;
			else if(firestate==1)
				refreshstate=(++refreshstate)%ExtinguishImageId.length;
			if(refreshstate==ExtinguishImageId.length-1)
			{
			    Log.d("BaseView","refreshstate = " + refreshstate + " firestate = " + firestate);
				exitlock();
			}
			startThread();
		}
		
	};
	public void exitlock()
	{
		Message msg=new  Message();
		msg.what=100;
		getMhandler().sendMessage(msg);
	}
	@Override
	public void onDraw(Canvas canvas) {
		Bitmap image;
		if(firestate==0)
		{
			image=fires[refreshstate];
		}
		else
		{
			image=extinguish[refreshstate];
		}
 		canvas.drawBitmap(image, Fire_x,
				Fire_y, ObjectGetter.getBitmapPaint());
	}
	@Override
	public void InitUI(int w, int h) {
		// TODO Auto-generated method stub
		super.InitUI(w, h);
		//Fire_x=w-fires[0].getWidth()-space1*w/480;
		Fire_y=h-fires[0].getHeight()-space2*h/800;
	}
	public void setFire_x(float x)
	{
		Fire_x=x-fires[0].getWidth()/2;
	}
	public int getFirestate() {
		return firestate;
	}
	
	public void setFirestate(int firestate) {
		if(this.firestate==1 && refreshstate>7)
		{
			closeflag=true;
			return ;
		}
		this.firestate = firestate;
	}
	private void clearinfo(){
		if(!closeflag)
		{
			firestate=0;
			refreshstate=0;
		}
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		clearinfo();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		clearinfo();
	}
}
