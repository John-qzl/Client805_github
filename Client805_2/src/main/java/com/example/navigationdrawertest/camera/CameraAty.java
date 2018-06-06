package com.example.navigationdrawertest.camera;

import java.io.File;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.activity.BaseActivity;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.customCamera.FileOperateUtil;
import com.example.navigationdrawertest.customCamera.album.view.FilterImageView;
import com.example.navigationdrawertest.customCamera.camera.view.CameraContainer;
import com.example.navigationdrawertest.customCamera.camera.view.CameraContainer.TakePictureListener;
import com.example.navigationdrawertest.customCamera.camera.view.CameraView.FlashMode;
import com.example.navigationdrawertest.model.Operation;



import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/** 
 * @ClassName: CameraAty 
 * @Description:  自定义照相机类
 * @author LinJ
 * @date 2014-12-31 上午9:44:25 
 *  
 */
public class CameraAty extends BaseActivity implements View.OnClickListener,TakePictureListener{
	public final static String TAG="CameraAty";
	private boolean mIsRecordMode=false;
	private String mSaveRoot;
	private CameraContainer mContainer;
	private FilterImageView mThumbView;
	private ImageButton mCameraShutterButton;
	private ImageButton mRecordShutterButton;
	private ImageView mFlashView;
	private ImageButton mSwitchModeButton;
	private ImageView mSwitchCameraView;
	private ImageView mSettingView;
	private ImageView mVideoIconView;
	private View mHeaderBar;
	private boolean isRecording=false;
	private static String userId = "";
	private static String taskId = "";
	private static String rwId = "";
	private static String rowNum = "";
	private static String cellId = "";
	private static String opId = "";
	
	public static void actionStart(Context context, String data1, String data2, String data3, String data4, String data5) {
		Intent intent = new Intent(context, CameraAty.class);
		intent.putExtra("param1", data1);
		intent.putExtra("param2", data2);
		intent.putExtra("param3", data3);
		intent.putExtra("param4", data4);
		intent.putExtra("param5", data5);
		userId = data1;
		taskId = data2;
		rwId = data3;
		rowNum = data4;
		cellId = data5;
//		List<Operation> operationList = DataSupport.where("userid = ? and cellid = ? and taskid = ?",
//        		OrientApplication.getApplication().loginUser.getUserid(), cellId, taskId).find(Operation.class);
		List<Operation> operationList = DataSupport.where("cellid = ? and taskid = ?", cellId, taskId).find(Operation.class);
		for(int i=0; i<operationList.size(); i++){
			if(operationList.get(i).getIsmedia().equals("TRUE")){
				opId = operationList.get(i).getOperationid();
			}
		}
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera);

		mHeaderBar=findViewById(R.id.camera_header_bar);
		mContainer=(CameraContainer)findViewById(R.id.container);
		mThumbView=(FilterImageView)findViewById(R.id.btn_thumbnail);
		mVideoIconView=(ImageView)findViewById(R.id.videoicon);
		mCameraShutterButton=(ImageButton)findViewById(R.id.btn_shutter_camera);
		mRecordShutterButton=(ImageButton)findViewById(R.id.btn_shutter_record);
		mSwitchCameraView=(ImageView)findViewById(R.id.btn_switch_camera);
		mFlashView=(ImageView)findViewById(R.id.btn_flash_mode);
		mSwitchModeButton=(ImageButton)findViewById(R.id.btn_switch_mode);
		mSettingView=(ImageView)findViewById(R.id.btn_other_setting);


		mThumbView.setOnClickListener(this);
		mCameraShutterButton.setOnClickListener(this);
		mRecordShutterButton.setOnClickListener(this);
		mFlashView.setOnClickListener(this);
		mSwitchModeButton.setOnClickListener(this);
		mSwitchCameraView.setOnClickListener(this);
		mSettingView.setOnClickListener(this);
		
		//缩略图地址
//		mSaveRoot="test";
		StringBuffer str = new StringBuffer();
//		str.append(userId).append("/").append(rwId).append("/").append(taskId).append("/").append(opId);
		str.append(rwId).append("/").append(taskId).append("/").append(opId);
		mSaveRoot = str.toString();
		
