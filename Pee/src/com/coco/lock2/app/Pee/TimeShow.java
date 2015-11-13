package com.coco.lock2.app.Pee;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.format.Time;
import android.util.Log;

public class TimeShow extends Base {
	
	private final static int space1=86;
	private final static int space2=17;
	private final static int DELAYTIME=1000;
	
    private SimpleDateFormat dateFormat ;
	private SimpleDateFormat time12Format = new SimpleDateFormat("hh:mm");
	private SimpleDateFormat time24Format = new SimpleDateFormat("kk:mm");
	private String timestr="";
	private String datestr="";
	private Context mcontext;
	private int ViewW=0;
	private int ViewH=0;
	LockImage timeimg[];
	LockImage dateimg[];
	private String DATE;
	private String WEEK;
	
	private final static int[] LargeId={
		R.drawable.large0,
		R.drawable.large1,
		R.drawable.large2,
		R.drawable.large3,
		R.drawable.large4,
		R.drawable.large5,
		R.drawable.large6,
		R.drawable.large7,
		R.drawable.large8,
		R.drawable.large9,
	};
	
	private final static int[] SmallId={
		R.drawable.img0,
		R.drawable.img1,
		R.drawable.img2,
		R.drawable.img3,
		R.drawable.img4,
		R.drawable.img5,
		R.drawable.img6,
		R.drawable.img7,
		R.drawable.img8,
		R.drawable.img9,
	};
	private final static int[] EN_MONTH={
		R.drawable.jan,
		R.drawable.feb,
		R.drawable.mar,
		R.drawable.apr,
		R.drawable.may,
		R.drawable.jun,
		R.drawable.jul,
		R.drawable.aug,
		R.drawable.sept,
		R.drawable.oct,
		R.drawable.nov,
		R.drawable.dec,
	
	};
	private final static int[] EN_WEEK={
		R.drawable.sun,
		R.drawable.mon,
		R.drawable.tues,
		R.drawable.wed,
		R.drawable.thur,
		R.drawable.fri,
		R.drawable.sat,
	
	};
	private final static int[] DateId={
		R.drawable.yue,
		R.drawable.zhou7,
		R.drawable.zhou
	};
	
	private final static int[] WeekId={
		R.drawable.zhou7,
		R.drawable.zhou1,
		R.drawable.zhou2,
		R.drawable.zhou3,
		R.drawable.zhou4,
		R.drawable.zhou5,
		R.drawable.zhou6,
	};
	private Bitmap en_monthimages[];
	private Bitmap en_weekimages[];
	private Bitmap en_dayimages[];
	private int en_month=0;
	private int en_week=0;
	private int en_day=0;
	public TimeShow(Context context){
		super();
		setRefresh(Refresh_handler, DELAYTIME);
		mcontext=context;
		dateFormat=new SimpleDateFormat(mcontext.getString(R.string.timeformat));
		DATE=mcontext.getString(R.string.date);
		WEEK=mcontext.getString(R.string.shuzi);
		
		en_monthimages = ObjectGetter.getBitmaps(EN_MONTH);
		en_weekimages = ObjectGetter.getBitmaps(EN_WEEK);
		en_dayimages = ObjectGetter.getBitmaps(SmallId);
		InitInfo();
	}
	
