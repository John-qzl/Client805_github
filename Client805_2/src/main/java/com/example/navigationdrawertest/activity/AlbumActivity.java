package com.example.navigationdrawertest.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.SweetAlert.SweetAlertDialog;
import com.example.navigationdrawertest.adapter.AlbumAdapter;
import com.example.navigationdrawertest.adapter.PictureListAdapter;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.camera.ImageDirectoryModel;
import com.example.navigationdrawertest.camera.MyUtils;
import com.example.navigationdrawertest.camera.PicPathEvent;
import com.example.navigationdrawertest.camera.PickOrTakeImageActivity;
import com.example.navigationdrawertest.camera.PickOrTakeVideoActivity;
import com.example.navigationdrawertest.camera.SingleImageModel;
import com.example.navigationdrawertest.camera1.CameraActivity;
import com.example.navigationdrawertest.camera1.video.PickerActivity;
import com.example.navigationdrawertest.camera1.video.PickerConfig;
import com.example.navigationdrawertest.camera1.video.entity.Media;
import com.example.navigationdrawertest.fragment.AlbumFragment;
import com.example.navigationdrawertest.fragment.HomeFragment;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.utils.CommonUtil;
import com.example.navigationdrawertest.utils.FileOperation;
import com.example.navigationdrawertest.utils.HtmlHelper;
import com.example.navigationdrawertest.utils.VideoInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.example.navigationdrawertest.camera.PickOrTakeImageActivity.CODE_FOR_TAKE_PIC;

/**
 * Created by qiaozhili on 2019/2/12 15:26.
 */
public class AlbumActivity extends FragmentActivity {
//    private GridView mGridView;
//    private AlbumAdapter mAlbumAdapter;
    private String path;
    private ArrayList<String> mPhotos = new ArrayList<String>();
    private ArrayList<String> mVideos = new ArrayList<String>();
    private ArrayList<String> mPhotosnew = new ArrayList<String>();
    private ProgressDialog prodlg;
    private final static int MAXIMGNUMBER = 100;
//    private PictureListAdapter adapter;
//    private RecyclerView mRecyclerView;
    private LinearLayout mAddPhoto, mAddVideo, mTakePic;
    private ImageView mBack;
    private String mCheck = "";
    private ArrayList<Media> mediaList;
    private LinearLayout mNoPhoto;
    private static int localPosition = 0;
    String tempPath = null;
    private MyHandler handler;
    private int currentShowPosition;
    private ArrayList<VideoInfo> videoInfos = new ArrayList<>();
    /**
     * 按时间排序的所有图片list
     */
    private ArrayList<SingleImageModel> allImages;
    /**
     * 按目录排序的所有图片list
     */
    private ArrayList<SingleImageDirectories> imageDirectories;

    /**
     * 一个文件夹中的图片数据实体
     */
    private class SingleImageDirectories {
        /**
         * 父目录的路径
         */
        public String directoryPath;
        /**
         * 目录下的所有图片实体
         */
        public ImageDirectoryModel images;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                prodlg.dismiss();
//                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        getActionBar().hide();
        EventBus.getDefault().register(this);
        path = getIntent().getStringExtra("path");
        mCheck = getIntent().getStringExtra("checkType");
        handler = new MyHandler(this);
        localPosition = 0;
        selectItem(localPosition);
        initUI();
        initData();
        allImages = new ArrayList<SingleImageModel>();
        imageDirectories = new ArrayList<SingleImageDirectories>();
        getAllImages();
        getCurrentAblumPosition();
    }

