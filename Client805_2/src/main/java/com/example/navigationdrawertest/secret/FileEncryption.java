package com.example.navigationdrawertest.secret;

import javax.crypto.KeyGenerator;
import javax.crypto.CipherInputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Key;
import java.io.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

public class FileEncryption
{
	
	/** 
     * 自定义一个key 
     *  
     * @param string 
     */  
    public static Key getKey(String keyRule) {  
    	Key key = null;  
        byte[] keyByte = keyRule.getBytes();  
        // 创建一个空的八位数组,默认情况下为0  
        byte[] byteTemp = new byte[8];  
        // 将用户指定的规则转换成八位数组  
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {  
            byteTemp[i] = keyByte[i];  
        }  
        key = new SecretKeySpec(byteTemp, "DES");  
        return key;  
    }  
	
	/**
	  * 根据参数生成KEY
	  */
	public static Key getKey1(String strKey) {
		Key key = null;
	    try {
	        KeyGenerator _generator = KeyGenerator.getInstance("DES");
//	        _generator.init(new SecureRandom(strKey.getBytes()));
	        _generator.init(new SecureRandom(Base64.decode(strKey.getBytes("UTF-8"), Base64.DEFAULT)));
//	        _generator.init(new SecureRandom(new byte[]{'c', 's', 's', 'r', 'c' ,'@', '7', '0', '2'}));
	        key = _generator.generateKey();
	        _generator = null;
	    } catch (Exception e) {
	        throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
	    }
	    return key;
	}

	  /**
	  * 文件file进行加密并保存目标文件destFile中
	  *
	  * @param file   要加密的文件 如c:/test/srcFile.txt
	  * @param destFile 加密后存放的文件名 如c:/加密后文件.txt
	  */
	public static void encrypt(String file, String destFile) throws Exception {
	    Cipher cipher = Cipher.getInstance("DES");
	    Key key = getKey("cssrc@702");
	    cipher.init(Cipher.ENCRYPT_MODE, key);
	    InputStream is = new FileInputStream(file);
	    OutputStream out = new FileOutputStream(destFile);
	    CipherInputStream cis = new CipherInputStream(is, cipher);
	    byte[] buffer = new byte[2048];
	    int r;
	    while ((r = cis.read(buffer)) > 0) {
	        out.write(buffer, 0, r);
	    }
	    cis.close();
	    is.close();
	    out.close();
	}
	
	public static byte[] initKey56() {
		KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56, new SecureRandom("cssrc702cssrc702cssrc702cssrc702cssrc702cssrc702cssrc702".getBytes()));
//            keyGen.init(56);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }
	
	public static void decryptold(String file, String dest) {
		try{
			Cipher cipher = Cipher.getInstance("DES");
		    Key key = getKey("cssrc702");
		    cipher.init(Cipher.DECRYPT_MODE, key);
			
//			SecretKey secretKey = new SecretKeySpec(initKey56(), "DES");
//			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
			
			
		    InputStream is = new FileInputStream(file);
		    OutputStream out = new FileOutputStream(dest);
		    CipherOutputStream cos = new CipherOutputStream(out, cipher);
		    byte[] buffer = new byte[2048];
		    int r;
		    while ((r = is.read(buffer)) >= 0) {
		        cos.write(buffer, 0, r);
		    }
		    cos.close();
		    out.close();
		    is.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	  /**
	  * 文件采用DES算法解密文件
	  *
	  * @param file 已加密的文件 如c:/加密后文件.txt
	  *         * @param destFile
	  *         解密后存放的文件名 如c:/ test/解密后文件.txt
	  */
	public static void decrypt(String file, String dest) throws Exception {
//	    Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//	    Key key = getKey("cssrc@702");
//	    cipher.init(Cipher.DECRYPT_MODE, key);
		
		Cipher cipher = Cipher.getInstance("DES");
	    String key = "cssrc@702";
	    SecretKeySpec keySpec = null;
	    
	    byte[] mybyte=Base64.decode(key.getBytes("UTF-8"), Base64.DEFAULT);
	    keySpec = new SecretKeySpec(mybyte, "DES");
	    
//	    keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//	    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
//	    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
	    cipher.init(Cipher.DECRYPT_MODE, keySpec);
	    InputStream is = new FileInputStream(file);
	    OutputStream out = new FileOutputStream(dest);
	    CipherOutputStream cos = new CipherOutputStream(out, cipher);
	    byte[] buffer = new byte[2048];
	    int r;
	    while ((r = is.read(buffer)) >= 0) {
	        cos.write(buffer, 0, r);
	    }
	    cos.close();
	    out.close();
	    is.close();
	}
	
	public static String decryptDES(String decryptString, String decryptKey) throws Exception {  
		byte[] iv = {'c', 's', 's', 'r', 'c' ,'@', 7, 0, 2};
        byte[] byteMi = Base64.decode(decryptString.getBytes("UTF-8"), Base64.DEFAULT);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);  
//      IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);  
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");  
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");  
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);  
        byte decryptedData[] = cipher.doFinal(byteMi);  
        return new String(decryptedData);  
    }  
	
