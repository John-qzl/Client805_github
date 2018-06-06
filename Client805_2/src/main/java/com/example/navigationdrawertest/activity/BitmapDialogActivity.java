package com.example.navigationdrawertest.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.utils.Config;

public class BitmapDialogActivity extends BaseActivity{
	
	private ImageView imageview;
	private static String userId;
	private static String taskId;
	private static String opId;
	public static void actionStart(Context context, String userid, String taskid, String opid) {
		Intent intent = new Intent(context, BitmapDialogActivity.class);
		userId = userid;
		taskId = taskid;
		opId = opid;
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bitmapdialog);
		initUI();
	}
	
	public void initUI(){
		imageview = (ImageView) findViewById(R.id.bitmapdialog_imageview);
//		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
//				+ Config.opphotoPath+ "/"+ userId+"/" + taskId + "/" + opId + ".jpg";
//		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
//				+ Config.opphotoPath+ "/"+ taskId + "/" + opId + ".jpg";
		final String absPath = Environment.getExternalStorageDirectory()
				+ Config.opphotoPath + "/"
				+ taskId + "/" + opId + ".jpg";
		Bitmap bitmap = BitmapFactory.decodeFile(absPath);
		if(bitmap != null){
			imageview.setImageBitmap(bitmap);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		finish();
		return true;
	}
	
}
