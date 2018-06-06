package com.example.navigationdrawertest.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.example.navigationdrawertest.CustomUI.CellTypeEnum;
import com.example.navigationdrawertest.CustomUI.ItemCell;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.data.AerospaceDB;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Task;
import com.example.navigationdrawertest.utils.CalculateUtil;
import com.example.navigationdrawertest.utils.CommonTools;
import com.example.navigationdrawertest.utils.DateUtil;
import com.example.navigationdrawertest.utils.HtmlHelper;

public class TableThread implements Runnable{

	private int[] arrHeadWidth;
	private Handler mHandler;
	private ArrayList<HashMap<String,Object>> lists;
//	private ListView listView_2;
	private Context context;
	public Document htmlDoc = null;
	private static long task_id;
	private int rowcolumn;			//表格总行数
    private int linecolumn;			//表格总列数
    public Map<Integer , List<Cell>> cells = new HashMap<Integer , List<Cell>>();
	
	public int[] getArrHeadWidth() {
		return arrHeadWidth;
	}

	public void setArrHeadWidth(int[] arrHeadWidth) {
		this.arrHeadWidth = arrHeadWidth;
	}

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public ArrayList<HashMap<String, Object>> getLists() {
		return lists;
	}

	public void setLists(ArrayList<HashMap<String, Object>> lists) {
		this.lists = lists;
	}

//	public ListView getListView_2() {
//		return listView_2;
//	}
//
//	public void setListView_2(ListView listView_2) {
//		this.listView_2 = listView_2;
//	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Document getHtmlDoc() {
		return htmlDoc;
	}

	public void setHtmlDoc(Document htmlDoc) {
		this.htmlDoc = htmlDoc;
	}

	public static long getTask_id() {
		return task_id;
	}

	public static void setTask_id(long task_id) {
		TableThread.task_id = task_id;
	}

	public int getRowcolumn() {
		return rowcolumn;
	}

	public void setRowcolumn(int rowcolumn) {
		this.rowcolumn = rowcolumn;
	}

	public int getLinecolumn() {
		return linecolumn;
	}

	public void setLinecolumn(int linecolumn) {
		this.linecolumn = linecolumn;
	}

	public Map<Integer, List<Cell>> getCells() {
		return cells;
	}

	public void setCells(Map<Integer, List<Cell>> cells) {
		this.cells = cells;
	}

	@Override
	public void run() {
		Log.i("开始时间1", DateUtil.getCurrentTime());
		final HashMap contentMap = new HashMap();
		testAddContent(contentMap);
		Log.i("开始时间2", DateUtil.getCurrentTime());
		
//		adapter = new CustomeTableViewAdapter(context, lists , listView_2 , false , this.arrHeadWidth, mHandler); 
		Log.i("开始时间3", DateUtil.getCurrentTime());
//		adapter.notifyDataSetChanged();                                 
        Message message = new Message(); 
        message.what = 0; 
        Bundle bundle = new Bundle();
        bundle.putIntArray("width", arrHeadWidth);
        message.setData(bundle);
        mHandler.sendMessage(message); 
	}

	private void testAddContent(HashMap contentMap){
//		Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
//    			OrientApplication.getApplication().rw.getRwid(), task_id+"").find(Task.class).get(0);
		Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), task_id+"").find(Task.class).get(0);
		htmlDoc = HtmlHelper.getHtmlDoc(task);
		for(int i=0; i<rowcolumn; i++){						//cells.size()是总行数
			HashMap rowMap = new HashMap();
			lists.add(rowMap);
			for(int j=0; j<linecolumn; j++){			//遍历每一行的内容
//				rowMap.put(j, cells.get(i).get(j));			//绑定UI和数据库，太复杂了
				if(cells.get(i).get(j).getType().equals("FALSE")){			//不是检查项
					Cell cell = cells.get(i).get(j);
//					//1，查找出该行所有的Cell
//					List<Cell> cellList = DataSupport.where("userid = ? and horizontalorder = ? and taskid = ? and type = ?", 
//							OrientApplication.getApplication().loginUser.getUserid(), cell.getHorizontalorder(),
//							cell.getTaskid(), "TRUE").find(Cell.class);
//					//2，查找出该行所有的operation
//					List<Operation> opList = new ArrayList<Operation>();
//					for(int loop=0; loop<cellList.size(); loop++){
//						List<Operation> op = DataSupport.where("userid = ? and cellid = ? and taskid = ?",
//								OrientApplication.getApplication().loginUser.getUserid(), cellList.get(loop).getCellid(),
//								cellList.get(loop).getTaskid()).find(Operation.class);
//						if(op.size() >= 1){		//一个Cell下面有多个operation项
//							for(int k=0; k<op.size(); k++){
//								opList.add(op.get(k));
//							}
//						}
//					}
//					//3，根据operation的5个提示项赋值
//					final StringBuffer str = new StringBuffer();
//					if(opList.size() > 0){
//						boolean opinion1 = false;
//						boolean opinion2 = false;
//						boolean opinion3 = false;
//						boolean opinion4 = false;
//						boolean opinion5 = false;
//						for(int m=0; m<opList.size(); m++){
//							if(opList.get(m).getIldd().equals("TRUE") && opinion1 == false){
//								str.append("1,一类单点;  ");
//								opinion1 = true;
//							}
//							if(opList.get(m).getIildd().equals("TRUE") && opinion2 == false){
//								str.append("2,二类单点;  ");	
//								opinion2 = true;
//							}
//							if(opList.get(m).getErr().equals("TRUE") && opinion3 == false){
//								str.append("3,易错难;  ");	
//								opinion3 = true;
//							}
//							if(opList.get(m).getLastaction().equals("TRUE") && opinion4 == false){
//								str.append("4,最后一次操作;  ");	
//								opinion4 = true;
//							}
//							if(opList.get(m).getTighten().equals("TRUE") && opinion5 == false){
//								str.append("5,拧紧力矩;  ");	
//								opinion5 = true;
//							}
//						}
//						if(str.toString().equals("")){
//							str.append("无");
//						}
//					}
//					cell.setShowContent(str.toString());
					this.testAddRows(rowMap, 1, cells.get(i).get(j).getTextvalue(), CellTypeEnum.LABEL, cell);
				}else if(cells.get(i).get(j).getType().equals("TRUE")){
						List<Operation> operationList = loadOperationByCellId(cells.get(i).get(j).getCellid(), cells.get(i).get(j).getTaskid());
						if(operationList.size() > 1){			//多个operation
							Cell cell = cells.get(i).get(j);
							boolean isPhoto = false;
							boolean isBitmap = false;
//							Operation hookOperation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
//				            		OrientApplication.getApplication().loginUser.getUserid(), cell.getCellid(), "1", cell.getTaskid())
//				            		.find(Operation.class).get(0);
							Operation hookOperation = DataSupport.where("cellid = ? and type = ? and taskid = ?", cell.getCellid(), "1", cell.getTaskid())
				            		.find(Operation.class).get(0);
//							Operation stringOperation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
//				            		OrientApplication.getApplication().loginUser.getUserid(), cell.getCellid(), "2", cell.getTaskid())
//				            		.find(Operation.class).get(0);
							Operation stringOperation = DataSupport.where("cellid = ? and type = ? and taskid = ?", cell.getCellid(), "2", cell.getTaskid())
				            		.find(Operation.class).get(0);
							cell.setHookOperation(hookOperation);
							cell.setStringOperation(stringOperation);
							for(int k=0; k<operationList.size(); k++){
								List<Integer> pows = CalculateUtil.CalculateOperationItem(operationList.get(k).getOperationtype());
								if(pows.contains(128)){
									isPhoto = true;
								}
								if(!operationList.get(k).getSketchmap().equals("") && operationList.get(k).getSketchmap() != null){
									isBitmap = true;
								}
							}
							if(isPhoto && !isBitmap){				//打钩+填值+拍照
								this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKSTRINGPHOTO, cell);
							}else if(!isPhoto && isBitmap){		//打钩+填值+缩略图
								this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKSTRINGBITMAP, cell);
							}else if(isPhoto && isBitmap){		//打钩+填值+拍照+缩略图
								this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKSTRINGPHOTOBITMAP, cell);
							}else{											//打钩+填值
								this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKSTRING, cell);
							}
						}else{
							Cell cell = cells.get(i).get(j);
							boolean isBitmap = false;
							if(!operationList.get(0).getSketchmap().equals("") && operationList.get(0).getSketchmap() != null){
								isBitmap = true;
							}
							if(operationList.get(0).getType().equals("1")){		//打钩
//								Operation hookOperation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
//					            		OrientApplication.getApplication().loginUser.getUserid(), cell.getCellid(), "1", cell.getTaskid())
//					            		.find(Operation.class).get(0);
								Operation hookOperation = DataSupport.where("cellid = ? and type = ? and taskid = ?", cell.getCellid(), "1", cell.getTaskid())
					            		.find(Operation.class).get(0);
								cell.setHookOperation(hookOperation);
								List<Integer> pows = CalculateUtil.CalculateOperationItem(operationList.get(0).getOperationtype());
								if(pows.contains(128) && !isBitmap){				//打钩+拍照
									this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKPHOTO, cell);
								}else if(pows.contains(128) && isBitmap){		//打钩+拍照+缩略图
									this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKPHOTOBITMAP, cell);
								}else if(!pows.contains(128) && isBitmap){	//打钩+缩略图
									this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKBITMAP, cell);
								}else{															//打钩
									this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOK, cell);
								}
							}else if(operationList.get(0).getType().equals("2")){																//填值
//								Operation stringOperation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
//					            		OrientApplication.getApplication().loginUser.getUserid(), cell.getCellid(), "2", cell.getTaskid())
//					            		.find(Operation.class).get(0);
								Operation stringOperation = DataSupport.where("cellid = ? and type = ? and taskid = ?", cell.getCellid(), "2", cell.getTaskid())
					            		.find(Operation.class).get(0);
								cell.setStringOperation(stringOperation);
								List<Integer> pows = CalculateUtil.CalculateOperationItem(operationList.get(0).getOperationtype());
								if(pows.contains(128) && !isBitmap){				//填值+拍照
									this.testAddRows(rowMap, 1, "", CellTypeEnum.STRINGPHOTO, cell);
								}else if(pows.contains(128) && isBitmap){		//填值+拍照+缩略图
									this.testAddRows(rowMap, 1, "", CellTypeEnum.STRINGPHOTOBITMAP, cell);
								}else if(!pows.contains(128) && isBitmap){	//填值+缩略图
									this.testAddRows(rowMap, 1, "", CellTypeEnum.STRINGBITMAP, cell);
								}else{															//填值
									this.testAddRows(rowMap, 1, "", CellTypeEnum.STRING, cell);
								}
							}else{
								this.testAddRows(rowMap, 1, CommonTools.null2String(cells.get(i).get(j).getTextvalue()), CellTypeEnum.LABEL, cell);
							}
						}
				}else{
					Cell cell = cells.get(i).get(j);
					this.testAddRows(rowMap, 1, CommonTools.null2String(cells.get(i).get(j).getTextvalue()), CellTypeEnum.LABEL, cell);
				}
			}
			if(i%2 == 0){
				rowMap.put("rowtype", "css2");
			}else{
				rowMap.put("rowtype", "css3");
			}
		}
	}
	
	private List<Operation> loadOperationByCellId(String cellid, String taskid){
		List<Operation> operationList = new AerospaceDB().loadOperationByCellId(cellid, taskid);
		return operationList;
	}
	
	private void testAddRows(HashMap rowMap, int colSpan, String cellValue, CellTypeEnum cellType, Cell cell){
	    ItemCell itemCell = new ItemCell(cellValue, cellType, colSpan, cell, htmlDoc);	
	    rowMap.put(rowMap.size()+"", itemCell);
	}
	
}
