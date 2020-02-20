package com.example.navigationdrawertest.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.camera1.video.entity.Media;

import java.io.File;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by user on 2020/2/20.
 */

public class VideoPagerAdapter extends PagerAdapter {
    private LayoutInflater mLayoutInflater;
    private List<String> mPhotos;
    private Context mContext;

    public VideoPagerAdapter(Context context, List<String> photos) {
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
        View layout = mLayoutInflater.inflate(R.layout.page_item_video, container, false);
        VideoView videoView = (VideoView) layout.findViewById(R.id.item_Video);
        Uri uri = Uri.fromFile(new File(mPhotos.get(position)));
        MediaController mediaController = new MediaController(mContext);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();
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
