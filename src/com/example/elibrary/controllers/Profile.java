package com.example.elibrary.controllers;

import com.example.elibrary.R;
import com.example.elibrary.controllers.Logout.OnLogoutSuccessful;
import com.example.elibrary.models.AppPreferences;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;

public class Profile extends Activity implements OnLogoutSuccessful {
	private static final String TAG = "Profile";
	private SharedPreferences authPref;
	private Context context;
	private Menu menuGlobal;
	private int auth;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "internet connected");
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
			} else {
				logout();
			}
		} else {
			noConnectionView();
		}
	}

	@Override
	public void startActivity(Intent intent) {
		Log.d(TAG, "in onStartActivity");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.d(TAG, "intent.getAction");
			intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE,
					AppPreferences.FRIENDSEARCH);
		}
		super.startActivity(intent);
	}

	@SuppressLint("InflateParams")
	public void noConnectionView() {
		View view = mInflater.inflate(R.layout.inflate_noconnection, null,
				false);
		setContentView(view);
		Button reload = (Button) view
				.findViewById(R.id.noconntection_button_reload);
		reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Profile.this, Profile.class));
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "onCreate");
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
			} else {
				logout();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		ComponentName cn = new ComponentName(this, SearchActivity.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
		menuGlobal = menu;
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
		if (id == R.id.settings_logout) {
			Log.d(TAG, "clicked logout");
			Logout logout = new Logout(this);
			auth = authPref.getInt(AppPreferences.Auth.KEY_AUTH, -1);
			Log.d(TAG,"auth in logout->"+auth);
			if (auth == AppPreferences.Auth.GOOGLE_AUTH) {
				logout.logoutFromGoogle();
			} else if (auth == AppPreferences.Auth.FACEBOOK_AUTH) {
				logout.logoutFromFacebook();
			} else {
				logout.clearSharedPref();
			}
		} else if (id == R.id.settings_library) {
			startActivity(new Intent(this, MainActivity.class));
		} else if (id == R.id.settings_uploads) {
			startActivity(new Intent(this, Uploads.class));
		} else if (id == R.id.settings_friends) {
			startActivity(new Intent(this, Friends.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCleardFields(boolean cleared) {
		if (cleared) {
			logout();
		}

	}

	public void logout() {
		Intent intent = new Intent(this, Authentication.class);
		startActivity(intent);
		finish();
	}
}
