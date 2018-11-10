package com.example.navigationdrawertest.activity;

import java.util.ArrayList;

import com.example.navigationdrawertest.adapter.Event.LocationEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.CustomUI.CellTypeEnum;
import com.example.navigationdrawertest.CustomUI.HeadItemCell;
import com.example.navigationdrawertest.CustomUI.ItemCell;
import com.example.navigationdrawertest.SweetAlert.SweetAlertDialog;
import com.example.navigationdrawertest.adapter.ConditionAdapter;
import com.example.navigationdrawertest.adapter.CustomeTableViewAdapter;
import com.example.navigationdrawertest.adapter.SignAdapter;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.data.AerospaceDB;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Scene;
import com.example.navigationdrawertest.model.Signature;
import com.example.navigationdrawertest.model.Task;
import com.example.navigationdrawertest.utils.CalculateUtil;
import com.example.navigationdrawertest.utils.DateUtil;
import com.example.navigationdrawertest.utils.HtmlHelper;

import de.greenrobot.event.EventBus;

public class CheckActivity extends BaseActivity implements OnItemClickListener {
	private ListView listView_1, listView_2, listView_3;
	private Button backButton;
	private ConditionAdapter conditionadapter = null;
	private SignAdapter signadapter = null;
//	private TableAdapter tableadapter = null;
	private List<Scene> scenelists = new ArrayList<Scene>();
	private List<Task> tasklists = new ArrayList<Task>();
	private List<Signature> signlists = new ArrayList<Signature>();
	
	private static long task_id;
	private AerospaceDB aerospacedb;

	//table表格所要声明的变量
	private LayoutInflater inflater;
    private LinearLayout headLayout = null;
    public int[] arrHeadWidth = null;
//    private ListView listView;
    private int HashmapSize = 0;
    private CustomeTableViewAdapter adapter = null;
    private ArrayList<HashMap<String,Object>>  lists = new ArrayList<HashMap<String,Object>>();
    private int mScreenWidth;
    private int mScreenHeight;
    public Map<Integer , List<Cell>> cells = new HashMap<Integer , List<Cell>>();
    public Map<Integer , List<Cell>> cellsReal = new HashMap<Integer , List<Cell>>();
    private int rowcolumn;			//表格总行数
    private int linecolumn;			//表格总列数
    private int cellcolumn;			//cell总个数
    public Document htmlDoc = null;				//该表格的HTML网页
//    private static Handler mhandler;
    private ProgressDialog dialog;
    private Switch checkSwitch;
    private static String mlocation;
    
    public Handler mHandler = new Handler(){  
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case 0:  
            	int[] arrHeadWidth = msg.getData().getIntArray("width");
            	adapter = new CustomeTableViewAdapter(CheckActivity.this, lists , listView_2 , false , arrHeadWidth, mHandler, mlocation); 
            	adapter.notifyDataSetChanged();
            	dialog.dismiss();
                break;   
            }  
        }
    };
    
    public void fixListViewHeight(ListView listView) {   
        // 如果没有设置数据适配器，则ListView没有子项，返回。  
        ListAdapter listAdapter = listView.getAdapter();  
        int totalHeight = rowcolumn * 75;
   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        // listView.getDividerHeight()获取子项间分隔符的高度   
        // params.height设置ListView完全显示需要的高度    
        
        params.height = totalHeight+ (1 * rowcolumn-1)+35;   
        listView.setLayoutParams(params);   
    } 
	
	public static void actionStart(Context context, long taskid, Handler handler, String location) {
		mlocation = location;
		task_id = taskid;
		Intent intent = new Intent(context, CheckActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle("检查表格"); 
		ActionBar actionBar = getActionBar();  
//	    actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.checkmain);
		aerospacedb = new AerospaceDB();
		listView_1 = (ListView) findViewById(R.id.check_mylistview_1);
		listView_2 = (ListView) findViewById(R.id.check_mylistview_2);
		checkSwitch = (Switch) findViewById(R.id.checkSwitch);
//		Task task = DataSupport.where("userid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
//    			task_id+"").find(Task.class).get(0);
		final Task task = DataSupport.where("taskid = ?", task_id+"").find(Task.class).get(0);
		if(task.getEndTime() != null && !task.getEndTime().equals("")){
			checkSwitch.setChecked(true);
		}
		checkSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView,  
                    boolean isChecked) {  
//            	Task task = DataSupport.where("userid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
//            			task_id+"").find(Task.class).get(0);
                if(isChecked) {  	//任务完成
                	task.setEndTime(DateUtil.getCurrentDate());
                	task.setLocation(2);
                	task.save();
                }else {					//任务没有完成
                	task.setEndTime("");
                	task.setLocation(1);
                	task.save();
                }  
            }  
        });  
		
		//table所要绑定的控件
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		mScreenHeight = dm.heightPixels;
		Log.i("宽度", mScreenWidth+"");
		Log.i("高度", mScreenHeight+"");
        headLayout = (LinearLayout)findViewById(R.id.check_linearlayout_head); 
		
        inflater = LayoutInflater.from(this);
        this.testData();//测试数据
		
		loadConditionAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid());
		loadSignAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid());
		conditionadapter = new ConditionAdapter(CheckActivity.this, R.layout.checkconditionitem, scenelists);
