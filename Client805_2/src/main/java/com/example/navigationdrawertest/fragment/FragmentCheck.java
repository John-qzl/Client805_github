package com.example.navigationdrawertest.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.greenrobot.eventbus.Subscribe;
import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationdrawertest.CustomUI.CellTypeEnum1;
import com.example.navigationdrawertest.MainActivity;
import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.SweetAlert.SweetAlertDialog;
import com.example.navigationdrawertest.activity.CheckActivity;
import com.example.navigationdrawertest.activity.CheckActivity1;
import com.example.navigationdrawertest.activity.LoginActivity;
import com.example.navigationdrawertest.activity.MainActivity1;
import com.example.navigationdrawertest.activity.ReadActivity;
import com.example.navigationdrawertest.activity.ReadActivity1;
import com.example.navigationdrawertest.activity.SignActivity;
import com.example.navigationdrawertest.activity.SignActivity1;
import com.example.navigationdrawertest.adapter.Event.LocationEvent;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Post;
import com.example.navigationdrawertest.model.Rows;
import com.example.navigationdrawertest.model.Rw;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.model.Scene;
import com.example.navigationdrawertest.model.Signature;
import com.example.navigationdrawertest.model.Task;
import com.example.navigationdrawertest.model.User;
import com.example.navigationdrawertest.tree.DepartmentNode;
import com.example.navigationdrawertest.tree.TreeHelper;
import com.example.navigationdrawertest.tree.TreeNode;
import com.example.navigationdrawertest.tree.UserNode;
import com.example.navigationdrawertest.utils.Config;
import com.example.navigationdrawertest.utils.FileOperation;
import com.example.navigationdrawertest.utils.HtmlHelper;
import com.example.navigationdrawertest.utils.NodeButtonEnum;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import de.greenrobot.event.EventBus;

/**
 * 待检查Fragment
 * @author liu
 *	2015-10-20 下午3:57:37
 */

@SuppressLint("ValidFragment")
public class FragmentCheck extends Fragment {
	private ListView mListView = null;
	private MyBaseAdapter adapter = null;
	private ArrayList<TreeNode> nodeList = new ArrayList<TreeNode>();
	private String currentUserid = OrientApplication.getApplication().loginUser.getUserid();
	Context context;
	TreeNode rootNode;
	private long clicktaskid;
	private NodeButtonEnum buttontype;
	private RwRelation proEntity;				//传递过来的项目树节点
	private static User user;
	private ProgressDialog prodlg;
	private AlertDialog.Builder dialog;
	
	public FragmentCheck(RwRelation proEntity){
		this.proEntity = proEntity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		OrientApplication.app.setPageflage(1);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.tree_main, container, false);
		mListView = (ListView) v.findViewById(R.id.listView1);
		context = getActivity();

		adapter = new MyBaseAdapter();
		loadData();
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(listViewItemClickListener);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}

	private void loadData() {
		List<Rw> rwList = new ArrayList<Rw>();				//任务id---人物名称
		List<Post> postList = new ArrayList<Post>();		//岗位实例ID---path
//		List<Task> taskList = new ArrayList<Task>();		//表格ID---表格名称
		user = DataSupport.where("userid = ?", currentUserid).find(User.class).get(0);
		
		nodeList.clear();

		List<Task> taskList_rw = new ArrayList<Task>();
		String testteamStr = user.getTtidandname();
		String[] testteamArr = testteamStr.split(",");
		for(int loop=0; loop<testteamArr.length; loop++){
			List<Task> tempTask = DataSupport.where("rwid = ? and postinstanceid = ? and location = ?", OrientApplication.getApplication().rw.getRwid(), testteamArr[loop], "1").find(Task.class);
			for(Task task:tempTask){
				taskList_rw.add(task);
			}
		}
		if(taskList_rw.size() != 0){

			rwList.clear();
			Rw rw = new Rw();
			rw.setRwid(OrientApplication.getApplication().rw.getRwid());
			rw.setRwname(OrientApplication.getApplication().rw.getRwname());
			rwList.add(rw);
			
			
			//3,获取所有的岗位实例-path
			//任务ID---人物名称；岗位实例ID---path；表格ID---表格名称
			for(int i=0; i<rwList.size(); i++){
				rootNode = new DepartmentNode(Long.valueOf(rwList.get(i).getRwid()), rwList.get(i).getRwname(), "0", null, 0);

				String postid_str = "";
				for(int j=0; j<taskList_rw.size(); j++){
					String postpath_str;
					Post post = new Post();
					if(j == 0){
						postid_str = taskList_rw.get(0).getPathId();
						postpath_str = taskList_rw.get(0).getPath();
						post.setPathId(postid_str);
						post.setPath(postpath_str);
						postList.add(post);
					}else{
						Boolean isPostList = true;
						for(int k=0; k<postList.size(); k++){
							if(postList.get(k).getPath().equals(taskList_rw.get(j).getPath())){
								isPostList = false;
							}
						}
						if(isPostList == true){
							post.setPathId(taskList_rw.get(j).getPathId());
							post.setPath(taskList_rw.get(j).getPath());
							postList.add(post);
						}
					}
				}
				
				//4,获取所有表格ID---表格名称
				for(int k=0; k<postList.size(); k++){
					TreeNode node = new DepartmentNode(Long.valueOf(postList.get(k).getPathId()), postList.get(k).getPath(),  "0", rootNode, 1);
					rootNode.add(node);
					List<Task> tasknodeList = new ArrayList<Task>();
					for(Task task : taskList_rw){
						if(task.getPath().equals(postList.get(k).getPath())){
							tasknodeList.add(task);
						}
					}
					for(int loop=0; loop<tasknodeList.size(); loop++){
						node.add(new UserNode(Long.valueOf(tasknodeList.get(loop).getTaskid()), tasknodeList.get(loop).getTaskname(), node, 2));
//						node.add(new UserNode(Long.valueOf(tasknodeList.get(loop).getTaskid()), tasknodeList.get(loop).getTaskname(), tasknodeList.get(loop).getTablesize(), node, 2));
					}
				}
			}
			

			rootNode.expandAllNode();

			rootNode.filterVisibleNode(nodeList);
		}
		adapter.notifyDataSetChanged();
	}
	
	OnItemClickListener listViewItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			// TODO Auto-generated method stub,arg2Ϊposition arg3Ϊid.
			TreeNode node = nodeList.get(position);
			Log.d("表的ID", clicktaskid+"");
			clicktaskid = nodeList.get(position).getId();
			if (node.getExpandStatus() == 2) {				//点击末尾节点事件
				node.setCheckStatus(node.getCheckStatus() == 0 ? 1 : 0);
				node.getParent().UpChecked();
				adapter.notifyDataSetChanged();
			}
			if (node.getExpandStatus() == 1) {
				node.setExpandStatus(0);
				nodeList = new ArrayList<TreeNode>();
				rootNode.filterVisibleNode(nodeList);
				adapter.notifyDataSetChanged();
			}
			else if (node.getExpandStatus() == 0) {
				node.setExpandStatus(1);
				nodeList = new ArrayList<TreeNode>();
				rootNode.filterVisibleNode(nodeList);
				adapter.notifyDataSetChanged();
			}
		}
	};
	
	/**
	 * 该方法在遇到LISTVIEW负责页面展现时有问题，需要重新优化
	 */
	OnClickListener OnCheckListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch(arg0.getId()){
			case R.id.check_line:
				Log.d("表的ID", clicktaskid+"");
				break;
			case R.id.read_line:
				Log.d("表的ID", clicktaskid+"");
				break;

			default:
				int index = (Integer) arg0.getTag();
				TreeNode node = nodeList.get(index);
				TreeHelper.onDepartmentChecked(node);
				if (node.getParent() != null)
					node.getParent().UpChecked();
				nodeList = new ArrayList<TreeNode>();
				rootNode.filterVisibleNode(nodeList);
				adapter.notifyDataSetChanged();
			}
		}
	};
	
