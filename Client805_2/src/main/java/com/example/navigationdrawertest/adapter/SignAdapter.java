package com.example.navigationdrawertest.adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.model.Signature;
import com.example.navigationdrawertest.model.Task;
import com.example.navigationdrawertest.utils.ActivityUtil;
import com.example.navigationdrawertest.utils.BitmapUtil;
import com.example.navigationdrawertest.utils.CommonTools;
import com.example.navigationdrawertest.utils.Config;
import com.example.navigationdrawertest.utils.DateUtil;
import com.example.navigationdrawertest.write.DialogListener;
import com.example.navigationdrawertest.write.WriteButtonClick;
import com.example.navigationdrawertest.write.WritePadDialog;

public class SignAdapter extends ArrayAdapter<Signature> {
	private Context context;
	private int resourceId;

	/**
	 * 手写签名定义的常量
	 */
	private Bitmap mSignBitmap;
	private String signPath;
	private Button buttonsign; // 按钮
	/** 网络，用于动态显示添加删除图片 */
	private GridView gv;
	/** 适配器 */
	private MyGridViewAdapter adapter;
	/** 数据列表 */
	private List<Bitmap> viewList;
	/** 布局填充器 */
	private LayoutInflater inflater;
	int windowHeight;
	int windowWidth;
	private String activityName;

	// private Signature sign;
	private String taskid; // 表格ID，用于标识图片存放路径

	public SignAdapter(Context context, int resource, List<Signature> objects) {
		super(context, resource, objects);
		// this.taskid = objects.get(0).getTaskid();
		this.resourceId = resource;
		this.context = context;
		activityName = ActivityUtil.getActivityName(context);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		windowWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		windowHeight = dm.heightPixels;
		viewList = new ArrayList<Bitmap>();
		viewList.add(null);
	}

	private WriteButtonClick myWbc;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Signature sign = getItem(position);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (myWbc != null) {
					// myWbc.click(v, sign);
					myWbc.click(v);
				}
			}
		};

//		View view;
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.signname = (TextView) convertView
					.findViewById(R.id.button_signname);
			viewHolder.signbutton = (Button) convertView
					.findViewById(R.id.button_sign);
			viewHolder.signimage = (ImageButton) convertView
					.findViewById(R.id.signimage);
			viewHolder.signtime = (TextView) convertView.findViewById(R.id.signtime);
			viewHolder.signbutton.setOnClickListener(listener);
			convertView.setTag(viewHolder);
		} else {
//			convertView = convertView;
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.signname.setText(sign.getSignname());
		viewHolder.signtime.setText(sign.getSignTime());
//		String signphotoPath = Environment.getDataDirectory().getPath()
//				+ Config.packagePath + Config.signphotoPath + "/"
//				+ sign.getTaskid() + "/";
//		String _path = signphotoPath + sign.getSignid() + ".jpg";
		String _path = CommonTools.null2String(sign.getBitmappath());
		Bitmap bitmap = BitmapUtil.getLoacalBitmap(_path);

		if (bitmap != null) {
			viewHolder.signbutton.setEnabled(false);
			viewHolder.signimage.setEnabled(false);
			viewHolder.signimage.setImageBitmap(bitmap);
//			viewHolder.signimage.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					AlertDialog.Builder builder = new Builder(context);
//					builder.setTitle("查看大图");
//					View view = inflater.inflate(R.layout.write_dlg_view, null);
//					((ImageView) view.findViewById(R.id.bigPic))
//							.setImageBitmap(bitmap);
//					builder.setView(view);
//					builder.setNegativeButton("返回", null);
//					builder.show();
//				}
//			});
		}

		viewHolder.signbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				WritePadDialog writeTabletDialog = new WritePadDialog(
						getContext(), new DialogListener() {
							@Override
							public void refreshActivity(Object object,
									Signature sign) {

								mSignBitmap = (Bitmap) object;
								signPath = createFile(sign.getSignid(),
										sign.getTaskid());
								Bitmap bmp = getBitmapByOpt(signPath);
								if (bmp != null) {
									String time = DateUtil.getCurrentDate();
									sign.setIsFinish("is");
									sign.setBitmappath(signPath);
									sign.setSignTime(time);
									sign.update(sign.getId());
									setLocation(sign);
//									viewHolder.signtime.setText(time);
									notifyDataSetChanged();
								}
								v.setEnabled(false);
							}
						}, sign);
				writeTabletDialog.show();
			}
		});

		if (activityName.equals("SignActivity1")) {
			viewHolder.signbutton.setEnabled(true);
		} else {
			viewHolder.signbutton.setEnabled(false);
		}

		return convertView;
	}

	public void setLocation(Signature sign) {
		int sumcount = 0; // 总共多少个签署项
		int finishcount = 0; // 已经完成多少个签署项
		String userid = OrientApplication.getApplication().loginUser
				.getUserid();
		List<Signature> signs = DataSupport.where("taskid = ?",
				sign.getTaskid()).find(Signature.class);
		Task task = DataSupport.where("taskid = ?", sign.getTaskid())
				.find(Task.class).get(0);
		sumcount = signs.size();
		for (int i = 0; i < signs.size(); i++) {
			if (signs.get(i).getIsFinish() != null
					&& signs.get(i).getIsFinish().equals("is")) {
				finishcount++;
			}
		}
		if (sumcount == finishcount) { // 全部签署完成
			task.setLocation(3);
			task.update(task.getId());
		} else if (sumcount > finishcount && finishcount > 0) { // 签署了一部分
			task.setLocation(2);
			task.update(task.getId());
		} else {

		}
	}

	class ViewHolder {
		public TextView signname;
		public Button signbutton;
		public ImageButton signimage;
		public TextView signtime;
	}

	private void setWriteButtonClick(WriteButtonClick wbc) {
		myWbc = wbc;
	}

	public Bitmap getBitmapByOpt(String picturePath) {
		Options opt = new Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picturePath, opt);
		int imgHeight = opt.outHeight;
		int imgWidth = opt.outWidth;
		int scaleX = imgWidth / windowWidth;
		int scaleY = imgHeight / windowHeight;
		int scale = 1;
		if (scaleX > scaleY & scaleY >= 1) {
			scale = scaleX;
		}
		if (scaleY > scaleX & scaleX >= 1) {
			scale = scaleY;
		}
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = scale;
		return BitmapFactory.decodeFile(picturePath, opt);
	}

	/**
	 * 创建手写签名文件
	 * 
	 * @return
	 */
	private String createFile(String signid, String taskid) {
		ByteArrayOutputStream baos = null;
		String _path = null;
		try {
			String signphotoPath = Environment.getDataDirectory().getPath()
					+ Config.packagePath + Config.signphotoPath + "/" + taskid
					+ "/";
			_path = signphotoPath + signid + ".jpg";
			File path = new File(signphotoPath);
			if (!path.exists()) {// 目录存在返回false
				path.mkdirs();// 创建一个目录
			}
			baos = new ByteArrayOutputStream();
			mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] photoBytes = baos.toByteArray();
			if (photoBytes != null) {
				new FileOutputStream(new File(_path)).write(photoBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _path;
	}

}
