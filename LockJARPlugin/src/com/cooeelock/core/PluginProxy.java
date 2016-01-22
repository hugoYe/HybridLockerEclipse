package com.cooeelock.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class PluginProxy implements IPluginProxy {

	private final String TAG = "PluginProxy";

	private String mLockAuthority;
	private boolean mCopyJarFinish;

	private final String url = "http://ec2-54-169-66-228.ap-southeast-1.compute.amazonaws.com/get_keywords/geo_getcitywords.php";

	private final String ACTION_SAVE_JSON_DATA = "com.cooeelock.save.jarplugin.jsondata";
	private final String ACTION_SAVE_RES_DATA = "com.cooeelock.save.jarplugin.resdata";
	private final String ACTION_GET_JSON_DATA = "com.cooeelock.get.jarplugin.jsondata";
	private final String ACTION_GET_RES_DATA = "com.cooeelock.get.jarplugin.resdata";
	private final String ACTION_CHECK_HOT_UPDATE = "checkHotData";
	private final String ACTION_GET_HOT_DATA = "getHotData";
	private final String ACTION_DOWN_HOT_DATA = "downHotData";
	private final String ACTION_INIT_JARPLUGIN = "initJarplugin";
	private final String ACTION_JAR_COPY_SUCCESS = "com.cooee.copy.jar.to.data.success";
	private final String ACTION_DOWN_RING_DATA = "downRingData";
	private final String ACTION_SAVE_RING_DATA = "saveRingData";
	private final String ACTION_GET_RING_DATA = "getRingData";
//	private final String downRingsUrl = "http://ec2-54-169-66-228.ap-southeast-1.compute.amazonaws.com/hostspot_ring/rings.zip";
//	private final String downRingsVersionUrl = "http://ec2-54-169-66-228.ap-southeast-1.compute.amazonaws.com/hostspot_ring/version.txt";
	private final String downRingsUrl = "http://www.coolauncher.cn/locker/rings/rings.zip";
	private final String downRingsVersionUrl = "http://www.coolauncher.cn/locker/rings/version.txt";
	
	/**
	 * 锁屏apk的上下文环境
	 * */
	private Context mRemoteContext;

	/**
	 * @param remoteContext
	 *            锁屏apk的上下文环境
	 * */
	public PluginProxy(Context remoteContext) {
		this.mRemoteContext = remoteContext;
		mLockAuthority = mRemoteContext.getPackageName();
	}

	public void copyFinish(Boolean multitasking){
		mCopyJarFinish = multitasking;
		
	}
	
	public void checkHotUpdate() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mRemoteContext);
		if (System.currentTimeMillis() - sp.getLong("hot_post_time", 0) > 2 * 1000 * 60 * 60
				&& sp.getLong("hot_post_time", 0) > 0) {
			Log.v(TAG, "checkHotUpdate");
			postEntity(url, "hotspot","json");
		}
	}

	@Override
	public int execute(String action, final JSONArray args) {
		if (action == null) {
			return -1;
		}
		if (action.equals(ACTION_INIT_JARPLUGIN)) {
			if (mCopyJarFinish) {
				mRemoteContext.sendBroadcast(new Intent(
						ACTION_JAR_COPY_SUCCESS));
			}
			Log.i(TAG, "####### execute ACTION_INIT_JARPLUGIN" + mCopyJarFinish);
		}else if (action.equals(ACTION_CHECK_HOT_UPDATE)) {
			if (!mCopyJarFinish) {
				checkHotUpdate();
			}
			Log.i(TAG, "####### execute ACTION_CHECK_HOT_UPDATE" + mCopyJarFinish);
		}else if (action.equals(ACTION_DOWN_HOT_DATA))  {
			Log.v(TAG, "ACTION_DOWN_HOT_DATA");
			postEntity(url, "hotspot","json");
		}else if (action.equals(ACTION_GET_HOT_DATA)) {
			Intent intent = new Intent(ACTION_GET_JSON_DATA);
			intent.putExtra("js_data", "showHotData");
			intent.putExtra("sp_dir", "json_dir");
			intent.putExtra("js_dir", "json");
			mRemoteContext.sendBroadcast(intent);
		}else if (action.equals(ACTION_DOWN_RING_DATA)) {
			SharedPreferences sharedPreferences = mRemoteContext.getSharedPreferences("Update",
					Context.MODE_PRIVATE);
			Date date = new Date(System.currentTimeMillis());
			String day = String.format("%te", date);
			if (!sharedPreferences.getString("down_rings_time", "").equals(day) ) {
				downRingsFile(mRemoteContext);
			}
		}else if (action.equals(ACTION_SAVE_RING_DATA)) {
			Intent intent = new Intent(ACTION_SAVE_RES_DATA);
			intent.putExtra("down_success", "success_rings");
			intent.putExtra("copy_path", "rings");
			mRemoteContext.sendBroadcast(intent);
		}else if (action.equals(ACTION_GET_RING_DATA)) {
			Intent intent = new Intent(ACTION_GET_RES_DATA);
			intent.putExtra("res_data", "randomRing");
			intent.putExtra("res_dir", "rings");
			intent.putExtra("res_sp", "rings_dir");
			mRemoteContext.sendBroadcast(intent);
		}
		return -1;
	}

	private boolean isUpdate(Context context) {
		SharedPreferences sharedPrefer = context.getSharedPreferences(
				"Update", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefer.edit();
		int curVersion = sharedPrefer.getInt("ringversion", 0);
		HttpURLConnection conn = null;
		InputStream inStream = null;
		try {
			URL url = new URL(downRingsVersionUrl);
			conn = (HttpURLConnection) url.openConnection();
			inStream = conn.getInputStream();
			String strVersion = getString(inStream);
			if (strVersion != null) {
				int updateVersion = Integer.parseInt(strVersion);
				if (updateVersion > curVersion) {
					editor.putInt("ringversion", updateVersion);
					editor.commit();
					return true;
				}
			} else {
				return false;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return false;
	}

	private String getString(InputStream inputStream) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "gbk");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private boolean downloadFile(final String serverUrl,
			String updateDir) throws Exception {
		// 判断文件目录是否存在
		File file = new File(updateDir);
		if (!file.exists()) {
			file.mkdir();
		}
		File updateFile = new File(updateDir + "/rings.zip");
		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream fos = null;
		int readsize = 0;
		boolean isFinish = false;
		try {
			URL url = new URL(serverUrl);
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
				Log.v(TAG,"fileName = "+fileName.toString());
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
					Log.v(TAG, " "+ tempFile.toString());
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
	private synchronized void downRingsFile(final Context context){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (isUpdate(context)) {
						String path = Environment.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/h5lock/"
								+ context.getPackageName();

						boolean downFinish = false;
						Log.v(TAG, "downRingsFile  " + downRingsUrl);
						downFinish = downloadFile(downRingsUrl, path);
						SharedPreferences sharedPrefer = context
								.getSharedPreferences("Update",
										Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPrefer.edit();
						if (downFinish) {
							FileUtils.deleteFile(new File(path + "/rings"));
							File file = new File(path + "/rings.zip");
							unzipFiles(file, path);
							if (file.exists()) {
								file.delete();
							}
							new File(path + "/success_rings").createNewFile();
							Date date = new Date(System.currentTimeMillis());
							String day = String.format("%te", date);
							editor.putString("down_rings_time", day).commit();
							Intent intent = new Intent(ACTION_SAVE_RES_DATA);
							intent.putExtra("down_success", "success_rings");
							intent.putExtra("copy_path", "rings");
							context.sendBroadcast(intent);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private synchronized void postEntity(final String url, final String tag,final String path) {
		new Thread() {
			public void run() {
				try {
					Log.i(TAG, "####### postEntity begin");
					String result = null;
					HttpPost request = new HttpPost(url);
					HttpResponse httpResp = new DefaultHttpClient()
							.execute(request);
					if (httpResp.getStatusLine().getStatusCode() == 200) {
						Log.i(TAG,
								"####### httpResp.getStatusLine().getStatusCode()"
										+ httpResp.getStatusLine()
												.getStatusCode());
						byte[] data = new byte[2048];
						data = EntityUtils.toByteArray((HttpEntity) httpResp
								.getEntity());
						result = new String(data, "UTF-8");
						Log.i(TAG, "service result：" + result);
						dataInsert(mRemoteContext, tag,path, result);
						Intent it = new Intent();
						it.setAction(ACTION_SAVE_JSON_DATA);
						mRemoteContext.sendBroadcast(it);
						Log.v(TAG, result);

						SharedPreferences sp = PreferenceManager
								.getDefaultSharedPreferences(mRemoteContext);
						sp.edit()
								.putLong("hot_post_time",
										System.currentTimeMillis()).commit();
					}
				} catch (Exception e) {
					Log.i(TAG, "####### UnsupportedEncodingException!!!!!!!!!!");
					Log.v("http",
							"UnsupportedEncodingException...." + e.toString());
				}
			};
		}.start();
	}

	private void dataInsert(Context context, String tag, String path,String str) {
		ContentResolver contentResolver = context.getContentResolver();
		Uri insertUri = Uri.parse("content://" + mLockAuthority
				+ ".plugin/jarplugin");
		ContentValues values = new ContentValues();
		values.put("label", tag);
		values.put("path", path);
		values.put("data", str);
		contentResolver.insert(insertUri, values);
	}
	
	@Override
	public void onPause(Boolean multitasking) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onPause " + multitasking.booleanValue());
	}

	@Override
	public void onResume(Boolean multitasking) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onResume " + multitasking.booleanValue());
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onStart ");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onStop ");
	}

	@Override
	public void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onNewIntent intent = " + intent);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "####### onDestroy ");
	}

	@Override
	public void onLauncherLoadFinish() {
		Log.i(TAG, "####### onLauncherLoadFinish ");
	}

	@Override
	public void onActivityResult(Integer requestCode, Integer resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG,
				"####### onActivityResult requestCode = "
						+ requestCode.intValue() + ", resultCode = "
						+ resultCode.intValue() + ", intent = " + intent);
	}

	@Override
	public Boolean shouldAllowRequest(String url) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### shouldAllowRequest url = " + url);
		return null;
	}

	@Override
	public Boolean shouldAllowNavigation(String url) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### shouldAllowNavigation url = " + url);
		return null;
	}

	@Override
	public Boolean shouldOpenExternalUrl(String url) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### shouldOpenExternalUrl url = " + url);
		return null;
	}

	@Override
	public boolean onOverrideUrlLoading(String url) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onOverrideUrlLoading url = " + url);
		return false;
	}

	@Override
	public Uri remapUri(Uri uri) {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### remapUri uri = " + uri);
		return null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageFinishedLoading() {
		// TODO Auto-generated method stub
		Log.i(TAG, "####### onPageFinishedLoading ");
	}

}
