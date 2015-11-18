/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidInput;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxNativesLoader;

/**
 * An implementation of the {@link Application} interface for Android. Create an
 * {@link Activity} that derives from this class. In the
 * {@link Activity#onCreate(Bundle)} method call the
 * {@link #initialize(ApplicationListener, boolean)} method specifying the
 * configuration for the GLSurfaceView.
 * 
 * @author mzechner
 */
public class AndroidApplication implements Application {
	static {
		GdxNativesLoader.load();
	}
	public Context mContext;
	public static volatile boolean swapSurface = true;
	protected AndroidGraphics graphics;
	protected AndroidInput input;
	protected AndroidAudio audio;
	protected AndroidFiles files;
	protected ApplicationListener listener;
	protected Handler handler;
	protected boolean firstResume = true;
	protected final Array<Runnable> runnables = new Array<Runnable>();
	protected final Array<Runnable> executedRunnables = new Array<Runnable>();
	protected final Array<Runnable> urgentRunnables = new Array<Runnable>();
	protected final Array<Runnable> executedUrgentRunnables = new Array<Runnable>();
	protected WakeLock wakeLock = null;
	protected int logLevel = LOG_INFO;
//	private boolean mOut = false;
//	private S3Wrap mWrap = null;
	public AndroidApplication(Context context) {
		mContext = context;
//		mWrap = wrap;
	}
	
	
	/**
	 * This method has to be called in the {@link Activity#onCreate(Bundle)}
	 * method. It sets up all the things necessary to get input, render via
	 * OpenGL and so on. If useGL20IfAvailable is set the AndroidApplication
	 * will try to create an OpenGL ES 2.0 context which can then be used via
	 * {@link Graphics#getGL20()}. The {@link GL10} and {@link GL11} interfaces
	 * should not be used when OpenGL ES 2.0 is enabled. To query whether
	 * enabling OpenGL ES 2.0 was successful use the
	 * {@link Graphics#isGL20Available()} method. Uses a default
	 * {@link AndroidApplicationConfiguration}.
	 * 
	 * @param listener
	 *            the {@link ApplicationListener} implementing the program logic
	 * @param useGL2IfAvailable
	 *            whether to use OpenGL ES 2.0 if its available.
	 */
	public void initialize(ApplicationListener listener,
			boolean useGL2IfAvailable) {
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL20 = useGL2IfAvailable;
		initialize(listener, config);
	}

	/**
	 * This method has to be called in the {@link Activity#onCreate(Bundle)}
	 * method. It sets up all the things necessary to get input, render via
	 * OpenGL and so on. If config.useGL20 is set the AndroidApplication will
	 * try to create an OpenGL ES 2.0 context which can then be used via
	 * {@link Graphics#getGL20()}. The {@link GL10} and {@link GL11} interfaces
	 * should not be used when OpenGL ES 2.0 is enabled. To query whether
	 * enabling OpenGL ES 2.0 was successful use the
	 * {@link Graphics#isGL20Available()} method. You can configure other
	 * aspects of the application with the rest of the fields in the
	 * {@link AndroidApplicationConfiguration} instance.
	 * 
	 * @param listener
	 *            the {@link ApplicationListener} implementing the program logic
	 * @param config
	 *            the {@link AndroidApplicationConfiguration}, defining various
	 *            settings of the application (use accelerometer, etc.).
	 */
	public void initialize(ApplicationListener listener,
			AndroidApplicationConfiguration config) {
		graphics = new AndroidGraphics(
				this,mContext,
				config,
				config.resolutionStrategy == null ? new FillResolutionStrategy()
						: config.resolutionStrategy);

//		cfg = AppConfig.getInstance(mContext);
		input = new AndroidInput(this, mContext,graphics.view, config);
		audio = new AndroidAudio(mContext);
		files = new AndroidFiles(mContext.getAssets(), "");
		this.listener = listener;
		this.handler = new Handler();

		Gdx.app = this;
		Gdx.input = this.getInput();
		Gdx.audio = this.getAudio();
		Gdx.files = this.getFiles();
		Gdx.graphics = this.getGraphics();

//		try {
//			requestWindowFeature(Window.FEATURE_NO_TITLE);
//		} catch (Exception ex) {
//			log("AndroidApplication",
//					"Content already displayed, cannot request FEATURE_NO_TITLE",
//					ex);
//		}
//		getWindow().setFlags(
//				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//		// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//		mContext.setContentView(graphics.getView(), createLayoutParams());
		createWakeLock(config);
	}

	protected FrameLayout.LayoutParams createLayoutParams() {
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		layoutParams.gravity = Gravity.CENTER;
		return layoutParams;
	}

