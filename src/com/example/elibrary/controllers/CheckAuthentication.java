package com.example.elibrary.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.elibrary.models.AppPreferences;

public class CheckAuthentication {
	private static final String TAG="CheckAuthentication";
	private SharedPreferences authPref;
	private int auth;
	public boolean checkForAuthentication(Context context) {
		authPref = getSharedPreferences(context, AppPreferences.Auth.AUTHPREF);
		auth = authPref.getInt(AppPreferences.Auth.KEY_AUTH, -1);
		Log.d(TAG, "in checkForAuthentication");
		Log.d(TAG, "user_pref->" + auth);
		if (auth == 1 //AppPreferences.Auth.EMAIL_AUTH
				|| auth == 2 //AppPreferences.Auth.FACEBOOK_AUTH
				|| auth == 0 //AppPreferences.Auth.GOOGLE_AUTH
				) {
			Log.d(TAG,"in true");
			return true;
		} else {
			Log.d(TAG,"in false");
			return false;
		}
	}
	public SharedPreferences getSharedPreferences(Context context, String name) {
		Log.d(TAG, "getSharedPreferences method");
		return context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}
}
