package com.cooee.control.center.module.base;

import android.os.Handler.Callback;
import android.view.View;

public interface IWrap {

	void onCreate();

	void onDestroy();

	void onResume();

	void onPause();

	View getView();

	void setKernelCallback(Callback callback);

	Callback getAppService();

	int KERNEL_EXIT = 10000;
	int KERNEL_RESET_LIGHT = 10001;
	int REQUEST_KERNEL_SEND_SIMCARD_NAME = 10002;
	int APP_LOGINFO = 20000;
	int APP_REMOTE_CONTEXT = 20001;
	int NOTIFY_APP_SIMCARD_NAME = 20002;
	int APP_SETZORDER = 20003;
	int NOTIFY_APP_SETKEYGUARDDONE = 20005;
	int REQUEST_SCREEN_BITMAP = 20006;
	int NOTITY_APP_SCREEN_BITMAP = 20007;
	int NOTITY_APP_NOTIFICATION = 20009;
	int REQUEST_FRAMEWORK_VERSION = 20010;
	int NOTITY_APP_FRAMEWORK_VERSION = 20011;

	int APP_FRAMEWORK_VERSION = 101;
}
