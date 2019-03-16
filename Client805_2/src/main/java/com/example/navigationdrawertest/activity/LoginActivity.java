package com.example.navigationdrawertest.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.EncodingUtils;
import org.litepal.crud.DataSupport;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationdrawertest.MainActivity;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.CustomUI.CustomDialog;
import com.example.navigationdrawertest.application.MyApplication;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.internet.SyncWorkThread;
import com.example.navigationdrawertest.login.Login;
import com.example.navigationdrawertest.login.PasswordUtil;
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
import com.example.navigationdrawertest.utils.Config;
import com.example.navigationdrawertest.utils.FontSize;
import com.example.navigationdrawertest.utils.ListStyle;
import com.example.navigationdrawertest.utils.NetCheckTool;
import com.example.navigationdrawertest.utils.RegexValidateUtil;
import com.example.navigationdrawertest.utils.SharedPrefsUtil;

public class LoginActivity extends BaseActivity{

	private Button loginBtn, exitBtn;
	private EditText username, password;
	private ProgressDialog prodlg;
	private boolean isClicked = false;
	private ImageButton shezhiBtn, deletedataBtn;
	private Context context;
	private AlertDialog.Builder dialog;
	private TextView mVersion;
	
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	prodlg.dismiss();
            if(msg.what == 1) {
            	dialog = new AlertDialog.Builder(LoginActivity.this);
				dialog.setIcon(R.drawable.logo_title).setTitle(R.string.app_name);
				dialog.setMessage("本地数据库已经清空！");
				dialog.setCancelable(false);
				dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
            }
        }
    };
	
	public final ResponseHandler<String> loginResponseHandle = new ResponseHandler<String>() {
		@Override
		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			HttpEntity entity = response.getEntity();
			String result = null;
			try {
				InputStream is = entity.getContent();
				byte[] buff = new byte[1024];
				int hasread = 0;
				StringBuilder sb = new StringBuilder("");
				while ((hasread = is.read(buff)) > 0) {
					sb.append(new String(buff, 0, hasread));
				}
				result = sb.toString();
				if ("success".equals(result)) {
					Message msg = handler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString("RESPONSE", "OK");
					msg.setData(bundle);
					handler.sendMessage(msg);
				}else if("userIsNotExist".equals(result)){
					Message msg = handler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString("RESPONSE", "userIsNotExist");
					bundle.putString("REASON", "该用户不存在!");
					msg.setData(bundle);
					handler.sendMessage(msg);
				}else {
					Message msg = handler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString("RESPONSE", "Failure");
					bundle.putString("REASON", "用户密码错误!");
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
			} catch (IOException e) {
				Message msg = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString("RESPONSE", "Failure");
				bundle.putString("REASON", "访问异常!!");
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
			return null;
		}
	};
	
	// 根据消息更新界面
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String processTitle = (String) bundle.get("UPDATE PORGRESS");
			if (processTitle != null && !processTitle.isEmpty()) {
				String infor = (String) bundle.get("INFORMATION");
				prodlg.setTitle(processTitle); // title
				prodlg.setMessage(infor); // information
				return;
			}

			// 登录响应
			String response = (String) bundle.get("RESPONSE");
			if (response != null && !response.isEmpty()) {
				if (response.equalsIgnoreCase("Failure")) {
					String reason = (String) bundle.get("REASON");
					alert(reason);
				}else if(response.equalsIgnoreCase("userIsNotExist")){
					String reason = (String) bundle.get("REASON");
					alert(reason);
				}else {
					User user = new User();
					user.setUsername(username.getText().toString());
					user.setPassword(password.getText().toString());
					OrientApplication.getApplication().loginUser = user;
					// 断网登录
					if (OrientApplication.getApplication().bConnenct == false) {
//						readLocalDB();
						alert("断网登录，尚未实现");
					} else {
						startSync();			//同步开始
					}
					// 启动服务（只有登录成功后才会启动该服务）
				}
			}

			// 响应读取本地文件或者同步结束
			String readResult = (String) bundle.get("localread");
			if (readResult != null && readResult.equalsIgnoreCase("Failure"))// 读本地出错
			{
				String reason = (String) bundle.get("REASON");
				alert(reason);
				prodlg.cancel();
			} else if (readResult != null
					&& readResult.equalsIgnoreCase("ERROR"))// 同步出错
			{
				// 错误处理
				String errorTitle = (String) bundle.get("ERRORTITLE");
				if (errorTitle != null && !errorTitle.isEmpty()) {
					String errorInfo = (String) bundle.get("ERRORINFORMATION");
//					errorDialog(errorTitle, errorInfo);
				}
			} else if (readResult != null && readResult.equalsIgnoreCase("oksync"))	// 同步成功
			{
				prodlg.dismiss();
				getSyncInformation();
			} else if (readResult != null && readResult.equalsIgnoreCase("OK"))// 读本地成功
			{
//				startMainTabIntent();
			}
			return;
		}
	};
	
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
	
	public void getSyncInformation() {
		Builder builder = new AlertDialog.Builder(this);
//		Resources res = this.getResources();
//		builder
//				.setIcon(res.getDrawable(R.drawable.logo))
//				.setTitle(res.getString(R.string.listsyncmessage));
		LayoutInflater li = LayoutInflater.from(this);
		View v = li.inflate(R.layout.listsyncmessage, null);
		ListView listsyncmessage = (ListView) v.findViewById(R.id.listsyncmessage);
		listsyncmessage.setAdapter(new ListSyncAdapter(this, (ArrayList<String>) OrientApplication.getApplication().uploadDownloadList));

		builder.setView(v)
				.setPositiveButton("请点击进入",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
//								MainActivity.actionStart(LoginActivity.this);
								Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
					        	startActivity(intent);
								dialog.cancel();
								prodlg.cancel();
							}
						})
				.setCancelable(false)
				.show();

	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_new);

		loginBtn = (Button) findViewById(R.id.loginButton);
		exitBtn = (Button) findViewById(R.id.eixtButton);
		deletedataBtn = (ImageButton) findViewById(R.id.deletedata);
		username = (EditText) findViewById(R.id.username);
		username.setText(SharedPrefsUtil.getValue(this, "username", ""));
		password = (EditText) findViewById(R.id.password);
		shezhiBtn = (ImageButton) findViewById(R.id.shezhi);
		mVersion = (TextView) findViewById(R.id.tv_version);
		context = this;
		mVersion.setText("版本号：v"+packageName(context));
		if (OrientApplication.getApplication().getWarn() != 1) {
			warnInfo();
		}

		if (Build.VERSION.SDK_INT >= 23) {
			int REQUEST_CODE_CONTACT = 101;
			String[] permissions1 = {Manifest.permission.CAMERA,
					Manifest.permission.WRITE_EXTERNAL_STORAGE};
			for (String str : permissions1) {
				if (ActivityCompat.checkSelfPermission(getApplicationContext(), str) != PackageManager.PERMISSION_GRANTED) {
					//申请权限
					ActivityCompat.requestPermissions(this, permissions1, REQUEST_CODE_CONTACT);
					return;
				}
			}
		}
		
		// 初始化IP和port
		Config cfg = new Config();
		String ret = cfg.getIpAndPort(this);
		if (!ret.isEmpty()) {
			/*
			 * 设置全局的IP属性
			 */
			SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
    	    String ipcontent = sp.getString("ip", "").toString();
    	    String portcontent = sp.getString("port", "").toString();
    	    if(!ipcontent.equals("")){
    	    	OrientApplication.getApplication().setting.IPAdress = ipcontent;
    	    }else{
    	    	OrientApplication.getApplication().setting.IPAdress = cfg.ip;
    	    }
    	    if(!portcontent.equals("")){
    	    	OrientApplication.getApplication().setting.PortAdress = portcontent;
    	    }else{
    	    	OrientApplication.getApplication().setting.PortAdress = cfg.port;
    	    }
		}
		OrientApplication.getApplication().addActivity(
				String.valueOf(R.layout.login), this);
		OrientApplication.getApplication().setCurrentActivity(this);
		/*
		 * 需要在初始化的时候设置按钮点击事件
		 */
		loginBtn.setOnClickListener(new LoginAction());
		exitBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
