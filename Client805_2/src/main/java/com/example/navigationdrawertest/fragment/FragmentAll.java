package com.example.navigationdrawertest.fragment;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.activity.CheckActivity;
import com.example.navigationdrawertest.activity.ReadActivity1;
import com.example.navigationdrawertest.adapter.Event.LocationEvent;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Post;
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
import com.example.navigationdrawertest.utils.NodeButtonEnum;

import de.greenrobot.event.EventBus;

@SuppressLint("ValidFragment")
public class FragmentAll extends Fragment{
	private ListView mListView = null;
	private MyBaseAdapter adapter = null;
	private ArrayList<TreeNode> nodeList = new ArrayList<TreeNode>();
	private String currentUserid = OrientApplication.getApplication().loginUser.getUserid();
	private Context context;
	private TreeNode rootNode;
	private long clicktaskid;
	private NodeButtonEnum buttontype;
	private RwRelation proEntity;				//传递过来的项目树节点
	private static User user;

	public FragmentAll() {
		context = getActivity();
	}
	
	public FragmentAll(RwRelation proEntity){
		this.proEntity = proEntity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
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
//		if(adapter == null){		//这里有可能有问题，再次滑动过来就不会刷新了
//			adapter = new MyBaseAdapter();
//			loadData();
//		}
		adapter = new MyBaseAdapter();
		loadData();
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(listViewItemClickListener);
		return v;
	}
	
	private void loadData() {
		List<Rw> rwList = new ArrayList<Rw>();				//任务id---人物名称
		List<Post> postList = new ArrayList<Post>();		//岗位实例ID---path
		List<Task> taskList = new ArrayList<Task>();		//表格ID---表格名称
		user = DataSupport.where("userid = ?", currentUserid).find(User.class).get(0);
		
		nodeList.clear();
		List<Task> taskssss = DataSupport.findAll(Task.class);
		//1,获得该用户下的所有表格实体类集合，List<Task>
//		taskList = DataSupport.findAll(Task.class);
//		taskList = DataSupport.where("userid = ? and rwid = ?", currentUserid,
//				OrientApplication.getApplication().rw.getRwid()).find(Task.class);
		List<Task> taskList_rw = new ArrayList<Task>();
		String testteamStr = user.getTtidandname();
		String[] testteamArr = testteamStr.split(",");
		for(int loop=0; loop<testteamArr.length; loop++){
			List<Task> tempTask = DataSupport.where("rwid = ? and postinstanceid = ?", OrientApplication.getApplication().rw.getRwid(), testteamArr[loop]).find(Task.class);
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
						postid_str = taskList_rw.get(0).getPostinstanceid();
						postpath_str = taskList_rw.get(0).getPath();
						post.setPostinstanceid(postid_str);
						post.setPath(postpath_str);
						postList.add(post);
					}else{
						Boolean isPostList = true;
						for(int k=0; k<postList.size(); k++){
//							if(postList.get(k).getPostinstanceid().equals(taskList_rw.get(j).getPostinstanceid())){
							if(postList.get(k).getPath().equals(taskList_rw.get(j).getPath())){
								isPostList = false;
							}
						}
						if(isPostList == true){
							post.setPostinstanceid(taskList_rw.get(j).getPostinstanceid());
							post.setPath(taskList_rw.get(j).getPath());
							postList.add(post);
						}
					}
				}
				
