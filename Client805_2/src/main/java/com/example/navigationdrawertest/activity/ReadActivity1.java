package com.example.navigationdrawertest.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.Subscribe;
import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.navigationdrawertest.CustomUI.NumImageButton;
import com.example.navigationdrawertest.CustomUI.ObservableScrollView;
import com.example.navigationdrawertest.CustomUI.SyncHorizontalScrollView;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.CustomUI.NoScrollListview;
import com.example.navigationdrawertest.CustomUI.MyScrollView.OnScrollListener;
import com.example.navigationdrawertest.adapter.ConditionAdapter;
import com.example.navigationdrawertest.adapter.ConditionAdapter1;
import com.example.navigationdrawertest.adapter.Event;
import com.example.navigationdrawertest.adapter.SignAdapter;
import com.example.navigationdrawertest.adapter.SignAdapter1;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.camera.AlbumAty;
import com.example.navigationdrawertest.data.AerospaceDB;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Scene;
import com.example.navigationdrawertest.model.Signature;
import com.example.navigationdrawertest.model.Task;
import com.example.navigationdrawertest.utils.ActivityCollector;
import com.example.navigationdrawertest.utils.BitmapUtil;
import com.example.navigationdrawertest.utils.CommonTools;
import com.example.navigationdrawertest.utils.Config;
import com.example.navigationdrawertest.utils.HtmlHelper;
import com.example.navigationdrawertest.utils.ScreenUtils;
import com.example.navigationdrawertest.utils.Utility;

import de.greenrobot.event.EventBus;

public class ReadActivity1 extends BaseActivity implements ObservableScrollView.Callbacks {
	
	private TableLayout table_headertest;
	private TableLayout table_header;
	private TableLayout table_content;
	private Context context;
	private int rowCount = 0;			//行数
	private int cellCount = 0;				//列数
	private List<Cell> cellList;				//本表格所有的CELL集合
	private AerospaceDB aerospacedb;
	private Task currentTask;				//当前表格
	private int width;							//屏幕总宽度
	private int avewdith;					//平均宽度
	private List<Cell> headMap = new ArrayList<Cell>();			//head的Cell集合
	public Document htmlDoc = null;
	private static long task_id;					//表格ID
	private int picturenumbers = 0;
	
	//表格之外的其他布局
	private com.example.navigationdrawertest.CustomUI.NoScrollListview listView_1;
//	private com.example.navigationdrawertest.CustomUI.NoScrollListview listView_3;
	private ListView listView_3;
//	private ListView listView_1, listView_3;
	private List<Scene> scenelists = new ArrayList<Scene>();
	private List<Signature> signlists = new ArrayList<Signature>();
//	private ConditionAdapter conditionadapter = null;
	private ConditionAdapter1 conditionadapter;
	private SignAdapter1 signadapter = null;
	private SyncHorizontalScrollView myScrollView, titleHorScr;

	private ImageView mBack, mClose;
	private TextView mTablename, mTotalPhNum;

	private LinearLayout mReadSign;
	private RelativeLayout mBottom;
	private Button mProview, mNext;
	private int rowsnum;
	private int pagetype;
	private int totalPhNumber = 0;

	public static void actionStart(Context context, long taskid, Handler handler, String location) {
		Intent intent = new Intent(context, ReadActivity1.class);
		task_id = taskid;
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		setContentView(R.layout.readmain1);
		getActionBar().hide();
		EventBus.getDefault().register(this);

		context = this;
		initUI(); 						// 初始化UI控件
		initParam();				 	// 初始化必要的全局参数

		initHeaderUI();
		initContentUI();
		setTitle(currentTask.getTaskname());
		mTablename.setText(currentTask.getTaskname());
		loadConditionAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid());
		loadSignAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid());
//		conditionadapter = new ConditionAdapter(ReadActivity1.this, R.layout.checkconditionitem, scenelists);
		conditionadapter = new ConditionAdapter1(ReadActivity1.this, scenelists);
		signadapter = new SignAdapter1(ReadActivity1.this, signlists);
		listView_1.setAdapter(conditionadapter);
		listView_3.setAdapter(signadapter);
		Utility.setListViewHeightBasedOnChildren(listView_3);
		
		myScrollView = (SyncHorizontalScrollView) findViewById(R.id.parent_scroll);
		titleHorScr = (SyncHorizontalScrollView) findViewById(R.id.title_horsv);
		myScrollView.setScrollView(titleHorScr);
		titleHorScr.setScrollView(myScrollView);

		// 当布局的状态或者控件的可见性发生改变回调的接口
