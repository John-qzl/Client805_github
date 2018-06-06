package com.example.navigationdrawertest.utils;

import java.util.ArrayList;
import java.util.List;

public class CalculateUtil {
	
	static int[] ckeckType = {1,2,4,8,16,32,64,128,256}; 
	
	public static void CalculateSquare(int num, List<Integer> strList){
		if(num == 128){
			strList.add(128);
		}else{
			if(num == 1){
				strList.add(1);
			}else{
				for(int i=0; i<=8; i++){
					if(num == 0){
						break;
					}else{
						if(num <= ckeckType[i]){
							num = num - ckeckType[i-1];
							strList.add(ckeckType[i-1]);
							CalculateSquare(num, strList);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 2º
	 * 2¹	I类单点
	 * 2²	易错项
	 * 2³	
	 * 。。。
	 * @param type:operationtype项的值
	 * @return
	 */
	public static List<Integer> CalculateOperationItem(String type){
		List<Integer> strList = new ArrayList<Integer>();
		int num = Integer.parseInt(type);
//		strList = CalculateSquare(num, strList);
		CalculateSquare(num, strList);
		return strList;
	}

}
