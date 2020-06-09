package com.example.navigationdrawertest.data;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import android.database.sqlite.SQLiteDatabase;

import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Post;
import com.example.navigationdrawertest.model.Scene;
import com.example.navigationdrawertest.model.Signature;
import com.example.navigationdrawertest.model.Task;
import com.example.navigationdrawertest.model.User;


public class AerospaceDB {
	/**
	 * 基本的数据库操作
	 */
	private SQLiteDatabase db;
	private static AerospaceDB aerospaceDB;
	public AerospaceDB(){
		db = Connector.getDatabase();		//生成数据库
	}
	
	/**
	 * 获得数据库实例对象
	 * @return
	 */
	public synchronized static AerospaceDB getInstance(){
		if(aerospaceDB == null){
			aerospaceDB = new AerospaceDB();
		}
		return aerospaceDB;
	}
	
	/**
	 * 取得该userid的用户信息
	 * @param userid
	 * @return
	 */
	public User getUserByUserid(String userid){
		List<User> userList = DataSupport.where("userid = ?", userid).find(User.class);
		if(userList != null){
			return userList.get(0);
		}
		return null;
	}
	
	/**
	 * 根据用户ID获取该用户下所有的岗位信息
	 * @param userid
	 * @return
	 */
	public List<Post> getPostByUserid(String userid){
		List<Post> postList = new ArrayList<Post>();
		User user = getUserByUserid(userid);
		if(user != null){
			String[] postLists = user.getPostsString().split(",");
			if(postLists.length > 0){
				for(int i=0; i<postLists.length; i++){
					postList.add(getPostByPostid(postLists[i]));
				}
			}
		}
		return postList;
	}
	/**
	 * 根据postid查找Post实体类
	 * @param postid
	 * @return
	 */
	public Post getPostByPostid(String postid){
		return DataSupport.where("postid = ?", postid).find(Post.class).get(0);		
	}
	
//	public List<Task> getTaskByUserid(String userid){
//		List<Post> postLists = new ArrayList<Post>();
//		List<Task> taskLists = new ArrayList<Task>();
//		postLists = getPostByUserid(userid);
//		taskLists.add(object)
//	}
	
	/**
	 * 将User实例存储到数据库
	 */
	public boolean saveUser(User user) {
		if (user != null) {
			if(user.save()){
				return true;
			}
		}
		return false;
	}
	public void saveTask(Task task) {
		if (task != null) {
			task.save();
		}
	}
	
	
	
	
	/**
	 * 加载用户信息
	 */
	public List<User> loadUsers() {
		List<User> list = DataSupport.findAll(User.class);
		return list;
	}
	
	/**
	 * 根据postid信息查找对应的task（表格）
	 * @return
	 */
	
	
	public List<Task> getAllTables(){
		return DataSupport.findAll(Task.class);
	}
	public List<Task> getAllTables(String userid){
//		return DataSupport.where("userid = ?", userid).find(Task.class);
		User user = DataSupport.where("userid = ?", userid).find(User.class).get(0);
		List<Task> taskList_rw = new ArrayList<Task>();
		String testteamStr = user.getTtidandname();
		String[] testteamArr = testteamStr.split(",");
		for(int loop=0; loop<testteamArr.length; loop++){
			List<Task> tempTask = DataSupport.where("postinstanceid = ?", testteamArr[loop]).find(Task.class);
			for(Task task:tempTask){
				taskList_rw.add(task);
			}
		}
		return taskList_rw;
	}
	
