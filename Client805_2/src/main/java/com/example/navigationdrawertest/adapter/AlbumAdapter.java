package com.example.navigationdrawertest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.activity.AlbumActivity;
import com.example.navigationdrawertest.camera.MyUtils;
import com.example.navigationdrawertest.utils.Config;

/**
 * 创建时间：2016/10/9
 * 创建者：Young
 * 功能描述：AlbumActivity页面的适配器，显示照片缩略图
 * 其他：
 */
public class AlbumAdapter extends BaseAdapter {

	private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mPhotos;
    //全局变量，记录CheckBox是否可见
    private boolean isShowCheckBox;
    private OnShowItemClickListener mOnShowItemClickListener;

    //定义接口
    public interface OnShowItemClickListener {
//        void onShowItemClick(String str);
        void onShortClick();
        void onLongClick();
    }

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }

    public AlbumAdapter(Context context, List<String> photos, OnShowItemClickListener onShowItemClickListener) {
    	mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mPhotos = photos;
        mOnShowItemClickListener = onShowItemClickListener;
    }

    @Override
    public int getCount() {
        return mPhotos == null ? 0 : mPhotos.size();
    }

    @Override
    public Object getItem(int i) {
        return mPhotos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (i == 0) {
            view = new ImageView(mContext);
            view.setBackgroundResource(R.drawable.take_pic);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    takePic();
                }
            });
            return view;
        }
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_photo, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String str = mPhotos.get(i);
//        String absPath = Environment.getExternalStorageDirectory()
//				+ Config.opphotoPath + "/"
//				+ taskId8 + "/" + opId8 + ".jpg";
		Bitmap bitmap = BitmapFactory.decodeFile(str);
//		ImageView image8 = new ImageButton(mContext);
		if(bitmap != null)
//			image8.setImageBitmap(bitmap);
			Glide
	    	    .with(mContext)
	    	    .load(str)
	    	    .override(80, 80)
	    	    .fitCenter()
	    	    .into(holder.simpleDraweeView);
        //目的是获取原图的缩略图再显示，防止卡顿
//        FrescoUtils.showThumb(holder.simpleDraweeView, photo.getPhotoUri(), 40, 40);
//        holder.checkBox.setVisibility(isShowCheckBox ? View.VISIBLE : View.GONE);
//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    photo.setChecked(true);
//                } else {
//                    photo.setChecked(false);
//                }
//                if (mOnShowItemClickListener != null) {
//                    mOnShowItemClickListener.onShowItemClick(photo);
//                }
//            }
//        });
//        //必须监听后设置CheckBox的状态
//        holder.checkBox.setChecked(photo.isChecked());
        return view;
    }

    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener) {
        mOnShowItemClickListener = onShowItemClickListener;
    }

    public final class ViewHolder {
        private ImageView simpleDraweeView;

        public ViewHolder(View view) {
            simpleDraweeView = (ImageView) view.findViewById(R.id.simple_drawee_view);
            view.setTag(ViewHolder.this);
        }
    }

    public void setData(List<String> photos) {
        mPhotos = photos;
        notifyDataSetChanged();
    }

}