	protected void createWakeLock(AndroidApplicationConfiguration config) {
		if (config.useWakelock) {
			PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
					"libgdx wakelock");
		}
	}

	/**
	 * This method has to be called in the {@link Activity#onCreate(Bundle)}
	 * method. It sets up all the things necessary to get input, render via
	 * OpenGL and so on. If useGL20IfAvailable is set the AndroidApplication
	 * will try to create an OpenGL ES 2.0 context which can then be used via
	 * {@link Graphics#getGL20()}. The {@link GL10} and {@link GL11} interfaces
	 * should not be used when OpenGL ES 2.0 is enabled. To query whether
	 * enabling OpenGL ES 2.0 was successful use the
	 * {@link Graphics#isGL20Available()} method. Uses a default
	 * {@link AndroidApplicationConfiguration}.
	 * <p/>
	 * Note: you have to add the returned view to your layout!
	 * 
	 * @param listener
	 *            the {@link ApplicationListener} implementing the program logic
	 * @param useGL2IfAvailable
	 *            whether to use OpenGL ES 2.0 if its available.
	 * @return the GLSurfaceView of the application
	 */
	public View initializeForView(ApplicationListener listener,
			boolean useGL2IfAvailable) {
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL20 = useGL2IfAvailable;
		return initializeForView(listener, config);
	}

	/**
	 * This method has to be called in the {@link Activity#onCreate(Bundle)}
	 * method. It sets up all the things necessary to get input, render via
	 * OpenGL and so on. If config.useGL20 is set the AndroidApplication will
	 * try to create an OpenGL ES 2.0 context which can then be used via
	 * {@link Graphics#getGL20()}. The {@link GL10} and {@link GL11} interfaces
	 * should not be used when OpenGL ES 2.0 is enabled. To query whether
	 * enabling OpenGL ES 2.0 was successful use the
	 * {@link Graphics#isGL20Available()} method. You can configure other
	 * aspects of the application with the rest of the fields in the
	 * {@link AndroidApplicationConfiguration} instance.
	 * <p/>
	 * Note: you have to add the returned view to your layout!
	 * 
	 * @param listener
	 *            the {@link ApplicationListener} implementing the program logic
	 * @param config
	 *            the {@link AndroidApplicationConfiguration}, defining various
	 *            settings of the application (use accelerometer, etc.).
	 * @return the GLSurfaceView of the application
	 */
	public View initializeForView(ApplicationListener listener,
			AndroidApplicationConfiguration config) {
		graphics = new AndroidGraphics(
				this,mContext,
				config,
				config.resolutionStrategy == null ? new FillResolutionStrategy()
						: config.resolutionStrategy);
		input = new AndroidInput(this, mContext,graphics.view, config);
		audio = new AndroidAudio(mContext);
		files = new AndroidFiles(mContext.getAssets(), "");
		this.listener = listener;
		this.handler = new Handler();

		Gdx.app = this;
		Gdx.input = this.getInput();
		Gdx.audio = this.getAudio();
		Gdx.files = this.getFiles();
		Gdx.graphics = this.getGraphics();

		createWakeLock(config);
		return graphics.getView();
	}

	//@Override
	public void onPause() {
		
		if (wakeLock != null) wakeLock.release();
		boolean isContinuous = graphics.isContinuousRendering();
		graphics.setContinuousRendering(true);
		Log.v("AndroidGraphics","app onPause0");
		graphics.pause();
		Log.v("AndroidGraphics","app onPause11");
		input.unregisterSensorListeners();
		// erase pointer ids. this sucks donkeyballs...
		int[] realId = input.realId;
		for (int i = 0; i < realId.length; i++)
			realId[i] = -1;
		Log.v("AndroidGraphics","app onPause2");
		graphics.setContinuousRendering(isContinuous);

	//	if (graphics != null && graphics.view != null && mWrap.isViewFinishing()) {
//		if (graphics != null && graphics.view != null && S3Wrap.isViewFinishing()) {
//			Log.v("AndroidGraphics","app onPause3");
//			if (graphics.view instanceof GLSurfaceViewCupcake) ((GLSurfaceViewCupcake)graphics.view).onPause();
//			if (graphics.view instanceof android.opengl.GLSurfaceView) ((android.opengl.GLSurfaceView)graphics.view).onPause();
//		}
	//	super.onPause();
		Log.v("iLoongLauncher","app onPause4");
	}

	//@Override
	public void onResume() {
		Log.v("AndroidGraphics","app onResume1");
		if (wakeLock != null)
			wakeLock.acquire();
		Gdx.app = this;
		Log.v("AndroidGraphics","app onResume2");
		Gdx.input = this.getInput();
		Log.v("AndroidGraphics","app onResume3");
		Gdx.audio = this.getAudio();
		Log.v("AndroidGraphics","app onResume4");
		Gdx.files = this.getFiles();
		Log.v("AndroidGraphics","app onResume5");
		Gdx.graphics = this.getGraphics();
		Log.v("AndroidGraphics","app onResume6");
		((AndroidInput) getInput()).registerSensorListeners();
		Log.v("AndroidGraphics","app onResume7");
		if (audio != null) audio.resume();
		Log.v("AndroidGraphics","app onResume8");
//		if (graphics != null && graphics.view != null) {
//			if (graphics.view instanceof GLSurfaceViewCupcake) ((GLSurfaceViewCupcake)graphics.view).onResume();
//			if (graphics.view instanceof android.opengl.GLSurfaceView) ((android.opengl.GLSurfaceView)graphics.view).onResume();
//		}

//		if (!firstResume) {
			Log.v("AndroidGraphics","app onResume9");
			graphics.resume();
			Log.v("AndroidGraphics","app onResume10");
//		}
//		else
//			firstResume = false;
			
	//	super.onResume();
		
	}