	private void updateTime()
	{
		Date curDate = new Date(System.currentTimeMillis());
		if (ObjectGetter.set.is24HourFormat()) {
			timestr = time24Format.format(curDate);
		} else {
			timestr = time12Format.format(curDate);
		}
		datestr=dateFormat.format(curDate);
		 Calendar calendar = Calendar.getInstance();  
		 calendar.setTime(curDate);  
		 int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);  
		 if (dayIndex >= 1 || dayIndex <= 7) {  
			 datestr+=" "+DATE.charAt(2)+WEEK.charAt(dayIndex-1);
		 }  
			Time time1 = new Time("GMT+8");       
	        time1.setToNow();   
	        en_month=time1.month;//(1-11)
	        en_day=time1.monthDay;
	        en_week=time1.weekDay;
			String date=time1.weekDay+"."+""+(time1.month+1)+"."+time1.monthDay;
			Log.d("song","updateTime date="+date);
		 
	}
	private void getLargeImage()
	{
		 timeimg=new LockImage[timestr.length()];
		 for(int i=0;i<timeimg.length;i++)
		 {
			 timeimg[i]=new LockImage();
			 if(i!=2)
			 {
				 timeimg[i].setImageId(LargeId[timestr.charAt(i)-'0']);
			 }
			 else
			 {
				 timeimg[i].setImageId(R.drawable.fenhao);
			 }
		 }
	}
	
	private void getSmallImage()
	{
		dateimg=new LockImage[datestr.length()-1];
		int kj=0;
		for(int i=0;i<dateimg.length;i++)
		 {
			char c;
			 dateimg[i]=new LockImage();
			 c= datestr.charAt(kj);
			 if(c==' ')
			 {
				 kj++;
				 c= datestr.charAt(kj);
			 }
			 kj++;
			 
				 if(c>='0' && c <='9')
				 {
					 dateimg[i].setImageId(SmallId[datestr.charAt(i)-'0']);
				 }
				 else{
					 int j=0;
					 for(;j<DATE.length();j++)
					 {
						 if(c==DATE.charAt(j))
						 {
							 dateimg[i].setImageId(DateId[j]);
							 break;
						 }
					 }
					 if(j==DATE.length())
					 {
						 for(j=0;j<WEEK.length();j++)
						 {
							 if(c==WEEK.charAt(j))
							 {
								 dateimg[i].setImageId(WeekId[j]);
								 break;
							 }
						 }
					 }
				 }
			 }
		 
	}
	
	private synchronized void InitInfo()
	{
		updateTime();
		getLargeImage();
		getSmallImage(); 
	}
	
	private synchronized void ImageLocate()
	{
		ImageLocate(0,space1*ViewH/800);
		ImageLocate(1,(space1+space2)*ViewH/800 +getTimeStringHeight(0));
	}
	private void ImageLocate(int type,float h)
	{
		LockImage img[];
		if(type==0)
		{
			img=timeimg;
		}
		else
		{
			img=dateimg;
		}
		Point p=new Point();
		p.x= (ViewW-getTimeStringWidth(type))/2;
		p.y=h+getTimeStringHeight(type);
		float y=p.y;
		for(int i=0;i<img.length;i++)
		{
			if(i!=0)
			{
				p.x+=img[i-1].getWidth();
			}
			
			p.y=y-img[i].getHeight();
			if(type!=0 && i==img.length-2)
			{	
				p.x+=img[0].getWidth();
			}
			
			img[i].setLocation(p);
		}
		
	}
	@Override
	public void InitUI(int w, int h) {
		ViewW=w;
		ViewH=h;
		ImageLocate();
		super.InitUI(w, h);

	
	}
	
	private Runnable Refresh_handler= new Runnable(){
		public void run() {
			InitInfo();
			ImageLocate();
			startThread();
		}
		
	};
	
	@Override
	public synchronized void onDraw(Canvas canvas) {
		Log.d("song","timestr lenght="+timestr.length()+
				"\n datestr lenght="+datestr.length());
		drawTimeString(0,canvas);
		drawTimeString(1,canvas);
	}
	
	private float getTimeStringWidth(int type) {
		float ret=0;
		LockImage img[];
		if(type==0)
		{
			img=timeimg;
		}else
		{
			img=dateimg;
			ret+=img[0].getWidth();
		}
		for(int i=0;i<img.length;i++)
		{
			ret+=img[i].getWidth();
		}
		return ret;
	}
	private float getTimeStringHeight(int type)
	{
		float ret=0;
		if(type==0)
		{
			ret=timeimg[0].getHeight();
		}
		else
		{
			ret=dateimg[dateimg.length-2].getHeight();
		}
		return ret;
	}
	private void drawTimeString(int type, Canvas c) {
		LockImage img[];
		boolean en=false;
		if(type==0)
		{
			img=timeimg;
		}else{
			img=dateimg;
			String loc = Locale.getDefault().getLanguage();
			if (!loc.equals("zh")) {
				en=true;
			}
		}
		if(en)
		{
			float y=dateimg[0].getLocation().y;
			
			Bitmap month=en_monthimages[en_month];
			Bitmap week = en_weekimages[en_week];
			Bitmap day1=en_dayimages[en_day%10];
			Bitmap day2=en_dayimages[en_day/10];

			float x=(ViewW-month.getWidth()-week.getWidth()-day1.getWidth()*2)/2;
			float x1=x;
			
			c.drawBitmap(week , x1,
					y, ObjectGetter.getBitmapPaint());
			x1+=week.getWidth();
			
			c.drawBitmap(month , x1,
					y, ObjectGetter.getBitmapPaint());
			x1+=month.getWidth();
			
			if(day2!=null)
			{
				c.drawBitmap(day2 , x1,
						y, ObjectGetter.getBitmapPaint());
			}
			x1+=day1.getWidth();
			
			c.drawBitmap(day1 , x1,
					y, ObjectGetter.getBitmapPaint());
			
			return ;
		}
		for(int i=0;i<img.length;i++)
		{
			img[i].onDraw(c);
		}
		
	}
		
}
