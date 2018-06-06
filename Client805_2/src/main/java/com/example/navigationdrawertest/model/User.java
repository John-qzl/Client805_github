package com.example.navigationdrawertest.model;

import java.util.List;

import org.litepal.crud.DataSupport;

public class User extends DataSupport{
	/**
	 * 数据库声明字段
	 */
	private int id;
	private String userid;
	private String username;
	private String password;
	private String displayname;
	//2016-3-15 13:56:34
	private String ttidandname;			//试验队的ID和NAME（id,name;id,name）
	
	public String getTtidandname() {
		return ttidandname;
	}
	public void setTtidandname(String ttidandname) {
		this.ttidandname = ttidandname;
	}
	//	private String liangzong;
	private String postsString;			//存储的是该用户所有岗位的ID
//	private String rwids;
//	private String rwnames;
	
	public List<Post> posts;
	/**
	 * 非数据库声明字段	
	 */
	public List<String> postids;		//该用户所属岗位模板ID
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
//	public String getLiangzong() {
//		return liangzong;
//	}
//	public void setLiangzong(String liangzong) {
//		this.liangzong = liangzong;
//	}
	
	/**
	 * 根据userid查询Post集合
	 * @param userid
	 * @return
	 */
	public List<Post> getPostsByuserid(String userid) {
		return DataSupport.where("userid = ?", userid).find(Post.class);
	}
	public String getPostsString() {
		return postsString;
	}
	public void setPostsString(String postsString) {
		this.postsString = postsString;
	}
}
