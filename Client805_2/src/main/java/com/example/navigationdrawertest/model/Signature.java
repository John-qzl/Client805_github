package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Element;


public class Signature extends DataSupport{
	private int id;
	private String signid;
	private String signname;		//签署名称
	private String signorder;				//签署顺序
	private String time;				//签署时间
	private String remark;
	private String signvalue;				//签署值（改成手写签名要重新设计）
	private String taskid;				//外键
	private Task task;
//	private String userid;
	private String mTTId;
	private String signTime;
	
	
//	public Bitmap bitmaps;
	private String isFinish;		//是否完成
//	public Bitmap getBitmap(){
//		return this.bitmaps;
//	}
//	public void setBitmap(Bitmap bmp){
//		this.bitmaps = bmp;
//	}
	
	//2016-10-31 19:15:18  解决805现场出现的签署乱掉的问题
	private String bitmappath;
	
	public String getBitmappath() {
		return bitmappath;
	}
	public void setBitmappath(String bitmappath) {
		this.bitmappath = bitmappath;
	}

	public static String	TAG_Sign = "sign";
	public static String	TAG_Signs = "signs";
	
	public static String 	Atti_InstanceID = "instanceid";
	public static String 	Atti_Name = "name";
	public static String	Atti_Order = "order";
	public static String	Atti_Value = "value";
	public static String 	Atti_Time = "time";
	public static String 	Atti_PostID = "postid";
	public static String	Atti_Remark = "remark";
	
	/**
	 * 805XML同步属性
	 * @return
	 */
	public static String Tag_Sign = "sign";
	public static String Attr_signId = "signId";
	public static String Attr_name = "name";
	
	public String getSignTime() {
		return signTime;
	}
	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}
	public String getmTTId() {
		return mTTId;
	}
	public void setmTTId(String mTTId) {
		this.mTTId = mTTId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSignid() {
		return signid;
	}
	public void setSignid(String signid) {
		this.signid = signid;
	}
	public String getSignname() {
		return signname;
	}
	public void setSignname(String signname) {
		this.signname = signname;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public String getSignorder() {
		return signorder;
	}
	public void setSignorder(String signorder) {
		this.signorder = signorder;
	}
	public String getSignvalue() {
		return signvalue;
	}
	public void setSignvalue(String signvalue) {
		this.signvalue = signvalue;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
//	public String getUserid() {
//		return userid;
//	}
//	public void setUserid(String userid) {
//		this.userid = userid;
//	}
	public String getIsFinish() {
		return isFinish;
	}
	public void setIsFinish(String isFinish) {
		this.isFinish = isFinish;
	}
	
	public Element setSignNode(Element signElement, Signature sign)
	{
//		signElement.setAttribute(Atti_InstanceID,sign.signid);
//		signElement.setAttribute(Atti_Value, sign.signvalue);
//		signElement.setAttribute(Atti_Order, sign.signorder);
//		signElement.setAttribute(Atti_Name, sign.signname);
//		signElement.setAttribute(Atti_Time, sign.time);
//		signElement.setAttribute(Atti_Remark, sign.remark);
		signElement.setAttribute(Attr_name, sign.getSignname());
		signElement.setAttribute(Attr_signId, sign.getSignid());
		signElement.setAttribute(Atti_Time, sign.getSignTime());
		return signElement;
	}
	
	
}
