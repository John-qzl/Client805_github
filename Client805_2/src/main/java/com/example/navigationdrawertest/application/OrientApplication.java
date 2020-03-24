package com.example.navigationdrawertest.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.example.navigationdrawertest.model.Rw;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.model.User;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class OrientApplication extends Application {
	public Map<String,Activity> allActivity = new HashMap<String,Activity>();
	public Activity currentActivity;
	public OSetting setting;
	public static OrientApplication app = null;
	public User loginUser = null;								//用于保存当前登录的用户信息
	public boolean bConnenct = false;					//用于保存当前网络连接状态
	public List<String> uploadDownloadList = new ArrayList<String>();		//保存同步时上传下载信息
	public List<String> updataInfoList = new ArrayList<String>();		//保存同步时上传下载信息
	public RwRelation rw = null;                                            //用于保存当前任务页面的信息
	public int pageflage;
	private int flag;
	public int panduFlag;
	public int warn;
	private static OrientApplication instance;
	public boolean isCommander;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		initImageLoader(getApplicationContext());
		// android 7.0系统解决拍照的问题
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		builder.detectFileUriExposure();
	}

	public static OrientApplication getInstance() {
		return instance;
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
//		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
//		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config.build());
	}

	public boolean isCommander() {
		return isCommander;
	}

	public void setCommander(boolean commander) {
		isCommander = commander;
	}

	public int getWarn() {
		return warn;
	}

	public void setWarn(int warn) {
		this.warn = warn;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getPanduFlag() {
		return panduFlag;
	}

	public void setPanduFlag(int panduFlag) {
		this.panduFlag = panduFlag;
	}

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
