package com.example.navigationdrawertest.utils;

import org.xmlpull.v1.XmlPullParser;

import com.example.navigationdrawertest.R;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class Config{
	
	public String ip=null;
	public String port=null;
	public static String rwphotoPath = "/805/files/rwphotopath";		//任务级别拍照路径
	public static String tablephotoPath = "/805/files/tablephotopath";				//检查项级别拍照路径
	public static String tableallphotoPath = "/805/files/tableallphotoPath";		//检查表格总级别拍照路径
	public static String htmlPath = "/805/files/tablehtml";					//检查表格HTML保存路径
	public static String packagePath = "/data/com.example.navigationdrawertest";			//包路径
//	public static String packagePath = "/activity";			//包路径
	public static String signphotoPath = "/805/files/signphoto";			//检查表格签署照片路径
	public static String hangsignphotoPath = "/805/files/hangsignphoto";			//行签署签署照片路径
	public static String opphotoPath = "/805/files/opphoto";					//检查项照片路径
	public static String refphotoPath = "/805/files/refphoto";					//示意图照片路径

	public static String requireval = "requireval";//要求值
	public static String upper = "upper";//上偏差
	public static String lower = "lower";//下偏差
	public static String actualval = "actualval";//实测值
	public static String compliance = "compliance";//符合度
	public static String fuhe = "符合";//符合度
	public static String bufuhe = "不符合";//符合度

	public static String v2photoPath = "/805/files/v2p";					//操作项拍照文件夹
	public static String mmcPath = "/mmccopy";					//操作项拍照文件夹
	public static String rootPath = "/805/files";					//操作项拍照文件夹
	public static String apkPath = "/805/files/apk";					//操作项拍照文件夹
	public static String normalTaskId = "10000123456789";//通用TaskID
	public static String HTMLHEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<html>\n" +
			"\t<table width=\"100%\" class=\"layui-table\">\n" +
			"\t\t<tbody>";//HTML头信息
	public static String HTMLTR = "10000123456789";//HTML行信息tr
	public static String firstRow = "class=\"firstRow\" style=\"background:rgb(160,182,204)\"";//HTML行信息tr
	public static String HTMLTD= "10000123456789";//HTML单元格td信息
	public static String HTMLEND = "</tbody>\n" +
			"\t</table>\n" +
			"</html>";//HTML尾信息

	public String getIpAndPort(Context context)
	{
		try {
			Resources r = context.getResources();
			int i = R.xml.config;
			/*
			 * The XML parsing interface returned for an XML resource. This is a standard XmlPullParser interface, 
			 * as well as an extended AttributeSet interface and an additional close() method on this interface for the client to indicate when it is done reading the resource.
			 */
			XmlResourceParser xrp = r.getXml(i);
			while(true)
			{
				/*
				 * Returns the type of the current event (START_TAG, END_TAG, TEXT, etc.)
				 */
				if(xrp.getEventType() == XmlPullParser.START_TAG)//遇到开始标签
				{
					String targName = xrp.getName();//获取标签名
					System.out.println(targName);
					if(targName.equals("user"))//所有的用户标签都是user
					{
						ip = xrp.getAttributeValue(null,"ipAddress").toString();//获得第一属性：IP地址
						System.out.println(ip);
						port = xrp.getAttributeValue(null,"port").toString();//获得第二属性:端口号
						System.out.println(port);
						break;
					}
				}
				xrp.next();//匹配IP和Port不成功，则查看下一个标签信息
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return ip+":"+port;
	}
	
	//用来设置系统当前时间，暂时没有用到
	public static boolean  setSystemTime(String time)
	{
		return false;
	}
}