//		findViewById(R.id.parent_layout).getViewTreeObserver()
//				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//
//					@Override
//					public void onGlobalLayout() {
//						// 这一步很重要，使得上面的购买布局和下面的购买布局重合
//						onScroll(myScrollView.getScrollY());
//
//						System.out.println(myScrollView.getScrollY());
//					}
//				});
		
		mTotalPhNum.setText("照片数量：" + String.valueOf(totalPhNumber));
	}
	
	private void initUI(){
		table_header = (TableLayout) findViewById(R.id.readtable_header);
		table_content = (TableLayout) findViewById(R.id.readtable_content);
		listView_1 = (NoScrollListview) findViewById(R.id.read_mylistview_1);
		listView_3 = (ListView) findViewById(R.id.read_mylistview_3);
		mTablename = (TextView) findViewById(R.id.table_name);
		mTotalPhNum = (TextView) findViewById(R.id.tv_totalPhNum);
		mReadSign = (LinearLayout) findViewById(R.id.read_signname);
		mClose = (ImageView) findViewById(R.id.read_close);
		mClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new Event.LocationEvent("finish"));
				OrientApplication.app.setPageflage(1);
				ReadActivity1.this.finish();
			}
		});
		mBack = (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				if (OrientApplication.app.getPageflage() > 1) {
					OrientApplication.app.setPageflage(pagetype-1);
				}
				finish();
			}
		});
		mBottom = (RelativeLayout) findViewById(R.id.read_bottom);
		mProview = (Button) findViewById(R.id.read_proview);
		mNext = (Button) findViewById(R.id.read_next);
		mProview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (OrientApplication.app.getPageflage() > 1) {
					OrientApplication.app.setPageflage(pagetype-1);
				}
				finish();
			}
		});
		mNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ReadActivity1.this, ReadActivity1.class);
				if (pagetype < rowsnum) {
					OrientApplication.app.setPageflage(pagetype + 1);
				}
				startActivity(intent);

			}
		});
	}
	
	// 初始化参数
	private void initParam() {
		currentTask = DataSupport.where("rwid=? and taskid = ?", OrientApplication.getApplication().rw.getRwid(), task_id+"").find(Task.class).get(0);

		rowsnum = Integer.parseInt(currentTask.getTablesize());

		pagetype = OrientApplication.app.getPageflage();

		if (rowsnum > 1) {
			mBottom.setVisibility(View.VISIBLE);
			if (pagetype == 1) {
				mProview.setVisibility(View.GONE);
				mNext.setVisibility(View.VISIBLE);
				mReadSign.setVisibility(View.GONE);
				listView_1.setVisibility(View.VISIBLE);

			} else if (pagetype > 1 && pagetype < rowsnum) {
				mProview.setVisibility(View.VISIBLE);
				mNext.setVisibility(View.VISIBLE);
				mReadSign.setVisibility(View.GONE);
				listView_1.setVisibility(View.GONE);

			} else if (pagetype == rowsnum) {
				mProview.setVisibility(View.VISIBLE);
				mNext.setVisibility(View.GONE);
				mReadSign.setVisibility(View.VISIBLE);
				listView_1.setVisibility(View.GONE);

			}
		} else {
			mBottom.setVisibility(View.GONE);
			mReadSign.setVisibility(View.VISIBLE);
			listView_1.setVisibility(View.VISIBLE);
		}

		htmlDoc = HtmlHelper.getHtmlDoc(currentTask);
		aerospacedb = new AerospaceDB();
		//获得该表格的所有CELL数据
		if (rowsnum > 1) {
			cellList = loadCellAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid(), pagetype);
		} else {
			cellList = loadCellAdapter(task_id, OrientApplication.getApplication().loginUser.getUserid(), 1);
		}

		width = ScreenUtils.getScreenWidth(context);
		headMap = DataSupport.where("horizontalorder=? and taskid=? and rowsid=?", "1", task_id+"", String.valueOf(pagetype)).order("verticalorder asc").find(Cell.class);
		if(headMap.size() > 8){
			avewdith = 250;
		}else{
			avewdith = width/headMap.size();
		}
		cellCount = headMap.size();
		rowCount = cellList.size()/cellCount;
		headMap.clear();
		for(int i=1; i<=cellCount; i++){
			Cell cell = DataSupport.where("horizontalorder=? and taskid=? and verticalorder=? and rowsid=?", "1", task_id+"", i+"", String.valueOf(pagetype)).find(Cell.class).get(0);
			headMap.add(cell);
		}
	}
	
	/**
	 * 初始化Header
	 */
	private void initHeaderUI(){
		TableRow tablerow = new TableRow(context);
		tablerow.setBackgroundColor(Color.rgb(236, 247, 82));
		tablerow.setGravity(Gravity.CENTER_VERTICAL);
		for (Cell cell : headMap) {
			android.widget.TableRow.LayoutParams para4 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
			android.widget.TableRow.LayoutParams para411 = new android.widget.TableRow.LayoutParams(avewdith-1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
			android.widget.TableRow.LayoutParams para412 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
			LinearLayout linear4 = new LinearLayout(context);
			linear4.setOrientation(LinearLayout.HORIZONTAL);
			linear4.setLayoutParams(para4);
			//初始化EditText
			TextView textview = new TextView(context);
			String labelName1 = cell.getRowname();
			textview.setGravity(Gravity.CENTER);
//			textview.setText(HtmlHelper.transCellLabel(labelName1));
			textview.setText(CheckActivity1.replaceStr(labelName1));
			textview.setTextSize(16);
			linear4.addView(textview, para411);
			//初始化textInfo
			ImageView image = new ImageView(context);
			image.setBackgroundResource(R.drawable.blacktiao);
			linear4.addView(image, para412);
			tablerow.addView(linear4, para4);
		}
		table_header.addView(tablerow, new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 55));
	}
	
	/**
	 * 初始化内容(type=1,checkbox    2,edittext)
	 */
	private void initContentUI(){
		for(int i=1; i<=rowCount; i++){
			TableRow tablerow = new TableRow(context);
			if (i % 2 == 0)
				tablerow.setBackgroundColor(Color.rgb(255, 255, 255));
			else
				tablerow.setBackgroundColor(Color.rgb(153, 204, 255));
			List<Cell> cellList = DataSupport.where("horizontalorder=? and taskid=? and rowsid=?", i+"", task_id+"", String.valueOf(pagetype)).order("verticalorder asc").find(Cell.class);
			List<Cell> newCellList = new ArrayList<Cell>();
			for(int j=1; j<=cellList.size(); j++){
				for(int k=1; k<=cellList.size(); k++){
					Cell cell = cellList.get(k-1);
					if(cell.getVerticalorder().equals(j+"")){
						newCellList.add(cell);
						break;
					}
				}
			}
			for(final Cell cell : newCellList){
				switch(cell.getCelltype()){
				case "LABEL":
					android.widget.TableRow.LayoutParams para1 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para1_1 = new android.widget.TableRow.LayoutParams(avewdith-1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para1_2 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear0 = new LinearLayout(context);
					linear0.setOrientation(LinearLayout.HORIZONTAL);
					linear0.setLayoutParams(para1);
					//初始化EditText
					TextView textview = new TextView(context);
					String labelName = cell.getTextvalue();
					textview.setGravity(Gravity.CENTER);
					textview.setWidth(avewdith);
//					textview.setHeight(100);
					textview.setTextSize(16);
//					textview.setText(HtmlHelper.transCellLabel(labelName));
					textview.setText(CheckActivity1.replaceStr(labelName));
					linear0.addView(textview, para1_1);
					//初始化textInfo
					ImageView image = new ImageView(context);
					image.setBackgroundResource(R.drawable.blacktiao);
					linear0.addView(image, para1_2);
					tablerow.addView(linear0, para1);
					break;
				case "STRING":
					android.widget.TableRow.LayoutParams para2 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para2_1 = new android.widget.TableRow.LayoutParams(avewdith-1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para2_2 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para2_3 = new android.widget.TableRow.LayoutParams(80, 70);
					android.widget.TableRow.LayoutParams para2_4 = new android.widget.TableRow.LayoutParams(avewdith-81, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear2 = new LinearLayout(context);
					linear2.setOrientation(LinearLayout.HORIZONTAL);
					linear2.setLayoutParams(para2);
					//初始化EditText
					final Operation operation2 = DataSupport.where("cellid=? and taskid=?", cell.getCellid(), task_id+"").find(Operation.class).get(0);
					final String str = CommonTools.null2String(operation2.getOpvalue());
					final ImageButton signButton = new ImageButton(context);
					final ImageView signImage = new ImageView(context);
					final String markup = cell.getMarkup();
					final List<Signature> signatureList = DataSupport.where("signid=?", cell.getCellid()).find(Signature.class);
//					final Signature signnature = new Signature();
					if (signatureList.size() > 0) {
//						signnature = signatureList.get(0);
						String _path = CommonTools.null2String(signatureList.get(0).getBitmappath());
						Bitmap SignBitmap = BitmapUtil.getLoacalBitmap(_path);
						if (SignBitmap != null) {
							signImage.setImageBitmap(SignBitmap);
						}
					}
					signButton.setBackgroundResource(R.drawable.takephoto);
					EditText edittext2 = new EditText(context);
					edittext2.setText(CheckActivity1.replaceStr(str));
					edittext2.setTextSize(16);
					edittext2.setEnabled(false);
					if (!markup.equals("") && markup.equals("sign")) {
						linear2.addView(signImage, para2_4);
						linear2.addView(signButton, para2_3);
					} else {
						linear2.addView(edittext2, para2_1);
					}
					//初始化textInfo
					ImageView image2 = new ImageView(context);
					image2.setBackgroundResource(R.drawable.blacktiao);
					linear2.addView(image2, para2_2);
					tablerow.addView(linear2, para2);
					break;
				case "DIGIT":
					break;
				case "HOOK":
					android.widget.TableRow.LayoutParams layoutParams3 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams layoutParams3_1 = new android.widget.TableRow.LayoutParams((avewdith-1)/2, 100);
					android.widget.TableRow.LayoutParams layoutParams3_2 = new android.widget.TableRow.LayoutParams(1, 100);
					LinearLayout linear3 = new LinearLayout(context);
					linear3.setOrientation(LinearLayout.HORIZONTAL);
					linear3.setLayoutParams(layoutParams3);
					layoutParams3.gravity = Gravity.CENTER;
					layoutParams3_1.setMargins((avewdith-1)/2, 0, 0, 0);				//左上右下
					CheckBox cb3 = new CheckBox(context);
					final Operation operation3 = DataSupport.where("cellid=? and taskid=?", cell.getCellid(), task_id+"").find(Operation.class).get(0);
					String value = CommonTools.null2String(operation3.getOpvalue());
					if(value != null){
						if(value.equals("is")){				//打钩状态
							cb3.setChecked(true);
						}else if(value.equals("no")){	//不打钩状态
							cb3.setChecked(false);
						}else{
							cb3.setChecked(false);
						}
					}
					cb3.setEnabled(false);
					ImageView img = new ImageView(this);
					img.setBackgroundResource(R.drawable.blacktiao);
					linear3.addView(cb3, layoutParams3_1);
					linear3.addView(img, layoutParams3_2);
					tablerow.addView(linear3, layoutParams3);
					break;
				case "HOOKPHOTO":
					android.widget.TableRow.LayoutParams para4 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para4_1 = new android.widget.TableRow.LayoutParams((avewdith-81)/2, 100);
					android.widget.TableRow.LayoutParams para4_2 = new android.widget.TableRow.LayoutParams(80, 70);
					android.widget.TableRow.LayoutParams para4_3= new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear4 = new LinearLayout(context);
					para4_1.gravity = Gravity.CENTER;
					para4_1.setMargins((avewdith-81)/2, 0, 0, 0);				//左上右下
					linear4.setOrientation(LinearLayout.HORIZONTAL);
					linear4.setLayoutParams(para4);
					//初始化EditText
					CheckBox checkbox4 = new CheckBox(context);
//					checkbox4.setGravity(Gravity.CENTER);
					final Operation operation4 = DataSupport.where("cellid=? and taskid=?", cell.getCellid(), task_id+"").find(Operation.class).get(0);
					String value4 = operation4.getOpvalue();
		    		if(value4 != null){
						if(value4.equals("is")){				//打钩状态
							checkbox4.setChecked(true);
						}else if(value4.equals("no")){	//不打钩状态
							checkbox4.setChecked(false);
						}else{
							checkbox4.setChecked(false);
						}
					}
					checkbox4.setEnabled(false);
					linear4.addView(checkbox4, para4_1);
					//初始化textInfo
					NumImageButton image4_1 = new NumImageButton(context);

					if (picturenumbers == 0) {
						if (operation4 != null) {
							final String path1 = Environment.getExternalStorageDirectory() + Config.v2photoPath
									+ File.separator
									+ OrientApplication.getApplication().rw.getRwid()
									+ File.separator
									+ cell.getTaskid()
									+ File.separator
									+ CommonTools.null2String(operation4.getOperationid())
									+ File.separator;
							getPictures(path1);
						}
					}
					image4_1.setNum(picturenumbers);
					totalPhNumber = totalPhNumber + picturenumbers;
					picturenumbers = 0;
					image4_1.setBackgroundResource(R.drawable.albumphoto);
					image4_1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
//							HtmlHelper.changePhotoValue(htmlDoc, operation4);
//							AlbumAty.actionStart2(context, cell.getTaskid(), cell.getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
							transAlbum(cell.getCellid(), cell.getTaskid(), operation4);
							
						}
					});
					linear4.addView(image4_1, para4_2);
					ImageView image4 = new ImageView(context);
					image4.setBackgroundResource(R.drawable.blacktiao);
					linear4.addView(image4, para4_3);
					tablerow.addView(linear4, para4);
					break;
				case "STRINGPHOTO":
					android.widget.TableRow.LayoutParams para5 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para5_1 = new android.widget.TableRow.LayoutParams(avewdith-81, 100);
					android.widget.TableRow.LayoutParams para5_2 = new android.widget.TableRow.LayoutParams(80, 70);
					android.widget.TableRow.LayoutParams para5_3= new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear5 = new LinearLayout(context);
					linear5.setOrientation(LinearLayout.HORIZONTAL);
					linear5.setLayoutParams(para5);
					//初始化EditText
					final EditText edit5 = new EditText(context);
					final Operation operation5 = DataSupport.where("cellid=? and taskid=?", cell.getCellid(), task_id+"").find(Operation.class).get(0);
					String stringdata5 = CommonTools.null2String(operation5.getOpvalue());
					edit5.setText(CheckActivity1.replaceStr(stringdata5));
					edit5.setTextSize(16);
					linear5.addView(edit5, para5_1);
					//初始化textInfo
					NumImageButton image5 = new NumImageButton(context);

					if (picturenumbers == 0) {
						if (operation5 != null) {
							final String path1 = Environment.getExternalStorageDirectory() + Config.v2photoPath
									+ File.separator
									+ OrientApplication.getApplication().rw.getRwid()
									+ File.separator
									+ cell.getTaskid()
									+ File.separator
									+ CommonTools.null2String(operation5.getOperationid())
									+ File.separator;
							getPictures(path1);
						}
					}
					image5.setNum(picturenumbers);
					totalPhNumber = totalPhNumber + picturenumbers;
					picturenumbers = 0;
					image5.setBackgroundResource(R.drawable.albumphoto);
					image5.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
//							AlbumAty.actionStart2(context, cell.getTaskid(), cell.getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
							transAlbum(cell.getCellid(), cell.getTaskid(), operation5);
						}
					});
					edit5.setEnabled(false);
					linear5.addView(image5, para5_2);
					ImageView image5_1 = new ImageView(context);
					image5_1.setBackgroundResource(R.drawable.blacktiao);
					linear5.addView(image5_1, para5_3);
					tablerow.addView(linear5, para5);
					break;
				case "HOOKSTRING":
					android.widget.TableRow.LayoutParams para6 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para6_1 = new android.widget.TableRow.LayoutParams(avewdith-81, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para6_2 = new android.widget.TableRow.LayoutParams(80, 80);
					android.widget.TableRow.LayoutParams para6_3 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear6 = new LinearLayout(context);
					linear6.setOrientation(LinearLayout.HORIZONTAL);
					linear6.setLayoutParams(para6);
					//初始化EditText
					final EditText edit6 = new EditText(context);
					final Operation operation6_1 = DataSupport.where("cellid=? and taskid=? and type=?", cell.getCellid(), task_id+"", "1").find(Operation.class).get(0);			//checkbox
					final Operation operation6_2 = DataSupport.where("cellid=? and taskid=? and type=?", cell.getCellid(), task_id+"", "2").find(Operation.class).get(0);			//edittext
					String stringdata6 = CommonTools.null2String(operation6_2.getOpvalue());
	    			edit6.setText(CheckActivity1.replaceStr(stringdata6));
	    			edit6.setTextSize(16);
					linear6.addView(edit6, para6_1);
					//初始化checkbox
					CheckBox checkbox6 = new CheckBox(context);
					String value6 = operation6_1.getOpvalue();
		    		if(value6 != null){
						if(value6.equals("is")){				//打钩状态
							checkbox6.setChecked(true);
						}else if(value6.equals("no")){	//不打钩状态
							checkbox6.setChecked(false);
						}else{
							checkbox6.setChecked(false);
						}
					}
		    		checkbox6.setEnabled(false);
					linear6.addView(checkbox6, para6_2);
					edit6.setEnabled(false);
					ImageView image6_1 = new ImageView(context);
					image6_1.setBackgroundResource(R.drawable.blacktiao);
					linear6.addView(image6_1, para6_3);
					tablerow.addView(linear6, para6);
					break;
				case "HOOKSTRINGPHOTO":
					android.widget.TableRow.LayoutParams para7 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para7_1 = new android.widget.TableRow.LayoutParams(60, 60);			//checkbox
					android.widget.TableRow.LayoutParams para7_2 = new android.widget.TableRow.LayoutParams(avewdith-141, android.widget.TableRow.LayoutParams.MATCH_PARENT);			//string
					android.widget.TableRow.LayoutParams para7_3 = new android.widget.TableRow.LayoutParams(80, 70);			//photo
					android.widget.TableRow.LayoutParams para7_4 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear7 = new LinearLayout(context);
					linear7.setOrientation(LinearLayout.HORIZONTAL);
					linear7.setLayoutParams(para7);
					//初始化EditText
					final EditText edit7 = new EditText(context);
					final Operation operation71 = DataSupport.where("cellid=? and taskid=? and type=?", cell.getCellid(), task_id+"", "1").find(Operation.class).get(0);			//checkbox
					final Operation operation72 = DataSupport.where("cellid=? and taskid=? and type=?", cell.getCellid(), task_id+"", "2").find(Operation.class).get(0);			//edittext
					String stringdata7 = CommonTools.null2String(operation72.getOpvalue());
	    			edit7.setText(CheckActivity1.replaceStr(stringdata7));
	    			edit7.setTextSize(16);
					linear7.addView(edit7, para7_2);
					//初始化checkbox
					CheckBox cb7 = new CheckBox(context);
					String value7 = CommonTools.null2String(operation71.getOpvalue());
					if(value7 != null){
						if(value7.equals("is")){						//打钩状态
							cb7.setChecked(true);
						}else if(value7.equals("no")){			//不打钩状态
							cb7.setChecked(false);
						}else{
							cb7.setChecked(false);
						}
					}
					linear7.addView(cb7, para7_1);
					cb7.setEnabled(false);
					edit7.setEnabled(false);
					NumImageButton image7_1 = new NumImageButton(context);

					if (picturenumbers == 0) {
						if (operation72 != null) {
							final String path1 = Environment.getExternalStorageDirectory() + Config.v2photoPath
									+ File.separator
									+ OrientApplication.getApplication().rw.getRwid()
									+ File.separator
									+ cell.getTaskid()
									+ File.separator
									+ CommonTools.null2String(operation72.getOperationid())
									+ File.separator;
							getPictures(path1);
						}
					}
					image7_1.setNum(picturenumbers);
					totalPhNumber = totalPhNumber + picturenumbers;
					picturenumbers = 0;
					image7_1.setBackgroundResource(R.drawable.albumphoto);
					image7_1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
//							AlbumAty.actionStart2(context, cell.getTaskid(), cell.getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
							Operation temp = null;
							if(operation71.getIsmedia().equals("TRUE")){
								temp = operation71;
								HtmlHelper.changePhotoValue(htmlDoc, operation71);
							}
							if(operation72.getIsmedia().equals("TRUE")){
								temp = operation72;
								HtmlHelper.changePhotoValue(htmlDoc, operation72);
							}
							if(temp != null)
								transAlbum(cell.getCellid(), cell.getTaskid(), operation72);
						}
					});
					linear7.addView(image7_1, para7_3);
					ImageView image7_2 = new ImageView(context);
					image7_2.setBackgroundResource(R.drawable.blacktiao);
					linear7.addView(image7_2, para7_4);
					tablerow.addView(linear7, para7);
					break;
				case "HOOKBITMAP":
					android.widget.TableRow.LayoutParams para8 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para8_1 = new android.widget.TableRow.LayoutParams((avewdith-81)/2, 60);			//checkbox
					android.widget.TableRow.LayoutParams para8_2 = new android.widget.TableRow.LayoutParams(80, 80);				//bitmap
					android.widget.TableRow.LayoutParams para8_3 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear8 = new LinearLayout(context);
					linear8.setOrientation(LinearLayout.HORIZONTAL);
					linear8.setLayoutParams(para8);
					//初始化EditText
					final Operation operation8 = DataSupport.where("cellid=? and taskid=?", cell.getCellid(), task_id+"").find(Operation.class).get(0);		
					final String opId8 = operation8.getSketchmap();
					final String userId8 = OrientApplication.getApplication().loginUser.getUserid();
		    		final String taskId8 = cell.getTaskid();
					//初始化checkbox
					CheckBox cb8 = new CheckBox(context);
					para8_1.setMargins((avewdith-81)/2, 0, 0, 0);
					linear8.addView(cb8, para8_1);
					String value8 = CommonTools.null2String(operation8.getOpvalue());
					if(value8 != null){
						if(value8.equals("is")){						//打钩状态
							cb8.setChecked(true);
						}else if(value8.equals("no")){				//不打钩状态
							cb8.setChecked(false);
						}else{
							cb8.setChecked(false);
						}
					}
					cb8.setEnabled(false);
					final String absPath = Environment.getExternalStorageDirectory()
							+ Config.opphotoPath + "/"
							+ taskId8 + "/" + opId8 + ".jpg";
		    		Bitmap bitmap = BitmapFactory.decodeFile(absPath);
					ImageView image8 = new ImageButton(context);
					if(bitmap != null)
