package com.example.navigationdrawertest.utils;

import java.io.File;
import android.os.Environment;

import com.example.navigationdrawertest.application.OrientApplication;

public class TaskPhotoUtil {

	public static String  rootName = "data/com.orient.targetsiteflow/files/RWPhoto";
	/**
	 * 快速排序
	 * @param listFileNames
	 * @param l
	 * @param r
	 */
	public static void quick_sort(String[] listFileNames, int l, int r){
		if(l < r){
			int i=l, j=r;	//x是基准值
			String x=listFileNames[l];
			while(i < j){
				while(i < j && listFileNames[j].compareToIgnoreCase(x) >= 0)	//从右向左找第一个小于x的数
					j--;
				if(i < j){
					listFileNames[i++] = listFileNames[j];	//把右边大于x的数值放到待填入的坑中（1）
				}
				while(i<j && listFileNames[i].compareToIgnoreCase(x) <0)	//从左向右找第一个大于或者等于x的数
					i++;
				if(i < j)
					listFileNames[j--] = listFileNames[i];	//把这个数填入到从右往左找时留下的坑（2）
			}
			listFileNames[i] = x;
			//到此为止，只进行了一趟排序
			quick_sort(listFileNames, l, i-1);	//递归调用
			quick_sort(listFileNames, i+1, r);
		}
	}
	
	public static String getAllTaskPhotoNameList()
	{
		String retPhotoNames = "";
		//任务级别的拍照暂时放到人员下面
		String rootName = "data/com.orient.targetsiteflow/files/RWPhoto";
		File dataRoot = Environment.getDataDirectory();
		String dataRootPath = dataRoot.getPath();
		String approotPath = dataRootPath + "/" + rootName;
		
		//任务拍照存放根目录
		File rootFile = new File(approotPath);
		if (!rootFile.exists())
		{
			rootFile.mkdirs();
		}
		//任务级别的拍照现在放在人员下面
//		File rwRootDir = new File(approotPath+"/"+OrientApplication.getApplication().loginUser.getUserid());
		File rwRootDir = new File(approotPath);
		if(!rwRootDir.exists())
		{
			rwRootDir.mkdirs();
		}
		File[] listFiles = rwRootDir.listFiles();
		if(listFiles.length != 0){
			String[] listFileNames = new String[listFiles.length];
			for(int count =0 ;count <listFiles.length; count++){
				listFileNames[count] = "";
			}
			for(int i =0 ; i< listFiles.length; i++){
				listFileNames[i] = listFiles[i].getName();
			}
			
			quick_sort(listFileNames, 0 , listFileNames.length-1);
			
			for(String fileNames : listFileNames){
				if("".equals(retPhotoNames))
				{
					retPhotoNames = fileNames;
				}
				else
				{
					retPhotoNames = retPhotoNames + "," + fileNames;
				}
			}
		}else{
			for(File file:listFiles)
			{
				String fileName = file.getName();
				if("".equals(retPhotoNames))
				{
					retPhotoNames = fileName;
				}
				else
				{
					retPhotoNames = retPhotoNames + "," + fileName;
				}
			}
		}
		return retPhotoNames;
	}
	
	public static String findContentByName(String fileName)
	{
		String fileContent = "";
		String rootName = "data/com.orient.targetsiteflow/files/RWPhoto";
		File dataRoot = Environment.getDataDirectory();
		String dataRootPath = dataRoot.getPath();
		String approotPath = dataRootPath + "/" + rootName;
		
		//任务拍照存放根目录
		File rootFile = new File(approotPath);
		if (!rootFile.exists())
		{
			rootFile.mkdirs();
		}
		//任务级别的拍照现在放到人员ID下面
//		File rwRootDir = new File(approotPath+"/"+OrientApplication.getApplication().loginUser.getUserid());
		File rwRootDir = new File(approotPath);
		if(!rwRootDir.exists())
		{
			rwRootDir.mkdirs();
		}
		File[] listFiles = rwRootDir.listFiles();
		for(File file:listFiles)
		{
			if(file.getName().equals(fileName))
			{
				String filePath = file.getAbsolutePath();
				fileContent = FileOperation.readPicture(filePath);
				break;
			}
		}
		return fileContent;
	}
}
