package com.zgy.catchuninstallself;

import java.io.File;

import android.os.Environment;

import com.example.navigationdrawertest.application.OrientApplication;

/**
 * 
 * @Author 
 * @Date:
 * @version 
 * @since
 * C代码参考网上资料进行修改的，国外开源代码
 * 
 * 	卸载事件
 */

public class UninstallObserver {

	static{
		System.loadLibrary("observer");
		
		String sign_dir = Environment.getExternalStorageDirectory() + File.separator+OrientApplication.getApplication()
				.loginUser.getUserid()+"/";
		File file = new File(sign_dir);
		RecursionDeleteFile(file);
	}
//	public static native String startWork(String path, String url, int version);//path：data/data/[packageNmae]   ；   url:跳转的页面，需要http://或https://开头
	
	static void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
}