//		signadapter = new SignAdapter(CheckActivity.this, R.layout.write_main, signlists);
//		loadTableAdapter(task_id);
//		loadSignAdapter(task_id);
		
		listView_1.setAdapter(conditionadapter);
//		listView_2.setAdapter(tableadapter);
//		listView_3.setAdapter(signadapter);

//		listView_1.setOnItemClickListener(this);
//		listView_2.setOnItemClickListener(this);
//		listView_3.setOnItemClickListener(this);
//		listView_2.setOnItemClickListener(listViewItemClickListener);
//		
		setListViewHeightBasedOnChildren(listView_1);
		Log.i("开始时间4", DateUtil.getCurrentTime());
//		setListViewHeightBasedOnChildrenTable(listView_2);
//		setListViewHeightBasedOnChildren1(listView_2);
		Log.i("开始时间5", DateUtil.getCurrentTime());
//		setListViewHeightBasedOnChildren(listView_3);
//		Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
//				OrientApplication.getApplication().rw.getRwid(), task_id+"").find(Task.class).get(0);
//		htmlDoc = HtmlHelper.getHtmlDoc(task);
		
		fixListViewHeight(listView_2);
	}
	
	OnItemClickListener listViewItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(android.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			StringBuffer str = new StringBuffer();
			for(int j=0; j<linecolumn; j++){			//遍历每一行的内容
				Cell cell = cells.get(arg2).get(j);
				//1，查找出该行所有的Cell
//				List<Cell> cellList = DataSupport.where("userid = ? and horizontalorder = ? and taskid = ? and type = ?", 
//						OrientApplication.getApplication().loginUser.getUserid(), cell.getHorizontalorder(),
//						cell.getTaskid(), "TRUE").find(Cell.class);
				List<Cell> cellList = DataSupport.where("horizontalorder = ? and taskid = ? and type = ?", cell.getHorizontalorder(),
						cell.getTaskid(), "TRUE").find(Cell.class);
				//2，查找出该行所有的operation
				List<Operation> opList = new ArrayList<Operation>();
				for(int loop=0; loop<cellList.size(); loop++){
//					List<Operation> op = DataSupport.where("userid = ? and cellid = ? and taskid = ?",
//							OrientApplication.getApplication().loginUser.getUserid(), cellList.get(loop).getCellid(),
//							cellList.get(loop).getTaskid()).find(Operation.class);
					List<Operation> op = DataSupport.where("cellid = ? and taskid = ?", cellList.get(loop).getCellid(),
							cellList.get(loop).getTaskid()).find(Operation.class);
					if(op.size() >= 1){		//一个Cell下面有多个operation项
						for(int k=0; k<op.size(); k++){
							opList.add(op.get(k));
						}
					}
				}
				//3，根据operation的5个提示项赋值
				if(opList.size() > 0){
					boolean opinion1 = false;
					boolean opinion2 = false;
					boolean opinion3 = false;
					boolean opinion4 = false;
					boolean opinion5 = false;
					for(int m=0; m<opList.size(); m++){
						if(opList.get(m).getIldd().equals("TRUE") && opinion1 == false){
							str.append("1,一类单点;  ");
							opinion1 = true;
						}
						if(opList.get(m).getIildd().equals("TRUE") && opinion2 == false){
							str.append("2,二类单点;  ");	
							opinion2 = true;
						}
						if(opList.get(m).getErr().equals("TRUE") && opinion3 == false){
							str.append("3,易错难;  ");	
							opinion3 = true;
						}
						if(opList.get(m).getLastaction().equals("TRUE") && opinion4 == false){
							str.append("4,最后一次操作;  ");	
							opinion4 = true;
						}
						if(opList.get(m).getTighten().equals("TRUE") && opinion5 == false){
							str.append("5,拧紧力矩;  ");	
							opinion5 = true;
						}
					}
					if(str.toString().equals("")){
						str.append("无");
					}
				}
			}
			new SweetAlertDialog(CheckActivity.this).setTitleText("注意事项!").setContentText(str.toString()).show();
		};
	};
	
	@Override  
	public boolean onCreateOptionsMenu(Menu menu) {  
	    MenuInflater inflater = getMenuInflater();  
	    inflater.inflate(R.menu.readactivity, menu);  
	    return super.onCreateOptionsMenu(menu);  
	}
	
	@Override  
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch (item.getItemId()) {  
	    case R.id.action_save:  
	    	EventBus.getDefault().post(new LocationEvent("ok"));  	
	    	finish();  
	        return true;  
	    default:  
	        return super.onOptionsItemSelected(item);  
	    }  
	}
	
	/**
	 * 控制Listview中item可以填满Listview
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildrenTable(ListView listView) {  
		  ListAdapter listAdapter = listView.getAdapter();  
		  if (listAdapter == null) {  
			  return;  
		  }  
		  int totalHeight = 0;  
		  for (int i = 0; i < listAdapter.getCount(); i++) {  
			   View listItem = listAdapter.getView(i, null, listView);  
			   listItem.measure(0, 0);  
			   totalHeight += listItem.getMeasuredHeight();  
		  }  
		  ViewGroup.LayoutParams params = listView.getLayoutParams();  
		  params.height = totalHeight  
		    + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
		  listView.setLayoutParams(params);  
	}
	
	private View getVerticalLine(){
		return inflater.inflate(R.layout.table_atom_line_v_view, null);
	}
	
	private void addVLine(){
		LinearLayout v_line = (LinearLayout)getVerticalLine();
		v_line.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT));
		headLayout.addView(v_line);
	}
	
	private void addHead(HashMap headMap){
		arrHeadWidth = new int[headMap.size()];
		int width = 0;
		for(int i=0;i<headMap.size();i++){
			HeadItemCell itemCell = (HeadItemCell)headMap.get(i+"");
			String name = itemCell.getCellValue();
//			width = Dp2Px(this,itemCell.getWidth());
			width = itemCell.getWidth();
			Log.i("width", itemCell.getWidth()+"");
			Log.i("实际宽度", width+"");
			if(linecolumn > 8){
				setHeadName(name,200);
			}else{
				setHeadName(name,width);
			}
//			setHeadName(name,width);
			arrHeadWidth[i] = width;
			if(i != headMap.size()-1){		//不是最后一列
				this.addVLine();
			}
		}
	}
	
	private void setHeadName(String name,int width){
		TextView headView = (TextView)inflater.inflate(R.layout.table_atom_head_text_view, null);
		if(headView!=null){
			String viewName = "<b>" + name + "</b>";
			headView.setText(HtmlHelper.transCellLabel(name));
			headView.setWidth(width);
			headLayout.addView(headView);
		}
	}

	private int Dp2Px(Context context, float dp) {  
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    Log.i("scale", scale+"");
	    return (int) (dp * scale + 0.5f);  
	} 
	
	/**
	 * 处理Cell的水平排布，row id
	 * @param cellList
	 */
	public void handleHorizontalorder(List<Cell> cellList){
		
		Cell HorizontionCellSigle = new Cell();
		List<String> HorizontionList = new ArrayList<String>();		//得到表格多少行，把行数记录到集合中
		HorizontionList.add(cellList.get(0).getHorizontalorder());					//getHorizontalorder是水平行的唯一标识
		for(int i=0; i<cellList.size(); i++){
			for(int j=0; j<HorizontionList.size(); j++){
				if(cellList.get(i).getHorizontalorder().equals(HorizontionList.get(j))){
					break;
				}
				if(j == (HorizontionList.size()-1) && !cellList.get(i).getHorizontalorder().equals(HorizontionList.get(j))){
					HorizontionList.add(cellList.get(i).getHorizontalorder());
					break;
				}
			}
		}
		
		rowcolumn = HorizontionList.size();
		linecolumn = cellcolumn/rowcolumn;
		for(int i=0; i<rowcolumn; i++){
			List<Cell> HorizontionCell = new ArrayList<Cell>();
//			HorizontionCell.clear();
			for(int j=0; j<linecolumn; j++){
				HorizontionCellSigle = loadCellByVerticalorder(i+1+"", j+1+"");
				HorizontionCell.add(HorizontionCellSigle);
			}
			cells.put(i, HorizontionCell);	//数据排列是按照List<Map<String, Object>>行号(0开始)+列号（Cell）
		}
		
	}
	
	private void testAddHead(HashMap headMap,String headName){
		//因为布局问题，预留了左右两边的边框距离，要先减去边框距离
		HeadItemCell itemCell = new HeadItemCell(headName,(mScreenWidth-80)/HashmapSize);		
		headMap.put(headMap.size()+"", itemCell);
	}
	
	private void testData(){
		HashMap headMap = new HashMap();
		List<Cell> cellList = new ArrayList<Cell>();
//		loadConditionAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid());
//		List<Cell> test_1 = DataSupport.findAll(Cell.class);
//		List<Cell> test_1 = DataSupport.where("userid = ?", "107").find(Cell.class);
//		List<Cell> testCell = DataSupport.where("taskid = ?", task_id+"").find(Cell.class);
		//获得该表格的所有CELL数据
		cellList = loadCellAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid());
		cellcolumn = cellList.size();
		if(cellList != null){
			handleHorizontalorder(cellList);
		}
		
		List<Cell> firstRowCellList = new ArrayList<Cell>();
