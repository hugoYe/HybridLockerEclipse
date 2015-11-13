package com.iLoong.launcher.MList;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import cool.sdk.MicroEntry.MicroEntryHelper;

public class MEServiceActivity extends Activity {

	boolean[] visible;
	int index = -1;
	int MEShowType = 0;
	Dialog ad = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// MeGeneralMethod.CanelKillProcess();
		super.onCreate(savedInstanceState);
		MELOG.v("ME_RTFSC", "==== MEServiceActivity  onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		if (null != intent) {
			MeServiceType type = (MeServiceType) intent
					.getSerializableExtra("MeServiceType");
			MELOG.v("ME_RTFSC", "==== MeServiceType  type:" + type);
			switch (type) {
			case MEShowType:
				onMEShowType(intent);
				break;
			case MEApkOnNotifyReStart:
				onMEApkOnNotifyReStart(intent);
				break;
			case MEApkReStartByEntryID:
				onMEApkReStartByEntryID(intent);
				break;
			case MEApkReStartAll:
				onMEApkReStartAll(intent);
				break;
			case MEApkOnSucess:
				onMEApkOnSucess(intent);
				break;
			case MEApkOnSucessEx:
				onMEApkOnSucessEx(intent);
				break;
			case MEApkOnDownloading:
				onMEApkOnDownloading(intent);
				break;
			case MePushShowType:
				onMePushShowType(intent);
				break;
			case MeApkOnPkgInstalled:
				onMeApkOnPkgInstalled(intent);
				break;
			case MeApkOnPkgUninstall:
				onMeApkOnPkgUninstall(intent);
				break;
			default:
				finish();
				break;
			}
		} else {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// if(!MeGeneralMethod.IsDownloadTaskRunning( getApplicationContext() )
		// && !MeGeneralMethod.IsForegroundRunning( getApplicationContext() ))
		// {
		// android.os.Process.killProcess( android.os.Process.myPid() );
		// }
		// MeGeneralMethod.KillProcessIfNeed( getApplicationContext() );
	}

	public void onMeApkOnPkgUninstall(Intent intent) {
		MELOG.v("ME_RTFSC", "==== onMeApkOnPkgUninstall  ");
		// MePkgRemoveIntent.putExtra( "PkgName" , pkgName );
		String pkgName = intent.getStringExtra("PkgName");
		if (null != pkgName && !pkgName.isEmpty()) {
			Map<String, MeApkDownloadManager> mgrMap = MeApkDlMgrBuilder
					.GetAllMeApkDownloadManager();
			MeApkDlNotifyManager.getInstance(getApplicationContext())
					.onMeApkUninstallCanel(pkgName);
			if (null != mgrMap && !mgrMap.isEmpty()) {
				for (String curMgrID : mgrMap.keySet()) {
					MELOG.v("ME_RTFSC", "ACTION_PACKAGE_REMOVED:" + curMgrID);
					mgrMap.get(curMgrID).ApkUninstall(pkgName);
				}
			}
		}
		finish();
	}

	public void onMeApkOnPkgInstalled(Intent intent) {
		MELOG.v("ME_RTFSC", "==== onMeApkOnPkgAdded  ");
		ArrayList<Integer> PkgAddedEntryIDList = intent
				.getIntegerArrayListExtra("PkgAddedEntryIDList");
		String pkgName = intent.getStringExtra("PkgName");
		Map<String, MeApkDownloadManager> mgrMap = MeApkDlMgrBuilder
				.GetAllMeApkDownloadManager();
		MELOG.v("ME_RTFSC", "pkgName = " + pkgName + ", PkgAddedEntryIDList"
				+ PkgAddedEntryIDList);
		if (null != PkgAddedEntryIDList && !PkgAddedEntryIDList.isEmpty()
				&& null != pkgName && !pkgName.isEmpty()) {
			MELOG.v("ME_RTFSC", "ACTION_PACKAGE_ADDED:mgrMap =" + mgrMap);
			if (null != mgrMap && !mgrMap.isEmpty()) {
				for (String curMgrID : mgrMap.keySet()) {
					MELOG.v("ME_RTFSC", "ACTION_PACKAGE_ADDED:" + curMgrID);
					mgrMap.get(curMgrID).ApkInstalled(pkgName);
				}
			} else {
				for (int i = 0; i < PkgAddedEntryIDList.size(); i++) {
					MeApkDlMgrBuilder.Build(getApplicationContext(), "M",
							PkgAddedEntryIDList.get(i)).ApkInstalled(pkgName);
				}
			}
		} else if (null != pkgName && !pkgName.isEmpty() && null != mgrMap
				&& !mgrMap.isEmpty()) {
			for (String curMgrID : mgrMap.keySet()) {
				MELOG.v("ME_RTFSC", "ACTION_PACKAGE_ADDED:" + curMgrID);
				mgrMap.get(curMgrID).ApkInstalled(pkgName);
			}
		}
		finish();
	}

