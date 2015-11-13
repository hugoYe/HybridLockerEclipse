package com.coco.lock2.app.Pee;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class LockImage  {

	private int image_id;
	private Point location;
	private float w,h;
	private Bitmap bt=null;
	private boolean type;//ture; 为顶点 ，false 为 低点
	
	public LockImage(Bitmap img,boolean t)
	{
		bt=img;
		w=bt.getWidth();
		h=bt.getHeight();
		type =t;
		location=new Point();
		Log.d("song","LockImage(Bitmap img,boolean t) \n bt.getWidth()"+bt.getWidth()+"\n bt.getHeight() "+bt.getHeight());
	}
	
	public LockImage(int id,boolean t)
	{
	
		this.image_id=id;
		w=0;
		h=0;
		
		if(image_id!=0)
		{
			bt=((BitmapDrawable) ObjectGetter.getView().getResources().getDrawable(
					image_id)).getBitmap();
			w=bt.getWidth();
			h=bt.getHeight();
		}
		type =t;
		location=new Point();
		Log.d("song","id="+id+", w="+w+", h="+h);
		
	}
	public LockImage()
	{
		type=true;
		location=new Point();
	}
	
	public void setImageId(int id)
	{
		this.image_id=id;
		bt=((BitmapDrawable) ObjectGetter.getView().getResources().getDrawable(
				image_id)).getBitmap();
		w=bt.getWidth();
		h=bt.getHeight();
	}
	
	public  float getWidth()
	{
		return w;
	}
	
	public float getHeight(){
		return h;
	}
	
	public LockImage(int id,Point p,boolean t)
	{
		this(id,t);
		this.location.x=p.x;
		this.location.y=p.y;	
	}
	
	public synchronized  void setLocation(Point p)
	{
		this.location.x=p.x;
		this.location.y=p.y;
	}
	
	public Point getLocation()
	{
		return  location;
	}
	
	public synchronized void onDraw(Canvas canvas) 
	{
		float x=0,y=0;
		Bitmap image=bt;
		if(type)
		{
			x=location.x;
			y=location.y;
		}
		else
		{
			x=location.x;
			y=location.y-image.getHeight();
		}
		canvas.drawBitmap(image, x,
				y, ObjectGetter.getBitmapPaint());
	}
	
	public int getImageId()
	{
		return image_id;
	}
	
}
