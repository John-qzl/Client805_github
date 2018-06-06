package com.example.navigationdrawertest.utils;

import java.util.Arrays;

public class ArrUtil {
	
	public static boolean useList(String[] arr, String targetValue) {
	    return Arrays.asList(arr).contains(targetValue);
	}
	
}
