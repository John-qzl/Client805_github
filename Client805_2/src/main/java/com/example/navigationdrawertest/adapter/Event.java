package com.example.navigationdrawertest.adapter;

public class Event {
	
	//更新每个fragment的adapter
	public static class LocationEvent{
		private String str;
		
		public LocationEvent(String str){
			this.str = str;
		}
		
		public String getContent(){
			return str;
		}
	}
	
	
}