//	@Override
	public void onDestroy() {
	//	super.onDestroy();
	//	if (ldestroy) {
//			Log.v("AndroidGraphics", "p");
//			listener.dispose();
//			if(audio!=null){
//				audio.dispose();
//				audio = null;
//			}
//			Gdx.app.log("AndroidGraphics", "destroyed");
	//	}
		if (true) {
			graphics.clearManagedCaches();
			listener.dispose();
			if(audio!=null){
				audio.dispose();
				audio = null;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Audio getAudio() {
		return audio;
	}

	/** {@inheritDoc} */
	@Override
	public Files getFiles() {
		return files;
	}

	/** {@inheritDoc} */
	@Override
	public Graphics getGraphics() {
		return graphics;
	}

	/** {@inheritDoc} */
	@Override
	public Input getInput() {
		return input;
	}

	/** {@inheritDoc} */
	@Override
	public ApplicationType getType() {
		return ApplicationType.Android;
	}

	/** {@inheritDoc} */
	@Override
	public int getVersion() {
		return Integer.parseInt(android.os.Build.VERSION.SDK);
	}

	@Override
	public long getJavaHeap() {
		return Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
	}

	@Override
	public long getNativeHeap() {
		return Debug.getNativeHeapAllocatedSize();
	}

	@Override
	public Preferences getPreferences(String name) {
		return new AndroidPreferences(mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE));
	}

	@Override
	public void postRunnable (Runnable runnable) {
		//Log.e("anr", "post 1");
		synchronized (runnables) {
			//Log.e("anr", "post 2");
			runnables.add(runnable);
			//Log.e("anr", "post 3");
		}
		Gdx.graphics.requestRendering();
	}
	
	public void postUrgentRunnable (Runnable runnable) {
		//Log.e("anr", "post 1");
		synchronized (urgentRunnables) {
			//Log.e("anr", "post 2");
			urgentRunnables.add(runnable);
			//Log.e("anr", "post 3");
		}
		Log.v("AndroidGraphics", "post 1");
		Gdx.graphics.requestRendering();
		Log.v("AndroidGraphics", "post 2");
	}

//	@Override
	public void onConfigurationChanged(Configuration config) {
//		super.onConfigurationChanged(config);
//		boolean keyboardAvailable = false;
//		if (config.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO)
//			keyboardAvailable = true;
//		input.keyboardAvailable = keyboardAvailable;
		input.keyboardAvailable = false;
	}

	@Override
	public void exit () {
//		handler.post(new Runnable() {
//			@Override
//			public void run () {
//			//	AndroidApplication.this.finish();
//			}
//		});
//		onDestroy();
	}

	@Override
	public void debug(String tag, String message) {
		if (logLevel >= LOG_DEBUG) {
			System.out.println(tag + ": " + message);
		}
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		if (logLevel >= LOG_DEBUG) {
			System.out.println(tag + ": " + message);
			exception.printStackTrace(System.out);
		}
	}

	@Override
	public void log(String tag, String message) {
		if (logLevel >= LOG_INFO)
			Log.i(tag, message);
	}

	@Override
	public void log(String tag, String message, Exception exception) {
		if (logLevel >= LOG_INFO)
			Log.i(tag, message, exception);
	}

	@Override
	public void error(String tag, String message) {
		if (logLevel >= LOG_ERROR)
			Log.e(tag, message);
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		if (logLevel >= LOG_ERROR)
			Log.e(tag, message, exception);
	}

	@Override
	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}
}
