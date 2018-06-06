package com.example.navigationdrawertest.CustomUI;

public class HeadItemCell extends ItemCell{
	private int width = 0;
	public HeadItemCell(String cellValue,int width){
		super(cellValue,CellTypeEnum.LABEL);
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
}
