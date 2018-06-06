package com.example.navigationdrawertest.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidateUtil {
	static boolean flag = false;
	static String regex = "";

	public static boolean check(String str, String regex) {
		try {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证非空
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkNotEmputy(String notEmputy) {
		regex = "^\\s*$";
		return check(notEmputy, regex) ? false : true;
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		String regex = "^\\w+[-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$ ";
		return check(email, regex);
	}

	/**
	 * 验证手机号码
	 * 
	 * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
	 * 联通号码段:130、131、132、136、185、186、145 电信号码段:133、153、180、189
	 * 
	 * @param cellphone
	 * @return
	 */
	public static boolean checkCellphone(String cellphone) {
		String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
		return check(cellphone, regex);
	}

	/**
	 * 验证固话号码
	 * 
	 * @param telephone
	 * @return
	 */
	public static boolean checkTelephone(String telephone) {
		String regex = "^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";
		return check(telephone, regex);
	}

	/**
	 * 验证传真号码
	 * 
	 * @param fax
	 * @return
	 */
	public static boolean checkFax(String fax) {
		String regex = "^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";
		return check(fax, regex);
	}

	/**
	 * 验证QQ号码
	 * 
	 * @param QQ
	 * @return
	 */
	public static boolean checkQQ(String QQ) {
		String regex = "^[1-9][0-9]{4,} $";
		return check(QQ, regex);
	}
	
	/**
	 * 验证是否是100以内的整数或者带一位小数
	 * @param str
	 * @return
	 */
	public static boolean checkZoreToTen(String str) {
		String regex = "0|[1-9]|[1-9]\\d|100";
		return check(str, regex);
	}
	
	/**
	 * 验证是否是10以内的整数或者带一位小数
	 * @param str
	 * @return
	 */
	public static boolean checkZoreToTenTen(String str) {
		String regex = "[0-9]|10";
		return check(str, regex);
	}

	/**
	 * 判断输入的是否是IP
	 * @param str
	 * @return
	 */
	public static String removeBlank(String IP){//去掉IP字符串前后所有的空格  
        while(IP.startsWith(" ")){  
               IP= IP.substring(1,IP.length()).trim();  
            }  
        while(IP.endsWith(" ")){  
               IP= IP.substring(0,IP.length()-1).trim();  
            }  
        return IP;  
    }  
    public static boolean isIp(String IP){//判断是否是一个IP  
        boolean b = false;  
        IP = removeBlank(IP);  
        if(IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")){  
            String s[] = IP.split("\\.");  
            if(Integer.parseInt(s[0])<255)  
                if(Integer.parseInt(s[1])<255)  
                    if(Integer.parseInt(s[2])<255)  
                        if(Integer.parseInt(s[3])<255)  
                            b = true;  
        }  
        return b;  
    }
    
    /**
     * 判断输入的是否是端口号
     * @param port
     * @return
     */
    public static boolean isport(String port){
    	String regex="0|(^[1-9]\\d{0,3}$)|(^[1-5]\\d{4}$)|(^6[0-4]\\d{3}$)|(^65[0-4]\\d{2}$)|(^655[0-2]\\d$)|(^6553[0-5]$)";
    	return check(port, regex);
    } 
	
}
