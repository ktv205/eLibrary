package com.example.elibrary.controllers;

import com.example.elibrary.R;
import com.example.elibrary.controllers.Logout.OnLogoutSuccessful;
import com.example.elibrary.models.AppPreferences;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnLogoutSuccessful {
	private static final String TAG = "MainActivity";
	private SharedPreferences authPref;
	private Context context;
	private Menu menuGlobal;
	private int auth;
	private LinearLayout parentLinear;
	private ScrollView scrollView;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate");
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "internet connected");
			parentLinear = new LinearLayout(this);
			parentLinear.setOrientation(LinearLayout.VERTICAL);
			scrollView = (ScrollView) findViewById(R.id.main_scrollview_parent);

			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
				fetchData();
				fillWithBooks();
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
				startActivity(new Intent(MainActivity.this, MainActivity.class));
				finish();
			}
		});
	}

	public void fetchData() {

	}

	@SuppressLint("InflateParams")
	public void fillWithBooks() {
		Log.d(TAG, "fillWithBooks");
		for (int i = 0; i < 10; i++) {
			View singleCategory = mInflater.inflate(
					R.layout.inflate_single_category, null, false);
			TextView textView = (TextView) singleCategory
					.findViewById(R.id.single_category_textview_book_category);
			LinearLayout horizontal = (LinearLayout) singleCategory
					.findViewById(R.id.single_category_linearlayout_horizontal);
			textView.setText("category");

			for (int j = 0; j < 20; j++) {
				View singleBook = mInflater.inflate(
						R.layout.inflate_singlebook, null, false);
				ImageView imageView = (ImageView) singleBook
						.findViewById(R.id.single_book_cover);
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(MainActivity.this, Book.class));

					}
				});
				horizontal.addView(singleBook);
			}
			if (parentLinear != null) {
				parentLinear.addView(singleCategory);

			}
		}
		scrollView.addView(parentLinear);
		setContentView(scrollView);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
				// fetchData();
				// fillWithBooks();
			} else {
				logout();
			}
		}else{
			noConnectionView();
		}

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
	public void startActivity(Intent intent) {
		Log.d(TAG, "in onStartActivity");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.d(TAG, "intent.getAction");
			intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE,
					AppPreferences.BOOKSEARCH);
		}
		super.startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.name_account_menu) {
			startActivity(new Intent(this, Profile.class));
		} else if (id == R.id.settings_logout) {
			Log.d(TAG, "clicked logout");
			Logout logout = new Logout(this);
			auth = authPref.getInt(AppPreferences.Auth.KEY_AUTH, -1);
			Log.d(TAG, "auth in logout->" + auth);
			if (auth == AppPreferences.Auth.GOOGLE_AUTH) {
				Log.d(TAG, "logout fron google");
				logout.logoutFromGoogle();
			} else if (auth == AppPreferences.Auth.FACEBOOK_AUTH) {
				Log.d(TAG, "logout fron facebook");
				logout.logoutFromFacebook();
			} else {
				Log.d(TAG, "logout from email");
				logout.clearSharedPref();
			}
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
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
				// fetchData();
				// fillWithBooks();
			} else {
				logout();
			}
		}

	}

	public void logout() {
		Intent intent = new Intent(this, Authentication.class);
		startActivity(intent);
		finish();
	}

	public void setParams() {

	}

	public class FetchBooksAsyncTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
		}

	}
}
