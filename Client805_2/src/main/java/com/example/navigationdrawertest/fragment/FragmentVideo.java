package com.example.navigationdrawertest.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.SweetAlert.SweetAlertDialog;
import com.example.navigationdrawertest.activity.PhotoActivity;
import com.example.navigationdrawertest.activity.VideoActivity;
import com.example.navigationdrawertest.adapter.AlbumAdapter;
import com.example.navigationdrawertest.adapter.PictureListAdapter;
import com.example.navigationdrawertest.adapter.VideoListAdapter;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.camera1.video.entity.Media;
import com.example.navigationdrawertest.utils.FileOperation;
import com.example.navigationdrawertest.utils.VideoInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 2020/2/17.
 */

public class FragmentVideo extends Fragment implements AdapterView.OnItemClickListener {

    private GridView mGridView;
    private AlbumAdapter mAlbumAdapter;
    private RecyclerView mRecyclerView;
    private VideoListAdapter adapter;
//    private VideoAdapter adapter;
    Context context;
    private ArrayList<String> mPhotos = new ArrayList<String>();
    private ProgressDialog prodlg;
    private LinearLayout mNoPhoto;
    private String path;
    private ArrayList<Media> mediaList;
    ArrayList<VideoInfo> videoInfos;
    private ListView listView;

    public FragmentVideo(String path, ArrayList<VideoInfo> videoInfos){
        this.path = path;
        this.videoInfos = videoInfos;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
//                adapter.setList(videoInfos);
//                mRecyclerView.setAdapter(adapter);
                prodlg.dismiss();
                adapter.notifyDataSetChanged();
//                List<VideoInfo> videoInfos = (List<VideoInfo>) msg.obj;
//                MyAdapter adapter = new MyAdapter(videoInfos);
//                listView.setAdapter(adapter);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
        OrientApplication.app.setPageflage(1);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_photo, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.mRecyclerView);
//        listView = (ListView)v. findViewById(R.id.lv_show);
//        listView.setOnItemClickListener(context);
        mNoPhoto = (LinearLayout) v.findViewById(R.id.noPhoto);
        context = getActivity();
        initData();
        return v;
    }

    private void initData() {
        mPhotos = FileOperation.getVideoByPath(path, "mp4", "avi", "FLV");
        if (mPhotos.size() > 0) {
            mNoPhoto.setVisibility(View.GONE);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 6);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new VideoListAdapter(context, R.layout.video_list_item, mPhotos);
//        adapter = new VideoAdapter(context);
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10));
        new Thread(new Runnable() {
            @Override
            public void run() {
//                adapter.setList(videoInfos);
//                getAllVideoFiles(context, path);
                loadVaule();//oncreate()里定义这个方法特别耗时，导致整个界面启动超级慢，需要4/5秒的样子
                Message msg = new Message();
                msg.what = 0;
//                msg.obj = listPictures;

                mHandler.sendMessage(msg);

            }
        }).start();
        mRecyclerView.setAdapter(adapter);

        //从相册中选择照片 20190130 乔志理添加
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(context, VideoActivity.class);
                intent.putStringArrayListExtra("photos", mPhotos);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定删除?")
                        .setContentText("是否确定删除？")
                        .setCancelText("否")
                        .setConfirmText("是，删除！")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                prodlg = ProgressDialog.show(context, "删除", "正在删除照片");
                                prodlg.setIcon(getResources().getDrawable(R.drawable.logo_title));
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        FileOperation.deleteFile(mPhotos.get(position));
                                        mPhotos.remove(position);
                                        Message message = new Message();
                                        message.what = 1;
                                        mHandler.sendMessage(message);
                                    }
                                }).start();
                                sDialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

