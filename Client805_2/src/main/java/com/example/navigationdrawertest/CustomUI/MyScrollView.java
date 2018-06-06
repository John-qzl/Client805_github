package com.example.navigationdrawertest.CustomUI;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * @blog http://blog.csdn.net/xiaanming
 * 
 * @author xiaanming
 * 
 */
public class MyScrollView extends ScrollView {
	private OnScrollListener onScrollListener;

	private int lastScrollY;

	public MyScrollView(Context context) {
		this(context, null);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 设置滚动接口
	 * 
	 * @param onScrollListener
	 */
	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	/**
	 * 用于用户手指离开MyScrollView的时候获取MyScrollView滚动的Y距离，然后回调给onScroll方法中
	 */
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			int scrollY = MyScrollView.this.getScrollY();

			//此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
			if(lastScrollY != scrollY){
				lastScrollY = scrollY;
				handler.sendMessageDelayed(handler.obtainMessage(), 5);
			}
			if(onScrollListener != null){
				onScrollListener.onScroll(scrollY);
			}

		};

	};

	/**
	 * 重写onTouchEvent， 当用户的手在MyScrollView上面的时候，
	 * 直接将MyScrollView滑动的Y方向距离回调给onScroll方法中，当用户抬起手的时候，
	 * MyScrollView可能还在滑动，所以当用户抬起手我们隔5毫秒给handler发送消息，在handler处理
	 * MyScrollView滑动的距离
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(onScrollListener != null){
			onScrollListener.onScroll(lastScrollY = this.getScrollY());
		}
		switch(ev.getAction()){
			case MotionEvent.ACTION_UP:
				handler.sendMessageDelayed(handler.obtainMessage(), 5);
				break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (onScrollListener != null) {
			onScrollListener.onScroll(t);
		}
	}

	/**
	 * 
	 * �����Ļص��ӿ�
	 * 
	 * @author xiaanming
	 * 
	 */
	public interface OnScrollListener {
		/**
		 * �ص������� ����MyScrollView������Y�������
		 * 
		 * @param scrollY
		 *            ��
		 */
		public void onScroll(int scrollY);
	}

}
