package com.coco.lock.test;

import com.coco.lock.favorites.FavoritesService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FavoritesActivity extends Activity {
	FavoritesView mFavoritesView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mFavoritesView = new FavoritesView(this);
		setContentView(mFavoritesView);
		startService(new Intent(this, FavoritesService.class));
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
}
