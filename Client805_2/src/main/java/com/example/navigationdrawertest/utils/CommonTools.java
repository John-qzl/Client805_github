package com.example.navigationdrawertest.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * 常用的工具类。
 * @author 施俊
 * @since 2004-10-19
 * @version 1.0
 * @see
 */
public class CommonTools{

	private static Calendar	calendar = Calendar.getInstance();
	
	/**
	 * @param list
	 * @return 判断集合是否为空
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmptyList(List list){
		return list ==null || list.isEmpty();
	}
	
	public static String listJoinCommaToString(List<String> list){
		
		StringBuilder sb = new StringBuilder(); 
		for(String string : list){
			sb.append(string).append(",");
		}
		
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		
		return sb.toString();
	}

	/**
	 * 判断输入的yyyy-MM-dd是否是日期
	 * @param str
	 * @return
	 */
	public static boolean isDate(String str){
		boolean breturn = false;
		if(!CommonTools.isNullString(str)){
			try{
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				dateFormat.setLenient(false);
				dateFormat.parse(str);
				breturn = true;
			}catch(Exception e){
				breturn = false;
			}
		}
		return breturn;
	}

	public static boolean isLastLine(String[] line){
		boolean breturn = true;
		for(int i = 0; i < line.length; i++){
			if("*****".equals(line[i]) || CommonTools.isNullString(line[i])){
				continue;
			}else{
				breturn = false;
				break;
			}
		}
		return breturn;
	}

	/**
	 * 判断输入的字符串是否为null，如果是null则返回"",否则，返回原字符串。
	 * @param str
	 * @return
	 */
	public static String null2String(String str){
		if(isNullString(str)){
			return "";
		}else return str;
	}

	/**
	 * 判断输入的对象是否为null，如果是null则返回"",否则，返回 对象.toString()。
	 * @param str
	 * @return
	 */
	public static String Obj2String(Object obj){
		return obj == null ?"" :obj.toString();
	}

	public static String formatStrDate(String date){
		String returnFormatDate = "";
		if(date.length() > 1){
			returnFormatDate = "to_date('" + date + "','yyyy-mm-dd')";
		}else{
			returnFormatDate = "null";
		}
		return returnFormatDate;
	}

	/**
	 * 将字符串转化为日期格式，字符串的格式为：yyyy-mm-dd，<br>
	 * @param date
	 *        date的格式为：yyyy-mm-dd
	 * @return Date - 如果无法转换，则返回null
	 */
	public static Date str2Date(String date){
		java.sql.Date return_Date = null;
		try{
			return_Date = java.sql.Date.valueOf(date);
		}catch(Exception e){
			return null;
		}
		return return_Date;
	}