//				LoginActivity.this.finish();
				exitApplication();
				
			}
		});
		deletedataBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new AlertDialog.Builder(LoginActivity.this);
				dialog.setIcon(R.drawable.logo_title).setTitle(R.string.app_name);
				dialog.setMessage("是否清空数据库？");
				dialog.setCancelable(false);
				dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						prodlg = ProgressDialog.show(context, "警告", "正在清空数据库，请稍侯...");
						new Thread(new Runnable() {
				            @Override
				            public void run() {
				                try{
				                	Message message = new Message();
				                	DataSupport.deleteAll(Cell.class);
				                	DataSupport.deleteAll(Scene.class);
				                	DataSupport.deleteAll(Operation.class);
				                	DataSupport.deleteAll(Post.class);
				                	DataSupport.deleteAll(Signature.class);
				                	DataSupport.deleteAll(Task.class);
				                	DataSupport.deleteAll(User.class);
				                	DataSupport.deleteAll(Rw.class);
				                	DataSupport.deleteAll(RwRelation.class);
				                	DataSupport.deleteAll(Diagram.class);
				                	DataSupport.deleteAll(Mmc.class);
				                	DataSupport.deleteAll(Product.class);
				                	DataSupport.deleteAll(UploadFileRecord.class);
				                	message.what = 1;
				                	mHandler.sendMessage(message);
				                }catch (Exception e) {
				                    Log.e("clear database",e.toString());
				                }
				            }
				        }).start();
					}
				});
				dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		shezhiBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final CustomDialog dialog = new CustomDialog(context);
				final EditText edit_ip = (EditText) dialog.getEdit_ip();		//方法在CustomDialog中实现
        		final EditText edit_port = (EditText) dialog.getEdit_port();
        		SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        	    String ipcontent = sp.getString("ip", "").toString();
        	    String portcontent = sp.getString("port", "").toString();
        	    if(!ipcontent.equals("")){
        	    	edit_ip.setText(ipcontent);
        	    	OrientApplication.getApplication().setting.IPAdress = ipcontent;
        	    }
        	    if(!portcontent.equals("")){
        	    	edit_port.setText(portcontent);
        	    	OrientApplication.getApplication().setting.PortAdress = portcontent;
        	    }
				dialog.setOnPositiveListener(new OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				//dosomething youself
        				 String ip = edit_ip.getText().toString();
        				 String port = edit_port.getText().toString();
        				 boolean ipok = RegexValidateUtil.isIp(ip);
        				 boolean portok = RegexValidateUtil.isport(port);
        				 if(ipok && portok){
        					 OrientApplication.getApplication().setting.IPAdress = ip;
        					 OrientApplication.getApplication().setting.PortAdress = port;
        					 SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        					 Editor editor = sp.edit();
        					 editor.putString("ip", ip);
        					 editor.putString("port", port);
        					 editor.commit();
        					 dialog.dismiss();
        				 }else{
        					 Toast.makeText(context, "IP或者端口号输入有误", Toast.LENGTH_SHORT).show();
        				 }
        			}
        		});
        		dialog.setOnNegativeListener(new OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				dialog.dismiss();
        			}
        		});
        		dialog.show();
			}
		});
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = this.getMenuInflater();
//		inflater.inflate(R.menu.loginmenu, menu);
//		return true;
//	}

	class LoginAction implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (isClicked) {
				Log.d("DoubleClick", "doubleClicked");
				return;
			}
			isClicked = true;
			login();
		}
	}

	private void login() {
		this.prodlg = ProgressDialog.show(this, "登陆中", "请稍侯...");
		prodlg.setIcon(this.getResources().getDrawable(
				R.drawable.logo_title));
		String userName = username.getText().toString().trim();
		String passwordInput = password.getText().toString().trim();
		if (userName.contains(" ") || passwordInput.contains(" ")) {
			alert("用户名或密码不能包含空格！");
			prodlg.cancel();
		}
		boolean bConnected = NetCheckTool.check(this);// 检测本地网络是否可连接服务
		if (bConnected == true)	// 联网登录
		{
			prodlg.dismiss();
			Login loginthread = new Login(this, handler, userName, passwordInput);
			loginthread.start();
		}else{		//离线登录
			prodlg.dismiss();
			offline(userName, passwordInput);
		}
	}

	public void alert(String error) {
		new AlertDialog.Builder(this).setMessage(error)
				.setIcon(R.drawable.logo_title).setTitle(R.string.app_name)
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						isClicked = false; 
						dialog.cancel();
						prodlg.cancel();
					}
				}).show();
	}

	public void startSync() {
		this.prodlg = ProgressDialog.show(this, "同步", "正在同步数据");
		prodlg.setIcon(this.getResources().getDrawable(R.drawable.logo_title));
		SyncWorkThread syncThread = new SyncWorkThread(this, handler);
		syncThread.start();
	}
	
	public void offline(String username, String password){
		List<User> userList = DataSupport.where("username = ?", username).find(User.class);
		if(userList.size() == 0){	//该用户不存在,警告框
			alert("该用户不存在");
		}else{							//该用户存在
			//验证用户名和密码
			String dataPassword = DataSupport.select("password").where("username = ?", 
					username).find(User.class).get(0).getPassword();
			if(!dataPassword.equals(PasswordUtil.generatePassword(password))){
				alert("密码填写错误！");
			}else{
				User user = new User();
				user.setUsername(username);
				SharedPrefsUtil.putValue(this, "username", username);
				String userId = DataSupport.select("userid").where("username = ?", username).find(User.class).get(0).getUserid();
				user.setUserid(userId);
				OrientApplication.getApplication().loginUser = user;
//				MainActivity.actionStart(LoginActivity.this); 
				Intent intent = new Intent(this, MainActivity1.class);
	        	startActivity(intent);
	        	finish();
			}
		}
	}
	
	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if(keyCode == KeyEvent.KEYCODE_BACK){      
			return  true;
		}  
		return  super.onKeyDown(keyCode, event);     
	} 

	private void exitApplication()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
