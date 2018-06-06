package com.example.navigationdrawertest.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

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

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class ReadActivity extends BaseActivity implements OnItemClickListener{
	private ListView listView_1, listView_2, listView_3;
	private ConditionAdapter conditionadapter = null;
	private SignAdapter signadapter = null;
//	private TableAdapter tableadapter = null;
	private List<Scene> scenelists = new ArrayList<Scene>();
	private List<Task> tasklists = new ArrayList<Task>();
	private List<Signature> signlists = new ArrayList<Signature>();
	private Button readback;
	
	private static Context checkContext;
	private static int task_id;
	private AerospaceDB aerospacedb;
//	private Context context;

	//table表格所要声明的变量
	private LayoutInflater inflater;
    private LinearLayout headLayout = null;
    private int[] arrHeadWidth = null;
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
    private static Handler mhandler;
//    SweetAlertDialog pDialog;
    private ProgressDialog dialog;
//    private Switch readSwitch;
    private static String mlocation;
    
    public Handler mHandler = new Handler(){  
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case 0:  
            	int[] arrHeadWidth = msg.getData().getIntArray("width");
            	adapter = new CustomeTableViewAdapter(ReadActivity.this, lists , listView_2 , false , arrHeadWidth, mHandler, mlocation); 
            	adapter.notifyDataSetChanged();
            	dialog.dismiss();
                break;   
            }  
        }
    };
	
    public void fixListViewHeight(ListView listView) {   
        // 如果没有设置数据适配器，则ListView没有子项，返回。  
        int totalHeight = rowcolumn * 75;
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        // listView.getDividerHeight()获取子项间分隔符的高度   
        // params.height设置ListView完全显示需要的高度    
        params.height = totalHeight+ (1 * rowcolumn-1)+35;   
        listView.setLayoutParams(params);   
    }
    
	public static void actionStart(Context context, int taskid, Handler handler, String location) {
		checkContext = context;
		task_id = taskid;
		mhandler = handler;
		mlocation = location;
		Intent intent = new Intent(context, ReadActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.readmain);
//		setTitle("查看表格"); 
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ActionBar actionBar = getActionBar();  
//	    actionBar.setDisplayHomeAsUpEnabled(true);
//	    pDialog = new SweetAlertDialog(ReadActivity.this, SweetAlertDialog.PROGRESS_TYPE);
//		pDialog.getProgressHelper().setBarColor(checkContext.getResources().getColor(R.color.alertDialog_Progress_Color));
//        pDialog.getProgressHelper().setBarWidth(5);
//        pDialog.getProgressHelper().setCircleRadius(100);
//        pDialog.getProgressHelper().setRimColor(checkContext.getResources().getColor(R.color.alertDialog_Progress_hintColor));
//        pDialog.getProgressHelper().setRimWidth(5);
//        pDialog.setTitleText("加载中..");
//        pDialog.setCancelable(false);
//        pDialog.show();
		aerospacedb = new AerospaceDB();
		listView_1 = (ListView) findViewById(R.id.read_mylistview_1);
		listView_2 = (ListView) findViewById(R.id.read_mylistview_2);
		listView_3 = (ListView) findViewById(R.id.read_mylistview_3);
//		readSwitch = (Switch) findViewById(R.id.readSwitch);
//		Task task = DataSupport.where("userid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
//    			task_id+"").find(Task.class).get(0);
//		if(task.getEndTime() != null){
//			readSwitch.setChecked(true);
//		}
//		readSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
//            @Override  
//            public void onCheckedChanged(CompoundButton buttonView,  
//                    boolean isChecked) {  
//            	Task task = DataSupport.where("userid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
//            			task_id+"").find(Task.class).get(0);
//                if(isChecked) {  	//任务完成
//                	task.setEndTime(DateUtil.getCurrentDate());
//                	task.save();
//                }else {					//任务没有完成
//                	task.setEndTime("");
//                	task.save();
//                }
//            }  
//        });
//		readback = (Button) findViewById(R.id.readback);
//		readback.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onDestroy();
//			}
//		});
		
		//table所要绑定的控件
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		mScreenHeight = dm.heightPixels;
		Log.i("宽度", mScreenWidth+"");
		Log.i("高度", mScreenHeight+"");
        headLayout = (LinearLayout)findViewById(R.id.read_linearlayout_head); 
		
        inflater = LayoutInflater.from(this);
        this.testData();//测试数据
		
		loadConditionAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid());
		loadSignAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid());
		conditionadapter = new ConditionAdapter(ReadActivity.this, R.layout.checkconditionitem, scenelists);
		signadapter = new SignAdapter(ReadActivity.this, R.layout.write_main, signlists);
//		loadTableAdapter(task_id);
//		loadSignAdapter(task_id);
		
		listView_1.setAdapter(conditionadapter);
//		listView_2.setAdapter(tableadapter);
		listView_3.setAdapter(signadapter);

//		listView_1.setOnItemClickListener(this);
//		listView_2.setOnItemClickListener(this);
//		listView_3.setOnItemClickListener(this);
//		
		setListViewHeightBasedOnChildren(listView_1);
//		setListViewHeightBasedOnChildrenTable(listView_2);
		setListViewHeightBasedOnChildren(listView_3);

		fixListViewHeight(listView_2);
	}
	
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
			cells.put(i, HorizontionCell);
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
		
