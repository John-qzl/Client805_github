package com.example.navigationdrawertest.adapter;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.SweetAlert.SweetAlertDialog;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.model.Signature;
import com.example.navigationdrawertest.utils.BitmapUtil;

public class MyGridViewAdapter extends BaseAdapter {

	/** 数据列表 */
	private List<Bitmap> viewList;
	/** 布局填充器 */
	private LayoutInflater inflater;
	private Context context;
	private Signature sign;
	private Bitmap viewList1;
	
	private String getPath(Signature sign){
//		String sign_dir = Environment.getExternalStorageDirectory() + File.separator+OrientApplication.getApplication()
//				.loginUser.getUserid()+"/"+sign.getTaskid()+"/";		
		String sign_dir = Environment.getExternalStorageDirectory() + File.separator + sign.getTaskid()+"/";		
		String _path = sign_dir + sign.getSignid() + ".jpg";
		return _path;
	}
	
	public MyGridViewAdapter(Context context, List<Bitmap> viewList, Signature sign) {
		this.context = context;
		String _path = getPath(sign);
		Bitmap bitmap = BitmapUtil.getLoacalBitmap(_path);
		this.viewList1 = bitmap;
		this.sign = sign;
	}

	@Override
	public int getCount() {
		if (viewList == null || viewList.size() == 0) {
			return 0;
		}
		return viewList.size();
	}

	@Override
	public Object getItem(int position) {
		return viewList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		inflater = LayoutInflater.from(context);
		
//		View picView = inflater.inflate(R.layout.write_gv_item_pic, null);
//		ImageButton picIBtn = (ImageButton) picView.findViewById(R.id.pic);
		View picView = inflater.inflate(R.layout.write_main, null);
		ImageButton picIBtn = (ImageButton) picView.findViewById(R.id.signimage);
		picIBtn.setImageBitmap(viewList.get(position));
		picIBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(context);
				builder.setTitle("查看大图");
				View view = inflater.inflate(R.layout.write_dlg_view, null);
				((ImageView)view.findViewById(R.id.bigPic)).setImageBitmap(viewList.get(position));
				builder.setView(view);
				builder.setNegativeButton("返回", null);
				builder.show();
			}
		});
		return picView;
	}
	
}
