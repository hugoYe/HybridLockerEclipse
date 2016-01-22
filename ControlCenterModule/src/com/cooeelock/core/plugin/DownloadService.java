package com.cooeelock.core.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cooee.control.center.module.R;

public class DownloadService extends Service {

	private NotificationManager mNotificationManager;
	private Notification notification;
	private boolean cancelUpdate = false;
	private MyHandler myHandler;
	private ExecutorService executorService = Executors.newFixedThreadPool(5); // 固定五个线程来执行任务
	public static Map<Integer, Integer> download = new HashMap<Integer, Integer>();
	private Context context;
	private final String path = Environment.getExternalStorageDirectory()
			.getPath() + "/Download/";
	private final String ACTION_JS_DATA = "com.cooeelock.core.apkplugin.jsdata";

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		myHandler = new MyHandler(Looper.myLooper(), this);
		context = this;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String url = intent.getStringExtra("down_url");
			String packageName = intent.getStringExtra("packageName");
			String apkName = intent.getStringExtra("apkName");
			if (!isApkInstall(context, packageName)) {
				SharedPreferences sharedPrefer = PreferenceManager
						.getDefaultSharedPreferences(context);
				if (!sharedPrefer.getBoolean(apkName, false)) {
					downNewFile(url, 1071, apkName, packageName);
				} else {
					File flie = new File(path + apkName + ".apk");
					if (flie.exists()) {
						Instanll(flie, context);
						unlock(context);
					} else {
						sharedPrefer.edit().putBoolean(apkName, false).commit();
						downNewFile(url, 1071, apkName, packageName);
					}
				}
			}
		}
		return START_NOT_STICKY;
	}

	public void downNewFile(final String url, final int notificationId,
			final String name, final String packagename) {
		if (download.containsKey(notificationId))
			return;
		notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.contentView = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		notification.contentView.setProgressBar(R.id.progressbar_progress, 100,
				0, false);
		notification.contentView.setTextViewText(R.id.textview_title, name);
		notification.defaults = Notification.DEFAULT_LIGHTS;
		// 显示在“正在进行中”
		notification.flags = Notification.FLAG_NO_CLEAR
				| Notification.FLAG_ONGOING_EVENT;
		download.put(notificationId, 0);
		// 将下载任务添加到任务栏中
		mNotificationManager.notify(notificationId, notification);
		// 启动线程开始执行下载任务
		downFile(url, notificationId, name, packagename);
	}

	// 下载更新文件
	private void downFile(final String url, final int notificationId,
			final String name, final String packagename) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				File tempFile = null;
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					HttpResponse response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					if (is != null) {
						File rootFile = new File(path);
						if (!rootFile.exists() && !rootFile.isDirectory())
							rootFile.mkdir();

						tempFile = new File(path, name + ".apk");
						if (tempFile.exists())
							tempFile.delete();
						tempFile.createNewFile();

						// 已读出流作为参数创建一个带有缓冲的输出流
						BufferedInputStream bis = new BufferedInputStream(is);

						// 创建一个新的写入流，讲读取到的图像数据写入到文件中
						FileOutputStream fos = new FileOutputStream(tempFile);
						// 已写入流作为参数创建一个带有缓冲的写入流
						BufferedOutputStream bos = new BufferedOutputStream(fos);

						int read;
						long count = 0;
						int precent = 0;
						byte[] buffer = new byte[1024];
						while ((read = bis.read(buffer)) != -1 && !cancelUpdate) {
							bos.write(buffer, 0, read);
							count += read;
							precent = (int) (((double) count / length) * 100);

							// 每下载完成1%就通知任务栏进行修改下载进度
							if (precent - download.get(notificationId) >= 1) {
								download.put(notificationId, precent);
								Message message = myHandler.obtainMessage(3,
										precent);
								Bundle bundle = new Bundle();
								bundle.putString("name", name);
								message.setData(bundle);
								message.arg1 = notificationId;
								myHandler.sendMessage(message);
							}
						}
						bos.flush();
						bos.close();
						fos.flush();
						fos.close();
						is.close();
						bis.close();
					}

					if (!cancelUpdate) {
						Message message = myHandler.obtainMessage(2, tempFile);
						message.arg1 = notificationId;
						Bundle bundle = new Bundle();
						bundle.putString("name", name);
						bundle.putString("packagename", packagename);
						message.setData(bundle);
						myHandler.sendMessage(message);
					} else {
						tempFile.delete();
					}
				} catch (ClientProtocolException e) {
					if (tempFile.exists())
						tempFile.delete();
					Message message = myHandler.obtainMessage(4, name
							+ "下载失败：网络异常！");
					message.arg1 = notificationId;
					myHandler.sendMessage(message);
				} catch (IOException e) {
					if (tempFile.exists())
						tempFile.delete();
					Message message = myHandler.obtainMessage(4, name
							+ "下载失败：文件传输异常");
					message.arg1 = notificationId;
					myHandler.sendMessage(message);
				} catch (Exception e) {
					if (tempFile.exists())
						tempFile.delete();
					Message message = myHandler.obtainMessage(4, name + "下载失败,"
							+ e.getMessage());
					message.arg1 = notificationId;
					myHandler.sendMessage(message);
				}
			}
		});
	}

	private void unlock(Context context){
		Intent it = new Intent();
		it.setAction(ACTION_JS_DATA);
		it.putExtra("key_js_data", "javascript:onjsdata();");
		it.putExtra("key_unlock", true);
		context.sendBroadcast(it);
	}
	
	// 安装下载后的apk文件
	private void Instanll(File file, Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/* 事件处理类 */
	class MyHandler extends Handler {
		private Context context;

		public MyHandler(Looper looper, Context c) {
			super(looper);
			this.context = c;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg != null) {
				switch (msg.what) {
				case 0:
					Toast.makeText(context, msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
					download.remove(msg.arg1);
					break;
				case 1:
					break;
				case 2:
					notification.contentView.setProgressBar(
							R.id.progressbar_progress, 100, 100, false);
					notification.contentView.setTextViewText(
							R.id.textview_progress, "下载完成:100%");
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.fromFile((File) msg.obj),
							"application/vnd.android.package-archive");
					notification.contentIntent = PendingIntent.getActivity(
							context, 0, intent, 0);
					mNotificationManager.notify(msg.arg1, notification);
					// 下载完成后清除所有下载信息，执行安装提示
					download.remove(msg.arg1);
					// mNotificationManager.cancel(msg.arg1);
					SharedPreferences sharedPrefer = PreferenceManager
							.getDefaultSharedPreferences(context);
					sharedPrefer
							.edit()
							.putInt(msg.getData().getString("packagename"),
									msg.arg1).commit();
					sharedPrefer.edit()
							.putBoolean(msg.getData().getString("name"), true)
							.commit();
	
					Instanll((File) msg.obj, context);
					unlock(context);
					break;
				case 3:
					notification.contentView.setProgressBar(
							R.id.progressbar_progress, 100,
							download.get(msg.arg1), false);
					notification.contentView.setTextViewText(
							R.id.textview_progress,
							"正在下载:" + download.get(msg.arg1) + "%");
					mNotificationManager.notify(msg.arg1, notification);
					break;
				case 4:
					Toast.makeText(context, msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
					download.remove(msg.arg1);
					mNotificationManager.cancel(msg.arg1);
					break;
				}
			}
		}
	}

	private boolean isApkInstall(Context context, String packagename) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}
}
