package com.example.navigationdrawertest.internet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.navigationdrawertest.CustomUI.CellTypeEnum;
import com.example.navigationdrawertest.CustomUI.CellTypeEnum1;
import com.example.navigationdrawertest.adapter.SignAdapter1;
import com.example.navigationdrawertest.application.MyApplication;
import com.example.navigationdrawertest.application.OrientApplication;
import com.example.navigationdrawertest.data.AerospaceDB;
import com.example.navigationdrawertest.model.Cell;
import com.example.navigationdrawertest.model.Mmc;
import com.example.navigationdrawertest.model.Operation;
import com.example.navigationdrawertest.model.Product;
import com.example.navigationdrawertest.model.Rows;
import com.example.navigationdrawertest.model.Rw;
import com.example.navigationdrawertest.model.RwRelation;
import com.example.navigationdrawertest.model.Scene;
import com.example.navigationdrawertest.model.Signature;
import com.example.navigationdrawertest.model.Task;
import com.example.navigationdrawertest.model.UploadFileRecord;
import com.example.navigationdrawertest.model.User;
import com.example.navigationdrawertest.secret.FileEncryption;
import com.example.navigationdrawertest.utils.CalculateUtil;
import com.example.navigationdrawertest.utils.CommonTools;
import com.example.navigationdrawertest.utils.CommonUtil;
import com.example.navigationdrawertest.utils.Config;
import com.example.navigationdrawertest.utils.ConverXML;
import com.example.navigationdrawertest.utils.DateUtil;
import com.example.navigationdrawertest.utils.FileOperation;
import com.example.navigationdrawertest.utils.Setting;

/**
 * @author liu 2015-10-17 下午1:58:54
 */
public class SyncWorkThread extends Thread {
	private String errorMessage = ""; // 错误信息提示
	private AerospaceDB aerospacedb;
	private List<String> syncList = new ArrayList<String>();
	private Context context;
	private Handler handler;
	private Bitmap mSignBitmap;

	public SyncWorkThread(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	public void run() {
		super.run();
		aerospacedb = AerospaceDB.getInstance();
		// 1,更新用户信息
		downloadUsers();

		// 2-1,下载任务~人员关系表
		downloadRwUser();

		// 2,下载未完成表格清单
		// 3,下载未完成XML:downloadUnfinish
		downloadUnfinishList();

		//上传复制节点信息
		upLoadCopyInfo();
		// 4,上传待上传XML
		boolean flag = uploadTable();
		if(!flag){				//错误信息

		}
		//上传已完成状态的数据
		if (OrientApplication.getApplication().getFlag() == 1) {
			uploadDoneTable();
		}

		// 5,下载已完成表格清单
		downloadFinishList2();

		// 6，下载产品表格数据
		downloadProductList();

		// 7,下载试验队下的多媒体资料
		downloadmmcList();

		// 6,下载已完成XML
		// downloadFinish();

		// 7,保存人员关系结构表
		// getuserrwrelation();

		// 8,上传任务级别拍照
		// uploadRwPic();

		//附加
		initData();
		
		// 7,更新同步完成
		notifyCompletion();

		// 4,5,8,7
	}
	
	private List<Operation> loadOperationByCellId(String cellid, String taskid){
		List<Operation> operationList = aerospacedb.loadOperationByCellId(cellid, taskid);
		return operationList;
	}
	
	//初始化数据
	public void initData(){
		updateInformation("准备数据","初始化表格数据！");
//		List<Task> taskList = DataSupport.findAll(Task.class);
		List<Task> taskList = DataSupport.where("initStatue=?", "unfinish").find(Task.class);
		List<Rows> rowsList = DataSupport.where().find(Rows.class);
		for(Task task : taskList){
			for (int i = 1; i <= Integer.parseInt(task.getRownum()); i++) {
				List<Cell> cellList = DataSupport.where("taskid = ? and rowsid=?", task.getTaskid(), String.valueOf(i)).find(Cell.class);
				for(Cell cell : cellList){
					if(cell.getType().equals("FALSE")){			//不是检查项
						cell.setCelltype(CellTypeEnum1.LABEL);
						cell.update(cell.getId());
					}else if(cell.getType().equals("TRUE")){
						List<Operation> operationList = loadOperationByCellId(cell.getCellid(), cell.getTaskid());
						if(operationList.size() > 1){					//一个Cell,多个Operation
							boolean isPhoto = false;
							boolean isBitmap = false;
							Operation hookOperation = null, stringOperation = null;
							for(Operation op : operationList){
								if(op.getType().equals("1"))
									hookOperation = op;
								if(op.getType().equals("2"))
									stringOperation = op;
							}
//						Operation hookOperation = DataSupport.where("cellid = ? and type = ? and taskid = ?", cell.getCellid(), "1", cell.getTaskid())
//			            		.find(Operation.class).get(0);
//						Operation stringOperation = DataSupport.where("cellid = ? and type = ? and taskid = ?", cell.getCellid(), "2", cell.getTaskid())
//			            		.find(Operation.class).get(0);
							cell.setHookOperation(hookOperation);
							cell.setStringOperation(stringOperation);
							for(int k=0; k<operationList.size(); k++){
								List<Integer> pows = CalculateUtil.CalculateOperationItem(operationList.get(k).getOperationtype());
								if(pows.contains(128)){
									isPhoto = true;
								}
								if(!operationList.get(k).getSketchmap().equals("") && operationList.get(k).getSketchmap() != null){
									isBitmap = true;
								}
							}
							if(isPhoto && !isBitmap){				//打钩+填值+拍照
								cell.setCelltype(CellTypeEnum1.HOOKSTRINGPHOTO);
							}else if(!isPhoto && isBitmap){		//打钩+填值+缩略图
								cell.setCelltype(CellTypeEnum1.HOOKSTRINGBITMAP);
							}else if(isPhoto && isBitmap){		//打钩+填值+拍照+缩略图
								cell.setCelltype(CellTypeEnum1.HOOKSTRINGPHOTOBITMAP);
							}else{											//打钩+填值
								cell.setCelltype(CellTypeEnum1.HOOKSTRING);
							}
							cell.update(cell.getId());
						}else{
							boolean isBitmap = false;
							if(!operationList.get(0).getSketchmap().equals("") && operationList.get(0).getSketchmap() != null){
								isBitmap = true;
							}
							if(operationList.get(0).getType().equals("1")){		//打钩
								Operation hookOperation = DataSupport.where("cellid = ? and type = ? and taskid = ?", cell.getCellid(), "1", cell.getTaskid())
										.find(Operation.class).get(0);
								cell.setHookOperation(hookOperation);
								List<Integer> pows = CalculateUtil.CalculateOperationItem(operationList.get(0).getOperationtype());
								if(pows.contains(128) && !isBitmap){				//打钩+拍照
									cell.setCelltype(CellTypeEnum1.HOOKPHOTO);
								}else if(pows.contains(128) && isBitmap){		//打钩+拍照+缩略图
									cell.setCelltype(CellTypeEnum1.HOOKPHOTOBITMAP);
								}else if(!pows.contains(128) && isBitmap){	//打钩+缩略图
									cell.setCelltype(CellTypeEnum1.HOOKBITMAP);
								}else{
									cell.setCelltype(CellTypeEnum1.HOOK);		//打钩
								}
								cell.update(cell.getId());
							}else if(operationList.get(0).getType().equals("2")){																//填值
								Operation stringOperation = DataSupport.where("cellid = ? and type = ? and taskid = ?", cell.getCellid(), "2", cell.getTaskid())
										.find(Operation.class).get(0);
								cell.setStringOperation(stringOperation);
								List<Integer> pows = CalculateUtil.CalculateOperationItem(operationList.get(0).getOperationtype());
								if(pows.contains(128) && !isBitmap){				//填值+拍照
									cell.setCelltype(CellTypeEnum1.STRINGPHOTO);
								}else if(pows.contains(128) && isBitmap){		//填值+拍照+缩略图
									cell.setCelltype(CellTypeEnum1.STRINGPHOTOBITMAP);
								}else if(!pows.contains(128) && isBitmap){	//填值+缩略图
									cell.setCelltype(CellTypeEnum1.STRINGBITMAP);
								}else{															//填值
									cell.setCelltype(CellTypeEnum1.STRING);
								}
								cell.update(cell.getId());
							}else{
								cell.setCelltype(CellTypeEnum1.LABEL);
								cell.update(cell.getId());
							}
						}
					}else{
						cell.setCelltype(CellTypeEnum1.LABEL);
						cell.update(cell.getId());
					}
				}
			}

			task.setInitStatue("finish");
			task.update(task.getId());
		}

	}

	/**
	 * @param
	 * @return
	 * @Description: 判断当前登录人员是否为节点负责人
	 * @author qiaozhili
	 * @date 2019/2/19 8:56
	 */
	private void isCommander() {
		String userId = OrientApplication.getApplication().loginUser.getUserid();
//		List<String> commanderIds = DataSupport.find()
	}

	public boolean downMmc(Mmc mmc) {
		updateInformation("保存", mmc.getMmc_Name());

		HttpClient client = HttpClientHelper.getOrientHttpClient();
		HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
		errorMessage = "网络有误!";
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"dowmloadmmc"));
			nameValuePairs.add(new BasicNameValuePair("mmcid", mmc.getMmc_Id()
					.toString()));
			postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(postmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				errorMessage = code + "错误";
				return false;
			}
			/**
			 * 2017-1-11 09:28:08
			 * 修改前：服务端传过来时解密之后的文件
			 * 修改后：服务端传过来之后是加密的文件
			 */
//			String filePath = Environment.getExternalStorageDirectory()
//					+ File.separator + "mmc";
//			String path = Environment.getExternalStorageDirectory()
//					+ File.separator + "mmc" + File.separator + mmc.getMmc_Id()
//					+ "." + mmc.getType();
			String filePath = Setting.FILE_SECRET_START;
			String path = Setting.FILE_SECRET_START + File.separator + mmc.getMmc_Name()
					+ "." + mmc.getType();
//			String secret_end = Setting.FILE_SECRET_END + File.separator + mmc.getMmc_Id()
//					+ "." + mmc.getType();
			Header header = response.getAllHeaders()[1];
			long filelength = Long.parseLong(header.toString().substring(header.toString().lastIndexOf("=")+1));
			long downlen = 0;
			
