package com.example.navigationdrawertest.Broadcast;

import com.example.navigationdrawertest.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver{

	public final String action_boot = "android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(action_boot)){
			Intent mainIntent = new Intent(context, MainActivity.class);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mainIntent);
		}
	}
	
}
