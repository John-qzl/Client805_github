package com.example.navigationdrawertest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.adapter.PhotoPagerAdapter;
import com.example.navigationdrawertest.widget.CustomViewPager;

/**
 * 创建时间：2016/10/9
 * 创建者：Young
 * 功能描述：1.显示照片详图;2.修改照片名字
 * 其他：
 */
public class PhotoActivity extends BaseActivity {

    private CustomViewPager mViewPager;
    private PhotoPagerAdapter mAdapter;
    private List<String> mPhotos;
    private int mCurrentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_photo);	
    	mPhotos = getIntent().getStringArrayListExtra("photos");
    	mCurrentPosition = getIntent().getIntExtra("position", 0);
    	initUI();
    	initData();
    }
    
    private void initUI(){
    	mViewPager = ((CustomViewPager) findViewById(R.id.viewPager));
    }
    
    private void initData(){
    	mAdapter = new PhotoPagerAdapter(this, mPhotos);
    	mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
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
