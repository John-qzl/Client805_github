package com.example.navigationdrawertest.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.example.navigationdrawertest.R;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.model.Mmc;
import com.example.navigationdrawertest.model.Product;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.secret.FileEncryption;
import com.example.navigationdrawertest.tree.DepartmentNode;
import com.example.navigationdrawertest.tree.TreeNode;
import com.example.navigationdrawertest.tree.UserNode;
import com.example.navigationdrawertest.utils.ArrUtil;
import com.example.navigationdrawertest.utils.FileOperation;
import com.example.navigationdrawertest.utils.Setting;
import com.example.navigationdrawertest.utils.ThridToolUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DocActivity extends BaseActivity{
	
	private ArrayList<TreeNode> nodeList = new ArrayList<TreeNode>();
	private MyBaseAdapter adapter;
	private TreeNode rootNode;
	private ListView searchList;
	private ProgressDialog progressDialog;
	private Context context;
	private ImageView mBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doclist);
		getActionBar().hide();
		context = this;
		initUI();
		adapter = new MyBaseAdapter();
		searchList.setAdapter(adapter);
		searchList.setOnItemClickListener(listViewItemClickListener);
		initData();
	}
	
	public void initUI(){
		searchList = (ListView) findViewById(R.id.doclist_list);
		mBack = (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(DocActivity.this);
                progressDialog.setTitle("加密操作");
                progressDialog.setMessage("数据正在加密处理中，请稍候！");
                progressDialog.setCancelable(false);               					//设置进度条是否可以按退回键取消
                progressDialog.setCanceledOnTouchOutside(false);  		//设置点击进度对话框外的区域对话框不消失
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Android文件系统短期内删除后重新创建需要提前更名删除
                        File file = new File(Setting.FILE_SECRET_END);
                        FileOperation.RecursionDeleteFile(file);
                        Message message = new Message();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });
	}

	/**
	 * 0层节点：product
	 * 1层节点：project
	 * 2层节点：path
	 * 3层节点：多媒体资料
	 */
	public void initData(){
//		String userId = OrientApplication.getApplication().loginUser.getUserid();							//登录用户ID
//		String ttidandname = OrientApplication.getApplication().loginUser.getTtidandname();		//试验队的id和名称
		//1,找到所有的产品数据
		List<Product> productList = DataSupport.findAll(Product.class);
		rootNode = new DepartmentNode(Long.valueOf(-1), "805多媒体资料", "0", null, 0);
		if(productList.size() > 0){
			for(Product product : productList){
//				rootNode = new DepartmentNode(-1, product.getProduct_Name(), null, 0);
				TreeNode productNode = new DepartmentNode(Long.valueOf(product.getProduct_Id()),
						product.getProduct_Name(), "0", rootNode, 1);
				//2,找到该产品下所有的发次节点
				List<RwRelation> rwListTest = DataSupport.findAll(RwRelation.class);
				List<RwRelation> rwList = DataSupport.where("productid = ? and userid =?", 
						product.getProduct_Id(), OrientApplication.getApplication().loginUser.getUserid()).find(RwRelation.class);
				if(rwList.size() > 0){ 
					for(RwRelation rw : rwList){
						TreeNode rwNode = new DepartmentNode(Long.valueOf(rw.getRwid()), rw.getRwname(), "0", productNode, 2);
						//3，找到该发次下所有的多媒体资料，找出Path清单
						//3-2,为了确保离线登录，要保证同一个岗位下的人员能够看到这些资料，所以除了任务，还有试验队
						List<Mmc> mmcList = DataSupport.where("rw_Id = ?", rw.getRwid()).find(Mmc.class);
						String[] gwArr = OrientApplication.getApplication().loginUser.getTtidandname().split(",");
						if(mmcList.size() > 0){
							//3-1,遍历所有的多媒体数据，找出不重复的path路径
							List<String> mmcPathList = new ArrayList<String>();
							for(Mmc mmc : mmcList){
								String path = mmc.getDisplaypath_Name();
								String mmcGw = mmc.getGw_Id();
								if(!mmcPathList.contains(path) && ArrUtil.useList(gwArr, mmcGw)){
									mmcPathList.add(path);
								}
							}
							for(int i=0; i<mmcPathList.size(); i++){
								TreeNode pathNode = new DepartmentNode(Long.valueOf(i), mmcPathList.get(i), "0", rwNode, 3);
								//4,找到该path下所有的mmc记录
								List<Mmc> mmcList1 = DataSupport.where("displaypath_Name = ? and rw_Id = ?",
										mmcPathList.get(i), rw.getRwid()).find(Mmc.class);
								for(Mmc mmc : mmcList1){
									if(ArrUtil.useList(gwArr, mmc.getGw_Id())){
										TreeNode mmcNode = new UserNode(Long.valueOf(mmc.getMmc_Id()), mmc.getMmc_Name(), pathNode, 4);
//										TreeNode mmcNode = new UserNode(Long.valueOf(mmc.getMmc_Id()), mmc.getMmc_Name(), "1",  pathNode, 4);
										pathNode.add(mmcNode);
									}
								}
								rwNode.add(pathNode);
							}
						}
						productNode.add(rwNode);
					}
					rootNode.add(productNode);
				}
			}
		}else{
			rootNode = new DepartmentNode(Long.valueOf(-1), "无", "0", null, 0);
		}
//		nodeList.add(rootNode);

		rootNode.expandAllNode();
		rootNode.filterVisibleNode(nodeList);
		adapter.notifyDataSetChanged();
	}
	
	
	OnItemClickListener listViewItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(android.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			TreeNode node = nodeList.get(arg2);
			if (node.getExpandStatus() == 2) {		//点击末尾节点事件
				final Mmc mmc = DataSupport.where("mmc_Id = ?", node.getId()+"").find(Mmc.class).get(0);
				if(mmc != null){
					/**
					 * 读取文件的时候临时生成一个文件
					 */
					progressDialog = new ProgressDialog(context);
	                progressDialog.setTitle("加密操作");
	                progressDialog.setMessage("文件正在解密处理中，请稍候！");
	                progressDialog.setCancelable(false);               					//设置进度条是否可以按退回键取消
	                progressDialog.setCanceledOnTouchOutside(false);  		//设置点击进度对话框外的区域对话框不消失
	                progressDialog.show();
		        	new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								String secret_start = Setting.FILE_SECRET_START + File.separator + mmc.getMmc_Name()
										+ "." + mmc.getType();
								FileOperation.createDir(Setting.FILE_SECRET_END);
								String secret_end = Setting.FILE_SECRET_END + File.separator + mmc.getMmc_Name()
										+ "." + mmc.getType();
								//乔志理  注销加密操作
//								FileEncryption.decryptold(secret_start, secret_end);
//								DESUtils.decrypt(secret_start, secret_end);
								Message message = new Message();
								message.what = 2;
								Bundle bundle = new Bundle();
								bundle.putString("path", secret_start);
								message.setData(bundle);
								mHandler.sendMessage(message);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
//					String path = Environment.getExternalStorageDirectory() + File.separator + "mmc" + 
//							 File.separator + mmc.getMmc_Id() + "." + mmc.getType();
//					File file = new File(path);
//					File file = new File(secret_end);
//					boolean s = file.exists();
//					ThridToolUtils.openFile(file, DocActivity.this);
				}else{
					Toast.makeText(DocActivity.this, "该文件不存在，请联系管理员", Toast.LENGTH_SHORT).show();
				}
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
		};
	};
	
	private class MyBaseAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return nodeList.size();
		}

		@Override
		public Object getItem(int position) {
			return nodeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder viewholder = null;
			if(viewholder == null){
				viewholder = new ViewHolder();
				LayoutInflater mInflater = LayoutInflater.from(DocActivity.this);
				view = mInflater.inflate(R.layout.tree_item_init, null);
				viewholder.iv_left = (ImageView) view.findViewById(R.id.init_img_tree_left);
				viewholder.tv_name = (TextView) view.findViewById(R.id.init_txt_tree_name);
				viewholder.tv_width = (TextView) view.findViewById(R.id.init_txt_tree_width);
				viewholder.delete = (ImageView) view.findViewById(R.id.init_img_tree_delete);
				view.setTag(viewholder);
			}else{
				viewholder = (ViewHolder) view.getTag();
			}
			
			int layer = nodeList.get(position).getLayer();
			viewholder.tv_name.setText("" + nodeList.get(position).getName());
			viewholder.tv_width.setText("");
			int[] leftIds = {R.drawable.icon_plusminus_add_black, R.drawable.icon_plusminus_reduce_black, R.drawable.icon_head_default};
			int[] fourType = {R.drawable.iconfont_doc, R.drawable.iconfont_dwg, R.drawable.iconfont_mp3, R.drawable.iconfont_txt, R.drawable.iconfont_jpg};
			if(layer == 4){
				viewholder.delete.setVisibility(View.VISIBLE);
				String name = nodeList.get(position).getName();
				if(name.contains(".doc") || name.contains(".DOC") || name.contains(".docx") || name.contains(".DOCX") || name.contains(".xls") || name.contains(".XLS")
						|| name.contains(".xlsx") || name.contains(".XLSX")){
					viewholder.iv_left.setImageResource(fourType[0]);
				}else if(name.contains(".dwg") || name.contains(".dwf") || name.contains(".dxf") || name.contains(".DWG") || name.contains(".DWF") || name.contains(".DXF")){
					viewholder.iv_left.setImageResource(fourType[1]);
				}else if(name.contains(".txt") || name.contains(".TXT")){
					viewholder.iv_left.setImageResource(fourType[3]);
				}else if(name.contains(".mp3") || name.contains(".avi") || name.contains(".3gp")
						||name.equals(".mp4") || name.contains(".MP3") || name.contains(".AVI") || name.contains(".3GP")
						||name.equals(".MP4")){
					viewholder.iv_left.setImageResource(fourType[2]);
				}else if(name.contains(".jpg") || name.contains(".png") || name.contains(".JPG") || name.contains(".PNG")){
					viewholder.iv_left.setImageResource(fourType[4]);
				}else{
					viewholder.iv_left.setImageResource(leftIds[nodeList.get(position).getExpandStatus()]);
				}
			}else{
				viewholder.iv_left.setImageResource(leftIds[nodeList.get(position).getExpandStatus()]);
			}
			viewholder.tv_width.setMinWidth(layer * (viewholder.iv_left.getLayoutParams().width));
			
			//2016-10-27 20:36:07
			viewholder.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(DocActivity.this);
	                builder.setIcon(R.drawable.logo_title).setTitle("删除");
	                builder.setMessage("确定删除本文档？");
	                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialogInterface, int i) {
	                    	TreeNode node = nodeList.get(position);
//	            			if (node.getExpandStatus() == 2) {		//点击末尾节点事件
	            				Mmc mmc = DataSupport.where("mmc_Id = ?", node.getId()+"").find(Mmc.class).get(0);
	            				if(mmc != null){
	            					mmc.delete();
	            				}else{
	            					Toast.makeText(DocActivity.this, "该文件不存在，请联系管理员", Toast.LENGTH_SHORT).show();
	            				}
//	            			}
	            			nodeList.remove(position);
	            			dialogInterface.dismiss();
	            			adapter.notifyDataSetChanged();
	                    }
	                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
	                builder.show();
				}
			});
			return view;
		}
		
		public final class ViewHolder{
			public ImageView iv_left;
			public TextView tv_name;
			public TextView tv_width;
			public ImageView delete;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.docmenu, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
	        case R.id.doc_fanhui:
	        	progressDialog = new ProgressDialog(this);
//	        	progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                progressDialog.setTitle("加密操作");
//                progressDialog.setIcon(R.drawable.orient_min);
                progressDialog.setMessage("数据正在加密处理中，请稍候！");
                progressDialog.setCancelable(false);               					//设置进度条是否可以按退回键取消
                progressDialog.setCanceledOnTouchOutside(false);  		//设置点击进度对话框外的区域对话框不消失
                progressDialog.show();
	        	new Thread(new Runnable() {
					@Override
					public void run() {
						//Android文件系统短期内删除后重新创建需要提前更名删除
						File file = new File(Setting.FILE_SECRET_END);
						FileOperation.RecursionDeleteFile(file);
						Message message = new Message();
						message.what = 1;
						mHandler.sendMessage(message);
					}
				}).start();
	        	//finish();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
	}
	
	/**
     * 1,启动；2更新；3关闭
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch ((msg.what)) {
                case 1:
                	progressDialog.dismiss();
                	onDestroy();
                    break;
                case 2:
                	progressDialog.dismiss();
                	String path = msg.getData().getString("path"); 
					File file = new File(path);
					boolean s = file.exists();
					ThridToolUtils.openFile(file, DocActivity.this);
                    break;
            }
        }
    };
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		finish();
	}
	
}
