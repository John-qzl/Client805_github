package com.example.navigationdrawertest.login;

import java.lang.reflect.Method;

public class PadInfoHelp {
	
	
	public static String  getSeriNumber()
	{
		String serialnum = null;                                                                                                                                        
		try {                                                           
			 Class<?> c = Class.forName("android.os.SystemProperties"); 
			 Method get = c.getMethod("get", String.class, String.class );     
			 serialnum = (String)(   get.invoke(c, "ro.serialno", "unknown" )  );
			 return serialnum;
		}                                                                                
		catch (Exception ignored)                                                        
		{  
			 return "unknow exception";
		}
	}
	
/*	@SuppressWarnings({ "unused", "finally" })
	public static List<String> getSystemMacAddress(){

		
		//jdk1.6新特性，取得物理地址
		List<String> address = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces();
			while (el.hasMoreElements()) {
				//String displayName = el.nextElement().getDisplayName();
				NetworkInterface inter =  el.nextElement();
				String aa = inter.toString();
				Enumeration<InetAddress>  interAddressList =  inter.getInetAddresses();
				
				byte[] mac = inter.getHardwareAddress();
				if (mac == null || mac.length <= 0)
					continue;
				StringBuilder builder = new StringBuilder();
				for (byte b : mac) {
					builder.append(hexByte(b));
					builder.append("-");
				}
				builder.deleteCharAt(builder.length() - 1);
				address.add(builder.toString().toUpperCase());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return address;
		}
	}
	
	static String hexByte(byte b) {
		String s = "000000" + Integer.toHexString(b);
		return s.substring(s.length() - 2);
	}
*/
}
