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
import android.os.Environment;
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
import com.example.navigationdrawertest.application.MyApplication;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.entity.NavDrawerItem;
import com.example.navigationdrawertest.fragment.HomeFragment;
import com.example.navigationdrawertest.internet.HttpClientHelper;
import com.example.navigationdrawertest.internet.SyncWorkThread;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Diagram;
import com.example.navigationdrawertest.model.Mmc;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Post;
import com.example.navigationdrawertest.model.Product;
import com.example.navigationdrawertest.model.Rw;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.model.Scene;
import com.example.navigationdrawertest.model.Signature;
import com.example.navigationdrawertest.model.Task;
import com.example.navigationdrawertest.model.UploadFileRecord;
import com.example.navigationdrawertest.model.User;
import com.example.navigationdrawertest.utils.ActivityCollector;
import com.example.navigationdrawertest.utils.CommonTools;
import com.example.navigationdrawertest.utils.CommonUtil;
import com.example.navigationdrawertest.utils.Config;
import com.example.navigationdrawertest.utils.ConverXML;
import com.example.navigationdrawertest.utils.FileOperation;
import com.example.navigationdrawertest.utils.FontSize;
import com.example.navigationdrawertest.utils.ListStyle;
import com.example.navigationdrawertest.utils.NetCheckTool;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2018/1/16.
 */

