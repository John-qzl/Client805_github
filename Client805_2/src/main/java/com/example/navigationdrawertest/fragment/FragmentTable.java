package com.example.navigationdrawertest.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.CustomUI.CellTypeEnum;
import com.example.navigationdrawertest.CustomUI.HeadItemCell;
import com.example.navigationdrawertest.CustomUI.ItemCell;
import com.example.navigationdrawertest.activity.CheckActivity;
import com.example.navigationdrawertest.adapter.CustomeTableViewAdapter;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.utils.HtmlHelper;

import de.greenrobot.event.EventBus;

public class FragmentTable extends Fragment{
	
    private LayoutInflater inflater;
    private LinearLayout headLayout = null;
    private int[] arrHeadWidth = null;
    private ListView listView;
    private CustomeTableViewAdapter adapter = null;
    private ArrayList<HashMap<String,Object>>  lists = new ArrayList<HashMap<String,Object>>();
    private int mScreenWidth;
    private int mScreenHeight;
    
    public Handler mHandler = new Handler(){  
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case 0:  
            	adapter.notifyDataSetChanged();
                break;   
            }  
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	EventBus.getDefault().register(this);
    };
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.table_list_view_ui, container, false);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		mScreenHeight = dm.heightPixels;
		Log.i("宽度", mScreenWidth+"");
		Log.i("高度", mScreenHeight+"");
        headLayout = (LinearLayout)v.findViewById(R.id.linearlayout_head); 
		listView = (ListView) v.findViewById(R.id.listview);
		setListViewHeightBasedOnChildren(listView);
        
        testData();//测试数据
        return v;
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
		  listView.setLayoutParams(params);  
	}
	
	private void addVLine(){
//		LinearLayout v_line = (LinearLayout)getVerticalLine();
		View v_line = inflater.inflate(R.layout.table_atom_line_v_view, null);
//		v_line.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
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
			setHeadName(name,width);
			arrHeadWidth[i] = width;
			if(i != headMap.size()-1){		//不是最后一列
				this.addVLine();
			}
		}
	}
	
	private void setHeadName(String name, int width){
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
	
	private void testData(){
		HashMap headMap = new HashMap();
		this.testAddHead(headMap,"列1");
		this.testAddHead(headMap,"列2");
		this.testAddHead(headMap,"列3");
		this.testAddHead(headMap,"列4");
		this.testAddHead(headMap,"列5");
		
		addHead(headMap);
		
//		HashMap contentMap = new HashMap();
//		this.testAddContent(contentMap);
		testAddContent();
		
		adapter = new CustomeTableViewAdapter(this.getActivity(), lists , listView , false , this.arrHeadWidth, mHandler, "1");
		adapter.notifyDataSetChanged();
	}
	
	private void testAddHead(HashMap headMap,String headName){
		HeadItemCell itemCell = new HeadItemCell(headName,mScreenWidth/5);
		headMap.put(headMap.size()+"", itemCell);
	}
	
	private void testAddContent(){
		HashMap rowMap1 = new HashMap();
		lists.add(rowMap1);
		this.testAddRows(rowMap1, 1, "1-1(1)hahahahhahahahahaahhaahahahaha", CellTypeEnum.LABEL, null);
		this.testAddRows(rowMap1, 1, "1-2(1)", CellTypeEnum.STRING, null);
		this.testAddRows(rowMap1, 2, "1-3(2)", CellTypeEnum.STRING, null);
		this.testAddRows(rowMap1, 1, "1-4(1)", CellTypeEnum.DIGIT, null);
		rowMap1.put("rowtype", "css1");	//表样标示放在内容添加后再添加

		HashMap rowMap2 = new HashMap();
		lists.add(rowMap2);
		this.testAddRows(rowMap2, 1, "2-1(1)", CellTypeEnum.LABEL, null);
		this.testAddRows(rowMap2, 3, "2-2(3)", CellTypeEnum.STRING, null);
		this.testAddRows(rowMap2, 1, "2-3(1)", CellTypeEnum.DIGIT, null);
		rowMap2.put("rowtype", "css2");
		

		HashMap rowMap3 = new HashMap();
		lists.add(rowMap3);
		this.testAddRows(rowMap3, 1, "3-1(1)", CellTypeEnum.LABEL, null);
		this.testAddRows(rowMap3, 3, "3-2(3)", CellTypeEnum.LABEL, null);
		this.testAddRows(rowMap3, 1, "3-3(1)", CellTypeEnum.DIGIT, null);
		rowMap3.put("rowtype", "css3");
		

		HashMap rowMap4 = new HashMap();
		lists.add(rowMap4);
		this.testAddRows(rowMap4, 1, "4-1(1)", CellTypeEnum.LABEL, null);
		this.testAddRows(rowMap4, 1, "4-2(1)", CellTypeEnum.STRING, null);
		this.testAddRows(rowMap4, 2, "4-3(2)", CellTypeEnum.STRING, null);
		this.testAddRows(rowMap4, 1, "4-4(1)", CellTypeEnum.DIGIT, null);
		rowMap4.put("rowtype", "css1");
	}
	
	private void testAddRows(HashMap rowMap,int colSpan,String cellValue,CellTypeEnum cellType, Cell cell){
//	    ItemCell itemCell = new ItemCell(cellValue,cellType,colSpan, cell);	
//	    rowMap.put(rowMap.size()+"", itemCell);
	}

	
}
