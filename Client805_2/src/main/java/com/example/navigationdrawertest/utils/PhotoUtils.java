package com.example.navigationdrawertest.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;

/**
 * 调用系统相机工具类  COPY BY GNY
 * Created by LYC on 2017/2/21.
 */
public class PhotoUtils {

//    public interface callPhotoInfo{
//        public void initPhotoInfo();
//    }

    public static String photoName1 = "";
    public static String checkOpMainId1 = "";
    public static String fileStr1 = "";

    /**
     * Activity调用系统相机
     *
     * @param activity 调用此工具类的Activity
     * @param dirStr   照片保存的文件夹
     * @param view     Snackbar要依附的view源
     */
    public static void transferCamera(Activity activity, String dirStr) {
        //如果sd卡没有挂载则返回
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        	Toast.makeText(activity, "sd卡异常！", Toast.LENGTH_SHORT).show();
//            Snackbar.make(view, "sd卡异常!", Snackbar.LENGTH_LONG).show();
            return;
        }
        //下面的操作是在sd卡挂载成功的时候才进行的
        //创建文件的目录
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //获取时间,这里用时间作为照片的名字
        String photoName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", 
        		Locale.CHINA).format(new Date(System.currentTimeMillis()))
                + Setting.PHOTO_NAME_SUFFIX;
        File file = new File(dirStr + File.separator + photoName);
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        //上面的操作会将拍摄的照片保存到所指定的文件目录下，照片名字以时间命名，照片的格式为jpg
        //下面的代码会调用系统自带的拍照应用
        photoName1 = photoName;
        fileStr1 = dirStr + File.separator + photoName;
        activity.startActivityForResult(intent, CAMERA);
    }


    /**
     * @param fragment      调用此工具类的Fragment
     * @param dirStr        保存路径
     * @param view          snackbar的依赖view
     * @param checkOpMainId checkOp的主键id
     */
    public static void transferCamera(Fragment fragment, String dirStr, View view, int checkOpMainId) {
        //如果sd卡没有挂载则返回
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        //下面的操作是在sd卡挂载成功的时候才进行的
        //创建文件的目录
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //获取时间,这里用时间作为照片的名字
        String photoName = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        .format(new Date(System.currentTimeMillis()))
                + Setting.PHOTO_NAME_SUFFIX;
        File file = new File(dirStr + File.separator + photoName);
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        //上面的操作会将拍摄的照片保存到所指定的文件目录下，照片名字以时间命名，照片的格式为jpg
        //下面的代码会调用系统自带的拍照应用
        photoName1 = photoName;
        checkOpMainId1 = checkOpMainId + "";
        fileStr1 = dirStr + File.separator + photoName;
//        intent.putExtra("photoName", photoName);
//        intent.putExtra("checkOpMainId", checkOpMainId);
//        intent.putExtra("fileStr", dirStr + File.separator + photoName);
        fragment.startActivityForResult(intent, CAMERA);
    }

}