		mContainer.setRootPath(mSaveRoot);
		initThumbnail();
	}


	/**
	 * 加载缩略图
	 */
	private void initThumbnail() {
		String thumbFolder=FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, mSaveRoot);
		List<File> files=FileOperateUtil.listFiles(thumbFolder, ".jpg");
		if(files!=null&&files.size()>0){
			Bitmap thumbBitmap=BitmapFactory.decodeFile(files.get(0).getAbsolutePath());
			if(thumbBitmap!=null){
				mThumbView.setImageBitmap(thumbBitmap);
				//视频缩略图显示播放图案
				if(files.get(0).getAbsolutePath().contains("video")){
					mVideoIconView.setVisibility(View.VISIBLE);
				}else {
					mVideoIconView.setVisibility(View.GONE);
				}
			}
		}else {
			mThumbView.setImageBitmap(null);
			mVideoIconView.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_shutter_camera:
			mCameraShutterButton.setClickable(false);
			mContainer.takePicture(this);
			break;
		case R.id.btn_thumbnail:
//			startActivity(new Intent(this,AlbumAty.class));
			AlbumAty.actionStart(this, userId, taskId, rwId, opId);
			break;
		case R.id.btn_flash_mode:
			if(mContainer.getFlashMode()==FlashMode.ON){
				mContainer.setFlashMode(FlashMode.OFF);
				mFlashView.setImageResource(R.drawable.btn_flash_off);
			}else if (mContainer.getFlashMode()==FlashMode.OFF) {
				mContainer.setFlashMode(FlashMode.AUTO);
				mFlashView.setImageResource(R.drawable.btn_flash_auto);
			}
			else if (mContainer.getFlashMode()==FlashMode.AUTO) {
				mContainer.setFlashMode(FlashMode.TORCH);
				mFlashView.setImageResource(R.drawable.btn_flash_torch);
			}
			else if (mContainer.getFlashMode()==FlashMode.TORCH) {
				mContainer.setFlashMode(FlashMode.ON);
				mFlashView.setImageResource(R.drawable.btn_flash_on);
			}
			break;
		case R.id.btn_switch_mode:
			if(mIsRecordMode){
				mSwitchModeButton.setImageResource(R.drawable.ic_switch_camera);
				mCameraShutterButton.setVisibility(View.VISIBLE);
				mRecordShutterButton.setVisibility(View.GONE);
				//拍照模式下显示顶部菜单
				mHeaderBar.setVisibility(View.VISIBLE);
				mIsRecordMode=false;
				mContainer.switchMode(0);
				stopRecord(mSaveRoot);
			}
			else {
				mSwitchModeButton.setImageResource(R.drawable.ic_switch_video);
				mCameraShutterButton.setVisibility(View.GONE);
				mRecordShutterButton.setVisibility(View.VISIBLE);
				//录像模式下隐藏顶部菜单 
				mHeaderBar.setVisibility(View.GONE);
				mIsRecordMode=true;
				mContainer.switchMode(5);
			}
			break;
		case R.id.btn_shutter_record:
			if(!isRecording){
				isRecording=mContainer.startRecord(mSaveRoot);
				if (isRecording) {
					mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_recording);
				}
			}else {
				stopRecord(mSaveRoot);	
			}
			break;
		case R.id.btn_switch_camera:
			mContainer.switchCamera();
			break;
		case R.id.btn_other_setting:
			mContainer.setWaterMark();
			break;
		default:
			break;
		}
	}


	private void stopRecord(String mSaveRoot) {
		mContainer.stopRecord(this, mSaveRoot);
		isRecording=false;
		mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_record);
	}
	
	@Override
	public void onTakePictureEnd(Bitmap thumBitmap) {
		mCameraShutterButton.setClickable(true);	
	}

	@Override
	public void onAnimtionEnd(Bitmap bm,boolean isVideo) {
		if(bm!=null){
			//生成缩略图
			Bitmap thumbnail=ThumbnailUtils.extractThumbnail(bm, 213, 213);
			mThumbView.setImageBitmap(thumbnail);
			if(isVideo)
				mVideoIconView.setVisibility(View.VISIBLE);
			else {
				mVideoIconView.setVisibility(View.GONE);
			}
		}
	}

}