	public List<Task> getNotFinishTables(String userid){
//		return DataSupport.where("location = ? or location = ? or location = ? and userid = ?", "1","2","3", userid).find(Task.class);
		User user = DataSupport.where("userid = ?", userid).find(User.class).get(0);
		List<Task> taskList_rw = new ArrayList<Task>();
		String testteamStr = user.getTtidandname();
		String[] testteamArr = testteamStr.split(",");
		for(int loop=0; loop<testteamArr.length; loop++){
			List<Task> tempTask = DataSupport.where("postinstanceid = ? and location = ? or location = ? or location = ?", testteamArr[loop], "1","2","3").find(Task.class);
			for(Task task:tempTask){
				taskList_rw.add(task);
			}
		}
		return taskList_rw;
	}
	public List<Task> getCheckTables(String userid){
//		return DataSupport.where("location = ?", "1").find(Task.class);
		User user = DataSupport.where("userid = ?", userid).find(User.class).get(0);
		List<Task> taskList_rw = new ArrayList<Task>();
		String testteamStr = user.getTtidandname();
		String[] testteamArr = testteamStr.split(",");
		for(int loop=0; loop<testteamArr.length; loop++){
			List<Task> tempTask = DataSupport.where("postinstanceid = ? and location = ?", testteamArr[loop], "1").find(Task.class);
			for(Task task:tempTask){
				taskList_rw.add(task);
			}
		}
		return taskList_rw;
	}
	public List<Task> getSignTables(String userid){
//		return DataSupport.where("location = ?", "2").find(Task.class);
		User user = DataSupport.where("userid = ?", userid).find(User.class).get(0);
		List<Task> taskList_rw = new ArrayList<Task>();
		String testteamStr = user.getTtidandname();
		String[] testteamArr = testteamStr.split(",");
		for(int loop=0; loop<testteamArr.length; loop++){
			List<Task> tempTask = DataSupport.where("postinstanceid = ? and location = ?", testteamArr[loop], "2").find(Task.class);
			for(Task task:tempTask){
				taskList_rw.add(task);
			}
		}
		return taskList_rw;
	}
	public List<Task> getNotUploadTables(String userid){
//		return DataSupport.where("location = ?", "3").find(Task.class);
		User user = DataSupport.where("userid = ?", userid).find(User.class).get(0);
		List<Task> taskList_rw = new ArrayList<Task>();
		String testteamStr = user.getTtidandname();
		String[] testteamArr = testteamStr.split(",");
		for(int loop=0; loop<testteamArr.length; loop++){
			List<Task> tempTask = DataSupport.where("postinstanceid = ? and location = ?", testteamArr[loop], "3").find(Task.class);
			for(Task task:tempTask){
				taskList_rw.add(task);
			}
		}
		return taskList_rw;
	}
	public List<Task> getUploadTables(String userid){
//		return DataSupport.where("location = ? and userid = ?", "4", userid).find(Task.class);
		User user = DataSupport.where("userid = ?", userid).find(User.class).get(0);
		List<Task> taskList_rw = new ArrayList<Task>();
		String testteamStr = user.getTtidandname();
		String[] testteamArr = testteamStr.split(",");
		for(int loop=0; loop<testteamArr.length; loop++){
			List<Task> tempTask = DataSupport.where("postinstanceid = ? and location = ?", testteamArr[loop], "4").find(Task.class);
			for(Task task:tempTask){
				taskList_rw.add(task);
			}
		}
		return taskList_rw;
	}
	public Cell getCellByHV(String h, String v){
		return DataSupport.where("horizontalorder =? and verticalorder = ?", h,v).find(Cell.class).get(0);
	}
	//加载该用户下某个表格的Sign数据
	public List<Signature> loadSignatureAdapter(long taskid, String userid){
		String taskidToStr = taskid+"";
//		List<Signature> signs = DataSupport.where("taskid = ? and userid = ?", taskidToStr, userid).find(Signature.class);
		List<Signature> signs = DataSupport.where("taskid = ? and signType = ?", taskidToStr, "0").order("signorder asc").find(Signature.class);
		return signs;
	}
	
	public List<Scene> loadConditionAdapter(long taskid, String userid){
		String taskidToStr = taskid+"";
//		List<Scene> Scenes = DataSupport.where("taskid = ? and userid = ?", taskidToStr, userid).find(Scene.class);
		List<Scene> Scenes = DataSupport.where("taskid = ?", taskidToStr).order("sceneorder asc").find(Scene.class);
//		if(Scenes != null && Scenes.size() > 10){
//			
//		}
		return Scenes;
	}
	
	public List<Cell> loadTableAdapter(long taskid, String userid, int pagetype){
		String taskidToStr = taskid+"";
		String pagetypeToStr = pagetype + "";
		List<Cell> cellList = DataSupport.where("taskid = ? and rowsid = ?", taskidToStr, pagetypeToStr).find(Cell.class);
		return cellList;
	}
	
	public List<Cell> loadCellByHorizontalorder(long taskid, String userid, String Horizontalorder){
		String taskidToStr = taskid+"";
//		List<Cell> cellList = DataSupport.where("taskid = ? and userid = ? and horizontalorder = ?", 
//				taskidToStr, userid, Horizontalorder).find(Cell.class);
		List<Cell> cellList = DataSupport.where("taskid = ? and horizontalorder = ?", 
				taskidToStr, Horizontalorder).find(Cell.class);
		return cellList;
	}
	
	public Cell loadCellByVerticalorder(long taskid, String userid, String Horizontalorder, String verticalorder){
		String taskidToStr = taskid+"";
//		Cell cell = DataSupport.where("taskid = ? and userid = ? and horizontalorder = ? and verticalorder = ?", 
//				taskidToStr, userid, Horizontalorder, verticalorder).find(Cell.class).get(0);
		Cell cell = DataSupport.where("taskid = ? and horizontalorder = ? and verticalorder = ?", 
				taskidToStr, Horizontalorder, verticalorder).find(Cell.class).get(0);
		if(cell != null){
			return cell;
		}else{
			return null;
		}
	}
	
	public List<Operation> loadOperationByCellId(String cellid, String taskId){
		List<Operation> operationList = new ArrayList<Operation>();
		operationList = DataSupport.where("cellid = ? and taskid = ?", cellid, taskId).find(Operation.class);
		return operationList;
	}
	
	public Signature loadSignAdapter(){
		return null;
	}
	
	
	/**
	 * 删除数据库
	 */
	public int deleteAllUsers(){
		return DataSupport.deleteAll(User.class);
	}
	
	
	
	/**
	 * 关闭数据库
	 */
	public void destroyDB() {
		if (db != null) {
			db.close();
		}
	}
	
	
	
	
}



















