package com.example.elibrary.controllers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckConnection {
	private static final String TAG="CheckConnection";
	    
        public static boolean isConnected(Context context){
        	Log.d(TAG, "isConnected method");
    		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo info = manager.getActiveNetworkInfo();
    		if (info != null && info.isConnected()) {
    			return true;
    		} else {
    			return false;
    		}
        }
}
