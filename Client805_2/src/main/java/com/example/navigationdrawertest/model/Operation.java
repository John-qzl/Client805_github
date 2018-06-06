package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Element;

public class Operation extends DataSupport{
	private int id;
	private String operationid;
	private String type;
//	private String value;
	private String opvalue;
	private String remark;
	private String isfinished;			//是否是打钩项
	private String textvalue;			//填入的文本值
//	private String userid;
	private String mTTID;			//试验队
	
	
	private String operationtype;
//	private String operationPic;		//单独一列的缩略图，最高优先级
	
	private String ildd;		//一类单点
	private String iildd;		//二类单点
	private String tighten;	//拧紧力矩
	private String err;			//易错项
	private String lastaction;	//最后一次动作
	private String ismedia;	//是否是多媒体项
	private String sketchmap;			//检查项示意图

	private String cellid;				//外键
	private String realcellid;		//真实对应的cell外键
	private Cell cell;
	private String taskid;				//表格外键
	private String time;
	
	public static String TAG_Operation = "operation";
	public static String Atti_Type = "type";
	public static String Atti_OperationType = "operationtype";
	public static String Atti_TextValue = "textvalue";
	public static String Atti_CellID = "cellid";
	public static String Atti_ResultID = "resultid";
	public static String Atti_Remark = "remark";
	public static String Atti_Value = "value";
	public static String Atti_IsFinished = "isfinished";
	public static String Atti_Time = "time";
	public static String Atti_Pic = "operationPic";
	public static String Atti_PhotoPic = "uploadpic";
	public static String Flag_True = "true";
	public static String Flag_False = "false";
	
	public static int OP_TYPE_CHECK = 1;
	public static int OP_TYPE_VALUE = 2;
	public static int OP_TYPE_REMARK = 3;
	
	public static int OP_CATEGORY_COMMON = 1;
	public static int OP_CATEGORY_SINGLE = 2;//I类单点
	public static int OP_CATEGORY_DOUBLE = 64;//II类单点
	public static int OP_CATEGORY_HARD= 16;//难
	public static int OP_CATEGORY_MOMENT= 32;//拧紧力矩
	public static int OP_CATEGORY_WARNNING= 4;//易错项
	public static int OP_CATEGORY_PHOTO = 128;//照相
	
	/**
	 * 805XML同步属性
	 * @return
	 */
	public static String Attr_type = "type";
	public static String Attr_cellid = "cellid";
	public static String Attr_operationtype = "operationtype";
	public static String Attr_realcellid = "realcellid";
	public static String Attr_resultid = "resultid";
	public static String Attr_value = "value";
	
	public String getmTTID() {
		return mTTID;
	}
	public void setmTTID(String mTTID) {
		this.mTTID = mTTID;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOperationid() {
		return operationid;
	}
	public void setOperationid(String operationid) {
		this.operationid = operationid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getIsfinished() {
		return isfinished;
	}
	public void setIsfinished(String isfinished) {
		this.isfinished = isfinished;
	}
	public String getTextvalue() {
		return textvalue;
	}
	public void setTextvalue(String textvalue) {
		this.textvalue = textvalue;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Cell getCell() {
		return cell;
	}
	public void setCell(Cell cell) {
		this.cell = cell;
	}
	public String getCellid() {
		return cellid;
	}
	public void setCellid(String cellid) {
		this.cellid = cellid;
	}
//	public String getUserid() {
//		return userid;
//	}
//	public void setUserid(String userid) {
//		this.userid = userid;
//	}
	public String getOperationtype() {
		return operationtype;
	}
	public void setOperationtype(String operationtype) {
		this.operationtype = operationtype;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	
	public Element setOperationElement(Element operationElement,Operation operation)
	{
//		operationElement.setAttribute(Atti_OperationType, operation.operationtype);
//		operationElement.setAttribute(Atti_Type, operation.type);
//		operationElement.setAttribute(Atti_CellID, operation.cellid);
//		operationElement.setAttribute(Atti_Value, operation.value);
//		operationElement.setAttribute(Atti_Remark, operation.remark);
//		operationElement.setAttribute(Atti_Time, operation.time);
//		operationElement.setAttribute(Atti_Pic, operation.operationPic);
		
		operationElement.setAttribute(Attr_type, operation.getType());
		operationElement.setAttribute(Attr_cellid, operation.getCellid());
		operationElement.setAttribute(Attr_operationtype, operation.getOperationtype());
		operationElement.setAttribute(Attr_realcellid, operation.getRealcellid());
		operationElement.setAttribute(Attr_resultid, operation.getOperationid());
		operationElement.setAttribute(Attr_value, operation.getOpvalue());
		return operationElement;
	}
	
	public String getRealcellid() {
		return realcellid;
	}
	public void setRealcellid(String realcellid) {
		this.realcellid = realcellid;
	}
	public String getOpvalue() {
		return opvalue;
	}
	public void setOpvalue(String opvalue) {
		this.opvalue = opvalue;
	}

	public String getIldd() {
		return ildd;
	}
	public void setIldd(String ildd) {
		this.ildd = ildd;
	}
	public String getIildd() {
		return iildd;
	}
	public void setIildd(String iildd) {
		this.iildd = iildd;
	}
	public String getTighten() {
		return tighten;
	}
	public void setTighten(String tighten) {
		this.tighten = tighten;
	}
	public String getErr() {
		return err;
	}
	public void setErr(String err) {
		this.err = err;
	}
	public String getLastaction() {
		return lastaction;
	}
	public void setLastaction(String lastaction) {
		this.lastaction = lastaction;
	}
	public String getIsmedia() {
		return ismedia;
	}
	public void setIsmedia(String ismedia) {
		this.ismedia = ismedia;
	}
	public String getSketchmap() {
		return sketchmap;
	}
	public void setSketchmap(String sketchmap) {
		this.sketchmap = sketchmap;
	}
	
}
