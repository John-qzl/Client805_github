package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Element;

public class Scene extends DataSupport{
	private int id;
	private String conditionid;
	private String conditionname;
//	private String sceneorder;
	private int sceneorder;
	private String scenevalue;
	private String taskid;			//外键
	private Task task;
//	private String userid;
	private String mTTID;


	private Long timeL;				//表单复制时的时间戳

	public Long getTimeL() {
		return timeL;
	}

	public void setTimeL(Long timeL) {
		this.timeL = timeL;
	}


	public static String TAG_Conditions = "conditions";
	public static String TAG_Condition = "condition";
	
	public static String Atti_conditionId = "conditionId";
	public static String Atti_conditionname = "conditionname";
	public static String Atti_valuename = "valuename";
	public static String Atti_order = "order";

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
	public String getConditionid() {
		return conditionid;
	}
	public void setConditionid(String conditionid) {
		this.conditionid = conditionid;
	}
	public String getConditionname() {
		return conditionname;
	}
	public void setConditionname(String conditionname) {
		this.conditionname = conditionname;
	}
	
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	
	public String getScenevalue() {
		return scenevalue;
	}
	public int getSceneorder() {
		return sceneorder;
	}
	public void setSceneorder(int sceneorder) {
		this.sceneorder = sceneorder;
	}
	public void setScenevalue(String scenevalue) {
		this.scenevalue = scenevalue;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	
	
	public Element setConditionNode(Element conditionElement, Scene condition)
	{
		conditionElement.setAttribute(Atti_conditionId, condition.getConditionid());
		conditionElement.setAttribute(Atti_valuename, condition.getScenevalue());
		return conditionElement;
	}
	
}
