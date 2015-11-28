package com.cooee.control.center.module.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.cooee.control.center.module.base.FileUtils;

public class UpdateTask {

	private Context mContext;
	private XmlTask xmlTask;
	private SharedPreferences sharedPrefer;
	private SharedPreferences.Editor editor;
	private boolean updateFailed = false;
	private final String downUrl = "http://www.coolauncher.cn/locker/h5style01.zip";
	private final String zipName = "h5style01.zip";

	public UpdateTask(Context context) {
		mContext = context;
		sharedPrefer = mContext.getSharedPreferences("Update",
				Context.MODE_PRIVATE);
		editor = sharedPrefer.edit();
		updateFailed = sharedPrefer.getBoolean("update_failed", false);
		if (updateFailed) {
			if (judgeUpdate(30 * 60 * 1000)) {
				updateFile();
			}else {
				stopUpdateService(mContext);
			}
		} else {
			if (judgeUpdate(6 * 60 * 60 * 1000)) {
				checkUpdate();
			}else {
				stopUpdateService(mContext);
			}
		}
	}

	public void checkUpdate() {
		xmlTask = new XmlTask();
		xmlTask.execute();
	}

	// 检查是否更新
	private boolean judgeUpdate(long duration) {
		Long time = sharedPrefer.getLong("update_time", 0);
		if (System.currentTimeMillis() - time > duration) {
			editor.putLong("update_time",
					System.currentTimeMillis()).commit();
			return true;
		}
		return false;
	}

	private void updateFile() {
		editor.putBoolean("update_complete", false);
		editor.commit();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String path = Environment.getExternalStorageDirectory()
							.getAbsolutePath()
							+ "/h5lock/"
							+ mContext.getPackageName();
					boolean downFinish = downloadFile(path);
					if (downFinish) {
						FileUtils.deleteFile(new File(path + "/www/"));
						File file = new File(path + "/" + zipName);
						unzipFiles(file, path);
						if (file.exists()) {
							Log.v("UpdateManager", "delete h5style01");
							file.delete();
							Log.v("UpdateManager", "delete h5style01 111111111");
						}
						new File(path + "/success").createNewFile();
						editor.putLong("update_time",
								System.currentTimeMillis());
						editor.putBoolean("update_complete", true);
						editor.putBoolean("update_failed", false);
						editor.commit();
						stopUpdateService(mContext);
					} else {
						editor.putLong("update_time",
								System.currentTimeMillis());
						editor.putBoolean("update_complete", true);
						editor.putBoolean("update_failed", true);
						editor.commit();
						stopUpdateService(mContext);
					}
				} catch (Exception e) {
					e.printStackTrace();
					stopUpdateService(mContext);
				}
			}
		}).start();
	}

	private class XmlTask extends AsyncTask<Void, Void, Boolean> {

		private UpdateManager updateManager;

		public XmlTask() {
			updateManager = new UpdateManager(mContext);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (updateManager.isUpdate()) {
				Log.v("UpdateManager", "doInBackground");
				return true;
			}else {
				stopUpdateService(mContext);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.v("UpdateManager", "onPostExecute");
			if (result) {
				updateFile();
			}
		}
	}

	private boolean downloadFile(String updateDir) throws Exception {
		// 判断文件目录是否存在
		File file = new File(updateDir);
		if (!file.exists()) {
			file.mkdir();
		}
		File updateFile = new File(updateDir + "/" + zipName);
		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream fos = null;
		int readsize = 0;
		boolean isFinish = false;
		try {
			URL url = new URL(downUrl);
			// 创建连接
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			// 创建输入流
			is = conn.getInputStream();
			fos = new FileOutputStream(updateFile);
			// 缓存
			byte buf[] = new byte[1024];
			// 写入到文件中
			while ((readsize = is.read(buf)) > 0) {
				// 写入文件
				fos.write(buf, 0, readsize);
			}
			isFinish = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return isFinish;
	}

	private void unzipFiles(File file, String destDir) {
		// 压缩文件
		File srcZipFile = file;
		// 基本目录
		if (!destDir.endsWith("/")) {
			destDir += "/";
		}
		String prefixion = destDir;
		// 压缩输入流
		ZipInputStream zipInput = null;
		try {
			zipInput = new ZipInputStream(new FileInputStream(srcZipFile));
			// 压缩文件入口
			ZipEntry currentZipEntry = null;
			// 循环获取压缩文件及目录
			while ((currentZipEntry = zipInput.getNextEntry()) != null) {
				// 获取文件名或文件夹名
				String fileName = currentZipEntry.getName();
				// Log.v("filename",fileName);
				// 构成File对象
				File tempFile = new File(prefixion + fileName);
				// 父目录是否存在
				if (!tempFile.getParentFile().exists()) {
					// 不存在就建立此目录
					tempFile.getParentFile().mkdir();
				}
				// 如果是目录，文件名的末尾应该有“/"
				if (currentZipEntry.isDirectory()) {
					// 如果此目录不在，就建立目录。
					if (!tempFile.exists()) {
						tempFile.mkdir();
					}
					// 是目录，就不需要进行后续操作，返回到下一次循环即可。
					continue;
				}
				// 如果是文件
				if (!tempFile.exists()) {
					// 不存在就重新建立此文件。当文件不存在的时候，不建立文件就无法解压缩。
					tempFile.createNewFile();
				}
				// 输出解压的文件
				FileOutputStream tempOutputStream = new FileOutputStream(
						tempFile);
				// 获取压缩文件的数据
				byte[] buffer = new byte[1024];
				int hasRead = 0;
				// 循环读取文件数据
				while ((hasRead = zipInput.read(buffer)) > 0) {
					tempOutputStream.write(buffer, 0, hasRead);
				}
				tempOutputStream.flush();
				tempOutputStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				zipInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void stopUpdateService(Context context) {
		if (context != null) {
			Intent intent = new Intent();
			intent.setClassName(context,
					"com.cooee.control.center.module.update.UpdateService");
			context.stopService(intent);
		}
	}
	
}
