package com.example.navigationdrawertest.CustomUI;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class NoScrollListview extends ListView{
	
	public NoScrollListview(Context context, AttributeSet attrs) {  
        super(context, attrs);  
	}  
  
	/** 
	 * 设置不滚动 
	 */  
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
	{  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                        MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
	}  
	
	//2016-11-7 10:16:03   LYC 新增滑动到顶部固定功能
	
	
	
	
	
	
}
