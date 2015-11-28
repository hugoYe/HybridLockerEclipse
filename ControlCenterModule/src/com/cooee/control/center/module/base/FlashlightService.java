package com.cooee.control.center.module.base;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.IBinder;

public class FlashlightService extends Service {

	private boolean releasing;
	private Camera mCamera;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getBooleanExtra("open_flash", false)) {
			// When switching on immediately after checking for isAvailable,
			// the release method may still be running, so wait a bit.
			try {
				while (releasing) {
					Thread.sleep(10);
				}
				mCamera = Camera.open();
				if (Build.VERSION.SDK_INT >= 11) { // honeycomb
					// required for (at least) the Nexus 5
					mCamera.setPreviewTexture(new SurfaceTexture(0));
				}
				toggleTorch(true);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			toggleTorch(false);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private boolean isCapable() {
		PackageManager packageManager = null;
		packageManager = getPackageManager();
		for (final FeatureInfo feature : packageManager
				.getSystemAvailableFeatures()) {
			if (PackageManager.FEATURE_CAMERA_FLASH
					.equalsIgnoreCase(feature.name)) {
				return true;
			}
		}
		return false;
	}

	private void toggleTorch(boolean switchOn) {
		final Camera.Parameters mParameters = mCamera.getParameters();
		if (isCapable()) {
			mParameters
					.setFlashMode(switchOn ? Camera.Parameters.FLASH_MODE_TORCH
							: Camera.Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(mParameters);
			mCamera.startPreview();
		}
	}

	private void releaseCamera() {
		releasing = true;
		// we need to release the camera, so other apps can use it
		new Thread(new Runnable() {
			public void run() {
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				mCamera.release();
				releasing = false;
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		toggleTorch(false);
		releaseCamera();
	}
}
