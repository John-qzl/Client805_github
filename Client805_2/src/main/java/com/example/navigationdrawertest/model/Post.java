package com.example.navigationdrawertest.model;

import java.util.List;

import org.litepal.crud.DataSupport;
/**
 * 岗位模板表（岗位模板表和检查表格实例是一对多的关系）
 * @author liu
 *	2015 下午3:12:52
 */
public class Post extends DataSupport{
	private int id;
	private String postid;
	private String postname;
	private String postinstanceid;
	private String path;
	private String pathId;
	private String userId;

	public String getPathId() {
		return pathId;
	}

	public void setPathId(String pathId) {
		this.pathId = pathId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPostinstanceid() {
		return postinstanceid;
	}
	public void setPostinstanceid(String postinstanceid) {
		this.postinstanceid = postinstanceid;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	private List<Task> tasks;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPostid() {
		return postid;
	}
	public void setPostid(String postid) {
		this.postid = postid;
	}
	public String getPostname() {
		return postname;
	}
	public void setPostname(String postname) {
		this.postname = postname;
	}
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	/**
	 * litepal框架查询与Post关联的Task实体类
	 */
	public List<Task> getAllTaskByPostid() {  
        return DataSupport.where("post_id = ?", String.valueOf(id)).find(Task.class);  
    }
	
}