//		dialog.setTitle("退出？");
		dialog.setIcon(R.drawable.logo_title).setTitle(R.string.app_name);
		dialog.setMessage("是否退出应用？");
		dialog.setCancelable(false);
		dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				LoginActivity.this.finish();
				OrientApplication.getApplication().setWarn(0);
				ActivityCollector.finishAll();
			}
		});
		dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * @Description: 首次打开应用展示提示信息
	 * @author qiaozhili
	 * @date 2019/1/28 9:05
	 * @param
	 * @return
	 */
	private void warnInfo() {
		String file = "warn.txt";
		AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
		dialog.setIcon(R.drawable.logo_title).setTitle("重要提示！");
		dialog.setMessage(loadFromSDFile(file));
		dialog.setCancelable(false);
		dialog.setPositiveButton("时间设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				OrientApplication.getApplication().setWarn(1);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * @Description: 读取SD卡上指定路径文件信息
	 * @author qiaozhili
	 * @date 2019/1/28 9:03
	 * @param
	 * @return
	 */

	private String loadFromSDFile(String fname) {
		fname = "/" + fname;
		String result = null;
		String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "805" + File.separator + "files" + fname;
		try {

			File f = new File(path);
			InputStreamReader isr = new InputStreamReader(new FileInputStream(f), "GB2312");
			BufferedReader br = new BufferedReader(isr);
			String str = "";
			String mimeTypeLine = null ;
			while ((mimeTypeLine = br.readLine()) != null) {
				str = str + mimeTypeLine+"\r\n";
				result = str;
			}
			isr.close();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(LoginActivity.this, "没有找到指定文件", Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	public static String packageName(Context context) {
		PackageManager manager = context.getPackageManager();
		String name = null;
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			name = info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} return name;
	}
}
