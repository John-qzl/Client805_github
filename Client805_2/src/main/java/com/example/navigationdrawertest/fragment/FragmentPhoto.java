package com.example.navigationdrawertest.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.SweetAlert.SweetAlertDialog;
import com.example.navigationdrawertest.activity.AlbumActivity;
import com.example.navigationdrawertest.activity.PhotoActivity;
import com.example.navigationdrawertest.adapter.AlbumAdapter;
import com.example.navigationdrawertest.adapter.PictureListAdapter;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.utils.FileOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by qiaozhili on 2020/2/17 10:27.
 */

@SuppressLint("ValidFragment")
public class FragmentPhoto extends Fragment {

    private GridView mGridView;
    private AlbumAdapter mAlbumAdapter;
    private RecyclerView mRecyclerView;
    private PictureListAdapter adapter;
    Context context;
    private ArrayList<String> mPhotos = new ArrayList<String>();
    private ProgressDialog prodlg;
    private LinearLayout mNoPhoto;
    private String path;

    public FragmentPhoto(String path){
        this.path = path;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                prodlg.dismiss();
                adapter.notifyDataSetChanged();
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
        mNoPhoto = (LinearLayout) v.findViewById(R.id.noPhoto);
        context = getActivity();
        initData();
        return v;
    }

    private void initData() {
        mPhotos = FileOperation.getAlbumByPath(path, "jpg", "png", "mp4", "avi", "FLV");
        if (mPhotos.size() > 0) {
            mNoPhoto.setVisibility(View.GONE);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 6);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new PictureListAdapter(context, R.layout.image_list_item, mPhotos);
        mRecyclerView.setAdapter(adapter);

//        //从相册中选择照片 20190130 乔志理添加
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(context, PhotoActivity.class);
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
                        .setContentText("是否确定删除本张照片？")
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
}
