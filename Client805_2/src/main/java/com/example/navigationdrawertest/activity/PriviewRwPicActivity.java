package com.example.navigationdrawertest.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.utils.FileOperation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;


public class PriviewRwPicActivity extends Activity {

	private Button preBtn2 = null;
	private Button nextBtn2 = null;
	private Button exitBtn = null;
	private Button deleteBtn = null;
	private ImageView imageView = null;
	private TextView textNum = null;
	private int curIndex = 0;// 当前图片的位置
	private Bitmap bitmap = null;
	private List<PhotoMsg> rwPhotoMsgList = new ArrayList<PhotoMsg>();

	public static void actionStart(Context context) {
		Intent intent = new Intent(context, PriviewRwPicActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.photo_priviewrwpicactivity);
		preBtn2 = (Button) findViewById(R.id.preBtn2);
		nextBtn2 = (Button) findViewById(R.id.nextBtn2);
		exitBtn = (Button) findViewById(R.id.exitBtn);
		deleteBtn = (Button) findViewById(R.id.deleteBtn);

		textNum = (TextView) findViewById(R.id.textNum);
		imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setBackgroundColor(Color.BLACK);
		imageView.setOnTouchListener(new MulitPointTouchListener(imageView));

		initFirstPhoto();

		initOnClick();
	}

	private void initFirstPhoto() {
		rwPhotoMsgList = readAllPhotoMsg();
		if (rwPhotoMsgList.size() != 0) {
			textNum.setText(curIndex + 1 + "/" + rwPhotoMsgList.size());
			PhotoMsg photoMsg = rwPhotoMsgList.get(curIndex);
			String absPath = photoMsg.absPath;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			bitmap = BitmapFactory.decodeFile(absPath, options);
			imageView.setImageBitmap(bitmap);
			preBtn2.setClickable(false);
			nextBtn2.setClickable(false);
		}
	}

	private void initOnClick() {
		// preBtn1.setOnClickListener(new PreViewOnClickListener());
		preBtn2.setOnClickListener(new PreViewOnClickListener());
		// nextBtn1.setOnClickListener(new NextViewOnClickListener());
		nextBtn2.setOnClickListener(new NextViewOnClickListener());
		deleteBtn.setOnClickListener(new DeleteOnClickListener());
		exitBtn.setOnClickListener(new ExitOnClickListener());
	}

