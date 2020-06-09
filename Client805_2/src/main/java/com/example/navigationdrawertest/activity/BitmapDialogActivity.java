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
import com.example.navigationdrawertest.utils.FileOperation;

import java.util.ArrayList;

public class BitmapDialogActivity extends BaseActivity{
	
	private ImageView imageview;
	private static String refphoto;
	private static String taskId;
	private static String opId;
	private ArrayList<String> mPhotos = new ArrayList<String>();
	public static void actionStart(Context context, String refphotoId, String taskid, String opid) {
		Intent intent = new Intent(context, BitmapDialogActivity.class);
		refphoto = refphotoId;
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
//				+ Config.opphotoPath+ "/"+ refphotoId+"/" + taskId + "/" + opId + ".jpg";
//		String absPath = Environment.getDataDirectory().getPath() + Config.packagePath
//				+ Config.opphotoPath+ "/"+ taskId + "/" + opId + ".jpg";
		final String absPath = Environment.getExternalStorageDirectory()
				+ Config.refphotoPath + "/"
				+ taskId + "/";
		String refPath = "";
		mPhotos = FileOperation.getAlbumByPath(absPath, "jpg", "png");
		if (mPhotos.size() > 0) {
			for (int i = 0; i < mPhotos.size(); i++) {
				if (mPhotos.get(i).contains(refphoto)) {
					refPath = mPhotos.get(i);
				}
			}
		}
		Bitmap bitmap = BitmapFactory.decodeFile(refPath);
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