//		firstRowCellList = cells.get(1);
		for(int i=1; i<=cells.get(0).size(); i++){
			Cell cell = loadCellByVerticalorder("1", i+"");		//根据表格ID，行号，列号可以确定一个cell
			firstRowCellList.add(cell);
		}
		HashmapSize = firstRowCellList.size();
		for(int i=0; i<firstRowCellList.size(); i++){
			this.testAddHead(headMap, firstRowCellList.get(i).getRowname());
		}
		this.addHead(headMap);
		
		dialog = ProgressDialog.show(this, "加載中...", "正在搜尋。。。。，請稍後！"); 
//        Thread thread = new Thread(new Runnable() { 
//                public void run() { 
//                	Log.i("开始时间1", DateUtil.getCurrentTime());
//            		final HashMap contentMap = new HashMap();
////            		Toast.makeText(this, "数据预加载.......", Toast.LENGTH_LONG).show();
////            		new Thread(
////            			new Runnable() {
////            				public void run() {
//            		testAddContent(contentMap);
////            				}
////            			}
////            		).start();
//            		Log.i("开始时间2", DateUtil.getCurrentTime());
//            		
//            		adapter = new CustomeTableViewAdapter(this, lists , listView_2 , false , this.arrHeadWidth, mHandler); 
//            		Log.i("开始时间3", DateUtil.getCurrentTime());
//            		adapter.notifyDataSetChanged();                                 
//                    Message message = new Message(); 
//                    message.what = 0; 
//                    mHandler.sendMessage(message); 
//                } 
//        }); 
//        thread.start(); 
		TableThread tableThread = new TableThread();
		tableThread.setArrHeadWidth(arrHeadWidth);
		tableThread.setCells(cells);
		tableThread.setContext(this);
		tableThread.setHtmlDoc(htmlDoc);
		tableThread.setLinecolumn(linecolumn);
		tableThread.setLists(lists);