//						image8.setImageBitmap(bitmap);
						Glide
				    	    .with(context)
				    	    .load(absPath)
				    	    .override(80, 80)
				    	    .fitCenter()
				    	    .into(image8);
						image8.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								BitmapDialogActivity.actionStart(context, userId8, taskId8, opId8);
						}
					});
					linear8.addView(image8, para8_2);
					ImageView image8_2 = new ImageView(context);
					image8_2.setBackgroundResource(R.drawable.blacktiao);
					linear8.addView(image8_2, para8_3);
					tablerow.addView(linear8, para8);
					break;
				case "HOOKSTRINGBITMAP":
					android.widget.TableRow.LayoutParams para9 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para9_1 = new android.widget.TableRow.LayoutParams(60, 60);			//checkbox
					android.widget.TableRow.LayoutParams para9_2 = new android.widget.TableRow.LayoutParams(avewdith-141, android.widget.TableRow.LayoutParams.MATCH_PARENT);				//bitmap
					android.widget.TableRow.LayoutParams para9_3 = new android.widget.TableRow.LayoutParams(80, 80);
					android.widget.TableRow.LayoutParams para9_4 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear9 = new LinearLayout(context);
					linear9.setOrientation(LinearLayout.HORIZONTAL);
					linear9.setLayoutParams(para9);
					//初始化EditText
					final EditText edit9 = new EditText(context);
					final Operation operation91 = DataSupport.where("cellid=? and taskid=? and type=?", cell.getCellid(), task_id+"", "1").find(Operation.class).get(0);			//checkbox
					final Operation operation92 = DataSupport.where("cellid=? and taskid=? and type=?", cell.getCellid(), task_id+"", "2").find(Operation.class).get(0);			//edittext
					String stringdata9 = CommonTools.null2String(operation92.getOpvalue());
	    			edit9.setText(CheckActivity1.replaceStr(stringdata9));
	    			edit9.setTextSize(16);
					linear9.addView(edit9, para9_2);
					//初始化checkbox
					CheckBox cb9 = new CheckBox(context);
					linear9.addView(cb9, para9_1);
					String value9 = CommonTools.null2String(operation91.getOpvalue());
					if(value9 != null){
						if(value9.equals("is")){						//打钩状态
							cb9.setChecked(true);
						}else if(value9.equals("no")){			//不打钩状态
							cb9.setChecked(false);
						}else{
							cb9.setChecked(false);
						}
					}
					cb9.setEnabled(false);
					edit9.setEnabled(false);
					String opId9 = "";
					if(!operation91.getSketchmap().equals("") &&  operation91.getSketchmap() != null){
						opId9 = operation91.getSketchmap();
	    			}
					if(!operation92.getSketchmap().equals("") &&  operation92.getSketchmap() != null){
						opId9 = operation92.getSketchmap();
	    			}
					final String sketbitmap = opId9;
					final String userId9 = OrientApplication.getApplication().loginUser.getUserid();
		    		final String taskId9 = cell.getTaskid();
					final String absPath9 = Environment.getExternalStorageDirectory()
							+ Config.opphotoPath + "/"
							+ taskId9 + "/" + sketbitmap + ".jpg";
		    		Bitmap bitmap9 = BitmapFactory.decodeFile(absPath9);
					ImageView image9 = new ImageButton(context);
					if(bitmap9 != null)
