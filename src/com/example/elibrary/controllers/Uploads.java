package com.example.elibrary.controllers;

import java.io.File;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

public class Uploads extends Activity implements OnLogoutSuccessful,
		OnClickListener {
	private static final String TAG = "Uploads";
	private Menu menuGlobal;
	private SharedPreferences authPref;
	private int auth;
	private Context context;
	private Button uploadButton, submitButton;
	private TextView fileNameTextview;
	private LayoutInflater mInflater;
	private static final int PICK_FILE_REQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uploads);
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "internet connected");
			if (CheckAuthentication.checkForAuthentication(context)) {
				initialize();
			} else {
				logout();
			}

		} else {

		}
	}

	@Override
	public void startActivity(Intent intent) {
		Log.d(TAG, "in onStartActivity");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.d(TAG, "intent.getAction");
			intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE,
					AppPreferences.BOOKSEARCH);
		}
		super.startActivity(intent);
	}

	public void initialize() {
		uploadButton = (Button) findViewById(R.id.uploads_button_uplaod);
		submitButton = (Button) findViewById(R.id.uploads_button_submit);
		fileNameTextview = (TextView) findViewById(R.id.uploads_textview_filename);
		uploadButton.setOnClickListener(this);
		submitButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
			} else {
				logout();
			}
		} else {
			noConnectionView();
		}

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
				startActivity(new Intent(Uploads.this, Uploads.class));
				finish();
			}
		});
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.settings_logout) {
			Log.d(TAG, "clicked logout");
			Logout logout = new Logout(this);
			auth = authPref.getInt(AppPreferences.Auth.KEY_AUTH, -1);
			if (auth == AppPreferences.Auth.GOOGLE_AUTH) {
				logout.logoutFromGoogle();
			} else if (auth == AppPreferences.Auth.FACEBOOK_AUTH) {
				logout.logoutFromFacebook();
			} else {
				logout.clearSharedPref();
			}
		} else if (id == R.id.settings_library) {
			startActivity(new Intent(this, MainActivity.class));
		} else if (id == R.id.name_account_menu) {
			startActivity(new Intent(this, Profile.class));
		} else if (id == R.id.settings_friends) {
			startActivity(new Intent(this, Friends.class));
		}
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

	public void logout() {
		Intent intent = new Intent(this, Authentication.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onCleardFields(boolean cleared) {
		if (cleared) {
			logout();
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == uploadButton.getId()) {
			getFile();
		}

	}

	public void getFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("application/pdf");
		startActivityForResult(intent, PICK_FILE_REQUEST);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_FILE_REQUEST) {
			if (resultCode == RESULT_OK) {
				Log.d(TAG, "URI->" + data.getData().getPath());
				Log.d(TAG, "URI->" + data.getDataString());
				File file = new File(data.getData().getPath());
				if (file.exists()) {
					Log.d(TAG, "file exists");
				} else {
					Log.d(TAG, "file does not exists");
				}
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {

					}
				});
				thread.start();

			}
		}
	}
}
