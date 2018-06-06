package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;

/**
 * 任务关系表，用于抽屉框架初始化时数据填充
 * 目前的主要关系是任务~人员，1~N，
 * @author liuyangchao
 *	2015-12-2 上午11:29:47
 */
public class RwRelation extends DataSupport{
	private int id;
	private String rwid;
	private String rwname;
	private String userid;
	private String username;
	private String productid; 		//产品ID
	
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
	public String getRwname() {
		return rwname;
	}
	public void setRwname(String rwname) {
		this.rwname = rwname;
	}
	public String getProductid() {
		return productid;
	}
	public void setProductid(String productid) {
		this.productid = productid;
	}
	
}
