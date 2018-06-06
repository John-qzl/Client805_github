package com.example.navigationdrawertest.utils;

import java.io.File;

import android.os.Environment;

/**
 * 下载文件配置项
 * @author Administrator
 *	2017-1-11
 *	上午9:21:47
 */
public class Setting {
	
	public static final String PHOTO_NAME_SUFFIX = ".jpg";
	
	/**
	 * 加密文件
	 */
	public static String FILE_SECRET_START = Environment.getExternalStorageDirectory()
			+ File.separator + "mmccopy";
	
	/**
	 * 解密文件
	 */
	public static String FILE_SECRET_END = Environment.getExternalStorageDirectory()
			+ File.separator + "mmc";
	
	
	
}