	class PreViewOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (curIndex == 0) {
				// 提示最前面一张图片
				Toast.makeText(PriviewRwPicActivity.this, "已经是最前一张图片",
						Toast.LENGTH_SHORT).show();
			} else {
				preBtn2.setClickable(true);
				curIndex--;
				PhotoMsg photoMsg = rwPhotoMsgList.get(curIndex);
				String absPath = photoMsg.absPath;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = BitmapFactory.decodeFile(absPath, options);
				imageView.setImageBitmap(bitmap);
				textNum.setText(curIndex + 1 + "/" + rwPhotoMsgList.size());
			}

		}

	}

	class NextViewOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			// if(curIndex == 0)
			if (rwPhotoMsgList.size() == 0) {
				Toast.makeText(PriviewRwPicActivity.this, "没有可操作的图片",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (curIndex == rwPhotoMsgList.size() - 1) {
				// 提示最后一张图片
				// nextBtn1.setClickable(false);
				// nextBtn2.setClickable(false);
				Toast.makeText(PriviewRwPicActivity.this, "已经是最后一张图片",
						Toast.LENGTH_SHORT).show();
			} else {
				// nextBtn1.setClickable(true);
				nextBtn2.setClickable(true);
				curIndex++;
				PhotoMsg photoMsg = rwPhotoMsgList.get(curIndex);
				String absPath = photoMsg.absPath;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = BitmapFactory.decodeFile(absPath, options);
				imageView.setImageBitmap(bitmap);
				textNum.setText(curIndex + 1 + "/" + rwPhotoMsgList.size());
			}

		}

	}

	class DeleteOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (rwPhotoMsgList.size() == 0) {
				return;
			}
			PhotoMsg photomsg = rwPhotoMsgList.get(curIndex);
			String filePath = photomsg.absPath;
			rwPhotoMsgList.remove(curIndex);
			FileOperation.deleteFile(filePath);

			if (rwPhotoMsgList.size() != 0)// 文件多于一个
			{
				if (curIndex == rwPhotoMsgList.size())// 不是最后图片
				{
					curIndex--;
				}
				PhotoMsg photoMsg = rwPhotoMsgList.get(curIndex);
				String absPath = photoMsg.absPath;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = BitmapFactory.decodeFile(absPath, options);
				imageView.setImageBitmap(bitmap);
				textNum.setText(curIndex + 1 + "/" + rwPhotoMsgList.size());
			} else// 文件为空
			{
				imageView.setImageBitmap(null);
				textNum.setText(curIndex + "/" + rwPhotoMsgList.size());
			}
		}

	}

	class ExitOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			onBackPressed();
		}

	}

	public class MulitPointTouchListener implements OnTouchListener {
		Matrix matrix = new Matrix();
		Matrix savedMatrix = new Matrix();

		public ImageView image;
		static final int NONE = 0;
		static final int DRAG = 1;
		static final int ZOOM = 2;
		int mode = NONE;

		PointF start = new PointF();
		PointF mid = new PointF();
		float oldDist = 1f;

		public MulitPointTouchListener(ImageView image) {
			super();
			this.image = image;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			this.image.setScaleType(ScaleType.MATRIX);
			ImageView view = (ImageView) v;

			switch (event.getAction() & MotionEvent.ACTION_MASK) {

			case MotionEvent.ACTION_DOWN:
				matrix.set(view.getImageMatrix());
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				mode = DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY()
							- start.y);
				} else if (mode == ZOOM) {
					float newDist = spacing(event);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
			}
			view.setImageMatrix(matrix);
			return true;
		}

		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		private void midPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 快速排序
	 * 
	 * @param listFileNames
	 * @param l
	 * @param r
	 */
	public void quick_sort(String[] listFileNames, int l, int r) {
		if (l < r) {
			int i = l, j = r; // x是基准值
			String x = listFileNames[l];
			while (i < j) {
				while (i < j && listFileNames[j].compareToIgnoreCase(x) >= 0)
					// 从右向左找第一个小于x的数
					j--;
				if (i < j) {
					listFileNames[i++] = listFileNames[j]; // 把右边大于x的数值放到待填入的坑中（1）
				}
				while (i < j && listFileNames[i].compareToIgnoreCase(x) < 0)
					// 从左向右找第一个大于或者等于x的数
					i++;
				if (i < j)
					listFileNames[j--] = listFileNames[i]; // 把这个数填入到从右往左找时留下的坑（2）
			}
			listFileNames[i] = x;
			// 到此为止，只进行了一趟排序
			quick_sort(listFileNames, l, i - 1); // 递归调用
			quick_sort(listFileNames, i + 1, r);
		}
	}

	private List<PhotoMsg> readAllPhotoMsg() {
		List<PhotoMsg> retAllPhotoMsgList = new ArrayList<PhotoMsg>();
		String rootName = "data/com.orient.targetsiteflow/files/RWPhoto";
//		DBHelper dbRw = new DBHelper(this);
//		Rws rwdb = dbRw.getRws();
//		if (rwdb != null) {
//			OrientApplication.getApplication().rwid = rwdb.rwid;
//			OrientApplication.getApplication().rwname = rwdb.rwname;
//			OrientApplication.getApplication().rw = rwdb;
//		}
//		String rwId = OrientApplication.getApplication().rwid;

		String userid = OrientApplication.getApplication().loginUser.getUserid();
		File dataRoot = Environment.getDataDirectory();
		String dataRootPath = dataRoot.getPath();
		String approotPath = dataRootPath + "/" + rootName;

		// 任务拍照存放根目录
		File rootFile = new File(approotPath);
		if (!rootFile.exists()) {
			rootFile.mkdirs();
		}
		// 任务Id下拍照
		File rwRootDir = new File(approotPath + "/" + userid);
		if (!rwRootDir.exists()) {
			rwRootDir.mkdir();
		}
		File[] listFiles = rwRootDir.listFiles();

		if (listFiles.length != 0) {
			String[] listFileNames = new String[listFiles.length];
			for (int count = 0; count < listFiles.length; count++) {
				listFileNames[count] = "";
			}
			for (int i = 0; i < listFiles.length; i++) {
				listFileNames[i] = listFiles[i].getName();
			}
			quick_sort(listFileNames, 0, listFileNames.length - 1);

			for (int count = 0; count < listFileNames.length; count++) {
				for (File file : listFiles) {
					if (file.getName().equals(listFileNames[count])
							|| file.getName().contains(listFileNames[count])) {
						PhotoMsg photoMsg = new PhotoMsg();
						photoMsg.absPath = file.getAbsolutePath();
						photoMsg.photoName = file.getName();
						retAllPhotoMsgList.add(photoMsg);
						break;
					}
				}
			}

		} else {
			for (File file : listFiles) {
				PhotoMsg photoMsg = new PhotoMsg();
				photoMsg.absPath = file.getAbsolutePath();
				photoMsg.photoName = file.getName();

				retAllPhotoMsgList.add(photoMsg);
			}
		}

		return retAllPhotoMsgList;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	class PhotoMsg {
		public String photoName;
		public String absPath;
	}

	@Override
	protected void onStop() {
		if (bitmap != null) {
			if (!bitmap.isRecycled()) {
				bitmap.recycle(); // 回收图片所占的内存
				bitmap = null;
				System.gc(); // 提醒系统及时回收
			}
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null) {
			if (!bitmap.isRecycled()) {
				bitmap.recycle(); // 回收图片所占的内存
				bitmap = null;
				System.gc(); // 提醒系统及时回收
			}
		}
	}
}