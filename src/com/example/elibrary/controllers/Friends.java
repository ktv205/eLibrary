package com.example.elibrary.controllers;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.elibrary.R;
import com.example.elibrary.controllers.Logout.OnLogoutSuccessful;
import com.example.elibrary.models.AppPreferences;

public class Friends extends Activity implements OnLogoutSuccessful {
	private SharedPreferences authPref;
	private Context context;
	private Menu menuGlobal;
	private int auth;
	private static final String TAG = "Friends";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		context = getApplicationContext();
		checkForAuthentication();
		if (isConntected()) {
			Log.d(TAG, "internet connected");

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkForAuthentication();
		if (isConntected()) {
			Log.d(TAG, "internet connected");

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG,"onCreateOptionsmenu");
		getMenuInflater().inflate(R.menu.main, menu);
		menuGlobal=menu;
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		ComponentName cn = new ComponentName(this, SearchActivity.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
		setMenuName();
		return true;
	}

	public void setMenuName() {
		Log.d(TAG, "setMenuName");
		if (menuGlobal != null) {
			MenuItem item = menuGlobal.findItem(R.id.name_account_menu);
			item.setTitle(authPref.getString(AppPreferences.Auth.KEY_NAME,
					"Name"));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.name_account_menu) {
			startActivity(new Intent(this, Profile.class));
		} else if (id == R.id.settings_logout) {
			Log.d(TAG, "clicked logout");
			Logout logout = new Logout(this);
			auth = getSharedPreferences(AppPreferences.Auth.AUTHPREF,
					MODE_PRIVATE).getInt(AppPreferences.AUTH_KEY, -1);
			if (auth == AppPreferences.Auth.GOOGLE_AUTH) {
				logout.logoutFromGoogle();
			} else if (auth == AppPreferences.Auth.FACEBOOK_AUTH) {
				logout.logoutFromFacebook();
			} else {
				logout.clearSharedPref();
			}
		} else if (id == R.id.settings_uploads) {
			startActivity(new Intent(this, Uploads.class));
		} else if (id == R.id.settings_library) {
			startActivity(new Intent(this, MainActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	public void checkForAuthentication() {
		authPref = getSharedPreferences(context, AppPreferences.Auth.AUTHPREF);
		if (new CheckAuthentication().checkForAuthentication(context)) {
			Log.d(TAG, "in checkForAuthentication and it is true");

			// Log.d(TAG, "user_pref->" + auth);
			setMenuName();
		} else {
			Intent intent = new Intent(this, Authentication.class);
			startActivity(intent);
			finish();
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

	public SharedPreferences getSharedPreferences(Context context, String name) {
		Log.d(TAG, "getSharedPreferences method");
		return context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	@Override
	public void onCleardFields(boolean cleared) {
		if (cleared) {
			checkForAuthentication();
		}

	}
}
