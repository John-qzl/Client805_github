package com.example.navigationdrawertest.model;

import org.w3c.dom.Element;

public class Row {
	
	public String rowId;
	
	public static String	TAG_Row = "row";
	public static String	TAG_Rows = "rows";

	public static String	Atti_ID = "id";
	public static String	Atti_IsFinished = "isfinished";
	public static String	Flag_True = "true";
	public static String	Flag_False = "false";
	
	/**
	 * 805XML同步属性
	 * @return
	 */
	public static String Attr_rowid = "rowid";
	
	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public Element setRowElement(Element rowElement, Row row)
	{
//		rowElement.setAttribute(Atti_ID, row.rowId);
		rowElement.setAttribute(Attr_rowid, row.getRowId());
		return rowElement;
	}
	
}
