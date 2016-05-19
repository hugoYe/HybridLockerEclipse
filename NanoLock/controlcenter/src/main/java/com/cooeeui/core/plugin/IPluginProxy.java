package com.cooeeui.core.plugin;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;

import org.json.JSONArray;

public interface IPluginProxy {

    /**
     * Executes the request.
     *
     * This method is called from the WebView thread. To do a non-trivial amount of work, use:
     * cordova.getThreadPool().execute(runnable);
     *
     * To run on the UI thread, use: cordova.getActivity().runOnUiThread(runnable);
     *
     * @param action The action to execute.
     * @param args   The exec() arguments.
     * @return -1:not handle,0:handle failed,1:handle success.
     */
    public int execute(String action, final JSONArray args);

    /**
     * Called when the system is about to start resuming a previous activity.
     *
     * @param multitasking Flag indicating if multitasking is turned on for app
     */
    public void onPause(Boolean multitasking);

    /**
     * Called when the activity will start interacting with the user.
     *
     * @param multitasking Flag indicating if multitasking is turned on for app
     */
    public void onResume(Boolean multitasking);

    /**
     * Called when the activity is becoming visible to the user.
     */
    public void onStart();

    /**
     * Called when the activity is no longer visible to the user.
     */
    public void onStop();

    /**
     * Called when the activity receives a new intent.
     */
    public void onNewIntent(Intent intent);

    /**
     * The final call you receive before your activity is destroyed.
     */
    public void onDestroy();

    public void onLauncherLoadFinish();

    public void onActivityResult(Integer requestCode, Integer resultCode,
                                 Intent intent);

    /**
     * Hook for blocking the loading of external resources.
     *
     * This will be called when the WebView's shouldInterceptRequest wants to know whether to open a
     * connection to an external resource. Return false to block the request: if any plugin returns
     * false, Cordova will block the request. If all plugins return null, the default policy will be
     * enforced. If at least one plugin returns true, and no plugins return false, then the request
     * will proceed.
     *
     * Note that this only affects resource requests which are routed through
     * WebViewClient.shouldInterceptRequest, such as XMLHttpRequest requests and img tag loads.
     * WebSockets and media requests (such as <video> and <audio> tags) are not affected by this
     * method. Use CSP headers to control access to such resources.
     */
    public Boolean shouldAllowRequest(String url);

    /**
     * Hook for blocking navigation by the Cordova WebView. This applies both to top-level and
     * iframe navigations.
     *
     * This will be called when the WebView's needs to know whether to navigate to a new page.
     * Return false to block the navigation: if any plugin returns false, Cordova will block the
     * navigation. If all plugins return null, the default policy will be enforced. It at least one
     * plugin returns true, and no plugins return false, then the navigation will proceed.
     */
    public Boolean shouldAllowNavigation(String url);

    /**
     * Hook for blocking the launching of Intents by the Cordova application.
     *
     * This will be called when the WebView will not navigate to a page, but could launch an intent
     * to handle the URL. Return false to block this: if any plugin returns false, Cordova will
     * block the navigation. If all plugins return null, the default policy will be enforced. If at
     * least one plugin returns true, and no plugins return false, then the URL will be opened.
     */
    public Boolean shouldOpenExternalUrl(String url);

    /**
     * Allows plugins to handle a link being clicked. Return true here to cancel the navigation.
     *
     * @param url The URL that is trying to be loaded in the Cordova webview.
     * @return Return true to prevent the URL from loading. Default is false.
     */
    public boolean onOverrideUrlLoading(String url);

    /**
     * Hook for redirecting requests. Applies to WebView requests as well as requests made by
     * plugins. To handle the request directly, return a URI in the form:
     *
     * cdvplugin://pluginId/...
     *
     * And implement handleOpenForRead(). To make this easier, use the toPluginUri() and
     * fromPluginUri() helpers:
     *
     * public Uri remapUri(Uri uri) { return toPluginUri(uri); }
     *
     * public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri uri) throws IOException {
     * Uri origUri = fromPluginUri(uri); ... }
     */
    public Uri remapUri(Uri uri);

    /**
     * Called by the system when the device configuration changes while your activity is running.
     *
     * @param newConfig The new device configuration
     */
    public void onConfigurationChanged(Configuration newConfig);

    public void onPageFinishedLoading();
}
