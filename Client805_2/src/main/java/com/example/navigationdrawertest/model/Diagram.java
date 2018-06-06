package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;
/**
 * 示意图数据类
 * @author LYC
 * 2015-12-31---下午1:49:59
 */
public class Diagram extends DataSupport{
	private String diagramId;
	private String userid;
	private String rwid;
	private String taskid;
	private String operationid;
	
	public String getDiagramId() {
		return diagramId;
	}
	public void setDiagramId(String diagramId) {
		this.diagramId = diagramId;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getRwid() {
		return rwid;
	}
	public void setRwid(String rwid) {
		this.rwid = rwid;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	public String getOperationid() {
		return operationid;
	}
	public void setOperationid(String operationid) {
		this.operationid = operationid;
	}
	
}