//		HashMap contentMap = new HashMap();
//		this.testAddContent(contentMap);
//		adapter = new CustomeTableViewAdapter(this, lists , listView_2 , false , this.arrHeadWidth, mHandler); 
//		adapter.notifyDataSetChanged();
	}
	
	
	
	private void testAddContent(HashMap contentMap){
		
		for(int i=0; i<rowcolumn; i++){						//cells.size()是总行数
			HashMap rowMap = new HashMap();
			lists.add(rowMap);
			for(int j=0; j<linecolumn; j++){			//遍历每一行的内容
//				rowMap.put(j, cells.get(i).get(j));			//绑定UI和数据库，太复杂了
				if(cells.get(i).get(j).getType().equals("FALSE")){
					this.testAddRows(rowMap, 1, cells.get(i).get(j).getTextvalue(), CellTypeEnum.LABEL,cells.get(i).get(j));
				}else{
						List<Operation> operationList = loadOperationByCellId(cells.get(i).get(j).getCellid(), cells.get(i).get(j).getTaskid());
						if(operationList.size() > 1){			//多个operation
							boolean isPhoto = false;
							boolean isBitmap = false;
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
								this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKSTRINGPHOTO, cells.get(i).get(j));
							}else if(!isPhoto && isBitmap){		//打钩+填值+缩略图
								this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKSTRINGBITMAP, cells.get(i).get(j));
							}else if(isPhoto && isBitmap){		//打钩+填值+拍照+缩略图
								this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKSTRINGPHOTOBITMAP, cells.get(i).get(j));
							}else{											//打钩+填值
								this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKSTRING, cells.get(i).get(j));
							}
						}else{
							boolean isBitmap = false;
							if(!operationList.get(0).getSketchmap().equals("") && operationList.get(0).getSketchmap() != null){
								isBitmap = true;
							}
							if(operationList.get(0).getType().equals("1")){		//打钩项
								List<Integer> pows = CalculateUtil.CalculateOperationItem(operationList.get(0).getOperationtype());
								if(pows.contains(128) && !isBitmap){				//打钩+拍照
									this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKPHOTO, cells.get(i).get(j));
								}else if(pows.contains(128) && isBitmap){		//打钩+拍照+缩略图
									this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKPHOTOBITMAP, cells.get(i).get(j));
								}else if(!pows.contains(128) && isBitmap){	//打钩+缩略图
									this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOKBITMAP, cells.get(i).get(j));
								}else{															//打钩
									this.testAddRows(rowMap, 1, "", CellTypeEnum.HOOK, cells.get(i).get(j));
								}
							}else{		//填值项
								List<Integer> pows = CalculateUtil.CalculateOperationItem(operationList.get(0).getOperationtype());
								if(pows.contains(128) && !isBitmap){				//填值+拍照
									this.testAddRows(rowMap, 1, "", CellTypeEnum.STRINGPHOTO, cells.get(i).get(j));
								}else if(pows.contains(128) && isBitmap){		//填值+拍照+缩略图
									this.testAddRows(rowMap, 1, "", CellTypeEnum.STRINGPHOTOBITMAP, cells.get(i).get(j));
								}else if(!pows.contains(128) && isBitmap){	//填值+缩略图
									this.testAddRows(rowMap, 1, "", CellTypeEnum.STRINGBITMAP, cells.get(i).get(j));
								}else{															//填值
									this.testAddRows(rowMap, 1, "", CellTypeEnum.STRING, cells.get(i).get(j));
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
	
	public Document htmlDoc = null;				//该表格的HTML网页
	/**
	 * 添加每一行
	 * @param rowMap
	 * @param colSpan
	 * @param cellValue
	 * @param cellType
	 */
	private void testAddRows(HashMap rowMap,int colSpan,String cellValue,CellTypeEnum cellType, Cell cell){
//		Task task = DataSupport.where("userid=? and rwid=? and taskid = ?", OrientApplication.getApplication().loginUser.getUserid(),
//    			OrientApplication.getApplication().rw.getRwid(), cell.getTaskid()).find(Task.class).get(0);
		Task task = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), cell.getTaskid()).find(Task.class).get(0);
		htmlDoc = HtmlHelper.getHtmlDoc(task);
	    ItemCell itemCell = new ItemCell(cellValue,cellType,colSpan, cell, htmlDoc);	
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
	private void loadSignAdapter(int taskid, String userid){
		List<Signature> signs = aerospacedb.loadSignatureAdapter(taskid, userid);
		signlists = signs;
	}
	
	//加载Condition数据
	private void loadConditionAdapter(int taskid, String userid){
		List<Scene> conditions = aerospacedb.loadConditionAdapter(taskid, userid);
		scenelists = conditions;
	}
	
	//加载Table数据
	private List<Cell> loadCellAdapter(int taskid, String userid){
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
	
	private List<Operation> loadOperationByCellId(String cellid, String taskId){
		List<Operation> operationList = aerospacedb.loadOperationByCellId(cellid, taskId);
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
	
	@Override  
    public void onWindowFocusChanged(boolean hasFocus){  
        if(hasFocus){  
//        	Message message = new Message();  
//            message.what = 0;  
//            mHandler.sendMessage(message);
//        	pDialog.dismiss();
        }  
    }  

}
