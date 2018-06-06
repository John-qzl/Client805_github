package com.example.navigationdrawertest.CustomUI;

import java.util.List;

import org.jsoup.nodes.Document;

import com.example.navigationdrawertest.model.Cell;

public class ItemCell {
	
	/**
	 * 新增 2016-3-24 09:38:34
	 */
//	private String labelContent;					//非检查项标签名称
//	private String checkContent;				//打钩检查项的值
//	private String inputContent;					//填值检查项的值
//	private List<String> operationList;		//(分别为1,2,3,4,5)
	private static Document htmlDoc;					//HTML文件
	/**
	 * 新增结束
	 */
	
	private String cellValue;
	
	private int cellSpan = 1; 
	private CellTypeEnum cellType = CellTypeEnum.LABEL; //
	private int colNum = 0;  //
	private Cell cell;
	//private int rowType = 0; //
	
	private boolean isChange = false;//
	public ItemCell(String cellValue, CellTypeEnum cellType, int cellSpan, Cell cell, Document htmlDoc){
		this.cellValue = cellValue;
		this.cellType = cellType;
		this.cellSpan = cellSpan;
		this.cell = cell;
		
		
		this.htmlDoc = htmlDoc;
//		this.labelContent = labelContent;
//		this.checkContent = checkContent;
//		this.inputContent = inputContent;
//		this.operationList = operationList;
	}
	public ItemCell(String cellValue, CellTypeEnum cellType, Cell cell){
		this(cellValue, cellType, 1, cell, htmlDoc);
	}
	public ItemCell(String cellValue, CellTypeEnum cellType){
		this(cellValue, cellType, 1, null, htmlDoc);
	}
	public void setColNum(int colNum){
		this.colNum = colNum;
	}
	public int getColNum(){
		return this.colNum;
	}
//	public void setRowType(int rowType){
//		this.rowType = rowType;
//	}
//	public int getRowType(){
//		return this.rowType;
//	}
	public String getCellValue(){
		return cellValue;
	}
	public void setCellValue(String value){
		this.cellValue = value;
	}
	public CellTypeEnum getCellType(){
		return cellType;
	}
	public int getCellSpan(){
		return cellSpan;
	}
	public void setIsChange(boolean isChange){
		this.isChange = isChange;
	}
	public boolean getIsChange(){
		return this.isChange;
	}
	public Cell getCell() {
		return cell;
	}
	public void setCell(Cell cell) {
		this.cell = cell;
	}
	
//	public String getLabelContent() {
//		return labelContent;
//	}
//	public void setLabelContent(String labelContent) {
//		this.labelContent = labelContent;
//	}
//	public String getCheckContent() {
//		return checkContent;
//	}
//	public void setCheckContent(String checkContent) {
//		this.checkContent = checkContent;
//	}
//	public String getInputContent() {
//		return inputContent;
//	}
//	public void setInputContent(String inputContent) {
//		this.inputContent = inputContent;
//	}
//	public List<String> getOperationList() {
//		return operationList;
//	}
//	public void setOperationList(List<String> operationList) {
//		this.operationList = operationList;
//	}
	public Document getHtmlDoc() {
		return htmlDoc;
	}
	public void setHtmlDoc(Document htmlDoc) {
		this.htmlDoc = htmlDoc;
	}

}