//		tableThread.setListView_2(listView_2);
		tableThread.setmHandler(mHandler);
		tableThread.setRowcolumn(rowcolumn);
		tableThread.setTask_id(task_id);
		tableThread.run();
		
		
//		Log.i("开始时间1", DateUtil.getCurrentTime());
//		final HashMap contentMap = new HashMap();
//		Toast.makeText(this, "数据预加载.......", Toast.LENGTH_LONG).show();
//		new Thread(
//			new Runnable() {
//				public void run() {
//		testAddContent(contentMap);
//				}
//			}
//		).start();
//		Log.i("开始时间2", DateUtil.getCurrentTime());
		
//		adapter = new CustomeTableViewAdapter(this, lists , listView_2 , false , this.arrHeadWidth, mHandler); 
//		Log.i("开始时间3", DateUtil.getCurrentTime());
//		adapter.notifyDataSetChanged();
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
					//1，查找出该行所有的Cell
//					List<Cell> cellList = DataSupport.where("userid = ? and horizontalorder = ? and taskid = ? and type = ?", 
//							OrientApplication.getApplication().loginUser.getUserid(), cell.getHorizontalorder(),
//							cell.getTaskid(), "TRUE").find(Cell.class);
					List<Cell> cellList = DataSupport.where("horizontalorder = ? and taskid = ? and type = ?", cell.getHorizontalorder(),
							cell.getTaskid(), "TRUE").find(Cell.class);
					//2，查找出该行所有的operation
					List<Operation> opList = new ArrayList<Operation>();
					for(int loop=0; loop<cellList.size(); loop++){
//						List<Operation> op = DataSupport.where("userid = ? and cellid = ? and taskid = ?",
//								OrientApplication.getApplication().loginUser.getUserid(), cellList.get(loop).getCellid(),
//								cellList.get(loop).getTaskid()).find(Operation.class);
						List<Operation> op = DataSupport.where("cellid = ? and taskid = ?", cellList.get(loop).getCellid(),
								cellList.get(loop).getTaskid()).find(Operation.class);
						if(op.size() >= 1){		//一个Cell下面有多个operation项
							for(int k=0; k<op.size(); k++){
								opList.add(op.get(k));
							}
						}
					}
					//3，根据operation的5个提示项赋值
					final StringBuffer str = new StringBuffer();
					if(opList.size() > 0){
						boolean opinion1 = false;
						boolean opinion2 = false;
						boolean opinion3 = false;
						boolean opinion4 = false;
						boolean opinion5 = false;
						for(int m=0; m<opList.size(); m++){
							if(opList.get(m).getIldd().equals("TRUE") && opinion1 == false){
								str.append("1,一类单点;  ");
								opinion1 = true;
							}
							if(opList.get(m).getIildd().equals("TRUE") && opinion2 == false){
								str.append("2,二类单点;  ");	
								opinion2 = true;
							}
							if(opList.get(m).getErr().equals("TRUE") && opinion3 == false){
								str.append("3,易错难;  ");	
								opinion3 = true;
							}
							if(opList.get(m).getLastaction().equals("TRUE") && opinion4 == false){
								str.append("4,最后一次操作;  ");	
								opinion4 = true;
							}
							if(opList.get(m).getTighten().equals("TRUE") && opinion5 == false){
								str.append("5,拧紧力矩;  ");	
								opinion5 = true;
							}
						}
						if(str.toString().equals("")){
							str.append("无");
						}
					}
					cell.setShowContent(str.toString());
					this.testAddRows(rowMap, 1, cells.get(i).get(j).getTextvalue(), CellTypeEnum.LABEL, cell);
				}else{
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
							}else{																//填值
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
							}
						}
				}
			}
			if(i%2 == 0){
				rowMap.put("rowtype", "css2");
			}else{
				rowMap.put("rowtype", "css3");
			}
		}
	}
	
	/**
	 * 添加每一行
	 * @param rowMap
	 * @param colSpan
	 * @param cellValue
	 * @param cellType
	 */
	private void testAddRows(HashMap rowMap, int colSpan, String cellValue, CellTypeEnum cellType, Cell cell){
	    ItemCell itemCell = new ItemCell(cellValue, cellType, colSpan, cell, htmlDoc);	
	    rowMap.put(rowMap.size()+"", itemCell);
	}
	
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		Log.i("item高度", listView.getDividerHeight()+"");
		Log.i("item数量", listAdapter.getCount() - 1+"");
		// params.height += 5;// if without this statement,the listview will be
		// a
		// little short
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
	
	//加载Sign数据
	private void loadSignAdapter(long taskid, String userid){
		List<Signature> signs = aerospacedb.loadSignatureAdapter(taskid, userid);
		signlists = signs;
	}
	
	//加载Condition数据
	private void loadConditionAdapter(long taskid, String userid){
		List<Scene> conditions = aerospacedb.loadConditionAdapter(taskid, userid);
		scenelists = conditions;
	}
	
	//加载Table数据
	private List<Cell> loadCellAdapter(long taskid, String userid){
		List<Cell> cellList = aerospacedb.loadTableAdapter(taskid, userid, 1);
		return cellList;
	}
	
	//根据Horizontalorder查询Cell集合
	private List<Cell> loadCellByHorizontalorder(String Horizontalorder){
		List<Cell> cellList = aerospacedb.loadCellByHorizontalorder(task_id, 
				OrientApplication.getApplication().loginUser.getUserid(), Horizontalorder);
		return cellList;
	}
	
	private Cell loadCellByVerticalorder(String Horizontalorder, String verticalorder){
		Cell cell = aerospacedb.loadCellByVerticalorder(task_id, 
				OrientApplication.getApplication().loginUser.getUserid(), Horizontalorder, verticalorder);
		return cell;
	}
	
	private List<Operation> loadOperationByCellId(String cellid, String taskid){
		List<Operation> operationList = aerospacedb.loadOperationByCellId(cellid, taskid);
		return operationList;
	}
	
	//加载Sign数据
	private void loadSignAdapter(){
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		this.finish();
		super.onDestroy();
	}

