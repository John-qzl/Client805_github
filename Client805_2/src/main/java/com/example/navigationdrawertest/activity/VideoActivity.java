package com.example.navigationdrawertest.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.adapter.PhotoPagerAdapter;
import com.example.navigationdrawertest.adapter.VideoPagerAdapter;
import com.example.navigationdrawertest.widget.CustomViewPager;

import java.util.List;

/**
 * Created by qiaozhili on 2020/2/20 17:17.
 */

public class VideoActivity extends BaseActivity {
    private CustomViewPager mViewPager;
    private VideoPagerAdapter mAdapter;
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
        mAdapter = new VideoPagerAdapter(this, mPhotos);
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
