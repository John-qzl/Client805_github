package com.example.navigationdrawertest.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.model.Task;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class CommonUtil {
	
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		return status.equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取最大内存
	 * 
	 * @return
	 */
	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory() / 1024;
	}

	/**
	 * 检查网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetState(Context context) {
		boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
	}

	public static void showToast(Context context, String tip) {
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}

	public static DisplayMetrics metric = new DisplayMetrics();

	/**
	 * 得到屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Activity context) {
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	/**
	 * 得到屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Activity context) {
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	/**
	 * 获取版本号和版本次数
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context, int type) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			if (type == 1) {
				return String.valueOf(pi.versionCode);
			} else {
				return pi.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String searchFile(String filepath) {
		String result = ""; 
		File[] files = new File(filepath).listFiles(); 
		for (File file : files) {
			if (file.getName() != null && !"".equals(file.getName())) { 
				result += file.getPath() + "?"; 
			} 
		}
		if (result.equals("")){
			result = ""; 
		}
		return result; 
	}
	
	
	// 将表格实例的HTML转化成字符串
	public static String ConverHtmlToString(Task task) {
//		String filePath = Environment.getDataDirectory().getPath() + Config.packagePath
//				+ Config.htmlPath+ "/"+ task.getUserid()+"/" + task.getTaskid();
		String filePath = Environment.getDataDirectory().getPath() + Config.packagePath
				+ Config.htmlPath+ "/"+ task.getPostname()+"/" + task.getTaskid();
//		String filePath = Environment.getDataDirectory().getPath()+ "/" + Config.htmlPath+ "/"+ task.getUserid()+"/" + task.getTaskid()+"/";
		return FileOperation.fileReader(filePath);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
//	public static int dip2px(Context context, float dpValue) {
//		final float scale = context.getResources().getDisplayMetrics().density;
//		return (int) (dpValue * scale + 0.5f);
//	}



	/**
	 * md5加密
	 */
	public static String md5(Object object) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(toByteArray(object));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10) hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	public static byte[] toByteArray (Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray ();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}



	/**
	 * 获取存储路径
	 */
	public static String getDataPath() {
		String path;
		if (isExistSDcard())
			path = Environment.getExternalStorageDirectory().getPath() + "/albumSelect";
		else
			path = OrientApplication.getInstance().getFilesDir().getPath();
		if (!path.endsWith("/"))
			path = path + "/";
		return path;
	}


	/**
	 * 检测SDcard是否存在
	 *
	 * @return
	 */
	public static boolean isExistSDcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED))
			return true;
		else {
			return false;
		}
	}

	/**
	 * MD5 加密
	 */
	public static String toMD5(String data) {

		try {
			// 实例化一个指定摘要算法为MD5的MessageDigest对象
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			// 重置摘要以供再次使用
			md5.reset();
			// 使用bytes更新摘要
			md5.update(data.getBytes());
			// 使用指定的byte数组对摘要进行最的更新，然后完成摘要计算
			return toHexString(md5.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	// 将字符串中的每个字符转换为十六进制
	private static String toHexString(byte[] bytes) {

		StringBuilder hexstring = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xFF & b);
			if (hex.length() == 1) {
				hexstring.append('0');
			}
			hexstring.append(hex);

		}

		return hexstring.toString();
	}
}
