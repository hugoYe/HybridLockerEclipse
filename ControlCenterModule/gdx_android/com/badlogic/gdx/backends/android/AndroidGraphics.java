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

import java.lang.reflect.Method;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.opengl.GLSurfaceView.Renderer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.android.surfaceview.DefaultGLSurfaceView;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake;
import com.badlogic.gdx.backends.android.surfaceview.GdxEglConfigChooser;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.GLU;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.WindowedMean;

/** An implementation of {@link Graphics} for Android.
 * 
 * @author mzechner */

public final class AndroidGraphics implements Graphics, Renderer {
	final View view;
	int width;
	int height;
	AndroidApplication app;
	GLCommon gl;
	GL10 gl10;
	GL11 gl11;
	GL20 gl20;
	GLU glu;
	EGLContext eglContext;
	String extensions;

	private long lastFrameTime = System.nanoTime();
	private float deltaTime = 0;
	private long frameStart = System.nanoTime();
	private int frames = 0;
	private int fps;
	private WindowedMean mean = new WindowedMean(5);

	volatile boolean created = false;
	volatile boolean running = false;
	volatile boolean pause = false;
	volatile boolean resume = false;
	volatile boolean destroy = false;

	public static volatile boolean launcherPause = false;
	private float ppiX = 0;
	private float ppiY = 0;
	private float ppcX = 0;
	private float ppcY = 0;
	private float density = 1;

	private final AndroidApplicationConfiguration config;
	private BufferFormat bufferFormat = new BufferFormat(5, 6, 5, 0, 16, 0, 0, false);
	private boolean isContinuous = true;
	private boolean isSurfaceCreated = false;
	private Context mContext;
	public AndroidGraphics (AndroidApplication activity, Context context,AndroidApplicationConfiguration config,
		ResolutionStrategy resolutionStrategy) {
		this.config = config;
		mContext = context;
		view = createGLSurfaceView(mContext, config.useGL20, resolutionStrategy);
		setPreserveContext(view);
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		this.app = activity;
	}

	private void setPreserveContext (View view) {
		int sdkVersion = Integer.parseInt(android.os.Build.VERSION.SDK);
		if (sdkVersion >= 11 && view instanceof GLSurfaceView20) {
			try {
				Method method = null;
				for(Method m: view.getClass().getMethods()) {
					if(m.getName().equals("setPreserveEGLContextOnPause")) {
						method = m;
						break;
					}
				}
				if(method != null) {
					method.invoke((GLSurfaceView20)view, true);
				}
			} catch (Exception e) {
			}
		}
	}