//	public static Handler mHandler =new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//        	switch (msg.what) {
//            case 0:
//                break;
//            }
//        }
//    };


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			prodlg.dismiss();
			if(msg.what == 1) {
				dialog = new AlertDialog.Builder(getActivity());
				dialog.setIcon(R.drawable.logo_title).setTitle(R.string.app_name);
				dialog.setMessage("复制完成，点击确定重新加载！");
				dialog.setCancelable(false);
				dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent1 = new Intent(getActivity(), MainActivity1.class);
						startActivity(intent1);

					}
				});
				dialog.show();
			}
		}
	};

	private void showSweetAlertDialog(long clicktaskid, NodeButtonEnum nodebutton) {

        buttontype = nodebutton;
        if(buttontype == NodeButtonEnum.READBUTTON){
        	Toast.makeText(getActivity(), "数据初始化中，1秒后加载.......", Toast.LENGTH_LONG).show();
        	ReadActivity1.actionStart(getActivity(), clicktaskid, mHandler, "1");
        }
        if(buttontype == NodeButtonEnum.CHECKBUTTON){
        	Toast.makeText(getActivity(), "数据初始化中，1秒后加载.......", Toast.LENGTH_LONG).show();
        	CheckActivity1.actionStart(getActivity(), clicktaskid, mHandler, "1");
        }
        if(buttontype == NodeButtonEnum.SIGNBUTTON){
        	Toast.makeText(getActivity(), "数据初始化中，1秒后加载.......", Toast.LENGTH_LONG).show();
        	SignActivity1.actionStart(getActivity(), clicktaskid, mHandler, "1");
        }
    }
	
	
	/**
	 * ListView适配器。
	 */
	private class MyBaseAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return nodeList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return nodeList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			int layer = nodeList.get(position).getLayer();
			Long pathId = nodeList.get(position).getId();
