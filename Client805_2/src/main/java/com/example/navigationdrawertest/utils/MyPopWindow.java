package com.example.navigationdrawertest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.activity.LoginActivity;
import com.example.navigationdrawertest.activity.MainActivity1;

/**
 * Created by user on 2020/4/16.
 */

public class MyPopWindow extends PopupWindow implements View.OnClickListener{
    private LinearLayout pop_quit;

    public MyPopWindow(Activity context){
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content=inflater.inflate(R.layout.popupwindow_quit,null);
        initview(content);
        this.setContentView(content);
        this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();//刷新状态
        ColorDrawable dw=new ColorDrawable(0000000000);//半透明
        this.setBackgroundDrawable(dw);// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setAnimationStyle(R.style.AnimationPreview); // 设置SelectPicPopupWindow弹出窗体动画效果
        pop_quit.setOnClickListener(this);
    }

    private void initview(View v) {
        pop_quit = (LinearLayout) v.findViewById(R.id.pop_quit);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pop_quit:
//                Toast.makeText(getContentView().getContext(),"退出", Toast.LENGTH_SHORT).show();
                MyPopWindow.this.dismiss();
                Intent intent1 = new Intent(getContentView().getContext(), LoginActivity.class);
                getContentView().getContext().startActivity(intent1);
                break;
        }
    }

    //显示popupWindow
    public void showPopupWindow(View v){
        if(!this.isShowing()){
            this.showAsDropDown(v,0,0);
        }else{
            this.dismiss();
        }
    }
}
