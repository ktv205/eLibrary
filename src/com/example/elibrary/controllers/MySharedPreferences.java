package com.example.elibrary.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MySharedPreferences {
	private static final String TAG = "MySharedPreferences";

	public static SharedPreferences getSharedPreferences(Context context,
			String name) {
		return context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

}