//						image9.setImageBitmap(bitmap9);
						Glide
				    	    .with(context)
				    	    .load(absPath9)
				    	    .override(80, 80)
				    	    .fitCenter()
				    	    .into(image9);
						image9.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								BitmapDialogActivity.actionStart(context, userId9, taskId9, sketbitmap);
						}
					});
					linear9.addView(image9, para9_3);
					ImageView image9_2 = new ImageView(context);
					image9_2.setBackgroundResource(R.drawable.blacktiao);
					linear9.addView(image9_2, para9_4);
					tablerow.addView(linear9, para9);
					break;
				case "HOOKSTRINGPHOTOBITMAP":
					android.widget.TableRow.LayoutParams para10 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para10_1 = new android.widget.TableRow.LayoutParams(60, 60);				//checkbox
					android.widget.TableRow.LayoutParams para10_2 = new android.widget.TableRow.LayoutParams(avewdith-221, 100);				//string
					android.widget.TableRow.LayoutParams para10_3 = new android.widget.TableRow.LayoutParams(80, 80);				//photo
					android.widget.TableRow.LayoutParams para10_4 = new android.widget.TableRow.LayoutParams(80, 80);				//bitmap
					android.widget.TableRow.LayoutParams para10_5 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear10 = new LinearLayout(context);
					linear10.setOrientation(LinearLayout.HORIZONTAL);
					linear10.setLayoutParams(para10);
					//初始化EditText
					final Operation operation10_1 = DataSupport.where("cellid=? and taskid=? and type=?", cell.getCellid(), task_id+"", "1").find(Operation.class).get(0);
					final Operation operation10_2 = DataSupport.where("cellid=? and taskid=? and type=?", cell.getCellid(), task_id+"", "2").find(Operation.class).get(0);
					//初始化EditText
					String stringdata10 = CommonTools.null2String(operation10_2.getOpvalue());
					final EditText edit10 = new EditText(context);
	    			edit10.setText(CheckActivity1.replaceStr(stringdata10));
	    			edit10.setTextSize(16);
					linear10.addView(edit10, para10_2);
					//初始化checkbox
					CheckBox cb10 = new CheckBox(context);
					para10_1.gravity = Gravity.CENTER;
					linear10.addView(cb10, para10_1);
					String value10 = CommonTools.null2String(operation10_1.getOpvalue());
					if(value10 != null){
						if(value10.equals("is")){						//打钩状态
							cb10.setChecked(true);
						}else if(value10.equals("no")){			//不打钩状态
							cb10.setChecked(false);
						}else{
							cb10.setChecked(false);
						}
					}
					cb10.setEnabled(false);
					edit10.setEnabled(false);
					String opId10 = "";
					if(!operation10_1.getSketchmap().equals("") &&  operation10_1.getSketchmap() != null){
						opId10 = operation10_1.getSketchmap();
	    			}
					if(!operation10_2.getSketchmap().equals("") &&  operation10_2.getSketchmap() != null){
						opId10 = operation10_2.getSketchmap();
	    			}
					final String sketbitmap10 = opId10;
					final String userId10 = OrientApplication.getApplication().loginUser.getUserid();
		    		final String taskId10 = cell.getTaskid();
		    		final String rowNum10 = cell.getHorizontalorder();
		    		final String cellId10 = cell.getCellid();
					final String absPath10 = Environment.getExternalStorageDirectory()
							+ Config.opphotoPath + "/"
							+ taskId10 + "/" + sketbitmap10 + ".jpg";
		    		Bitmap bitmap10 = BitmapFactory.decodeFile(absPath10);
					ImageView image10 = new ImageButton(context);
					if(bitmap10 != null)
