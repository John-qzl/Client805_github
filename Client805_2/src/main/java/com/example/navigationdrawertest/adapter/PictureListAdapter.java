package com.example.navigationdrawertest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.navigationdrawertest.CustomUI.LoaderNativeImage;
import com.example.navigationdrawertest.R;

import java.util.List;


/**
 * @Description描述:
 * @Author作者: xsy
 * @Date日期: 2018/10/8
 */

public class PictureListAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
    private Context mContext;
    private final LoaderNativeImage loaderNativeImage;

    public PictureListAdapter(Context mContext, int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
        this.mContext=mContext;
        loaderNativeImage = new LoaderNativeImage(mContext);
    }

    @Override
    protected void convert(BaseViewHolder helper, String url) {
        ImageView img = helper.getView(R.id.image);
        RelativeLayout videoInfo = helper.getView(R.id.rl_video_info);
        TextView videoSize = helper.getView(R.id.textView_size);
        String imgType = "";
        if (url.length() > 0) {
            imgType= url.substring(url.length()-3,url.length());
        }
        if (TextUtils.isEmpty(url)){
//            img.setImageResource(R.drawable.ic_add_img);
        } else if (imgType.equals("jpg") || imgType.equals("png")) {
            loaderNativeImage.displayImage(url, img);
        } else if (imgType.equals("mp4") || imgType.equals("avi") || imgType.equals("FLV")){
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MICRO_KIND);
            img.setImageBitmap(bitmap);
            videoInfo.setVisibility(View.INVISIBLE);
        }
    }
}
