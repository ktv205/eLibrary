package com.example.elibrary.controllers;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;

public class GoogleFragment extends Fragment implements ConnectionCallbacks,
		OnConnectionFailedListener {
	/* Request code used to invoke sign in user interactions. */

	private GoogleApiClient mGoogleApiClient;
	/*
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	Button button;
	private ConnectionResult mConnectionResult;
	View view;
	private final static String TAG = "GoogleFragment";
	private SharedPreferences authPref;
	private SharedPreferences.Editor edit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "in onCreateView");
		view = inflater.inflate(R.layout.fragment_google, container, false);
		mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		view.findViewById(R.id.login_button_google_fragment)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d(TAG, "in click listner");
						if (!mGoogleApiClient.isConnecting()) {
							mSignInClicked = true;
							resolveSignInError();
						}

					}
				});

	}

	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();

	}

	@Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				getActivity().startIntentSenderForResult(
						mConnectionResult.getResolution().getIntentSender(),
						AppPreferences.Codes.RC_SIGN_IN, null, 0, 0, 0);
			} catch (IntentSender.SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			mConnectionResult = result;
			if (mConnectionResult != null) {
			}

			if (mSignInClicked) {
				resolveSignInError();
			}
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		Person currentPerson = Plus.PeopleApi
				.getCurrentPerson(mGoogleApiClient);
		String name = currentPerson.getDisplayName();
		int auth = 0;
		String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
		Image picture = currentPerson.getImage();
		String imageUri = picture.toString();
		String id = currentPerson.getId();
		setSharedPreferences(name, email, id, imageUri, auth);
		startMainActivity();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult out side");
		if (requestCode == AppPreferences.Codes.RC_SIGN_IN) {
			Log.d(TAG, "onActivityResult RC_SIGN_IN");
			if (resultCode != Activity.RESULT_OK) {
				Log.d(TAG, "onActivityResult resultCode != Activity.RESULT_OK");
				mSignInClicked = false;
			}
			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				Log.d("GoogleAuth", "!mGoogleApiClient.isConnecting()");
				mGoogleApiClient.connect();
			}
		}
	}

	public void setSharedPreferences(String name, String email, String id,
			String imageUri, int auth) {
		authPref = getSharedPreferences(getActivity(),
				AppPreferences.Auth.AUTHPREF);
		edit = authPref.edit();
		edit.putString(AppPreferences.Auth.KEY_NAME, name);
		edit.putString(AppPreferences.Auth.KEY_EMAIL, email);
		edit.putString(AppPreferences.Auth.KEY_GOOGLEID, id);
		edit.putInt(AppPreferences.Auth.KEY_AUTH, auth);
		edit.putString(AppPreferences.Auth.KEY_PICTURE, imageUri);
		edit.commit();
	}

	public SharedPreferences getSharedPreferences(Context context, String name) {

		return context.getSharedPreferences(name, Context.MODE_PRIVATE);

	}

	public void startMainActivity() {
		Intent intent = new Intent(getActivity(), MainActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

}
