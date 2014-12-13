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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnLogoutSuccessful {
	private static final String TAG = "MainActivity";
	private SharedPreferences authPref;
	private Context context;
	private Menu menuGlobal;
	private int auth;
	private LinearLayout parent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();

		parent = (LinearLayout) findViewById(R.id.parent_linearlayout_main);
		Log.d(TAG, "onCreate");
		if (isConntected()) {
			Log.d(TAG, "internet connected");
			checkForAuthentication();
		}
		fillWithBooks();

	}

	public void fillWithBooks() {
		Log.d(TAG, "fillWithBooks");
		LinearLayout hLinear = new LinearLayout(context);
		HorizontalScrollView hScrollView = new HorizontalScrollView(context);
		hLinear.setOrientation(LinearLayout.HORIZONTAL);
		for (int i = 0; i < 20; i++) {
			LinearLayout vLinear = new LinearLayout(context);
			vLinear.setOrientation(LinearLayout.VERTICAL);
			TextView text = new TextView(context);
			text.setText("title");
			ImageView image = new ImageView(context);
			image.setImageResource(R.drawable.ic_launcher);
			vLinear.addView(image);
			vLinear.addView(text);
			hLinear.addView(vLinear);

		}
		hScrollView.addView(hLinear);
		parent.addView(hScrollView);
		setContentView(parent);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		checkForAuthentication();

	}

	public SharedPreferences getSharedPreferences(Context context, String name) {
		Log.d(TAG, "getSharedPreferences method");
		return context.getSharedPreferences(name, Context.MODE_PRIVATE);
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
		menuGlobal = menu;
		setMenuName();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.name_account_menu) {
			Log.d(TAG, "clicked name_account_name");
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

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCleardFields(boolean cleared) {
		if (cleared) {
			checkForAuthentication();
		}

	}
}