	private View createGLSurfaceView (  Context activity , boolean useGL2, final ResolutionStrategy resolutionStrategy) {
		EGLConfigChooser configChooser = getEglConfigChooser();
		Log.v("AndroidGraphics", " createGLSurfaceView ");
		if (useGL2 && checkGL20()) {
			Log.v("AndroidGraphics", "new GLSurfaceView20 ");
//			GLSurfaceView20 view = new GLSurfaceView20(activity, resolutionStrategy);
			GLSurfaceView20 view = new GLSurfaceView20(activity, true,16,0,resolutionStrategy){

				@Override
				public void onWindowFocusChanged (boolean hasWindowFocus) {
					// TODO Auto-generated method stub
					super.onWindowFocusChanged(hasWindowFocus);
					Log.v("AndroidGraphics", "onWindowFocusChanged  "+hasWindowFocus);
					 if(hasWindowFocus){
							Log.v("AndroidGraphics", "intent.statusbar.update onWindowFocusChanged true");
							Intent intent = new Intent("intent.statusbar.update");
							intent.putExtra("cooeelock", true);
							mContext.sendBroadcast(intent);
				    }
				    else {
							Log.v("AndroidGraphics", "intent.statusbar.update onWindowFocusChanged false");
							Intent intent = new Intent("intent.statusbar.update");
							intent.putExtra("cooeelock", false);
							mContext.sendBroadcast(intent);
				    }
				}

				@Override
				public void surfaceDestroyed (SurfaceHolder holder) {
					RuntimeException e = new RuntimeException("leon is here");
					e.fillInStackTrace();
					Log.v("AndroidGraphics", "surfaceDestroyed", e);
					super.surfaceDestroyed(holder);
				}

				@Override
				public void surfaceChanged (SurfaceHolder holder, int format, int w, int h) {
					RuntimeException e = new RuntimeException("leon is here");
					e.fillInStackTrace();
					Log.v("AndroidGraphics", "surfaceChanged", e);
					super.surfaceChanged(holder, format, w, h);
				}
			};
			if (configChooser != null)
				view.setEGLConfigChooser(configChooser);
			else
				view.setEGLConfigChooser(config.r, config.g, config.b, config.a, config.depth, config.stencil);
			view.setRenderer(this);
			view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			return view;
		} else {
			config.useGL20 = false;
			configChooser = getEglConfigChooser();
			int sdkVersion = Integer.parseInt(android.os.Build.VERSION.SDK);
			
			if(sdkVersion >= 11) {
				GLSurfaceView view = new GLSurfaceView(activity) {
					@Override
					protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
						ResolutionStrategy.MeasuredDimension measures = resolutionStrategy.calcMeasures(widthMeasureSpec, heightMeasureSpec);
						setMeasuredDimension(measures.width, measures.height);
					}
				};
				if (configChooser != null)
					view.setEGLConfigChooser(configChooser);
				else
					view.setEGLConfigChooser(config.r, config.g, config.b, config.a, config.depth, config.stencil);
				view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
				view.setRenderer(this);
				return view;
			} else {
/*				GLSurfaceViewCupcake view = new GLSurfaceViewCupcake(activity, resolutionStrategy);
				if (configChooser != null)
					view.setEGLConfigChooser(configChooser);
				else
					view.setEGLConfigChooser(config.r, config.g, config.b, config.a, config.depth, config.stencil);
				view.setRenderer(this);*/
				
                android.opengl.GLSurfaceView view = new DefaultGLSurfaceView(activity, resolutionStrategy);
                view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                if (configChooser != null) view.setEGLConfigChooser(configChooser);
                view.setRenderer(this);
				return view;
			}
		}
	}

	private EGLConfigChooser getEglConfigChooser () {
		return new GdxEglConfigChooser(config.r, config.g, config.b, config.a, config.depth, config.stencil, config.numSamples,
			config.useGL20);
	}
	private void updatePpi() {
		// DisplayMetrics dm = new DisplayMetrics();
		// mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
	
		ppiX = dm.xdpi;
		ppiY = dm.ydpi;
		ppcX = dm.xdpi / 2.54f;
		ppcY = dm.ydpi / 2.54f;
		density = dm.density;
	}

	private boolean checkGL20 () {
		EGL10 egl = (EGL10)EGLContext.getEGL();
		EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

		int[] version = new int[2];
		egl.eglInitialize(display, version);

		int EGL_OPENGL_ES2_BIT = 4;
		int[] configAttribs = {EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4, EGL10.EGL_RENDERABLE_TYPE,
			EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE};

		EGLConfig[] configs = new EGLConfig[10];
		int[] num_config = new int[1];
		egl.eglChooseConfig(display, configAttribs, configs, 10, num_config);
		egl.eglTerminate(display);
		return num_config[0] > 0;
	}

	/** {@inheritDoc} */
	@Override
	public GL10 getGL10 () {
		return gl10;
	}

	/** {@inheritDoc} */
	@Override
	public GL11 getGL11 () {
		return gl11;
	}

	/** {@inheritDoc} */
	@Override
	public GL20 getGL20 () {
		return gl20;
	}

	/** {@inheritDoc} */
	@Override
	public int getHeight () {
		return height;
	}

	/** {@inheritDoc} */
	@Override
	public int getWidth () {
		return width;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isGL11Available () {
		return gl11 != null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isGL20Available () {
		return gl20 != null;
	}

	private static boolean isPowerOfTwo (int value) {
		return ((value != 0) && (value & (value - 1)) == 0);
	}

	/** This instantiates the GL10, GL11 and GL20 instances. Includes the check for certain devices that pretend to support GL11 but
	 * fuck up vertex buffer objects. This includes the pixelflinger which segfaults when buffers are deleted as well as the
	 * Motorola CLIQ and the Samsung Behold II.
	 * 
	 * @param gl */
	private void setupGL (javax.microedition.khronos.opengles.GL10 gl) {
		if (gl10 != null || gl20 != null) return;

		if (view instanceof GLSurfaceView20) {
			gl20 = new AndroidGL20();
			this.gl = gl20;
		} else {
			gl10 = new AndroidGL10(gl);
			this.gl = gl10;
			if (gl instanceof javax.microedition.khronos.opengles.GL11) {
				String renderer = gl.glGetString(GL10.GL_RENDERER);
				if (renderer != null) { // silly GT-I7500
					if (!renderer.toLowerCase().contains("pixelflinger")
						&& !(android.os.Build.MODEL.equals("MB200") || android.os.Build.MODEL.equals("MB220") || android.os.Build.MODEL
							.contains("Behold"))) {
						gl11 = new AndroidGL11((javax.microedition.khronos.opengles.GL11)gl);
						gl10 = gl11;
					}
				}
			}
		}

		this.glu = new AndroidGLU();

		Gdx.gl = this.gl;
		Gdx.gl10 = gl10;
		Gdx.gl11 = gl11;
		Gdx.gl20 = gl20;
		Gdx.glu = glu;

		Gdx.app.log("AndroidGraphics", "OGL renderer: " + gl.glGetString(GL10.GL_RENDERER));
		Gdx.app.log("AndroidGraphics", "OGL vendor: " + gl.glGetString(GL10.GL_VENDOR));
		Gdx.app.log("AndroidGraphics", "OGL version: " + gl.glGetString(GL10.GL_VERSION));
		Gdx.app.log("AndroidGraphics", "OGL extensions: " + gl.glGetString(GL10.GL_EXTENSIONS));
	}

	@Override
	public void onSurfaceChanged (javax.microedition.khronos.opengles.GL10 gl, int width, int height) {
		Log.v("AndroidGraphics", "onSurfaceChanged_a created = "+created );
		this.width = width;
		this.height = height;
		updatePpi();
		gl.glViewport(0, 0, this.width, this.height);
		if (created == false) {
			Log.v("AndroidGraphics", "onSurfaceChanged_b");
			app.listener.create();
			Log.v("AndroidGraphics", "onSurfaceChanged_c");
			created = true;
			synchronized (this) {
				running = true;
			}
		}
		Log.v("AndroidGraphics", "onSurfaceChanged_d");
		app.listener.resize(width, height);
		Log.v("AndroidGraphics", "onSurfaceChanged_e");
	}

	@Override
	public void onSurfaceCreated (javax.microedition.khronos.opengles.GL10 gl, EGLConfig config) {
		eglContext = ((EGL10)EGLContext.getEGL()).eglGetCurrentContext();
		setupGL(gl);
		logConfig(config);
		updatePpi();
		
		Mesh.invalidateAllMeshes(app);
		Texture.invalidateAllTextures(app);
		ShaderProgram.invalidateAllShaderPrograms(app);
		FrameBuffer.invalidateAllFrameBuffers(app);

		Gdx.app.log("AndroidGraphics", Mesh.getManagedStatus());
		Gdx.app.log("AndroidGraphics", Texture.getManagedStatus());
		Gdx.app.log("AndroidGraphics", ShaderProgram.getManagedStatus());
		Gdx.app.log("AndroidGraphics", FrameBuffer.getManagedStatus());

//		Display display = app.mContext.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		this.width = dm.widthPixels; 
		this.height = dm.heightPixels; 
		mean = new WindowedMean(5);
		this.lastFrameTime = System.nanoTime();

		gl.glViewport(0, 0, this.width, this.height);
		Log.v("AndroidGraphics", "isSurfaceCreated a = "+isSurfaceCreated);
		isSurfaceCreated = true;
		Log.v("AndroidGraphics", "isSurfaceCreated b = "+isSurfaceCreated);
	}

	private void logConfig (EGLConfig config) {
		EGL10 egl = (EGL10)EGLContext.getEGL();
		EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		int r = getAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
		int g = getAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
		int b = getAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
		int a = getAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
		int d = getAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
		int s = getAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
		int samples = Math.max(getAttrib(egl, display, config, EGL10.EGL_SAMPLES, 0),
			getAttrib(egl, display, config, GdxEglConfigChooser.EGL_COVERAGE_SAMPLES_NV, 0));
		boolean coverageSample = getAttrib(egl, display, config, GdxEglConfigChooser.EGL_COVERAGE_SAMPLES_NV, 0) != 0;

		Gdx.app.log("AndroidGraphics", "framebuffer: (" + r + ", " + g + ", " + b + ", " + a + ")");
		Gdx.app.log("AndroidGraphics", "depthbuffer: (" + d + ")");
		Gdx.app.log("AndroidGraphics", "stencilbuffer: (" + s + ")");
		Gdx.app.log("AndroidGraphics", "samples: (" + samples + ")");
		Gdx.app.log("AndroidGraphics", "coverage sampling: (" + coverageSample + ")");

		bufferFormat = new BufferFormat(r, g, b, a, d, s, samples, coverageSample);
	}

	int[] value = new int[1];

	private int getAttrib (EGL10 egl, EGLDisplay display, EGLConfig config, int attrib, int defValue) {
		if (egl.eglGetConfigAttrib(display, config, attrib, value)) {
			return value[0];
		}
		return defValue;
	}

	Object synch = new Object();

	void resume () {
		Log.v("AndroidGraphics", "AndroidGraphics resume 1");
		launcherPause = false;
		pause = false;
		synchronized (synch) {
			Log.v("AndroidGraphics", "AndroidGraphics resume 2");
			running = true;
			resume = true;
			isSurfaceCreated = false;
			requestRendering();
		}
	}
	void pause (){
		Log.v("AndroidGraphics", "pause a");
		launcherPause = true;
		synchronized (synch) {
			Log.v("AndroidGraphics", "pause b");
			running = false;
			pause = true;
	//		requestRendering();
			if(!isSurfaceCreated){
				Log.v("AndroidGraphics", "pause c");
				return;
			}			
			if (pause) {
				Log.v("AndroidGraphics", "pause e");
				try {
					synch.wait(500);
				} catch (InterruptedException ignored) {
					Gdx.app.log("AndroidGraphics", "waiting for pause synchronization failed!");
				}
			}
		}
	}
	void destroy () {
		Log.v("AndroidGraphics", "destroy a");
		synchronized (synch) {
			Log.v("AndroidGraphics", "destroy b");
			running = false;
			destroy = true;
		//	requestRendering();
			if(!isSurfaceCreated) return;
			Log.v("AndroidGraphics", "destroy c");
			if (destroy) {
				Log.v("AndroidGraphics", "destroy d");
				try {
					synch.wait(500);
				} catch (InterruptedException ex) {
					Gdx.app.log("AndroidGraphics", "waiting for destroy synchronization failed!");
				}
			}
		}
	}
//	void destroy () {
//		Log.v("AndroidGraphics", "destroy a");
//		synchronized (synch) {
//			running = false;
//			destroy = true;
//			Log.v("AndroidGraphics", "destroy b");
//			if(!isSurfaceCreated) return;
//			while (destroy) {
//				try {
//					Log.v("AndroidGraphics", "destroy d");
//					synch.wait();
//					Log.v("AndroidGraphics", "destroy c");
//				} catch (InterruptedException ex) {
//					Gdx.app.log("AndroidGraphics", "waiting for destroy synchronization failed!");
//				}
//			}
//		}
//	}
	@Override
	public void onDrawFrame (javax.microedition.khronos.opengles.GL10 gl) {
		Log.v("bounce", "onDrawFrame");
		long time = System.nanoTime();
		deltaTime = (time - lastFrameTime) / 1000000000.0f;
		lastFrameTime = time;
		Log.v("AndroidGraphics", "a");
		mean.addValue(deltaTime);
		//Log.v("AndroidGraphics", "b");
		boolean lrunning = false;
		boolean lpause = false;
		boolean ldestroy = false;
		boolean lresume = false;
		boolean lforceRender = false;
		synchronized (synch) {
			lrunning = running;
			lpause = pause;
			ldestroy = destroy;
			lresume = resume;
			if (resume) {
				resume = false;
				//Log.v("AndroidGraphics", "d");
			}

			if (pause) {
				//Log.v("AndroidGraphics", "e");
				pause = false;
				synch.notifyAll();
			}

			if (destroy) {
				Log.v("AndroidGraphics", "f");
				destroy = false;
				synch.notifyAll();
			}
		}
		if (lresume) {
			//Log.v("AndroidGraphics", "g");
			if(((AndroidApplication)app).audio!=null)
			{
				((AndroidApplication)app).audio.resume();
			}
			app.listener.resume();
			Gdx.app.log("AndroidGraphics", "resumed");
		}
		synchronized (app.urgentRunnables) {
			Log.v("AndroidGraphics", "h");
			app.executedUrgentRunnables.clear();
			app.executedUrgentRunnables.addAll(app.urgentRunnables);
			app.urgentRunnables.clear();
		}	
		for (int i = 0; i < app.executedUrgentRunnables.size; i++) {
			try {
				Log.v("AndroidGraphics", "i");
				app.executedUrgentRunnables.get(i).run();
			}
			catch(Throwable t) {
				t.printStackTrace();
			}
		}
		Log.v("AndroidGraphics", "lrunning = "+lrunning);
		Log.v("AndroidGraphics", "lforceRender = "+lforceRender);
		if (lrunning || lforceRender) {
			synchronized (app.runnables) {
				Log.v("AndroidGraphics", "g");
				app.executedRunnables.clear();
				Log.v("AndroidGraphics", "g1");
				app.executedRunnables.addAll(app.runnables);
				Log.v("AndroidGraphics", "g2");
				app.runnables.clear();
				Log.v("AndroidGraphics", "g3");
			}	
			Log.v("AndroidGraphics", "g4");
				for (int i = 0; i < app.executedRunnables.size; i++) {
					try {
						Log.v("AndroidGraphics", "k");
						app.executedRunnables.get(i).run();
					}
					catch(Throwable t) {
						t.printStackTrace();
					}
				}
				Log.v("AndroidGraphics", "l");
			app.input.processEvents();
			Log.v("AndroidGraphics", "m");
			app.listener.render();
			Log.v("AndroidGraphics", "i");
		}

		if (lpause) {
			//Log.v("AndroidGraphics", "o");
			app.listener.pause();
			if(((AndroidApplication)app).audio!=null){
				((AndroidApplication)app).audio.pause();
			}
			
			Gdx.app.log("AndroidGraphics", "paused");
		}

		if (ldestroy) {
			Log.v("AndroidGraphics", "p");
			app.listener.dispose();
			if(((AndroidApplication)app).audio!=null){
				((AndroidApplication)app).audio.dispose();
				((AndroidApplication)app).audio = null;
			}
			Gdx.app.log("AndroidGraphics", "destroyed");
		}
		Log.v("AndroidGraphics", "q");
		if (time - frameStart > 1000000000) {
			fps = frames;
			frames = 0;
			frameStart = time;
		}
		frames++;
	}
	/** {@inheritDoc} */
	@Override
	public float getDeltaTime () {
		return mean.getMean() == 0 ? deltaTime : mean.getMean();
//		return mean.getMean() == 0 ? deltaTime : 0;
	}
	
	@Override
	public float getRawDeltaTime () {
		return deltaTime;
	}

	/** {@inheritDoc} */
	@Override
	public GraphicsType getType () {
		return GraphicsType.AndroidGL;
	}

	/** {@inheritDoc} */
	@Override
	public int getFramesPerSecond () {
		return fps;
	}

	public void clearManagedCaches () {
		Mesh.clearAllMeshes(app);
		Texture.clearAllTextures(app);
		ShaderProgram.clearAllShaderPrograms(app);
		FrameBuffer.clearAllFrameBuffers(app);

		Gdx.app.log("AndroidGraphics", Mesh.getManagedStatus());
		Gdx.app.log("AndroidGraphics", Texture.getManagedStatus());
		Gdx.app.log("AndroidGraphics", ShaderProgram.getManagedStatus());
		Gdx.app.log("AndroidGraphics", FrameBuffer.getManagedStatus());
	}

	public View getView () {
		return view;
	}

	/** {@inheritDoc} */
	@Override
	public GLCommon getGLCommon () {
		return gl;
	}

	@Override
	public float getPpiX () {
		return ppiX;
	}

	@Override
	public float getPpiY () {
		return ppiY;
	}

	@Override
	public float getPpcX () {
		return ppcX;
	}

	@Override
	public float getPpcY () {
		return ppcY;
	}

	@Override
	public float getDensity () {
		return density;
	}

	@Override
	public GLU getGLU () {
		return glu;
	}

	@Override
	public boolean supportsDisplayModeChange () {
		return false;
	}

	@Override
	public boolean setDisplayMode (DisplayMode displayMode) {
		return false;
	}

	@Override
	public DisplayMode[] getDisplayModes () {
		return new DisplayMode[] {getDesktopDisplayMode()};
	}

	@Override
	public boolean setDisplayMode (int width, int height, boolean fullscreen) {
		return false;
	}

	@Override
	public void setTitle (String title) {

	}

	@Override
	public void setIcon (Pixmap[] pixmap) {

	}

	private class AndroidDisplayMode extends DisplayMode {
		protected AndroidDisplayMode (int width, int height, int refreshRate, int bitsPerPixel) {
			super(width, height, refreshRate, bitsPerPixel);
		}
	}

	@Override
	public DisplayMode getDesktopDisplayMode () {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();

		return new AndroidDisplayMode(dm.widthPixels, dm.heightPixels, 0, 0);
	}

	@Override
	public BufferFormat getBufferFormat () {
		return bufferFormat;
	}

	@Override
	public void setVSync (boolean vsync) {
	}

	@Override
	public boolean supportsExtension (String extension) {
		if (extensions == null) extensions = Gdx.gl.glGetString(GL10.GL_EXTENSIONS);
		return extensions.contains(extension);
	}

	@Override
	public void setContinuousRendering (boolean isContinuous) {
		if(view != null) {
			this.isContinuous = isContinuous;
			int renderMode = isContinuous?GLSurfaceView.RENDERMODE_CONTINUOUSLY:GLSurfaceView.RENDERMODE_WHEN_DIRTY;
			if(view instanceof GLSurfaceViewCupcake) ((GLSurfaceViewCupcake)view).setRenderMode(renderMode);
			if(view instanceof GLSurfaceView) ((GLSurfaceView)view).setRenderMode(renderMode);
		}
	}
	
	public boolean isContinuousRendering() {
		return isContinuous;
	}

	@Override
	public void requestRendering () {
		if(view != null) {
			if(view instanceof GLSurfaceViewCupcake) ((GLSurfaceViewCupcake)view).requestRender();
			if(view instanceof GLSurfaceView) ((GLSurfaceView)view).requestRender();
		}
	}

}
