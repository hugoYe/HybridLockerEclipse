package com.cooee.control.center.module.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class FileUtils {

	/**
	 * 将assets目录下文件夹拷贝到data目录下
	 * */
	public static void copyAssetDirToFiles(Context context, String sourDir)
			throws IOException {
		File dir = new File(context.getFilesDir() + "/" + sourDir);
		dir.mkdir();

		AssetManager assetManager = context.getAssets();
		String[] children = assetManager.list(sourDir);
		for (String child : children) {
			child = sourDir + '/' + child;
			String[] grandChildren = assetManager.list(child);
			if (0 == grandChildren.length)
				copyAssetFileToFiles(context, child);
			else
				copyAssetDirToFiles(context, child);
		}
	}

	public static void copyAssetFileToFiles(Context context, String filename)
			throws IOException {
		InputStream is = context.getAssets().open(filename);
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		is.close();

		File of = new File(context.getFilesDir() + "/" + filename);
		of.createNewFile();
		FileOutputStream os = new FileOutputStream(of);
		os.write(buffer);
		os.close();
	}

	public static void copyAssetDirToFiles(String destDir, Context context,
			String sourDir) throws IOException {

		File dir = new File(destDir + "/" + sourDir);
		dir.mkdir();

		AssetManager assetManager = context.getAssets();
		String[] children = assetManager.list(sourDir);
		for (String child : children) {
			child = sourDir + '/' + child;
			String[] grandChildren = assetManager.list(child);
			if (0 == grandChildren.length)
				copyAssetFileToFiles(destDir, context, child);
			else
				copyAssetDirToFiles(destDir, context, child);
		}
	}

	public static void copyAssetFileToFiles(String destDir, Context context,
			String sourDir) throws IOException {
		InputStream is = context.getAssets().open(sourDir);
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		is.close();

		File of = new File(destDir + "/" + sourDir);
		of.createNewFile();
		FileOutputStream os = new FileOutputStream(of);
		os.write(buffer);
		os.close();
	}

	public static int copySDDirToFiles(String fromFile, String toFile) {
		// 要复制的文件目录
		File[] currentFiles;
		File root = new File(fromFile);
		// 如同判断SD卡是否存在或者文件是否存在
		// 如果不存在则 return出去
		if (!root.exists()) {
			return -1;
		}
		// 如果存在则获取当前目录下的全部文件 填充数组
		currentFiles = root.listFiles();

		// 目标目录
		File targetDir = new File(toFile);
		// 创建目录
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		// 遍历要复制该目录下的全部文件
		for (int i = 0; i < currentFiles.length; i++) {
			if (currentFiles[i].isDirectory())// 如果当前项为子目录 进行递归
			{
				copySDDirToFiles(currentFiles[i].getPath() + "/", toFile + "/"
						+ currentFiles[i].getName() + "/");

			} else// 如果当前项为文件则进行文件拷贝
			{
				CopySdcardFile(currentFiles[i].getPath(), toFile + "/"
						+ currentFiles[i].getName());
			}
		}
		return 0;
	}

	// 文件拷贝
	// 要复制的目录下的所有非子目录(文件夹)文件拷贝
	public static int CopySdcardFile(String fromFile, String toFile) {

		try {
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;

		} catch (Exception ex) {
			return -1;
		}
	}

	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					deleteFile(f);
				}
				file.delete();
			}
		}
	}
}
