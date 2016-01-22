package com.coco.lock2.app.locktemplate;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Vibrator;

public class MediaHandle {

	public static boolean UnlockShock = false;
	private boolean isShake;
	private boolean isSound;
	public boolean isshacked = false;
	public Context mContext;
	private Handler mHandlerMain = new Handler();
	private Runnable mRunReleaseSoundPool = new Runnable() {
		public void run() {
			soundPool.release();
			mHandlerMain.removeCallbacks(mRunReleaseSoundPool);
		}
	};
	private int soundId = 0;
	private SoundPool soundPool;
	private Vibrator vibrator;

	public MediaHandle(Context paramContext) {
		mContext = paramContext;
		if (isSound) {
			soundPool = new SoundPool(10, 1, 5);
			soundId = soundPool.load(paramContext, 2131034112, 1);
		}
		if (isShake) {
			vibrator = ((Vibrator) paramContext.getSystemService("vibrator"));
		}
	}

	public void shakePlay(int paramInt) {
		if (isShake) {
			if ((paramInt == 2) && (UnlockShock) && !isshacked) {
				this.vibrator.vibrate(100L);
				isshacked = true;
			} else {
				this.vibrator.vibrate(100L);
			}
		}
	}

	public void soundPlay(int paramInt) {
		if (this.isSound) {
			int i = ((AudioManager) this.mContext.getSystemService("audio"))
					.getStreamVolume(2);
			this.soundPool.play(this.soundId, i / 7.0F, i / 7.0F, 1, 0, 1.0F);
			this.isSound = false;
			this.mHandlerMain.removeCallbacks(this.mRunReleaseSoundPool);
			this.mHandlerMain.postDelayed(this.mRunReleaseSoundPool, 2000L);
		}
	}
}
