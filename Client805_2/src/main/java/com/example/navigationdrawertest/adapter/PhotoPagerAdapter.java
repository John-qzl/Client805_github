package com.example.navigationdrawertest.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.navigationdrawertest.R;

import uk.co.senab.photoview.PhotoView;

/**
 * 创建时间：2016/10/10
 * 创建者：Young
 * 功能描述：PhotoActivity页面ViewPager的适配器
 * 其他：
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private LayoutInflater mLayoutInflater;
    private List<String> mPhotos;
    private Context mContext;

    public PhotoPagerAdapter(Context context, List<String> photos) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        mPhotos = photos;
    }

    @Override
    public int getCount() {
        return mPhotos == null ? 0 : mPhotos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View layout = mLayoutInflater.inflate(R.layout.page_item_image, container, false);
        PhotoView photoDraweeView = (PhotoView) layout.findViewById(R.id.photoDraweeView);
        Uri uri = Uri.fromFile(new File(mPhotos.get(position)));
        setImageToPhotoDraweeView(photoDraweeView, uri);
        container.addView(layout, 0);
        return layout;
    }

    private void setImageToPhotoDraweeView(ImageView pdv, Uri uri) {
    	Glide
	    .with(mContext)
	    .load(uri)
	    .fitCenter()
	    .into(pdv);
    }
}
