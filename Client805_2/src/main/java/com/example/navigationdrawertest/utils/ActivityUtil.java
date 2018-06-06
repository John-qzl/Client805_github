package com.example.navigationdrawertest.utils;

import android.content.Context;

public class ActivityUtil {
	
	/**
	 * 获取Activity的名称
	 * @param context
	 * @return
	 */
	public static String getActivityName(Context context){
		String[] activityNames = context.toString().split("\\.");
		int activitylocation = activityNames.length-1;
		String activityName = activityNames[activitylocation];
		int activitylocationat = activityName.indexOf("@");
		activityName = activityName.substring(0, activitylocationat);
		return activityName;
	}
	
}
