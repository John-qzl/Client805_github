package com.example.navigationdrawertest.model;

import org.litepal.crud.DataSupport;
/**
 * 记录文件上传状态
 * @author liu
 *
 */
public class UploadFileRecord extends DataSupport{
	
	private int id;
	private String mFileName;
	private String mFilePath;
	private String mState;			//true为已经上传，false为还未上传
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getmFileName() {
		return mFileName;
	}
	public void setmFileName(String mFileName) {
		this.mFileName = mFileName;
	}
	public String getmFilePath() {
		return mFilePath;
	}
	public void setmFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
	}
	public String getmState() {
		return mState;
	}
	public void setmState(String mState) {
		this.mState = mState;
	}
	
}
