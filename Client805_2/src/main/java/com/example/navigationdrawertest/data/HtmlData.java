package com.example.navigationdrawertest.data;

import com.example.navigationdrawertest.model.Cell;

public class HtmlData {
	
	private Cell cell;
	private String id;
	
	public HtmlData(Cell cell, String id){
		this.cell = cell;
		this.id = id;
	}
	
	public Cell getCell() {
		return cell;
	}
	public void setCell(Cell cell) {
		this.cell = cell;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
