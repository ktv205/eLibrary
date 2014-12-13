package com.example.elibrary.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.elibrary.R;
import com.example.elibrary.controllers.Logout.OnLogoutSuccessful;
import com.example.elibrary.models.AppPreferences;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Files;
import android.util.Log;
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
	private static final int PICK_FILE_REQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uploads);
		context = getApplicationContext();
		authPref = getSharedPreferences(this, AppPreferences.Auth.AUTHPREF);
		if (isConntected()) {
			Log.d(TAG, "internet connected");
			initialize();
		} else {
			TextView text = new TextView(this);
			text.setText("Not connected to a network!");
			setContentView(text);
		}
	}

	public void initialize() {
		uploadButton = (Button) findViewById(R.id.uploads_button_uplaod);
		submitButton = (Button) findViewById(R.id.uploads_button_submit);
		fileNameTextview = (TextView) findViewById(R.id.uploads_textview_filename);
		uploadButton.setOnClickListener(this);
		submitButton.setOnClickListener(this);
	}

	public SharedPreferences getSharedPreferences(Context context, String name) {
		Log.d(TAG, "getSharedPreferences method");
		return context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isConntected()) {
			Log.d(TAG, "internet connected");
		} else {
			TextView text = new TextView(this);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.main, menu);
		
		SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        ComponentName cn = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(cn));
		menuGlobal = menu;
		setMenuName();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.settings_logout) {
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
		} else if (id == R.id.settings_library) {
			startActivity(new Intent(this, MainActivity.class));
		} else if (id == R.id.name_account_menu) {
			startActivity(new Intent(this, Profile.class));
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
	public void onCleardFields(boolean cleared) {
		if (cleared) {
			checkForAuthentication();
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

				Cursor contentCursor = getContentResolver().query(
						data.getData(), null, null, null, null);
				Log.d(TAG, "column count->" + contentCursor.getColumnCount());

				for (int i = 0; i < contentCursor.getColumnCount(); i++) {
					Log.d(TAG, contentCursor.getColumnName(i));
				}
				contentCursor.moveToFirst();
				String documentId = contentCursor.getString(contentCursor
						.getColumnIndex("document_id"));
				Log.d(TAG, documentId + "");
				final File file = new File("/storage/emulated/0/Download/"
						+ contentCursor.getString(contentCursor
								.getColumnIndex("_display_name")));
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						final AmazonS3Client client = new AmazonS3Client(
								new BasicAWSCredentials("AKIAJ4YX333DR4DM4DUQ",
										"kScJ0H1axGW3ztuuuzBJdEYmRunOEYFCz45m4r0l"));

						PutObjectRequest por = new PutObjectRequest(
								"songbirdsongs", "test", file);
						client.putObject(por);

					}
				});
				thread.start();

			}
		}
	}
}