			File file1 = new File(filePath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			HttpEntity entity = response.getEntity();
			if(entity != null){
//				InputStream is = entity.getContent();
				ByteArrayInputStream is = new ByteArrayInputStream(
						EntityUtils.toByteArray(response.getEntity()));
				File file = new File(path);// 新建一个file文件
				FileOutputStream fos = new FileOutputStream(file); // 对应文件建立输出流
				byte[] buffer = new byte[8*1024]; // 新建缓存 用来存储 从网络读取数据 再写入文件
				int len = 0;
				while ((len = is.read(buffer)) != -1) {// 当没有读到最后的时候
					downlen += len;
					if(downlen > filelength){
						int bytecount = (int) (8192-(downlen-filelength));
						fos.write(buffer, 0, bytecount);
						break;
					}else{
						fos.write(buffer, 0, len);// 将缓存中的存储的文件流秀娥问file文件	
					}
				}
				fos.flush();// 将缓存中的写入file
				fos.close();
				
				//解密文件过程
//				FileEncryption.decrypt(path, secret_end);
				
			}
			
			
//			ByteArrayInputStream is = new ByteArrayInputStream(
//					EntityUtils.toByteArray(response.getEntity()));
//
//			File file = new File(path);// 新建一个file文件
//			FileOutputStream fos = new FileOutputStream(file); // 对应文件建立输出流
//			byte[] buffer = new byte[5*1024*1024]; // 新建缓存 用来存储 从网络读取数据 再写入文件
//			int len = 0;
//			while ((len = is.read(buffer)) != -1) {// 当没有读到最后的时候
//				fos.write(buffer, 0, len);// 将缓存中的存储的文件流秀娥问file文件
//			}
//			fos.flush();// 将缓存中的写入file
//			fos.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * <mmcs> <mmc mmc_Id="" mmc_Name="" gw_Id="" displaypath_Name=""
	 * rw_Id=""></mmc> <mmc></mmc> <mmc></mmc> </mmcs>
	 * 
	 * @param xmlContent
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public boolean updatemmcList(String xmlContent) {
		if (xmlContent.equals("")) { // 没有多媒体
		// DataSupport.deleteAll(Mmc.class);
			Log.i("mmc", "没有多媒体");
			return true;
		} else {
			List<Mmc> oldmmcList = DataSupport.findAll(Mmc.class);
			DocumentBuilderFactory docBuilderFactory = null;
			DocumentBuilder docBuilder = null;
			Document doc = null;
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			try {
				docBuilder = docBuilderFactory.newDocumentBuilder();
				InputStream buffer = new ByteArrayInputStream(
						xmlContent.getBytes());
				doc = docBuilder.parse(buffer);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Element root = doc.getDocumentElement();
			NodeList nodelist = root.getElementsByTagName("mmc");
			List<Mmc> newmmcList = new ArrayList<Mmc>();
			for (int i = 0; i < nodelist.getLength(); i++) {
				Element userElement = (Element) nodelist.item(i);
				String mmc_Id = CommonTools.null2String(userElement
						.getAttribute("mmcId"));
				String mmc_Name = CommonTools.null2String(userElement
						.getAttribute("mmcName"));
				String gw_Id = CommonTools.null2String(userElement
						.getAttribute("gwId"));
				String displaypath_Name = CommonTools.null2String(userElement
						.getAttribute("displaypathName"));
				String rw_Id = CommonTools.null2String(userElement
						.getAttribute("rwId"));
				String type = CommonTools.null2String(userElement
						.getAttribute("type"));
				String task_Id = CommonTools.null2String(userElement
						.getAttribute("taskId"));
				String task_Name = CommonTools.null2String(userElement
						.getAttribute("taskName"));

				Mmc mmc = new Mmc();
				mmc.setMmc_Id(mmc_Id);
				mmc.setMmc_Name(mmc_Name);
				mmc.setRw_Id(rw_Id);
				mmc.setDisplaypath_Name(displaypath_Name);
				mmc.setGw_Id(gw_Id);
				mmc.setType(type);
				mmc.setTaskId(task_Id);
				mmc.setTaskName(task_Name);
				newmmcList.add(mmc);
			}
			// List<String> addList = new ArrayList<String>();

			// 2016-6-7 11:25:20原有逻辑
			// for(Mmc mmc : oldmmcList){
			// boolean delete = true;
			// for(Mmc mmc2 : newmmcList){
			// if(mmc.getMmc_Id().equals(mmc2.getMmc_Id())){
			// delete = false;
			// oldmmcList.remove(mmc);
			// break;
			// }
			// }
			// if(delete){
			// mmc.delete();
			// }
			// }
			// for(Mmc mmc : newmmcList){
			// boolean add = true;
			// for(Mmc mmc2 : oldmmcList){
			// if(mmc.getMmc_Id().equals(mmc2.getMmc_Id())){
			// add = false;
			// break;
			// }
			// }
			// if(add){
			// if(mmc.save()){
			// downMmc(mmc);
			// updateInformation("保存", mmc.getMmc_Name() + "---成功");
			// }else{
			// return false;
			// }
			// }
			// }

			// 按照马晶该写的逻辑

			for (Mmc mmc : newmmcList) {
				boolean flag = true;
				for (Mmc mmc1 : oldmmcList) {
					if (mmc.getMmc_Id().equals(mmc1.getMmc_Id())) { // 新的覆盖旧的
						flag = false;
						downMmc(mmc);
					}
				}
				if (flag) {
					if (mmc.save()) {
						downMmc(mmc);
						updateInformation("保存", mmc.getMmc_Name() + "---成功");
					} else {
						updateInformation("保存", mmc.getMmc_Name() + "---失败");
					}
				}
			}

			return true;
		}
	}

	public boolean downloadmmcList() {
		HttpClient client = HttpClientHelper.getOrientHttpClient();
		HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
		errorMessage = "网络有误!";
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"downloadmmclist"));
			nameValuePairs
					.add(new BasicNameValuePair("username", OrientApplication
							.getApplication().loginUser.getUsername()));
			postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(postmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				errorMessage = code + "错误";
				return false;
			}
			String xmlContent = EntityUtils.toString(response.getEntity(),
					"utf-8");
			boolean isUpdateRight = this.updatemmcList(xmlContent); // 传过来的是MMC的XML列表
			if (isUpdateRight == false) {
				return false;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * productid,productName;productid,productName
	 * 
	 * @return
	 */
	public boolean updateProductDB(String xmlContent) {
		List<Product> oldProductList = DataSupport.findAll(Product.class);
		if (xmlContent.equals("")) { // 新的内容为空则删除所有内容
			DataSupport.deleteAll(Product.class);
			return true;
		}
		String[] newProductStringList = xmlContent.split(";");
		List<Product> newProductList = new ArrayList<Product>();
		for (int i = 0; i < newProductStringList.length; i++) {
			Product pro1 = new Product();
			pro1.setProduct_Id(newProductStringList[i].split(",")[0]);
			pro1.setProduct_Name(newProductStringList[i].split(",")[1]);
			newProductList.add(pro1);
		}
		if (oldProductList.size() > 0) { // 数据库中存在Product数据
			for (Product pro : oldProductList) {
				boolean delete = true;
				for (Product pro2 : newProductList) {
					if (pro.getProduct_Id().equals(pro2.getProduct_Id())) {
						delete = false;
						break;
					}
				}
				if (delete) {
					pro.delete();
				}
			}
			for (Product pro : newProductList) {
				boolean add = true;
				for (Product pro2 : oldProductList) {
					if (pro.getProduct_Id().equals(pro2.getProduct_Id())) {
						add = false;
						break;
					}
				}
				if (add) {
					pro.save();
				}
			}
			return true;
		} else {
			for (Product pro2 : newProductList) {
				pro2.save();
			}
			return true;
		}
	}

	public boolean downloadProductList() {
		HttpClient client = HttpClientHelper.getOrientHttpClient();
		HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
		errorMessage = "网络有误!";
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"downloadproductlist"));
			nameValuePairs
					.add(new BasicNameValuePair("username", OrientApplication
							.getApplication().loginUser.getUsername()));
			postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(postmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				errorMessage = code + "错误";
				return false;
			}
			String xmlContent = EntityUtils.toString(response.getEntity(),
					"utf-8");
			boolean isUpdateRight = this.updateProductDB(xmlContent);
			if (isUpdateRight == false) {
				return false;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void notifyCompletion() {
		OrientApplication.getApplication().uploadDownloadList = syncList;
		Message msg = handler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putString("localread", "oksync");
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	/**
	 * 下载产品表分函数
	 * 
	 * @return
	 */
	private boolean updatePostDB(String xmlContent, User user) {
		if(!xmlContent.equals("")){
			String[] pro = xmlContent.split("\\?");
			for (int i = 0; i < pro.length; i++) {
				int startLocation = pro[i].lastIndexOf(",") + 2;
				int endLocation = pro[i].lastIndexOf("#");
				String str = pro[i].substring(startLocation,
						endLocation);
				String str1 = str.substring(0, str.length());
				String proId = pro[i].split("\\,")[0];
				String proName = pro[i].split("\\,")[1];
				String productId = pro[i].split("\\,")[2];
				RwRelation proRe = new RwRelation();
				proRe.setRwid(proId);
				proRe.setRwname(proName);
				proRe.setProductid(productId);
				proRe.setUserid(user.getUserid());
				proRe.setUsername(user.getUsername());
				if (!str1.equals("")) {
					proRe.setNodeId(str1);
				} else {
					proRe.setNodeId("");
				}
				syncList.add(proRe.getRwid() + "---RwRelation表保存成功");
				proRe.save();
			}
		}
		return true;
	}

	/**
	 * 2-1,下载任务~人员关系表主函数
	 */
	public boolean downloadRwUser() {
		DataSupport.deleteAll(RwRelation.class);
		HttpClient client = HttpClientHelper.getOrientHttpClient();
		HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
		errorMessage = "网络有误!";
		List<User> userList = DataSupport.findAll(User.class);
		for (User user : userList) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("operationType",
						"getuserrwrelation"));
				// nameValuePairs.add(new BasicNameValuePair("username",
				// OrientApplication.getApplication().loginUser.getUsername()));
				nameValuePairs.add(new BasicNameValuePair("username", user
						.getUsername()));
				postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = client.execute(postmethod);
				int code = response.getStatusLine().getStatusCode();
				if (code != 200) {
					errorMessage = code + "错误";
					return false; // 错误
				}
				String xmlContent = EntityUtils.toString(response.getEntity(),
						"utf-8");

				boolean isUpdateRight = this.updatePostDB(xmlContent, user);
				if (isUpdateRight == false) {
					return false;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Operation保存
	 * 
	 * @param taskoperations
	 * @param cell
	 */
	private void readTaskOperations(NodeList taskoperations, Cell cell) {
		List<Operation> operationLists = new ArrayList<Operation>();
		// for (int i = 0; i < taskoperations.getLength(); i++) {
		// Element taskOperation = (Element)taskoperations.item(i);
		// NodeList xmlOperations =
		// taskOperation.getElementsByTagName("operation");
		for (int loop = 0; loop < taskoperations.getLength(); loop++) {
			Element xmlOperation = (Element) taskoperations.item(loop);
			Operation operation = new Operation();
			operation.setOperationid(xmlOperation.getAttribute("resultid"));
			operation.setType(xmlOperation.getAttribute("type"));
			operation.setTime(xmlOperation.getAttribute("time"));
			operation.setOperationtype(xmlOperation
					.getAttribute("operationtype"));
			operation.setRealcellid(xmlOperation.getAttribute("realcellid"));
			operation.setCellid(cell.getCellid());
			// operation.setUserid(OrientApplication.getApplication().loginUser.getUserid());
			operation.setmTTID(cell.getmTTID());
			operation.setTaskid(cell.getTaskid());
			operation.setIldd(xmlOperation.getAttribute("ildd"));
			operation.setIildd(xmlOperation.getAttribute("iildd"));
			operation.setErr(xmlOperation.getAttribute("err"));
			operation.setTighten(xmlOperation.getAttribute("tighten"));
			operation.setLastaction(xmlOperation.getAttribute("lastaction"));
			operation.setIsmedia(xmlOperation.getAttribute("ismedia"));
			operation.setOpvalue(xmlOperation.getAttribute("value"));
			operation.setSketchmap(xmlOperation.getAttribute("bitmapid"));
			cell.setOpvalue(xmlOperation.getAttribute("value"));
			operationLists.add(operation);
			if (operation.save()) {
				syncList.add(operation.getOperationid() + "---Operation表保存成功");
				// cell.getOperations().add(operation);
			} else {
				syncList.add(operation.getOperationid() + "---Operation表保存失败");
			}
		}
		// }
		// cell.setOperations(operationLists);
	}

	/**
	 * Cell表保存
	 * 
	 * @param taskcells
	 */
	private void readTaskCells(NodeList taskcells, Task task) {
		List<Cell> cellList = new ArrayList<Cell>();
		Rows rows = new Rows();
		// int cellCount = 0;
		//taskcells.getLength()为rows的数量
		task.setRownum(taskcells.item(0).getChildNodes().getLength() + "");
		task.setTablesize(taskcells.getLength() + "");
		rows.setTaskid(task.getTaskid());

		for (int i = 0; i < taskcells.getLength(); i++) {
			Element rowsXml = (Element) taskcells.item(i);

			//乔志理  修改rownum
			String rowsid = rowsXml.getAttribute("rowsid");
			String rownum = rowsXml.getChildNodes().getLength() + "";
			rows.setRowsnumber(rownum);
			rows.setRowsid(rowsid);

			NodeList xmlRows = rowsXml.getElementsByTagName("row");
			for (int rowIndex = 0; rowIndex < xmlRows.getLength(); rowIndex++) {
				Element xmlRowss = (Element) xmlRows.item(rowIndex);
				String Horizontalorder = xmlRowss.getAttribute("rowid");
				Element rowXml = (Element) xmlRows.item(rowIndex);
				NodeList xmlCells = rowXml.getElementsByTagName("cell");
				task.setLinenum(xmlCells.getLength() + "");
				for (int cellIndex = 0; cellIndex < xmlCells.getLength(); cellIndex++) {
					Element xmlCell = (Element) xmlCells.item(cellIndex);
					Cell cell = new Cell();
					cell.setRowsid(rowsid);
					cell.setHorizontalorder(Horizontalorder);
					cell.setCellid(xmlCell.getAttribute("cellid"));
					cell.setRowname(xmlCell.getAttribute("column"));
					cell.setColumnid(xmlCell.getAttribute("columnid"));
					cell.setTextvalue(xmlCell.getAttribute("textvalue"));
					cell.setVerticalorder(xmlCell.getAttribute("order"));
					cell.setType(xmlCell.getAttribute("type"));
					cell.setMarkup(xmlCell.getAttribute("markup"));
					// cell.setUserid(OrientApplication.getApplication().loginUser.getUserid());
					cell.setmTTID(task.getPostinstanceid());
					cell.setTaskid(task.getTaskid());
					cell.setIshook("");
//					cell.setTablesize(task.getTablesize());
					if (xmlCell.getAttribute("type").equals("TRUE")) {
						NodeList operationsXml = xmlCell
								.getElementsByTagName("operation");
						readTaskOperations(operationsXml, cell);
					}
					cellList.add(cell);
					if (cell.save()) {
						syncList.add(cell.getCellid() + "---Cell表保存成功");
					} else {
						syncList.add(cell.getCellid() + "---Cell表保存失败");
					}
				}
			}
		}
		if (task.save()) {
			syncList.add(task.getTaskname() + "---Task表保存成功");
		} else {
			syncList.add(task.getTaskname() + "---Task表保存失败");
		}
	}

	/**
	 * Sign表保存
	 *
	 * @param tasksigns
	 * @param
	 */
	private void readTaskSigns(NodeList tasksigns, Task task, String state) {
		List<Signature> signList = new ArrayList<Signature>();
		for (int i = 0; i < tasksigns.getLength(); i++) {
			Element taskSign = (Element) tasksigns.item(i);
			NodeList xmlSigns = taskSign.getElementsByTagName("sign");
			for (int loop = 0; loop < xmlSigns.getLength(); loop++) {
				Element xmlSign = (Element) xmlSigns.item(loop);
				Signature sign = new Signature();
				String signId=xmlSign.getAttribute("signId");
				sign.setSignid(signId);
				if(state.equalsIgnoreCase("finish")){
					String path = downloadSignPhoto1(task, signId);
					if (!CommonTools.Obj2String(path).equals("")) {
						sign.setIsFinish("is");
						sign.setBitmappath(path);
						sign.update(sign.getId());
//						sign.setBitmappath("/data/com.example.navigationdrawertest/805/files/signphoto/的方法/2141/2243.jpg");
					}
				}
				sign.setSignname(xmlSign.getAttribute("name"));
				sign.setSignorder(xmlSign.getAttribute("signorder"));
				sign.setSignvalue(xmlSign.getAttribute("value"));
				sign.setTime(xmlSign.getAttribute("time"));
				sign.setSignTime(xmlSign.getAttribute("time"));
				// sign.setUserid(OrientApplication.getApplication().loginUser.getUserid());
				sign.setmTTId(task.getPostinstanceid());
				sign.setTaskid(task.getTaskid());
				signList.add(sign);
				if (sign.save()) {
						syncList.add(sign.getSignid() + "---Sign表保存成功");
						// task.getSigns().add(sign);
				} else {
						syncList.add(sign.getSignid() + "---Sign表保存失败");
				}

//				task.setSigns(signList);


			}
		}

	}

	private String createFile(String signid, String taskid) {
		ByteArrayOutputStream baos = null;
		String _path = "";
		try {
			String signphotoPath = Environment.getDataDirectory().getPath()
					+ Config.packagePath + Config.signphotoPath + "/" + taskid
					+ "/";
			_path = signphotoPath + signid + ".jpg";
			File path = new File(signphotoPath);
			if (!path.exists()) {// 目录存在返回false
				path.mkdirs();// 创建一个目录
			}
			baos = new ByteArrayOutputStream();
			mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] photoBytes = baos.toByteArray();
			if (photoBytes != null) {
				new FileOutputStream(new File(_path)).write(photoBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _path;
	}

	public Bitmap getBitmapByOpt(String picturePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picturePath, opt);
		int imgHeight = opt.outHeight;
		int imgWidth = opt.outWidth;
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int windowWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		int windowHeight = dm.heightPixels;
		int scaleX = imgWidth / windowWidth;
		int scaleY = imgHeight / windowHeight;
		int scale = 1;
		if (scaleX > scaleY & scaleY >= 1) {
			scale = scaleX;
		}
		if (scaleY > scaleX & scaleX >= 1) {
			scale = scaleY;
		}
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = scale;
		return BitmapFactory.decodeFile(picturePath, opt);
	}

	/**
	 * 保存任务--岗位--表格关系表
	 * 
	 * @param root
	 */
	private void readRw(Element root) {
		Rw rw = new Rw();
		rw.setRwid(root.getAttribute("rwid"));
		rw.setRwname(root.getAttribute("rwname"));
		rw.setPostid(root.getAttribute("postid"));
		rw.setPostname(root.getAttribute("postname"));
		rw.setTableinstanceid(root.getAttribute("tableinstanceId"));
		rw.setTablename(root.getAttribute("name"));
		rw.setUserid(OrientApplication.getApplication().loginUser.getUserid());
		rw.setUsername(OrientApplication.getApplication().loginUser
				.getUsername());
		if (rw.save()) {
			syncList.add(rw.getRwname() + "---Rw表保存成功");
		} else {
			syncList.add(rw.getRwname() + "---Rw表保存失败");
		}
	}


	private Task readTask(Element root, Task task, String state, Map<String, String> map) {
		if (state.equals("finish")) { // 已完成状态
			task.setLocation(4);
		} else { // 未完成状态
			task.setLocation(1);
		}
		task.setTaskid(root.getAttribute("tableinstanceId"));
		Log.i("表格ID", root.getAttribute("tableinstanceId"));
		task.setPath(root.getAttribute("path"));
		task.setPathId(root.getAttribute("pathId"));
		task.setVersion(root.getAttribute("version"));
		task.setRemark(root.getAttribute("remark"));
		task.setTaskname(root.getAttribute("name"));
		task.setRwid(root.getAttribute("rwid"));
		task.setRwname(root.getAttribute("rwname"));
		task.setPostname(root.getAttribute("postname"));
		task.setPostinstanceid(root.getAttribute("postinstanceid"));
		task.setNodeLeaderId(root.getAttribute("nodeLeaderId"));
		// task.setUserid(OrientApplication.getApplication().loginUser.getUserid());
		// task.setUsername(OrientApplication.getApplication().loginUser.getUsername());
		task.setStartTime(root.getAttribute("starttime"));
		task.setEndTime(root.getAttribute("endtime"));
		task.setIsfinish("false"); 						// 2016-4-15 表格是否完成标记
		task.setInitStatue("unfinish");			// 2016-7-29 09:40:27

//		task.setTablesize(root.getAttribute("tablesize"));	//表格拆分标记

		readRw(root); // 保存任务--岗位--表格关系表
		NodeList conditionsXml = root.getElementsByTagName("conditions");
		if (conditionsXml != null) {
			readTaskConditions(conditionsXml, task);
		}

		NodeList signsXml = root.getElementsByTagName("signs");
		if (signsXml != null) {
			readTaskSigns(signsXml, task, state);
//			downloadBitmap(task);
//			downloadopphoto(task);

		}

		NodeList cellsXml = root.getElementsByTagName("rows");
		if (cellsXml != null) {
			readTaskCells(cellsXml, task);
		}
		return task;
	}

	/**
	 * Condition表保存
	 * 
	 * @param taskConditions
	 */
	private void readTaskConditions(NodeList taskConditions, Task task) {
		List<Scene> conditionList = new ArrayList<Scene>();
		for (int i = 0; i < taskConditions.getLength(); i++) {
			Element taskCondition = (Element) taskConditions.item(i);
			NodeList xmlConditions = taskCondition
					.getElementsByTagName("condition");
			for (int loop = 0; loop < xmlConditions.getLength(); loop++) {
				Element xmlCondition = (Element) xmlConditions.item(loop);
				Scene condition = new Scene();
				condition.setConditionid(xmlCondition
						.getAttribute("conditionId"));
				condition.setConditionname(xmlCondition
						.getAttribute("conditionname"));
				condition.setSceneorder(Integer.parseInt(xmlCondition.getAttribute("order")));
				condition.setScenevalue(xmlCondition.getAttribute("valuename"));
				condition.setTaskid(task.getTaskid());
				// condition.setUserid(OrientApplication.getApplication().loginUser.getUserid());
				condition.setmTTID(task.getPostinstanceid());
				conditionList.add(condition);
				if (condition.save()) {
					syncList.add(condition.getConditionid()
							+ "---Condition表保存成功");
					// task.getConditions().add(condition);
				} else {
					syncList.add(condition.getConditionid()
							+ "---Condition表保存失败");
				}
			}
		}
		// task.setConditions(conditionList);
	}

	public Task invertXMLtoTask(String taskXMLContent, String state) {
		// Task retTask = new Task();
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			InputStream buffer = new ByteArrayInputStream(
					taskXMLContent.getBytes());// new
												// StringBufferInputStream(xmlContent);
			doc = docBuilder.parse(buffer);

			// tag <task>
			Element root = doc.getDocumentElement();
			/**
			 * 1，解析conditions并且保存 2，解析signs并且保存 3，解析rows并且保存
			 */
			Task task = new Task();
			Map<String, String> map = new HashMap<>();
			readTask(root, task, state, map);
			// 下载签署图片
			if (state.equals("finish")) {
//				map = downloadSignPhoto(task, root);
			}
//			readTask1(root, task, state, map);
			return task;

		} catch (Exception e) {
			return null;
		}
	}

	private boolean downloadUnfinish(String tableIDs, String version) { // tableIDs是待下载表格实例的ID，仅有一个
		// 1,gettask
		// 2,gethtml
		// 3,getpic
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			HttpResponse response;
			HttpClient getTaskClient = HttpClientHelper.getOrientHttpClient();
			HttpPost getTaskPostmethod = new HttpPost(HttpClientHelper.getURL());
			nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"gettask"));
			nameValuePairs.add(new BasicNameValuePair("taskinstanceId",
					tableIDs + ""));
			//version的格式是V1.V2。
			String versionHeader = version.split("\\.")[0];
			int version1 = Integer.parseInt(versionHeader)+1;
			String newversion = version1+".0";
//			nameValuePairs.add(new BasicNameValuePair("taskversion", Integer
//					.parseInt(version) + 1 + ""));
			nameValuePairs.add(new BasicNameValuePair("taskversion", newversion));
			getTaskPostmethod
					.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = getTaskClient.execute(getTaskPostmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				return false;
			}
			String taskXMLContent = EntityUtils.toString(response.getEntity(),
					"utf-8");
			syncList.add("2,我的任务---检查表格下载列表");
			Task downloadtask = invertXMLtoTask(taskXMLContent, "unfinish");
			if (downloadtask != null) { // 如果表格实例XML保存到数据库，就下载该表格的HTML表格
				downloadHtml(downloadtask);
				updateInformation("下载", downloadtask.getTaskname()
						+ "---html文件成功");
				downloadBitmap(downloadtask);
				updateInformation("下载", downloadtask.getTaskname()
						+ "---Bitmap文件成功");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.i("Exception", e.toString());
			return false;
		}
		return true;
	}

	// 下载HTML文件
	private boolean downloadHtml(Task task) throws ClientProtocolException,
			IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		HttpResponse response;
		HttpClient getHtmlClient = HttpClientHelper.getOrientHttpClient();
		HttpPost getHtmlPostmethod = new HttpPost(HttpClientHelper.getURL());
		nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("operationType", "gethtml"));
		nameValuePairs.add(new BasicNameValuePair("taskinstanceId", task
				.getTaskid()));
		updateInformation("下载", task.getTaskname() + "---html文件");
		errorMessage = "请求下载：\"" + task.getTaskname() + "\"的html文件出错（网络有误）";
		getHtmlPostmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		response = getHtmlClient.execute(getHtmlPostmethod);
		int code = response.getStatusLine().getStatusCode();
		if (code != 200) {
			errorMessage = "请求下载：\"" + task.getTaskname() + "\"的html文件出错("
					+ code + "错误)";
			return false; // 错误
		}
		String htmlContent = EntityUtils
				.toString(response.getEntity(), "utf-8"); // html 内容
		updateInformation("保存", task.getTaskname() + "--html文件");
		boolean bWriteOK = FileOperation.writeTaskHtml(task, htmlContent); // 写入html把原来的覆盖
		if (bWriteOK == false) {
			errorMessage = "\"" + task.getTaskname()
					+ "\"的html文件保存本地有误！（本地保存出错）";
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 解压下载照片
	 * @param zipFile
	 * @param targetDir
	 */
	private static void Unzip(String zipFile, String targetDir) {
		int BUFFER = 4096; //这里缓冲区我们使用4KB，
		String strEntry; //保存每个zip的条目名称
		try {
			BufferedOutputStream dest = null; //缓冲输出流
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry; //每个zip条目的实例
			while ((entry = zis.getNextEntry()) != null) {
				try {
					Log.i("Unzip: ","="+ entry);
					int count;
					byte data[] = new byte[BUFFER];
					strEntry = entry.getName();
					File entryFile = new File(targetDir + strEntry);
					File entryDir = new File(entryFile.getParent());
					if (!entryDir.exists()) {
						entryDir.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(entryFile);
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			zis.close();
		} catch (Exception cwj) {
			cwj.printStackTrace();
		}
	}

	private boolean downloadopphoto(Task task) {
		try {
			HttpClient client = HttpClientHelper.getOrientHttpClient();
			HttpPost postmethod = new HttpPost(
					HttpClientHelper.getURL());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					1);
			nameValuePairs.add(new BasicNameValuePair("taskid", task
					.getTaskid()));
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"downloadopphoto"));
			postmethod.setEntity(new UrlEncodedFormEntity(
					nameValuePairs, "utf-8"));
			postmethod.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			HttpResponse response = client.execute(postmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				errorMessage = code + "错误";
				return false; // 错误
			}
			updateInformation("保存", task.getTaskname() + "--检查项照片");

			String filepath = Environment.getExternalStorageDirectory() + Config.v2photoPath
					+ File.separator
					+ task.getRwid()
					+ File.separator
					+ task.getTaskid();
//					+ ".zip";
			String path = Environment.getExternalStorageDirectory() + Config.v2photoPath
					+ File.separator
					+ task.getRwid()
					+ File.separator
					+ task.getTaskid()
					+ File.separator;

			File file1 = new File(filepath);
			file1.delete();
			if (!file1.exists()) {
				file1.mkdirs();
			}

			ByteArrayInputStream is = new ByteArrayInputStream(
					EntityUtils.toByteArray(response.getEntity()));
			File file = new File(file1.getAbsolutePath(),task.getTaskid()+".zip");// 新建一个file文件
			FileOutputStream fos = new FileOutputStream(file); // 对应文件建立输出流
			byte[] buffer = new byte[1024]; // 新建缓存 用来存储 从网络读取数据 再写入文件
			int len = 0;
			while ((len = is.read(buffer)) != -1) {// 当没有读到最后的时候
				fos.write(buffer, 0, len);// 将缓存中的存储的文件流秀娥问file文件
			}
			fos.flush();// 将缓存中的写入file
			fos.close();

			Unzip(file.getAbsolutePath(), filepath + File.separator);
			file.delete();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 下载示意图照片 1，找出该表格实例下所有的CK表 2，根据CK表的SKETCHMAP选项遍历出所有的CK
	 * 3，根据CKid，ck的schemaID查找出该操作项示意图的下载位置
	 * 
	 * @param task
	 * @return
	 */
	private boolean downloadBitmap(Task task) {
		String userid = OrientApplication.getApplication().loginUser
				.getUserid();
//		List<Operation> operationList = DataSupport.where(
//				"userid = ? and taskid = ?", userid, task.getTaskid()).find(
//				Operation.class);
		List<Operation> operationList = DataSupport.where("taskid = ?", task.getTaskid()).find(
				Operation.class);
		List<String> sketchList = new ArrayList<String>();
		for (int i = 0; i < operationList.size(); i++) {
			String sketchmark = operationList.get(i).getSketchmap();
			if (!sketchmark.equals("") && sketchmark != null) {
				sketchList.add(operationList.get(i).getSketchmap());
			}
		}
		if (sketchList.size() != 0) {
			try {
				for (int loop = 0; loop < sketchList.size(); loop++) {
					String opId = sketchList.get(loop);
					updateInformation("下载示意图", "iD为" + opId + "示意图开始下载");
					HttpClient client = HttpClientHelper.getOrientHttpClient();
					HttpPost postmethod = new HttpPost(
							HttpClientHelper.getURL());
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs.add(new BasicNameValuePair("taskid", task
							.getTaskid()));
					nameValuePairs.add(new BasicNameValuePair("opid", opId));
					nameValuePairs.add(new BasicNameValuePair("operationType",
							"downloadopbitmap"));
					postmethod.setEntity(new UrlEncodedFormEntity(
							nameValuePairs, "utf-8"));
					postmethod.setHeader("Content-Type",
							"application/x-www-form-urlencoded; charset=utf-8");
					HttpResponse response = client.execute(postmethod);
					int code = response.getStatusLine().getStatusCode();
					if (code != 200) {
						errorMessage = code + "错误";
						return false; // 错误
					}
					// 根据文档Id获取文档信息
					// String picContent =
					// EntityUtils.toString(response.getEntity(), "utf-8");
					updateInformation("保存", task.getTaskname() + "--示意图照片");
					// boolean bWriteOK = FileOperation.writeOpPhoto(task, opId,
					// picContent);
					// if (bWriteOK == false) {
					// errorMessage = "\"" + opId + "\"的示意图照片保存本地有误！（本地保存出错）";
					// return false;
					// }

					// String filepath =
					// Environment.getDataDirectory().getPath() +
					// Config.packagePath
					// + Config.opphotoPath+ "/"+ task.getUserid()+"/" +
					// task.getTaskid() + "/";
					// String path = Environment.getDataDirectory().getPath() +
					// Config.packagePath
					// + Config.opphotoPath+ "/"+ task.getUserid()+"/" +
					// task.getTaskid() + "/" + opId + ".jpg";
					
					//2016-6-12 15:15:06
//					String filepath = Environment.getDataDirectory().getPath()
//							+ Config.packagePath + Config.opphotoPath + "/"
//							+ task.getPostname() + "/" + task.getTaskid() + "/";
//					String path = Environment.getDataDirectory().getPath()
//							+ Config.packagePath + Config.opphotoPath + "/"
//							+ task.getPostname() + "/" + task.getTaskid() + "/"
//							+ opId + ".jpg";
					String filepath = Environment.getExternalStorageDirectory()
							+ Config.opphotoPath + File.separator
							+ task.getTaskid()+File.separator;
					String path = Environment.getExternalStorageDirectory()
							+ Config.opphotoPath + File.separator
							+ task.getTaskid() + File.separator
							+ opId + ".jpg";

					File file1 = new File(filepath);
					if (!file1.exists()) {
						file1.mkdirs();
					}
					ByteArrayInputStream is = new ByteArrayInputStream(
							EntityUtils.toByteArray(response.getEntity()));
					File file = new File(path);// 新建一个file文件
					FileOutputStream fos = new FileOutputStream(file); // 对应文件建立输出流
					byte[] buffer = new byte[1024]; // 新建缓存 用来存储 从网络读取数据 再写入文件
					int len = 0;
					while ((len = is.read(buffer)) != -1) {// 当没有读到最后的时候
						fos.write(buffer, 0, len);// 将缓存中的存储的文件流秀娥问file文件
					}
					fos.flush();// 将缓存中的写入file
					fos.close();
				}
			} catch (UnsupportedEncodingException e) {
				return false;
			} catch (ClientProtocolException e) {
				return false;
			} catch (IOException e) {
				return false;
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	private boolean downloadUnfinishList() {
		List<Task> UnfinishList = new ArrayList<Task>(); // 服务端发送的下载表格XML信息转化成Table的list集合
		try {
			errorMessage = "请求待下载任务id、version、path、name出错(网络有误)";
			HttpClient client = HttpClientHelper.getOrientHttpClient();
			HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"gettasklist"));
			nameValuePairs
					.add(new BasicNameValuePair("username", OrientApplication
							.getApplication().loginUser.getUsername()));
			postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response;
			response = client.execute(postmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				errorMessage = "请求待下载任务id、version、path、name出错(" + code + "错误)";
				return false; // 错误
			}
			errorInformation("同步待下载表格", "信号码：" + code);
			String rev = EntityUtils.toString(response.getEntity(), "UTF-8"); // 列表Id
			/**
			 * rev实例
			 * 1154,1,火箭系统流程项目->控制系统产品交接分系统项目r1r1r1r1r1->I级状态检查项目r1r1(11月11日
			 * --11月11日)->岗位一,检查表格测试实例测试082202?
			 * 1155,1,火箭系统流程项目->控制系统产品交接分系统项目r1r1r1r1r1
			 * ->I级状态检查项目r1r1(11月11日--11月11日)->岗位一,检查表格测试实例测试082203
			 */
			Log.i("ret", rev);
			// 1,根据rev转化为Map<String , String> 表格ID:version
			String[] ServerTableString = rev.split("\\?");
			if (!rev.equalsIgnoreCase("")) {
				for (int i = 0; i < ServerTableString.length; i++) {
					Task tempTable = new Task();
					tempTable.setTaskid(ServerTableString[i].split(",")[0]);
					tempTable.setVersion(ServerTableString[i].split(",")[1]);
					tempTable.setPath(ServerTableString[i].split(",")[2]);
					tempTable.setPathId(ServerTableString[i].split(",")[3]);
					UnfinishList.add(tempTable);
				}
			}

			List<String> ServerTableIds = new ArrayList<String>(); // 服务器端发送的需要下载的表格id集合
			if (UnfinishList.size() != 0) {
				for (int i = 0; i < UnfinishList.size(); i++) {
					ServerTableIds.add(UnfinishList.get(i).getTaskid());
				}
				// for(String str : ServerTableString){
				// ServerTableIds.add(str.split(",")[0]);
			}
			// 1-0,先根据用户id查找出本地是否存在该用户的表格数据
			// List<Task> localTaskList = DataSupport.where("userid = ?",
			// OrientApplication.getApplication().loginUser.getUserid()).find(Task.class);
			// 1-1，获取服务端的数量
			if (rev.equalsIgnoreCase("")) { // 同步时，该用户没有任何可下载的未完成表格

			} else {
				// 2,获取本地location为1,2,3的table
				List<Task> tableList = new ArrayList<Task>();
				// tableList =
				// aerospacedb.getNotFinishTables(OrientApplication.getApplication().loginUser.getUserid());
				tableList = aerospacedb.getAllTables(OrientApplication
						.getApplication().loginUser.getUserid());
				// 3,比较服务器端和pad端table，服务端有就添加到下载列表，服务端没有就添加到删除列表
				List<String> addList = new ArrayList<String>(); // 下载表格ID清单
				List<String> deleteList = new ArrayList<String>(); // 删除表格ID清单
				List<String> normalList = new ArrayList<String>(); // 中立表格ID清单
				Boolean Client = true;
				Boolean Server = true;
				List<Task> addTaskList = new ArrayList<Task>();
				List<Task> deleteTaskList = new ArrayList<Task>();

				// 4，判断该用户本地数据库存不存在已经下载的表格
				if (tableList.size() != 0) { // 该用户本地数据库存在
					// 该用户终端存在未完成表格，需要判断终端是否存在该ID，并且服务端版本号大于终端版本号就下载
					// 4-1，如果服务端存在，终端不存在就下载判断版本号，服务端版本号大的就下载，否则就不操作
					// 4-2，如果服务端存在，终端
					for (int i = 0; i < UnfinishList.size(); i++) {
						Task serverTask = UnfinishList.get(i);
						boolean flag = true;
						for (int j = 0; j < tableList.size(); j++) {
							Task clientTask = tableList.get(j);
							if (serverTask.getTaskid().equals(
									clientTask.getTaskid())) { // 表格ID相同
								boolean isServer = false;				//版本是否有小数点
								boolean isClient = false;
								String serverVersion1 = "";
								String serverVersion2 = "";
								String clientVersion1 = "";
								String clientVersion2 = "";
								if(serverTask.getVersion().contains(".")){
									isServer = true;
								}
								if(clientTask.getVersion().contains(".")){
									isClient = true;
								}
								String[] serverVersionArray = serverTask.getVersion().split("\\.");
								serverVersion1 = serverVersionArray[0];			//大版本号
								if(isServer)
									serverVersion2 = serverVersionArray[1];			//小版本号
								else
									serverVersion2 = "0";
								String[] clientVersionArray = clientTask.getVersion().split("\\.");
								clientVersion1 = clientVersionArray[0];
								if(isClient)
									clientVersion2 = clientVersionArray[1];
								else
									clientVersion2 = "0";
								
								if (Integer.parseInt(serverVersion1) > Integer
										.parseInt(clientVersion1)) { // 服务端版本号高，就下载
									deleteTaskList.add(clientTask);
									break;
								} else if (Integer.parseInt(serverVersion1) == Integer
										.parseInt(clientVersion1)
										&& clientTask.getLocation() == 4) {
									deleteTaskList.add(clientTask);
									break;
								} else {
									flag = false;
									break;
								}
							}
						}
						if (flag) {
							addTaskList.add(serverTask);
						}
					}

					for (Task task : deleteTaskList)
						deleteList.add(task.getTaskid());
					for (Task task : addTaskList)
						addList.add(task.getTaskid());

				} else { // 该用户本地数据库不存在，则说明是第一次下载，需要全部下载
				// addList = ServerTableIds;
					addTaskList = UnfinishList;
				}
				// 5,删除table id为删除列表的table
				for (String deleteint : deleteList) {

					DataSupport.deleteAll(Task.class, "taskid = ?", deleteint);
					DataSupport.deleteAll(Signature.class, "taskid = ?", deleteint);
					DataSupport.deleteAll(Cell.class, "taskid = ?", deleteint);
					DataSupport.deleteAll(Operation.class, "taskid = ?", deleteint);
					DataSupport.deleteAll(Scene.class, "taskid = ?", deleteint);
					DataSupport.deleteAll(Rw.class, "tableinstanceid = ?", deleteint);
				}
				// 6,添加table id为下载列表的table
				// for(String addint : addList){
				// downloadUnfinish(addint);
				// }
				for (Task task : addTaskList) {
					downloadUnfinish(task.getTaskid(), task.getVersion());
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.i("Exception", e.toString());
			return false;
		}
		return true;
	}

	private boolean downloadUsers() {
		// 0.需要先删除所有的人员信息
		DataSupport.deleteAll(User.class);
		// 1.重新下载所有的人员信息
		HttpClient client = HttpClientHelper.getOrientHttpClient();
		HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"getUsers"));
			postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(postmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				errorMessage = code + "错误";
				return false;
			}
			String xmlContent = EntityUtils.toString(response.getEntity(),
					"utf-8");
			syncList.add("1,用户信息同步列表");
			if (!updateUsers(xmlContent)) {
				return false;
			}
			OrientApplication.getApplication().loginUser = DataSupport
					.where("username = ?",
							OrientApplication.getApplication().loginUser
									.getUsername()).find(User.class).get(0);
			// List<User> userlist = DataSupport.where("username = ?",
			// OrientApplication.getApplication().loginUser.getUsername()).find(User.class);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean updateUsers(String xmlContent) {
		aerospacedb.deleteAllUsers();
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;

		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			InputStream buffer = new ByteArrayInputStream(xmlContent.getBytes());
			doc = docBuilder.parse(buffer);
			Element root = doc.getDocumentElement();
			NodeList nodelist = root.getElementsByTagName("user");
			for (int index = 0; index < nodelist.getLength(); index++) {
				Element userElement = (Element) nodelist.item(index);
				User user = new User();
				user.setUserid(userElement.getAttribute("id"));
				user.setUsername(userElement.getAttribute("username"));
				user.setPassword(userElement.getAttribute("password"));
				user.setDisplayname(userElement.getAttribute("displayname"));
				user.setTtidandname(userElement.getAttribute("ttidandname"));
				user.setCommanderId(userElement.getAttribute("commanderId"));
				// user.setLiangzong(userElement.getAttribute("liangzong"));
				// user.setPostsString(userElement.getAttribute("post"));
				// if(user.save()){
				// Log.i("AllUsers", user.getUsername());
				// }
				if (aerospacedb.saveUser(user)) {
					Log.i("AllUsers", user.getUsername());
				}
				syncList.add(user.getUsername());
			}
		} catch (Exception e) {
			errorMessage = errorMessage + "--解析user信息入库出错!";
			return false;
		}
		return true;
	}


	// 4,上传待上传XML
	private boolean uploadTable() {
		String userid = OrientApplication.getApplication().loginUser
				.getUserid();
		// String rwid = OrientApplication.getApplication().rw.getRwid();
		String location = "3";
		try {
			// 确定表格实例三要素：人员-任务-位置
			List<Task> taskList = DataSupport.where("location = ?", location).find(Task.class);
			for (int i = 0; i < taskList.size(); i++) {
				Task task = taskList.get(i);
				HttpClient client = HttpClientHelper.getOrientHttpClient();
				HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
				// 上传三步骤
				// 1，上传表格实例数据
				// 2，上传表格实例中存在的照片
				// 3，上传表格实例生成的HTML网页
				// 上传步骤一
				if (task == null) {
					errorMessage = "检查表格Id号为：'" + task.getTaskid() + "'值为NULL。";
					return false;
				}
				String taskxml = ConverXML.ConverTaskToXml(task);

				// 上传步骤三
				String taskHtml = CommonUtil.ConverHtmlToString(task);
				if ("".equals(taskHtml)) {
					errorMessage = "检查表格Id号为：'" + task.getTaskid()
							+ "'上传失败！(读取本地html出错)";
					return false;
				}

//				else{
//					//上传步骤六，删除已经上传的图片
//				}

				String script = "<script type=\"text/javascript\">	function showImage(imageFile,type){ 		if(window.showImageObj==undefined || window.showImageObj==null)		{			window.browser(imageFile,type);		}		else		{			window.showImageObj.clickOnAndroid(imageFile,type);		} 	}</script>";
				String html = taskHtml.contains("<body>") ? (script + taskHtml
						.split("<body>")[1].split("</body>")[0]) : taskHtml;
				Log.i("html", task.getTaskname() + "OK");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("operationType",
						"uploadtask")); // 上传表格实例的内容和表格实例的HTML
				nameValuePairs.add(new BasicNameValuePair("tableInstanId", task
						.getTaskid()));
				nameValuePairs.add(new BasicNameValuePair("taskContent",
						taskxml));
				nameValuePairs.add(new BasicNameValuePair("htmlContent", html));
				nameValuePairs.add(new BasicNameValuePair("broTaskId", task.getBroTaskId()));
				postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"utf-8"));
				Log.i("setEntity", task.getTaskname() + "OK");
				postmethod.setHeader("Content-Type",
						"application/x-www-form-urlencoded; charset=utf-8");
				Log.i("setHeader", task.getTaskname() + "OK");
				HttpResponse response;
				response = client.execute(postmethod);
				Log.i("execute", task.getTaskname() + "OK");
				int code = response.getStatusLine().getStatusCode();
				Log.i("response", task.getTaskname() + code + "OK");
				if (code != 200) {
					errorMessage = "上传:\"" + task.getTaskname()
							+ "\"的html和xml文件出错！（" + code + "错误）";
					return false; // 错误
				}
				String uploadResponseContent = EntityUtils.toString(response.getEntity(), "utf-8");

				// 上传步骤四
				if(!uploadSignPhoto(task.getTaskid())){
					Log.i("photo", task.getTaskid()+"签署照片");
					return false;
				}
				// 上传步骤五
				if (!uploadOpPhoto(task)) // 先执行上传相片
				{
					syncList.add(task.getTaskid() + "---失败，检查项照片");
					Log.i("photo", task.getTaskid()+"检查项照片");
//					return false;
				}

				if(uploadResponseContent.equals("true")){
					task.setLocation(4);
					task.save();
					Log.i("location", task.getTaskname() + "上传成功！");
				}else{
					updateInformation("上传表格失败", "表格ID为："+task
							.getTaskid()+"的表格上传失败！");
				}
				
				// 表格实例上传成功之后的操作
				// 1，把该表格的location设置为4
				
				// String taskid = task.getTaskid();
				// if(task.save()){
				
				// DataSupport.deleteAll(Signature.class, "taskid = ?",
				// task.getTaskid());
				// DataSupport.deleteAll(Cell.class, "taskid = ?",
				// task.getTaskid());
				// DataSupport.deleteAll(Operation.class, "taskid = ?",
				// task.getTaskid());
				// DataSupport.deleteAll(Task.class, "taskid = ?",
				// task.getTaskid());
				// // DataSupport.deleteAll(Rw.class, "tableinstanceid = ?",
				// task.getTaskid());
				// // DataSupport.deleteAll(Signature.class, "taskid = ?",
				// taskid);
				// // DataSupport.deleteAll(Cell.class, "taskid = ?", taskid);
				// // DataSupport.deleteAll(Operation.class, "taskid = ?",
				// taskid);
				// // DataSupport.deleteAll(Task.class, "taskid = ?", taskid);

				// 删除形式
				// String deleteint = task.getTaskid();
				// DataSupport.deleteAll(Task.class,
				// "taskid = ? and userid = ?", deleteint,
				// OrientApplication.getApplication().loginUser.getUserid());
				// DataSupport.deleteAll(Signature.class,
				// "taskid = ? and userid = ?", deleteint,
				// OrientApplication.getApplication().loginUser.getUserid());
				// DataSupport.deleteAll(Cell.class, "taskid = ?", deleteint);
				// DataSupport.deleteAll(Operation.class,
				// "taskid = ? and userid = ?", deleteint,
				// OrientApplication.getApplication().loginUser.getUserid());
				// DataSupport.deleteAll(Rw.class, "tableinstanceid = ?",
				// deleteint);
				// }
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			Log.i("ClientProtocolException", e.toString());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Log.i("IOException", e.toString());
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.i("Exception", e.toString());
			return false;
		}
		return true;
	}

	/**
	 * @param
	 * @return
	 * @Description: 上传复制的节点信息
	 * @author qiaozhili
	 * @date 2019/3/9 11:29
	 */
	private boolean upLoadCopyInfo() {
		String location = "3";
		List<Task> taskList = DataSupport.where("location = ?", location).find(Task.class);
		List<String> pathList = new ArrayList<>();
		for (int i = 0; i < taskList.size(); i++) {
			Task task = taskList.get(i);
			if (task.getIsBrother() == 1 && !pathList.contains(task.getPath())) {
				pathList.add(task.getPath());
				//上传pad端复制的节点信息
				upLoadPackageInfo(task);
			}
		}
		pathList.clear();
		return true;
	}

	private boolean upLoadPackageInfo(Task task) {
		String broTaskId = task.getBroTaskId();
		List<Task> taskList = DataSupport.where("taskid=?", broTaskId).find(Task.class);
		String broPathId = "";
		if (taskList.size() >= 0) {
			broPathId = taskList.get(0).getPathId();
		}
		try {
			HttpClient client = HttpClientHelper.getOrientHttpClient();
			HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("operationType", "uploadpackageinfo"));
			nameValuePairs.add(new BasicNameValuePair("path", task.getPath()));
			nameValuePairs.add(new BasicNameValuePair("pathId", task.getPathId()));
			nameValuePairs.add(new BasicNameValuePair("broPathId", broPathId));
			nameValuePairs.add(new BasicNameValuePair("broTaskId", broTaskId));
			nameValuePairs.add(new BasicNameValuePair("taskId", task.getTaskid()));
			postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			postmethod.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			HttpResponse response;
			errorMessage = "上传:\"" + task.getTaskname() + "\"的html和xml文件出错！（网络有误）";

			response = client.execute(postmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
                errorMessage = "上传:\"" + task.getTaskname() + "\"的html和xml文件出错！（" + code + "错误）";
                return false; // 错误
            }

			String uploadResponseContent = EntityUtils.toString(response.getEntity(), "utf-8");
			if(uploadResponseContent.equals("true")){
//                task.setLocation(4);
//                task.save();
//                Log.i("location", task.getTaskname() + "上传成功！");
            }else{
                updateInformation("上传表格失败", "表格ID为："+task.getTaskid()+"的表格上传失败！");
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	// 管理员操作，上传已完成表单实例及其相关的检查项照片数据
	private boolean uploadDoneTable() {
		String userid = OrientApplication.getApplication().loginUser
				.getUserid();
		// String rwid = OrientApplication.getApplication().rw.getRwid();
		String location = "4";
		try {
			// 确定表格实例三要素：人员-任务-位置
			List<Task> taskList = DataSupport.where("location = ?", location).find(Task.class);

			for (int i = 0; i < taskList.size(); i++) {
				Task task = taskList.get(i);

				HttpClient client = HttpClientHelper.getOrientHttpClient();
				HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
				// 上传三步骤
				// 1，上传表格实例数据
				// 2，上传表格实例中存在的照片
				// 3，上传表格实例生成的HTML网页
				// 上传步骤一
				if (task == null) {
					errorMessage = "检查表格Id号为：'" + task.getTaskid() + "'值为NULL。";
					return false;
				}
				String taskxml = ConverXML.ConverTaskToXml(task);

				// 上传步骤三
				String taskHtml = CommonUtil.ConverHtmlToString(task);
				if ("".equals(taskHtml)) {
					errorMessage = "检查表格Id号为：'" + task.getTaskid()
							+ "'上传失败！(读取本地html出错)";
					return false;
				}
				// 上传步骤四
				if(!uploadSignPhoto(task.getTaskid())){
					Log.i("photo", task.getTaskid()+"签署照片");
					return false;
				}
				// 上传步骤五
				if (!uploadOpPhoto(task)) // 先执行上传相片
				{
					syncList.add(task.getTaskid() + "---失败，检查项照片");
					Log.i("photo", task.getTaskid()+"检查项照片");
//					return false;
				}

				String script = "<script type=\"text/javascript\">	function showImage(imageFile,type){ 		if(window.showImageObj==undefined || window.showImageObj==null)		{			window.browser(imageFile,type);		}		else		{			window.showImageObj.clickOnAndroid(imageFile,type);		} 	}</script>";
				String html = taskHtml.contains("<body>") ? (script + taskHtml
						.split("<body>")[1].split("</body>")[0]) : taskHtml;
				Log.i("html", task.getTaskname() + "OK");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("operationType",
						"uploadtask")); // 上传表格实例的内容和表格实例的HTML
				nameValuePairs.add(new BasicNameValuePair("tableInstanId", task
						.getTaskid()));
				nameValuePairs.add(new BasicNameValuePair("taskContent",
						taskxml));
				nameValuePairs.add(new BasicNameValuePair("htmlContent", html));
				postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"utf-8"));
				Log.i("setEntity", task.getTaskname() + "OK");
				postmethod.setHeader("Content-Type",
						"application/x-www-form-urlencoded; charset=utf-8");
				Log.i("setHeader", task.getTaskname() + "OK");
				HttpResponse response;
				errorMessage = "上传:\"" + task.getTaskname()
						+ "\"的html和xml文件出错！（网络有误）";
				response = client.execute(postmethod);
				Log.i("execute", task.getTaskname() + "OK");
				int code = response.getStatusLine().getStatusCode();
				Log.i("response", task.getTaskname() + code + "OK");
				if (code != 200) {
					errorMessage = "上传:\"" + task.getTaskname()
							+ "\"的html和xml文件出错！（" + code + "错误）";
					return false; // 错误
				}
				String uploadResponseContent = EntityUtils.toString(response.getEntity(), "utf-8");
				if(uploadResponseContent.equals("true")){
					task.setLocation(4);
					task.save();
					Log.i("location", task.getTaskname() + "上传成功！");
				}else{
					updateInformation("上传表格失败", "表格ID为："+task
							.getTaskid()+"的表格上传失败！");
				}
				OrientApplication.getApplication().setFlag(0);
			}

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			Log.i("ClientProtocolException", e.toString());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Log.i("IOException", e.toString());
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.i("Exception", e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 上传操作项级别照片
	 * 
	 * @param task
	 * @return
	 */
	private boolean uploadOpPhoto(Task task) {
		// 1,找到该表格实例照片目录
		updateInformation("上传表格", "上传操作项拍照！");

		//2017-7-3 15:36:14
		String path = Environment.getExternalStorageDirectory() + Config.v2photoPath 
				+ File.separator
				+ task.getRwid()
//				+ OrientApplication.getApplication().rw.getRwid()
				+ File.separator 
				+ task.getTaskid()
				+ File.separator;

		List<String> presultList = new ArrayList<String>();
//		List<String> vresultList = new ArrayList<String>();
		FileOperation.findFiles(path, ".jpg", presultList);
//		FileOperation.findFiles(path, ".mp4", vresultList);
		String username = OrientApplication.getApplication().loginUser
				.getUsername();
		String userid = OrientApplication.getApplication().loginUser
				.getUserid();
		String tableId = task.getTaskid();
		updateInformation("上传", task.getTaskid() + "的表格操作项照片！");
		// 2,先传照片
		if (presultList.size() > 0) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");// HH:mm:ss
				Date date = new Date(System.currentTimeMillis());
				String uniqueNum = simpleDateFormat.format(date);
				// 表格级别照片命名规则
				for (int i = 0; i < presultList.size(); i++) {
					String photopath = presultList.get(i);
					HttpClient client = HttpClientHelper.getOrientHttpClient();
					// HttpPost postmethod = new
					// HttpPost(HttpClientHelper.getURL());
					int startLocation = photopath.lastIndexOf("/") + 1;
					int endLocation = photopath.lastIndexOf(".");
					String phName = photopath.substring(startLocation,
							endLocation);

					//获取opid
					String newStr = photopath.substring(0, startLocation-2);
					int start2Location = newStr.lastIndexOf("/")+1;
					String opId = photopath.substring(start2Location,startLocation-1);
					
					//新建一条文件记录
					UploadFileRecord uploadFile = new UploadFileRecord();
					uploadFile.setmFileName(phName);
					uploadFile.setmFilePath(photopath);

					String str = "http://"
							+ OrientApplication.getApplication().setting.IPAdress
							+ ":"
							+ OrientApplication.getApplication().setting.PortAdress
							+ "/dp/datasync/sync.do?operationType=uploadopphoto&username="
							+ username + "&userid=" + userid + "&photoName="
							+ phName + "&tableInstanId=" + tableId
							+ "&operationId=" + opId + "&describe=" + uniqueNum;
					HttpPost postmethod = new HttpPost(str);

					File file = new File(photopath);
					MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
					ContentBody cbFile = new FileBody(file);
					mpEntity.addPart("userfile", cbFile); // <input type="file"
//					mpEntity.addPart("d",cbFile);										// name="userfile"
															// /> 对应的
					postmethod.setEntity(mpEntity);

					HttpResponse response;
					errorMessage = "上传\""
							+ CommonTools.null2String(OrientApplication.getApplication().rw.getRwname())
							+ "\"的表格（Id=" + tableId + "）对应的拍照出错！（网络有误）";
					response = client.execute(postmethod);
					int code = response.getStatusLine().getStatusCode();
					if (code != 200) {
						uploadFile.setmState("false");
						uploadFile.save();
						errorMessage = "上传\""
								+ CommonTools.null2String(OrientApplication.getApplication().rw
										.getRwname()) + "\"的表格（Id=" + tableId
								+ "）对应的拍照出错！（" + code + "错误）";
						syncList.add(errorMessage);
						return false;
					}else{
						uploadFile.setmState("true");
						uploadFile.save();
					}
					postmethod = null;
				}
			} catch (UnsupportedEncodingException e) {
				syncList.add(task.getTaskid() + "---失败1，检查项照片"+e.toString());
				return false;
			} catch (ClientProtocolException e) {
				syncList.add(task.getTaskid() + "---失败2，检查项照片"+e.toString());
				return false;
			} catch (IOException e) {
				syncList.add(task.getTaskid() + "---失败3，检查项照片"+e.toString());
				return false;
			} catch (Exception e) {
				syncList.add(task.getTaskid() + "---失败4，检查项照片"+e.toString());
				return false;
			}

		}
		return true;
	}

	// 5,下载已完成表格清单
	private boolean downloadFinishList() {
		List<Task> finishList = new ArrayList<Task>(); // 服务端发送的已完成表格XML信息转化成Table的list集合
		try {
			errorMessage = "请求待下载已完成任务id出错(网络有误)";
			HttpClient client = HttpClientHelper.getOrientHttpClient();
			HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"getfinishedtasklist"));
			nameValuePairs
					.add(new BasicNameValuePair("username", OrientApplication
							.getApplication().loginUser.getUsername()));
			postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response;
			response = client.execute(postmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				errorMessage = "请求待下载已完成任务id出错(" + code + "错误)";
				return false; // 错误
			}
			String rev = EntityUtils.toString(response.getEntity(), "UTF-8"); // 获取已完成列表Id、Path、Name
			Log.i("ret", rev);

			// 1,根据rev转化为Map<String , String> 表格ID:version
			String[] ServerTableString = rev.split("\\?");
			if (!rev.equalsIgnoreCase("")) {
				for (int i = 0; i < ServerTableString.length; i++) {
					Task tempTable = new Task();
					tempTable.setTaskid(ServerTableString[i].split(",")[0]);
					tempTable.setVersion(ServerTableString[i].split(",")[1]);
					tempTable.setPath(ServerTableString[i].split(",")[2]);
					finishList.add(tempTable);
				}
			}

			List<String> ServerTableIds = new ArrayList<String>(); // 服务器端发送的需要下载的表格id集合
			for (String str : ServerTableString) {
				ServerTableIds.add(str.split(",")[0]);
			}
			// 1-0,先根据用户id查找出本地是否存在该用户的表格数据
			// List<Task> localTaskList = DataSupport.where("userid = ?",
			// OrientApplication.getApplication().loginUser.getUserid()).find(Task.class);
			// 1-1，获取服务端的数量
			if (rev.equalsIgnoreCase("")) { // 同步时，该用户没有任何可下载的已完成表格
				// 2,获取本地location为4的table(用于显示删除内容)
				aerospacedb
						.getUploadTables(OrientApplication.getApplication().loginUser
								.getUserid());
				// 3,删除本地location为4的table
//				DataSupport.deleteAll(Task.class,
//						"location = ? and userid = ?", "4", OrientApplication
//								.getApplication().loginUser.getUserid());
				DataSupport.deleteAll(Task.class, "location = ?", "4");
			} else {
				// 2,获取本地location为1,2,3的table
				List<Task> tableList = new ArrayList<Task>();
				tableList = aerospacedb.getUploadTables(OrientApplication
						.getApplication().loginUser.getUserid());
				// 3,比较服务器端和pad端table，服务端有就添加到下载列表，服务端没有就添加到删除列表
				List<String> addList = new ArrayList<String>(); // 下载表格ID清单
				List<String> deleteList = new ArrayList<String>(); // 删除表格ID清单
				List<String> normalList = new ArrayList<String>(); // 中立表格ID清单
				Boolean Client = true;
				Boolean Server = true;

				// 4，判断该用户本地数据库存不存在已经下载的表格
				if (tableList.size() != 0) { // 该用户本地数据库存在
					if (null == tableList || tableList.size() == 0) { // pad端没有任何数据
						for (int i = 0; i < ServerTableIds.size(); i++) {
							addList.add(ServerTableIds.get(i));
						}
					} else {
						for (Task table : tableList) {
							for (String str : ServerTableIds) {
								if (table.getTaskid().equals(str)) {
									Client = true;
									normalList.add(str);
									break;
								} else {
									Client = false;
								}
							}
							if (!Client) {
								deleteList.add(table.getTaskid());
							}
						}
						for (String str : ServerTableIds) {
							for (Task table : tableList) {
								if (str.equals(table.getTaskid())) {
									Server = true;
									break;
								} else {
									Server = false;
								}
							}
							if (!Server) {
								addList.add(str);
							}
						}
					}

					// 4,比较剩余的版本号，服务端版本号高的将ID添加到删除列表，将ID添加到下载列表
					List<Task> normalTable = new ArrayList<Task>();
					long[] longIds = new long[normalList.size()];
					for (int i = 0; i < normalList.size(); i++) {
						longIds[i] = Long.parseLong(normalList.get(i) + "");
					}
					if (longIds.length > 0) {

						normalTable = DataSupport.findAll(Task.class, longIds);
						for (Task ClientTable : normalTable) {
							for (Task ServerTable : finishList) {
								if (ClientTable.getTaskid().equals(
										ServerTable.getTaskid())) {
									if (!ServerTable.getVersion().equals(
											ClientTable.getVersion())) {
										addList.add(ServerTable.getTaskid());
										deleteList.add(ClientTable.getTaskid());
									}
								}
							}
						}

					}
				} else { // 该用户本地数据库不存在，则说明是第一次下载，需要全部下载
					addList = ServerTableIds;
				}
				// 5,删除table id为删除列表的table
				for (String deleteint : deleteList) {
					DataSupport.deleteAll(Task.class, "taskid = ?", deleteint);
					DataSupport.deleteAll(Signature.class, "taskid = ?",
							deleteint);
					DataSupport.deleteAll(Cell.class, "taskid = ?", deleteint);
					DataSupport.deleteAll(Operation.class, "taskid = ?",
							deleteint);
					DataSupport.deleteAll(Scene.class, "taskid = ?", deleteint);
					DataSupport.deleteAll(Rw.class, "tableinstanceid = ?",
							deleteint);
				}
				// 6,添加table id为下载列表的table
				for (String addint : addList) {
					downloadFinish(addint);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.i("Exception", e.toString());
			return false;
		}
		return true;
	}

	// 5,下载已完成表格清单
	private boolean downloadFinishList2() {
		List<Task> finishList = new ArrayList<Task>(); // 服务端发送的已完成表格XML信息转化成Table的list集合
		try {
			errorMessage = "请求待下载已完成任务id出错(网络有误)";
			HttpClient client = HttpClientHelper.getOrientHttpClient();
			HttpPost postmethod = new HttpPost(HttpClientHelper.getURL());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"getfinishedtasklist"));
			nameValuePairs
					.add(new BasicNameValuePair("username", OrientApplication
							.getApplication().loginUser.getUsername()));
			postmethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response;
			response = client.execute(postmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				errorMessage = "请求待下载已完成任务id出错(" + code + "错误)";
				return false; // 错误
			}
			String rev = EntityUtils.toString(response.getEntity(), "UTF-8"); // 获取已完成列表Id、Path、Name
			Log.i("ret", rev);

			// 1,根据rev转化为Map<String , String> 表格ID:version
			String[] ServerTableString = rev.split("\\?");
			if (!rev.equalsIgnoreCase("")) {
				for (int i = 0; i < ServerTableString.length; i++) {
					Task tempTable = new Task();
					tempTable.setTaskid(ServerTableString[i].split(",")[0]);
					tempTable.setVersion(ServerTableString[i].split(",")[1]);
					tempTable.setPath(ServerTableString[i].split(",")[2]);
					finishList.add(tempTable);
				}
			}

			List<String> ServerTableIds = new ArrayList<String>(); // 服务器端发送的需要下载的表格id集合
			for (String str : ServerTableString) {
				ServerTableIds.add(str.split(",")[0]);
			}
			// 1-0,先根据用户id查找出本地是否存在该用户的表格数据
			// List<Task> localTaskList = DataSupport.where("userid = ?",
			// OrientApplication.getApplication().loginUser.getUserid()).find(Task.class);
			// 1-1，获取服务端的数量
			if (rev.equalsIgnoreCase("")) { // 同步时，该用户没有任何可下载的已完成表格
				//	乔志理注销   显示已上传栏目表单
//				List<Task> deleteTask = aerospacedb
//						.getUploadTables(OrientApplication.getApplication().loginUser
//						.getUserid());
//				for (Task task : deleteTask) {
//					String deleteint = task.getTaskid();
//
//					DataSupport.deleteAll(Task.class, "taskid = ?", deleteint);
//					DataSupport.deleteAll(Signature.class, "taskid = ?", deleteint);
//					DataSupport.deleteAll(Cell.class, "taskid = ?", deleteint);
//					DataSupport.deleteAll(Operation.class, "taskid = ?", deleteint);
//					DataSupport.deleteAll(Scene.class, "taskid = ?", deleteint);
//					DataSupport.deleteAll(Rw.class, "tableinstanceid = ?", deleteint);
//				}
			} else {
				// 2,获取本地location为1,2,3的table
				List<Task> tableList = new ArrayList<Task>();
				tableList = aerospacedb.getUploadTables(OrientApplication
						.getApplication().loginUser.getUserid());
				// 3,比较服务器端和pad端table，服务端有就添加到下载列表，服务端没有就添加到删除列表
				List<String> addList = new ArrayList<String>(); // 下载表格ID清单
				List<String> deleteList = new ArrayList<String>(); // 删除表格ID清单
				List<String> normalList = new ArrayList<String>(); // 中立表格ID清单
				Boolean Client = true;
				Boolean Server = true;
				List<Task> addTaskList = new ArrayList<Task>();
				List<Task> deleteTaskList = new ArrayList<Task>();

				// 4，判断该用户本地数据库存不存在已经下载的表格
				if (tableList.size() != 0) {

					for (int i = 0; i < finishList.size(); i++) {
						Task serverTask = finishList.get(i);
						boolean flag = true;
						for (int j = 0; j < tableList.size(); j++) {
							Task clientTask = tableList.get(j);
							if (serverTask.getTaskid().equals(
									clientTask.getTaskid())) { // 表格ID相同
							// if(Integer.parseInt(serverTask.getVersion())>Integer.parseInt(clientTask.getVersion())){
							// //服务端版本号高，就下载
							// deleteTaskList.add(clientTask);
							// break;
							// }else{
								flag = false;
								break;
								// }
							}
						}
						if (flag) {
							addTaskList.add(serverTask);
						}
					}
					for (Task task : deleteTaskList)
						deleteList.add(task.getTaskid());
					for (Task task : addTaskList)
						addList.add(task.getTaskid());

				} else { // 该用户本地数据库不存在，则说明是第一次下载，需要全部下载
				// addList = ServerTableIds;
					addTaskList = finishList;
				}
				// 5,删除table id为删除列表的table
				for (String deleteint : deleteList) {

					 DataSupport.deleteAll(Task.class, "taskid = ?",
					 deleteint);
					 DataSupport.deleteAll(Signature.class, "taskid = ?",
					 deleteint);
					 DataSupport.deleteAll(Cell.class, "taskid = ?",
					 deleteint);
					 DataSupport.deleteAll(Operation.class, "taskid = ?",
					 deleteint);
					 DataSupport.deleteAll(Rw.class, "tableinstanceid = ?",
					 deleteint);
				}
				// 6,添加table id为下载列表的table
				// for(String addint : addList){
				// downloadFinish(addint);
				// }
				for (Task task : addTaskList) {
					downloadFinish(task.getTaskid());
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.i("Exception", e.toString());
			return false;
		}
		return true;
	}

	// 6,下载已完成XML
	private boolean downloadFinish(String tableIDs) {
		// 1,gettask
		// 2,gethtml
		// 3,getpic
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			HttpResponse response;
			HttpClient getTaskClient = HttpClientHelper.getOrientHttpClient();
			HttpPost getTaskPostmethod = new HttpPost(HttpClientHelper.getURL());
			nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("operationType",
					"getfinishedtask"));
			nameValuePairs.add(new BasicNameValuePair("taskinstanceId",
					tableIDs + ""));
			getTaskPostmethod
					.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = getTaskClient.execute(getTaskPostmethod);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				return false;
			}
			String taskXMLContent = EntityUtils.toString(response.getEntity(),
					"utf-8");
			syncList.add("6,我的任务---检查表格下载列表");
//			invertXMLtoTask(taskXMLContent, "finish");

			Task downloadtask = invertXMLtoTask(taskXMLContent, "finish");
			if (downloadtask != null) {
				downloadHtml(downloadtask);
				downloadopphoto(downloadtask);
				updateInformation("下载", downloadtask.getTaskname()
						+ "---检查项照片成功");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.i("Exception", e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 任务级别拍照
	 * 
	 * @return
	 */
	private boolean uploadRwPic() {
		// 1,找到该任务照片目录
		String userId = OrientApplication.getApplication().loginUser
				.getUserid();
//		String path = Environment.getDataDirectory().getPath() + "/"
//				+ Config.rwphotoPath + "/" + userId + "/"
//				+ OrientApplication.getApplication().rw.getRwid() + "/";
		String path = Environment.getDataDirectory().getPath() + "/"
				+ Config.rwphotoPath + "/" 
				+ CommonTools.null2String(OrientApplication.getApplication().rw.getRwid()) + "/";
		String photoName = CommonUtil.searchFile(path);
		String username = OrientApplication.getApplication().loginUser
				.getUsername();
		String userid = OrientApplication.getApplication().loginUser
				.getUserid();
		try {
			// updateInformation("上传", task.getTaskname() + "的拍照！");
			// 表格级别照片命名规则
			if (!"".equals(photoName)) {
				for (String phName : photoName.split("?")) {
					HttpClient client = HttpClientHelper.getOrientHttpClient();
					HttpPost postmethod = new HttpPost(
							HttpClientHelper.getURL());
					String resultId = phName.split("-")[1];
					String photopath = path + phName;
					String photoContent = FileOperation.readPicture(photopath);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							3);
					nameValuePairs.add(new BasicNameValuePair("operationType",
							"uploadpic"));
					nameValuePairs.add(new BasicNameValuePair("username",
							username));
					nameValuePairs
							.add(new BasicNameValuePair("userid", userid));
					nameValuePairs.add(new BasicNameValuePair("photoName",
							phName));
					nameValuePairs.add(new BasicNameValuePair("resultId",
							resultId));
					nameValuePairs.add(new BasicNameValuePair("picContent",
							photoContent));
					postmethod.setEntity(new UrlEncodedFormEntity(
							nameValuePairs, "utf-8"));
					postmethod.setHeader("Content-Type",
							"application/x-www-form-urlencoded; charset=utf-8");
					HttpResponse response;
					errorMessage = "上传\""
							+ CommonTools.null2String(OrientApplication.getApplication().rw.getRwname())
							+ "\"的任务（Id=" + resultId + "）对应的拍照出错！（网络有误）";
					response = client.execute(postmethod);
					int code = response.getStatusLine().getStatusCode();
					if (code != 200) {
						errorMessage = "上传\""
								+ CommonTools.null2String(OrientApplication.getApplication().rw
										.getRwname()) + "\"的任务（Id=" + resultId
								+ "）对应的拍照出错！（" + code + "错误）";
						return false;
					}
					postmethod = null;
				}
			}
		} catch (UnsupportedEncodingException e) {
			return false;
		} catch (ClientProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 上传表格签署照片
	 * 
	 * @param taskId
	 *            表格ID
	 * @return
	 */
	public boolean uploadSignPhoto(String taskId) {
		// 1,找到该任务照片目录
		String userId = OrientApplication.getApplication().loginUser
				.getUserid();
		String signphotoPath = Environment.getDataDirectory().getPath()
				+ Config.packagePath + Config.signphotoPath 
				+ "/" + taskId + "/";
		String photoName = "";
		List<String> photoNameList = GetFiles(signphotoPath, "jpg");
		String username = OrientApplication.getApplication().loginUser
				.getUsername();
		String userid = OrientApplication.getApplication().loginUser
				.getUserid();
		if (photoNameList.size() != 0) {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");// HH:mm:ss
			Date date = new Date(System.currentTimeMillis());
			String uniqueNum = simpleDateFormat.format(date);
			try {
				updateInformation("上传", taskId + "的表格签署照片！");

				// 表格级别照片命名规则
				for (int i = 0; i < photoNameList.size(); i++) {
					String phName = photoNameList.get(i);
					int startLocation = phName.lastIndexOf("/") + 1;
					int endLocation = phName.lastIndexOf(".");
					String resultId = phName.substring(startLocation,
							endLocation);
					String photopath = phName;
					HttpClient client = HttpClientHelper.getOrientHttpClient();
					String str = "http://"
							+ OrientApplication.getApplication().setting.IPAdress
							+ ":"
							+ OrientApplication.getApplication().setting.PortAdress
							+ "/dp/datasync/sync.do?operationType=uploadsignphoto&username="
							+ username + "&userid=" + userid + "&resultId="
							+ resultId + "&tableInstanId=" + taskId + "&describe=" + uniqueNum;
					HttpPost postmethod = new HttpPost(str);

					File file = new File(photopath);
					MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
					ContentBody cbFile = new FileBody(file);
					mpEntity.addPart("userfile", cbFile); // <input type="file"
															// name="userfile"
															// /> 对应的
					postmethod.setEntity(mpEntity);

					HttpResponse response = client.execute(postmethod);

					int code = response.getStatusLine().getStatusCode();
					if (code != 200) {
						errorMessage = "上传\""
								+ CommonTools.null2String(OrientApplication.getApplication().rw
										.getRwname()) + "\"的任务（Id=" + resultId
								+ "）对应的拍照出错！（" + code + "错误）";
						return false;
					}
					postmethod = null;
				}

			} catch (UnsupportedEncodingException e) {
				return false;
			} catch (ClientProtocolException e) {
				return false;
			} catch (IOException e) {
				return false;
			} catch (Exception e) {
				return false;
			}

		}
		return true;
	}

	public static List<String> GetFiles(String Path, String Extension) // 搜索目录，扩展名，是否进入子文件夹
	{
		List<String> lstFile = new ArrayList<String>(); // 结果 List
		File[] files = new File(Path).listFiles();

		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				if (f.getPath()
						.substring(f.getPath().length() - Extension.length())
						.equals(Extension)) // 判断扩展名
				{
					lstFile.add(f.getPath());
				}
			}
		}
		return lstFile;
	}

	/**
	 * 下载签署图片  下载签署图片需要userId和taskId
	 * 
	 * @param
	 * @param root
	 * @return
	 */
	public Map<String, String> downloadSignPhoto(Task task, Element root) {
		Map<String, String> map = new HashMap<>();
		String userId = OrientApplication.getApplication().loginUser
				.getUserid();
//		List<Signature> signList = DataSupport.where(
//				"userid = ? and taskid = ?", userId, task.getTaskid()).find(
//				Signature.class);
//		List<Signature> signList = DataSupport.where(
//				"taskid = ?", task.getTaskid()).find(
//				Signature.class);

		NodeList signsXml = root.getElementsByTagName("signs");
		List<String > signList =new ArrayList<>();
		for (int i = 0; i < signsXml.getLength(); i++) {
			Element taskSign = (Element) signsXml.item(i);
			NodeList xmlSigns = taskSign.getElementsByTagName("sign");
			for (int loop = 0; loop < xmlSigns.getLength(); loop++) {
				Element xmlSign = (Element) xmlSigns.item(loop);

				signList.add(loop,xmlSign.getAttribute("signId"));
			}
		}

		if (signList.size() != 0) {
			try {
				for (int loop = 0; loop < signList.size(); loop++) {
					String signId = signList.get(loop);
					HttpClient client = HttpClientHelper.getOrientHttpClient();
					HttpPost postmethod = new HttpPost(
							HttpClientHelper.getURL());
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs.add(new BasicNameValuePair("taskid", task
							.getTaskid()));
					nameValuePairs
							.add(new BasicNameValuePair("signid", signId));
					nameValuePairs.add(new BasicNameValuePair("operationType",
							"downloadsignphoto"));
					postmethod.setEntity(new UrlEncodedFormEntity(
							nameValuePairs, "utf-8"));
					postmethod.setHeader("Content-Type",
							"application/x-www-form-urlencoded; charset=utf-8");
					HttpResponse response = client.execute(postmethod);
					int code = response.getStatusLine().getStatusCode();
					if (code != 200) {
						errorMessage = code + "错误";
						return map; // 错误
					}
					// 根据文档Id获取文档信息
					// String picContent =
					// EntityUtils.toString(response.getEntity(), "utf-8");
					updateInformation("保存", task.getTaskname() + "--签署照片");
					// boolean bWriteOK = FileOperation.writeSignPhoto(task,
					// signId, picContent);
					// if (bWriteOK == false) {
					// errorMessage = "\"" + task.getTaskname() +
					// "\"的签署照片保存本地有误！（本地保存出错）";
					// return false;
					// }
					// // else{
					// // return true;
					// // }
					// String filepath =
					// Environment.getDataDirectory().getPath() +
					// Config.packagePath
					// + Config.signphotoPath+ "/"+ task.getUserid()+"/" +
					// task.getTaskid() + "/";
					// String path = Environment.getDataDirectory().getPath() +
					// Config.packagePath
					// + Config.signphotoPath+ "/"+ task.getUserid()+"/" +
					// task.getTaskid() + "/" + signId + ".jpg";
					String filepath = Environment.getDataDirectory().getPath()
							+ Config.packagePath + Config.signphotoPath + "/"
							+ task.getPostname() + "/" + task.getTaskid() + "/";
					String path = Environment.getDataDirectory().getPath()
							+ Config.packagePath + Config.signphotoPath + "/"
							+ task.getPostname() + "/" + task.getTaskid() + "/"
							+ signId + ".jpg";

					map.put(signId, path);
					File file1 = new File(filepath);
					if (!file1.exists()) {
						file1.mkdirs();
					}
					ByteArrayInputStream is = new ByteArrayInputStream(
							EntityUtils.toByteArray(response.getEntity()));
					File file = new File(path);// 新建一个file文件
					FileOutputStream fos = new FileOutputStream(file); // 对应文件建立输出流
					byte[] buffer = new byte[1024]; // 新建缓存 用来存储 从网络读取数据 再写入文件
					int len = 0;
					while ((len = is.read(buffer)) != -1) {// 当没有读到最后的时候
						fos.write(buffer, 0, len);// 将缓存中的存储的文件流秀娥问file文件
					}
					fos.flush();// 将缓存中的写入file
					fos.close();
				}
			} catch (UnsupportedEncodingException e) {
				return map;
			} catch (ClientProtocolException e) {
				return map;
			} catch (IOException e) {
				return map;
			} catch (Exception e) {
				return map;
			}
		}
		return map;
	}

	/**
	 * 下载签署图片  下载签署图片需要userId和taskId
	 *
	 * @param
	 * @param
	 * @return
	 */
	public String downloadSignPhoto1(Task task, String signId) {
		String path="";
		if (true) {
			try {
					HttpClient client = HttpClientHelper.getOrientHttpClient();
					HttpPost postmethod = new HttpPost(
							HttpClientHelper.getURL());
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs.add(new BasicNameValuePair("taskid", task
							.getTaskid()));
					nameValuePairs
							.add(new BasicNameValuePair("signid", signId));
					nameValuePairs.add(new BasicNameValuePair("operationType",
							"downloadsignphoto"));
					postmethod.setEntity(new UrlEncodedFormEntity(
							nameValuePairs, "utf-8"));
					postmethod.setHeader("Content-Type",
							"application/x-www-form-urlencoded; charset=utf-8");
					HttpResponse response = client.execute(postmethod);
					int code = response.getStatusLine().getStatusCode();
					if (code != 200) {
						errorMessage = code + "错误";
						return path; // 错误
					}
					// 根据文档Id获取文档信息
					// String picContent =
					// EntityUtils.toString(response.getEntity(), "utf-8");
					updateInformation("保存", task.getTaskname() + "--签署照片");

					String filepath = Environment.getDataDirectory().getPath()
							+ Config.packagePath + Config.signphotoPath
							+ "/" + task.getTaskid() + "/";
					path = Environment.getDataDirectory().getPath()
							+ Config.packagePath + Config.signphotoPath
							+ "/" + task.getTaskid() + "/"
							+ signId + ".jpg";

					File file1 = new File(filepath);
					if (!file1.exists()) {
						file1.mkdirs();
					}
					ByteArrayInputStream is = new ByteArrayInputStream(
							EntityUtils.toByteArray(response.getEntity()));
					File file = new File(path);// 新建一个file文件
					FileOutputStream fos = new FileOutputStream(file); // 对应文件建立输出流
					byte[] buffer = new byte[1024]; // 新建缓存 用来存储 从网络读取数据 再写入文件
					int len = 0;
					while ((len = is.read(buffer)) != -1) {// 当没有读到最后的时候
						fos.write(buffer, 0, len);// 将缓存中的存储的文件流秀娥问file文件
					}
					fos.flush();// 将缓存中的写入file
					fos.close();
				createFile(signId, task.getTaskid());

			} catch (UnsupportedEncodingException e) {
				return path;
			} catch (ClientProtocolException e) {
				return path;
			} catch (IOException e) {
				return path;
			} catch (Exception e) {
				return path;
			}
		}
		return path;
	}

	// 同步信息实时传递
	private void updateInformation(String title, String Msg) {
		Message msg = handler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putString("UPDATE PORGRESS", title);
		bundle.putString("INFORMATION", Msg);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	// 错误信息实时传递
	private void errorInformation(String title, String Msg) {
		Message msg = handler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putString("LOCALREAD", "ERROR");
		bundle.putString("ERRORTITLE", title);
		bundle.putString("ERRORINFORMATION", Msg);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

}
