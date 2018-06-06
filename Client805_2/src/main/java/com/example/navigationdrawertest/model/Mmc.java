package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;

public class Mmc extends DataSupport{
	
	private int id;
	private String mmc_Id;
	private String mmc_Name;
	private String gw_Id;					   		//岗位ID，也就是试验队ID
	private String displaypath_Name;			//展示路径
	private String rw_Id;								//任务ID
	private String type;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMmc_Id() {
		return mmc_Id;
	}
	public void setMmc_Id(String mmc_Id) {
		this.mmc_Id = mmc_Id;
	}
	public String getMmc_Name() {
		return mmc_Name;
	}
	public void setMmc_Name(String mmc_Name) {
		this.mmc_Name = mmc_Name;
	}
	public String getGw_Id() {
		return gw_Id;
	}
	public void setGw_Id(String gw_Id) {
		this.gw_Id = gw_Id;
	}
	public String getDisplaypath_Name() {
		return displaypath_Name;
	}
	public void setDisplaypath_Name(String displaypath_Name) {
		this.displaypath_Name = displaypath_Name;
	}
	public String getRw_Id() {
		return rw_Id;
	}
	public void setRw_Id(String rw_Id) {
		this.rw_Id = rw_Id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}