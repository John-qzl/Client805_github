package com.example.navigationdrawertest.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.navigationdrawertest.MainActivity;
import com.example.navigationdrawertest.R;
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

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2018/1/16.
 */

public class MainActivity1 extends FragmentActivity implements OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ImageView leftMenu, mSyn, mMedia, mQuit;
    private TextView mtitle;
    private ListView mDrawerList;
    private NavDrawerListAdapter mAdapter;
    private List<RwRelation> projectList;
    private List<NavDrawerItem> mNavDrawerItems;
    private TypedArray mNavMenuIconsTypeArray;
    private static int localPosition = 0;

    public static void actionStart1(Context context) {
        Intent intent = new Intent(context, MainActivity1.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        getActionBar().hide();
        ActivityCollector.addActivity(this);
        initUserInformation();
        initview();
        localPosition = 0;
        selectItem(localPosition);
        mtitle.setText(R.string.app_name);
    }

    @SuppressLint("NewApi")
    private void initview() {
        leftMenu = (ImageView) findViewById(R.id.leftmenu);
        mtitle = (TextView) findViewById(R.id.title);
        mSyn = (ImageView) findViewById(R.id.data_syn);
        mMedia = (ImageView) findViewById(R.id.media);
        mQuit = (ImageView) findViewById(R.id.quit);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerList = (ListView) findViewById(R.id.left_listview);
        leftMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        mQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity1.this, LoginActivity.class);
                startActivity(intent1);
            }
        });
        mMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity1.this, DocActivity.class);
                startActivity(intent);
            }
        });
        mSyn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetCheckTool.check(MainActivity1.this)){
                    ProgressDialog prodlg = ProgressDialog.show(MainActivity1.this, "同步", "正在同步数据");
                    prodlg.setIcon(MainActivity1.this.getResources().getDrawable(R.drawable.logo_title));
                    SyncWorkThread syncThread = new SyncWorkThread(MainActivity1.this, handler);
                    syncThread.start();
                }else{
                    Toast.makeText(MainActivity1.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
//            setTitle(projectList.get(position).getRwname());
            mtitle.setText(projectList.get(position).getRwname());
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

//    @SuppressLint("NewApi")
//    @Override
//    public void setTitle(CharSequence title) {
//        mTitle = title;
//        getActionBar().setTitle(mTitle);
//    }

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
//        Resources res = this.getResources();
//        builder.setIcon(res.getDrawable(R.drawable.logo_title)).setTitle(res.getString(R.string.listsyncmessage));
        LayoutInflater li = LayoutInflater.from(this);
        View v = li.inflate(R.layout.listsyncmessage, null);
        ListView listsyncmessage = (ListView) v.findViewById(R.id.listsyncmessage);
        listsyncmessage.setAdapter(new MainActivity1.ListSyncAdapter(this, (ArrayList<String>) OrientApplication.getApplication().uploadDownloadList));

        builder.setView(v)
                .setPositiveButton("请点击进入",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity1.actionStart1(MainActivity1.this);
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
            return new MainActivity1.ListSyncView(context, tableName);
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ActivityCollector.finishAll();
    }
}