//						image10.setImageBitmap(bitmap10);
						Glide
				    	    .with(context)
				    	    .load(absPath10)
				    	    .override(80, 80)
				    	    .fitCenter()
				    	    .into(image10);
						image10.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								BitmapDialogActivity.actionStart(context, userId10, taskId10, sketbitmap10);
						}
					});
					linear10.addView(image10, para10_3);

					NumImageButton image10_1 = new NumImageButton(context);

					if (picturenumbers == 0) {
						Operation operation = null;
						if(operation10_1.getIsmedia().equals("TRUE")){
							operation = operation10_1;

						}
						if(operation10_2.getIsmedia().equals("TRUE")){
							operation = operation10_2;

						}
						if (operation != null) {
							final String path1 = Environment.getExternalStorageDirectory() + Config.v2photoPath
									+ File.separator
									+ OrientApplication.getApplication().rw.getRwid()
									+ File.separator
									+ cell.getTaskid()
									+ File.separator
									+ CommonTools.null2String(operation.getOperationid())
									+ File.separator;
							getPictures(path1);
						}
					}
					image10_1.setNum(picturenumbers);
					totalPhNumber = totalPhNumber + picturenumbers;
					picturenumbers = 0;

					image10_1.setBackgroundResource(R.drawable.albumphoto);
					image10_1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