	/**
	 * 将字符串转化为日期格式
	 * @param dateTime
	 *        20101115201233[yyyyMMddHHmmss]
	 * @return Date
	 */
	public static Date str2Date2(String dateTime){
		Date date = new Date();
		try{
			int[] time = new int[6];
			time[0] = Integer.parseInt(dateTime.substring(0, 4));
			time[1] = Integer.parseInt(dateTime.substring(4, 6)) - 1;
			time[2] = Integer.parseInt(dateTime.substring(6, 8));
			time[3] = Integer.parseInt(dateTime.substring(8, 10));
			time[4] = Integer.parseInt(dateTime.substring(10, 12));
			time[5] = Integer.parseInt(dateTime.substring(12));
			// 
			calendar.set(time[0], time[1], time[2], time[3], time[4], time[5]);
			date = calendar.getTime();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
		return date;
	}

	/**
	 * 将字符串转化为日期格式
	 * @param dateTime
	 *        2010-11-15 20:12:33[yyyy-MM-dd HH:mm:ss]
	 * @return Date
	 */
	public static Date str2Date3(String dateTime){
		Date date = new Date();
		try{
			int[] time = new int[6];
			time[0] = Integer.parseInt(dateTime.substring(0, 4));
			time[1] = Integer.parseInt(dateTime.substring(5, 7)) - 1;
			time[2] = Integer.parseInt(dateTime.substring(8, 10));
			time[3] = Integer.parseInt(dateTime.substring(11, 13));
			time[4] = Integer.parseInt(dateTime.substring(14, 16));
			time[5] = Integer.parseInt(dateTime.substring(17));
			// 
			calendar.set(time[0], time[1], time[2], time[3], time[4], time[5]);
			date = calendar.getTime();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
		return date;
	}
	
	/**
	 * 将日期转化为字符格式，字符串的格式为：yyyy-mm-dd
	 * @param date
	 *        要转化的date
	 * @return String 返回的格式为：yyyy-mm-dd
	 */
	public static String date2String(Date date){
		if(date == null){
			return "";
		}else{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return dateFormat.format(date);
		}
	}

	/**
	 * 将日期转化为字符格式，字符串的格式为：yyyy-MM-dd HH:mm
	 * @param date
	 *        要转化的date
	 * @return String 返回的格式为：yyyy-MM-dd HH:mm
	 */
	public static String time2String(Date date){
		if(date == null){
			return "";
		}else{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return dateFormat.format(date);
		}
	}
	
	/**
	 * 将日期转化为字符格式，字符串的格式为：yyyyMMddHHmmss
	 * @param date
	 *        要转化的date
	 * @return String 返回的格式为：yyyyMMddHHmmss
	 */
	public static String time2String2(Date date){
		if(date == null){
			return "";
		}else{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			return dateFormat.format(date);
		}
	}

	/**
	 * 将日期转化为字符格式，字符串的格式为：yyyy-MM-dd HH:mm:ss
	 * @param date
	 *        要转化的date
	 * @return String 返回的格式为：yyyy-MM-dd HH:mm:ss
	 */
	public static String time2String3(Date date){
		if(date == null){
			return "";
		}else{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return dateFormat.format(date);
		}
	}
	

	/**
	 * 得到指定格式的日期
	 * @param date
	 *        日期
	 * @param format
	 *        指定的格式[yyyyMMddHHmmssSSS][yyyy-MM-dd HH:mm:ss]
	 * @return
	 */
	public static String time2String(Date date, String format){
		if(date == null){
			return "";
		}else{
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(date);
		}
	}

	

	/**
	 * 将日期转化为只有年度的字符格式，字符串的格式为：yyyy
	 * @param date
	 * @return
	 */
	public static String date2Year(Date date){
		if(date == null){
			return "";
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			return sdf.format(date);
		}
	}

	public static java.sql.Date util2Sql(java.util.Date date){
		return new java.sql.Date(date.getTime());
	}


	/**
	 * 删除文件夹，当该文件夹下有文件或者文件夹的时候不能删除该文件夹
	 * @param path
	 * @return boolean
	 */
	public static boolean deleteFile(String path){
		System.out.println("删除文件：" + path);
		File file = new File(path);
		boolean returnBoolean = file.delete();
		System.out.println("删除" + returnBoolean);
		return returnBoolean;
	}

	/**
	 * 在字符串上加CDATA。
	 * @param string
	 * @return String
	 */
	public static String addCDATA(String string){
		String returnStr = "<![CDATA[";
		returnStr += string;
		returnStr += "]]>";
		return returnStr;
	}


	/**
	 * 将char的高四位去掉，再合并成字符串。<br>
	 * 目的是要解决度dbf文件的字段名时，中文出现乱码问题
	 * @param str
	 * @return
	 */
	public static String getStrByCharToByte(String str){
		byte[] temp = new byte[str.length()];
		for(int i = 0; i < str.length(); i++){
			temp[i] = (byte)(str.charAt(i));
		}
		return new String(temp);
	}

	public static boolean isNullString(String str){
		if(str == null || str.trim().equals("") || str.trim().equalsIgnoreCase("NULL")) return true;
		else return false;
	}

	public static String convertorQuote(String sqlStr){
		if(isNullString(sqlStr)) return "";
		return sqlStr.replaceAll("'", "''");
	}

	public List compareList(List list1, List list2){
		List list = new ArrayList();
		if(!list2.equals(list1)){
			for(int i = 0; i < list2.size(); i++){
				if(!list1.contains(list2.get(i))) list.add(list2.get(i));
			}
		}
		return list;
	}

	public static String toChinese(String strvalue){
		try{
			if(strvalue == null) return null;
			else{
				strvalue = new String(strvalue.getBytes("ISO8859_1"), "GBK");
				return strvalue;
			}
		}catch(Exception e){
			return null;
		}
	}

	public static String gBK2ISO_8859_1(String str){
		try{
			return new String(str.getBytes("GBK"), "ISO-8859-1");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return str;
		}
	}

	public static String iSO_8859_12GBK(String str){
		try{
			return new String(str.getBytes("ISO-8859-1"), "GBK");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return str;
		}
	}

	/** 将数组转换成字串 ","分隔 */
	public static String array2String(Object[] array){
		StringBuffer str = new StringBuffer();
		for(int i = 0; i < array.length; i++)
			str.append((String)array[i]).append(",");
		if(str.length() != 0) str = new StringBuffer(str.substring(0, str.length() - 1));
		return str.toString();
	}

	/** 将数组转换成字串 ","分隔,字符串前后都有"," */
	public static String arrayToString(Object[] array){
		StringBuffer str = new StringBuffer();
		str.append(",");
		for(int i = 0; i < array.length; i++)
			str.append((String)array[i]).append(",");
		return str.toString();
	}

	/**
	 * 月份判断
	 * @param month
	 * @return
	 */
	public static String monthValidate(int month){
		String str = "";
		switch(month){
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				str = "l";
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				str = "s";
				break;
			case 2:
				str = "m";
				break;
			default:
				str = "err";
		}
		return str;
	}

	public static void println(Object obj){
		System.out.println("[ CommonTools DEBUG : ]" + obj);
	}

	/**
	 * 将字符串转换为UTF8编码。
	 * @param str
	 *        未经过编码的字符串。
	 * @return UTF8编码的字符串。
	 */
	public static String toUtf8String(String str){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < str.length(); i++){
			char c = str.charAt(i);
			if(c >= 0 && c <= 255){
				sb.append(c);
			}else{
				byte[] b;
				try{
					b = Character.toString(c).getBytes("utf-8");
				}catch(Exception ex){
					System.out.println(ex);
					b = new byte[0];
				}
				for(int j = 0; j < b.length; j++){
					int k = b[j];
					if(k < 0) k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	public static String list2String(List list, String st){
		String str = "";
		for(int i = 0; i < list.size(); i++){
			if(i == list.size() - 1){
				str = str + (String)((Map)list.get(i)).get(st);
			}else{
				str = str + (String)((Map)list.get(i)).get(st) + ",";
			}
		}
		return str;
	}

	public static String list2String(List<String> list){
		String str = "";
		for(String substr: list){
			str = str + substr + ",";
		}
		return str.equals("") ?"" :str.substring(0, str.length() - 1);
	}

	public static String set2String(Set<String> set){
		String str = "";
		for(String substr: set){
			str = str + substr + ",";
		}
		return str.equals("") ?"" :str.substring(0, str.length() - 1);
	}

	public static String getCurrentYearAndMonth(){
		GregorianCalendar lgc = new GregorianCalendar();
		int year = lgc.get(Calendar.YEAR);
		int month = lgc.get(Calendar.MONTH);
		if(month == 0){
			year = year - 1;
			month = 12;
		}
		if(month < 10){
			return String.valueOf(year) + "-0" + String.valueOf(month);
		}else{
			return String.valueOf(year) + "-" + String.valueOf(month);
		}
	}

	/**
	 * 计算年度列表
	 * @param span
	 *        跨度：当前系统年度的前后跨度
	 * @return
	 */
	public static List getYear(int span){
		GregorianCalendar lgc = new GregorianCalendar();
		String year = String.valueOf(lgc.get(Calendar.YEAR));
		List list = new ArrayList();
		Map map = null;
		for(int i = Integer.parseInt(year) - span; i <= Integer.parseInt(year) + span; i++){
			map = new HashMap();
			map.put("value", String.valueOf(i));
			list.add(map);
		}
		return list;
	}

	/**
	 * 计算年度列表
	 * @param span
	 *        跨度：当前系统年度的前后跨度
	 * @param flag
	 *        标识 0：系统时间前 1：系统时间后 其他：系统时间前后
	 * @return
	 */
	public static List getYears(int span, int flag){
		GregorianCalendar lgc = new GregorianCalendar();
		String year = String.valueOf(lgc.get(Calendar.YEAR));
		List list = new ArrayList();
		Map map = null;
		if(flag == 0) for(int i = Integer.parseInt(year) - span; i <= Integer.parseInt(year); i++){
			map = new HashMap();
			map.put("value", String.valueOf(i));
			list.add(map);
		}
		else if(flag == 1) for(int i = Integer.parseInt(year); i <= Integer.parseInt(year) + span; i++){
			map = new HashMap();
			map.put("value", String.valueOf(i));
			list.add(map);
		}
		else for(int i = Integer.parseInt(year) - span; i <= Integer.parseInt(year) + span; i++){
			map = new HashMap();
			map.put("value", String.valueOf(i));
			list.add(map);
		}
		return list;
	}

	/**
	 * 为编号左填充指定字符
	 * @param src
	 *        源字符
	 * @param cha
	 *        填充字符
	 * @param len
	 *        填充需要达到的长度
	 * @return String 填充后的
	 */
	public static String supplyChar(String src, String cha, int len){
		String strSrc = src;
		while(strSrc.length() < len){
			strSrc = cha + strSrc;
		}
		return strSrc;
	}

	/**
	 * 系统日期取得. <BR>
	 * @author 李晓强
	 * @return yyyymmdd格式
	 * @see
	 * @since 1.0
	 */
	public static String getSysdate(){
		GregorianCalendar lgc = new GregorianCalendar();
		String year = String.valueOf(lgc.get(Calendar.YEAR));
		String month = String.valueOf(lgc.get(Calendar.MONTH) + 1);
		if(month.length() == 1) month = "0" + month;
		String date = String.valueOf(lgc.get(Calendar.DATE));
		if(date.length() == 1) date = "0" + date;
		return year + "-" + month + "-" + date;
	}

	/**
	 * 系统时间取得. <BR>
	 * @author 李晓强
	 * @return HHMISS
	 * @see
	 * @since 1.0
	 */
	public static String getSystime(){
		GregorianCalendar lgc = new GregorianCalendar();
		String hour = String.valueOf(lgc.get(Calendar.HOUR_OF_DAY));
		if(hour.length() == 1) hour = "0" + hour;
		String minute = String.valueOf(lgc.get(Calendar.MINUTE));
		if(minute.length() == 1) minute = "0" + minute;
		String second = String.valueOf(lgc.get(Calendar.SECOND));
		if(second.length() == 1) second = "0" + second;
		return hour + ":" + minute + ":" + second;
	}

	public static void Debug(Object obj){
		System.out.println(obj.toString());
	}

	public static String FormatDate(java.util.Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	public static String FormatDate(java.util.Date date, String format){
		SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
		return simpledateformat.format(date);
	}

	public static String formatStrDate2(String date){
		String returnFormatDate = "";
		if(date.length() > 1){
			returnFormatDate = "to_date('" + date + "','yyyy-mm-dd')";
		}else{
			returnFormatDate = "";
		}
		return returnFormatDate;
	}

	/**
	 * 将字符串数组转化为整数进行相加，然后将结果作为字符串返回
	 * @param String
	 *        [] 字符串数组
	 * @return String 字符串数组之和
	 */
	public static String StringArray2String(Object[] stringArray){
		long sum = 0;
		for(int i = 0; i < stringArray.length; i++){
			try{
				sum += Long.parseLong((String)stringArray[i]);
			}catch(Exception e){
				sum += 0;
			}
		}
		return String.valueOf(sum);
	}

	/**
	 * 将字符串转化为整数进行拆分，拆分为2的n次幂的字符串数组
	 * @param String
	 *        字符串数组之和
	 * @return String[] 字符串数组
	 */
	public static String[] String2StringArray(String string){
		StringBuffer buf = new StringBuffer();
		String binaryString = Long.toBinaryString(Long.parseLong(string));// 转为2进制
		// System.out.println(binaryString);
		for(int i = 0; i < binaryString.length(); i++){
			if(binaryString.charAt(i) == '1'){
				buf.append(String.valueOf((long)Math.pow((double)2, (double)binaryString.length() - 1 - i)));
			}
			if(i != binaryString.length() - 1){
				buf.append(", ");
			}
		}
		StringTokenizer st = new StringTokenizer(buf.toString(), ", ");
		int count = st.countTokens();
		String[] s = new String[count];// 判断数组个数
		for(int i = 0; i < count; i++){
			s[i] = st.nextToken();
		}
		return s;
	}

	/**
	 * 将字符串转化为整数进行拆分，拆分为2的n次幂的字符串，中间用逗号分隔
	 * @param String
	 *        字符串数组之和
	 * @return String 用逗号分隔的拆分后的字符串
	 */
	public static String String2StringList(String string){
		StringBuffer buf = new StringBuffer();
		String binaryString = Long.toBinaryString(Long.parseLong(string));// 转为2进制
		// System.out.println(binaryString);
		for(int i = 0; i < binaryString.length(); i++){
			if(binaryString.charAt(i) == '1'){
				buf.append(String.valueOf((long)Math.pow((double)2, (double)binaryString.length() - 1 - i)));
			}
			if(i != binaryString.length() - 1){
				buf.append(",");
			}
		}
		return buf.toString();
	}

	public static String uniteString(String str1, String str2){
		if(CommonTools.isNullString(str1)) str1 = "0";
		if(CommonTools.isNullString(str2)) str2 = "0";
		Set set = new HashSet();
		String[] tmp = String2StringArray(str1);
		for(int i = 0; i < tmp.length; i++){
			set.add((String)tmp[i]);
		}
		tmp = String2StringArray(str2);
		for(int i = 0; i < tmp.length; i++){
			set.add((String)tmp[i]);
		}
		return StringArray2String(set.toArray());
	}

	public static String[] split(String str){
		return str.split(",");
	}

	/**
	 * 将Map按照key值进行排序，key只能是数字，不支持字符
	 * @param Map
	 * @return Map.Entry[]
	 */
	public static Map.Entry[] getSortedHashtableByKey(Map map){
		Set set = map.entrySet();
		Map.Entry[] entries = (Map.Entry[])set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator(){

			public int compare(Object arg0, Object arg1){
				String key1 = (String)((Map.Entry)arg0).getKey();
				String key2 = (String)((Map.Entry)arg1).getKey();
				return Long.parseLong(key1) > Long.parseLong(key2) ?1 :-1;
				// return ((Comparable) Long.parseLong(key1)).compareTo(Long.parseLong(key2));
			}
		});
		return entries;
	}

	public static boolean isInt(String character){
		char chars[] = character.toCharArray();
		for(int i = 0; i < chars.length; i++){
			int ascii = getAscii(chars[i]);
			if(ascii >= 48 && ascii <= 57) return true;
		}
		return false;
	}

	public static int getAscii(char cn){
		byte bytes[] = String.valueOf(cn).getBytes();
		if(bytes.length == 1) return bytes[0];
		if(bytes.length == 2){
			int hightByte = 256 + bytes[0];
			int lowByte = 256 + bytes[1];
			int ascii = (256 * hightByte + lowByte) - 0x10000;
			return ascii;
		}else{
			return 0;
		}
	}

	public static String getSubContent(String str, int length){
		str = null2String(str);
		if(!("".equals(str) || str.length() < length)){
			str = str.substring(0, length) + "....";
		}
		return str;
	}

	/**
	 * 测试字符是否为数字;
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str){
		String number = "0123456789.";
		if(!"".equals(null2String(str))){
			char[] chars = str.toCharArray();
			for(int i = 0; i < chars.length; i++){
				if(number.indexOf(String.valueOf(chars[i]), 0) == -1){ return false; }
			}
		}else{
			return false;
		}
		return true;
	}

	public static void main(String[] args){
		System.out.println("sss");
		System.out.println(CommonTools.getCurrentYearAndMonth().substring(0, 4));
		System.out.println(CommonTools.getCurrentYearAndMonth().substring(5, 7));
		System.out.println("sss");
		System.out.println(CommonTools.time2String(new Date()));
		System.out.println(CommonTools.isDate("2007-01-1"));
		boolean flag = isNumber("");
		System.out.println("1111" + flag);
	}

	// 转换sql为数据库分页
	public static String getPageBreak(String sql, int strNum, int endNum){
		StringBuffer pagebreak = new StringBuffer();
		pagebreak.append(" select * from ( ");
		pagebreak.append("  select main_sql.*,RowNum rn from ( ");
		pagebreak.append(sql).append(") main_sql where RowNum <= ").append(endNum).append(" ) ");
		pagebreak.append(" where rn >= ").append(strNum);
		return pagebreak.toString();
	}

	// 判断test是否在用','号隔开的字符串中出现，如果test和str任意为空则返回false
	public static boolean ifInStr(String test, String str){
		if(!"".equals(test) && !"".equals(str)){
			String[] main_str = str.split(",");
			for(int i = 0; i < main_str.length; i++){
				if(test.equalsIgnoreCase(main_str[i])){ return true; }
			}
			return false;
		}else{
			return false;
		}
	}

	public static String getArrayToString(String[] array, String boxOff){
		StringBuffer str = new StringBuffer();
		for(int i = 0; i < array.length; i++){
			// 组后一个不拼接 ,
			if((i + 1) == array.length){
				str.append(null2String(array[i]));
			}else{
				str.append(null2String(array[i])).append(boxOff);
			}
		}
		return str.toString();
	}

	public static Class getClassFromType(String type){
		String[] types = new String[]{type};
		Class[] cs = getClassFromType(types);
		if(cs.length > 0){
			return cs[0];
		}else{
			return null;
		}
	}

	public static Class[] getClassFromType(String[] types){
		Class[] cs = new Class[types.length];
		for(int i = 0; i < types.length; i++){
			if(types[i].equals("byte")){
				cs[i] = byte.class;
				continue;
			}
			if(types[i].equals("short")){
				cs[i] = short.class;
				continue;
			}else if(types[i].equals("int")){
				cs[i] = int.class;
				continue;
			}else if(types[i].equals("long")){
				cs[i] = long.class;
				continue;
			}else if(types[i].equals("float")){
				cs[i] = float.class;
				continue;
			}else if(types[i].equals("double")){
				cs[i] = double.class;
				continue;
			}else{
				try{
					cs[i] = Class.forName(types[i]);
				}catch(ClassNotFoundException e){
					// TODO Auto-generated catch block
					cs[i] = null;
				}
				continue;
			}
		}
		return cs;
	}

	// 判断字符串是否在string[]数组中
	public static boolean isInStrings(String value, String[] strs){
		boolean flag = false;
		for(int i = 0; i < strs.length; i++){
			if(value.equals(strs[i])){
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 获取根目录
	 * @return 项目的根路径
	 */
	public static String getRootPath(){
		String result = null;
		try{
			result = CommonTools.class.getResource("CommonTools.class").toURI().getPath().toString();
		}catch(URISyntaxException e1){
			e1.printStackTrace();
		}
		int index = result.indexOf("WEB-INF");
		if(index == -1){
			index = result.indexOf("bin");
		}
		result = result.substring(1, index);
		if(result.endsWith("/")) result = result.substring(0, result.length() - 1);// 不包含最后的"/"
		return result;
	}

	public static Object clob2String(Clob clob){
		String reString = "";
		Reader is = null;
		StringBuffer sb = new StringBuffer();
		try{
			is = clob.getCharacterStream();
			BufferedReader br = new BufferedReader(is);
			String s = br.readLine();
			while(s != null){ // 执行循环将字符串全部取出赋值给StringBuffer由StringBuffer转成STRING
				sb.append(s);
				s = br.readLine();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		reString = sb.toString();
		return reString;
	}

	public static String[] list2stringArray(List<String> list){
		String[] result = new String[list.size()];
		for(int i = 0; i < list.size(); i++){
			result[i] = list.get(i);
		}
		return result;
	}

	/**
	 * 获取根目录
	 * @return 项目的根路径
	 */
	public static String getProjectRoot(){
		String result = null;
		try{
			result = CommonTools.class.getResource("CommonTools.class").toURI().getPath().toString();
		}catch(URISyntaxException e1){
			e1.printStackTrace();
		}
		int str = result.indexOf("webapps") + 7;
		int end = result.indexOf("WEB-INF");
		result = result.substring(str, end);
		if(result.endsWith("/")) result = result.substring(0, result.length() - 1);// 不包含最后的"/"
		return result;
	}

	/**
	 * 将map中的key值变为小写 value值变为字符串型
	 * @param enums
	 *        the enums
	 * @return the list< map< string, string>>
	 */
	public static List<Map<String, String>> formatMapData(List<Map> enums){
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for(Map map: enums){
			Map<String, String> m = new HashMap<String, String>();
			for(Iterator it = map.keySet().iterator(); it.hasNext();){
				String key = (String)it.next();
				m.put(key.toLowerCase(), CommonTools.Obj2String(map.get(key)));
			}
			result.add(m);
		}
		return result;
	}
	/**
	 * @取得当前时间（格式为：yyyy-MM-dd HH:mm:ss）
	 * @return String
	 */
	public static String GetDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String sDate = sdf.format(new Date());
		return sDate;
	}
	
	

	public static String getSystemEncoding()
	{
		String encoding = "UTF-8";
		//获取系统参数
		Properties systemProperties = System.getProperties();
		//获取操作系统名称
		String systemName = CommonTools.Obj2String(systemProperties.get("os.name"));
		if(systemName.indexOf("win") != -1 || systemName.indexOf("Win") != -1){
			//windows环境
			encoding = "GBK";
		}
		return encoding;
	}
	
	/**
	 *@Function Name:  calculateBlank
	 *@Description: @param startDate yyyy-MM-dd hh:mm:ss
	 *@Description: @param endDate yyyy-MM-dd hh:mm:ss
	 *@Description: @return 计算日期间隔
	 *@Date Created:  2013-8-8 上午11:37:21
	 *@Author:  Pan Duan Duan
	 *@Last Modified:     ,  Date Modified: 
	 */
	public static String calculateBlank(String startDate, String endDate) {
		
		String retVal = "0";
		if(!"".equals(CommonTools.null2String(startDate)) && !"".equals(CommonTools.null2String(endDate))){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");    
			try {
				Date start = formatter.parse(startDate);
				Date end = formatter.parse(endDate);
				long diff = end.getTime() - start.getTime();    
				retVal = String.valueOf(diff/(1000*60*60*24) + 1);
			} catch (ParseException e) {
				e.printStackTrace();
			}    
		}
		return retVal;
	}
	
	/**
	 * @param startDate
	 * @param endDate
	 * @return 0 if one is null
	 */
	public static int getDayCountBetweenDates(Date startDate, Date endDate){
		if(startDate == null || endDate == null){
			return 0;
		}
		
		Calendar cmax = Calendar.getInstance();
		cmax.setTime(endDate);
		
		Calendar cmin = Calendar.getInstance();
		cmin.setTime(startDate);
		
		if(cmax.after(cmin)){
			long passed = cmax.getTimeInMillis() - cmin.getTimeInMillis();
			int date = (int)(passed/(1000*60*60*24));
			return date;
		}else{
			return 0;
		}
	}
	
	
	public static Map<Object, Object> nullMapToNew(Map<Object, Object> map) {
		
		if(null == map || map.isEmpty()){
			map = new HashMap<Object, Object>();
		}
		return map;
	}
}

