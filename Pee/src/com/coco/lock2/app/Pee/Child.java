package com.coco.lock2.app.Pee;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;


public class Child extends Base {
	
	
	private final static String TAG="Child";
	private final static int space1=18;
	private final static int space2=14;
	private int childstate=0;
	private int new_level=0;
	private int old_level=0;
	private int refreshstate=0;
	private final static int DELAYTIME=1000/17;
	private int ViewW=0;
	private int ViewH=0;
	
	private Bitmap childImage[];
	private Bitmap peeingImage[];
	
	private float child_X=0,child_Y=0;
	private Fire fire;
	private Note note;
	private final static int[] childImageId={
		R.drawable.pen001,
		R.drawable.pen002,
		R.drawable.pen003,
		R.drawable.pen004,
		R.drawable.pen005,
		R.drawable.pen006
	};
	private final static int[] childpeeingID={
		//R.drawable.peeing001,
		R.drawable.peeing002,
		//R.drawable.peeing003,
		R.drawable.peeing004,
		//R.drawable.peeing005,
		R.drawable.peeing006,
		//R.drawable.peeing007,
		R.drawable.peeing008,
		//R.drawable.peeing009,
		R.drawable.peeing010,
		//R.drawable.peeing011,
		R.drawable.peeing012,
		//R.drawable.peeing013,
		R.drawable.peeing014,
		//R.drawable.peeing015,
		R.drawable.peeing016,
		//R.drawable.peeing017,
		R.drawable.peeing018,
		//R.drawable.peeing019,
		R.drawable.peeing020,
		R.drawable.peeing021,
		R.drawable.peeing022,
		R.drawable.peeing023,
		R.drawable.peeing024,
		R.drawable.peeing025,
		R.drawable.peeing026,
		R.drawable.peeing027,
		R.drawable.peeing028,
		R.drawable.peeing029,
		R.drawable.peeing030
	};
	private int getLevel()
	{
		if(refreshstate==0)
		{
			childstate=0;
			return 0;
		}
		return refreshstate/(childpeeingID.length/Prompt.LEVEL)+1;
	}
	private Runnable Refresh_handler= new Runnable(){
		public void run() {
			if(childstate<0)
			{
				
			}else if(childstate==0)
				refreshstate=(++refreshstate)%childImageId.length;
			
			else if(new_level==old_level)
			{
					refreshstate=childpeeingID.length*(new_level-1)/Prompt.LEVEL+(++refreshstate)%(childpeeingID.length/Prompt.LEVEL);
			}else if(new_level<old_level){
				if(refreshstate>0)
				--refreshstate;
				old_level=getLevel();
				if(old_level<=2)
				{
					childstate=0;
					refreshstate=0;
				}
			}else if(new_level>old_level)
			{
				refreshstate=(++refreshstate)%childpeeingID.length;
				old_level=getLevel();
			}
			if(refreshstate<0)
			{
				refreshstate=0;
			}
			Log.d("TAG" ," Refresh_handler new_level ="+new_level+"old_level ="+old_level
					+"\nrefreshstate ="+refreshstate+",childstate="+childstate);
			if(old_level>=3)
			{
				note.setShowflag(true);
				fire.setFirestate(1);
			}
			else
			{
				note.setShowflag(false);
				fire.setFirestate(0);
			}
			startThread();
		}
		
	};
	public Child(){
		super();
		setRefresh(Refresh_handler,DELAYTIME);
	
		childImage=ObjectGetter.getBitmaps(childImageId);
		peeingImage=new Bitmap[childpeeingID.length];
		loadimage(childpeeingID.length-1);
	} 

	public Child(Fire fire2,Note note) {
		this();
		this.fire=fire2;
		this.note=note;
	}

	@Override
	public void InitUI(int w, int h) {
		// TODO Auto-generated method stub
		super.InitUI(w, h);
		ViewW=w;
		ViewH=h;
		child_X=space1*w/480;
		child_Y=h-space2*h/800;

	}
	@Override
	public void onDraw(Canvas canvas) {
		Bitmap image;
		if(childstate<=0)
		{
			image=childImage[refreshstate%childImage.length];
		}
		else
		{
			int i=refreshstate%peeingImage.length;
			if(peeingImage[i]==null)
				loadimage(i);
			image=peeingImage[i];
		}
		canvas.drawBitmap(image, child_X,
				child_Y-image.getHeight(), ObjectGetter.getBitmapPaint());
	
	}
	
	public int getChildstate() {
		return childstate;
	}
	public void setChildstate(int childstate1) {
		if(this.old_level==5)
		{
			return ;
		}
		if(childstate1==-1)
		{
			this.childstate=-1;
			return ;
		}
		if(this.childstate==-1)
		{
			this.childstate=0;
		}
		
		this.new_level = childstate1;
		if(childstate1==5)
			this.old_level=childstate1;
		
		if(childstate==0)
			refreshstate=0;
		if(childstate1!=0)
			this.childstate=1;
		Log.d("TAG" ," setChildstate new_level ="+new_level+"old_level ="+old_level);
			
	}
	public float getFire_X(){
		float ret=0;
		ret=(float) (child_X+peeingImage[childpeeingID.length-1].getWidth()*0.94);
		return ret;
	}
	@Override
	public void delayHandle() {
		// TODO Auto-generated method stub
		//peeingImage=ObjectGetter.getBitmaps(childpeeingID);
		for(int i=0;i<childpeeingID.length;i++)
		{
			if(peeingImage[i]==null)
				loadimage(i);
		}
	}
	
	public synchronized void loadimage(int i){
		 
			peeingImage[i]=ObjectGetter.getBitmap(childpeeingID[i]);
		
		
	}
	private void clearinfo(){
		if(!fire.closeflag)
		{
			childstate=0;
			new_level=0;
			old_level=0;
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
