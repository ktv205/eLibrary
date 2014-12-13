package com.example.elibrary.controllers;

import com.example.elibrary.R;
import com.example.elibrary.controllers.Logout.OnLogoutSuccessful;
import com.example.elibrary.models.AppPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Profile extends Activity implements OnLogoutSuccessful {
	private static final String TAG="Profile";
	private Menu menuGlobal;
	private SharedPreferences authPref;
	private int auth;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		context=getApplicationContext();
		authPref=getSharedPreferences(this, AppPreferences.Auth.AUTHPREF);
		if (isConntected()) {
			Log.d(TAG, "internet connected");
		}else{
			TextView text=new TextView(this);
			text.setText("Not connected to a network!");
			setContentView(text);
		}
	}
	public boolean isConntected() {
		Log.d(TAG, "isConnected method");
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			return false;
		}

	}
	@Override
	protected void onResume() {
		super.onResume();
		if (isConntected()) {
			Log.d(TAG, "internet connected");
		}else{
			TextView text=new TextView(this);
			text.setText("Not connected to a network!");
			setContentView(text);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.main, menu);
		menuGlobal = menu;
		setMenuName();
		return true;
	}
	public SharedPreferences getSharedPreferences(Context context, String name) {
		Log.d(TAG, "getSharedPreferences method");
		return context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public void setMenuName() {
		Log.d(TAG, "setMenuName");
		if (menuGlobal != null) {
			MenuItem item = menuGlobal.findItem(R.id.name_account_menu);
			item.setTitle(authPref.getString(AppPreferences.Auth.KEY_NAME,
					"Name"));
		}
	}
	public void checkForAuthentication() {
		if (new CheckAuthentication().checkForAuthentication(context)) {
			Log.d(TAG, "in checkForAuthentication and it is true");
			setMenuName();
		} else {
			Intent intent = new Intent(this, Authentication.class);
			startActivity(intent);
			finish();
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}else if(id==R.id.settings_logout){
			Log.d(TAG, "clicked logout");
			Logout logout = new Logout(this);
			auth = authPref.getInt(AppPreferences.AUTH_KEY, -1);
			if (auth == AppPreferences.Auth.GOOGLE_AUTH) {
				logout.logoutFromGoogle();
			} else if (auth == AppPreferences.Auth.FACEBOOK_AUTH) {
				logout.logoutFromFacebook();
			} else {
				logout.clearSharedPref();
			}
		}else if(id==R.id.settings_library){
			startActivity(new Intent(this, MainActivity.class));
		}else if (id == R.id.settings_uploads) {
			startActivity(new Intent(this, Uploads.class));
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onCleardFields(boolean cleared) {
		if(cleared){
			checkForAuthentication();
		}
		
	}
}
