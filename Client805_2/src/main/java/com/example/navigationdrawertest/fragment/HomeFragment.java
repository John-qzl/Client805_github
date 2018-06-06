package com.example.navigationdrawertest.fragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;

import com.cssrc.astuetz.PagerSlidingTabStrip;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.model.RwRelation;

@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment {
	private ViewPager pager;
	private View view;
	private LayoutInflater inflater;
	private RwRelation proEntity;
	
	private FragmentCheck fragmentcheck;
	private FragmentSign fragmentsign;
	private FragmentUnupload fragmentunupload;
	private FragmentUpload fragmentupload;
	private FragmentAll fragmentall;
	private MyPagerAdapter myAdapter;				//适配器定义
	
	private Handler handler;

	/**
	 * PagerSlidingTabStrip的实例
	 */
	private PagerSlidingTabStrip tabs;

	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;
	
	public HomeFragment(RwRelation proEntity){
		this.proEntity = proEntity;
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
        View rootView = inflater.inflate(R.layout.item_experiment, container, false);
        setOverflowShowingAlways();
		dm = getResources().getDisplayMetrics();
		pager = (ViewPager) rootView.findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
//		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
//		pager.setAdapter(new MyPagerAdapter(getFragmentManager()));
		myAdapter = new MyPagerAdapter(getChildFragmentManager());
		pager.setAdapter(myAdapter);
		tabs.setViewPager(pager);
		setTabsValue();
        return rootView;
    }
	
	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 2, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#45c01a"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {
	    
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = { "待检查", "待签署", "待上传", "已上传", "全部" };


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
				if (fragmentcheck == null) {
					fragmentcheck = new FragmentCheck(proEntity);
				}
				return fragmentcheck;
			case 1:
				if (fragmentsign == null) {
					fragmentsign = new FragmentSign(proEntity);
				}
				return fragmentsign;
			case 2:
				if (fragmentunupload == null) {
					fragmentunupload = new FragmentUnupload(proEntity);
				}
				return fragmentunupload;
			case 3:
				if (fragmentupload == null) {
					fragmentupload = new FragmentUpload(proEntity);
				}
				return fragmentupload;
			case 4:
				if (fragmentall == null) {
					fragmentall = new FragmentAll(proEntity);
				}
				return fragmentall;
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
