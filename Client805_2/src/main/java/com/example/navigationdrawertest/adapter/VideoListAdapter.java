package com.example.navigationdrawertest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.navigationdrawertest.CustomUI.LoaderNativeImage;
import com.example.navigationdrawertest.R;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by qiaozhili on 2020/2/19 19:46.
 */

public class VideoListAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
    private Context mContext;
    private final LoaderNativeImage loaderNativeImage;

    public VideoListAdapter(Context mContext, int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
        this.mContext=mContext;
        loaderNativeImage = new LoaderNativeImage(mContext);
    }

    @Override
    protected void convert(BaseViewHolder helper, String url) {
        ImageView img = helper.getView(R.id.video);
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
//            videoInfo.setVisibility(View.INVISIBLE);
        }
        double size = getFileOrFilesSize(url, 3);
        videoSize.setText(String.valueOf(size) + "M");
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            blockSize = getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"获取文件大小失败!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e(TAG,"获取文件大小不存在!");
        }
        return size;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
        return fileSizeLong;
    }
}
