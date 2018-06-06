package com.example.navigationdrawertest.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.model.Scene;
import com.example.navigationdrawertest.utils.ActivityUtil;

public class ConditionAdapter extends ArrayAdapter<Scene>{
	private int resourceId;
	private Context context;			//用于区分从哪个activity传递过来数据
	private List<Scene> sceneList = new ArrayList<Scene>();
	
	public ConditionAdapter(Context context,
		int textViewResourceId, List<Scene> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		sceneList = objects;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Scene scene = getItem(position);
		View view;
		String activityName = ActivityUtil.getActivityName(context);
		
		final ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView) view.findViewById(R.id.iv_list_item);
			viewHolder.displayname = (TextView) view.findViewById(R.id.tv_list_item);
			viewHolder.inputvalue = (EditText) view.findViewById(R.id.conditiontext);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
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
		viewHolder.image.setImageResource(R.drawable.arrow);
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
		return view;
	}
	
	class ViewHolder {
		public ImageView image;
		public TextView displayname;
		public EditText inputvalue;
	}

}
