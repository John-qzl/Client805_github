package com.example.navigationdrawertest.model;

public class Header{
	
	private int id;
	private String headerid;
	private String headername;
	private String headernum;					//列号
	private String headerwidth;					//宽度占比例
	private String headerealwidth;				//计算出来的真是宽度
	private String checkTempId;
	
	public String getHeaderealwidth() {
		return headerealwidth;
	}
	public void setHeaderealwidth(String headerealwidth) {
		this.headerealwidth = headerealwidth;
	}
	public String getCheckTempId() {
		return checkTempId;
	}
	public void setCheckTempId(String checkTempId) {
		this.checkTempId = checkTempId;
	}
	public String getHeaderwidth() {
		return headerwidth;
	}
	public void setHeaderwidth(String headerwidth) {
		this.headerwidth = headerwidth;
	}
	public String getHeadernum() {
		return headernum;
	}
	public void setHeadernum(String headernum) {
		this.headernum = headernum;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHeaderid() {
		return headerid;
	}
	public void setHeaderid(String headerid) {
		this.headerid = headerid;
	}
	public String getHeadername() {
		return headername;
	}
	public void setHeadername(String headername) {
		this.headername = headername;
	}
	
}
