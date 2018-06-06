package com.example.navigationdrawertest.login;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.navigationdrawertest.activity.LoginActivity;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.internet.HttpClientHelper;
import com.example.navigationdrawertest.utils.SharedPrefsUtil;

public class  Login extends Thread {

	private Activity activity ;
	private Handler handler;
	private String password;
	private String userName;
	private String seritycode;
	public Login(Activity activity , Handler handler,String username, String password)
	{
		this.activity = activity;
		this.handler = handler;		
		this.userName = username;
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try{			
			String seriNumber = PadInfoHelp.getSeriNumber();
			seritycode  = PasswordUtil.generatePassword(seriNumber);
			
			HttpClient mClient  = HttpClientHelper.getOrientHttpClient();
//			String url = "http://"+com.example.navigationdrawertest.application.OrientApplication.getApplication().setting.IPAdress+":"+String.valueOf(OrientApplication.getApplication().setting.port)+"/OrientEDM/datasync/sync.rdm?operationType=login&username="
//								+this.userName+"&password="+this.password+"&code="+seritycode+"seriNum="+seriNumber;
			String url = "http://"+com.example.navigationdrawertest.application.OrientApplication.getApplication().setting.IPAdress+":"+String.valueOf(OrientApplication.getApplication().setting.PortAdress)+"/dp/datasync/sync.do?operationType=login&username="
					+this.userName+"&password="+this.password;
			url = url.replace(" ","");
			HttpGet httpMethod = new HttpGet(url);
			if(activity instanceof LoginActivity)
			{
				SharedPrefsUtil.putValue(this.activity, "username", this.userName);
				LoginActivity loginactivity = (LoginActivity)activity;
				mClient.execute(httpMethod,loginactivity.loginResponseHandle);
			}
		}catch(IOException e)//服务端关闭或者无信号造成无反馈相应
		{
			Log.e("ERROR", e.toString());	
			Message msg = handler.obtainMessage();
			 Bundle bundle = new Bundle();
			 bundle.putString("RESPONSE", "Failure");
			 bundle.putString("REASON", "网络异常!");
			 OrientApplication.getApplication().bConnenct = false;
			 msg.setData(bundle);
			 handler.sendMessage(msg);
		}					
	}
}