	/** 
     * 解密文件 
     * @param in 
     */  
//    public void doDecryptFile(InputStream in)  
//    {  
//    	Key mKey; 
//    	Cipher mDecryptCipher;  
//    	Cipher mEncryptCipher;  
//        if(in==null)  
//        {  
//            System.out.println("inputstream is null");  
//            return;  
//        }  
//        try {  
//            CipherInputStream cin = new CipherInputStream(in, mDecryptCipher);  
//            BufferedReader reader = new BufferedReader(new InputStreamReader(cin)) ;  
//            String line = null;  
//            while((line=reader.readLine())!=null)  
//            {  
//                System.out.println(line);  
//            }  
//            reader.close();  
//            cin.close();  
//            in.close();  
//            System.out.println("解密成功");  
//        } catch (Exception e) {  
//            System.out.println("解密失败");  
//            e.printStackTrace();  
//        }  
//    }  
	
	
	/**
	 * 问题：pad block corrupted
	 * @param inPath    加密文件解密之后的文件
	 * @param outPath	加密文件
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public static void decrypt2(String inPath, String outPath) throws IOException, NoSuchAlgorithmException,
		NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {
		FileInputStream fis = new FileInputStream(outPath);
		FileOutputStream fos = new FileOutputStream(inPath);
//		SecretKeySpec sks = new SecretKeySpec("cssrc@702".getBytes(),
//			"AES");
		Cipher cipher = Cipher.getInstance("DES");
		
		IvParameterSpec iv = new IvParameterSpec("cssrc@702".getBytes());
		AlgorithmParameterSpec paramSpec = iv;
		DESKeySpec dks = new DESKeySpec("cssrc@702".getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// key的长度不能够小于8位字节
		Key secretKey = keyFactory.generateSecret(dks);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		int b;
		byte[] d = new byte[8];
		while ((b = cis.read(d)) != -1) {
			fos.write(d, 0, b);
		}
		fos.flush();
		fos.close();
		cis.close();
	}
	
	
	/**
	 * 2017-1-17 09:24:23
	 */
	 /** 
     * 对文件srcFile进行加密输出到文件distFile 
     * @param srcFile 明文文件 
     * @param distFile 加密后的文件 
     * @throws Exception 
     */  
	private static String sKey = "cssrc702";
    public static void EncryptFile(String srcFile,String distFile) throws Exception{  
  
        InputStream  is=null;  
        OutputStream out  = null;  
        CipherInputStream cis =null;  
        try {  
            int mode = Cipher.ENCRYPT_MODE;  
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
            byte[] keyData = sKey.getBytes();  
            DESKeySpec keySpec = new DESKeySpec(keyData);  
            Key key = keyFactory.generateSecret(keySpec);  
            Cipher cipher = Cipher.getInstance("DES");  
            cipher.init(mode, key);  
            is= new FileInputStream(srcFile);  
            out  = new FileOutputStream(distFile);  
            cis = new CipherInputStream(is,cipher);  
            byte[] buffer = new byte[1024];  
            int r;  
            while((r=cis.read(buffer))>0){  
                out.write(buffer, 0, r);  
            }  
        } catch (Exception e) {  
            throw e;  
        } finally {  
            cis.close();  
            is.close();  
            out.close();  
        }  
    }  
  
    /** 
     * 解密文件srcFile到目标文件distFile 
     * @param srcFile 密文文件 
     * @param distFile 解密后的文件 
     * @throws Exception 
     */  
    public static void DecryptFile(String srcFile,String distFile) throws Exception{  
  
        InputStream  is=null;  
        OutputStream out  = null;  
        CipherOutputStream cos =null;  
        try {  
            int mode = Cipher.DECRYPT_MODE;  
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
            byte[] keyData = sKey.getBytes();  
            DESKeySpec keySpec = new DESKeySpec(keyData);  
            Key key = keyFactory.generateSecret(keySpec);  
            Cipher cipher = Cipher.getInstance("DES");  
            cipher.init(mode, key);  
            byte[] buffer = new byte[1024];  
            is= new FileInputStream(srcFile);  
            out  = new FileOutputStream(distFile);  
            cos = new CipherOutputStream(out,cipher);  
  
            int r;  
            while((r=is.read(buffer))>=0){  
                cos.write(buffer, 0, r);  
            }  
  
        } catch (Exception e) {  
            throw e;  
        } finally {  
            cos.close();  
            is.close();  
            out.close();  
        }  
    }  
	
	public void test(){
		
		try {

	           // Generate a temporary key. In practice, you would save this key.

	           // See also Encrypting with DES Using a Pass Phrase.

	           SecretKey key = KeyGenerator.getInstance("DES").generateKey();

	 

	           // Create encrypter/decrypter class

	           DesEncrypter encrypter = new DesEncrypter(key);

	 

	           // Encrypt

	           encrypter.encrypt(new FileInputStream("C:\\test.doc"),

	               new FileOutputStream("C:\\test.des"));

	 

	           // Decrypt

	           encrypter.decrypt(new FileInputStream("C:\\test.des"),

	               new FileOutputStream("C:\\test_out.doc"));

	       } catch (Exception e) {

	           System.out.println("Exception");

	       }
		
	}
	

}
