package com.example.navigationdrawertest.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DESUtils {
	
	private DESUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 生成密钥
     *
     * @return
     */
    public static byte[] initKey561() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }

    public static byte[] initKey56(){
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
    
    
    /**
     * 生成密钥
     *
     * @param keysize
     * @return
     */
    public static byte[] initKey(int keysize) {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(keysize);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * DES 加密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "DES");
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherBytes = cipher.doFinal(data);
            return cipherBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES 解密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "DES");
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainBytes = cipher.doFinal(data);
            return plainBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void encrypt(String file, String destFile) throws Exception {
    	SecretKey secretKey = new SecretKeySpec(initKey56(), "DES");
    	Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
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
	  /**
	  * 文件采用DES算法解密文件
	  *
	  * @param file 已加密的文件 如c:/加密后文件.txt
	  *         * @param destFile
	  *         解密后存放的文件名 如c:/ test/解密后文件.txt
	  */
	public static void decrypt(String file, String dest) throws Exception {
		try{
			SecretKey secretKey = new SecretKeySpec(initKey56(), "DES");
			Cipher cipher = Cipher.getInstance("DES");
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
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
	
}
