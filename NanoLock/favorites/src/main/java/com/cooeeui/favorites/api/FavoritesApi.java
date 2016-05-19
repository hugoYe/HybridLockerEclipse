package com.cooeeui.favorites.api;

import android.content.Context;
import android.content.Intent;

import com.cooeeui.favorites.AppInfo;
import com.cooeeui.favorites.FavoritesData;
import com.cooeeui.favorites.FavoritesService;

import java.util.ArrayList;


public class FavoritesApi {

    private static String TAG = "FavoritesApi";

    private static FavoritesApi mInstance;


    private FavoritesApi() {

    }

    public static FavoritesApi getInstance() {
        if (mInstance == null) {
            mInstance = new FavoritesApi();
        }
        return mInstance;
    }

    public void init(Context context) {
        context.startService(new Intent(context, FavoritesService.class));
    }


    public ArrayList<AppInfo> getFavoriteApp() {
        return FavoritesData.getFavorityAppInfo(10);
    }
}
