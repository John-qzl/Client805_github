package com.example.navigationdrawertest.internet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.data.AerospaceDB;
import com.example.navigationdrawertest.utils.Config;
import com.example.navigationdrawertest.utils.Setting;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/3/20.
 */

public class UpdataVersionThread extends Thread {
    private String errorMessage = ""; // 错误信息提示
    private AerospaceDB aerospacedb;
    private List<String> updataList = new ArrayList<String>();
    private Context context;
    private Handler handler;
    private Bitmap mSignBitmap;
    private String apkpath = "";
    private String apkVersion = "";

    public UpdataVersionThread(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        aerospacedb = AerospaceDB.getInstance();
        boolean ss = checkUrl();
        if (ss) {
            // 获取版本信息
            versionCheck();
        } else {
            //地址无效
            notifyCompletion("disableUrl");
        }

//        notifyCompletion();
    }

    /**
     * @param
     * @return
     * @Description: 版本检查
     * @author qiaozhili
     * @date 2020/3/20 16:58
     */
    private void versionCheck() {
        String version = String.valueOf(getAppVersionCode(context));
//        Toast.makeText(context, "当前版本号：" + version, Toast.LENGTH_SHORT).show();
        try {
            HttpClient client = HttpClientHelper.getOrientHttpClient();
            HttpPost postmethod = new HttpPost(
                    HttpClientHelper.getURL());
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                    1);
            nameValuePairs.add(new BasicNameValuePair("operationType", "checkversion"));
            nameValuePairs.add(new BasicNameValuePair("version", version));
            postmethod.setEntity(new UrlEncodedFormEntity(
                    nameValuePairs, "utf-8"));
            postmethod.setHeader("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            HttpResponse response = client.execute(postmethod);
            int code = response.getStatusLine().getStatusCode();
            if (code != 200) {
                errorMessage = code + "错误";
//                return false; // 错误
            }
            String versionNew = EntityUtils.toString(response.getEntity(), "utf-8");
            apkVersion = versionNew;
            if (version != "" && versionNew != "") {
                if (Integer.valueOf(versionNew) > Integer.valueOf(version)) {
                    downloadapk(versionNew);
                } else {
                    updataList.add(versionNew + "---无新版本");
                }
            } else {
                updataList.add(versionNew + "---版本信息有误");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 下载最新版本apk
     * @author qiaozhili
     * @date 2020/3/20 16:52
     * @param
     * @return
     */
    private boolean downloadapk(String versionNew) {
        HttpClient client = HttpClientHelper.getOrientHttpClient();
        HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
        errorMessage = "网络有误!";
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("operationType",
                    "downloadapk"));
            postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(postmethod);
            int code = response.getStatusLine().getStatusCode();
            if (code != 200) {
                errorMessage = code + "错误";
                return false;
            }
            String filePath = Environment.getExternalStorageDirectory() + Config.v2photoPath;
            String path = Environment.getExternalStorageDirectory() + Config.v2photoPath
                    + File.separator
//                    + "PAD"
//                    + File.separator
                    + versionNew
                    + ".apk";
            Header header = response.getAllHeaders()[1];
            long filelength = Long.parseLong(header.toString().substring(header.toString().lastIndexOf("=")+1));
            long downlen = 0;

            File file1 = new File(filePath);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            HttpEntity entity = response.getEntity();
            if(entity != null){
                ByteArrayInputStream is = new ByteArrayInputStream(
                        EntityUtils.toByteArray(response.getEntity()));
                File file = new File(path);// 新建一个file文件
                FileOutputStream fos = new FileOutputStream(file); // 对应文件建立输出流
                byte[] buffer = new byte[8*1024]; // 新建缓存 用来存储 从网络读取数据 再写入文件
                int len = 0;
                while ((len = is.read(buffer)) != -1) {// 当没有读到最后的时候
                    downlen += len;
                    if(downlen > filelength){
                        int bytecount = (int) (8192-(downlen-filelength));
                        fos.write(buffer, 0, bytecount);
                        break;
                    }else{
                        fos.write(buffer, 0, len);// 将缓存中的存储的文件流秀娥问file文件
                    }
                }
                fos.flush();// 将缓存中的写入file
                fos.close();
            }
            updateInformation(path);
            apkpath = path;
            notifyCompletion("okupdata");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 返回当前程序版本号
     */
    public static int getAppVersionCode(Context context) {
        int versioncode = 0;
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }

    public void notifyCompletion(String status) {
        OrientApplication.getApplication().updataInfoList = updataList;
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("localread", status);
        bundle.putString("apkpath", apkpath);
        bundle.putString("apkVersion", apkVersion);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }


    // 同步信息实时传递
    private void updateInformation(String apkpath) {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("apkpath", apkpath);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    public static Boolean checkUrl()
    {
        Socket socket = null;
        try {
            URL url = new URL(HttpClientHelper.getURL());
            String host = url.getHost();
            int port = url.getPort();
            if (port == -1) {
                port = 80;
            }
            socket = new Socket();
            InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName(host), port);

            socket.connect(isa, 10);
            if (socket.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
