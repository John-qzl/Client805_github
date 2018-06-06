package com.example.navigationdrawertest.utils;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jack on 2015/10/20.
 */
public class DateUtil {

	public static final long ONE_DAY_MS = 60 * 60 * 24 * 1000;
	public static final long ONE_DAY_SEC = 60 * 60 * 24;
	public static final long ONE_MINUTE_MS = 60 * 1000;
	public static final long ONE_MINUTE_SEC = 60;

	// 获取明天某个时间
	public static Date tomorrow(int hour, int minute, int second) {
		return tomorrow(-1, hour, minute, second);
	}

	public static Date tomorrow(long date, int hour, int minute, int second) {
		Calendar ca = Calendar.getInstance();
		if (date > -1) {
			ca.setTimeInMillis(date);
		}
		ca.add(Calendar.DAY_OF_YEAR, 1);

		if (hour > -1)
			ca.set(Calendar.HOUR_OF_DAY, hour);
		if (minute > -1)
			ca.set(Calendar.MINUTE, minute);
		if (second > -1)
			ca.set(Calendar.SECOND, second);
		return ca.getTime();
	}

	public static long compareNowInMs(long date) {
		return date - System.currentTimeMillis();
	}

	public static long compareNowInMs(Date date) {
		return date.getTime() - System.currentTimeMillis();
	}

	/**
	 * 获取下周N某个时间
	 * 
	 * @param weekOfDay
	 *            星期�?��1，依次类�?
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date nextWeek(int weekOfDay, int hour, int minute, int second) {
		return nextWeek(-1, hour, minute, second);
	}

	public static Date nextWeek(long date, int weekOfDay, int hour, int minute,
			int second) {
		Calendar ca = Calendar.getInstance();
		if (date > -1) {
			ca.setTimeInMillis(date);
		}
		int week = ca.get(Calendar.DAY_OF_WEEK);
		if (week > 1) {
			ca.add(Calendar.DAY_OF_MONTH, 7 - week + 1 + weekOfDay);
		} else {// 周日
			ca.add(Calendar.DAY_OF_MONTH, weekOfDay);
		}

		if (hour > -1)
			ca.set(Calendar.HOUR_OF_DAY, hour);
		if (minute > -1)
			ca.set(Calendar.MINUTE, minute);
		if (second > -1)
			ca.set(Calendar.SECOND, second);
		return ca.getTime();
	}

	/**
	 * 下个月的某一�?
	 * 
	 * @param monthOfDay
	 *            下个月的某一�?
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date nextMonth(int monthOfDay, int hour, int minute,
			int second) {
		return nextMonth(-1, monthOfDay, hour, minute, second);
	}

	public static Date nextMonth(long date, int monthOfDay, int hour,
			int minute, int second) {
		Calendar ca = Calendar.getInstance();
		if (date > -1) {
			ca.setTimeInMillis(date);
		}
		ca.add(Calendar.MONTH, 1);

		ca.set(Calendar.DAY_OF_MONTH, monthOfDay);
		if (hour > -1)
			ca.set(Calendar.HOUR_OF_DAY, hour);
		if (minute > -1)
			ca.set(Calendar.MINUTE, minute);
		if (second > -1)
			ca.set(Calendar.SECOND, second);
		return ca.getTime();
	}
	
	public static String getCurrentTime(){
//		Date date=new Date();
//		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String time=format.format(date);
//		return time;
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
		int year = c.get(Calendar.YEAR); 
		int month = c.get(Calendar.MONTH); 
		int date = c.get(Calendar.DATE); 
		int hour = c.get(Calendar.HOUR_OF_DAY); 
		int minute = c.get(Calendar.MINUTE); 
		int second = c.get(Calendar.SECOND); 
		int MI = c.get(Calendar.MILLISECOND);
		String time = year + "-" + month + "-" + date + "-" +hour + "-" +minute + "-" + second+"-"+MI;
		return time;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDate(){
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		return time;
	}
	
}
