package com.example.navigationdrawertest.model;

import java.util.List;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Element;

public class Cell extends DataSupport{
	private int id;
	private String cellid;
	private String rowname;		
	private String horizontalorder;			//水平order（row中的id），行的ID
	private String verticalorder;				//垂直order（cell中的order）
	private String type;							//是否是检查项（true/false）
	private String textvalue;					//显示内容
	private String columnid;					//
	private String tablesize;					//表单拆分个数
	private String rowsid;					//拆表id

	private String taskid; 						//外键
	private Task task;
	private List<Operation> operations;
//	private String userid;
	private String mTTID;
	private String ishook;		//是否打钩
	private String opvalue;		//填值内容
	
	private String celltype;
	
	public static String	TAG_Cell = "cell";
	public static String 	Atti_Type = "type";
	public static String 	Atti_ColumnID = "columnid";
	public static String 	Atti_Order = "order";
	public static String 	Atti_TextValue = "textvalue";
	public static String 	Atti_Column = "column";
	
	public static String TYPE_CONTENT 	= "FALSE";
	public static String TYPE_RESULT 		= "TRUE";
	
	public Operation hookOperation;
	public Operation StringOperation;
	public Operation photoOperation;
	public Operation bitmapOperation;
	public String showContent;
	
	public String getCelltype() {
		return celltype;
	}
	public void setCelltype(String celltype) {
		this.celltype = celltype;
	}
	public String getmTTID() {
		return mTTID;
	}
	public void setmTTID(String mTTID) {
		this.mTTID = mTTID;
	}
	public String getShowContent() {
		return showContent;
	}
	public void setShowContent(String showContent) {
		this.showContent = showContent;
	}
	public Operation getHookOperation() {
		return hookOperation;
	}
	public void setHookOperation(Operation hookOperation) {
		this.hookOperation = hookOperation;
	}
	public Operation getStringOperation() {
		return StringOperation;
	}
	public void setStringOperation(Operation stringOperation) {
		StringOperation = stringOperation;
	}
	public Operation getPhotoOperation() {
		return photoOperation;
	}
	public void setPhotoOperation(Operation photoOperation) {
		this.photoOperation = photoOperation;
	}
	public Operation getBitmapOperation() {
		return bitmapOperation;
	}
	public void setBitmapOperation(Operation bitmapOperation) {
		this.bitmapOperation = bitmapOperation;
	}

	/**
	 * 805同步XML
	 * @return
	 */
	public static String Attr_type = "type";
	public static String Attr_column = "column";
	public static String Attr_columnid = "columnid";
	public static String Attr_cellid = "cellid";
	public static String Attr_order = "order";
	public static String Attr_textvalue = "textvalue";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCellid() {
		return cellid;
	}
	public void setCellid(String cellid) {
		this.cellid = cellid;
	}
	public String getRowname() {
		return rowname;
	}
	public void setRowname(String cellname) {
		this.rowname = cellname;
	}
	public String getHorizontalorder() {
		return horizontalorder;
	}
	public void setHorizontalorder(String horizontalorder) {
		this.horizontalorder = horizontalorder;
	}
	public String getVerticalorder() {
		return verticalorder;
	}
	public void setVerticalorder(String verticalorder) {
		this.verticalorder = verticalorder;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTextvalue() {
		return textvalue;
	}
	public void setTextvalue(String textvalue) {
		this.textvalue = textvalue;
	}
	public List<Operation> getOperations() {
		return operations;
	}
	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
//	public String getUserid() {
//		return userid;
//	}
//	public void setUserid(String userid) {
//		this.userid = userid;
//	}


	public String getRowsid() {
		return rowsid;
	}

	public void setRowsid(String rowsid) {
		this.rowsid = rowsid;
	}

	public String getTablesize() {
		return tablesize;
	}

	public void setTablesize(String tablesize) {
		this.tablesize = tablesize;
	}

	public String getColumnid() {
		return columnid;
	}
	public void setColumnid(String columnid) {
		this.columnid = columnid;
	}
	public String getOpvalue() {
		return opvalue;
	}
	public void setOpvalue(String opvalue) {
		this.opvalue = opvalue;
	}
	public String getIshook() {
		return ishook;
	}
	public void setIshook(String ishook) {
		this.ishook = ishook;
	}
	
	public Element setCellElement(Element cellElement, Cell cell)
	{
//		cellElement.setAttribute(Atti_Type, cell.type);
//		cellElement.setAttribute(Atti_Order, cell.verticalorder);
//		cellElement.setAttribute(Atti_TextValue, cell.textvalue);
//		cellElement.setAttribute(Atti_Column, cell.rowname);
		cellElement.setAttribute(Attr_type, cell.getType());
		cellElement.setAttribute(Attr_column, cell.getRowname());
		cellElement.setAttribute(Attr_columnid, cell.getColumnid());
		cellElement.setAttribute(Attr_cellid, cell.getCellid());
		cellElement.setAttribute(Attr_order, cell.getVerticalorder());
		cellElement.setAttribute(Attr_textvalue, cell.getTextvalue());
		
		return cellElement;
	}
	
}
