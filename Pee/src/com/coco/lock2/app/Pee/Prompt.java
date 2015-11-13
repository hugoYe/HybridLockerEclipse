package com.coco.lock2.app.Pee;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

public class Prompt extends Base {
	
	private final static int[] showinfo={183,10,35};
	private boolean showflag=true;
	private boolean touchflag=false;
	private boolean initflag=true;
	private int Screen_h=0;
	private final static String TAG="Prompt";
	private Point Ptourch_start=new Point();
	private final static int[] ArrowImageId={
		R.drawable.arrow001,
		R.drawable.arrow002,
		R.drawable.arrow003,
		R.drawable.arrow004,	
	};
	
	private Point[] location=new Point[5];
	private Bitmap[] arrows=new Bitmap[4];
	private Bitmap Connection;
	private Child child;
	
	private final static int DELAYTIME=800;
	private int refreshstate=0;
	private Point Ptouch=new Point();
	private float touch_w=0;
	private float touch_h=0;
	private final static int EXPAND_X=80;
	private final static int EXPAND_Y=30;
	public final static int LEVEL=5;
	private int level=1;
	
	public synchronized int getLevel() {
		return level;
	}

	public synchronized void setLevel(int level) {
		this.level = level;
	}
	private Runnable Refresh_handler= new Runnable(){
		public void run() {
			if(showflag)
			{
				if(refreshstate==(arrows.length-1))
				{
					int i=getLevel();
					setLevel((i+2)%5);
				}
				refreshstate=(++refreshstate)%arrows.length;
				
			}
			startThread();
		}
		
	};
	public Prompt(){
		super();
		setRefresh(Refresh_handler,DELAYTIME);
		Bitmap[] imgs=ObjectGetter.getBitmaps(ArrowImageId);
		arrows=ObjectGetter.ImageEnlarge(imgs, (float) 1.5);
		Connection=ObjectGetter.getBitmap(R.drawable.arrow005);
	}
	
	public Prompt(Child child) {
		// TODO Auto-generated constructor stub
		this();
		this.child=child;
	}
int tp=0;

	@Override
	public synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//canvas.drawRect(Ptouch.x, Ptouch.y, Ptouch.x+touch_w, Ptouch.y+touch_h, new Paint());
		//Paint t1= new Paint();
		//t1.setTextSize(20);
		//canvas.drawText(""+tp+", x="+Ptourch_start.x+",y="+Ptourch_start.y, 50,50,t1);
		
		if(!showflag)
			return ;
		int j=0;
//		for(int i=0;i<location.length;i++)
			for(int i=0;i<1;i++)
		{
			if(i%2==0)
			{
				canvas.drawBitmap(arrows[(refreshstate+j)%arrows.length], location[i].x,
						location[i].y, ObjectGetter.getBitmapPaint());
			}else{
				canvas.drawBitmap(Connection, location[i].x,
						location[i].y, ObjectGetter.getBitmapPaint());
			}
		
		}
	
	}
	
	@Override
	public void InitUI(int w, int h) {
		// TODO Auto-generated method stub
		Screen_h=h;
		super.InitUI(w, h);
		float x=w-showinfo[0]*w/480;
		float y=h-showinfo[2]*h/800;
		float space=showinfo[1]*h/800;
		for(int i=0;i<location.length;i++)
		{
			location[i]=new Point();
			if(i==0)
			{
				location[i].x=x-arrows[0].getWidth()/2;
				location[i].y=y-arrows[0].getHeight();
			}
			else if(i%2==0)
			{
				location[i].x=location[0].x;
				location[i].y=location[i-1].y-space-arrows[0].getHeight();
			}
			else
			{
				location[i].x=x-Connection.getWidth()/2;
				location[i].y=location[i-1].y-space-Connection.getHeight();
			}
		}
		Ptouch.x=location[4].x-EXPAND_X*w/480;
		Ptouch.y=location[4].y-EXPAND_Y*h/800;
		touch_h=location[0].y-location[4].y+2*EXPAND_Y*h/800+arrows[0].getHeight();
		touch_w=arrows[0].getWidth()+2*EXPAND_X*w/480;
	}
	
	public boolean isShowflag() {
		return showflag;
	}
	private boolean is_in_Range(Point p)
	{
		boolean ret=false;
		if(p.y>Screen_h*0.6)
		{
			ret=true;
		}
		return ret;
	}
	private int get_Level(Point p)
	{
		int ret=0;
		float space=Ptourch_start.y-p.y;
		
		if(space>=0 &&space<touch_h)
		{
			ret=(int) Math.floor(space*LEVEL/touch_h)+1;
		}
		
		return ret;
	}
	
	public void setShowflag(boolean showflag) {
		this.showflag = showflag;
	}
	public void vibrate()
	{
		Message msg=new  Message();
		msg.what=101;
		getMhandler().sendMessage(msg);
	}
	public void onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(!initflag)
			return ;
		Point p=new Point(event.getX(),event.getY());
		tp=get_Level(p);
		
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
		
			if(is_in_Range(p) )
			{
				touchflag=true;
//				child.setChildstate(-1);
				Ptourch_start.x=p.x;
				Ptourch_start.y=p.y;
				vibrate();
			}
//			setShowflag(false);
		}else if(event.getAction()==MotionEvent.ACTION_MOVE){
			if(touchflag)
			{
				int level=get_Level(p);
				Log.d("song1","level ="+level);
				if(level>=3){
					child.setChildstate(level>5?5:level);
				}else {
					setShowflag(false);
					child.setChildstate(-1);
				}
			}
		
		}else if(event.getAction()==MotionEvent.ACTION_UP){
			setShowflag(true);
			touchflag=false;
			child.setChildstate(0);
		}
		
	}

}
