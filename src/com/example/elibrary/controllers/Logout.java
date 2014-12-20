package com.example.elibrary.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.elibrary.models.AppPreferences;
import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

public class Logout implements ConnectionCallbacks, OnConnectionFailedListener {
	private final static String TAG = "Logout";
	GoogleApiClient mGoogleApiClient;
	int id;
	Session session;
	Context context;
	private SharedPreferences pref;
	private SharedPreferences.Editor edit;
	ProgressDialog dialog;
	OnLogoutSuccessful obj;

	public interface OnLogoutSuccessful {
		public void onCleardFields(boolean cleared);
	}

	public Logout(Context context) {
		this.context = context;
		pref = context.getSharedPreferences(AppPreferences.Auth.AUTHPREF,
				Context.MODE_PRIVATE);
		edit = pref.edit();
	}

	public void logoutFromFacebook() {
		Log.d(TAG, "in facebook signout");
		if (Session.getActiveSession() == null) {
			Log.d(TAG, "Session.getActiveSession() is null");
			clearSharedPref();
		} else {
			Log.d(TAG, "Session.getActiveSession() is null");
			session = Session.getActiveSession();
			if (session != null) {
				Log.d(TAG, "session is active");
				session.closeAndClearTokenInformation();
				clearSharedPref();
			} else {
				Log.d(TAG, "session is not active");
			}
		}

	}

	public void logoutFromGoogle() {
		Log.d(TAG, "in logout google");
		mGoogleApiClient = new GoogleApiClient.Builder(context)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		mGoogleApiClient.connect();
		dialog = new ProgressDialog(context);
		dialog.show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(TAG, "connection failed");

	}

	@Override
	public void onConnected(Bundle arg0) {

		Log.d(TAG, "in connected");
		Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
		mGoogleApiClient.disconnect();
		clearSharedPref();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.d(TAG, "connection suspended");

	}

	public void clearSharedPref() {
		edit.putInt(AppPreferences.Auth.KEY_AUTH, -1);
		edit.putString(AppPreferences.Auth.KEY_NAME, "name");
		edit.putString(AppPreferences.Auth.KEY_EMAIL, "email");
		edit.putString(AppPreferences.Auth.KEY_FACEBOOKID, "facebook_id");
		edit.putString(AppPreferences.Auth.KEY_GOOGLEID, "google_id");
		edit.putString(AppPreferences.Auth.KEY_PICTURE, "picture");
		edit.commit();
		Toast.makeText(context, "you are logged out", Toast.LENGTH_LONG).show();
		Log.d(TAG, "cleared the fields");
		obj = (OnLogoutSuccessful) context;
		new LogoutAsyncTask().execute();
		obj.onCleardFields(true);

	}

	public class LogoutAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "do in background");
			URL url = null;
			try {
				url = new URL("http://" + AppPreferences.ipAdd
						+ "/eLibrary/lib/includes/logout.php");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpURLConnection con = null;
			try {
				con = (HttpURLConnection) url.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringBuilder builder = new StringBuilder();
			String line = null;
			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(con.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			BufferedReader buffer = null;
			buffer = new BufferedReader(reader);
			try {
				line = buffer.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (line != null) {
				builder.append(line);
				try {
					line = buffer.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Log.d(TAG, "builder->" + builder.toString());

			return null;
		}

	}

}
