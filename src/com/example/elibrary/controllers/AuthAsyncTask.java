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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AuthAsyncTask extends AsyncTask<RequestParams, Void, String> {
	ProgressDialog dialog;
	private final static String TAG = "AuthAsyncTask";
	UserModel user;
	Context context;
	private SharedPreferences authPref;
	private SharedPreferences.Editor edit;

	public AuthAsyncTask(UserModel user, Context context) {
		this.user = user;
		this.context = context;
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		edit = authPref.edit();

	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.show();
	}

	@Override
	protected String doInBackground(RequestParams... params) {
		return new HttpManager().sendUserData(params[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		dialog.dismiss();
		int user_id = 0;
		user_id = parseJsonString(result);
		Intent intent;
		if (user_id != 0) {
			if (user.getAuth() == AppPreferences.Auth.EMAIL_ENUM) {

				Toast.makeText(context, "check your mail", Toast.LENGTH_SHORT)
						.show();
				intent = new Intent(context, Verification.class);

				intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_USERID,
						user_id);
				intent.putExtra(
						AppPreferences.Auth.KEY_PARCELABLE_SIGNUP_VERIFICATION,
						user);
				context.startActivity(intent);
				((Activity) context).finish();
			} else {
				intent = new Intent(context, MainActivity.class);
				intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_USERID,
						user_id);
				intent.putExtra(
						AppPreferences.Auth.KEY_PARCELABLE_SIGNUP_VERIFICATION,
						user);
				context.startActivity(intent);
				((Activity) context).finish();
			}
		} else {
			Toast.makeText(context, "Please try again", Toast.LENGTH_LONG)
					.show();
		}
	}

	public int parseJsonString(String result) {
		JSONObject obj = null;
		int user_id;
		try {
			obj = new JSONObject(result.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			if (obj.has("success")) {
				if (obj.getInt("success") == 1) {
					user_id = obj.getInt("user_id");
					edit.putInt(AppPreferences.Auth.KEY_USERID, user_id);
					if (obj.has("user_pic")) {
						edit.putString(AppPreferences.Auth.KEY_PICTURE,
								obj.getString("user_pic"));
					}
					edit.commit();
					return user_id;
				} else {
					renderError(obj);
					return 0;
				}
			} else {
				return 0;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}

	}

	public void renderError(JSONObject obj) {
		try {
			Toast.makeText(context, obj.getString("error_msg"),
					Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