public class MainActivity1 extends FragmentActivity implements OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ImageView leftMenu, mDoneSync, mSyn, mMedia, mQuit;
    private TextView mtitle;
    private ListView mDrawerList;
    private NavDrawerListAdapter mAdapter;
    private List<RwRelation> projectList;
    private List<NavDrawerItem> mNavDrawerItems;
    private TypedArray mNavMenuIconsTypeArray;
    private static int localPosition = 0;
    private AlertDialog.Builder dialog;

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
        mDoneSync = (ImageView) findViewById(R.id.done_data_syn);
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
        mDoneSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetCheckTool.check(MainActivity1.this)) {
                    dialog = new AlertDialog.Builder(MainActivity1.this);
                    dialog.setIcon(R.drawable.logo_title).setTitle(R.string.app_name);
                    dialog.setMessage("是否要进行已完成状态的数据同步？（注：此操作仅限管理员！）");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            OrientApplication.getApplication().setFlag(1);
                            ProgressDialog prodlg = ProgressDialog.show(MainActivity1.this, "同步", "正在同步数据");
                            prodlg.setIcon(MainActivity1.this.getResources().getDrawable(R.drawable.logo_title));
                            SyncWorkThread syncThread = new SyncWorkThread(MainActivity1.this, handler);
                            syncThread.start();
//                            uploadDoneTable();
//                            prodlg.dismiss();
                        }
                    });
                    dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
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
    public void selectItem(final int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        OrientApplication.getApplication().setCommander(false);
        if(projectList.size() > 0 && projectList != null){
            RwRelation proEntity = projectList.get(position);
            if(proEntity != null){
                OrientApplication.getApplication().rw = proEntity;
                Log.i("项目名称", proEntity.getRwname());
            }
            localPosition = position;
            //判断用户是否为节点负责人
//            String nodeIds = proEntity.getNodeId();
//            if (nodeIds != null) {
//                isCommander(nodeIds);
//            }
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
        OrientApplication.getApplication().setCommander(false);
        ActivityCollector.finishAll();
    }

    // 管理员操作，上传已完成表单实例及其相关的检查项照片数据
    private boolean uploadDoneTable() {
        String userid = OrientApplication.getApplication().loginUser
                .getUserid();
        String location = "4";
        try {
            // 确定表格实例三要素：人员-任务-位置
            List<Task> taskList = DataSupport.where("location = ?", location).find(Task.class);
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                HttpClient client = HttpClientHelper.getOrientHttpClient();
                HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
                // 上传三步骤
                // 1，上传表格实例数据
                // 2，上传表格实例中存在的照片
                // 3，上传表格实例生成的HTML网页
                // 上传步骤一
                if (task == null) {
                    return false;
                }
                String taskxml = ConverXML.ConverTaskToXml(task);

                // 上传步骤三
                String taskHtml = CommonUtil.ConverHtmlToString(task);
                if ("".equals(taskHtml)) {
                    return false;
                }
                // 上传步骤四
                if(!uploadSignPhoto(task.getTaskid())){
                    Log.i("photo", task.getTaskid()+"签署照片");
                    return false;
                }
                // 上传步骤二
                if (!uploadOpPhoto(task)) // 先执行上传相片
                {
                    Log.i("photo", task.getTaskid()+"检查项照片");
                }

                String script = "<script type=\"text/javascript\">	function showImage(imageFile,type){ 		if(window.showImageObj==undefined || window.showImageObj==null)		{			window.browser(imageFile,type);		}		else		{			window.showImageObj.clickOnAndroid(imageFile,type);		} 	}</script>";
                String html = taskHtml.contains("<body>") ? (script + taskHtml
                        .split("<body>")[1].split("</body>")[0]) : taskHtml;
                Log.i("html", task.getTaskname() + "OK");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        3);
                nameValuePairs.add(new BasicNameValuePair("operationType",
                        "uploadtask")); // 上传表格实例的内容和表格实例的HTML
                nameValuePairs.add(new BasicNameValuePair("tableInstanId", task
                        .getTaskid()));
                nameValuePairs.add(new BasicNameValuePair("taskContent",
                        taskxml));
                nameValuePairs.add(new BasicNameValuePair("htmlContent", html));
                postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                        "utf-8"));
                Log.i("setEntity", task.getTaskname() + "OK");
                postmethod.setHeader("Content-Type",
                        "application/x-www-form-urlencoded; charset=utf-8");
                Log.i("setHeader", task.getTaskname() + "OK");
                HttpResponse response;
                response = client.execute(postmethod);
                Log.i("execute", task.getTaskname() + "OK");
                int code = response.getStatusLine().getStatusCode();
                Log.i("response", task.getTaskname() + code + "OK");
                if (code != 200) {
                    return false; // 错误
                }


                String uploadResponseContent = EntityUtils.toString(response.getEntity(), "utf-8");
                if(uploadResponseContent.equals("true")){
                    task.setLocation(4);
                    task.save();
                    Log.i("location", task.getTaskname() + "上传成功！");
                }else{
                }

                // 表格实例上传成功之后的操作
                // 1，把该表格的location设置为4

                // String taskid = task.getTaskid();
                // if(task.save()){

                // DataSupport.deleteAll(Signature.class, "taskid = ?",
                // task.getTaskid());
                // DataSupport.deleteAll(Cell.class, "taskid = ?",
                // task.getTaskid());
                // DataSupport.deleteAll(Operation.class, "taskid = ?",
                // task.getTaskid());
                // DataSupport.deleteAll(Task.class, "taskid = ?",
                // task.getTaskid());
                // // DataSupport.deleteAll(Rw.class, "tableinstanceid = ?",
                // task.getTaskid());
                // // DataSupport.deleteAll(Signature.class, "taskid = ?",
                // taskid);
                // // DataSupport.deleteAll(Cell.class, "taskid = ?", taskid);
                // // DataSupport.deleteAll(Operation.class, "taskid = ?",
                // taskid);
                // // DataSupport.deleteAll(Task.class, "taskid = ?", taskid);

                // 删除形式
                // String deleteint = task.getTaskid();
                // DataSupport.deleteAll(Task.class,
                // "taskid = ? and userid = ?", deleteint,
                // OrientApplication.getApplication().loginUser.getUserid());
                // DataSupport.deleteAll(Signature.class,
                // "taskid = ? and userid = ?", deleteint,
                // OrientApplication.getApplication().loginUser.getUserid());
                // DataSupport.deleteAll(Cell.class, "taskid = ?", deleteint);
                // DataSupport.deleteAll(Operation.class,
                // "taskid = ? and userid = ?", deleteint,
                // OrientApplication.getApplication().loginUser.getUserid());
                // DataSupport.deleteAll(Rw.class, "tableinstanceid = ?",
                // deleteint);
                // }
            }
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return false;
        } catch (ClientProtocolException e) {
            Log.i("ClientProtocolException", e.toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.i("IOException", e.toString());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            Log.i("Exception", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 上传表格签署照片
     *
     * @param tableId
     *            表格ID
     * @return
     */
    public boolean uploadSignPhoto(String tableId) {
        // 1,找到该任务照片目录
        String userId = OrientApplication.getApplication().loginUser
                .getUserid();
        String signphotoPath = Environment.getDataDirectory().getPath()
                + Config.packagePath + Config.signphotoPath
                + "/" + tableId + "/";
        String photoName = "";
        List<String> photoNameList = SyncWorkThread.GetFiles(signphotoPath, "jpg");
        String username = OrientApplication.getApplication().loginUser
                .getUsername();
        String userid = OrientApplication.getApplication().loginUser
                .getUserid();
        if (photoNameList.size() != 0) {

            try {
                // 表格级别照片命名规则
                for (int i = 0; i < photoNameList.size(); i++) {
                    String phName = photoNameList.get(i);
                    int startLocation = phName.lastIndexOf("/") + 1;
                    int endLocation = phName.lastIndexOf(".");
                    String resultId = phName.substring(startLocation,
                            endLocation);
                    String photopath = phName;
                    HttpClient client = HttpClientHelper.getOrientHttpClient();
                    String str = "http://"
                            + OrientApplication.getApplication().setting.IPAdress
                            + ":"
                            + OrientApplication.getApplication().setting.PortAdress
                            + "/dp/datasync/sync.do?operationType=uploadsignphoto&username="
                            + username + "&userid=" + userid + "&resultId="
                            + resultId + "&tableInstanId=" + tableId;
                    HttpPost postmethod = new HttpPost(str);

                    File file = new File(photopath);
                    MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
                    ContentBody cbFile = new FileBody(file);
                    mpEntity.addPart("userfile", cbFile); // <input type="file"
                    // name="userfile"
                    // /> 对应的
                    postmethod.setEntity(mpEntity);

                    HttpResponse response = client.execute(postmethod);

                    int code = response.getStatusLine().getStatusCode();
                    if (code != 200) {
                        return false;
                    }
                    postmethod = null;
                }

            } catch (UnsupportedEncodingException e) {
                return false;
            } catch (ClientProtocolException e) {
                return false;
            } catch (IOException e) {
                return false;
            } catch (Exception e) {
                return false;
            }

        }
        return true;
    }

    /**
     * 上传操作项级别照片
     *
     * @param task
     * @return
     */
    private boolean uploadOpPhoto(Task task) {
        // 1,找到该表格实例照片目录

        String path = Environment.getExternalStorageDirectory() + Config.v2photoPath
                + File.separator
                + task.getRwid()
                + File.separator
                + task.getTaskid()
                + File.separator;

        List<String> presultList = new ArrayList<String>();
        FileOperation.findFiles(path, ".jpg", presultList);
        String username = OrientApplication.getApplication().loginUser
                .getUsername();
        String userid = OrientApplication.getApplication().loginUser
                .getUserid();
        String tableId = task.getTaskid();
        // 2,先传照片
        if (presultList.size() > 0) {
            try {
                // 表格级别照片命名规则
                for (int i = 0; i < presultList.size(); i++) {
                    String photopath = presultList.get(i);
                    HttpClient client = HttpClientHelper.getOrientHttpClient();
                    // HttpPost postmethod = new
                    // HttpPost(HttpClientHelper.getURL());
                    int startLocation = photopath.lastIndexOf("/") + 1;
                    int endLocation = photopath.lastIndexOf(".");
                    String phName = photopath.substring(startLocation,
                            endLocation);

                    //获取opid
                    String newStr = photopath.substring(0, startLocation-2);
                    int start2Location = newStr.lastIndexOf("/")+1;
                    String opId = photopath.substring(start2Location,startLocation-1);

                    //新建一条文件记录
                    UploadFileRecord uploadFile = new UploadFileRecord();
                    uploadFile.setmFileName(phName);
                    uploadFile.setmFilePath(photopath);

                    String str = "http://"
                            + OrientApplication.getApplication().setting.IPAdress
                            + ":"
                            + OrientApplication.getApplication().setting.PortAdress
                            + "/dp/datasync/sync.do?operationType=uploadopphoto&username="
                            + username + "&userid=" + userid + "&photoName="
                            + phName + "&tableInstanId=" + tableId
                            + "&operationId=" + opId;
                    HttpPost postmethod = new HttpPost(str);

                    File file = new File(photopath);
                    MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
                    ContentBody cbFile = new FileBody(file);
                    mpEntity.addPart("userfile", cbFile);
                    // <input type="file"
//					mpEntity.addPart("d",cbFile);										// name="userfile"
                    // /> 对应的
                    postmethod.setEntity(mpEntity);

                    HttpResponse response;
                    response = client.execute(postmethod);
                    int code = response.getStatusLine().getStatusCode();
                    if (code != 200) {
                        uploadFile.setmState("false");
                        uploadFile.save();
                        return false;
                    }else{
                        uploadFile.setmState("true");
                        uploadFile.save();
                    }
                    postmethod = null;
                }
            } catch (UnsupportedEncodingException e) {
                return false;
            } catch (ClientProtocolException e) {
                return false;
            } catch (IOException e) {
                return false;
            } catch (Exception e) {
                return false;
            }

        }
        return true;
    }

    /**
     * @Description: 判断用户是否为该节点的负责人
     * @author qiaozhili
     * @date 2019/2/19 16:44
     * @param
     * @return
     */
//    private void isCommander(String nodeIds) {
//        String userId = OrientApplication.getApplication().loginUser.getUserid();
//        List<User> userList = DataSupport.where("userid=?",userId ).find(User.class);
//        if (!nodeIds.equals("")) {
//            String[] strNodeId = nodeIds.split("\\、");
//            for (int i = 0; i < strNodeId.length; i++) {
//                String nodeID = strNodeId[i];
//                if (userList.size() > 0) {
//                    String commanderId = userList.get(0).getCommanderId();
//                    if (!commanderId.equals("")) {
//                        String[] strCommanderId = commanderId.split("\\,");
//                        for (int j = 0; j < strCommanderId.length; j++) {
//                            if (nodeID.equals(strCommanderId[j])) {
//                                OrientApplication.getApplication().setCommander(true);
//                            }
//                        }
//                    } else {
//                        OrientApplication.getApplication().setCommander(false);
//                    }
//                } else {
//                    OrientApplication.getApplication().setCommander(false);
//                }
//            }
//        } else {
//            OrientApplication.getApplication().setCommander(false);
//        }
//    }
}
