package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;

public class Product extends DataSupport{
	
	private int id;
	private String product_Id;
	private String product_Name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProduct_Id() {
		return product_Id;
	}
	public void setProduct_Id(String product_Id) {
		this.product_Id = product_Id;
	}
	public String getProduct_Name() {
		return product_Name;
	}
	public void setProduct_Name(String product_Name) {
		this.product_Name = product_Name;
	}
	
}
