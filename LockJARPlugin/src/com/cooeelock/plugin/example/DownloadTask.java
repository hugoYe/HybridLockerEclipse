package com.cooeelock.plugin.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * 传入URL即可在notification处进行下载，默认保存在sdcard的download目录下，文件名保持一致
 * 
 * @author Steve
 */
@SuppressLint("NewApi")
public class DownloadTask extends AsyncTask<String, Integer, String> {

	public static final int PREPARE = 11;
	public static final int COMPLETED = 22;
	public static final int UPDATEPROGRESS = 33;
	final String path = Environment.getExternalStorageDirectory().getPath()
			+ "/Download/";

	private Context mContext;
	private NotificationManager mNotificationManager;
	private Notification.Builder mBuilder;
	private RemoteViews mRemoteViews;
	private PendingIntent contentIntent;
	private Notification notification;
	// 指定ID可以重复产生notification
	private int notificationID = 11;
	private String title = "Title";
	private String filename;
	private int fileSize;
	private int downLoadFileSize;
	private FileOutputStream fos;
	private int iconID = 0;
	private String tip_download = "Download";

	public DownloadTask(Context context, int id, String title, int srcId) {
		Log.e("DownloadTask", "######## DownloadTask  111");
		this.mContext = context;
		this.mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		this.notificationID = id;
		this.title = title;
		this.iconID = srcId;
		this.tip_download = "download";
		mBuilder = new Notification.Builder(mContext);
		Log.e("DownloadTask", "######## DownloadTask  222");
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		// 进度条更新
		// mRemoteViews.setProgressBar(R.id.progressbar_progress, fileSize,
		// downLoadFileSize, false);
		// int i = downLoadFileSize * 100 / fileSize;
		// mRemoteViews.setTextViewText(R.id.textview_progress, tip_download +
		// ":"
		// + i + "%");
		// mRemoteViews.setImageViewResource(R.id.image_icon, iconID);
		// notification.contentView = mRemoteViews;
		Log.e("DownloadTask", "######## onProgressUpdate");
		mNotificationManager.notify(notificationID, notification);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Log.e("DownloadTask", "######## onPreExecute  111");
		mBuilder.setSmallIcon(iconID).setWhen(System.currentTimeMillis())
				.setAutoCancel(false);
		notification = mBuilder.build();
		// mRemoteViews.setTextViewText(R.id.textview_title, title);
		// notification.contentView = mRemoteViews;
		notification.flags = Notification.DEFAULT_LIGHTS
				| Notification.FLAG_AUTO_CANCEL;
		// 加i是为了显示多条Notification
		mNotificationManager.notify(notificationID, notification);
		Log.e("DownloadTask", "######## onPreExecute 222");
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			String url = params[0];
			// 获取文件名
			filename = url.substring(url.lastIndexOf("/") + 1);
			down_file(url, path + filename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 下载文件函数
	 * 
	 * @param url
	 *            需要被下载的文件的地址
	 * @param path
	 *            文件需要被存储的完整路径
	 */
	public void down_file(String url, String path) throws IOException {
		int times = 0;
		URL downloadURL = new URL(url);
		URLConnection conn = downloadURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		this.fileSize = conn.getContentLength();// 根据响应获取文件大小
		if (this.fileSize <= 0) {
			throw new RuntimeException("无法获知文件大小 ");
		}
		if (is == null) {
			throw new RuntimeException("stream is null");
		}
		fos = new FileOutputStream(path);
		// 把数据存入路径+文件名
		byte buf[] = new byte[1024];

		downLoadFileSize = 0;
		// sendMsg(PREPARE);
		do {
			// 循环读取
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
			downLoadFileSize += numread;
			// 每下载10KB进行一次刷新，避免频繁刷新
			if ((times == 10) || (downLoadFileSize == fileSize)) {
				publishProgress(downLoadFileSize);
				times = 0;
			}
			times++;
		} while (true);
		try {
			fos.flush();
			fos.close();
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private final String ACTION_JS_DATA = "com.cooeelock.core.jarplugin.jsdata";

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.e("onPostExecute", "######## onPreExecute 111");
		/**
		 * 点击安装
		 */
		File apkFile = new File(path + filename);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkFile),
				"application/vnd.android.package-archive");
		contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
		// 点击执行的intent
		notification.contentIntent = contentIntent;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		mNotificationManager.notify(notificationID, notification);
		Log.e("onPostExecute", "######## onPreExecute 222");
		InstallApk(mContext, apkFile.getAbsolutePath());

		Intent it = new Intent();
		it.setAction(ACTION_JS_DATA);
		it.putExtra("key_js_data", "javascript:onjsdata();");
		it.putExtra("key_unlock", true);
		mContext.sendBroadcast(it);

	}

	public static boolean InstallApk(Context context, String apkPath) {
		Log.e("onPostExecute", "######## InstallApk apkPath = " + apkPath);
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(apkPath)),
					"application/vnd.android.package-archive");
			context.startActivity(intent);

			return true;
		} catch (Exception ex) {
			// ex.printStackTrace();
			Log.e("onPostExecute", "######## InstallApk e = " + ex);
			return false;
		}
	}

}
