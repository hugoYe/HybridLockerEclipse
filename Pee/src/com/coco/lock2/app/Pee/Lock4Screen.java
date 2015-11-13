package com.coco.lock2.app.Pee;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;


import com.coco.lock2.app.Pee.common.BaseView;
import com.cooee.statistics.StatisticsBaseNew;
import com.cooee.statistics.StatisticsExpandNew;

public class Lock4Screen extends BaseView {
	private static final String TAG = "Lock4ScreenPee";
	private Bitmap bgImg;
	private boolean playSounded=false;
	private  SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	private Vibrator vibrator;
	private Context mContext;
	private final static int DELAYTIME=100;
	private TimeShow time;
	private Child child;
	private Fire fire;
	private Prompt prompt;
	private Note note;
	private Matrix matrix=null;
	private boolean statusbarupdate = true;
	static private AppConfig mAppConfig;
	public Lock4Screen(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext=context;

		ObjectGetter.Init(context, this,mhandler);
		
		InitUI();
		if (mAppConfig == null) {
			mAppConfig = AppConfig.getInstance(context);
		}
		time=new TimeShow(context);
		fire =new Fire();
		note=new Note();
		child=new Child(fire,note);
		prompt=new Prompt(child);
		statusbarupdate = mAppConfig.statusbarupdate();	

	}
	
	Runnable mRunnable=new Runnable(){//刷新屏幕
	
		public void run() {
			Lock4Screen.this.mhandler.postDelayed(this, DELAYTIME);
			
			postInvalidate();
		}};
	public void vibrate(){
		if(ObjectGetter.set.isVibrateOpen())
		vibrator.vibrate(50);
	}
	private void InitUI(){
		vibrator = (Vibrator) mContext
				.getSystemService(Context.VIBRATOR_SERVICE);
//		soundPool = new SoundPool(10, AudioManager.STREAM_RING, 5);
//		soundPoolMap = new HashMap<Integer, Integer>();
//		soundPoolMap.put(1, soundPool.load(mcontext, R.raw.sound, 1));
		
	
		mhandler.postDelayed(mRunnable, DELAYTIME);
	
	}
	private void delayhandle()
	{
		new Thread(){

			@Override
			public void run() {
				super.run();
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					//child.delayHandle();
				
			}}.start();
	}
	private void InitUIInfo(int w,int h)
	{
		
		time.InitUI( w, h);
		child.InitUI(w,h);
		fire.InitUI(w,h);
		prompt.InitUI(w, h);
		note.InitUI(w, h);
		fire.setFire_x(child.getFire_X());
		
		//Bitmap bitbgs=null;
		//bitbgs.recycle();
		Bitmap bitbg=((BitmapDrawable)getResources().getDrawable(
				R.drawable.bg)).getBitmap();
		Log.d("song","InitUIInfo w="+w+",h="+h+",bitbg.getHeight()="+bitbg.getHeight()+",bitbg.getWidth()="+bitbg.getWidth());
		if(bitbg.getHeight()<h || bitbg.getWidth()<w)
		{
			matrix = new Matrix();   
		    matrix.postScale((float)(1.0*w/bitbg.getWidth()), (float) (1.0*h/bitbg.getHeight()));   
		    //bitbgs = Bitmap.createBitmap(bitbg, 0, 0,   
		    //bitbg.getWidth(), bitbg.getHeight(), matrix, true);   
		}
		//else
		//{
			//bitbgs=bitbg;
		//}
		bgImg=bitbg;
		//bgImg=new LockImage(bitbgs,true);
		//Point p=new Point();
		//p.x=0;
		//p.y=h-bgImg.getHeight();
		//bgImg.setLocation(p);
		//delayhandle();
	}
	
	private Handler mhandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==101)
			{
				vibrate();
			}
			if(msg.what==100)
			{
//				if(ObjectGetter.set.isSoundOpen() && !playSounded )
//				{
//					AudioManager mgr = (AudioManager) getContext()
//							.getSystemService(Context.AUDIO_SERVICE);
//					int streamVolume = mgr
//							.getStreamVolume(AudioManager.STREAM_RING);
//					soundPool.play(soundPoolMap.get(1), streamVolume,
//							streamVolume, 1, 0, 1f);
//					playSounded=true;
//				}
				exitLock();
			}
		}
		
	};
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
		if(!statusbarupdate)return;
		Log.v(TAG, "onWindowFocusChanged  " + hasWindowFocus);
		if (hasWindowFocus) {
			Log.v(TAG,
					"intent.statusbar.update onWindowFocusChanged true");
			Intent intent = new Intent("intent.statusbar.update");
			intent.putExtra("cooeelock", true);
			mContext.sendBroadcast(intent);
		} else {
			Log.v(TAG,
					"intent.statusbar.update onWindowFocusChanged false");
			Intent intent = new Intent("intent.statusbar.update");
			intent.putExtra("cooeelock", false);
			mContext.sendBroadcast(intent);
		}
	}	
	@Override
	protected void onDraw(Canvas canvas) {
		//bgImg.onDraw(canvas);
		//canvas.draw
		if(matrix!=null)
			canvas.drawBitmap(bgImg, matrix,ObjectGetter.getBitmapPaint());
		else
		canvas.drawBitmap(bgImg,0,
				getHeight()-bgImg.getHeight(),ObjectGetter.getBitmapPaint());
		super.onDraw(canvas);
		time.onDraw(canvas);
		child.onDraw(canvas);
		fire.onDraw(canvas);
		prompt.onDraw(canvas);
		note.onDraw(canvas);
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		Log.d("song","onTouchEvent event.getAction()="+event.getAction());
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
			if(wrap!=null)
			{
				wrap.resetLight();
			}
		}
		prompt.onTouchEvent(event);
		return true;
	}

	@Override
	public void onViewResume() {
		super.onViewResume();
		
		mhandler.removeCallbacks(mRunnable);
		mhandler.postDelayed(mRunnable, DELAYTIME);
		
		time.onResume();
		child.onResume();
		fire.onResume();
		prompt.onResume();
		note.onResume();
		
	}

	@Override
	public void onViewPause() {
		mhandler.removeCallbacks(mRunnable);
		time.onPause();
		child.onPause();
		fire.onPause();
		prompt.onPause();
		note.onPause();
		super.onViewPause();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		InitUIInfo(w,h);
	}

}
