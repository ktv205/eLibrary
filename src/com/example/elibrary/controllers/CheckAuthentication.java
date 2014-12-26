package com.example.elibrary.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.elibrary.models.AppPreferences;

public class CheckAuthentication {
	private static final String TAG = "CheckAuthentication";
	private static SharedPreferences authPref;
	private static int auth;

	public static boolean checkForAuthentication(Context context) {
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		auth = authPref.getInt(AppPreferences.Auth.KEY_AUTH, -1);
		if (auth == 1 // AppPreferences.Auth.EMAIL_AUTH
				|| auth == 2 // AppPreferences.Auth.FACEBOOK_AUTH
				|| auth == 0 // AppPreferences.Auth.GOOGLE_AUTH
		) {
			return true;
		} else {
			return false;
		}
	}
}
