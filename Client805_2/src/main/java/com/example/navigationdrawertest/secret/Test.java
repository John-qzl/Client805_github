package com.example.navigationdrawertest.secret;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

//2．  测试DesEncrypter加解密类

public class Test {

	public static void main(String[] args) {

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