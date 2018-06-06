package com.example.navigationdrawertest.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;

import com.example.navigationdrawertest.model.Rw;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.model.User;

public class OrientApplication extends Application {
	public Map<String,Activity> allActivity = new HashMap<String,Activity>();
	public Activity currentActivity;
	public OSetting setting;
	public static OrientApplication app = null;
	public User loginUser = null;								//用于保存当前登录的用户信息
	public boolean bConnenct = false;					//用于保存当前网络连接状态
	public List<String> uploadDownloadList = new ArrayList<String>();		//保存同步时上传下载信息
	public RwRelation rw = null;                                            //用于保存当前任务页面的信息
	public int pageflage;

	public int getPageflage() {
		return pageflage;
	}

	public void setPageflage(int pageflage) {
		this.pageflage = pageflage;
	}

	private OrientApplication()
	{
		setting = new OSetting();
	}
	public static OrientApplication getApplication()
	{
		if(app==null)
		{
			app = new OrientApplication();
		}
		return app;
	}
	
	public void  addActivity(String key,Activity activity)
	{
		Activity act = allActivity.get(key);
		if(act!=null)
		{
			act.finish();
		}
		allActivity.put(key, activity);
	}
	
	public Activity getActivity(String key)
	{
		return	allActivity.get(key);
	}
	
	public void setCurrentActivity(Activity currentActivity)
	{
		this.currentActivity = currentActivity;
	}
	public Activity getCurrentActivity()
	{
		return this.currentActivity;
	}
	
}
