package com.example.elibrary.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.RequestParams;
import com.example.elibrary.models.UserModel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AuthAsyncTask extends AsyncTask<RequestParams, Void, String> {
	ProgressDialog dialog;
	private final static String TAG = "AuthAsyncTask";
	UserModel user;
	Context context;

	public AuthAsyncTask(UserModel user, Context context) {
		this.user = user;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		Log.d(TAG, "onPreExecute");
		dialog = new ProgressDialog(context);
		dialog.show();
	}

	@Override
	protected String doInBackground(RequestParams... params) {
		Log.d(TAG, "doInBackground");
		return new HttpManager().sendUserData(params[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		dialog.dismiss();
		Log.d(TAG, "onPostExecute");
		int user_id = 0;
		Intent intent;
		if (user.getAuth() == AppPreferences.Auth.EMAIL_ENUM) {
			Log.d(TAG, "onPostExecute EMAIL_ENUM");
			Toast.makeText(context, "check your mail", Toast.LENGTH_SHORT)
					.show();
			intent = new Intent(context, Verification.class);
			user_id = parseJsonString(result);
			intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_USERID,
					user_id);
			intent.putExtra(
					AppPreferences.Auth.KEY_PARCELABLE_SIGNUP_VERIFICATION,
					user);
			context.startActivity(intent);
			((Activity) context).finish();
		} else {
			Toast.makeText(context, "successfully signed up",
					Toast.LENGTH_SHORT).show();
			intent = new Intent(context, MainActivity.class);
			intent.putExtra(
					AppPreferences.Auth.KEY_PARCELABLE_SIGNUP_VERIFICATION,
					user);
			context.startActivity(intent);
			((Activity) context).finish();
		}
	}

	public int parseJsonString(String result) {
		Log.d(TAG, "in parseJsonString");
		JSONObject obj = null;
		int user_id;
		try {
			obj = new JSONObject(result.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			if (obj.getBoolean("success")) {
				user_id = obj.getInt("user_id");
				Log.d(TAG, "user_id->" + user_id);
				return user_id;
			} else {
				int error_id = obj.getInt("error_id");
				Log.d(TAG, "Error_id->" + error_id);
				return 0;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}

	}

}
