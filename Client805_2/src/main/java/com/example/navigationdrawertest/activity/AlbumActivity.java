package com.example.navigationdrawertest.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.navigationdrawertest.camera.MyUtils;
import com.example.navigationdrawertest.camera.PicPathEvent;
import com.example.navigationdrawertest.camera.PickOrTakeImageActivity;
import com.example.navigationdrawertest.camera.PickOrTakeVideoActivity;
import com.example.navigationdrawertest.camera1.CameraActivity;
import com.example.navigationdrawertest.camera1.video.PickerActivity;
import com.example.navigationdrawertest.camera1.video.PickerConfig;
import com.example.navigationdrawertest.camera1.video.entity.Media;
import com.example.navigationdrawertest.utils.CommonUtil;
import com.example.navigationdrawertest.utils.FileOperation;
import com.example.navigationdrawertest.utils.HtmlHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.navigationdrawertest.camera.PickOrTakeImageActivity.CODE_FOR_TAKE_PIC;

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
    private final static int MAXIMGNUMBER = 100;
    private PictureListAdapter adapter;
    private RecyclerView mRecyclerView;
    private Button mAddPhoto, mAddVideo, mTakePic;
    private ImageView mBack;
    private String mCheck = "";
    private ArrayList<Media> mediaList;
    private LinearLayout mNoPhoto;

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
//        mGridView = ((GridView) findViewById(R.id.gridView));
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mNoPhoto = (LinearLayout) findViewById(R.id.noPhoto);
        mBack = (ImageView) findViewById(R.id.iv_go_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAddPhoto = (Button) findViewById(R.id.bt_add_photo);
        mAddVideo = (Button) findViewById(R.id.bt_add_video);
        mTakePic = (Button) findViewById(R.id.bt_takepic);
        if (mCheck.equals("check")) {
            mAddPhoto.setVisibility(View.VISIBLE);
            mTakePic.setVisibility(View.VISIBLE);
//            mAddVideo.setVisibility(View.VISIBLE);
        } else {
            mAddPhoto.setVisibility(View.INVISIBLE);
            mTakePic.setVisibility(View.INVISIBLE);
//            mAddVideo.setVisibility(View.INVISIBLE);
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
        mAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotos != null) {
                    Intent intent =new Intent(AlbumActivity.this, PickerActivity.class);
                    intent.putExtra(PickerConfig.SELECT_MODE,PickerConfig.PICKER_VIDEO);//default image and video (Optional)
                    long maxSize=188743680L;//long long long
                    intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); //default 180MB (Optional)
                    intent.putExtra(PickerConfig.MAX_SELECT_COUNT,15);  //default 40 (Optional)
                    intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, mediaList); // (Optional)
                    AlbumActivity.this.startActivityForResult(intent,200);
                } else {
                    Intent intent =new Intent(AlbumActivity.this, PickerActivity.class);
                    intent.putExtra(PickerConfig.SELECT_MODE,PickerConfig.PICKER_VIDEO);//default image and video (Optional)
                    long maxSize=188743680L;//long long long
                    intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); //default 180MB (Optional)
                    intent.putExtra(PickerConfig.MAX_SELECT_COUNT,15);  //default 40 (Optional)
                    intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, mediaList); // (Optional)
                    AlbumActivity.this.startActivityForResult(intent,200);
                }
            }
        });
        mTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HtmlHelper.changePhotoValue(htmlDoc, operation);
//				PhotoUtils.transferCamera(CheckActivity1.this, path);
                Intent intent2 = new Intent();
                intent2.setClass(AlbumActivity.this, CameraActivity.class);
                intent2.putExtra("path", path);
                startActivity(intent2);
            }
        });

    }

    private void initData() {
        mPhotos = FileOperation.getAlbumByPath(path, "jpg", "png", "mp4", "avi", "FLV");
        if (mPhotos.size() > 0) {
            mNoPhoto.setVisibility(View.GONE);
        }
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

//        //从相册中选择照片 20190130 乔志理添加
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
//        mPhotos = FileOperation.getAlbumByPath(path, "jpg", "png");
//        adapter.notifyDataSetChanged();
        initData();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode==PickerConfig.RESULT_CODE){
            mediaList =data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            Log.i("mediaList","mediaList.size"+ mediaList.size());
            for(Media media: mediaList){
                Log.i("media",media.path);
                Log.e("media","s:"+media.size);

                mPhotos.add(media.path);
            }
            adapter.notifyDataSetChanged();
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

//    public class AlbumAdapter1 extends BaseAdapter {
//
//        private Context mContext;
//        private LayoutInflater mLayoutInflater;
//        private List<String> mPhotos;
//        //全局变量，记录CheckBox是否可见
//        private boolean isShowCheckBox;
////        private OnShowItemClickListener1 mOnShowItemClickListener1;
//
////        //定义接口
////        public interface OnShowItemClickListener1 {
////            //        void onShowItemClick(String str);
////            void onShortClick();
////            void onLongClick();
////        }
//
//        public boolean isShowCheckBox() {
//            return isShowCheckBox;
//        }
//
//        public void setShowCheckBox(boolean showCheckBox) {
//            isShowCheckBox = showCheckBox;
//        }
//
////        public AlbumAdapter1(Context context, List<String> photos, OnShowItemClickListener1 onShowItemClickListener1) {
////            mContext = context;
////            mLayoutInflater = LayoutInflater.from(context);
////            mPhotos = photos;
////            mOnShowItemClickListener1 = onShowItemClickListener1;
////        }
//
//        @Override
//        public int getCount() {
//            return mPhotos == null ? 0 : mPhotos.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return mPhotos.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(final int i, View view, ViewGroup viewGroup) {
//            ViewHolder holder;
//
//            if (i == 0) {
//                view = new ImageView(AlbumActivity.this);
//                view.setBackgroundResource(R.drawable.take_pic);
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (MyUtils.checkTakePhotoPermissions(AlbumActivity.this, AlbumActivity.this)) {
////                            takePic();
//                        }
//                    }
//                });
////                view.setLayoutParams(new GridView.LayoutParams(perWidth, perWidth));
//                return view;
//            }
//
//            if (view == null) {
//                view = mLayoutInflater.inflate(R.layout.list_item_photo, viewGroup, false);
//                holder = new ViewHolder(view);
//            } else {
//                holder = (ViewHolder) view.getTag();
//            }
//            String str = mPhotos.get(i);
//            Bitmap bitmap = BitmapFactory.decodeFile(str);
//            if(bitmap != null)
//                Glide
//                        .with(mContext)
//                        .load(str)
//                        .override(80, 80)
//                        .fitCenter()
//                        .into(holder.simpleDraweeView);
//            return view;
//        }
//
////        public void setOnShowItemClickListener(OnShowItemClickListener1 onShowItemClickListener1) {
////            mOnShowItemClickListener1 = onShowItemClickListener1;
////        }
//
//        public final class ViewHolder {
//            private ImageView simpleDraweeView;
//
//            public ViewHolder(View view) {
//                simpleDraweeView = (ImageView) view.findViewById(R.id.simple_drawee_view);
//                view.setTag(ViewHolder.this);
//            }
//        }
//
//        public void setData(List<String> photos) {
//            mPhotos = photos;
//            notifyDataSetChanged();
//        }
//
//    }

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

}
