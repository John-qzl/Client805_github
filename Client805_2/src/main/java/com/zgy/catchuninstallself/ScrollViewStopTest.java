package com.zgy.catchuninstallself;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.navigationdrawertest.CustomUI.MyScrollView;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.activity.BaseActivity;
import com.example.navigationdrawertest.utils.ActivityCollector;

/**
 * Created by john on 2017/11/21.
 */

public class ScrollViewStopTest extends BaseActivity implements MyScrollView.OnScrollListener{
    private MyScrollView myScrollView;
    private LinearLayout mBuyLayout;
    private WindowManager mWindowManager;
    /**
     * 手机屏幕宽度
     */
    private int screenWidth;
    /**
     * 悬浮框View
     */
    private static View suspendView;
    /**
     * 悬浮框的参数
     */
    private static WindowManager.LayoutParams suspendLayoutParams;
    /**
     * 购买布局的高度
     */
    private int buyLayoutHeight;
    /**
     * myScrollView与其父类布局的顶部距离
     */
    private int myScrollViewTop;

    /**
     * 购买布局与其父类布局的顶部距离
     */
    private int buyLayoutTop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_scroll_stop);

        myScrollView = (MyScrollView) findViewById(R.id.scrollView);
        mBuyLayout = (LinearLayout) findViewById(R.id.buy);

        myScrollView.setOnScrollListener(this);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
    }

    /**
     * 窗口有焦点的时候，即所有的布局绘制完毕的时候，我们来获取购买布局的高度和myScrollView距离父类布局的顶部位置
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            buyLayoutHeight = mBuyLayout.getHeight();
            buyLayoutTop = mBuyLayout.getTop();

            myScrollViewTop = myScrollView.getTop();
        }
    }



    /**
     * 滚动的回调方法，当滚动的Y距离大于或者等于 购买布局距离父类布局顶部的位置，就显示购买的悬浮框
     * 当滚动的Y的距离小于 购买布局距离父类布局顶部的位置加上购买布局的高度就移除购买的悬浮框
     *
     */
    @Override
    public void onScroll(int scrollY) {
        if(scrollY >= buyLayoutTop){
            if(suspendView == null){
                showSuspend();
            }
        }else if(scrollY <= buyLayoutTop + buyLayoutHeight){
            if(suspendView != null){
                removeSuspend();
            }
        }
    }


    /**
     * 显示购买的悬浮框
     */
    private void showSuspend(){
        if(suspendView == null){
            suspendView = LayoutInflater.from(this).inflate(R.layout.table_head_stop, null);
            if(suspendLayoutParams == null){
                suspendLayoutParams = new WindowManager.LayoutParams();
                suspendLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE; //悬浮窗的类型，一般设为2002，表示在所有应用程序之上，但在状态栏之下
                suspendLayoutParams.format = PixelFormat.RGBA_8888;
                suspendLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;  //悬浮窗的行为，比如说不可聚焦，非模态对话框等等
                suspendLayoutParams.gravity = Gravity.TOP;  //悬浮窗的对齐方式
                suspendLayoutParams.width = screenWidth;
                suspendLayoutParams.height = buyLayoutHeight;
                suspendLayoutParams.x = 0;  //悬浮窗X的位置
                suspendLayoutParams.y = myScrollViewTop;  ////悬浮窗Y的位置
            }
        }

        mWindowManager.addView(suspendView, suspendLayoutParams);
    }


    /**
     * 移除购买的悬浮框
     */
    private void removeSuspend(){
        if(suspendView != null){
            mWindowManager.removeView(suspendView);
            suspendView = null;
        }
    }

}
