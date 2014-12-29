package com.example.elibrary.controllers;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Authentication extends Activity {
	private static final String TAG = "Authentication";
	private Context context;
	private LayoutInflater mInflater;
	private SharedPreferences authPref;
	private SharedPreferences.Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		edit = authPref.edit();
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {
				startActivity(new Intent(this, MainActivity.class));
			} else {
				Button signInButton = (Button) findViewById(R.id.authentication_button_signin);
				Button signUpButton = (Button) findViewById(R.id.authentication_button_signup);
				signInButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Authentication.this,
								Signin.class);
						intent.putExtra(AppPreferences.AUTH_KEY,
								AppPreferences.SIGNIN);
						edit.putInt(AppPreferences.AUTH_KEY,
								AppPreferences.SIGNIN);
						edit.commit();
						startActivity(intent);

					}
				});
				signUpButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Authentication.this,
								Signup.class);
						intent.putExtra(AppPreferences.AUTH_KEY,
								AppPreferences.SIGNUP);
						edit.putInt(AppPreferences.AUTH_KEY,
								AppPreferences.SIGNUP);
						edit.commit();
						startActivity(intent);

					}
				});
			}
		} else {
			noConnectionView();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {
				startActivity(new Intent(this, MainActivity.class));
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
				startActivity(new Intent(Authentication.this,
						MainActivity.class));
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
