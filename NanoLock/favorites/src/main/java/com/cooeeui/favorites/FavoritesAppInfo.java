package com.cooeeui.favorites;


public class FavoritesAppInfo extends AppInfo {

    /**
     *
     */
    public static final int NO_ID = -1;
    /**
     * The id in the settings database for this item
     */
    public long id = NO_ID;
    public String packageName;
    /**
     * The times of the application launch.
     */
    public long launchTimes;
}
