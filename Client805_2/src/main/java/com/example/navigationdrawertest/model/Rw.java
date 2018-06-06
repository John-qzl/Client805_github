package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;

public class Rw extends DataSupport{
	private int id;
	private String rwid;
	private String rwname;
	private String postid;
	private String postname;
	private String tableinstanceid;
	private String tablename;
	private String userid;
	private String username;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRwid() {
		return rwid;
	}
	public void setRwid(String rwid) {
		this.rwid = rwid;
	}
	public String getRwname() {
		return rwname;
	}
	public void setRwname(String rwname) {
		this.rwname = rwname;
	}
	public String getPostid() {
		return postid;
	}
	public void setPostid(String postid) {
		this.postid = postid;
	}
	public String getPostname() {
		return postname;
	}
	public void setPostname(String postname) {
		this.postname = postname;
	}
	public String getTableinstanceid() {
		return tableinstanceid;
	}
	public void setTableinstanceid(String tableinstanceid) {
		this.tableinstanceid = tableinstanceid;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
