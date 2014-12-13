package com.example.elibrary.controllers;

import com.example.elibrary.R;
import com.example.elibrary.controllers.AuthenticationFragment.OnClickAuthentication;
import com.example.elibrary.controllers.EmailFragment.OnClickEmailSignup;
import com.example.elibrary.models.AppPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

//import android.widget.LinearLayout;

public class Authentication extends FragmentActivity implements
		OnClickAuthentication, OnClickEmailSignup {
	private int fragmentFlag = 0;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private static final String TAG = "Authentication";

	// private LinearLayout authLinear;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		// authLinear=(LinearLayout)findViewById(R.id.linear_authentication);
		Log.d(TAG, "in onCreate");
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		AuthenticationFragment fragment = new AuthenticationFragment();
		fragmentTransaction.add(R.id.linear_authentication, fragment,
				AppPreferences.AUTH_TAG);
		fragmentTransaction.commit();
		fragmentFlag = 1;
	}

	@Override
	public void onClickAuthButton(int flag) {
		Bundle bundle = new Bundle();
		if (flag == AppPreferences.SIGNIN) {
			bundle.putInt(AppPreferences.AUTH_KEY, AppPreferences.SIGNIN);
		} else {
			bundle.putInt(AppPreferences.AUTH_KEY, AppPreferences.SIGNUP);
		}
		Log.d(TAG, "in onClickAuthButton->" + flag);
		fragmentTransaction = fragmentManager.beginTransaction();
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(
				AppPreferences.AUTH_TAG);
		fragmentTransaction.remove(fragment);
		FacebookFragment fb = new FacebookFragment();
		GoogleFragment g = new GoogleFragment();
		EmailFragment emailFragment = new EmailFragment();
		fragmentTransaction.add(R.id.linear_authentication, fb,
				AppPreferences.FB_TAG);
		// fragmentTransaction.commit();
		// fragmentTransaction=fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.linear_authentication, g,
				AppPreferences.G_TAG);
		// fragmentTransaction.commit();

		// fragmentTransaction = fragmentManager.beginTransaction();
		emailFragment.setArguments(bundle);
		fragmentTransaction.add(R.id.linear_authentication, emailFragment,
				AppPreferences.EMAIL_TAG);
		fragmentTransaction.commit();
		fragmentFlag = 2;

	}

	@Override
	protected void onResume() {
		super.onResume();
		 if(new CheckAuthentication().checkForAuthentication(this)){
		   //startActivity(new Intent(this,MainActivity.class));
			 fragmentFlag=1;
			 onBackPressed();
		   
		 }
		
	}

	@Override
	public void onClickSignupButton(int flag) {
		Log.d(TAG, "inOnClickSignupButton");
		Intent intent;
		if (flag == AppPreferences.SIGNIN) {
			intent = new Intent(this, Signin.class);
			startActivity(intent);
		} else {
			intent = new Intent(this, Signup.class);
			startActivity(intent);
		}

	}

	@Override
	protected void onActivityResult(int REQUEST_CODE, int RESPONSE_CODE,
			Intent data) {
		Log.d(TAG, "in onActivityResult");
		// TODO Auto-generated method stub
		if (REQUEST_CODE == AppPreferences.Codes.RC_SIGN_IN) {
			Log.d(TAG, "Request code is for google plus login");
			GoogleFragment fragment = (GoogleFragment) getSupportFragmentManager()
					.findFragmentByTag(AppPreferences.G_TAG);
			fragment.onActivityResult(REQUEST_CODE, RESPONSE_CODE, data);
		}
		super.onActivityResult(REQUEST_CODE, RESPONSE_CODE, data);
	}
	@Override
	public void onBackPressed() {
		if(fragmentFlag==1){
			super.onBackPressed();
		}else if(fragmentFlag==2){
			fragmentManager = getSupportFragmentManager();
			fragmentTransaction = fragmentManager.beginTransaction();
			FacebookFragment fb = (FacebookFragment) getSupportFragmentManager().findFragmentByTag(AppPreferences.FB_TAG);
			GoogleFragment g = (GoogleFragment) getSupportFragmentManager().findFragmentByTag(AppPreferences.G_TAG);
			EmailFragment emailFragment = (EmailFragment) getSupportFragmentManager().findFragmentByTag(AppPreferences.EMAIL_TAG);
			fragmentTransaction.remove(fb);
			fragmentTransaction.remove(g);
			fragmentTransaction.remove(emailFragment);
			AuthenticationFragment fragment = new AuthenticationFragment();
			fragmentTransaction.add(R.id.linear_authentication, fragment,
					AppPreferences.AUTH_TAG);
			fragmentTransaction.commit();
			fragmentFlag=1;
			
		}
		
	}
}
