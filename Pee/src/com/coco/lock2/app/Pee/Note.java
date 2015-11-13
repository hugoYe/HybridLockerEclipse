package com.coco.lock2.app.Pee;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Note extends Base {
	
	
	private boolean showflag=false;
	private final static Point OLD=new Point(250,500);
	private final static int DELAYTIME=1000/4;
	private Point[] location= new Point[5];
	private int refreshstate=0;
	private int show_num=1;
	
	private Bitmap note_images[]=null;
	private final static int[] noteImageId={
		R.drawable.note001,
		R.drawable.note002,
		R.drawable.note003,
		R.drawable.note004,
	};
	
	private Runnable Refresh_handler= new Runnable(){

		public void run() {
			// TODO Auto-generated method stub
			if(showflag)
			{
				if(show_num<location.length)
				{
					show_num++;
				}
				refreshstate = (++refreshstate)%location.length;
			}
			startThread();
		}
		
	};
	private void loadImage(){
		if(note_images==null)
			note_images=ObjectGetter.getBitmaps(noteImageId);
	}
	public boolean isShowflag() {
		return showflag;
	}
	@Override
	public void InitUI(int w, int h) {
		super.InitUI(w, h);
		loadImage();
		for(int i=0;i<location.length;i++)
		{
			location[i]=new Point();
			location[i].x=OLD.x*w/480+(note_images[0].getWidth()+2)*i;
			location[i].y=OLD.y*h/800-(note_images[0].getHeight()-20)*i;
			if(i%2==0)
			{
				location[i].y += note_images[0].getHeight()*0.2;
			}
			else
			{
				location[i].y -= note_images[0].getHeight()*0.2;
			}
		}
	}
	public void setShowflag(boolean showflag) {
		if(showflag==false)
		{
			refreshstate=0;
			show_num=0;
		}
		this.showflag = showflag;
	}
	public Note(){
		setRefresh(Refresh_handler, DELAYTIME);
		loadImage();
	}
	@Override
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(!showflag)
			return ;
		
		for(int i=0;i<show_num;i++)
		{
			canvas.drawBitmap(note_images[(i+refreshstate)%note_images.length], location[i].x,
					location[i].y, ObjectGetter.getBitmapPaint());
		}
		
	}
	private void clearinfo(){
		showflag=false;
		refreshstate=0;
		show_num=1;
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
