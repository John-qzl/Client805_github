package com.example.navigationdrawertest.CustomUI;

import com.example.navigationdrawertest.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CustomDialog extends Dialog{
	
	private EditText edit_ip;
	private EditText edit_port;
	private Button positiveButton, negativeButton;
	private TextView title;

	public CustomDialog(Context context) {
		super(context,R.style.ssDialog);
		setCustomDialog();
	}

	private void setCustomDialog() {
		View mView = LayoutInflater.from(getContext()).inflate(R.layout.setting_dialog_layout, null);
		title = (TextView) mView.findViewById(R.id.title);
		edit_ip = (EditText) mView.findViewById(R.id.setting_ip);
		edit_port = (EditText) mView.findViewById(R.id.setting_port);
		positiveButton = (Button) mView.findViewById(R.id.positiveButton);
		negativeButton = (Button) mView.findViewById(R.id.negativeButton);
		super.setContentView(mView);
	}
	
	
	public EditText getEdit_ip() {
		return edit_ip;
	}
	public void setEdit_ip(EditText edit_ip) {
		this.edit_ip = edit_ip;
	}
	public EditText getEdit_port() {
		return edit_port;
	}
	public void setEdit_port(EditText edit_port) {
		this.edit_port = edit_port;
	}

	@Override
	public void setContentView(int layoutResID) {
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
	}

	@Override
	public void setContentView(View view) {
		
	}

	/** 
     * 确定键监听器 
     * @param listener 
     */  
    public void setOnPositiveListener(View.OnClickListener listener){  
    	positiveButton.setOnClickListener(listener);  
    }  
    /** 
     * 取消键监听器 
     * @param listener 
     */  
    public void setOnNegativeListener(View.OnClickListener listener){  
    	negativeButton.setOnClickListener(listener);  
    }
}