//	@Override  
//    public boolean onKeyDown(int keyCode, KeyEvent event){
//		Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
//				OrientApplication.getApplication().rw.getRwid(),task_id+"").find(Task.class).get(0);
//    	htmlDoc = HtmlHelper.getHtmlDoc(task);
//    	if(HtmlHelper.saveHtmlDoc(task, htmlDoc))
//    		return true;
//    	return false;
//	}
	
	@Override  
	public boolean dispatchKeyEvent(KeyEvent event) {  
	    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK  
	            && event.getAction() == KeyEvent.ACTION_DOWN  
	            && event.getRepeatCount() == 0) {             
	    	Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(),task_id+"").find(Task.class).get(0);
	    	htmlDoc = HtmlHelper.getHtmlDoc(task);
	    	if(HtmlHelper.saveHtmlDoc(task, htmlDoc))
	    		Log.i("HTML编辑保存", task.getTaskname()+"保存成功！");
	    }  
	    return super.dispatchKeyEvent(event);  
	} 
	
	/**
	 * Activity加载完成之后的时间
	 */
	@Override  
    public void onWindowFocusChanged(boolean hasFocus){  
        if(hasFocus){  
//        	Message message = new Message();  
//            message.what = 0;  
//            mHandler.sendMessage(message);
        }  
    } 
	
	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if(keyCode == KeyEvent.KEYCODE_BACK){      
			EventBus.getDefault().post(new LocationEvent("ok"));
		}  
		return  super.onKeyDown(keyCode, event);     
	} 
}
