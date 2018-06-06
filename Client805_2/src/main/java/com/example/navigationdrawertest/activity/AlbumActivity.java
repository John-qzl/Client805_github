package com.example.navigationdrawertest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.SweetAlert.SweetAlertDialog;
import com.example.navigationdrawertest.adapter.AlbumAdapter;
import com.example.navigationdrawertest.utils.FileOperation;

import java.util.ArrayList;

/**
 * 创建时间：2016/10/8
 * 创建者：Young
 * 功能描述：1.查看照片的缩略图;2.提供删除照片;3.点击缩略图跳转到详图页面（PhotoActivity页面）
 * 其他：
 */
public class AlbumActivity extends BaseActivity {

    private GridView mGridView;
    private AlbumAdapter mAlbumAdapter;
    private String path;
    private ArrayList<String> mPhotos = new ArrayList<String>();
    private ProgressDialog prodlg;
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1) {
            	prodlg.dismiss();
            	mAlbumAdapter.notifyDataSetChanged();
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_album);
    	path = getIntent().getStringExtra("path");
    	initUI();
    	initData();
    }
    
    private void initUI(){
    	mGridView = ((GridView) findViewById(R.id.gridView));
    }
    
    private void initData(){
    	mPhotos = FileOperation.getAlbumByPath(path, "jpg");
        //适配器刚开始绑定的数据为空
        mAlbumAdapter = new AlbumAdapter(this, mPhotos, new AlbumAdapter.OnShowItemClickListener() {
			
			@Override
			public void onShortClick() {
				
			}
			
			@Override
			public void onLongClick() {
				
			}
		});
        mGridView.setAdapter(mAlbumAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position,
        			long id) {
        		Intent intent = new Intent(AlbumActivity.this, PhotoActivity.class);
        		intent.putStringArrayListExtra("photos", mPhotos);
        		intent.putExtra("position", position);
        		startActivity(intent);
        	}
		});
        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
        			long id) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:  
                finish();
                return true;
            default:  
    	        return super.onOptionsItemSelected(item);  
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();  
	    inflater.inflate(R.menu.readactivity, menu);  
	    return true;
    }

}
