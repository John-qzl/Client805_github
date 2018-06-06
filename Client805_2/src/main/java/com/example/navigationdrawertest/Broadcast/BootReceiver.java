package com.example.navigationdrawertest.Broadcast;

import java.io.File;

import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.utils.FileOperation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 接收安装广播
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString();
			System.out.println("安装了:" + packageName + "包名的程序");
			Log.i("安裝", packageName);
		}
		// 接收卸载广播
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			String packageName = intent.getDataString();
			String sign_dir = Environment.getExternalStorageDirectory() + File.separator+OrientApplication.getApplication()
					.loginUser.getUserid();
			File file = new File(sign_dir);
			System.out.println("卸载了:" + packageName + "包名的程序");
			FileOperation.RecursionDeleteFile(file);
			Log.i("卸載", packageName);
		}
	}
}