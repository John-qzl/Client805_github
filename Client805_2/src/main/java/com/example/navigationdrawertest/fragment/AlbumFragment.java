package com.example.navigationdrawertest.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.cssrc.astuetz.PagerSlidingTabStrip;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.camera1.video.entity.Media;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.utils.VideoInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by qiaozhili on 2020/2/17 15:32.
 */

@SuppressLint("ValidFragment")
public class AlbumFragment extends Fragment {
    private ViewPager album_pager;
    private View view;
    private LayoutInflater inflater;
    private RwRelation proEntity;

    private FragmentPhoto fragmentPhoto;
    private FragmentVideo fragmentVideo;
    private MyPagerAdapter myAdapter;				//适配器定义

    private Handler handler;
    private String path;
    ArrayList<VideoInfo> videoInfos;

    /**
     * PagerSlidingTabStrip的实例
     */
    private PagerSlidingTabStrip album_tabs;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    public AlbumFragment(String path, ArrayList<VideoInfo> videoInfos){
        this.path = path;
        this.videoInfos = videoInfos;
//		refresh();
    }

    public void refresh(){
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        setOverflowShowingAlways();
        dm = getResources().getDisplayMetrics();
        album_pager = (ViewPager) rootView.findViewById(R.id.album_pager);
        album_tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.album_tabs);
//		album_pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
//		album_pager.setAdapter(new MyPagerAdapter(getFragmentManager()));
        myAdapter = new MyPagerAdapter(getChildFragmentManager());
        album_pager.setAdapter(myAdapter);
        album_tabs.setViewPager(album_pager);
        setTabsValue();
        return rootView;
    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        album_tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        album_tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        album_tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        album_tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        album_tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        album_tabs.setIndicatorColor(Color.parseColor("#2990EA"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        album_tabs.setSelectedTextColor(Color.parseColor("#2990EA"));
        // 取消点击Tab时的背景色
        album_tabs.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "图片", "视频" };


        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (fragmentPhoto == null) {
                        fragmentPhoto = new FragmentPhoto(path);
                    }
                    return fragmentPhoto;
                case 1:
                    if (fragmentVideo == null) {
                        fragmentVideo = new FragmentVideo(path, videoInfos);
                    }
                    return fragmentVideo;
                default:
                    return null;
            }
        }

    }

    private void setOverflowShowingAlways() {
        try {
//			ViewConfiguration config = ViewConfiguration.get(this);
            ViewConfiguration config = ViewConfiguration.get(getActivity());
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
