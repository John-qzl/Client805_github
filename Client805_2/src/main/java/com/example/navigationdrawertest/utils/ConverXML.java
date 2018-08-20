package com.example.navigationdrawertest.utils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.data.AerospaceDB;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Row;
import com.example.navigationdrawertest.model.Rows;
import com.example.navigationdrawertest.model.Scene;
import com.example.navigationdrawertest.model.Signature;
import com.example.navigationdrawertest.model.Task;

/**
 * @author LYC
 * 2015-12-11---上午9:22:26
 * XML转换工具类
 */
public class ConverXML {
	private static AerospaceDB aerospacedb;
	private static int rowCount = 0;			//行数
	private static int cellCount = 0;				//列数
	private static List<Cell> cellList;				//本表格所有的CELL集合
	public static long task_id;
	private static List<Cell> headMap = new ArrayList<Cell>();			//head的Cell集合
	
	public static String ConverTaskToXml(Task task)
	{
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;
		task_id = Long.parseLong(task.getTaskid());
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			
			// 添加currentTaskMap下面的标签
			Element taskElement = doc.createElement(Task.TAG_Task);				//Task标签
			task.setTaskNode(taskElement, task);
		    doc.appendChild(taskElement);
			 
		    //添加Conditon标签
		    Element conditionsElement = doc.createElement(Scene.TAG_Conditions);		//Condition标签
		    taskElement.appendChild(conditionsElement);
			//确定签署项要素：人员--表格
//			List<Signature> signList = DataSupport.where("userid = ? and taskid = ?", 
//					task.getUserid(), task.getTaskid()).find(Signature.class);
			List<Scene> conditionList = DataSupport.where("mTTId = ? and taskid = ?", 
					task.getPostinstanceid(), task.getTaskid()).find(Scene.class);
			for(int i=0; i<conditionList.size(); i++){
				Scene condition = conditionList.get(i);
				Element conditionElement = doc.createElement(Scene.TAG_Condition);	//sign标签
				condition.setConditionNode(conditionElement, condition);
				conditionsElement.appendChild(conditionElement);
			}
		    
		    
			Element signsElement = doc.createElement(Signature.TAG_Signs);		//Signs标签
			taskElement.appendChild(signsElement);
			//确定签署项要素：人员--表格
//			List<Signature> signList = DataSupport.where("userid = ? and taskid = ?", 
//					task.getUserid(), task.getTaskid()).find(Signature.class);
			List<Signature> signList = DataSupport.where("mTTId = ? and taskid = ?", 
					task.getPostinstanceid(), task.getTaskid()).find(Signature.class);
			for(int i=0; i<signList.size(); i++){
				Signature sign = signList.get(i);
				Element signElement = doc.createElement(Signature.TAG_Sign);	//sign标签
				sign.setSignNode(signElement, sign);
				signsElement.appendChild(signElement);
			}
			
			Element rowsElement = doc.createElement(Row.TAG_Rows);				//Rows标签
			taskElement.appendChild(rowsElement);

			//乔志理 修改rows number  修复在老版本上已采集数据，还没上传服务器的情况下，数据只传一行的问题
			//rowsnum1是task中的简单表检查项行数，rowsnum2是当前表的实际行数
			int rownum1 = Integer.parseInt(task.getRownum());
			int rownum2 = getrowsnumber();
			int rownum3;
			if (rownum1 == rownum2) {
				rownum3 = rownum1;
			} else {
				rownum3 = rownum2;
			}

			for(int i=0; i<rownum3; i++){
				Row row = new Row();
				row.setRowId(i+1+"");
				Element rowElement = doc.createElement(Row.TAG_Row);			//Row标签
				row.setRowElement(rowElement, row);
				rowsElement.appendChild(rowElement);
				//确定一行Cell集合的要素:人员--表格--行号
//				List<Cell> cellLists = DataSupport.where("userid = ? and taskid = ? and horizontalorder = ?", 
//						task.getUserid(), task.getTaskid(), row.getRowId()).find(Cell.class);
				List<Cell> cellLists = DataSupport.where("mTTID = ? and taskid = ? and horizontalorder = ?", 
						task.getPostinstanceid(), task.getTaskid(), row.getRowId()).find(Cell.class);
				for(int j=0; j<cellLists.size(); j++){
					Cell cell = cellLists.get(j);
					Element cellElement = doc.createElement(Cell.TAG_Cell);				//Cell标签
					cell.setCellElement(cellElement, cell);
					rowElement.appendChild(cellElement);
					//确定一个Cell下所有Operation的要素：人员--表格--CellId
//					List<Operation> opLists = DataSupport.where("userid=? and taskid = ? and cellid = ?", 
//							task.getUserid(), task.getTaskid(), cell.getCellid()).find(Operation.class);
					List<Operation> opLists = DataSupport.where("mTTID=? and taskid = ? and cellid = ?", 
							task.getPostinstanceid(), task.getTaskid(), cell.getCellid()).find(Operation.class);
					for(int m=0; m<opLists.size(); m++){
						 Operation operator = opLists.get(m);
						 Element operationElement = doc.createElement(Operation.TAG_Operation);		//operation标签
						 operator.setOperationElement(operationElement, operator);
						 cellElement.appendChild(operationElement);
					}
				}
			}
			
		    DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            OutputStreamWriter write = new OutputStreamWriter(outStream);
            Result result = new StreamResult(write);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, result);
            return outStream.toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static int getrowsnumber() {
		cellList = loadCellAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid(), 1);
		headMap = DataSupport.where("horizontalorder=? and taskid=? and rowsid=?", "1", task_id + "", "1").order("verticalorder asc").find(Cell.class);
		cellCount = headMap.size();
		rowCount = cellList.size()/cellCount;
		return rowCount;
	}
	//加载Table数据
	private static List<Cell> loadCellAdapter(long taskid, String userid, int pagetype){
		aerospacedb = new AerospaceDB();
		List<Cell> cellList = aerospacedb.loadTableAdapter(taskid, userid, pagetype);
		return cellList;
	}
	
	
}