//							AlbumAty.actionStart2(context, cell.getTaskid(), cell.getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
							Operation temp = null;
							if(operation10_1.getIsmedia().equals("TRUE")){
								temp = operation10_1;
							}
							if(operation10_2.getIsmedia().equals("TRUE")){
								temp = operation10_2;
							}
							if(temp != null)
								transAlbum(cell.getCellid(), cell.getTaskid(), temp);
						}
					});
					linear10.addView(image10_1, para10_4);
					ImageView image10_2 = new ImageView(context);
					image10_2.setBackgroundResource(R.drawable.blacktiao);
					linear10.addView(image10_2, para10_5);
					tablerow.addView(linear10, para10);
					break;
				case "STRINGBITMAP":
					android.widget.TableRow.LayoutParams para11 = new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para11_1 = new android.widget.TableRow.LayoutParams(avewdith-81, 60);			//checkbox
					android.widget.TableRow.LayoutParams para11_2 = new android.widget.TableRow.LayoutParams(80, 80);				//bitmap
					android.widget.TableRow.LayoutParams para11_3 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear11 = new LinearLayout(context);
					linear11.setOrientation(LinearLayout.HORIZONTAL);
					linear11.setLayoutParams(para11);
					//初始化EditText
					final Operation operation11 = DataSupport.where("cellid=? and taskid=?", cell.getCellid(), task_id+"").find(Operation.class).get(0);		
					//初始化EditText
					String stringdata11 = CommonTools.null2String(operation11.getOpvalue());
					final EditText edit11 = new EditText(context);
	    			edit11.setText(CheckActivity1.replaceStr(stringdata11));
	    			edit11.setTextSize(16);
	    			edit11.setEnabled(false);
					linear11.addView(edit11, para11_1);
					final String userId11 = OrientApplication.getApplication().loginUser.getUserid();
		    		final String taskId11 = cell.getTaskid();
		    		final String opId11 = operation11.getSketchmap();
					final String absPath11 = Environment.getExternalStorageDirectory()
							+ Config.opphotoPath + "/"
							+ taskId11 + "/" + opId11 + ".jpg";
		    		Bitmap bitmap11 = BitmapFactory.decodeFile(absPath11);
		    		
					ImageView image11 = new ImageButton(context);
					if(bitmap11 != null)
