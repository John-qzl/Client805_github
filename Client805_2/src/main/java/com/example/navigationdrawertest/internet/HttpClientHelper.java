package com.example.navigationdrawertest.internet;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.example.navigationdrawertest.application.OrientApplication;


public class HttpClientHelper {

	public static HttpClient getOrientHttpClient()
	{
		try {
	    	HttpParams params = new BasicHttpParams();
			ConnManagerParams.setMaxTotalConnections(params, 10);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			
			int timeoutConnection = 300000;
			HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 500000;
			HttpConnectionParams.setSoTimeout(params, timeoutSocket);
			
			// Create and initialize scheme registry
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), OrientApplication.getApplication().setting.port));
			schemeRegistry.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), OrientApplication.getApplication().setting.sport));
			
			// Create an HttpClient with the ThreadSafeClientConnManager.
			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
					params, schemeRegistry);
			
			DefaultHttpClient mClient = new DefaultHttpClient(cm,params);
			
		
			return mClient;
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
	public static String  getURL()
	{
		String url = "http://"+OrientApplication.getApplication().setting.IPAdress+":"
				+OrientApplication.getApplication().setting.PortAdress+"/dp/datasync/sync.do";
		return url;
	}
}