//			final List<Task> taskList = DataSupport.where("pathId = ? and location=?", String.valueOf(pathId), "1").find(Task.class);
			final List<Task> taskList = DataSupport.where("pathId = ?", String.valueOf(pathId)).find(Task.class);
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				holder = new ViewHolder();
				if(layer == 2){
					convertView = inflater.inflate(R.layout.tree_item, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.txt_tree_width);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.img_tree_left);
					holder.check_line = (LinearLayout) convertView.findViewById(R.id.check_line);
					holder.read_line = (LinearLayout) convertView.findViewById(R.id.read_line);
					final List<Task> taskList1 = DataSupport.where("taskid = ?", String.valueOf(nodeList.get(position).getId())).find(Task.class);
					if (taskList1.size() > 0) {
						if (taskList1.get(0).getIsChecking() == 1) {
							holder.tv_name.setTextColor(getResources().getColor(R.color.actionbar));
						}
					}
					holder.check_line.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							clicktaskid = nodeList.get(position).getId();
							showSweetAlertDialog(clicktaskid, NodeButtonEnum.CHECKBUTTON);
						}
					});
					holder.read_line.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							clicktaskid = nodeList.get(position).getId();
							showSweetAlertDialog(clicktaskid, NodeButtonEnum.READBUTTON);
						}
					});

				} else if (layer == 1) {
					convertView = inflater.inflate(R.layout.tree_item_init_copy, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.init_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.init_txt_tree_width);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.init_img_tree_left);
					holder.copy_line = (LinearLayout) convertView.findViewById(R.id.copy_line);
					holder.add_line = (LinearLayout) convertView.findViewById(R.id.add_line);
					if (taskList.size() > 0) {
						if (taskList.get(0).getNodeLeaderId().contains(OrientApplication.getApplication().loginUser.getUserid())
								&& taskList.get(0).getIsBrother() != 1) {
							holder.copy_line.setVisibility(View.VISIBLE);
							holder.add_line.setVisibility(View.VISIBLE);
						}
					}
					holder.copy_line.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							warnInfo(taskList);
						}
					});
					holder.add_line.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							warnAddInfo(taskList);
						}
					});
				} else {
					convertView = inflater.inflate(R.layout.tree_item_init, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.init_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.init_txt_tree_width);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.init_img_tree_left);
				}
				convertView.setTag(holder);
			}
			else {
				LayoutInflater inflater = LayoutInflater.from(context);
				holder = new ViewHolder();
				if(layer == 2){
					convertView = inflater.inflate(R.layout.tree_item, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.txt_tree_width);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.img_tree_left);
					holder.check_line = (LinearLayout) convertView.findViewById(R.id.check_line);
					holder.read_line = (LinearLayout) convertView.findViewById(R.id.read_line);
					final List<Task> taskList1 = DataSupport.where("taskid = ?", String.valueOf(nodeList.get(position).getId())).find(Task.class);
					if (taskList1.size() > 0) {
						if (taskList1.get(0).getIsChecking() == 1) {
							holder.tv_name.setTextColor(getResources().getColor(R.color.actionbar));
						}
					}
					holder.check_line.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							clicktaskid = nodeList.get(position).getId();
							showSweetAlertDialog(clicktaskid, NodeButtonEnum.CHECKBUTTON);
						}
					});
					holder.read_line.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							clicktaskid = nodeList.get(position).getId();
							showSweetAlertDialog(clicktaskid, NodeButtonEnum.READBUTTON);
						}
					});
				} else if (layer == 1) {
					convertView = inflater.inflate(R.layout.tree_item_init_copy, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.init_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.init_txt_tree_width);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.init_img_tree_left);
					holder.copy_line = (LinearLayout) convertView.findViewById(R.id.copy_line);
					holder.add_line = (LinearLayout) convertView.findViewById(R.id.add_line);
					if (taskList.size() > 0) {
						if (taskList.get(0).getNodeLeaderId().contains(OrientApplication.getApplication().loginUser.getUserid())
								&& taskList.get(0).getIsBrother() != 1) {
							holder.copy_line.setVisibility(View.VISIBLE);
							holder.add_line.setVisibility(View.VISIBLE);
						}
					}
					holder.copy_line.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							warnInfo(taskList);
						}
					});
					holder.add_line.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							warnAddInfo(taskList);
						}
					});
				} else {
					convertView = inflater.inflate(R.layout.tree_item_init, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.init_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.init_txt_tree_width);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.init_img_tree_left);
				}
				convertView.setTag(holder);
			}

