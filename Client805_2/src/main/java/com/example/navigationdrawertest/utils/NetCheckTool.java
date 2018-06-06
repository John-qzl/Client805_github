package com.example.navigationdrawertest.utils;

import com.example.navigationdrawertest.application.OrientApplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

/**
 * @author Administrator
 * 网络检查的工具类
 */
public class NetCheckTool {
	
	ConnectivityManager cMgr = null;
	
	
	
	public static boolean check(Context context)
	{
		ConnectivityManager cMgr  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo  info = cMgr.getActiveNetworkInfo();
		if(info==null)
		{
			return false;
		}
		OrientApplication.getApplication().bConnenct = info.isConnected();
		return info.isConnected();
	
	}


}