				//4,获取所有表格ID---表格名称
				for(int k=0; k<postList.size(); k++){
					TreeNode node = new DepartmentNode(Long.valueOf(postList.get(k).getPostinstanceid()),
						postList.get(k).getPath(), "0", rootNode, 1);
					rootNode.add(node);
					List<Task> tasknodeList = new ArrayList<Task>();
//					tasknodeList = DataSupport.where("postinstanceid = ? and userid = ? and rwid = ?",
//					tasknodeList = DataSupport.where("path = ? and userid = ? and rwid = ?",
//							postList.get(k).getPath(), currentUserid, OrientApplication.getApplication().rw.getRwid())
////					tasknodeList = DataSupport.where("path = ? and userid = ? and location = ? and rwid = ?",
////							postList.get(k).getPostinstanceid(), currentUserid, OrientApplication.getApplication().rw.getRwid())
//							.find(Task.class);
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
			TreeNode node = nodeList.get(position);
			Log.d("表的ID", clicktaskid+"");
			clicktaskid = nodeList.get(position).getId();
			if (node.getExpandStatus() == 2) {
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
//				showSweetAlertDialog();
				break;
			case R.id.read_line:
				Log.d("表的ID", clicktaskid+"");
//				showSweetAlertDialog();
				break;
//			case R.id.sign_button:
//				Log.d("表的ID", clicktaskid+"");
////				showSweetAlertDialog();
////				CheckActivity.actionStart(getActivity(), clicktaskid); 
//				break;
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
	
	public Handler mHandler =new Handler(){
		@Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {  
            case 0:  
//            	pDialog.dismiss();
                break;   
            }
        }
    };
    
   
	private void showSweetAlertDialog(long clicktaskid, NodeButtonEnum nodebutton) {

        buttontype = nodebutton;
//        dismiss(pDialog, clicktaskid);
        if(buttontype == NodeButtonEnum.READBUTTON){
        	Toast.makeText(getActivity(), "数据初始化中，5秒后加载.......", Toast.LENGTH_LONG).show();
        	ReadActivity1.actionStart(getActivity(), clicktaskid, mHandler, "2");
        }
        if(buttontype == NodeButtonEnum.CHECKBUTTON){
        	Toast.makeText(getActivity(), "数据初始化中，5秒后加载.......", Toast.LENGTH_LONG).show();
        	CheckActivity.actionStart(getActivity(), clicktaskid, mHandler, "2");
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
			return arg0;
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
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				holder = new ViewHolder();
				if(layer == 2){
					convertView = inflater.inflate(R.layout.tree_item_last, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.fragmentupload_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.fragmentupload_txt_tree_width);
//					holder.iv_right = (ImageView) convertView.findViewById(R.id.fragmentupload_img_tree_right);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.fragmentupload_img_tree_left);
					holder.upload_read = (LinearLayout) convertView.findViewById(R.id.upload_read);
					holder.upload_read.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							clicktaskid = nodeList.get(position).getId();
							showSweetAlertDialog(clicktaskid, NodeButtonEnum.READBUTTON);
						}
					});
					holder.upload_shanchu = (LinearLayout) convertView.findViewById(R.id.upload_shanchu);
					holder.upload_shanchu.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Dialog alertDialog = new AlertDialog.Builder(context).
									setTitle("确定删除？").
									setMessage("您确定删除该条表单吗？").
									setIcon(R.drawable.logo_title).
									setNegativeButton("取消", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									}).
									setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											String deleteint = nodeList.get(position).getId()+"";
											DataSupport.deleteAll(Task.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Signature.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Cell.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Operation.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Scene.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Rw.class, "tableinstanceid = ?", deleteint);
											EventBus.getDefault().post(new LocationEvent("ok"));
										}
									}).
									create();
							alertDialog.show();
						}
					});
				}else if (layer == 1) {
					convertView = inflater.inflate(R.layout.tree_item_init_sign, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.init_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.init_txt_tree_width);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.init_img_tree_left);
				}else{
					convertView = inflater.inflate(R.layout.tree_item_init, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.init_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.init_txt_tree_width);
//					holder.iv_right = (ImageView) convertView.findViewById(R.id.init_img_tree_right);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.init_img_tree_left);
				}
				convertView.setTag(holder);
			}
			else {
//				holder = (ViewHolder) convertView.getTag();
				LayoutInflater inflater = LayoutInflater.from(context);
				holder = new ViewHolder();
				if(layer == 2){
					convertView = inflater.inflate(R.layout.tree_item_last, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.fragmentupload_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.fragmentupload_txt_tree_width);
//					holder.iv_right = (ImageView) convertView.findViewById(R.id.fragmentupload_img_tree_right);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.fragmentupload_img_tree_left);
					holder.upload_read = (LinearLayout) convertView.findViewById(R.id.upload_read);
					holder.upload_read.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							clicktaskid = nodeList.get(position).getId();
							showSweetAlertDialog(clicktaskid, NodeButtonEnum.READBUTTON);
						}
					});
					holder.upload_shanchu = (LinearLayout) convertView.findViewById(R.id.upload_shanchu);
					holder.upload_shanchu.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Dialog alertDialog = new AlertDialog.Builder(context).
									setTitle("确定删除？").
									setMessage("您确定删除该条表单吗？").
									setIcon(R.drawable.logo_title).
									setNegativeButton("取消", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									}).
									setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											String deleteint = nodeList.get(position).getId()+"";
											DataSupport.deleteAll(Task.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Signature.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Cell.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Operation.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Scene.class, "taskid = ?", deleteint);
											DataSupport.deleteAll(Rw.class, "tableinstanceid = ?", deleteint);
											EventBus.getDefault().post(new LocationEvent("ok"));
										}
									}).
									create();
							alertDialog.show();
						}
					});
				}else if (layer == 1) {
					convertView = inflater.inflate(R.layout.tree_item_init_sign, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.init_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.init_txt_tree_width);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.init_img_tree_left);
				}else{
					convertView = inflater.inflate(R.layout.tree_item_init, null);
					holder.tv_name = (TextView) convertView.findViewById(R.id.init_txt_tree_name);
					holder.tv_width = (TextView) convertView.findViewById(R.id.init_txt_tree_width);
//					holder.iv_right = (ImageView) convertView.findViewById(R.id.init_img_tree_right);
					holder.iv_left = (ImageView) convertView.findViewById(R.id.init_img_tree_left);
				}
				convertView.setTag(holder);
			}
			
			holder.tv_name.setText("" + nodeList.get(position).getName());
			holder.tv_width.setText("");

			int[] leftIds = { R.drawable.tree_z, R.drawable.tree_s, R.drawable.biao };
			holder.iv_left.setImageResource(leftIds[nodeList.get(position).getExpandStatus()]);
//			int[] rightIds = { R.drawable.icon_checkbox_none, R.drawable.icon_checkbox_all, R.drawable.icon_checkbox_part };
			holder.tv_width.setMinWidth(layer * (holder.iv_left.getLayoutParams().width));

			return convertView;
		}

		public final class ViewHolder {
			public ImageView iv_left;
//			public ImageView iv_right;
			public TextView tv_name;
			public TextView tv_width;
			//查看，检查，签署按钮
			public LinearLayout upload_read;
			public LinearLayout upload_shanchu;
		}
	}
	
	public void onEventMainThread(LocationEvent locationEvent){
		if(locationEvent != null){
			loadData();
			adapter.notifyDataSetChanged();
		}
	}
	
}