//    private ArrayList<VideoInfo> getAllVideoFiles(Context mContext) {
//        VideoInfo video = new VideoInfo();
//        ArrayList<VideoInfo> videos = new ArrayList<>();
//        for (Media media : mediaList) {
//            video.setVideoPath(media.path);
//            video.setSize(String.valueOf(media.size));
//            video.setVideoName(media.name);
//            //获取当前Video对应的ID, 然后根据该ID获取缩略图
//            int id = media.id;
//            String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
//            String[] selectionArgs = new String[]{
//                    id + ""
//            };
//            ContentResolver crThumb = mContext.getContentResolver();
//            BitmapFactory.Options options=new BitmapFactory.Options();
//            options.inSampleSize = 1;
//            Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
//
//            video.setThumbBitmap(curThumb);
//            videos.add(video);
//        }
//        return videos;
//    }

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

    //下面就看看这个方法里有多耗时
    private void loadVaule(){
        File file = new File(path);
        File[] files  = null;
        files = file.listFiles();
        videoInfos = new ArrayList<VideoInfo>();
        for (int i = 0; i < files.length; i++) {
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setThumbBitmap(getVideoThumbnail(files[i].getPath(), 200, 200, MediaStore.Images.Thumbnails.MICRO_KIND));
            videoInfo.setVideoPath(files[i].getPath());
            videoInfos.add(videoInfo);

        }

        Message msg = new Message();
        msg.what = 0;
        msg.obj = videoInfos;

        mHandler.sendMessage(msg);

    }


    //获取视频的缩略图
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
//         System.out.println("w"+bitmap.getWidth());
//         System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    //定义适配器放listview
    public class MyAdapter extends BaseAdapter {
        private List<VideoInfo> listPictures;

        public MyAdapter(List<VideoInfo> listPictures) {
            super();
            this.listPictures = listPictures;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listPictures.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listPictures.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup arg2) {
            // TODO Auto-generated method stu
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.video_list_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.video);
            TextView textView = (TextView) view.findViewById(R.id.tv_show);

            imageView.setImageBitmap(listPictures.get(position).getThumbBitmap());
            textView.setText(listPictures.get(position).getVideoPath());
            return view;

        }
    }





    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
        // TODO Auto-generated method stub
//        Toast.makeText(getApplicationContext(), "点击了"+arg2, 200).show();
//        startPlay(this, listPictures.get(arg2).getPath());//点击后打开播放器
//        Log.e("path", listPictures.get(arg2).getPath());
    }


    public static void startPlay(Context context, String url) {
        if (url.contains(".mp4") || url.contains(".MP4")) {
            Intent intent = new Intent();
            intent.setClassName(context, "com.easemob.chatuidemo.activity.VideoPlayerActivity");
            intent.putExtra("playpath", url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else if(url.contains("rtsp") || url.contains("RTSP")){
            Intent intent = new Intent();
            intent.setClassName(context, "com.easemob.chatuidemo.activity.MakeVideoActivity");
            intent.putExtra("playpath", url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (url.contains(".jpg") || url.contains(".png")) {
            Intent intent = new Intent();
            intent.setClassName(context, "com.easemob.chatuidemo.activity.VideoPlayerActivity");
            intent.putExtra("playpath", "/sdcard/VRDemo/capture/aa.jpg");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }else {
            Toast.makeText(context, "没有资源哦", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClassName(context, "com.easemob.chatuidemo.activity.MainActivity");
        }
    }

//    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.Holder>{
//
//        private Context context;
//        private ArrayList<VideoInfo> list = new ArrayList<>();
//
//        public VideoAdapter(Context context){
//            this.context = context;
//        }
//
//        private void   setList(ArrayList<VideoInfo> list){
//            this.list = list;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public VideoAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new VideoAdapter.Holder(LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(VideoAdapter.Holder holder, int position) {
//            if (holder instanceof VideoAdapter.Holder){
////                Glide.with(context).asBitmap().load(list.get(position).thumbBitmap).into(holder.imageView);
//                holder.imageView.setImageBitmap(list.get(position).getThumbBitmap());
//                Date date = new Date(list.get(position).getDuration());
//                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
//                holder.textView.setText(sdf.format(date));
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return list.size();
//        }
//
//        class Holder extends RecyclerView.ViewHolder{
//
//            TextView textView;
//            ImageView imageView;
//
//            public Holder(View itemView) {
//                super(itemView);
//                imageView = (ImageView) itemView.findViewById(R.id.image);
//                textView = (TextView) itemView.findViewById(R.id.textView_size);
//            }
//        }
//    }
//
//    class SpaceItemDecoration extends RecyclerView.ItemDecoration{
//        private int space;
//
//        public SpaceItemDecoration(int space){
//            this.space = space;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            outRect.left = space;
//            outRect.bottom = space;
//
//            if (parent.getChildLayoutPosition(view) % 3 == 0){
//                outRect.left = 0;
//            }
//        }
//    }

}



