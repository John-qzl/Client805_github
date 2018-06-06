package com.example.navigationdrawertest.CustomUI;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.SweetAlert.SweetAlertDialog;
import com.example.navigationdrawertest.activity.BitmapDialogActivity;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.camera.AlbumAty;
import com.example.navigationdrawertest.camera.CameraAty;
import com.example.navigationdrawertest.customInterface.CheckedChangeInterface;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Task;
import com.example.navigationdrawertest.utils.ActivityUtil;
import com.example.navigationdrawertest.utils.CommonTools;
import com.example.navigationdrawertest.utils.Config;
import com.example.navigationdrawertest.utils.DateUtil;
import com.example.navigationdrawertest.utils.HtmlHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CustomeTableItem extends LinearLayout {
	private Context context = null;
	private boolean isRead = false;//是否只读
	private ArrayList<View> viewList = new ArrayList();//行的表格列表
	private int[] headWidthArr = null;//表头的列宽设置
	private String rowType = "0";//行的样式id
	public Document htmlDoc = null;				//该表格的HTML网页
	private String location;					//能否更改数据内容,1能，2不能
	ImageLoaderConfiguration config;
	
	public CustomeTableItem(Context context) {
		super(context);
	}
	public CustomeTableItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public CustomeTableItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
//	/*
//	 * rowType:行的样式，字符任意，相同样式的行不需要再创建了
//	 * itemCells:单元格信息
//	 * headWidthArr:每列宽度
//	 * isRead:是否只读，如果是只读，则所有的输入都不生效
//	 */
//    public void buildItem(Context context,String rowType,ArrayList<ItemCell> itemCells
//    		,int[] headWidthArr,boolean isRead, String location){
//		this.setOrientation(LinearLayout.VERTICAL);//第一层布局垂直
//    	this.context = context;
//    	this.headWidthArr =headWidthArr.clone();
//        this.rowType = rowType;
//        this.location = location;
//    	this.addCell(itemCells);
//    	config = ImageLoaderConfiguration.createDefault(context);
//    }
//    
//    private CheckedChangeInterface checkinterface;					//Hook项，checkbox自定义事件
//    private void addCell(ArrayList<ItemCell> itemCells){
//    	this.removeAllViews();
//    	LinearLayout secondLayout = new LinearLayout(context);
//		secondLayout.setOrientation(LinearLayout.HORIZONTAL);
//		secondLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
//		this.addView(secondLayout);
//    	int cellIndex = 0;
//    	for(int i=0;i<itemCells.size();i++){
//    		final ItemCell itemCell = itemCells.get(i);
//			final int endIndex = cellIndex+itemCell.getCellSpan();//所占行数
//			final int endlie = i;														//列号
//			int width = getCellWidth(cellIndex,endIndex);//行宽度
//			htmlDoc = itemCell.getHtmlDoc();
//			if(itemCells.size()>8){
//				width = 200;
//			}
////			int width = 600;
//			cellIndex = endIndex;
//	    	if(itemCell.getCellType()==CellTypeEnum.STRING){
//	    		final EditText view= (EditText)getInputView();
//	    		String activityName = ActivityUtil.getActivityName(context);
////	    		String stringdata = itemCell.getCell().getOpvalue();
////	    		Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////	            		.find(Operation.class).get(0);
////	    		String stringdata = operation.getOpvalue();
//	    		String stringdata = itemCell.getCell().getOpvalue();
////	    		if(null == itemCell.getCell().getOpvalue() || "".equals(stringdata)){
//	    		if(null == stringdata || "".equals(stringdata)){
//	    			view.setText("");
//	    		}else{
//	    			view.setText(stringdata);
//	    		}
//	    		view.addTextChangedListener(new TextWatcher() {  
//	    	        @Override    
//	    	        public void afterTextChanged(Editable s) {     
//	    	            // TODO Auto-generated method stub     
//	    	        }   
//	    	        @Override 
//	    	        public void beforeTextChanged(CharSequence s, int start, int count,  
//	    	                int after) {  
//	    	            // TODO Auto-generated method stub  
//	    	        }  
//	    	        @Override    
//	    	        public void onTextChanged(CharSequence s, int start, int before, int count) { 
////	    	        	Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	    	    				OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    	        	htmlDoc = HtmlHelper.getHtmlDoc(task);
//	    	            String str = view.getText().toString();  
//	    	            itemCell.getCell().setOpvalue(str);
//	    	            itemCell.getCell().save();
////	    	            Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////	    	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////	    	            		.find(Operation.class).get(0);
//	    	            Operation operation = itemCell.getCell().getStringOperation();
//	    	            operation.setOpvalue(str);
//	    	            operation.save();
//	    	            HtmlHelper.changeTextValue(htmlDoc, itemCell.getCell());
//	    	        }                    
//	    		});
////	    		if(activityName.equals("CheckActivity")){
////	    			view.setEnabled(true);
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			view.setEnabled(false);
////	    		}
//	    		if(location.equals("1")){
//	    			view.setEnabled(true);
//	    		}else{
//	    			view.setEnabled(false);
//	    		}
//				view.setWidth(width);
//				view.setHeight(80);
//				this.setEditView(view);
//				secondLayout.addView(view);
//				viewList.add(view);
//	    	}else if(itemCell.getCellType()==CellTypeEnum.DIGIT){
//	    		EditText view= (EditText)getInputView();
//				view.setText(itemCell.getCellValue());
//				view.setWidth(width);
//				view.setHeight(80);
//				this.setEditView(view);
//				this.setOnKeyBorad(view);
//				secondLayout.addView(view);
//				viewList.add(view);
//	    	}else if(itemCell.getCellType()==CellTypeEnum.LABEL){
//	    		final TextView view = (TextView)getLabelView();
//				view.setText(Html.fromHtml(itemCell.getCellValue()));
//				view.setWidth(width);
//				view.setHeight(80);
//				secondLayout.addView(view);
////				//1，查找出该行所有的Cell
////				List<Cell> cellList = DataSupport.where("userid = ? and horizontalorder = ? and taskid = ? and type = ?", 
////						OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getHorizontalorder(),
////						itemCell.getCell().getTaskid(), "TRUE").find(Cell.class);
////				//2，查找出该行所有的operation
////				List<Operation> opList = new ArrayList<Operation>();
////				for(int j=0; j<cellList.size(); j++){
////					List<Operation> op = DataSupport.where("userid = ? and cellid = ? and taskid = ?",
////							OrientApplication.getApplication().loginUser.getUserid(), cellList.get(j).getCellid(),
////							cellList.get(j).getTaskid()).find(Operation.class);
////					if(op.size() >= 1){		//一个Cell下面有多个operation项
////						for(int k=0; k<op.size(); k++){
////							opList.add(op.get(k));
////						}
////					}
////				}
////				//3，根据operation的5个提示项赋值
////				final StringBuffer str = new StringBuffer();
////				if(opList.size() > 0){
////					boolean opinion1 = false;
////					boolean opinion2 = false;
////					boolean opinion3 = false;
////					boolean opinion4 = false;
////					boolean opinion5 = false;
////					for(int m=0; m<opList.size(); m++){
////						if(opList.get(m).getIldd().equals("TRUE") && opinion1 == false){
////							str.append("1,一类单点;  ");
////							opinion1 = true;
////						}
////						if(opList.get(m).getIildd().equals("TRUE") && opinion2 == false){
////							str.append("2,二类单点;  ");	
////							opinion2 = true;
////						}
////						if(opList.get(m).getErr().equals("TRUE") && opinion3 == false){
////							str.append("3,易错难;  ");	
////							opinion3 = true;
////						}
////						if(opList.get(m).getLastaction().equals("TRUE") && opinion4 == false){
////							str.append("4,最后一次操作;  ");	
////							opinion4 = true;
////						}
////						if(opList.get(m).getTighten().equals("TRUE") && opinion5 == false){
////							str.append("5,拧紧力矩;  ");	
////							opinion5 = true;
////						}
////					}
////					if(str.toString().equals("")){
////						str.append("无");
////					}
////				}
//				
//				view.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						//1，查找出该行所有的Cell
////						List<Cell> cellList = DataSupport.where("userid = ? and horizontalorder = ? and taskid = ? and type = ?", 
////								OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getHorizontalorder(),
////								itemCell.getCell().getTaskid(), "TRUE").find(Cell.class);
//						List<Cell> cellList = DataSupport.where("horizontalorder = ? and taskid = ? and type = ?", itemCell.getCell().getHorizontalorder(),
//								itemCell.getCell().getTaskid(), "TRUE").find(Cell.class);
//						//2，查找出该行所有的operation
//						List<Operation> opList = new ArrayList<Operation>();
//						for(int j=0; j<cellList.size(); j++){
////							List<Operation> op = DataSupport.where("userid = ? and cellid = ? and taskid = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), cellList.get(j).getCellid(),
////									cellList.get(j).getTaskid()).find(Operation.class);
//							List<Operation> op = DataSupport.where("cellid = ? and taskid = ?", cellList.get(j).getCellid(),
//									cellList.get(j).getTaskid()).find(Operation.class);
//							if(op.size() >= 1){		//一个Cell下面有多个operation项
//								for(int k=0; k<op.size(); k++){
//									opList.add(op.get(k));
//								}
//							}
//						}
//						//3，根据operation的5个提示项赋值
//						final StringBuffer str = new StringBuffer();
//						if(opList.size() > 0){
//							boolean opinion1 = false;
//							boolean opinion2 = false;
//							boolean opinion3 = false;
//							boolean opinion4 = false;
//							boolean opinion5 = false;
//							for(int m=0; m<opList.size(); m++){
//								if(opList.get(m).getIldd().equals("TRUE") && opinion1 == false){
//									str.append("1,一类单点;  ");
//									opinion1 = true;
//								}
//								if(opList.get(m).getIildd().equals("TRUE") && opinion2 == false){
//									str.append("2,二类单点;  ");	
//									opinion2 = true;
//								}
//								if(opList.get(m).getErr().equals("TRUE") && opinion3 == false){
//									str.append("3,易错难;  ");	
//									opinion3 = true;
//								}
//								if(opList.get(m).getLastaction().equals("TRUE") && opinion4 == false){
//									str.append("4,最后一次操作;  ");	
//									opinion4 = true;
//								}
//								if(opList.get(m).getTighten().equals("TRUE") && opinion5 == false){
//									str.append("5,拧紧力矩;  ");	
//									opinion5 = true;
//								}
//							}
//							if(str.toString().equals("")){
//								str.append("无");
//							}
//						}
//						new SweetAlertDialog(view.getContext())
//		                .setTitleText("注意事项!")
//		                .setContentText(str.toString())
//		                .show();
//					}
//				});
//				
//				viewList.add(view);
//	    	}else if(itemCell.getCellType()==CellTypeEnum.HOOK){
//	    		CheckBox view = (CheckBox)getCheckBoxView();
//	    		String activityName = ActivityUtil.getActivityName(context);
//	    		view.setWidth(width);
//				view.setHeight(80);
////				Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//				Operation operation = itemCell.getCell().getHookOperation();
//				String value = CommonTools.null2String(operation.getOpvalue());
//				if(value != null){
//					if(value.equals("is")){				//打钩状态
//						view.setChecked(true);
//					}else if(value.equals("no")){	//不打钩状态
//						view.setChecked(false);
//					}else{
//						view.setChecked(false);
//					}
//				}
//				view.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
//			        @Override  
//			        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
//			            // TODO Auto-generated method stub  
//			        	if(checkinterface!=null){
//			        		checkinterface.onCheckedChanged(buttonView, isChecked, itemCell.getCell(), endIndex, endlie);
//			        	}
//			        }  
//			    });
//				setMyCheckedChangeListener(new CheckedChangeInterface(){
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked, Cell cell, int x, int y) {
//						// TODO Auto-generated method stub
//						if(isChecked){
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////		    	    				OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//							Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////		    	        	htmlDoc = HtmlHelper.getHtmlDoc(task);
//							cell.setIshook("is");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////		    	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////		    	            		.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//		    	            operation.setOpvalue("is");
//		    	            operation.save();
//		    	            String firsttime = task.getStartTime();
//		    	            if(firsttime == null || firsttime.equals("")){
//		    	            	task.setStartTime(DateUtil.getCurrentDate());
//		    	            	task.save();
//		    	            }
//		    	            HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//		    	            //记录第一次操作的时间
//		    	            
//		    	            
//							Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show(); 
//						}else{
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////		    	    				OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////		    	        	htmlDoc = HtmlHelper.getHtmlDoc(task);
//		    	        	cell.setIshook("no");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////		    	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////		    	            		.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//		    	            operation.setOpvalue("no");
//		    	            operation.save();
//		    	            HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//							Toast.makeText(context, "未选中", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
////				if(activityName.equals("CheckActivity")){
////					view.setEnabled(true);
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			view.setEnabled(false);
////	    		}
//				if(location.equals("1")){
//	    			view.setEnabled(true);
//	    		}else{
//	    			view.setEnabled(false);
//	    		}
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//	    	}else if(itemCell.getCellType()==CellTypeEnum.HOOKSTRING){
//	    		LinearLayout view = (LinearLayout) gethookstringview();
//	    		CheckBox checkbox = (CheckBox) view.findViewById(R.id.ownerhookstring_check);
//	    		final EditText edittext = (EditText) view.findViewById(R.id.ownerhookstring_edittext);
//	    		String activityName = ActivityUtil.getActivityName(context);
////	    		Operation operation1 = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		Operation operation1 = itemCell.getCell().getHookOperation();
//				String value = operation1.getOpvalue();
//	    		if(value != null){
//					if(value.equals("is")){				//打钩状态
//						checkbox.setChecked(true);
//					}else if(value.equals("no")){	//不打钩状态
//						checkbox.setChecked(false);
//					}else{
//						checkbox.setChecked(false);
//					}
//				}
//	    		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
//			        @Override  
//			        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
//			            // TODO Auto-generated method stub  
//			        	if(checkinterface!=null){
//			        		checkinterface.onCheckedChanged(buttonView, isChecked, itemCell.getCell(), endIndex, endlie);
//			        	}
//			        }  
//			    });
//				setMyCheckedChangeListener(new CheckedChangeInterface(){
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked, Cell cell, int x, int y) {
//						// TODO Auto-generated method stub
//						if(isChecked){  
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////									OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//							Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////							htmlDoc = HtmlHelper.getHtmlDoc(task);
//							cell.setIshook("is");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1")
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("is");
//							operation.save();
//							String firsttime = task.getStartTime();
//		    	            if(firsttime == null || firsttime.equals("")){
//		    	            	task.setStartTime(DateUtil.getCurrentDate());
//		    	            	task.save();
//		    	            }
//							HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//							Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show(); 
//						}else{
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////									OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////							htmlDoc = HtmlHelper.getHtmlDoc(task);
//							cell.setIshook("no");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1")
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("no");
//							operation.save();
//							HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//							Toast.makeText(context, "未选中", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
////				Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////	            		.find(Operation.class).get(0);
//				Operation operation = itemCell.getCell().getStringOperation();
//	    		String stringdata = operation.getOpvalue();
//	    		if(null == itemCell.getCell().getOpvalue() || "".equals(stringdata)){
//	    			edittext.setText("");
//	    		}else{
//	    			edittext.setText(stringdata);
//	    		}
//	    		edittext.addTextChangedListener(new TextWatcher() {  
//	    	        @Override    
//	    	        public void afterTextChanged(Editable s) {     
//	    	            // TODO Auto-generated method stub     
//	    	        }   
//	    	        @Override 
//	    	        public void beforeTextChanged(CharSequence s, int start, int count,  
//	    	                int after) {  
//	    	            // TODO Auto-generated method stub  
//	    	        }  
//	    	        @Override    
//	    	        public void onTextChanged(CharSequence s, int start, int before, int count) {     
////	    	        	Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	    	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//	    	        	Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    	        	htmlDoc = HtmlHelper.getHtmlDoc(task);
//	    	        	String str = edittext.getText().toString();  
//	    	        	itemCell.getCell().setOpvalue(str);
//	    	        	itemCell.getCell().save();
////	    	        	Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////	    	        			OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////	    	        			.find(Operation.class).get(0);
//	    	        	Operation operation = itemCell.getCell().getStringOperation();
//	    	        	operation.setOpvalue(str);
//	    	        	operation.save();
//	    	        	String firsttime = task.getStartTime();
//	    	            if(firsttime == null || firsttime.equals("")){
//	    	            	task.setStartTime(DateUtil.getCurrentDate());
//	    	            	task.save();
//	    	            }
//	    	        	HtmlHelper.changeTextValue(htmlDoc, itemCell.getCell());
//	    	        }                    
//	    		});
////	    		if(activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(true);
////	    			edittext.setEnabled(true);
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(false);
////	    			edittext.setEnabled(false);
////	    		}
//	    		if(location.equals("1")){
//	    			checkbox.setEnabled(true);
//	    			edittext.setEnabled(true);
////	    			view.setEnabled(true);
//	    		}else{
//	    			checkbox.setEnabled(false);
//	    			edittext.setEnabled(false);
////	    			view.setEnabled(false);
//	    		}
//	    		LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//	    	}else if(itemCell.getCellType()==CellTypeEnum.HOOKPHOTO){
//	    		LinearLayout view = (LinearLayout) gethookphoto();
//	    		CheckBox checkbox = (CheckBox) view.findViewById(R.id.ownerhookphoto_check);
//	    		Button button = (Button) view.findViewById(R.id.ownerhookphoto_photo);
//	    		String activityName = ActivityUtil.getActivityName(context);
//	    		
////	    		final Operation operation1 = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		final Operation operation1 = itemCell.getCell().getHookOperation();
//				String value = operation1.getOpvalue();
//	    		if(value != null){
//					if(value.equals("is")){				//打钩状态
//						checkbox.setChecked(true);
//					}else if(value.equals("no")){	//不打钩状态
//						checkbox.setChecked(false);
//					}else{
//						checkbox.setChecked(false);
//					}
//				}
//	    		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
//			        @Override  
//			        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
//			            // TODO Auto-generated method stub  
//			        	if(checkinterface!=null){
//			        		checkinterface.onCheckedChanged(buttonView, isChecked, itemCell.getCell(), endIndex, endlie);
//			        	}
//			        }  
//			    });
//	    		setMyCheckedChangeListener(new CheckedChangeInterface(){
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked, Cell cell, int x, int y) {
//						// TODO Auto-generated method stub
//						if(isChecked){  
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////									OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//							Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////							htmlDoc = HtmlHelper.getHtmlDoc(task);
//							cell.setIshook("is");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), cell.getCellid(), "1", cell.getTaskid())
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("is");
//							operation.save();
//							String firsttime = task.getStartTime();
//		    	            if(firsttime == null || firsttime.equals("")){
//		    	            	task.setStartTime(DateUtil.getCurrentDate());
//		    	            	task.save();
//		    	            }
//							HtmlHelper.changeCheckValue(htmlDoc, cell);
//							Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show(); 
//						}else{
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////									OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////							htmlDoc = HtmlHelper.getHtmlDoc(task);
//							cell.setIshook("no");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), cell.getCellid(), "1", cell.getTaskid())
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("no");
//							operation.save();
//							HtmlHelper.changeCheckValue(htmlDoc, cell);
//							Toast.makeText(context, "未选中", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
//	    		final String userId = OrientApplication.getApplication().loginUser.getUserid();
//	    		final String taskId = itemCell.getCell().getTaskid();
//	    		final String rowNum = itemCell.getCell().getHorizontalorder();
//	    		final String cellId = itemCell.getCell().getCellid();
////	    		if(activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(true);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							HtmlHelper.changePhotoValue(htmlDoc, operation1);
////							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
////									rowNum, cellId);
////						}
////		    		});
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(false);
////	    			button.setBackgroundResource(R.drawable.photoalbum);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
////									OrientApplication.getApplication().rw.getRwid());
////						}
////		    		});
////	    		}
//	    		if(location.equals("1")){
//	    			checkbox.setEnabled(true);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							HtmlHelper.changePhotoValue(htmlDoc, operation1);
//							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
//									rowNum, cellId);
//						}
//		    		});
//	    		}else{
//	    			checkbox.setEnabled(false);
//	    			button.setBackgroundResource(R.drawable.photoalbum);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
//						}
//		    		});
//	    		}
//	    		
//	    		LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//	    	}else if(itemCell.getCellType()==CellTypeEnum.STRINGPHOTO){
//	    		LinearLayout view = (LinearLayout) getstringphoto();
//	    		final EditText edittext = (EditText) view.findViewById(R.id.ownerstringphoto_edittext);
//	    		Button button = (Button) view.findViewById(R.id.ownerstringphoto_photo);
////	    		final Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////	            		.find(Operation.class).get(0);
//	    		final Operation operation = itemCell.getCell().getStringOperation();
////	    		final Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    		htmlDoc = HtmlHelper.getHtmlDoc(task);
//	    		String stringdata = operation.getOpvalue();
//	    		if(null == itemCell.getCell().getOpvalue() || "".equals(stringdata)){
//	    			edittext.setText("");
//	    		}else{
//	    			edittext.setText(stringdata);
//	    		}
//	    		edittext.addTextChangedListener(new TextWatcher() {  
//	    	        @Override    
//	    	        public void afterTextChanged(Editable s) {     
//	    	            // TODO Auto-generated method stub     
//	    	        }   
//	    	        @Override 
//	    	        public void beforeTextChanged(CharSequence s, int start, int count,  
//	    	                int after) {  
//	    	            // TODO Auto-generated method stub  
//	    	        }  
//	    	        @Override    
//	    	        public void onTextChanged(CharSequence s, int start, int before, int count) {   
//	    	        	String str = edittext.getText().toString();  
//	    	        	itemCell.getCell().setOpvalue(str);
//	    	        	itemCell.getCell().save();
////	    	        	Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	    	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//	    	        	Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    	        	Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////	    	        			OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////	    	        			.find(Operation.class).get(0);
//	    	        	Operation operation = itemCell.getCell().getStringOperation();
//	    	        	operation.setOpvalue(str);
//	    	        	operation.save();
//	    	        	String firsttime = task.getStartTime();
//	    	            if(firsttime == null || firsttime.equals("")){
//	    	            	task.setStartTime(DateUtil.getCurrentDate());
//	    	            	task.save();
//	    	            }
//	    	        	HtmlHelper.changeTextValue(htmlDoc, itemCell.getCell());
//	    	        }                    
//	    		});
//	    		String activityName = ActivityUtil.getActivityName(context);
//	    		final String userId = OrientApplication.getApplication().loginUser.getUserid();
//	    		final String taskId = itemCell.getCell().getTaskid();
//	    		final String rowNum = itemCell.getCell().getHorizontalorder();
//	    		final String cellId = itemCell.getCell().getCellid();
////	    		if(activityName.equals("CheckActivity")){
////	    			edittext.setEnabled(true);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							HtmlHelper.changePhotoValue(htmlDoc, operation);
////							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
////									rowNum, cellId);
////						}
////		    		});
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			edittext.setEnabled(false);
////	    			button.setBackgroundResource(R.drawable.photoalbum);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
////									OrientApplication.getApplication().rw.getRwid());
////						}
////		    		});
////	    		}
//	    		if(location.equals("1")){
//	    			edittext.setEnabled(true);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							HtmlHelper.changePhotoValue(htmlDoc, operation);
//							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
//									rowNum, cellId);
//						}
//		    		});
//	    		}else{
//	    			edittext.setEnabled(false);
//	    			button.setBackgroundResource(R.drawable.photoalbum);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
//						}
//		    		});
//	    		}
//	    		
//	    		LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//	    	}else if(itemCell.getCellType()==CellTypeEnum.HOOKSTRINGPHOTO){
//	    		LinearLayout view = (LinearLayout) gethookstringphoto();
//	    		CheckBox checkbox = (CheckBox) view.findViewById(R.id.ownerhookstringphoto_check);
//	    		final EditText edittext = (EditText) view.findViewById(R.id.ownerhookstringphoto_edittext);
//	    		Button button = (Button) view.findViewById(R.id.ownerhookstringphoto_photo);
//	    		String activityName = ActivityUtil.getActivityName(context);
////	    		Operation operation1 = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		Operation operation1 = itemCell.getCell().getHookOperation();
////	    		final Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    		htmlDoc = HtmlHelper.getHtmlDoc(task);
//				String value = operation1.getOpvalue();
//	    		if(value != null){
//					if(value.equals("is")){				//打钩状态
//						checkbox.setChecked(true);
//					}else if(value.equals("no")){	//不打钩状态
//						checkbox.setChecked(false);
//					}else{
//						checkbox.setChecked(false);
//					}
//				}
//	    		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
//			        @Override  
//			        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
//			            // TODO Auto-generated method stub  
//			        	if(checkinterface!=null){
//			        		checkinterface.onCheckedChanged(buttonView, isChecked, itemCell.getCell(), endIndex, endlie);
//			        	}
//			        }  
//			    });
//				setMyCheckedChangeListener(new CheckedChangeInterface(){
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked, Cell cell, int x, int y) {
//						// TODO Auto-generated method stub
//						if(isChecked){  
//							itemCell.getCell().setIshook("is");
//							itemCell.getCell().save();
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////				        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//							Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("is");
//							operation.save();
//							String firsttime = task.getStartTime();
//		    	            if(firsttime == null || firsttime.equals("")){
//		    	            	task.setStartTime(DateUtil.getCurrentDate());
//		    	            	task.save();
//		    	            }
//							HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//							Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show(); 
//						}else{
//							cell.setIshook("no");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("no");
//							operation.save();
//							HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//							Toast.makeText(context, "未选中", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
//				final String userId = OrientApplication.getApplication().loginUser.getUserid();
//	    		final String taskId = itemCell.getCell().getTaskid();
//	    		final String rowNum = itemCell.getCell().getHorizontalorder();
//	    		final String cellId = itemCell.getCell().getCellid();
//	    		button.setOnClickListener(new OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						CameraAty.actionStart(context, userId, taskId, 
//								OrientApplication.getApplication().rw.getRwid(), rowNum, cellId);
//					}
//	    		});
////				final Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		final Operation operation = itemCell.getCell().getStringOperation();
//	    		String stringdata = operation.getOpvalue();
//	    		if(null == itemCell.getCell().getOpvalue() || "".equals(stringdata)){
//	    			edittext.setText("");
//	    		}else{
//	    			edittext.setText(stringdata);
//	    		}
//	    		edittext.addTextChangedListener(new TextWatcher() {  
//	    	        @Override    
//	    	        public void afterTextChanged(Editable s) {     
//	    	            // TODO Auto-generated method stub     
//	    	        }   
//	    	        @Override 
//	    	        public void beforeTextChanged(CharSequence s, int start, int count,  
//	    	                int after) {  
//	    	            // TODO Auto-generated method stub  
//	    	        }  
//	    	        @Override    
//	    	        public void onTextChanged(CharSequence s, int start, int before, int count) {     
//	    	        	String str = edittext.getText().toString();  
//	    	        	itemCell.getCell().setOpvalue(str);
//	    	        	itemCell.getCell().save();
////	    	        	Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	    	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//	    	        	Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    	        	Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	    	        			OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2", itemCell.getCell().getTaskid())
////	    	        			.find(Operation.class).get(0);
//	    	        	Operation operation = itemCell.getCell().getStringOperation();
//	    	        	operation.setOpvalue(str);
//	    	        	operation.save();
//	    	        	String firsttime = task.getStartTime();
//	    	            if(firsttime == null || firsttime.equals("")){
//	    	            	task.setStartTime(DateUtil.getCurrentDate());
//	    	            	task.save();
//	    	            }
//	    	        	HtmlHelper.changeTextValue(htmlDoc, itemCell.getCell());
//	    	        }                    
//	    		});
////	    		if(activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(true);
////	    			edittext.setEnabled(true);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							if(htmlDoc != null)
////								HtmlHelper.changePhotoValue(htmlDoc, operation);
////							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
////									rowNum, cellId);
////						}
////		    		});
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(false);
////	    			edittext.setEnabled(false);
////	    			button.setBackgroundResource(R.drawable.photoalbum);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
////									OrientApplication.getApplication().rw.getRwid());
////						}
////		    		});
////	    		}
//	    		if(location.equals("1")){
//	    			checkbox.setEnabled(true);
//	    			edittext.setEnabled(true);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							if(htmlDoc != null)
//								HtmlHelper.changePhotoValue(htmlDoc, operation);
//							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
//									rowNum, cellId);
//						}
//		    		});
//	    		}else{
//	    			checkbox.setEnabled(false);
//	    			edittext.setEnabled(false);
//	    			button.setBackgroundResource(R.drawable.photoalbum);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
//						}
//		    		});
//	    		}
//	    		
//	    		LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//	    	}else if(itemCell.getCellType()==CellTypeEnum.HOOKBITMAP){
//	    		
//	    		String activityName = ActivityUtil.getActivityName(context);
////	    		Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		Operation operation = itemCell.getCell().getHookOperation();
//	    		final String userId = OrientApplication.getApplication().loginUser.getUserid();
//	    		final String taskId = itemCell.getCell().getTaskid();
//	    		final String opId = operation.getSketchmap();
//	    		String value = operation.getOpvalue();
//	    		LinearLayout view = (LinearLayout) getstringbitmap();
//	    		CheckBox checkbox = (CheckBox) view.findViewById(R.id.ownerhookbitmap_check);
//	    		ImageView imageview = (ImageView) view.findViewById(R.id.ownerhookbitmap_bitmap);
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/"+ userId+"/" + taskId + "/" + opId + ".jpg";
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/"+ taskId + "/" + opId + ".jpg";
//	    		final String absPath = Environment.getExternalStorageDirectory()
//						+ Config.opphotoPath + "/"
//						+ taskId + "/" + opId + ".jpg";
//	    		Bitmap bitmap = BitmapFactory.decodeFile(absPath);
//	    		if(bitmap != null)
//	    			imageview.setImageBitmap(bitmap);
//					imageview.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							BitmapDialogActivity.actionStart(context, userId, taskId, opId);
//					}
//				});
//				if(value != null){
//	    			if(value.equals("is")){				//打钩状态
//	    				checkbox.setChecked(true);
//	    			}else if(value.equals("no")){	//不打钩状态
//	    				checkbox.setChecked(false);
//	    			}else{
//	    				checkbox.setChecked(false);
//	    			}
//	    		}
//				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
//	    			@Override  
//	    			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
//	    				// TODO Auto-generated method stub  
//	    				if(checkinterface!=null){
//	    					checkinterface.onCheckedChanged(buttonView, isChecked, itemCell.getCell(), endIndex, endlie);
//	    				}
//	    			}  
//	    		});
//	    		setMyCheckedChangeListener(new CheckedChangeInterface(){
//	    			@Override
//	    			public void onCheckedChanged(CompoundButton buttonView,
//	    					boolean isChecked, Cell cell, int x, int y) {
//	    				// TODO Auto-generated method stub
//	    				if(isChecked){
////	    					Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	    							OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//	    					Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    					htmlDoc = HtmlHelper.getHtmlDoc(task);
//	    					cell.setIshook("is");
//	    					cell.save();
////	    					Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	    							OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	    							.find(Operation.class).get(0);
//	    					Operation operation = itemCell.getCell().getHookOperation();
//	    					operation.setOpvalue("is");
//	    					operation.save();
//	    					String firsttime = task.getStartTime();
//		    	            if(firsttime == null || firsttime.equals("")){
//		    	            	task.setStartTime(DateUtil.getCurrentDate());
//		    	            	task.save();
//		    	            }
//	    					HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//	    					Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show(); 
//	    				}else{
////	    					Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	    							OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    					htmlDoc = HtmlHelper.getHtmlDoc(task);
//	    					cell.setIshook("no");
//	    					cell.save();
////	    					Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	    							OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	    							.find(Operation.class).get(0);
//	    					Operation operation = itemCell.getCell().getHookOperation();
//	    					operation.setOpvalue("no");
//	    					operation.save();
//	    					HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//	    					Toast.makeText(context, "未选中", Toast.LENGTH_SHORT).show();
//	    				}
//	    			}
//	    		});
////	    		if(activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(true);
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(false);
////	    		}
//	    		if(location.equals("1")){
//	    			checkbox.setEnabled(true);
//	    		}else{
//	    			checkbox.setEnabled(false);
//	    		}
//	    		
//				
//				LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//	    		
//	    	}else if(itemCell.getCellType()==CellTypeEnum.HOOKSTRINGBITMAP){
//	    		
//	    		String activityName = ActivityUtil.getActivityName(context);
////	    		List<Operation> operationList = DataSupport.where("userid = ? and cellid = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), itemCell.getCell().getTaskid())
////	            		.find(Operation.class);
//	    		List<Operation> operationList = DataSupport.where("cellid = ? and taskid = ?", itemCell.getCell().getCellid(), itemCell.getCell().getTaskid())
//	            		.find(Operation.class);
//	    		final String userId = OrientApplication.getApplication().loginUser.getUserid();
//	    		final String taskId = itemCell.getCell().getTaskid();
//	    		String sketchmapId = "";
//	    		for(int ii=0; ii<operationList.size(); ii++){
//	    			String sketchmap = operationList.get(ii).getSketchmap();
//	    			if(!sketchmap.equals("") && sketchmap != null){
//	    				sketchmapId = sketchmap;
//	    				break;
//	    			}
//	    		}
//	    		LinearLayout view = (LinearLayout) getstringbitmap();
//	    		CheckBox checkbox = (CheckBox) view.findViewById(R.id.ownerhookstringbitmap_check);
//	    		final EditText edittext = (EditText) view.findViewById(R.id.ownerhookstringbitmap_edittext);
//	    		ImageView imageview = (ImageView) view.findViewById(R.id.ownerhookstringbitmap_bitmap);
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/"+ userId+"/" + taskId + "/" + sketchmapId + ".jpg";
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/" + taskId + "/" + sketchmapId + ".jpg";
//	    		final String absPath = Environment.getExternalStorageDirectory()
//						+ Config.opphotoPath + "/"
//						+ taskId + "/" + sketchmapId + ".jpg";
//	    		Bitmap bitmap = BitmapFactory.decodeFile(absPath);
//	    		if(bitmap != null)
//	    			imageview.setImageBitmap(bitmap);
//	    			int startlocation = absPath.lastIndexOf("/");
//	    			int endlocation = absPath.lastIndexOf(".jpg");
//	    			final String opId = absPath.substring(startlocation+1,endlocation);
//					imageview.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							BitmapDialogActivity.actionStart(context, userId, taskId, opId);
//					}
//				});
////				Operation operation1 = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////						OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////						.find(Operation.class).get(0);
//				Operation operation1 = itemCell.getCell().getHookOperation();
//				String value = operation1.getOpvalue();
//				if(value != null){
//					if(value.equals("is")){				//打钩状态
//						checkbox.setChecked(true);
//					}else if(value.equals("no")){	//不打钩状态
//						checkbox.setChecked(false);
//					}else{
//						checkbox.setChecked(false);
//					}
//				}
//				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
//					@Override  
//					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
//						// TODO Auto-generated method stub  
//						if(checkinterface!=null){
//							checkinterface.onCheckedChanged(buttonView, isChecked, itemCell.getCell(), endIndex, endlie);
//						}
//					}  
//				});
//				setMyCheckedChangeListener(new CheckedChangeInterface(){
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked, Cell cell, int x, int y) {
//						// TODO Auto-generated method stub
//						if(isChecked){  
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////									OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//							Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////							htmlDoc = HtmlHelper.getHtmlDoc(task);
//							cell.setIshook("is");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1")
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("is");
//							operation.save();
//							String firsttime = task.getStartTime();
//		    	            if(firsttime == null || firsttime.equals("")){
//		    	            	task.setStartTime(DateUtil.getCurrentDate());
//		    	            	task.save();
//		    	            }
//							HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//							Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show(); 
//						}else{
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////									OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////							htmlDoc = HtmlHelper.getHtmlDoc(task);
//							cell.setIshook("no");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1")
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("no");
//							operation.save();
//							HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//							Toast.makeText(context, "未选中", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
////				Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////						OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////						.find(Operation.class).get(0);
//				Operation operation = itemCell.getCell().getStringOperation();
//				String stringdata = operation.getOpvalue();
//				if(null == itemCell.getCell().getOpvalue() || "".equals(stringdata)){
//					edittext.setText("");
//				}else{
//					edittext.setText(stringdata);
//				}
//				edittext.addTextChangedListener(new TextWatcher() {  
//					@Override    
//					public void afterTextChanged(Editable s) {     
//						// TODO Auto-generated method stub     
//					}   
//					@Override 
//					public void beforeTextChanged(CharSequence s, int start, int count,  
//							int after) {  
//						// TODO Auto-generated method stub  
//					}  
//					@Override    
//					public void onTextChanged(CharSequence s, int start, int before, int count) {     
////						Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////								OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//						Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////						htmlDoc = HtmlHelper.getHtmlDoc(task);
//						String str = edittext.getText().toString();  
//						itemCell.getCell().setOpvalue(str);
//						itemCell.getCell().save();
////						Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////								OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////								.find(Operation.class).get(0);
//						Operation operation = itemCell.getCell().getStringOperation();
//						operation.setOpvalue(str);
//						operation.save();
//						String firsttime = task.getStartTime();
//	    	            if(firsttime == null || firsttime.equals("")){
//	    	            	task.setStartTime(DateUtil.getCurrentDate());
//	    	            	task.save();
//	    	            }
//						HtmlHelper.changeTextValue(htmlDoc, itemCell.getCell());
//					}                    
//				});
////				if(activityName.equals("CheckActivity")){
////					checkbox.setEnabled(true);
////					edittext.setEnabled(true);
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(false);
////					edittext.setEnabled(false);
////	    		}
//	    		if(location.equals("1")){
//	    			checkbox.setEnabled(true);
//					edittext.setEnabled(true);
//	    		}else{
//	    			checkbox.setEnabled(false);
//					edittext.setEnabled(false);
//	    		}
//				
//					
//				LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//	    		
//	    	}else if(itemCell.getCellType()==CellTypeEnum.HOOKSTRINGPHOTOBITMAP){
//	    		String activityName = ActivityUtil.getActivityName(context);
////	    		List<Operation> operationList = DataSupport.where("userid = ? and cellid = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), itemCell.getCell().getTaskid())
////	            		.find(Operation.class);
//	    		List<Operation> operationList = DataSupport.where("cellid = ? and taskid = ?", itemCell.getCell().getCellid(), itemCell.getCell().getTaskid())
//	            		.find(Operation.class);
////	    		final Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    		htmlDoc = HtmlHelper.getHtmlDoc(task);
//	    		final String userId = OrientApplication.getApplication().loginUser.getUserid();
//	    		final String taskId = itemCell.getCell().getTaskid();
//	    		String sketchmapId = "";
//	    		for(int ii=0; ii<operationList.size(); ii++){
//	    			String sketchmap = operationList.get(ii).getSketchmap();
//	    			if(!sketchmap.equals("") && sketchmap != null){
//	    				sketchmapId = sketchmap;
//	    				break;
//	    			}
//	    		}
//	    		LinearLayout view = (LinearLayout) getstringbitmap();
//	    		CheckBox checkbox = (CheckBox) view.findViewById(R.id.ownerhookstringphotobitmap_check);
//	    		Button button = (Button) view.findViewById(R.id.ownerhookstringphotobitmap_photo);
//	    		final EditText edittext = (EditText) view.findViewById(R.id.ownerhookstringphotobitmap_edittext);
//	    		ImageView imageview = (ImageView) view.findViewById(R.id.ownerhookstringphotobitmap_bitmap);
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/"+ userId+"/" + taskId + "/" + sketchmapId + ".jpg";
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/" + taskId + "/" + sketchmapId + ".jpg";
//	    		final String absPath = Environment.getExternalStorageDirectory()
//						+ Config.opphotoPath + "/"
//						+ taskId + "/" + sketchmapId + ".jpg";
//	    		Bitmap bitmap = BitmapFactory.decodeFile(absPath);
//	    		if(bitmap != null)
//	    			imageview.setImageBitmap(bitmap);
//	    			int startlocation = absPath.lastIndexOf("/");
//	    			int endlocation = absPath.lastIndexOf(".jpg");
//	    			final String opId = absPath.substring(startlocation+1,endlocation);
//					imageview.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							BitmapDialogActivity.actionStart(context, userId, taskId, opId);
//					}
//				});
//					
//	    		
////	    		Operation operation1 = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//				Operation operation1 = itemCell.getCell().getHookOperation();
//				String value = operation1.getOpvalue();
//	    		if(value != null){
//					if(value.equals("is")){				//打钩状态
//						checkbox.setChecked(true);
//					}else if(value.equals("no")){	//不打钩状态
//						checkbox.setChecked(false);
//					}else{
//						checkbox.setChecked(false);
//					}
//				}
//	    		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
//			        @Override  
//			        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
//			            // TODO Auto-generated method stub  
//			        	if(checkinterface!=null){
//			        		checkinterface.onCheckedChanged(buttonView, isChecked, itemCell.getCell(), endIndex, endlie);
//			        	}
//			        }  
//			    });
//				setMyCheckedChangeListener(new CheckedChangeInterface(){
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked, Cell cell, int x, int y) {
//						// TODO Auto-generated method stub
//						if(isChecked){  
//							itemCell.getCell().setIshook("is");
//							itemCell.getCell().save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("is");
//							operation.save();
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////				        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//							Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), 
//									itemCell.getCell().getTaskid()).find(Task.class).get(0);
//							String firsttime = task.getStartTime();
//		    	            if(firsttime == null || firsttime.equals("")){
//		    	            	task.setStartTime(DateUtil.getCurrentDate());
//		    	            	task.save();
//		    	            }
//							HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//							Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show(); 
//						}else{
//							cell.setIshook("no");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("no");
//							operation.save();
//							HtmlHelper.changeCheckValue(htmlDoc, itemCell.getCell());
//							Toast.makeText(context, "未选中", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
//	    		final String rowNum = itemCell.getCell().getHorizontalorder();
//	    		final String cellId = itemCell.getCell().getCellid();
//	    		button.setOnClickListener(new OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						CameraAty.actionStart(context, userId, taskId, 
//								OrientApplication.getApplication().rw.getRwid(), rowNum, cellId);
//					}
//	    		});
////				final Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		final Operation operation = itemCell.getCell().getStringOperation();
//	    		String stringdata = operation.getOpvalue();
//	    		if(null == itemCell.getCell().getOpvalue() || "".equals(stringdata)){
//	    			edittext.setText("");
//	    		}else{
//	    			edittext.setText(stringdata);
//	    		}
//	    		edittext.addTextChangedListener(new TextWatcher() {  
//	    	        @Override    
//	    	        public void afterTextChanged(Editable s) {     
//	    	            // TODO Auto-generated method stub     
//	    	        }   
//	    	        @Override 
//	    	        public void beforeTextChanged(CharSequence s, int start, int count,  
//	    	                int after) {  
//	    	            // TODO Auto-generated method stub  
//	    	        }  
//	    	        @Override    
//	    	        public void onTextChanged(CharSequence s, int start, int before, int count) {     
////	    	        	Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	    	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//	    	        	Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), 
//	    	        			itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    	        	htmlDoc = HtmlHelper.getHtmlDoc(task);
//	    	        	String str = edittext.getText().toString();  
//	    	        	itemCell.getCell().setOpvalue(str);
//	    	        	itemCell.getCell().save();
////	    	        	Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	    	        			OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2", itemCell.getCell().getTaskid())
////	    	        			.find(Operation.class).get(0);
//	    	        	Operation operation = itemCell.getCell().getStringOperation();
//	    	        	operation.setOpvalue(str);
//	    	        	operation.save();
//	    	        	String firsttime = task.getStartTime();
//	    	            if(firsttime == null || firsttime.equals("")){
//	    	            	task.setStartTime(DateUtil.getCurrentDate());
//	    	            	task.save();
//	    	            }
//	    	        	HtmlHelper.changeTextValue(htmlDoc, itemCell.getCell());
//	    	        }                    
//	    		});
////	    		if(activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(true);
////	    			edittext.setEnabled(true);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							if(htmlDoc != null)
////								HtmlHelper.changePhotoValue(htmlDoc, operation);
////							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
////									rowNum, cellId);
////						}
////		    		});
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(false);
////	    			edittext.setEnabled(false);
////	    			button.setBackgroundResource(R.drawable.photoalbum);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
////									OrientApplication.getApplication().rw.getRwid());
////						}
////		    		});
////	    		}
//	    		if(location.equals("1")){
//	    			checkbox.setEnabled(true);
//	    			edittext.setEnabled(true);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							if(htmlDoc != null)
//								HtmlHelper.changePhotoValue(htmlDoc, operation);
//							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
//									rowNum, cellId);
//						}
//		    		});
//	    		}else{
//	    			checkbox.setEnabled(false);
//	    			edittext.setEnabled(false);
//	    			button.setBackgroundResource(R.drawable.photoalbum);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
//						}
//		    		});
//	    		}
//	    		
//				LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//	    	}else if(itemCell.getCellType()==CellTypeEnum.STRINGBITMAP){
//	    		String activityName = ActivityUtil.getActivityName(context);
////	    		Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		Operation operation = itemCell.getCell().getStringOperation();
//	    		final String userId = OrientApplication.getApplication().loginUser.getUserid();
//	    		final String taskId = itemCell.getCell().getTaskid();
//	    		final String opId = operation.getSketchmap();
//	    		LinearLayout view = (LinearLayout) getstringbitmap();
//	    		final EditText edittext = (EditText) view.findViewById(R.id.ownerstringbitmap_edittext);
//	    		ImageView imageview = (ImageView) view.findViewById(R.id.ownerstringbitmap_bitmap);
////	    		final String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/"+ userId+"/" + taskId + "/" + opId + ".jpg";
////	    		final String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/" + taskId + "/" + opId + ".jpg";
//	    		final String absPath = Environment.getExternalStorageDirectory()
//						+ Config.opphotoPath + "/"
//						+ taskId + "/" + opId + ".jpg";
//	    		
//				imageview.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Bitmap bitmap = BitmapFactory.decodeFile(absPath);
//						if(bitmap != null)
//							BitmapDialogActivity.actionStart(context, userId, taskId, opId);
//						else
//							Toast.makeText(context, "请上传示意图", Toast.LENGTH_SHORT).show();
//					}
//				});
//				
//				DisplayImageOptions options = new DisplayImageOptions.Builder()
//					.showImageOnFail(R.drawable.shiyitu)
//					.cacheInMemory(true)
//					.cacheOnDisc(true)
//					.bitmapConfig(Bitmap.Config.RGB_565)
//					.build();
//				ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
//				ImageLoader.getInstance().displayImage(absPath, imageview, options);
//				
////				imageview.setOnClickListener(new OnClickListener() {
////					@Override
////					public void onClick(View v) {
////						BitmapDialogActivity.actionStart(context, userId, taskId, opId);
////					}
////				});
//					
//					
//					
//					
//				String stringdata = itemCell.getCell().getOpvalue();
//	    		if(null == itemCell.getCell().getOpvalue() || "".equals(stringdata)){
//	    			edittext.setText("");
//	    		}else{
//	    			edittext.setText(itemCell.getCell().getOpvalue());
//	    		}
////	    		String stringname = edittext.getText().toString();
////	    		itemCell.getCell().setOpvalue(stringname);
////	    		itemCell.getCell().save();
//	    		
//	    		edittext.addTextChangedListener(new TextWatcher() {  
//	    	        @Override    
//	    	        public void afterTextChanged(Editable s) {     
//	    	            // TODO Auto-generated method stub     
//	    	        }   
//	    	        @Override 
//	    	        public void beforeTextChanged(CharSequence s, int start, int count,  
//	    	                int after) {  
//	    	            // TODO Auto-generated method stub  
//	    	        }  
//	    	        @Override    
//	    	        public void onTextChanged(CharSequence s, int start, int before, int count) {   
//	    	        	String str = edittext.getText().toString();  
//	    	        	itemCell.getCell().setOpvalue(str);
//	    	        	itemCell.getCell().save();
////	    	        	Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	    	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//	    	        	Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), 
//	    	        			itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    	        	Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////	    	        			OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////	    	        			.find(Operation.class).get(0);
//	    	        	Operation operation = itemCell.getCell().getStringOperation();
//	    	        	operation.setOpvalue(str);
//	    	        	operation.save();
//	    	        	String firsttime = task.getStartTime();
//	    	            if(firsttime == null || firsttime.equals("")){
//	    	            	task.setStartTime(DateUtil.getCurrentDate());
//	    	            	task.save();
//	    	            }
//	    	        	HtmlHelper.changeTextValue(htmlDoc, itemCell.getCell());
//	    	        }                    
//	    		});
//	    		
////	    		if(activityName.equals("CheckActivity")){
////	    			edittext.setEnabled(true);
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			edittext.setEnabled(false);
////	    		}
//	    		if(location.equals("1")){
//	    			edittext.setEnabled(true);
//	    		}else{
//	    			edittext.setEnabled(false);
//	    		}
//	    		
//					
//				LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//				
//	    	}else if(itemCell.getCellType()==CellTypeEnum.STRINGPHOTOBITMAP){
//	    		String activityName = ActivityUtil.getActivityName(context);
////	    		final Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		final Operation operation = itemCell.getCell().getStringOperation();
////	    		final Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    		htmlDoc = HtmlHelper.getHtmlDoc(task);
//	    		final String userId = OrientApplication.getApplication().loginUser.getUserid();
//	    		final String taskId = itemCell.getCell().getTaskid();
//	    		final String opId = operation.getSketchmap();
//	    		LinearLayout view = (LinearLayout) getstringphotobitmap();
//	    		final EditText edittext = (EditText) view.findViewById(R.id.ownerstringphotobitmap_edittext);
//	    		Button button = (Button) view.findViewById(R.id.ownerstringphotobitmap_photo);
//	    		ImageView imageview = (ImageView) view.findViewById(R.id.ownerstringphotobitmap_bitmap);
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/"+ userId+"/" + taskId + "/" + opId + ".jpg";
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/" + taskId + "/" + opId + ".jpg";
//	    		final String absPath = Environment.getExternalStorageDirectory()
//						+ Config.opphotoPath + "/"
//						+ taskId + "/" + opId + ".jpg";
//	    		Bitmap bitmap = BitmapFactory.decodeFile(absPath);
//	    		if(bitmap != null)
//	    			imageview.setImageBitmap(bitmap);
//					imageview.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							BitmapDialogActivity.actionStart(context, userId, taskId, opId);
//					}
//				});
//					
//	    		String stringdata = operation.getOpvalue();
//	    		if(null == itemCell.getCell().getOpvalue() || "".equals(stringdata)){
//	    			edittext.setText("");
//	    		}else{
//	    			edittext.setText(stringdata);
//	    		}
//	    		edittext.addTextChangedListener(new TextWatcher() {  
//	    	        @Override    
//	    	        public void afterTextChanged(Editable s) {     
//	    	            // TODO Auto-generated method stub     
//	    	        }   
//	    	        @Override 
//	    	        public void beforeTextChanged(CharSequence s, int start, int count,  
//	    	                int after) {  
//	    	            // TODO Auto-generated method stub  
//	    	        }  
//	    	        @Override    
//	    	        public void onTextChanged(CharSequence s, int start, int before, int count) {   
//	    	        	String str = edittext.getText().toString();  
//	    	        	itemCell.getCell().setOpvalue(str);
//	    	        	itemCell.getCell().save();
////	    	        	Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ?",
////	    	        			OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "2")
////	    	        			.find(Operation.class).get(0);
//	    	        	Operation operation = itemCell.getCell().getStringOperation();
//	    	        	operation.setOpvalue(str);
//	    	        	operation.save();
////	    	        	Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	    	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//	    	        	Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), 
//	    	        			itemCell.getCell().getTaskid()).find(Task.class).get(0);
//	    	        	String firsttime = task.getStartTime();
//	    	            if(firsttime == null || firsttime.equals("")){
//	    	            	task.setStartTime(DateUtil.getCurrentDate());
//	    	            	task.save();
//	    	            }
//	    	        	HtmlHelper.changeTextValue(htmlDoc, itemCell.getCell());
//	    	        }                    
//	    		});
//	    		final String rowNum = itemCell.getCell().getHorizontalorder();
//	    		final String cellId = itemCell.getCell().getCellid();
////	    		if(activityName.equals("CheckActivity")){
////	    			edittext.setEnabled(true);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							if(htmlDoc != null)
////								HtmlHelper.changePhotoValue(htmlDoc, operation);
////							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
////									rowNum, cellId);
////						}
////		    		});
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			edittext.setEnabled(false);
////	    			button.setBackgroundResource(R.drawable.photoalbum);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
////									OrientApplication.getApplication().rw.getRwid());
////						}
////		    		});
////	    		}
//	    		if(location.equals("1")){
//	    			edittext.setEnabled(true);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							if(htmlDoc != null)
//								HtmlHelper.changePhotoValue(htmlDoc, operation);
//							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
//									rowNum, cellId);
//						}
//		    		});
//	    		}else{
//	    			edittext.setEnabled(false);
//	    			button.setBackgroundResource(R.drawable.photoalbum);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
//						}
//		    		});
//	    		}
//	    		
//	    		
//				LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//				
//	    	}else if(itemCell.getCellType()==CellTypeEnum.HOOKPHOTOBITMAP){
//	    		String activityName = ActivityUtil.getActivityName(context);
////	    		final Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		final Operation operation = itemCell.getCell().getHookOperation();
////	    		final Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////	        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
////	    		htmlDoc = HtmlHelper.getHtmlDoc(task);
//	    		final String userId = OrientApplication.getApplication().loginUser.getUserid();
//	    		final String taskId = itemCell.getCell().getTaskid();
//	    		final String opId = operation.getSketchmap();
//	    		LinearLayout view = (LinearLayout) gethookphotobitmap();
//	    		CheckBox checkbox = (CheckBox) view.findViewById(R.id.ownerhookphotobitmap_check);
//	    		Button button = (Button) view.findViewById(R.id.ownerhookphotobitmap_photo);
//	    		ImageView imageview = (ImageView) view.findViewById(R.id.ownerhookphotobitmap_bitmap);
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/"+ userId+"/" + taskId + "/" + opId + ".jpg";
////	    		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
////	    				+ Config.opphotoPath+ "/" + taskId + "/" + opId + ".jpg";
////	    		final String absPath = Environment.getExternalStorageDirectory()
////						+ Config.opphotoPath + "/"
////						+ taskId + "/" + opId + ".jpg";
//	    		final String absPath = Environment.getExternalStorageDirectory()
//						+ Config.opphotoPath + "/"
//						+ taskId + "/" + opId + ".jpg";
//	    		Bitmap bitmap = BitmapFactory.decodeFile(absPath);
//	    		if(bitmap != null){
//	    			imageview.setImageBitmap(bitmap);
//					imageview.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							BitmapDialogActivity.actionStart(context, userId, taskId, opId);
//						}
//					});
//	    		}
////	    		Operation operation1 = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////	            		OrientApplication.getApplication().loginUser.getUserid(), itemCell.getCell().getCellid(), "1", itemCell.getCell().getTaskid())
////	            		.find(Operation.class).get(0);
//	    		Operation operation1 = itemCell.getCell().getHookOperation();
//				String value = operation1.getOpvalue();
//	    		if(value != null){
//					if(value.equals("is")){				//打钩状态
//						checkbox.setChecked(true);
//					}else if(value.equals("no")){	//不打钩状态
//						checkbox.setChecked(false);
//					}else{
//						checkbox.setChecked(false);
//					}
//				}
//	    		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
//			        @Override  
//			        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
//			            // TODO Auto-generated method stub  
//			        	if(checkinterface!=null){
//			        		checkinterface.onCheckedChanged(buttonView, isChecked, itemCell.getCell(), endIndex, endlie);
//			        	}
//			        }  
//			    });
//	    		setMyCheckedChangeListener(new CheckedChangeInterface(){
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked, Cell cell, int x, int y) {
//						// TODO Auto-generated method stub
//						if(isChecked){  
//							cell.setIshook("is");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), cell.getCellid(), "1", cell.getTaskid())
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("is");
//							operation.save();
////							Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
////				        			OrientApplication.getApplication().rw.getRwid(), itemCell.getCell().getTaskid()).find(Task.class).get(0);
//							Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), 
//									itemCell.getCell().getTaskid()).find(Task.class).get(0);
//							String firsttime = task.getStartTime();
//		    	            if(firsttime == null || firsttime.equals("")){
//		    	            	task.setStartTime(DateUtil.getCurrentDate());
//		    	            	task.save();
//		    	            }
//							HtmlHelper.changeCheckValue(htmlDoc, cell);
//							Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show(); 
//						}else{
//							cell.setIshook("no");
//							cell.save();
////							Operation operation = DataSupport.where("userid = ? and cellid = ? and type = ? and taskid = ?",
////									OrientApplication.getApplication().loginUser.getUserid(), cell.getCellid(), "1", cell.getTaskid())
////									.find(Operation.class).get(0);
//							Operation operation = itemCell.getCell().getHookOperation();
//							operation.setOpvalue("no");
//							operation.save();
//							HtmlHelper.changeCheckValue(htmlDoc, cell);
//							Toast.makeText(context, "未选中", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
//	    		final String rowNum = itemCell.getCell().getHorizontalorder();
//	    		final String cellId = itemCell.getCell().getCellid();
////	    		if(activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(true);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							if(htmlDoc != null)
////								HtmlHelper.changePhotoValue(htmlDoc, operation);
////							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
////									rowNum, cellId);
////						}
////		    		});
////	    		}
////	    		if(!activityName.equals("CheckActivity")){
////	    			checkbox.setEnabled(false);
////	    			button.setBackgroundResource(R.drawable.photoalbum);
////	    			button.setOnClickListener(new OnClickListener(){
////						@Override
////						public void onClick(View v) {
////							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
////									OrientApplication.getApplication().rw.getRwid());
////						}
////		    		});
////	    		}
//	    		if(location.equals("1")){
//	    			checkbox.setEnabled(true);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							if(htmlDoc != null)
//								HtmlHelper.changePhotoValue(htmlDoc, operation);
//							CameraAty.actionStart(context, userId, taskId, OrientApplication.getApplication().rw.getRwid(), 
//									rowNum, cellId);
//						}
//		    		});
//	    		}else{
//	    			checkbox.setEnabled(false);
//	    			button.setBackgroundResource(R.drawable.photoalbum);
//	    			button.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//							AlbumAty.actionStart2(context, itemCell.getCell().getTaskid(), itemCell.getCell().getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
//						}
//		    		});
//	    		}
//	    		
//	    		
//	    		LayoutParams linear =new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//	    		linear.gravity = Gravity.CENTER;
//	    		view.setLayoutParams(linear);
//	    		secondLayout.addView(view);
//	    		viewList.add(view);
//	    	}
//	    	if(i!=itemCells.size()-1){			//插入竖线
//				LinearLayout v_line = (LinearLayout)getVerticalLine();
//				v_line.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
//				secondLayout.addView(v_line);
//			}
//    	}
//    }
//    
//    private void setMyCheckedChangeListener(
//			CheckedChangeInterface checkedChangeInterface) {
//		// TODO Auto-generated method stub
//		this.checkinterface = checkedChangeInterface;
//	}
//
//	OnClickListener ocl=new OnClickListener() {  
//        @Override  
//        public void onClick(View v) {  
//            // TODO Auto-generated method stub  
//            if(!((CheckBox) v).isChecked()){  
//                Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();  
//            }  
//        }  
//    };  
//      
//    OnCheckedChangeListener occl=new OnCheckedChangeListener() {  
//        @Override  
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
//            // TODO Auto-generated method stub  
//            if(isChecked){  
//                Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show();  
//            }  
//        }  
//    };
//    
//    
//    public void refreshData(ArrayList<ItemCell> itemCells){
//		for(int i=0;i<itemCells.size();i++){
//			final ItemCell itemCell = itemCells.get(i);
//			if(itemCell.getCellType()==CellTypeEnum.LABEL){
//				TextView view = (TextView)viewList.get(i);
//				view.setText(itemCell.getCellValue());
//			}else if(itemCell.getCellType()==CellTypeEnum.DIGIT){
//				EditText view= (EditText)viewList.get(i);
//				view.setText(itemCell.getCellValue());
//				this.setEditView(view);
//				this.setOnKeyBorad(view);
//			}else if(itemCell.getCellType()==CellTypeEnum.STRING){
//				EditText view= (EditText)viewList.get(i);
//				//新增内容
//				String stringdata = itemCell.getCell().getOpvalue();
//	    		if(null == itemCell.getCell().getOpvalue() || "".equals(stringdata)){
//	    			view.setText("");
//	    		}else{
//	    			view.setText(itemCell.getCell().getOpvalue());
//	    		}
//	    		String stringname = view.getText().toString();
//	    		itemCell.getCell().setOpvalue(stringname);
//	    		itemCell.getCell().save();
////				view.setText(itemCell.getCellValue());
//				this.setEditView(view);
//			}else if(itemCell.getCellType()==CellTypeEnum.HOOK){
//				CheckBox view = (CheckBox) viewList.get(i);
//				if(itemCell.getCell().getIshook() != null){
//					if(itemCell.getCell().getIshook().equals("is")){				//打钩状态
//						view.setChecked(true);
//					}else if(itemCell.getCell().getIshook().equals("no")){	//不打钩状态
//						view.setChecked(false);
//					}else{
//						view.setChecked(false);
//					}
//				}
////				view.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
////			        @Override  
////			        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
////			            // TODO Auto-generated method stub  
////			        	if(checkinterface!=null){
////			        		checkinterface.onCheckedChanged(buttonView, isChecked, itemCell.getCell());
////			        	}
////			        }  
////			    });
////				setMyCheckedChangeListener(new CheckedChangeInterface(){
////					@Override
////					public void onCheckedChanged(CompoundButton buttonView,
////							boolean isChecked, Cell cell, int x, int y) {
////						// TODO Auto-generated method stub
////						if(isChecked){  
////							cell.setIshook("is");
////							cell.isSaved();
////							Toast.makeText(context, "选中", Toast.LENGTH_SHORT).show(); 
////						}else{
////							Toast.makeText(context, "未选中", Toast.LENGTH_SHORT).show();
////						}
////					}
////				});
//			}
//			else if(itemCell.getCellType()==CellTypeEnum.HOOKPHOTO){
//				CheckBox view = (CheckBox) viewList.get(i);
//			}
//			else if(itemCell.getCellType()==CellTypeEnum.HOOKSTRING){
//				CheckBox view = (CheckBox) viewList.get(i);
//			}
//			else if(itemCell.getCellType()==CellTypeEnum.HOOKSTRINGPHOTO){
//				CheckBox view = (CheckBox) viewList.get(i);
//			}
//			else if(itemCell.getCellType()==CellTypeEnum.STRINGPHOTO){
//				CheckBox view = (CheckBox) viewList.get(i);
//			}
//		}
//	}
//    
//    private View getVerticalLine(){
//		return LayoutInflater.from(context).inflate(R.layout.table_atom_line_v_view, null);
//	}
//    
//    private int getCellWidth(int cellStart,int cellEnd){
//		int width = 0;
//		for(int i=cellStart;i<cellEnd;i++){
//			width = this.headWidthArr[i] + width;
//		}
//		return width;
//	}
//    private View getLabelView(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.table_atom_text_view, null);
//	}
//	private View getInputView(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.table_atom_edttxt_view, null);
//	}
//	private View getCheckBoxView(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerhook, null);
//	}
//	private View gethookstringview(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerhookstring, null);
//	}
//	private View gethookphoto(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerhookphoto, null);
//	}
//	private View getstringphoto(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerstringphoto, null);
//	}
//	private View gethookstringphoto(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerhookstringphoto, null);
//	}
//	private View gethookbitmap(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerhookbitmap, null);
//	}
//	private View gethookstringbitmap(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerhookstringbitmap, null);
//	}
//	private View gethookstringphotobitmap(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerhookstringphotobitmap, null);
//	}
//	private View getstringbitmap(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerstringbitmap, null);
//	}
//	private View getstringphotobitmap(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerstringphotobitmap, null);
//	}
//	private View gethookphotobitmap(){
//		return (View)LayoutInflater.from(context).inflate(R.layout.ownerhookphotobitmap, null);
//	}
//	private void setEditView(EditText edtText1){
//    	if(this.isRead){
//    		edtText1.setEnabled(false);
//    	}else{
//    		
//    	}
//	}
//	private void setOnKeyBorad(EditText edtText1){
//		//数字键盘
//		if(!this.isRead){//非只读
//			
//		}
//	}
//	public String getRowType() {
//		return rowType;
//	}
}
