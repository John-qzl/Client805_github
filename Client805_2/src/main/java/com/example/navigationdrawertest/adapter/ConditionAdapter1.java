package com.example.navigationdrawertest.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.model.Scene;
import com.example.navigationdrawertest.utils.ActivityUtil;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ConditionAdapter1 extends BaseAdapter{

	private Context context;			//用于区分从哪个activity传递过来数据
	private List<Scene> sceneList = new ArrayList<Scene>();
	private LayoutInflater inflater;
	String activityName = "";
	
	public ConditionAdapter1(Context context, List<Scene> sceneList){
		this.context = context;
		this.sceneList = sceneList;
		inflater = LayoutInflater.from(context);
		activityName = ActivityUtil.getActivityName(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sceneList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return sceneList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Scene scene = sceneList.get(position);
		final ViewHolder viewHolder;
//		if(convertView == null){
			convertView = inflater.inflate(R.layout.checkconditionitem, null);
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView) convertView.findViewById(R.id.iv_list_item);
			viewHolder.displayname = (TextView) convertView.findViewById(R.id.tv_list_item);
			viewHolder.inputvalue = (EditText) convertView.findViewById(R.id.conditiontext);
//			convertView.setTag(viewHolder);
//		}else{
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
		String scenevalue = scene.getScenevalue();
		if(activityName.equals("CheckActivity1")){
			viewHolder.inputvalue.setEnabled(true);
		}else if(activityName.equals("SignActivity1")){
			viewHolder.inputvalue.setEnabled(false);
		}else if(activityName.equals("ReadActivity1")){
			viewHolder.inputvalue.setEnabled(false);
		}else{
			viewHolder.inputvalue.setEnabled(false);
		}
		if(scenevalue != null){
			viewHolder.inputvalue.setText(scenevalue);
		}
		viewHolder.image.setImageResource(R.drawable.condition);
		viewHolder.displayname.setText(scene.getConditionname());
		viewHolder.inputvalue.addTextChangedListener(new TextWatcher() {  
	        @Override    
	        public void afterTextChanged(Editable s) {     
	            // TODO Auto-generated method stub     
	        }   
	        @Override 
	        public void beforeTextChanged(CharSequence s, int start, int count,  
	                int after) {  
	            // TODO Auto-generated method stub  
	        }  
	        @Override    
	        public void onTextChanged(CharSequence s, int start, int before, int count) {     
	            String str = viewHolder.inputvalue.getText().toString();  
	            scene.setScenevalue(str);
	            scene.update(scene.getId());
	        }                    
		});
		
		return convertView;
	}
	
	class ViewHolder {
		public ImageView image;
		public TextView displayname;
		public EditText inputvalue;
	}

}
