package com.example.navigationdrawertest.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.activity.PriviewRwPicActivity;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.utils.FileOperation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

@SuppressLint("NewApi")
public class PlusActionProvider extends ActionProvider {

	private Context context;
	private String fileName = "";

	public PlusActionProvider(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		subMenu.clear();
		subMenu.add(context.getString(R.string.plus_group_chat))
				.setIcon(R.drawable.ofm_group_chat_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(context, Test.class);
						context.startActivity(intent);
						return true;
					}
				});
		subMenu.add(context.getString(R.string.plus_add_friend))
				.setIcon(R.drawable.ofm_add_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return false;
					}
				});
		subMenu.add(context.getString(R.string.plus_video_chat))
				.setIcon(R.drawable.ofm_video_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return false;
					}
				});
		subMenu.add(context.getString(R.string.plus_scan))
				.setIcon(R.drawable.ofm_qrcode_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return false;
					}
				});
		subMenu.add(context.getString(R.string.plus_take_photo))
				.setIcon(R.drawable.ofm_camera_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						new TaskDealDialog(context);
						return false;
					}
				});
	}

	class TaskDealDialog extends AlertDialog {
		private View view;
		private TextView takePhoto;
		private TextView photoShow;
		private Builder builder = null;

		protected TaskDealDialog(Context context) {
			super(context);

			LayoutInflater li = LayoutInflater.from(context);
			view = li.inflate(R.layout.photo_select, null);
			view.setBackgroundColor(Color.argb(123, 56, 34, 21));
			takePhoto = (TextView) view.findViewById(R.id.takephoto);
			photoShow = (TextView) view.findViewById(R.id.photoshow);

			builder = new AlertDialog.Builder(context);
			builder.setTitle("拍照及预览界面");
			builder.setIcon(R.drawable.logo_title);
			builder.setView(view);
			AlertDialog dialog = builder.create();
			dialog.show();
			addAction();
		}

		private void addAction() {
			takePhoto.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
					File dataRoot = Environment.getExternalStorageDirectory();
					String dataRootPath = dataRoot.getPath();
					String approotPath = "";
					approotPath = dataRootPath + "/target";
					File out = new File(approotPath);
					if (!out.exists()) {
						out.mkdir();
					}

					String userid = OrientApplication.getApplication().loginUser
							.getUserid();
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd-HH-mm-ss");
					String times = String.valueOf(format.format(new Date()));
					StringBuilder photoNameBuilder = new StringBuilder();
					photoNameBuilder.append(userid).append("_").append(times)
							.append(".jpg");
					fileName = photoNameBuilder.toString();
					File outfile = new File(out, photoNameBuilder.toString());
					Uri uri = Uri.fromFile(outfile);
					it.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//					startActivityForResult(it, 101);
				}
			});

			photoShow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					Intent intent = new Intent(context, PriviewRwPicActivity.class);
					PriviewRwPicActivity.actionStart(context);
//					startActivity(intent);
//					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});

		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 101) {
			if (resultCode == Activity.RESULT_OK) {
				// 文件拷贝到指定的文件夹下
				File dataRoot = Environment.getExternalStorageDirectory();
				String dataRootPath = dataRoot.getPath();
				String sdpicPath;
				sdpicPath = dataRootPath + "/target/" + fileName;
				// copy the file to post position

				String postPath = this.getNextPhotoPath(fileName, OrientApplication.getApplication().loginUser.getUserid());
				boolean bOK = FileOperation.coppyFile(sdpicPath, postPath);

				if (bOK) {
					FileOperation.deleteFile(sdpicPath);
				}
			}
		}
	}

	private String getNextPhotoPath(String fileName,String userid)
	{
		 String retPath = "";
		 String rootName = "data/com.orient.targetsiteflow/files/RWPhoto";
			
		 File dataRoot = Environment.getDataDirectory();
		 String dataRootPath = dataRoot.getPath();
		 String approotPath = dataRootPath + "/" + rootName;
			
		 //任务拍照存放根目录
		 File rootFile = new File(approotPath);
		 if (!rootFile.exists())
		 {
			rootFile.mkdirs();
		 }
		 //任务Id下拍照
		 File rwRootDir = new File(approotPath+"/"+userid);
		 if(!rwRootDir.exists())
		 {
			 rwRootDir.mkdir();
		 }
		 retPath = rwRootDir + "/" + fileName;
		 return retPath;
	 }
	
	@Override
	public boolean hasSubMenu() {
		return true;
	}

	@Override
	public View onCreateActionView() {
		// TODO Auto-generated method stub
		return null;
	}

}