package com.example.navigationdrawertest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.SweetAlert.SweetAlertDialog;
import com.example.navigationdrawertest.adapter.AlbumAdapter;
import com.example.navigationdrawertest.adapter.PictureListAdapter;
import com.example.navigationdrawertest.camera.PicPathEvent;
import com.example.navigationdrawertest.camera.PickOrTakeImageActivity;
import com.example.navigationdrawertest.utils.FileOperation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by qiaozhili on 2019/2/12 15:26.
 */
public class AlbumActivity extends BaseActivity {

    private GridView mGridView;
    private AlbumAdapter mAlbumAdapter;
    private String path;
    private ArrayList<String> mPhotos = new ArrayList<String>();
    private ArrayList<String> mPhotosnew = new ArrayList<String>();
    private ProgressDialog prodlg;
    private final static int MAXIMGNUMBER = 10;
    private PictureListAdapter adapter;
    private RecyclerView mRecyclerView;
    private Button mAddPhoto;
    private ImageView mBack;
    private String mCheck = "";

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
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        getActionBar().hide();
        EventBus.getDefault().register(this);
        path = getIntent().getStringExtra("path");
        mCheck = getIntent().getStringExtra("checkType");
        initUI();
        initData();
    }

    private void initUI() {
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mBack = (ImageView) findViewById(R.id.iv_go_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAddPhoto = (Button) findViewById(R.id.bt_add_photo);
        if (mCheck.equals("check")) {
            mAddPhoto.setVisibility(View.VISIBLE);
        } else {
            mAddPhoto.setVisibility(View.INVISIBLE);
        }
        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotos != null) {
                    int surplus_pics = MAXIMGNUMBER - mPhotos.size() + 1;//mPhotos没有变
                    Intent intent = new Intent(AlbumActivity.this, PickOrTakeImageActivity.class);
                    intent.putExtra("pic_max", surplus_pics);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AlbumActivity.this, PickOrTakeImageActivity.class);
                    intent.putExtra("pic_max", 10);
                    startActivity(intent);
                }
            }
        });

    }

    private void initData() {
        mPhotos = FileOperation.getAlbumByPath(path, "jpg");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new PictureListAdapter(this, R.layout.image_list_item, mPhotos);
        mRecyclerView.setAdapter(adapter);
        //适配器刚开始绑定的数据为空
//        mAlbumAdapter = new AlbumAdapter(this, mPhotos, new AlbumAdapter.OnShowItemClickListener() {
//
//			@Override
//			public void onShortClick() {
//
//			}
//
//			@Override
//			public void onLongClick() {
//
//			}
//		});
//        mGridView.setAdapter(mAlbumAdapter);
//        mGridView.setOnItemClickListener(new OnItemClickListener() {
//        	@Override
//        	public void onItemClick(AdapterView<?> parent, View view, int position,
//        			long id) {
//        		Intent intent = new Intent(AlbumActivity.this, PhotoActivity.class);
//        		intent.putStringArrayListExtra("photos", mPhotos);
//        		intent.putExtra("position", position);
//        		startActivity(intent);
//        	}
//		});
//        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
//        	@Override
//        	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
//        			long id) {
//        		new SweetAlertDialog(AlbumActivity.this, SweetAlertDialog.WARNING_TYPE)
//                .setTitleText("确定删除?")
//                .setContentText("是否确定删除本张照片？")
//                .setCancelText("否")
//                .setConfirmText("是，删除！")
//                .showCancelButton(true)
//                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                    	sDialog.dismiss();
//                    }
//                })
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                    	prodlg = ProgressDialog.show(AlbumActivity.this, "删除", "正在删除照片");
//            			prodlg.setIcon(getResources().getDrawable(R.drawable.logo_title));
//            			new Thread(new Runnable() {
//            				@Override
//            				public void run() {
//            					FileOperation.deleteFile(mPhotos.get(position));
//            					mPhotos.remove(position);
//            					Message message = new Message();
//            					message.what = 1;
//            					mHandler.sendMessage(message);
//            				}
//            			}).start();
//            			sDialog.dismiss();
//                    }
//                })
//                .show();
//        		return true;
//        	}
//        });

        //从相册中选择照片 20190130 乔志理添加
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(AlbumActivity.this, PhotoActivity.class);
                intent.putStringArrayListExtra("photos", mPhotos);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                new SweetAlertDialog(AlbumActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                                prodlg = ProgressDialog.show(AlbumActivity.this, "删除", "正在删除照片");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
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
        adapter.notifyDataSetChanged();
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
