package com.example.navigationdrawertest.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;

import com.example.navigationdrawertest.model.Task;

import android.os.Environment;
import android.util.Base64;

public class FileOperation {

	/**
	 * 搜索目录，扩展名，是否进入子文件夹
	 *
	 * @param path
	 * @param jpg
	 * @param isIterative
	 * @return
	 */
	public static ArrayList<String> getAlbumByPath(String path, String jpg, String png) {

		ArrayList<String> lstFile = new ArrayList<String>();                //结果 List
//		File[] files = new File(path).listFiles();
		File file1 = new File(path);
		File[] files = file1.listFiles();
		if (!file1.exists()) {
			file1.mkdirs();
		}
		if (files == null)
			return lstFile;
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				String pictureType = f.getPath().substring(f.getPath().length() - jpg.length());
				if (pictureType.equals(jpg) || pictureType.equals(png)) {
					lstFile.add(f.getPath());
				}
			}
		}
		return lstFile;
	}

	public static ArrayList<String> getAlbumVideoByPath(String path, String mp4, String avi, String FLV) {

		ArrayList<String> lstFile = new ArrayList<String>();                //结果 List
//		File[] files = new File(path).listFiles();
		File file1 = new File(path);
		File[] files = file1.listFiles();
		if (!file1.exists()) {
			file1.mkdirs();
		}
		if (files == null)
			return lstFile;
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				String pictureType = f.getPath().substring(f.getPath().length() - mp4.length());
				if (pictureType.equals(avi) || pictureType.equals(FLV) || pictureType.equals(mp4)) {
					lstFile.add(f.getPath());
				}
			}
		}
		return lstFile;
	}

	public static ArrayList<String> getVideoByPath(String path, String mp4, String avi, String FLV) {

		ArrayList<String> lstFile = new ArrayList<String>();                //结果 List
//		File[] files = new File(path).listFiles();
		File file1 = new File(path);
		File[] files = file1.listFiles();
		if (!file1.exists()) {
			file1.mkdirs();
		}
		if (files == null)
			return lstFile;
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				String pictureType = f.getPath().substring(f.getPath().length() - mp4.length());
				if (pictureType.equals(mp4) || pictureType.equals(avi) || pictureType.equals(FLV)) {
					lstFile.add(f.getPath());
				}
			}
		}
		return lstFile;
	}
	
	public static boolean fileWriter(String filePath, String fileContent) {
		try {
			// ---old code start ---
			// File file = new File(filePath);
			// FileOutputStream fos = new FileOutputStream(file);
			// ---old code end ---
			File file = null;
			if (!filePath.equals("") || filePath != null) {
				file = new File(filePath);
				if (!file.exists()) {
					// 创建目录
					File fileDir = new File(file.getParent());
					fileDir.mkdirs();
					System.out.println("上层文件夹： " + fileDir);

//					File fileDir = new File(file.getPath());
//					fileDir.mkdirs();
					try {
						file.createNewFile();// 在已有文件路径上直接创建文件
						System.out.println("文件名称：" + file);
					} catch (IOException e) {
						System.out.println("创建文件失败：" + e.getLocalizedMessage());
						e.printStackTrace();
					}
				} else {
					System.out.println("【" + filePath + "】：" + "该文件已经存在");
				}
			}

			FileOutputStream fos = new FileOutputStream(file);

			fos.write(fileContent.getBytes("UTF-8"));
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String fileReader(String filePath) {
		File file = new File(filePath);

		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			byte[] buff = new byte[1024];

			int hasRead = 0;
			while ((hasRead = fis.read(buff)) > 0) {
				os.write(buff, 0, hasRead);
				// sb.append(new String(buff, 0, hasRead,"UTF-8"));
			}
			String ret = new String(os.toByteArray(), "UTF-8");

			fis.close();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean writePicture(String filepath, String path, String picContent) {
		if (picContent == null) // 图像数据为空
			return false;
		try {
			File file = new File(filepath);
			if(!file.exists()){
				file.mkdirs();
			}
			byte[] b = Base64.decode(picContent.getBytes("UTF-8"),
					Base64.DEFAULT);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据/
					b[i] += 256;
				}
			}
			// 生成jpg图片
			OutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String readPicture(String path) {
		String content = null;
		try {
			FileInputStream fileForInput = new FileInputStream(path);
			byte[] bytes = new byte[fileForInput.available()];
			fileForInput.read(bytes);
			byte[] bytesEncode = Base64.encode(bytes, Base64.DEFAULT);
			content = new String(bytesEncode, "UTF-8");// 具体的编码方法
			fileForInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public static Boolean coppyFile(String sourcePath, String descPath) {
		Boolean flag = false;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {// 创建源文件路径【sourcePath】
			// 【从源文件中读取数据的数据流 fis】
			fis = new FileInputStream(sourcePath);
		} catch (FileNotFoundException e) {
			System.out.println("系统找不到源文件，请确认以下路径是否正确： " + sourcePath);
			e.printStackTrace();
			return flag;
		}
		File file = new File(descPath);
		if (!file.exists()) {// 如果目标路径不存在
			createFiles(descPath);
		}
		try {// 创建目录文件【descPath】
			// 【写入目标文件中的数据流 fos】
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("目标文件创建失败：" + descPath);
			// e.printStackTrace();
			return flag;
		}
		byte buffer[] = new byte[1024];
		try {
			while (fis.read(buffer) != -1) {
				fos.write(buffer);
			}
			flag = true;
			System.out.println("恭喜你!文件拷贝成");
		} catch (IOException e) {
			System.out.println("文件写入失败：" + e.getLocalizedMessage());
			// e.printStackTrace();
			return flag;
		}
		// 关闭输入输出流
		try {
			fos.close();
			fis.close();
		} catch (IOException e) {
			System.out.println("关闭输入输出流: " + e.getLocalizedMessage());
			// e.printStackTrace();
			return flag;
		}
		return flag;
	}

	/**
	 * 非根路径中创建文件，即多层文件夹是 前提是: path必须一个正确的路径 拷贝单个文件
	 * 
	 * @param path文件
	 */
	public static Boolean createFiles(String path) {
		Boolean flag = false;
		File file = null;
		if (!path.equals("") || path != null) {
			file = new File(path);
			if (!file.exists()) {
				// 创建目录
				File fileDir = new File(file.getParent());
				fileDir.mkdirs();
				System.out.println("上层文件夹： " + fileDir);
				try {
					flag = file.createNewFile();// 在已有文件路径上直接创建文件
					System.out.println("文件名称：" + file);
				} catch (IOException e) {
					System.out.println("创建文件失败：" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			} else {
				System.out.println("【" + path + "】：" + "该文件已经存在");
			}
		}
		return flag;
	}
	
	public static boolean createDir(String path){
		File file = new File(path);
        if (!file.exists()) {
            try {
                //按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
            file.mkdirs();
        }
        return true;
	}

	public static boolean deleteFile(String path) {

		File bdeleteFile = new File(path);
		if (bdeleteFile.exists()) {
			return bdeleteFile.delete();
		}
		return true;

	}

	public static void deleteFolder(File file) {
		if (file == null) {
			return;
		}
		if (file.isFile()) {
			file.delete();
			return;
		}

		File[] files = file.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			if (f.isDirectory()) {
				deleteFolder(f);
			} else {
				f.delete();
			}
		}
	}
	
	/**
     * 递归删除文件和文件夹
     * @param file    要删除的根目录
     */
    public static void RecursionDeleteFile(File file){
        if(file.isFile()){
        	File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
        	file.renameTo(to);
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
            	File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
            	file.renameTo(to);
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
    
    public static boolean writeTaskHtml(Task task, String htmlContent) {
		// 调用一院一部源代码

    	String fileAbsPath = Environment.getDataDirectory().getPath() + Config.packagePath
				+ Config.htmlPath+ "/"+ task.getPostname()+"/" + task.getTaskid();
		return HtmlHelper.writeTaskHtml(fileAbsPath, htmlContent);
	}
    
    public static boolean writeSignPhoto(Task task, String signId, String picContent){

    	String filepath = Environment.getDataDirectory().getPath() + Config.packagePath
				+ Config.signphotoPath+ "/"+ task.getPostname()+"/" + task.getTaskid() + "/";
    	String path = Environment.getDataDirectory().getPath() + Config.packagePath
				+ Config.signphotoPath+ "/"+ task.getPostname()+"/" + task.getTaskid() + "/" + signId + ".jpg";
    	return FileOperation.writePicture(filepath, path, picContent);
    }
    
    
    public static boolean writeOpPhoto(Task task, String opId, String picContent){

    	String filepath = Environment.getDataDirectory().getPath() + Config.packagePath
				+ Config.opphotoPath+ "/"+ task.getPostname()+"/" + task.getTaskid() + "/";
    	String path = Environment.getDataDirectory().getPath() + Config.packagePath
				+ Config.opphotoPath+ "/"+ task.getPostname()+"/" + task.getTaskid() + "/" + opId + ".jpg";
    	return FileOperation.writePicture(filepath, path, picContent);
    }
    
    
    
    /**
     * 
     * @param baseDirName  查找的文件夹路径
     * @param targetFileName		需要查找的文件名
     * @param fileList	查找到的文件集合
     * Image   Video   Thumbnail
     */
    public static void findFiles(String baseDirName, String targetFileName, List<String> fileList){
    	
    	String tempName = null;
    	File baseDir = new File(baseDirName);
    	if(!baseDir.exists() || !baseDir.isDirectory()){
    		System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
    	}else{
    		String[] filelist = baseDir.list();
    		for(int i=0; i<filelist.length; i++){
    			File readFile = new File(baseDirName + "/" +filelist[i]);
    			if(!readFile.isDirectory()){
    				tempName = readFile.getName();
    				if(wildcardMatch(targetFileName, tempName)){
    					//匹配成功，将文件名添加到结果集
    					fileList.add(readFile.getAbsoluteFile().toString());
    				}
    			}else if(readFile.isDirectory() && !readFile.getName().equals("Thumbnail")){
    				findFiles(baseDirName + "/"+ filelist[i], targetFileName, fileList);
    			}
    		}
    	}
    }
    
    
    /**
     * 本方法只查找缩略图文件
     * @param baseDirName
     * @param targetFileName
     * @param fileList
     */
    public static void findThumbnailFiles(String baseDirName, String targetFileName, List<String> fileList){
    	String tempName = null;
    	File baseDir = new File(baseDirName);
    	if(!baseDir.exists() || !baseDir.isDirectory()){
    		System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
    	}else{
    		String[] filelist = baseDir.list();
    		for(int i=0; i<filelist.length; i++){
    			File readFile = new File(baseDirName + "/" +filelist[i]);
    			if(!readFile.isDirectory()){
    				tempName = readFile.getName();
    				if(wildcardMatch(targetFileName, tempName)){
    					//匹配成功，将文件名添加到结果集
    					fileList.add(readFile.getAbsoluteFile().toString());
    				}
    			}else if(readFile.isDirectory()){
    				findFiles(baseDirName + "/"+ filelist[i], targetFileName, fileList);
    			}
    		}
    	}
    }
    
    /**
     * 通配符匹配
     * @param pattern 通配符模式
     * @param str	待匹配的字符串
     * @return 匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str){
    	if(str.contains(pattern)){
    		return true;
    	}else{
    		return false;
    	}
//    	int patternLength = pattern.length();
//    	int strLength = str.length();
//    	int strIndex = 0;
//    	char ch;
//    	for(int patternIndex = 0; patternIndex<patternLength; patternIndex++){
//    		ch = pattern.charAt(patternIndex);
//    		if(ch == '*'){		//通配符为*表示可以匹配任意多个字符
//    			while(strIndex < strLength){
//    				if(wildcardMatch(pattern.substring(patternIndex + 1), 
//    						str.substring(strIndex)))
//    					return true;
//    				strIndex++;
//    			}
//    		}else if(ch == '?'){	  //通配符为？表示匹配任意一个字符
//    			strIndex++;
//    			if(strIndex > strLength){
//    				return false;
//    			}
//    		}else{
//    			if((strIndex >= strLength) || (ch != str.charAt(strIndex))){
//    				return false;
//    			}
//    			strIndex++;
//    		}
//    	}
//    	return (strIndex == strLength);
    }
    
    
    public static boolean saveMmc(String docName, String filePath, String path, String content)
	{
		if (content == null) // 图像数据为空
			return false;
		try {
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
//			byte[] b = Base64.decode(content.getBytes("UTF-8"),
//					Base64.DEFAULT);
			byte[] b = content.getBytes("UTF-8");
//			for (int i = 0; i < b.length; ++i) {
//				if (b[i] < 0) {// 调整异常数据/
//					b[i] += 256;
//				}
//			}
			OutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
    
    public static byte[] readInputStream(InputStream inputStream) {
    	 
	    // 1.建立通道对象
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    // 2.定义存储空间
	    byte[] buffer = new byte[1024];
	    // 3.开始读文件
	    int len = -1;
	    try {
	      if (inputStream != null) {
	        while ((len = inputStream.read(buffer)) != -1) {
	          // 将Buffer中的数据写到outputStream对象中
	          outputStream.write(buffer, 0, len);
	        }
	      }
	      // 4.关闭流
	      outputStream.close();
	      inputStream.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return outputStream.toByteArray();
  }
    
    /**
	 * 将String类型转换为byte[]
	 * @param docContent
	 * @return
	 */
	public static byte[] stringToByte(String docContent)
	{
		try {
			return Base64.decode(docContent.getBytes("UTF-8"),Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	
//	public static void postFile() throws ParseException, IOException{
//	    Closeable HttpClient httpClient = HttpClients.createDefault();
//	    try{
//	        // 要上传的文件的路径
//	        String filePath =newString("F:/pic/001.jpg");
//	        // 把一个普通参数和文件上传给下面这个地址 是一个servlet
//	        HttpPost httpPost =newHttpPost(
//	                "http://localhost:8080/xxx/xxx.action");
//	        // 把文件转换成流对象FileBody
//	        File file =newFile(filePath);
//	        FileBody bin =newFileBody(file); 
//	        StringBody uploadFileName =newStringBody(
//	                "把我修改成文件名称", ContentType.create(
//	                        "text/plain", Consts.UTF_8));
//	        //以浏览器兼容模式运行，防止文件名乱码。 
//	           HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
//	                .addPart("uploadFile", bin)//uploadFile对应服务端类的同名属性<File类型>
//	                .addPart("uploadFileName", uploadFileName)//uploadFileName对应服务端类的同名属性<String类型>
//	                .setCharset(CharsetUtils.get("UTF-8")).build();
//	 
//	        httpPost.setEntity(reqEntity);
//	 
//	        System.out.println("发起请求的页面地址 "+ httpPost.getRequestLine());
//	        // 发起请求 并返回请求的响应
//	        CloseableHttpResponse response = httpClient.execute(httpPost);
//	        try{
//	            System.out.println("----------------------------------------");
//	            // 打印响应状态
//	            System.out.println(response.getStatusLine());
//	            // 获取响应对象
//	            HttpEntity resEntity = response.getEntity();
//	            if(resEntity !=null) {
//	                // 打印响应长度
//	                System.out.println("Response content length: "
//	                        + resEntity.getContentLength());
//	                // 打印响应内容
//	                System.out.println(EntityUtils.toString(resEntity,
//	                        Charset.forName("UTF-8")));
//	            }
//	            // 销毁
//	            EntityUtils.consume(resEntity);
//	        }finally{
//	            response.close();
//	        }
//	    }finally{
//	        httpClient.close();
//	    }
//	}
//	 
//	 /**
//	 * 下载文件
//	 * @param  url
//	 * @param  destFileName   xxx.jpg/xxx.png/xxx.txt
//	 * @throws  ClientProtocolException
//	 * @throws IOException
//	 */
//	public static void getFile(String url, String destFileName)
//	        throws ClientProtocolException, IOException {
//	    // 生成一个httpclient对象
//	    CloseableHttpClient httpclient = HttpClients.createDefault();
//	    HttpGet httpget =newHttpGet(url);
//	    HttpResponse response = httpclient.execute(httpget);
//	    HttpEntity entity = response.getEntity();
//	    InputStream in = entity.getContent();
//	    File file =new File(destFileName);
//	    try{
//	        FileOutputStream fout =new FileOutputStream(file);
//	        int l = -1;
//	        byte[] tmp =new byte[1024];
//	        while((l = in.read(tmp)) != -1) {
//	            fout.write(tmp,0, l);
//	            // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
//	        }
//	        fout.flush();
//	        fout.close();
//	    }finally{
//	        // 关闭低层流。
//	        in.close();
//	    }
//	    httpclient.close();
//	}
	
	public byte[] getContent(String filePath) throws IOException {  
        File file = new File(filePath);  
        long fileSize = file.length();  
        if (fileSize > Integer.MAX_VALUE) {  
            System.out.println("file too big...");  
            return null;  
        }  
        FileInputStream fi = new FileInputStream(file);  
        byte[] buffer = new byte[(int) fileSize];  
        int offset = 0;  
        int numRead = 0;  
        while (offset < buffer.length  
        && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {  
            offset += numRead;  
        }  
        // 确保所有数据均被读取  
        if (offset != buffer.length) {  
        throw new IOException("Could not completely read file "  
                    + file.getName());  
        }  
        fi.close();  
        return buffer;  
    }  
  
    /** 
     * the traditional io way 
     *  
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static byte[] toByteArray(String filename) throws IOException {  
  
        File f = new File(filename);  
        if (!f.exists()) {  
            throw new FileNotFoundException(filename);  
        }  
  
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());  
        BufferedInputStream in = null;  
        try {  
            in = new BufferedInputStream(new FileInputStream(f));  
            int buf_size = 1024;  
            byte[] buffer = new byte[buf_size];  
            int len = 0;  
            while (-1 != (len = in.read(buffer, 0, buf_size))) {  
                bos.write(buffer, 0, len);  
            }  
            return bos.toByteArray();  
        } catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        } finally {  
            try {  
                in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            bos.close();  
        }  
    }  
  
    /** 
     * NIO way 
     *  
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static byte[] toByteArray2(String filename) throws IOException {  
  
        File f = new File(filename);  
        if (!f.exists()) {  
            throw new FileNotFoundException(filename);  
        }  
  
        FileChannel channel = null;  
        FileInputStream fs = null;  
        try {  
            fs = new FileInputStream(f);  
            channel = fs.getChannel();  
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());  
            while ((channel.read(byteBuffer)) > 0) {  
                // do nothing  
                // System.out.println("reading");  
            }  
            return byteBuffer.array();  
        } catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        } finally {  
            try {  
                channel.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            try {  
                fs.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /** 
     * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能 
     *  
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static byte[] toByteArray3(String filename) throws IOException {  
  
        FileChannel fc = null;  
        try {  
            fc = new RandomAccessFile(filename, "r").getChannel();  
            MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0,  
                    fc.size()).load();  
            System.out.println(byteBuffer.isLoaded());  
            byte[] result = new byte[(int) fc.size()];  
            if (byteBuffer.remaining() > 0) {  
                // System.out.println("remain");  
                byteBuffer.get(result, 0, byteBuffer.remaining());  
            }  
            return result;  
        } catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        } finally {  
            try {  
                fc.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }
    
    
}
