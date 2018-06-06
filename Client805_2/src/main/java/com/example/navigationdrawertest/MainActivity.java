package com.example.navigationdrawertest;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationdrawertest.activity.DocActivity;
import com.example.navigationdrawertest.activity.LoginActivity;
import com.example.navigationdrawertest.adapter.NavDrawerListAdapter;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.entity.NavDrawerItem;
import com.example.navigationdrawertest.fragment.HomeFragment;
import com.example.navigationdrawertest.internet.SyncWorkThread;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.model.User;
import com.example.navigationdrawertest.utils.ActivityCollector;
import com.example.navigationdrawertest.utils.FontSize;
import com.example.navigationdrawertest.utils.ListStyle;
import com.example.navigationdrawertest.utils.NetCheckTool;

public class MainActivity extends FragmentActivity implements OnItemClickListener {

	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private String[] mNavMenuTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private List<NavDrawerItem> mNavDrawerItems;
	private TypedArray mNavMenuIconsTypeArray;
	private NavDrawerListAdapter mAdapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private List<RwRelation> projectList;
	private static int localPosition = 0;
	
	public static void actionStart(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
		
	}
	
	public static void actionStart1(Context context, int position){
		Intent intent = new Intent(context, MainActivity.class);
		localPosition = position;
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.root_main);
		ActivityCollector.addActivity(this);
		findView();
		initUserInformation();
		localPosition = 0;
		selectItem(localPosition);


	}
	
	@SuppressLint("NewApi")
	private void findView() {
		
		mTitle = mDrawerTitle = getTitle();
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        mNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        // nav drawer icons from resources
     	mNavMenuIconsTypeArray = getResources()
     				.obtainTypedArray(R.array.nav_drawer_icons);
     		
        mNavDrawerItems = new ArrayList<NavDrawerItem>();
        
        //左侧项目树展现方法2，根据RwRelation表格展现----数据表采用
        List<RwRelation> proList = DataSupport.where("userid = ?", 
        		OrientApplication.getApplication().loginUser.getUserid()).find(RwRelation.class);
        projectList = proList;
        for(int i=0; i<proList.size(); i++){
        	mNavDrawerItems.add(new NavDrawerItem(proList.get(i).getRwname(), mNavMenuIconsTypeArray
    				.getResourceId(0, -1)));
        }

		// Recycle the typed array
		mNavMenuIconsTypeArray.recycle();
        
		// setting the nav drawer list adapter
		mAdapter = new NavDrawerListAdapter(getApplicationContext(),
						mNavDrawerItems);
		mDrawerList.setAdapter(mAdapter);
		mDrawerList.setOnItemClickListener(this);
		
		
		// enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		
		// ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.menu_new,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_refresh:
        	if(NetCheckTool.check(this)){
        		ProgressDialog prodlg = ProgressDialog.show(this, "同步", "正在同步数据");
        		prodlg.setIcon(this.getResources().getDrawable(R.drawable.logo_title));
        		SyncWorkThread syncThread = new SyncWorkThread(this, handler);
        		syncThread.start();
        	}else{
        		Toast.makeText(this, "网络连接异常", Toast.LENGTH_SHORT).show();
        	}
        	return true;
        case R.id.action_album:
        	Intent intent = new Intent(this, DocActivity.class);
        	startActivity(intent);
        	return true;
        case R.id.action_exit:
        	Intent intent1 = new Intent(this, LoginActivity.class);
    	    startActivity(intent1);
        	return true;
			default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		selectItem(position);
		
		
	}
	
	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void selectItem(final int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		if(projectList.size() > 0){
			RwRelation proEntity = projectList.get(position);
			if(proEntity != null){
				OrientApplication.getApplication().rw = proEntity;
				Log.i("项目名称", proEntity.getRwname());
			}
			localPosition = position;
			fragment = new HomeFragment(proEntity);
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(projectList.get(position).getRwname());
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
	
	public void refresh(int position){
		finish();
		Intent intent = new Intent(this, MainActivity.class);
	    Bundle bl=new Bundle();
	    bl.putInt("position", position);
	    intent.putExtras(bl);
	    startActivity(intent);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.finishAll();
	}
	
	protected void initUserInformation(){
		User user = DataSupport.where("userid = ?", OrientApplication.getApplication().loginUser.getUserid()).find(User.class).get(0);
		OrientApplication.getApplication().loginUser = user;
	}
	
	// 根据消息更新界面
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			// 响应读取本地文件或者同步结束
			String readResult = (String) bundle.get("localread");
			if (readResult != null && readResult.equalsIgnoreCase("Failure"))	// 读本地出错
			{
				String reason = (String) bundle.get("REASON");
			} else if (readResult != null
					&& readResult.equalsIgnoreCase("ERROR"))// 同步出错
			{
				// 错误处理
				String errorTitle = (String) bundle.get("ERRORTITLE");
				if (errorTitle != null && !errorTitle.isEmpty()) {
					String errorInfo = (String) bundle.get("ERRORINFORMATION");
				}
			} else if (readResult != null && readResult.equalsIgnoreCase("oksync"))	// 同步成功
			{
				getSyncInformation();
			} else if (readResult != null && readResult.equalsIgnoreCase("OK"))// 读本地成功
			{
			}
			return;
		}
	};
	
	public void getSyncInformation() {
		Builder builder = new AlertDialog.Builder(this);
//		Resources res = this.getResources();
//		builder.setIcon(res.getDrawable(R.drawable.logo_title)).setTitle(res.getString(R.string.listsyncmessage));
		LayoutInflater li = LayoutInflater.from(this);
		View v = li.inflate(R.layout.listsyncmessage, null);
		ListView listsyncmessage = (ListView) v.findViewById(R.id.listsyncmessage);
		listsyncmessage.setAdapter(new ListSyncAdapter(this, (ArrayList<String>) OrientApplication.getApplication().uploadDownloadList));

		builder.setView(v)
				.setPositiveButton("请点击进入",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								MainActivity.actionStart(MainActivity.this);
								dialog.cancel();
							}
						}).setCancelable(false).show();
	}
	
	public class ListSyncAdapter extends BaseAdapter {
		private final Context context;
		private final ArrayList<String> uploadDownloadList;

		public ListSyncAdapter(Context context,
				ArrayList<String> uploadDownloadList) {
			this.context = context;
			this.uploadDownloadList = uploadDownloadList;
		}

		@Override
		public int getCount() {
			return uploadDownloadList.size();
		}

		@Override
		public Object getItem(int position) {
			return uploadDownloadList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String tableName = uploadDownloadList.get(position);
			return new ListSyncView(context, tableName);
		}

	}
	
	public class ListSyncView extends LinearLayout {
		private final Context context;
		private final String tableName;
		private TextView taskName;

		public ListSyncView(Context context, String tableName) {
			super(context);
			this.context = context;
			this.tableName = tableName;
			// 文字
			LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
					ListStyle.listTaskNameWidth + 500,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			textParams.setMargins(5, 0, 5, 0);
			textParams.gravity = Gravity.CENTER;
			taskName = new TextView(this.context);
			taskName.setTextColor(Color.BLACK);
			taskName.setText(this.tableName);
			if (tableName.equals("无")) {
				taskName.setTextColor(Color.RED);
			}
			taskName.setTextSize(FontSize.listMidleSize1);
			taskName.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			this.addView(taskName, textParams);
		}
	}
	
	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){      
			return  true;
		}  
		return super.onKeyDown(keyCode, event);     
	}
	
	
}