//						image11.setImageBitmap(bitmap11);
						Glide
				    	    .with(context)
				    	    .load(absPath11)
				    	    .override(80, 80)
				    	    .fitCenter()
				    	    .into(image11);
						image11.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								BitmapDialogActivity.actionStart(context, userId11, taskId11, opId11);
						}
					});
					linear11.addView(image11, para11_2);
					ImageView image11_2 = new ImageView(context);
					image11_2.setBackgroundResource(R.drawable.blacktiao);
					linear11.addView(image11_2, para11_3);
					tablerow.addView(linear11, para11);
					break;
				case "STRINGPHOTOBITMAP":
					android.widget.TableRow.LayoutParams para12= new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para12_1 = new android.widget.TableRow.LayoutParams(avewdith-161, 100);				//string
					android.widget.TableRow.LayoutParams para12_2 = new android.widget.TableRow.LayoutParams(80, 80);				//photo
					android.widget.TableRow.LayoutParams para12_3 = new android.widget.TableRow.LayoutParams(80, 80);				//bitmap
					android.widget.TableRow.LayoutParams para12_4 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear12 = new LinearLayout(context);
					linear12.setOrientation(LinearLayout.HORIZONTAL);
					linear12.setLayoutParams(para12);
					//初始化EditText
					final Operation operation12_2 = DataSupport.where("cellid=? and taskid=? and type=?", cell.getCellid(), task_id+"", "2").find(Operation.class).get(0);
					//初始化EditText
					String stringdata12 = CommonTools.null2String(operation12_2.getOpvalue());
					final EditText edit12 = new EditText(context);
	    			edit12.setText(CheckActivity1.replaceStr(stringdata12));
	    			edit12.setTextSize(16);
					linear12.addView(edit12, para12_1);
					edit12.setEnabled(false);
					String opId12 = operation12_2.getSketchmap();
					final String sketbitmap12 = opId12;
					final String userId12 = OrientApplication.getApplication().loginUser.getUserid();
		    		final String taskId12 = cell.getTaskid();
					final String absPath12 = Environment.getExternalStorageDirectory()
							+ Config.opphotoPath + "/"
							+ taskId12 + "/" + sketbitmap12 + ".jpg";
		    		Bitmap bitmap12 = BitmapFactory.decodeFile(absPath12);
					ImageView image12 = new ImageButton(context);
					if(bitmap12 != null)
//						image12.setImageBitmap(bitmap12);
						Glide
				    	    .with(context)
				    	    .load(absPath12)
				    	    .override(80, 80)
				    	    .fitCenter()
				    	    .into(image12);
						image12.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								BitmapDialogActivity.actionStart(context, userId12, taskId12, sketbitmap12);
						}
					});
					linear12.addView(image12, para12_2);
					NumImageButton image12_1 = new NumImageButton(context);
					if (picturenumbers == 0) {
						if (operation12_2 != null) {
							final String path1 = Environment.getExternalStorageDirectory() + Config.v2photoPath
									+ File.separator
									+ OrientApplication.getApplication().rw.getRwid()
									+ File.separator
									+ cell.getTaskid()
									+ File.separator
									+ CommonTools.null2String(operation12_2.getOperationid())
									+ File.separator;
							getPictures(path1);
						}
					}
					image12_1.setNum(picturenumbers);
					totalPhNumber = totalPhNumber + picturenumbers;
					picturenumbers = 0;
					image12_1.setBackgroundResource(R.drawable.albumphoto);
					image12_1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