	public void onMePushShowType(Intent intent) {
		MELOG.v("ME_RTFSC", "==== onMePushShowType  ");
		int PushID = intent.getIntExtra("PUSH_ID", 0);
		int MicEnrtyID = intent.getIntExtra("APP_ID", 0);
		String strAction = intent.getStringExtra("Action");
		String strActionDescription = intent
				.getStringExtra("ActionDescription");
		MELOG.v("ME_RTFSC", "onMePushShowType  strPushID:" + PushID + ", "
				+ "strMicEnrtyID:" + MicEnrtyID + ", strAction:" + strAction
				+ ", strActionDescription:" + strActionDescription);
		if (MicEnrtyID > 0 && null != strAction && null != strActionDescription) {
			MELOG.v("ME_RTFSCX",
					" ApkMangerActivity  onMePushShowType  ======    instance:"
							+ ApkMangerActivity.instance + ",CurEntryID:"
							+ ApkMangerActivity.CurEntryID);
			if (null != ApkMangerActivity.instance) {
				MELOG.v("ME_RTFSC",
						"finish ApkMangerActivity.instance.finish() ");
				ApkMangerActivity.instance.finish();
				// 防止ApkMangerActivity onDestory , 在新的OnCreate之后运行
				ApkMangerActivity.CurEntryID = -1;
				ApkMangerActivity.instance = null;
			}
			if (null != MainActivity.instance) {
				MELOG.v("ME_RTFSC", "finish MainActivity.instance.finish() ");
				MainActivity.instance.finish();
			}
			Intent ActivtyIntent = new Intent(getApplicationContext(),
					MainActivity.class);
			ActivtyIntent.putExtra("APP_ID", MicEnrtyID);
			ActivtyIntent.putExtra("Action", strAction);
			ActivtyIntent.putExtra("ActionDescription", strActionDescription);
			ActivtyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(ActivtyIntent);
		}
		// ActivtyIntent.putExtra( "PUSH_ID" , msg.getMsgId() );
		finish();
	}

