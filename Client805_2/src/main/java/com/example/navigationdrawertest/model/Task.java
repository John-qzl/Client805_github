package com.example.navigationdrawertest.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Element;
/**
 * 检查表格实例表
 * @author liu
 *	2015 下午3:13:33
 */
public class Task extends DataSupport{
	private int id;
	private String taskid;				//每个检查表的ID
	private String taskname;			//每个检查表的名称
	private String remark;
	private String path;				//所属实验节点名称
	private String pathId;				//所属实验节点ID
	private String version;
	private int location;				//1,2,3,4（位置）
	private String taskpic;				//表格照片（暂时不提示），就是在表格实例中的照片，随着表格实例而来的
	private String tablesize;			//是否拆分表格


	private String rwid;					//发次ID
	private String rwname;					//发次名称
	private String postname;				//所属工作队队名称
	private String postinstanceid;			//所属工作队ID
	private String nodeLeaderId; 			//所属节点负责人ID
//	private String userid;				//用户id
//	private String username;			//用户名
	private Post post;

	private String rownum;	//行数
	private String linenum;		//列数

	//2016-4-11 15:54:56
	private String startTime;			//开始时间
	private String endTime;			//结束时间
	private String isfirstfinish;			//检查开始标志
	//2016-4-15 14:00:06
	private String isfinish;				//表格是否完成按钮		"true","false"
	//2016-7-29 09:34:23
	private String initStatue;			//初始化状态   finish,unfinish

	private List<Scene> conditions;
	private List<Signature> signs;
	private List<Cell> cells;

	private Map<String, String> rownummap;
	private int IsBrother;		//是否为复制的表单，1为复制
	private String broTaskId;			//复制于哪张表的ID

	public String getBroTaskId() {
		return broTaskId;
	}

	public void setBroTaskId(String broTaskId) {
		this.broTaskId = broTaskId;
	}

	public int getIsBrother() {
		return IsBrother;
	}

	public void setIsBrother(int isBrother) {
		IsBrother = isBrother;
	}

	public Map<String, String> getRownummap() {
		return rownummap;
	}

	public void setRownummap(Map<String, String> rownummap) {
		this.rownummap = rownummap;
	}

	public static String 	TAG_Task = "task";
	public static String	TAG_TaskJPG = "TaskJPG";
	public static String	TAG_OperationJPG = "OperationJPG";
	public static String	TAG_PhotoJPG = "PhotoJPG";
	public static String 	TAG_POSTDOC = "PostDoc";

	public static String 	Atti_PostID = "postid";
	public static String 	Atti_PostName = "postname";
	public static String 	Atti_RwID = "rwid";
	public static String 	Atti_RwName = "rwname";
	public static String	Atti_InstanceID = "tableinstanceId";
	public static String	Atti_Name = "name";
	public static String	Atti_Version = "version";
	public static String	Atti_File = "file";
	public static String	Atti_Remark = "remark";
	public static String	Atti_Path = "path";
	public static String	Atti_responsibility = "responsibility";
	public static String	Atti_isOK = "isOK";
	public static String	Atti_Order= "order";
	public static String	Atti_Pic = "taskPic";
	public static String 	Atti_PhotoList = "taskPhotoList";
	public static String	Atti_Finished = "isfinished";
	public static String 	Atti_FXTID = "fxtid";
	public static String 	Atti_FXTName = "fxtname";

	/**
	 * 805XML交互标签和属性
	 * @return
	 */
	public static String Tag_Task = "task";
	public static String Attr_name = "name";
	public static String Attr_tableinstanceId = "tableinstanceId";
	public static String Attr_path = "path";
	public static String Attr_rwid = "rwid";
	public static String Attr_rwname = "rwname";
	public static String Attr_postname = "postname";
	public static String Attr_postinstanceid = "postinstanceid";
	public static String Attr_starttime = "starttime";
	public static String Attr_endtime = "endtime";

	public String getInitStatue() {
		return initStatue;
	}
	public void setInitStatue(String initStatue) {
		this.initStatue = initStatue;
	}
	public String getIsfinish() {
		return isfinish;
	}
	public void setIsfinish(String isfinish) {
		this.isfinish = isfinish;
	}
//	public String getUserid() {
//		return userid;
//	}
//	public void setUserid(String userid) {
//		this.userid = userid;
//	}
//	public String getUsername() {
//		return username;
//	}
//	public void setUsername(String username) {
//		this.username = username;
//	}

	public String getNodeLeaderId() {
		return nodeLeaderId;
	}

	public void setNodeLeaderId(String nodeLeaderId) {
		this.nodeLeaderId = nodeLeaderId;
	}

	public String getPostinstanceid() {
		return postinstanceid;
	}
	public void setPostinstanceid(String postinstanceid) {
		this.postinstanceid = postinstanceid;
	}
	public String getRwid() {
		return rwid;
	}
	public void setRwid(String rwid) {
		this.rwid = rwid;
	}
	public String getRwname() {
		return rwname;
	}
	public void setRwname(String rwname) {
		this.rwname = rwname;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}


	public String getTablesize() {
		return tablesize;
	}

	public void setTablesize(String tablesize) {
		this.tablesize = tablesize;
	}
	public String getTaskname() {
		return taskname;
	}
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public String getPathId() {
		return pathId;
	}

	public void setPathId(String pathId) {
		this.pathId = pathId;
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public List<Scene> getConditions() {
		return conditions;
	}
	public void setConditions(List<Scene> conditions) {
		this.conditions = conditions;
	}
	public List<Signature> getSigns() {
		return signs;
	}
	public void setSigns(List<Signature> signs) {
		this.signs = signs;
	}
	public List<Cell> getCells() {
		return cells;
	}
	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public String getTaskpic() {
		return taskpic;
	}
	public void setTaskpic(String taskpic) {
		this.taskpic = taskpic;
	}

	public Element setTaskNode(Element taskElement, Task task)
	{
		taskElement.setAttribute(Attr_name, task.taskname);
		taskElement.setAttribute(Attr_tableinstanceId, task.taskid);
		taskElement.setAttribute(Attr_path, task.path);
		taskElement.setAttribute(Attr_rwid, task.rwid);									//是projectId
		taskElement.setAttribute(Attr_rwname, task.rwname);
		taskElement.setAttribute(Attr_postname, task.postname);					//path就是树节点名称
		taskElement.setAttribute(Attr_postinstanceid, task.postinstanceid);
		taskElement.setAttribute(Attr_starttime, task.startTime);	//
		taskElement.setAttribute(Attr_endtime, task.endTime);

//		if(task.location.equals("3")){		//在待上传中，location=3
//			taskElement.setAttribute(Atti_isOK, "true");
//		}else{
//			taskElement.setAttribute(Atti_isOK, "false");
//		}
		return taskElement;
	}
	public String getRownum() {
		return rownum;
	}
	public void setRownum(String rownum) {
		this.rownum = rownum;
	}
	public String getLinenum() {
		return linenum;
	}
	public void setLinenum(String linenum) {
		this.linenum = linenum;
	}
	public String getPostname() {
		return postname;
	}
	public void setPostname(String postname) {
		this.postname = postname;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getIsfirstfinish() {
		return isfirstfinish;
	}
	public void setIsfirstfinish(String isfirstfinish) {
		this.isfirstfinish = isfirstfinish;
	}

}
