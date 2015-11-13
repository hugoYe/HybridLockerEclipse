package com.iLoong.launcher.MList;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import cool.sdk.MicroEntry.MicroEntryHelper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

	private static boolean isNeedClearHistory = false;
	String reloadUrl = null;
	boolean flag = false;
	boolean flagError = false;
	JSClass mainJsClass = null;
	JSClass subJsCalss = null;
	WebView mainWebView = null;
	WebView subWebView = null;
	View MainFrameWebview = null;
	View SubFrameWebview = null;
	// static MyFloatView myFV;
	boolean isenable1 = true;
	boolean isenable2 = true;
	boolean isenable3 = true;
	boolean isenable4 = true;
	int app_id = 10009;
	public static MainActivity instance = null;
	byte[] UPath = { 80, 17, 86, -2, -9, 6, 110, -48, 88, 47, 124, -61, -2,
			104, 67, -56, -75, -81, -31, 26, 66, 34, -57, -92, 26, 23, -73, 71,
			5, 61, 24, -15, -72, 125, 110, -1, 6, -121, 77, -88, -21, 36, 85,
			62, 35, 5, -95, -31, -99, 10, 2, -64, -88, -33, 105, -31, -7, 39,
			26, 85, -54, 73, 35, -94 };
	public boolean isExit = false;
	public String url;

	public int getId() {
		return app_id;
	}

	private String strAction = null;
	MyR R;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MELOG.v("ME_RTFSC", "==== MainActivity  onCreate ====");
		// MeGeneralMethod.CanelKillProcess();
		instance = this;
		super.onCreate(savedInstanceState);
		R = MyR.getMyR(this);
		if (R == null) {
			finish();
			return;
		}
		Intent intent = getIntent();
		app_id = intent.getIntExtra("APP_ID", 1);
		strAction = intent.getStringExtra("Action");
		String strActionDescription = intent
				.getStringExtra("ActionDescription");
		setContentView(R.layout.cool_ml_activity_main);
		MainFrameWebview = findViewById(R.id.cool_ml_mainwebviewframe);
		mainWebView = (WebView) findViewById(R.id.cool_ml_webView1);
		SubFrameWebview = findViewById(R.id.cool_ml_subwebviewframe);
		subWebView = (WebView) findViewById(R.id.cool_ml_webView2);
		if (getId() >= 800 && getId() <= 808) {
			mainJsClass = new JSClass(mainWebView, "M", 0, getId(), mHandler,
					R, MeApkDLShowType.WebviewMain);
		} else {
			mainJsClass = new JSClass(mainWebView, "M", getId(), getId(),
					mHandler, R, MeApkDLShowType.WebviewMain);
		}
		MeApkDownloadManager.MeAddActiveCallBackMap.put(
				MeApkDLShowType.WebviewMain, new MeActiveCallback() {

					@Override
					public void NotifyUninstallApkAction(String pkgName) {
						// TODO Auto-generated method stub
						mainJsClass.appInstallInfoChange(
								getApplicationContext(), pkgName, 0);
						mainJsClass.invokeJSMethod("reloadDownstate", pkgName);
					}

					@Override
					public void NotifyInstallSucessAction(String pkgName) {
						// TODO Auto-generated method stub
						mainJsClass.appInstallInfoChange(
								getApplicationContext(), pkgName, 1);
						mainJsClass
								.invokeJSMethod("AppInstallSuccess", pkgName);
					}

					@Override
					public void NotifyDelAction(String pkgName) {
						// TODO Auto-generated method stub
						MELOG.v("ME_RTFSC",
								"WebviewMain MeActiveCallback NotifyDelAction");
						mainJsClass.invokeJSMethod("reloadDownstate", pkgName);
					}

					@Override
					public void NoifySatrtAction(String pkgName) {
						// TODO Auto-generated method stub
						mainJsClass.invokeJSMethod("reloadDownstate", pkgName);
					}
				});
		bindJsClass(mainJsClass, mainWebView);
		SubFrameWebview.setVisibility(View.INVISIBLE);
		if (getId() >= 800 && getId() <= 808) {
			subJsCalss = new JSClass(subWebView, "M", 0, getId(), mHandler, R,
					MeApkDLShowType.WebviewSub);
		} else {
			subJsCalss = new JSClass(subWebView, "M", getId(), getId(),
					mHandler, R, MeApkDLShowType.WebviewSub);
		}
		MeApkDownloadManager.MeAddActiveCallBackMap.put(
				MeApkDLShowType.WebviewSub, new MeActiveCallback() {

					@Override
					public void NotifyUninstallApkAction(String pkgName) {
						// TODO Auto-generated method stub
						subJsCalss.appInstallInfoChange(
								getApplicationContext(), pkgName, 0);
						subJsCalss.invokeJSMethod("reloadDownstate", pkgName);
					}

					@Override
					public void NotifyInstallSucessAction(String pkgName) {
						// TODO Auto-generated method stub
						subJsCalss.invokeJSMethod("AppInstallSuccess", pkgName);
						subJsCalss.appInstallInfoChange(
								getApplicationContext(), pkgName, 1);
					}

					@Override
					public void NotifyDelAction(String pkgName) {
						// TODO Auto-generated method stub
						MELOG.v("ME_RTFSC",
								"subJsCalss MeActiveCallback NotifyDelAction");
						subJsCalss.invokeJSMethod("reloadDownstate", pkgName);
					}

					@Override
					public void NoifySatrtAction(String pkgName) {
						// TODO Auto-generated method stub
						subJsCalss.invokeJSMethod("reloadDownstate", pkgName);
					}
				});
		bindJsClass(subJsCalss, subWebView);
		MELOG.v("ME_RTFSC", "id:" + getId() + " strAction:" + strAction
				+ ", strActionDescription:" + strActionDescription);
		if (null == strAction || strAction.isEmpty()
				|| null == strActionDescription
				|| strActionDescription.isEmpty()) {
			MELOG.v("ME_RTFSC", "1111111111111111111111111");
			MainFrameWebview.setVisibility(View.VISIBLE);
			LoadMiroEntryUrl(null, true);
		}
		// 增加 detailbox_pkgname 入口方式
		else if (strAction.equals("detailbox_pkgname")) {
			MELOG.v("ME_RTFSC",
					"detailbox_pkgname  detailbox_pkgname  detailbox_pkgname ");
			SubFrameWebview.setVisibility(View.VISIBLE);
			MainFrameWebview.setVisibility(View.INVISIBLE);
			subWebView.clearView();
			subJsCalss.setDialog();
			isNeedClearHistory = true;
			subWebView
					.loadUrl(" http://uifolder.coolauncher.com.cn/qqkhd/detailbox.htm?pkgname="
							+ strActionDescription);
			LoadMiroEntryUrl(null, false);
		} else if (strAction.equals("detailfolder_pkgname")) {
			MELOG.v("ME_RTFSC",
					"detailfolder_pkgname  detailfolder_pkgname  detailfolder_pkgname ");
			SubFrameWebview.setVisibility(View.VISIBLE);
			MainFrameWebview.setVisibility(View.INVISIBLE);
			subWebView.clearView();
			subJsCalss.setDialog();
			isNeedClearHistory = true;
			subWebView
					.loadUrl("http://uifolder.coolauncher.com.cn/qqkhd/detail.php?p="
							+ LoadURL.BaseDetalFolder64Str(this,
									strActionDescription));
			LoadMiroEntryUrl(null, false);
		} else if (strAction.equals("pkgname")) {
			MELOG.v("ME_RTFSC", "pkgname  pkgname  pkgname ");
			SubFrameWebview.setVisibility(View.VISIBLE);
			MainFrameWebview.setVisibility(View.INVISIBLE);
			subWebView.clearView();
			subJsCalss.setDialog();
			isNeedClearHistory = true;
			subWebView
					.loadUrl(" http://uifolder.coolauncher.com.cn/qqkhd/detailpush.htm?pkgname="
							+ strActionDescription);
			LoadMiroEntryUrl(null, false);
		} else if (strAction.equals("url")) {
			MELOG.v("ME_RTFSC", "url  url  url ");
			SubFrameWebview.setVisibility(View.VISIBLE);
			MainFrameWebview.setVisibility(View.INVISIBLE);
			subWebView.clearView();
			subJsCalss.setDialog();
			isNeedClearHistory = true;
			subWebView.loadUrl(strActionDescription);
			LoadMiroEntryUrl(null, false);
		} else if ((strAction.equals("anchor"))) {
			MELOG.v("ME_RTFSC", "anchor  anchor  anchor ");
			MainFrameWebview.setVisibility(View.VISIBLE);
			LoadMiroEntryUrl(strActionDescription, true);
		}
		mainJsClass.DownloadApkNeedDownload();
	}

	private void LoadMiroEntryUrl(String tableIndex,
			boolean isNeedShowPregressDlg) {
		// TODO Auto-generated method stub
		LoadURL.initPhoneInfoma(getApplicationContext());
		String url = MicroEntryHelper.getInstance(this).getEntryUrl(getId());
		if (url != null
				&& (url.startsWith("http://") || url.startsWith("https://"))) {
			url = url + "?" + "p="
					+ LoadURL.Base64Str(MainActivity.this, getId());
			// MELOG.v( "ME_RTFSC" , "1111  mainWebView.loadUrl" + url );
		} else {
			RijndaelCrypt aes = new RijndaelCrypt(RijndaelCrypt.PWD,
					RijndaelCrypt.IV);
			url = aes.decrypt(UPath) + "?p="
					+ LoadURL.Base64Str(MainActivity.this, getId());
			// MELOG.v( "ME_RTFSC" , "2222  mainWebView.loadUrl" + url );
		}
		// String url = "http://192.168.1.225/shl/test_goto.php" +
		// LoadURL.Base64Str( MainActivity.this , getId() );
		// MELOG.v( "ME_RTFSC" , "2222  mainWebView.loadUrl" );
		if (null != tableIndex && !tableIndex.isEmpty()) {
			url = url + "&tab=" + tableIndex;
		}
		MELOG.v("ME_RTFSC", "mainWebView.loadUrl" + url);
		if (isNeedShowPregressDlg) {
			mainJsClass.setDialog();
		}
		mainWebView.loadUrl(url);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "==== MainActivity  onStop ====");
		super.onStop();
	}

	public void onDestroy() {
		MELOG.v("ME_RTFSC", "==== MainActivity  onDestroy ====");
		super.onDestroy();
		// instance = null;
		if (mainWebView != null) {
			// Utils3D.showPidMemoryInfo( MainActivity.this , "MainActivity" );
		}
		// if( true == isExit )
		// {
		// android.os.Process.killProcess( android.os.Process.myPid() );
		// }
		// 在微入口activity销毁前，需要先把可能显示的dialog 销毁掉
		mainJsClass.canelDialog();
		subJsCalss.canelDialog();
		// MeGeneralMethod.KillProcessIfNeed( getApplicationContext() ) ;
		// if( !MeGeneralMethod.IsDownloadTaskRunning( getApplicationContext() )
		// && !MeGeneralMethod.IsForegroundRunning( getApplicationContext() ) )
		// {
		// MeGeneralMethod.stopMeDLProtectionService( getApplicationContext() );
		// android.os.Process.killProcess( android.os.Process.myPid() );
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		MELOG.v("ME_RTFSC", "==== MainActivity  onResume ====");
		// jsClass.Init();
		if (true == flag) {
			mainWebView.goBack();
			flag = false;
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 这里要退出，否则会导致界面还乱，原因未知，bug11952
		// finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		MELOG.v("ME_RTFSC", " onKeyDown ");
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (View.VISIBLE == MainFrameWebview.getVisibility()) {
				return MainWebViewOnBackListener();
			} else {
				return SubWebViewOnBackListener();
			}
		}
		return super.onKeyDown(keyCode, event); // 不会回到 home 页面
	}

	private boolean SubWebViewOnBackListener() {
		MELOG.v("ME_RTFSC", " SubWebViewOnBackListener ");
		// WebBackForwardList Weblist = subWebView.copyBackForwardList();
		// MELOG.v( "ME_RTFSC" , " WebViewOnBackListener Weblist size:" +
		// Weblist.getSize() + "Webview Cur:" + Weblist.getCurrentIndex() );
		flag = false;
		if (subWebView.canGoBack()) {
			subWebView.goBack();
			// Weblist = subWebView.copyBackForwardList();
			// MELOG.v( "ME_RTFSC" , " WebViewOnBackListener Weblist size:" +
			// Weblist.getSize() + "Webview Cur:" + Weblist.getCurrentIndex() );
		} else if ("detailfolder_pkgname".equals(strAction)) {
			finish();
		} else {
			SubFrameWebview.setVisibility(View.INVISIBLE);
			MainFrameWebview.setVisibility(View.VISIBLE);
		}
		return true;
	}

	private boolean MainWebViewOnBackListener() {
		MELOG.v("ME_RTFSC", " MainWebViewOnBackListener ");
		// TODO Auto-generated method stub
		// WebBackForwardList Weblist = mainWebView.copyBackForwardList();
		// MELOG.v( "ME_RTFSC" , " WebViewOnBackListener Weblist size:" +
		// Weblist.getSize() + "Webview Cur:" + Weblist.getCurrentIndex() );
		flag = false;
		if (mainWebView.canGoBack()) {
			mainWebView.goBack();
			// Weblist = mainWebView.copyBackForwardList();
			// MELOG.v( "ME_RTFSC" , " WebViewOnBackListener Weblist size:" +
			// Weblist.getSize() + "Webview Cur:" + Weblist.getCurrentIndex() );
			return true;
		} else {
			if (!isExit) {
				isExit = true;
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				mHandler.sendEmptyMessageDelayed(0, 2000);
			} else {
				finish();
			}
			return false;
		}
	}

	@SuppressLint("JavascriptInterface")
	private void bindJsClass(final JSClass jsClass, WebView webView) {
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setBackgroundColor(Color.parseColor("#e0e0df"));
		WebSettings webSettings = webView.getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setAppCacheMaxSize(16 * 1024 * 1024);
		webSettings.setAppCachePath(PathUtil
				.getPathDBSdcard(getApplicationContext()));
		// webSettings.set
		// HTML5地理位置服务在Android中的应用，因为用不着，所有注释掉
		// webSettings.setGeolocationEnabled( true );
		// webSettings.setGeolocationDatabasePath( PathUtil.getPathDBSdcard() );
		webSettings.setDatabaseEnabled(true);
		webSettings.setDatabasePath(PathUtil
				.getPathDBSdcard(getApplicationContext()));
		if ("detailfolder_pkgname".equals(strAction)) {
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		} else if (true == JSClass
				.IsNetworkAvailableLocal(getApplicationContext())) {
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		webView.addJavascriptInterface((Object) jsClass, "JSClass");
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				MELOG.v("ME_RTFSC", "=== onReceivedError ===  failingUrl:   "
						+ failingUrl);
				// view.loadUrl( "javascript:document.body.innerHTML= '' " );
				// view.loadUrl(
				// "javascript:window.location.replace('file:///android_asset/cool_ml_NoNet.htm');"
				// );
				view.loadUrl("file:///android_asset/cool_ml_NoNet.htm");
				jsClass.failingUrl = failingUrl;
				jsClass.canelDialog();
				// if( jsClass.builder != null )
				// {
				// jsClass.builder.dismiss();
				// }
				// WebBackForwardList Weblist = view.copyBackForwardList();
				// MELOG.v( "ME_RTFSC" , " onReceivedError Weblist size:" +
				// Weblist.getSize() + "Webview Cur:" +
				// Weblist.getCurrentIndex() );
				flagError = true;
				flag = true;
			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {// 网页覆盖
				MELOG.v("ME_RTFSC", "=== shouldOverrideUrlLoading ===  url:   "
						+ url);
				// WebBackForwardList Weblist = view.copyBackForwardList();
				// MELOG.v( "ME_RTFSC" ,
				// " shouldOverrideUrlLoading Weblist size:" + Weblist.getSize()
				// + "Webview Cur:" + Weblist.getCurrentIndex() );
				return super.shouldOverrideUrlLoading(view, url);
			}

			public void onPageFinished(WebView view, String url) {// 网页加载完毕
				MELOG.v("ME_RTFSC", "=== onPageFinished ===  url:   " + url);
				// WebBackForwardList Weblist = view.copyBackForwardList();
				// MELOG.v( "ME_RTFSC" , " onPageFinished Weblist size:" +
				// Weblist.getSize() + "Webview Cur:" +
				// Weblist.getCurrentIndex() );
				super.onPageFinished(view, url);
				if (flagError == true
						&& "file:///android_asset/cool_ml_NoNet.htm"
								.equals(url)) {
					// view.loadUrl( "javascript:document.body.innerHTML= ' ' "
					// );
					// view.loadUrl(
					// "javascript:window.location.replace('file:///android_asset/cool_ml_NoNet.htm');"
					// );
					// view.goBack();
					view.clearHistory();
					flagError = false;
				}
				if (true == isNeedClearHistory) {
					view.clearHistory();
					isNeedClearHistory = false;
				}
				jsClass.canelDialog();
				// Weblist = view.copyBackForwardList();
				// MELOG.v( "ME_RTFSC" , " onPageFinished Weblist size:" +
				// Weblist.getSize() + "Webview Cur:" +
				// Weblist.getCurrentIndex() );
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {// 网页开始加载
				MELOG.v("ME_RTFSC", "=== onPageStarted ===  url:   " + url);
				super.onPageStarted(view, url, favicon);
				// WebBackForwardList Weblist = view.copyBackForwardList();
				// MELOG.v( "ME_RTFSC" , " onPageStarted Weblist size:" +
				// Weblist.getSize() + "Webview Cur:" +
				// Weblist.getCurrentIndex() );
			}
		});
		webView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
	}

	public void setConfigCallback(WindowManager windowManager) {
		try {
			Field field = WebView.class.getDeclaredField("mWebViewCore");
			field = field.getType().getDeclaredField("mBrowserFrame");
			field = field.getType().getDeclaredField("sConfigCallback");
			field.setAccessible(true);
			Object configCallback = field.get(null);
			if (null == configCallback) {
				return;
			}
			field = field.getType().getDeclaredField("mWindowManager");
			field.setAccessible(true);
			field.set(configCallback, windowManager);
		} catch (Exception e) {
		}
	}

	// MainActivity的mHandler handleMaessge 的处理项
	public static final int openSubWebView = 1;
	public static final int subWebViewBackSoftKey = 2;
	public static final int mainWebViewBackSoftKey = 3;
	public static final int setBackgroundWithWallpaper = 4;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// 0 --退出 ； 1--下载失败
			switch (msg.what) {
			// 从MainView跳转到SubView
			case openSubWebView: {
				SubFrameWebview.setVisibility(View.VISIBLE);
				MainFrameWebview.setVisibility(View.INVISIBLE);
				String subUrl = (String) msg.obj;
				subWebView.clearView();
				subJsCalss.setDialog();
				isNeedClearHistory = true;
				subWebView.loadUrl(subUrl);
			}
				break;
			// MainWebView中返回软按键响应函数
			case mainWebViewBackSoftKey: {
				MainWebViewOnBackListener();
			}
				break;
			// subWebView中返回软按键响应函数
			case subWebViewBackSoftKey: {
				SubWebViewOnBackListener();
			}
				break;
			// 使用系统当前壁纸做为WEB页壁纸
			case setBackgroundWithWallpaper: {
				WebView curWebview = (WebView) msg.obj;
				WallpaperManager wallpaperManager = WallpaperManager
						.getInstance(getApplicationContext()); // 获取壁纸管理器
				Drawable wallpaperDrawable = wallpaperManager.getDrawable();// 获取当前壁纸
				curWebview.setBackgroundColor(0);
				curWebview.setBackgroundDrawable(wallpaperDrawable);
				// curWebview.refreshDrawableState();
			}
				break;
			default:
				break;
			}
		}
	};
}