	public void onMEApkOnDownloading(Intent intent) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "==== onMEApkOnDownloading  ");
		int entryID = intent.getIntExtra("entryId", 0);
		String moudleName = intent.getStringExtra("moudleName");
		MELOG.v("ME_RTFSC", "entryID:" + entryID + ", moudleName:" + moudleName);
		// 所以微入口的下载管理器都是用ApkMangerActivity现实，当有两个和两个以上的正在下载的入口的时候，
		// 这里需要先结束之前的下载/安装管理器，然后启动当前的下载/安装管理器
		if (ApkMangerActivity.CurEntryID != entryID
				&& null != ApkMangerActivity.instance) {
			ApkMangerActivity.instance.finish();
			// 防止ApkMangerActivity onDestory , 在新的OnCreate之后运行
			ApkMangerActivity.CurEntryID = -1;
			ApkMangerActivity.instance = null;
		}
		Intent intenta = new Intent(this, ApkMangerActivity.class);
		intenta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intenta.putExtra("moudleName", moudleName);
		intenta.putExtra("entryId", entryID);
		intenta.putExtra("isStartByNotify", true);
		startActivity(intenta);
		finish();
	}

	public void onMEApkOnSucessEx(Intent intent) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "==== onMEApkOnSucess  ");
		String moudleName = intent.getStringExtra("moudleName");
		String apkFilePath = intent.getStringExtra("apkFilePath");
		int notifyID = intent.getIntExtra("notifyID", 0);
		if (!moudleName.isEmpty() && !apkFilePath.isEmpty()) {
			File file = new File(apkFilePath);
			if (file.exists()) {
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				installIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				installIntent.setDataAndType(
						Uri.fromFile(new File(apkFilePath)),
						"application/vnd.android.package-archive");
				startActivity(installIntent);
				// MELOG.v( "ME_RTFSC" ,
				// "==== onMEApkOnSucess set value pkgName:" + pkgName +
				// ", entryID:" + entryID );
				// MicroEntryHelper.getInstance( getApplicationContext()
				// ).setValue( pkgName , entryID );
			} else {
				MyR RR = MyR.getMyR(getApplicationContext());
				if (RR == null) {
					finish();
				}
				Toast.makeText(getApplicationContext(),
						RR.string.cool_ml_install_file_not_exsit,
						Toast.LENGTH_SHORT).show();
				NotificationManager notificationManager = (NotificationManager) getApplicationContext()
						.getSystemService(
								android.content.Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(notifyID);
			}
		}
		finish();
	}

	public void onMEApkOnSucess(Intent intent) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "==== onMEApkOnSucess  ");
		int entryID = intent.getIntExtra("entryID", 0);
		int notifyID = intent.getIntExtra("notifyID", 0);
		String moudleName = intent.getStringExtra("moudleName");
		String pkgName = intent.getStringExtra("PkgName");
		String appName = intent.getStringExtra("appName");
		String apkFilePath = intent.getStringExtra("apkFilePath");
		String apkIconPath = intent.getStringExtra("apkIconPath");
		if (!moudleName.isEmpty() && !apkFilePath.isEmpty()) {
			File file = new File(apkFilePath);
			if (file.exists()) {
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				installIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				// installIntent.putExtra( "NOTIFY_ID" , entryID );
				installIntent.setDataAndType(
						Uri.fromFile(new File(apkFilePath)),
						"application/vnd.android.package-archive");
				startActivity(installIntent);

				// MELOG.v( "ME_RTFSC" ,
				// "==== onMEApkOnSucess set value pkgName:" + pkgName +
				// ", entryID:" + entryID );
				// MicroEntryHelper.getInstance( getApplicationContext()
				// ).setValue( pkgName , entryID );
				MeApkDlNotifyManager.getInstance(getApplicationContext())
						.onMeApkDlSucessEx(entryID, notifyID, moudleName,
								pkgName, appName, apkFilePath, apkIconPath);
			} else {
				MyR RR = MyR.getMyR(getApplicationContext());
				if (RR == null) {
					finish();
				}
				Toast.makeText(getApplicationContext(),
						RR.string.cool_ml_install_file_not_exsit,
						Toast.LENGTH_SHORT).show();
				NotificationManager notificationManager = (NotificationManager) getApplicationContext()
						.getSystemService(
								android.content.Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(notifyID);
			}
		}
		finish();
	}

	public void onMEApkReStartByEntryID(Intent intent) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "MEServiceActivity:onMEApkReStartByEntryID");
		int entryID = intent.getIntExtra("entryID", -1);
		String moudleName = intent.getStringExtra("moudleName");
		ArrayList<String> PkgNameList = intent
				.getStringArrayListExtra("PkgNameList");
		MELOG.v("ME_RTFSC", "PkgNameList:" + PkgNameList + "entryID:" + entryID);
		if (-1 != entryID && !moudleName.isEmpty() && null != PkgNameList
				&& !PkgNameList.isEmpty()) {
			MeApkDownloadManager DlMgr = MeApkDlMgrBuilder.Build(
					getApplicationContext(), moudleName, entryID);
			for (String pkgName : PkgNameList) {
				DlMgr.ReStartDownload(MeApkDLShowType.NeedReStartDownload,
						pkgName, null);
				// 如果图片没有下载完成，还需要下载图片
				if (null != DlMgr.GetSdkIconMgr().IconGetInfo(pkgName)) {
					if (!DlMgr.GetSdkIconMgr().IconGetInfo(pkgName)
							.IsDownloadSuccess()) {
						DlMgr.GetSdkIconMgr().IconDownload(pkgName, null);
					}
				}
			}
		}
		finish();
	}

	public void onMEApkReStartAll(Intent intent) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "MEServiceActivity:onMEApkReStartAll");
		String moudleName = intent.getStringExtra("moudleName");
		ArrayList<Integer> entryIDList = intent
				.getIntegerArrayListExtra("entryIDList");
		if (null != entryIDList && !entryIDList.isEmpty()) {
			for (Integer entryID : entryIDList) {
				ArrayList<String> PkgNameList = intent
						.getStringArrayListExtra("PkgNameList" + entryID);
				MELOG.v("ME_RTFSC", "PkgNameList:" + PkgNameList + "entryID:"
						+ entryID);
				if (-1 != entryID && !moudleName.isEmpty()
						&& null != PkgNameList && !PkgNameList.isEmpty()) {
					MeApkDownloadManager DlMgr = MeApkDlMgrBuilder.Build(
							getApplicationContext(), moudleName, entryID);
					for (String pkgName : PkgNameList) {
						DlMgr.ReStartDownload(
								MeApkDLShowType.NeedReStartDownload, pkgName,
								null);
						// 如果图片没有下载完成，还需要下载图片
						if (null != DlMgr.GetSdkIconMgr().IconGetInfo(pkgName)) {
							if (!DlMgr.GetSdkIconMgr().IconGetInfo(pkgName)
									.IsDownloadSuccess()) {
								DlMgr.GetSdkIconMgr().IconDownload(pkgName,
										null);
							}
						}
					}
				}
			}
		}
		finish();
	}

	public void onMEApkOnNotifyReStart(Intent intent) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "MEServiceActivity:onMEApkOnNotifyReStart");
		int entryID = intent.getIntExtra("entryID", -1);
		String moudleName = intent.getStringExtra("moudleName");
		String PkgName = intent.getStringExtra("PkgName");
		MELOG.v("ME_RTFSC", "PkgName:" + PkgName + "entryID:" + entryID);
		if (-1 != entryID && !moudleName.isEmpty() && !PkgName.isEmpty()) {
			MeApkDownloadManager DlMgr = MeApkDlMgrBuilder.Build(
					getApplicationContext(), moudleName, entryID);
			DlMgr.ReStartDownload(MeApkDLShowType.Notification, PkgName, null);
		}
		finish();
	}

	class MeDisclaimeDlg extends Dialog {

		Context mContext = null;
		boolean IsShowCanel = false;
		MyR MeR = null;
		View cancel = null;
		View update = null;

		MeDisclaimeDlg(Context Context, MyR RR, int MEShowType) {
			// TODO Auto-generated constructor stub
			super(Context, R.style.Theme_Translucent_NoTitleBar);
			mContext = Context;
			MeR = RR;
			IsShowCanel = (0 == MEShowType);
		}

		android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == update) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							MicroEntryHelper.getInstance(
									getApplicationContext())
									.UpdateMeStateUserConfirm(visible, index);
						}
					}).start();
				}
				ad.dismiss();
				ad = null;
				finish();
			}
		};

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(MeR.layout.cool_ml_disclaimer_dialog);
			cancel = findViewById(MeR.id.cool_ml_discalmer_del);
			update = findViewById(MeR.id.cool_ml_disclaimer_update);
			if (false == IsShowCanel) {
				cancel.setVisibility(View.GONE);
			}
			update.setOnClickListener(listener);
			cancel.setOnClickListener(listener);
			setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					ad.dismiss();
					ad = null;
					finish();
				}
			});
		};

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			// TODO Auto-generated method stub
			MELOG.v("ME_RTFSC", "dlg  dispatchKeyEvent :" + event.getKeyCode());
			return super.dispatchKeyEvent(event);
		}
	}

	public void onMEShowType(Intent intent) {
		visible = intent.getBooleanArrayExtra("NOTIFY_ME_SHOW_ARRY");
		index = intent.getIntExtra("NOTIFY_ME_SHOW_ID", -1);
		MEShowType = intent.getIntExtra("NOTIFY_ME_SHOW_TYPE", -1);
		MELOG.v("ME_RTFSC", "  index:" + index + "visible" + visible[0] + ","
				+ visible[1] + "," + visible[2] + "," + visible[3]);
		if (-1 == index) {
			finish();
		}
		MyR RR = MyR.getMyR(getApplicationContext());
		if (RR == null) {
			finish();
		}
		ad = null;
		ad = new MeDisclaimeDlg(this, RR, MEShowType);
		ad.getWindow().setGravity(Gravity.CENTER);
		ad.setCanceledOnTouchOutside(false); // 点击外面区域不会让dialog消失
		ad.show();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		MELOG.v("ME_RTFSC", "dispatchKeyEvent :" + event.getKeyCode());
		return false;
	}
}