//							AlbumAty.actionStart2(context, cell.getTaskid(), cell.getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
							transAlbum(cell.getCellid(), cell.getTaskid(), operation12_2);
						}
					});
					linear12.addView(image12_1, para12_3);
					ImageView image12_2 = new ImageView(context);
					image12_2.setBackgroundResource(R.drawable.blacktiao);
					linear12.addView(image12_2, para12_4);
					tablerow.addView(linear12, para12);
					break;
				case "HOOKPHOTOBITMAP":
					android.widget.TableRow.LayoutParams para13= new android.widget.TableRow.LayoutParams(avewdith, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					android.widget.TableRow.LayoutParams para13_1 = new android.widget.TableRow.LayoutParams(avewdith-161, 100);				//string
					android.widget.TableRow.LayoutParams para13_2 = new android.widget.TableRow.LayoutParams(80, 80);				//photo
					android.widget.TableRow.LayoutParams para13_3 = new android.widget.TableRow.LayoutParams(80, 80);				//bitmap
					android.widget.TableRow.LayoutParams para13_4 = new android.widget.TableRow.LayoutParams(1, android.widget.TableRow.LayoutParams.MATCH_PARENT);
					LinearLayout linear13 = new LinearLayout(context);
					para13_1.gravity = Gravity.CENTER;
					linear13.setOrientation(LinearLayout.HORIZONTAL);
					linear13.setLayoutParams(para13);
					//初始化EditText
					final Operation operation13_1 = DataSupport.where("cellid=? and taskid=?", cell.getCellid(), task_id+"").find(Operation.class).get(0);
					//初始化checkbox
					CheckBox cb13 = new CheckBox(context);
					String value13 = CommonTools.null2String(operation13_1.getOpvalue());
					if(value13 != null){
						if(value13.equals("is")){						//打钩状态
							cb13.setChecked(true);
						}else if(value13.equals("no")){			//不打钩状态
							cb13.setChecked(false);
						}else{
							cb13.setChecked(false);
						}
					}
					linear13.addView(cb13, para13_1);
					cb13.setEnabled(false);
					String opId13 = operation13_1.getSketchmap();
					final String sketbitmap13 = opId13;
					final String userId13 = OrientApplication.getApplication().loginUser.getUserid();
		    		final String taskId13 = cell.getTaskid();
		    		final String rowNum13 = cell.getHorizontalorder();
		    		final String cellId13 = cell.getCellid();
					final String absPath13 = Environment.getExternalStorageDirectory()
							+ Config.opphotoPath + "/"
							+ taskId13 + "/" + sketbitmap13 + ".jpg";
		    		Bitmap bitmap13 = BitmapFactory.decodeFile(absPath13);
					ImageView image13 = new ImageButton(context);
					if(bitmap13 != null)
//						image13.setImageBitmap(bitmap13);
						Glide
				    	    .with(context)
				    	    .load(absPath13)
				    	    .override(80, 80)
				    	    .fitCenter()
				    	    .into(image13);
						image13.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								BitmapDialogActivity.actionStart(context, userId13, taskId13, sketbitmap13);
						}
					});
					linear13.addView(image13, para13_2);
					NumImageButton image13_1 = new NumImageButton(context);
					if (picturenumbers == 0) {
						if (operation13_1 != null) {
							final String path1 = Environment.getExternalStorageDirectory() + Config.v2photoPath
									+ File.separator
									+ OrientApplication.getApplication().rw.getRwid()
									+ File.separator
									+ cell.getTaskid()
									+ File.separator
									+ CommonTools.null2String(operation13_1.getOperationid())
									+ File.separator;
							getPictures(path1);
						}
					}
					image13_1.setNum(picturenumbers);
					totalPhNumber = totalPhNumber + picturenumbers;
					picturenumbers = 0;
					image13_1.setBackgroundResource(R.drawable.albumphoto);
					image13_1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
//							AlbumAty.actionStart2(context, cell.getTaskid(), cell.getCellid(), 
//									OrientApplication.getApplication().rw.getRwid());
							transAlbum(cell.getCellid(), cell.getTaskid(), operation13_1);
						}
					});
					linear13.addView(image13_1, para13_3);
					ImageView image13_2 = new ImageView(context);
					image13_2.setBackgroundResource(R.drawable.blacktiao);
					linear13.addView(image13_2, para13_4);
					tablerow.addView(linear13, para13);
					break;
				default:
					
				}
			}
			table_content.addView(tablerow, new TableLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 50));
		}
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
	private List<Cell> loadCellAdapter(long taskid, String userid, int pagetype){
		List<Cell> cellList = aerospacedb.loadTableAdapter(taskid, userid, pagetype);
		return cellList;
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		this.finish();
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	@Override  
    public void onWindowFocusChanged(boolean hasFocus){  
        if(hasFocus){  
        	
        }  
    }
	
	private void transAlbum(final String cellId, final String taskId, final Operation operation){
		final String path = Environment.getExternalStorageDirectory() + Config.v2photoPath 
				+ File.separator
				+ OrientApplication.getApplication().rw.getRwid()
				+ File.separator 
				+ taskId
				+ File.separator
				+ CommonTools.null2String(operation.getOperationid()) 
				+ File.separator;
		Intent intent = new Intent(ReadActivity1.this, AlbumActivity.class);
        intent.putExtra("path", path);
		intent.putExtra("checkType", "read");
        startActivity(intent);
	}

	@Override
	public void onScrollChanged(int scrollY) {

	}

	@Override
	public void onDownMotionEvent() {

	}

	@Override
	public void onUpOrCancelMotionEvent() {

	}

	public void getPictures(String string) {
		// TODO Auto-generated method stub
		File file = new File(string);
		File[] files = file.listFiles();
		if (files != null) {
			for (int j = 0; j < files.length; j++) {
				String name = files[j].getName();
				if (files[j].isDirectory()) {
					String dirPath = files[j].toString().toLowerCase();
					System.out.println(dirPath);
					getPictures(dirPath + "/");
				} else if (files[j].isFile() & name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".bmp") || name.endsWith(".gif") || name.endsWith(".jpeg")) {
					System.out.println("FileName===" + files[j].getName());
					picturenumbers++;
				}
			}
		}
	}

	@Subscribe
	public void onEventMainThread(Event.LocationEvent locationEvent) {
		if (locationEvent.getContent().equals("finish")) {
			finish();
		}
	}

}
