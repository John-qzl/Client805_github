package com.example.navigationdrawertest.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.CustomUI.CustomeTableItem;
import com.example.navigationdrawertest.CustomUI.ItemCell;
import com.example.navigationdrawertest.utils.DateUtil;

public class CustomeTableViewAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<HashMap<String,Object>> lists;
	private ListView listView = null;
	private boolean isReadOnly = false;
	private String[] arrCellType = null; 
	private int[] arrHeadWidth = null;
//	private Handler handler;
	private String location;
	
	/**
	 * 
	 * @param context	上下文
	 * @param lists			表格内容Contents
	 * @param listView	表格内容所要填充的Listview
	 * @param isReadOnly	是否是只读属性
	 * @param arrHeadWidth		每个row（列）的宽度
	 */
	public CustomeTableViewAdapter(Context context, ArrayList<HashMap<String,Object>> lists
			,ListView listView,boolean isReadOnly
			,int[] arrHeadWidth, Handler handler, String location) {
		super();
		this.context = context;
		this.lists = lists;
		inflater = LayoutInflater.from(context);
		this.listView = listView;
		this.isReadOnly = isReadOnly;
		this.arrHeadWidth = arrHeadWidth;
		this.location = location;
//		this.handler = handler;
		this.listView.setAdapter(this);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	@Override
	public View getView(final int index, View view, ViewGroup arg2) {
//		HashMap map = lists.get(index);
//		String type = (String)map.get("rowtype");		//获得每一行数据的样式
//		ArrayList<ItemCell> itemCells = new ArrayList();
//		for(int i=0;i<map.size()-1;i++){		//最后一个是标示,add by danielinbiti
//			ItemCell itemCell = (ItemCell)map.get(i+"");
//			itemCells.add(itemCell);
//		}
//		Log.i("开始时间6", DateUtil.getCurrentTime());
//		
//		//性能优化后需要放开注释
////		if(view == null||view!=null&&!((CustomeTableItem)view.getTag()).getRowType().equals(type)){
//		view = inflater.inflate(R.layout.table_customel_list_item, null);
//		CustomeTableItem itemCustom = (CustomeTableItem) view.findViewById(R.id.custome_item);
//		
//		itemCustom.buildItem(context, type ,itemCells, arrHeadWidth, isReadOnly, location);
//		
//		inflater.inflate(R.layout.table_customel_list_item, null).setTag(itemCustom);
//		
//		if(index%2 == 0){
//			view.setBackgroundColor(Color.argb(250 ,  255 ,  255 ,  255 )); 
//		}else{
//			view.setBackgroundColor(Color.argb(250 ,  224 ,  243 ,  250 ));    
//		}
//		Log.i("开始时间7", DateUtil.getCurrentTime());
		return view;
	}

}