    private void initUI() {
        mBack = (ImageView) findViewById(R.id.iv_go_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAddPhoto = (LinearLayout) findViewById(R.id.lin_add_photo);
        mAddVideo = (LinearLayout) findViewById(R.id.lin_add_video);
        mTakePic = (LinearLayout) findViewById(R.id.lin_takepic);
        if (mCheck.equals("check")) {
            mAddPhoto.setVisibility(View.VISIBLE);
            mTakePic.setVisibility(View.VISIBLE);
            mAddVideo.setVisibility(View.VISIBLE);
        } else {
            mAddPhoto.setVisibility(View.INVISIBLE);
            mTakePic.setVisibility(View.INVISIBLE);
            mAddVideo.setVisibility(View.INVISIBLE);
        }
        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentAblumPosition();
                if (mPhotos != null) {
                    int surplus_pics = MAXIMGNUMBER - mPhotos.size();//mPhotos没有变
                    Intent intent = new Intent(AlbumActivity.this, PickOrTakeImageActivity.class);
                    intent.putExtra("pic_max", surplus_pics);
                    intent.putExtra("currentShowPosition", currentShowPosition);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AlbumActivity.this, PickOrTakeImageActivity.class);
                    intent.putExtra("pic_max", 100);
                    startActivity(intent);
                }
            }
        });
        mAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mVideos = FileOperation.getAlbumVideoByPath(path, "mp4", "avi", "FLV");
                if (mVideos != null) {
                    int surplus_vids = 5 - mVideos.size();//mPhotos没有变
                    Intent intent =new Intent(AlbumActivity.this, PickerActivity.class);
                    intent.putExtra(PickerConfig.SELECT_MODE,PickerConfig.PICKER_VIDEO);//default image and video (Optional)
                    long maxSize=188743680L;//long long long
                    intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); //default 180MB (Optional)
                    intent.putExtra(PickerConfig.MAX_SELECT_COUNT,surplus_vids);  //default 5 (Optional)
                    intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, mediaList); // (Optional)
                    intent.putExtra("currentShowPosition", currentShowPosition);
                    AlbumActivity.this.startActivityForResult(intent,200);
                } else {
                    Intent intent =new Intent(AlbumActivity.this, PickerActivity.class);
                    intent.putExtra(PickerConfig.SELECT_MODE,PickerConfig.PICKER_VIDEO);//default image and video (Optional)
                    long maxSize=108743680L;//long long long
                    intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); //default 180MB (Optional)
                    intent.putExtra(PickerConfig.MAX_SELECT_COUNT,5);  //default 5 (Optional)
                    intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, mediaList); // (Optional)
                    intent.putExtra("currentShowPosition", currentShowPosition);
                    AlbumActivity.this.startActivityForResult(intent,200);
                }
            }
        });
        mTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(AlbumActivity.this, CameraActivity.class);
                intent2.putExtra("path", path);
                startActivity(intent2);
            }
        });

    }

    private void initData() {
        mPhotos = FileOperation.getAlbumByPath(path, "jpg", "png");
        mVideos = FileOperation.getAlbumVideoByPath(path, "mp4", "avi", "FLV");
        if (mPhotos.size() > 0) {
//            mNoPhoto.setVisibility(View.GONE);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        videoInfos = getAllVideoFiles(getBaseContext(), path);
                        selectItem(localPosition);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPhotos = FileOperation.getAlbumByPath(path, "jpg", "png");
//        adapter.notifyDataSetChanged();
        initData();
        selectItem(localPosition);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PicPathEvent event) {
        mPhotosnew.remove(null);
        mPhotosnew.addAll(event.getPathList());

        for (int i = 0; i < mPhotosnew.size(); i++) {
            copyFile(mPhotosnew.get(i), path, getPhotoName(mPhotosnew.get(i)));
            mPhotos.add(mPhotosnew.get(i));
            System.out.print(mPhotosnew.get(i));
        }
        mPhotosnew.clear();
        event.getPathList().clear();
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode==PickerConfig.RESULT_CODE){
            mediaList =data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            VideoInfo video = new VideoInfo();
            Log.i("mediaList","mediaList.size"+ mediaList.size());
            for(Media media: mediaList){
                Log.i("media",media.path);
                Log.e("media","s:"+media.size);
                getCopyVideo(media.path, path, media.name);
                mPhotos.add(media.path);

                video.setVideoPath(media.path);
                video.setSize(String.valueOf(media.size));
                video.setVideoName(media.name);
                //获取当前Video对应的ID, 然后根据该ID获取缩略图
                int id = media.id;
                String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
                String[] selectionArgs = new String[]{
                        id + ""
                };
                ContentResolver crThumb = this.getContentResolver();
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);

                video.setThumbBitmap(curThumb);
                videoInfos.add(video);
            }
            selectItem(localPosition);
        }
    }

    /**
     * 复制单个文件  （可考虑添加复制进度条）
     *
     * @param oldPath 原文件路径 如：c:/fqf.txt String
     * @param newPath 复制后路径 如：f:/fqf.txt
     */
    public static void copyFile(String oldPath, String newPath, String photoName) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists()) {
                newfile.mkdir();
            }
            String newfile1 = newfile + "/" + photoName;
            if (oldfile.exists()) {
                //文件存在时
                InputStream inStream = new FileInputStream(oldPath);//读入原文件
                FileOutputStream fs = new FileOutputStream(newfile1);
                byte[] buffer = new byte[1444];
                int length;
                int value = 0;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;//字节数 文件大小
                    value++; //计数
//                     System.out.println("完成"+bytesum+"  总大小"+fileTotalLength);
                    fs.write(buffer, 0, byteread);
                    Message msg = new Message(); //创建一个msg对象
                    msg.what = 110;
                    msg.arg1 = value; //当前的value
//                    handler.sendMessage(msg);
//                    Thread.sleep(10);//每隔10ms发送一消息，也就是说每隔10ms value就自增一次，将这个value发送给主线程处理
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * @Description: 获取图片名称
     * @author qiaozhili
     * @date 2019/2/12 15:28
     * @param
     * @return
     */
    private String getPhotoName(String uri) {
        String temp[] = uri.replaceAll("\\\\", "/").split("/");
        String fileName = "";
        if (temp.length > 1) {
            fileName = temp[temp.length - 1];
            System.out.println(fileName);
        }
        return fileName;
    }

    /**
     * 调用系统相机进行拍照
     */
    private void takePic() {
        String name = "temp";
        if (!new File(CommonUtil.getDataPath()).exists())
            new File(CommonUtil.getDataPath()).mkdirs();
        path = CommonUtil.getDataPath() + name + System.currentTimeMillis() + ".jpg";
        File file = new File(path);
        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CODE_FOR_TAKE_PIC);
    }

    public void selectItem(final int position) {
        Fragment fragment = null;
        OrientApplication.getApplication().setCommander(false);
        fragment = new AlbumFragment(path, videoInfos,mCheck);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.album_content_frame, fragment).commit();
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    /**
     * 从手机中获取所有的手机图片
     */
    private void getAllImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SingleImageModel temp = new SingleImageModel();
                temp.path = tempPath;
                temp.date = System.currentTimeMillis();
                temp.id = 0;


                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContentResolver();
                //获取jpeg和png格式的文件，并且按照时间进行倒序
                Cursor cursor = contentResolver.query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=\"image/jpeg\" or " +
                                MediaStore.Images.Media.MIME_TYPE + "=\"image/png\" or "+ MediaStore.Video.Media.MIME_TYPE+ "=\"video/mp4\""
                        , null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
                if (cursor != null) {
                    allImages.clear();
                    if (!TextUtils.isEmpty(temp.path)) {
                        allImages.add(temp);
                    }
                    while (cursor.moveToNext()) {
                        SingleImageModel singleImageModel = new SingleImageModel();
                        singleImageModel.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        try {
                            singleImageModel.date = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
                        } catch (NumberFormatException e) {
                            singleImageModel.date = System.currentTimeMillis();
                        }
                        try {
                            singleImageModel.id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)));
                        } catch (NumberFormatException e) {
                            singleImageModel.id = 0;
                        }

                        //存入按照目录分配的list
                        String path = singleImageModel.path;
                        File file = new File(path);
                        String parentPath = file.getParent();

                        //图片是否破损
                        boolean isFalseFile = false;
                        BitmapFactory.Options options = null;
                        if (options == null) options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;

                        BitmapFactory.decodeFile(path, options); //filePath代表图片路径
                        if (options.mCancel || options.outWidth == -1
                                || options.outHeight == -1) {
                            //表示图片已损毁
                            isFalseFile = true;
                        }

                        if (file.length() > 0 && !isFalseFile) {
                            allImages.add(singleImageModel);
                            putImageToParentDirectories(parentPath, path, singleImageModel.date, singleImageModel.id);
                        }
                    }
                    cursor.close();
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    /**
     * 从手机中获取所有的手机图片
     */
    private void getCopyVideo(final String oldPath, final String newPath, final String photoName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int bytesum = 0;
                    int byteread = 0;
                    File oldfile = new File(oldPath);
                    File newfile = new File(newPath);
                    if (!newfile.exists()) {
                        newfile.mkdir();
                    }
                    String newfile1 = newfile + "/" + photoName;
                    if (oldfile.exists()) {
                        //文件存在时
                        InputStream inStream = new FileInputStream(oldPath);//读入原文件
                        FileOutputStream fs = new FileOutputStream(newfile1);
                        byte[] buffer = new byte[1444];
                        int length;
                        int value = 0;
                        while ((byteread = inStream.read(buffer)) != -1) {
                            bytesum += byteread;//字节数 文件大小
                            value++; //计数
//                     System.out.println("完成"+bytesum+"  总大小"+fileTotalLength);
                            fs.write(buffer, 0, byteread);
                            Message msg = new Message(); //创建一个msg对象
                            msg.what = 110;
                            msg.arg1 = value; //当前的value
//                    handler.sendMessage(msg);
//                    Thread.sleep(10);//每隔10ms发送一消息，也就是说每隔10ms value就自增一次，将这个value发送给主线程处理
                        }
                        inStream.close();
                    }
                } catch (Exception e) {
                    System.out.println("复制单个文件操作出错");
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 将图片插入到对应parentPath路径的文件夹中
     */
    private void putImageToParentDirectories(String parentPath, String path, long date, long id) {
        ImageDirectoryModel model = getModelFromKey(parentPath);
        if (model == null) {
            model = new ImageDirectoryModel();
            SingleImageDirectories directories = new SingleImageDirectories();
            directories.images = model;
            directories.directoryPath = parentPath;
            imageDirectories.add(directories);
        }
        model.addImage(path, date, id);
    }

    private ImageDirectoryModel getModelFromKey(String path) {
        for (SingleImageDirectories directories : imageDirectories) {
            if (directories.directoryPath.equalsIgnoreCase(path)) {
                return directories.images;
            }
        }
        return null;
    }

    /**
     * leak memory
     */
    private static class MyHandler extends Handler {

        WeakReference<AlbumActivity> activity = null;

        public MyHandler(AlbumActivity context) {
            activity = new WeakReference<AlbumActivity>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private void getCurrentAblumPosition() {
        for (int i = 0; i < imageDirectories.size(); i++) {
            String dircFullname = imageDirectories.get(i).directoryPath;
            String dircName = dircFullname.substring(dircFullname.lastIndexOf("/") + 1, dircFullname.length());
            if (dircName.equals("Camera")) {
                currentShowPosition = i;
            }
        }
    }

    public static ArrayList<VideoInfo> getAllVideoFiles(Context mContext, String path) {
        VideoInfo video;
        ArrayList<VideoInfo> videos = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, null);
            while (cursor.moveToNext()) {
                video = new VideoInfo();

                if (cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)) != 0) {
                    video.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
                    video.setVideoPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                    video.setCreateTime(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)));
                    video.setVideoName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                    //获取当前Video对应的ID, 然后根据该ID获取缩略图
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
                    String[] selectionArgs = new String[]{
                            id + ""
                    };
                    ContentResolver crThumb = mContext.getContentResolver();
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);

                    video.setThumbBitmap(curThumb);
                    videos.add(video);
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videos;
    }
}