//			if (!nodeList.get(position).getTableSize().equals("0") && !nodeList.get(position).getTableSize().equals("1")
//					&& !nodeList.get(position).getTableSize().equals("")) {
//				holder.tv_name.setTextColor(getResources().getColor(R.color.red_btn_bg_color));
//			}

			holder.tv_name.setText("" + nodeList.get(position).getName()); 
			holder.tv_width.setText("");

			int[] leftIds = { R.drawable.tree_z, R.drawable.tree_s, R.drawable.biao };
			holder.iv_left.setImageResource(leftIds[nodeList.get(position).getExpandStatus()]);
			holder.tv_width.setMinWidth(layer * (holder.iv_left.getLayoutParams().width));

			return convertView;
		}

		public final class ViewHolder {
			public ImageView iv_left;
			public TextView tv_name;
			public TextView tv_width;
			//查看，检查，签署按钮
			public LinearLayout check_line;
			public LinearLayout read_line;
			public Button delete_button;
			public LinearLayout copy_line, add_line;
		}
	}

	@Subscribe
	public void onEventMainThread(LocationEvent locationEvent){
		if(locationEvent != null){
			loadData();
			adapter.notifyDataSetChanged();

		}
//		Toast.makeText(this, locationEvent, Toast.LENGTH_SHORT).show();
			
	}

	public void warnInfo(final List<Task> taskList) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setIcon(R.drawable.logo_title).setTitle("是否进行表单复制！");
		dialog.setMessage("此操作只允许管理员执行，请谨慎操作！");
		dialog.setCancelable(false);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String nodeName = getNodeName(taskList);

			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * @Description: 自定义节点名称
	 * @author qiaozhili
	 * @date 2019/3/4 11:39
	 * @param
	 * @return
	 */
	public String getNodeName(final List<Task> taskList) {
		LayoutInflater factory = LayoutInflater.from(getActivity());//提示框
		final View view = factory.inflate(R.layout.editbox_layout, null);//这里必须是final的
		final EditText edit=(EditText)view.findViewById(R.id.editText);//获得输入框对象
		final String[] nodeName = {""};
		new AlertDialog.Builder(getActivity())
				.setTitle("请输入新节点名称！")//提示框标题
				.setView(view)
				.setPositiveButton("确定",//提示框的两个按钮
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								Message message = new Message();
								prodlg = ProgressDialog.show(context, "警告", "正在复制，请稍侯...");
								nodeName[0] = String.valueOf(edit.getText());
								for (Task task : taskList) {
									copyTask(task, nodeName[0]);
								}
								message.what = 1;
								mHandler.sendMessage(message);
							}
						})
				.setNegativeButton("取消", null).create().show();
		return nodeName[0];
	}

	/**
	 * @Description: 复制节点及task
	 * @author qiaozhili
	 * @date 2019/3/4 10:56
	 * @param
	 * @return
	 */
	public void copyTask(Task task, String nodeName) {
		Task taskNew = new Task();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String timeString = formatter.format(curDate);
		long timeL = 0;
		String taskIdNew = "";
		String currentSecond = getTime(timeString);
		long taskIdL = Long.parseLong(task.getTaskid());
		long PathIdL = Long.parseLong(task.getPathId());
		if (task.getTaskid().length() > currentSecond.length()) {
			int size = task.getTaskid().length() - currentSecond.length();
			timeL = (long) (Long.parseLong(currentSecond) * Math.pow(10,size));
		}
		if (!task.getTaskid().equals("")) {
			String taskIdN = String.valueOf(taskIdL + timeL);
			taskNew.setTaskid(taskIdN);
			taskIdNew = taskIdN;
		} else {
			taskNew.setTaskid("");
		}
		if (!task.getPathId().equals("")) {
			String pathIdN = String.valueOf(PathIdL + timeL);
			taskNew.setPathId(pathIdN);
		} else {
			taskNew.setPathId("");
		}
		taskNew.setPath(nodeName);
		taskNew.setTaskname(task.getTaskname());
		taskNew.setRemark(task.getRemark());
		taskNew.setVersion(task.getVersion());
		taskNew.setLocation(1);
		taskNew.setTaskpic(task.getTaskpic());
		taskNew.setTablesize(task.getTablesize());
		taskNew.setRwid(task.getRwid());
		taskNew.setRwname(task.getRwname());
		taskNew.setPost(task.getPost());
		taskNew.setPostinstanceid(task.getPostinstanceid());
		taskNew.setPostname(task.getPostname());
		taskNew.setNodeLeaderId(task.getNodeLeaderId());
		taskNew.setLinenum(task.getLinenum());
		taskNew.setRownum(task.getRownum());
		taskNew.setStartTime(task.getStartTime());
		taskNew.setEndTime(task.getEndTime());
		taskNew.setIsfinish(task.getIsfinish());
		taskNew.setIsfirstfinish(task.getIsfirstfinish());
		taskNew.setInitStatue(task.getInitStatue());
		taskNew.setConditions(task.getConditions());
		taskNew.setSigns(task.getSigns());
		taskNew.setCells(task.getCells());
		taskNew.setRownummap(task.getRownummap());
		taskNew.setIsBrother(1);
		taskNew.setBroTaskId(task.getTaskid());
		taskNew.save();
		if (!taskIdNew.equals("")) {
			copyCondition(task, taskIdNew, timeL);
			copySignature(task, taskIdNew, timeL);
			copyRows(task, taskIdNew, timeL);
			copyCells(task, taskIdNew, timeL);
//			copyHtml(task, task.getPostname(), taskIdNew, timeL);
		} else {
			Toast.makeText(getActivity(), "数据有误，请检查数据", Toast.LENGTH_LONG);
		}

	}

	/**
	 * @param
	 * @return
	 * @Description: 复制conditions
	 * @author qiaozhili
	 * @date 2019/3/4 14:44
	 */
	public void copyCondition(Task task, String taskIdN, long timeL) {
		List<Scene> conditionList = DataSupport.where("taskid=?", task.getTaskid()).find(Scene.class);
		for (Scene scne : conditionList) {
			Scene scneNew = new Scene();
			if (!scne.getConditionid().equals("")) {
				long conditionIdL = Long.parseLong(scne.getConditionid());
				String conditionIdN = String.valueOf(conditionIdL + timeL);
				scneNew.setConditionid(conditionIdN);
			} else {
				scneNew.setConditionid("");
			}
			scneNew.setTaskid(taskIdN);
			scneNew.setTimeL(timeL);
			scneNew.setConditionname(scne.getConditionname());
			scneNew.setSceneorder(scne.getSceneorder());
			scneNew.setScenevalue(scne.getScenevalue());
			scneNew.setmTTID(scne.getmTTID());
			scneNew.save();
		}
	}

	/**
	 * @param
	 * @return
	 * @Description: 复制signs Signature
	 * @author qiaozhili
	 * @date 2019/3/4 14:45
	 */
	public void copySignature(Task task, String taskIdN, long timeL) {
		List<Signature> signatureList = DataSupport.where("taskid=?", task.getTaskid()).find(Signature.class);
		for (Signature signature : signatureList) {
			Signature signstureNew = new Signature();
			if (!signature.getSignid().equals("")) {
				long signIdL = Long.parseLong(signature.getSignid());
				String signIdN = String.valueOf(signIdL + timeL);
				signstureNew.setSignid(signIdN);
			} else {
				signstureNew.setSignid("");
			}
			signstureNew.setTaskid(taskIdN);
			signstureNew.setTimeL(timeL);
			signstureNew.setSignname(signature.getSignname());
			signstureNew.setSignorder(signature.getSignorder());
			signstureNew.setTime(signature.getTime());
			signstureNew.setRemark(signature.getRemark());
			signstureNew.setSignvalue(signature.getSignvalue());
			signstureNew.setmTTId(signature.getmTTId());
			signstureNew.setSignTime(signature.getSignTime());
			signstureNew.setIsFinish(signature.getIsFinish());
			signstureNew.setBitmappath(signature.getBitmappath());
			signstureNew.save();
		}
	}

	/**
	 * @param
	 * @return
	 * @Description: 复制Rows
	 * @author qiaozhili
	 * @date 2019/3/4 14:47
	 */
	public void copyRows(Task task, String taskIdN, long timeL) {
		List<Rows> rowsList = DataSupport.where("taskid=?", task.getTaskid()).find(Rows.class);
		for (Rows rows : rowsList) {
			Rows rowsNew = new Rows();
			rowsNew.setTaskid(taskIdN);
			rowsNew.setTimeL(timeL);
			rowsNew.setRowsid(rows.getRowsid());
			rowsNew.setRowsnumber(rows.getRowsnumber());
			rowsNew.save();
		}
	}

	/**
	 * @param
	 * @return
	 * @Description: 复制Cells
	 * @author qiaozhili
	 * @date 2019/3/4 15:24
	 */
	public void copyCells(Task task, String taskIdN, long timeL) {
		Document htmlDoc = HtmlHelper.getHtmlDoc(task);
		String fileAbsPath = Environment.getDataDirectory().getPath() + Config.packagePath
				+ Config.htmlPath+ "/"+ task.getPostname()+"/" + taskIdN;
		String htmlStr = htmlDoc.toString();
		List<Cell> cellList = DataSupport.where("taskid=?", task.getTaskid()).find(Cell.class);
		for (Cell cell : cellList) {
			Cell cellNew = new Cell();
			String cellIdNew = "";
			String cellIdOlder = cell.getCellid();
			if (!cellIdOlder.equals("")) {
				long cellIdL = Long.parseLong(cellIdOlder);
				String cellIdN = String.valueOf(cellIdL + timeL);
				cellNew.setCellid(cellIdN);
				cellIdNew = cellIdN;
			} else {
				cellNew.setCellid("");
			}
			if (htmlStr.contains(cellIdOlder)) {
				htmlStr = htmlStr.replace(cellIdOlder, cellIdNew);
			}
//			cellNew.setCellid(cell.getCellid());
			cellNew.setCellidold(cellIdOlder);
			cellNew.setTaskid(taskIdN);
			cellNew.setTimeL(timeL);
			cellNew.setRowname(cell.getRowname());
			cellNew.setHorizontalorder(cell.getHorizontalorder());
			cellNew.setVerticalorder(cell.getVerticalorder());
			cellNew.setType(cell.getType());
			cellNew.setTextvalue(cell.getTextvalue());
			cellNew.setColumnid(cell.getColumnid());
			cellNew.setTablesize(cell.getTablesize());
			cellNew.setRowsid(cell.getRowsid());
			cellNew.setmTTID(cell.getmTTID());
			cellNew.setIshook(cell.getIshook());
			cellNew.setOpvalue(cell.getOpvalue());
			cellNew.setCelltype(cell.getCelltype());
			cellNew.save();

			if (!cell.getCellid().equals("")) {
				copyOperetion(task, taskIdN, cellIdNew, cell.getCellid(), timeL);
			} else {
				Toast.makeText(getActivity(), "数据有误，请检查数据！", Toast.LENGTH_LONG);
			}
		}

		boolean bWriteOK = HtmlHelper.writeTaskHtml(fileAbsPath, htmlStr);
		if (bWriteOK) {
			Toast.makeText(getActivity(), taskIdN + ":HTML复制成功", Toast.LENGTH_LONG);
		} else {
			Toast.makeText(getActivity(), taskIdN + ":HTML复制失败", Toast.LENGTH_LONG);
		}

	}

	/**
	 * @param
	 * @return
	 * @Description: 复制Operetion
	 * @author qiaozhili
	 * @date 2019/3/4 15:25
	 */
	public void copyOperetion(Task task, String taskIdN, String cellIdN, String cellIdOld, Long timeL) {
		List<Operation> operationList = DataSupport.where("cellid=? and taskid=?", cellIdOld, task.getTaskid()).find(Operation.class);
		for (Operation operation : operationList) {
			Operation operationNew = new Operation();
			operationNew.setTaskid(taskIdN);
			operationNew.setCellidold(cellIdOld);
			operationNew.setTimeL(timeL);
			operationNew.setCellid(cellIdN);
			operationNew.setOperationid(cellIdN);
			operationNew.setRealcellid(cellIdN);
			operationNew.setType(operation.getType());
			operationNew.setOpvalue(operation.getOpvalue());
			operationNew.setRemark(operation.getRemark());
			operationNew.setIsfinished(operation.getIsfinished());
			operationNew.setTextvalue(operation.getTextvalue());
			operationNew.setmTTID(operation.getmTTID());
			operationNew.setOperationtype(operation.getOperationtype());
			operationNew.setIldd(operation.getIldd());
			operationNew.setIildd(operation.getIildd());
			operationNew.setTighten(operation.getTighten());
			operationNew.setErr(operation.getErr());
			operationNew.setLastaction(operation.getLastaction());
			operationNew.setIsmedia(operation.getIsmedia());
			operationNew.setSketchmap(operation.getSketchmap());
			operationNew.setTime(operation.getTime());
			operationNew.save();
		}
	}

	/**
	 * @param
	 * @return
	 * @Description: 复制HTMl
	 * @author qiaozhili
	 * @date 2019/3/4 17:49
	 */
	public void copyHtml(Task task, String postName, String taskIdN, long timeL) {
		Document htmlDoc = HtmlHelper.getHtmlDoc(task);
		String fileAbsPath = Environment.getDataDirectory().getPath() + Config.packagePath
				+ Config.htmlPath+ "/"+ postName+"/" + taskIdN;
		boolean bWriteOK = HtmlHelper.writeTaskHtml(fileAbsPath, htmlDoc.toString());
		if (bWriteOK) {
			Toast.makeText(getActivity(), taskIdN + ":HTML复制成功", Toast.LENGTH_LONG);
		} else {
			Toast.makeText(getActivity(), taskIdN + ":HTML复制失败", Toast.LENGTH_LONG);
		}
	}

	/**
	 * @Description: 获取当前时间毫秒
	 * @author qiaozhili
	 * @date 2019/3/4 11:10
	 * @param
	 * @return
	 */
	public static String getTime(String timeString){

		String timeStamp = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
		Date d;
		try{
			d = sdf.parse(timeString);
			long l = d.getTime();
			timeStamp = String.valueOf(l);
		} catch(ParseException e){
			e.printStackTrace();
		}
		return timeStamp;
	}

	/**
	 * @Description: 新增通用模板提示
	 * @author qiaozhili
	 * @date 2020/5/25 16:01
	 * @param
	 * @return
	 */
	public void warnAddInfo(final List<Task> taskList) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setIcon(R.drawable.logo_title).setTitle("是否添加PAD通用表单？");
		dialog.setMessage("说明：PAD通用表单为固定格式，4行5列，表单格式不可修改，可用于采集临时数据。");
		dialog.setCancelable(false);
		dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				addNormalTask(taskList.get(0));
			}
		});
		dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void addNormalTask(final Task task) {
		LayoutInflater factory = LayoutInflater.from(getActivity());//提示框
		final View view = factory.inflate(R.layout.normaltabletitle, null);//这里必须是final的
		final EditText et_tablename = (EditText) view.findViewById(R.id.et_tablename);
		final EditText et_linename1 = (EditText) view.findViewById(R.id.et_linename1);
		final EditText et_linename2 = (EditText) view.findViewById(R.id.et_linename2);
		final EditText et_linename3 = (EditText) view.findViewById(R.id.et_linename3);
		final EditText et_linename4 = (EditText) view.findViewById(R.id.et_linename4);
		final EditText et_linename5 = (EditText) view.findViewById(R.id.et_linename5);
		et_linename1.setHint("列1");
		et_linename2.setHint("列2");
		et_linename3.setHint("列3");
		et_linename4.setHint("列4");
		et_linename5.setHint("列5");
		final String[] taskName = {""};
		final String[] titleName = {"", "", "", "", ""};
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String timeString = formatter.format(curDate);
		et_tablename.setText("PAD通用表单" + timeString);
		new AlertDialog.Builder(getActivity())
				.setTitle("请输入表名及表头名称")//提示框标题
				.setView(view)
				.setPositiveButton("确定",//提示框的两个按钮
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								Message message = new Message();
								String tableName = String.valueOf(et_tablename.getText());
								String linename1 = String.valueOf(et_linename1.getText());
								String linename2 = String.valueOf(et_linename2.getText());
								String linename3 = String.valueOf(et_linename3.getText());
								String linename4 = String.valueOf(et_linename4.getText());
								String linename5 = String.valueOf(et_linename5.getText());
								if (linename1.equals("")) {linename1 = String.valueOf(et_linename1.getHint());}
								if (linename2.equals("")) {linename2 = String.valueOf(et_linename2.getHint());}
								if (linename3.equals("")) {linename3 = String.valueOf(et_linename3.getHint());}
								if (linename4.equals("")) {linename4 = String.valueOf(et_linename4.getHint());}
								if (linename5.equals("")) {linename5 = String.valueOf(et_linename5.getHint());}
								if (tableName.equals("")) {
									Toast.makeText(getActivity(), "表名不能为空", Toast.LENGTH_LONG);
									return;
								}
								if (!linename1.equals("") && !linename2.equals("") && !linename3.equals("") && !linename4.equals("") && !linename5.equals("")) {
									titleName[0] = linename1;
									titleName[1] = linename2;
									titleName[2] = linename3;
									titleName[3] = linename4;
									titleName[4] = linename5;
									prodlg = ProgressDialog.show(context, "提示", "正在添加，请稍侯...");
									taskName[0] = String.valueOf(et_tablename.getText());
									addTask(taskName[0], task, titleName);
									message.what = 1;
									mHandler.sendMessage(message);
								} else {
									Toast.makeText(getActivity(), "表头不能为空", Toast.LENGTH_LONG);
									return;
								}
							}
						})
				.setNegativeButton("取消", null).create().show();
	}

	/**
	 * @param
	 * @return
	 * @Description: 判读String是否为纯数字
	 * @author qiaozhili
	 * @date 2020/6/9 17:53
	 */
	public boolean isNumber(String str) {
		String pattern4 = "^^(0|[1-9]\\d*)$|^(0|[1-9]\\d*)\\.(\\d+)$";
		Pattern r4 = Pattern.compile(pattern4);
		Matcher m4 = r4.matcher(str);
		System.out.println(m4.matches());
		if (m4.matches()) {
//			Toast.makeText(context, "实测值" + actualvalNum + "非纯数字，无法判读！", Toast.LENGTH_SHORT).show();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @Description: 添加通用Task
	 * @author qiaozhili
	 * @date 2020/5/25 16:36
	 * @param
	 * @return
	 */
	public void addTask(String newTaskName, Task task, String[] titleName) {
		//组装HTML
		StringBuffer html =  new StringBuffer();
		html.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
		.append("<html>" + "<table width=\"100%\" class=\"layui-table\">" + "<tbody>" + "<tr class=\"firstRow\" style=\"background:rgb(160,182,204)\">");
		for (int i = 0; i < titleName.length; i++) {
			html.append("<td valign=\"middle\">")
					.append(titleName[i])
					.append("</td>");
		}
		html.append("</tr>");


		Task taskNew = new Task();
		String taskIdNew = "";
		String normalTaskId = Config.normalTaskId;
		long taskIdL = Long.parseLong(normalTaskId);
		long timeL = getCurTimeString(normalTaskId);
		long PathIdL = Long.parseLong(task.getPathId());
		if (!task.getTaskid().equals("")) {
			String taskIdN = String.valueOf(taskIdL + timeL);
			taskNew.setTaskid(taskIdN);
			taskIdNew = taskIdN;
		} else {
			taskNew.setTaskid("");
		}
		if (!task.getPathId().equals("")) {
			String pathIdN = String.valueOf(PathIdL + timeL);
			taskNew.setPathId(pathIdN);
		} else {
			taskNew.setPathId("");
		}
//		taskNew.setPathId(task.getPathId());
		taskNew.setPath(task.getPath());
		taskNew.setTaskname(newTaskName);
		taskNew.setRemark(task.getRemark());
		taskNew.setVersion(task.getVersion());
		taskNew.setLocation(task.getLocation());
		taskNew.setTaskpic(task.getTaskpic());
		taskNew.setTablesize(task.getTablesize());
		taskNew.setRwid(task.getRwid());
		taskNew.setRwname(task.getRwname());
		taskNew.setPost(task.getPost());
		taskNew.setPostinstanceid(task.getPostinstanceid());
		taskNew.setPostname(task.getPostname());
		taskNew.setNodeLeaderId(task.getNodeLeaderId());
		taskNew.setLinenum(task.getLinenum());
		taskNew.setRownum(task.getRownum());
		taskNew.setStartTime(task.getStartTime());
		taskNew.setEndTime(task.getEndTime());
		taskNew.setIsfinish("false");
		taskNew.setIsfirstfinish(task.getIsfirstfinish());
		taskNew.setInitStatue(task.getInitStatue());
		taskNew.setConditions(task.getConditions());
		taskNew.setSigns(task.getSigns());
		taskNew.setCells(task.getCells());
		taskNew.setRownummap(task.getRownummap());
		taskNew.setIsAdd(1);
		taskNew.setBroTaskId(task.getTaskid());
		taskNew.save();
		if (!taskIdNew.equals("")) {
			addSignature(task, taskIdNew);
			addRows(task, taskIdNew);
			addCells(task, taskIdNew, html, titleName);
//			copyHtml(task, task.getPostname(), taskIdNew, timeL);
		} else {
			Toast.makeText(getActivity(), "数据有误，请检查数据", Toast.LENGTH_LONG);
		}
//		boolean bWriteOK = FileOperation.writeTaskHtml(task, html.toString()); // 写入html把原来的覆盖

	}

	/**
	 * @Description: 添加通用表单签署信息
	 * @author qiaozhili
	 * @date 2020/6/28 11:04
	 * @param
	 * @return
	 */

	public void addSignature(Task task, String taskIdN) {
		long timeL = getCurTimeString(task.getTaskid());
		Signature signstureNew = new Signature();
		long signIdL = Long.parseLong(task.getTaskid());
		String signIdN = String.valueOf(signIdL + timeL);
		signstureNew.setSignid(signIdN);
		signstureNew.setTaskid(taskIdN);
		signstureNew.setTimeL(timeL);
		signstureNew.setSignname("操作人员");
		signstureNew.setSignorder("1");
		signstureNew.setmTTId(task.getPostinstanceid());
//			signstureNew.setTime(signature.getTime());
//		signstureNew.setRemark(signature.getRemark());
//		signstureNew.setSignvalue(signature.getSignvalue());
//			signstureNew.setSignTime(signature.getSignTime());
//			signstureNew.setIsFinish(signature.getIsFinish());
//		signstureNew.setBitmappath(signature.getBitmappath());
		signstureNew.save();
	}

	/**
	 * @Description: 添加通用表单rows信息
	 * @author qiaozhili
	 * @date 2020/6/28 11:05
	 * @param
	 * @return
	 */
	public void addRows(Task task, String taskIdN) {
		long timeL = getCurTimeString(task.getTaskid());
		Rows rowsNew = new Rows();
		rowsNew.setTaskid(taskIdN);
		rowsNew.setTimeL(timeL);
		rowsNew.setRowsid("1");
		rowsNew.setRowsnumber("1");
		rowsNew.save();
	}

	/**
	 * @Description: 添加通用表单Cells信息
	 * @author qiaozhili
	 * @date 2020/6/28 11:05
	 * @param
	 * @return
	 */
	public void addCells(Task task, String taskIdNew, StringBuffer html, String[] titleName) {
//		Document htmlDoc = HtmlHelper.getHtmlDoc(task);
		String fileAbsPath = Environment.getDataDirectory().getPath() + Config.packagePath
				+ Config.htmlPath+ "/"+ task.getPostname()+"/" + taskIdNew;
		long timeL = getCurTimeString(task.getTaskid());
		for (int  j= 0; j < 3; j++) {
			html.append("<tr>")
					.append("<td valign=\"middle\">")
					.append(j + 1)
					.append("</td>");
			long id1 = timeL + j * 100;
			for (int i = 0; i < titleName.length; i++) {
				Cell cellNew = new Cell();

				String cellIdNew = "";
				long cellIdL = Long.parseLong(task.getTaskid());
				long idL = cellIdL + id1;
				String cellIdN = String.valueOf(idL + i);
				cellIdNew = cellIdN;
				html.append("<td valign=\"middle\" class=\"selectTdClass\" input=\"1\" photo=\"1\">")
						.append("<input type=\"text\" class=\"dpInputText\" style=\"width: 60px;\" photo=\"是\" title=\"1,")
						.append(titleName[i])
						.append("列1\" id=\"")
						.append(cellIdNew)
						.append("\"/>")
						.append("<input class=\"dpInputBtn\" type=\"button\" disabled=\"true\" value=\"附件\" onclick=\"addAndShowPhoto(this)\" style=\"width: 45px; height: 24px;\"/>")
						.append("</td>");
//				cellNew.setCellidold(taskIdN);
//				cellNew.setCellid(cellIdN);
				cellNew.setCellid(cellIdNew);
				cellNew.setTaskid(taskIdNew);
				cellNew.setTimeL(timeL);
				cellNew.setRowname(titleName[i]);
				cellNew.setHorizontalorder(String.valueOf(j + 1));
				cellNew.setVerticalorder(String.valueOf(i + 1));
				if (i == 0) {
					cellNew.setType("FALSE");
				} else {
					cellNew.setType("TRUE");
//					addOperetion(task, taskIdNew, cellIdNew);
				}
				cellNew.setTextvalue("");
				cellNew.setColumnid(cellIdNew);
//				cellNew.setTablesize(cell.getTablesize());
				cellNew.setRowsid("1");
				cellNew.setmTTID(task.getPostinstanceid());
//				cellNew.setIshook(cell.getIshook());
				cellNew.setOpvalue("");
				cellNew.setCelltype(CellTypeEnum1.STRINGPHOTO);
				cellNew.save();
				addOperetion(task, taskIdNew, cellIdNew);
				if (i != 0) {
				}
			}
			html.append("</tr>");
		}
		html.append("\t\t</tbody>\n" + "\t</table>\n" + "</html>");

		boolean bWriteOK = HtmlHelper.writeTaskHtml(fileAbsPath, html.toString());
		if (bWriteOK) {
			Toast.makeText(getActivity(), taskIdNew + ":HTML复制成功", Toast.LENGTH_LONG);
		} else {
			Toast.makeText(getActivity(), taskIdNew + ":HTML复制失败", Toast.LENGTH_LONG);
		}

	}

	/**
	 * @param
	 * @return
	 * @Description: 获取当前时间字符串
	 * @author qiaozhili
	 * @date 2020/6/28 11:08
	 */
	public static long getCurTimeString(String id) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String timeString = formatter.format(curDate);

		String timeStamp = null;
		Date d;
		try{
			d = formatter.parse(timeString);
			long l = d.getTime();
			timeStamp = String.valueOf(l);
		} catch(ParseException e){
			e.printStackTrace();
		}

		long timeL = Long.parseLong(timeStamp);
//		if (id.length() > timeStamp.length()) {
//			int size = id.length() - timeStamp.length();
//			timeL = (long) (Long.parseLong(timeStamp) * Math.pow(10,size));
//		}
		return timeL;
	}

	/**
	 * @Description: 添加通用表单Operation
	 * @author qiaozhili
	 * @date 2020/6/28 15:02
	 * @param
	 * @return
	 */
	public void addOperetion(Task task, String taskIdNew, String cellIdN) {
		long timeL = getCurTimeString(task.getTaskid());
		Operation operationNew = new Operation();
		operationNew.setTaskid(taskIdNew);
//		operationNew.setCellidold(cellIdOld);
		operationNew.setTimeL(timeL);
		operationNew.setCellid(cellIdN);
		operationNew.setOperationid(cellIdN);
		operationNew.setRealcellid(cellIdN);
		operationNew.setType("2");
		operationNew.setOpvalue("");
//		operationNew.setRemark(operation.getRemark());
//		operationNew.setIsfinished(operation.getIsfinished());
		operationNew.setTextvalue("");
//		operationNew.setmTTID(operation.getmTTID());
		operationNew.setOperationtype("128");
		operationNew.setIldd("FALSE");
		operationNew.setIildd("FALSE");
		operationNew.setTighten("FALSE");
		operationNew.setErr("FALSE");
		operationNew.setLastaction("FALSE");
		operationNew.setIsmedia("TRUE");
//		operationNew.setSketchmap(operation.getSketchmap());
//		operationNew.setTime(operation.getTime());
		operationNew.save();
	}

}
