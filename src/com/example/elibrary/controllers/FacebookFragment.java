package com.example.elibrary.controllers;

import java.util.Arrays;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FacebookFragment extends Fragment implements GraphUserCallback {
	private static final String TAG = "FacebookFragment";
	private UiLifecycleHelper uiHelper;
	private SharedPreferences authPref;
	private SharedPreferences.Editor edit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "in onCreate");
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "in onCreateView");
		View view = inflater.inflate(R.layout.fragment_facebook, container,
				false);
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.login_button_facebook_fragment);
		authButton.setReadPermissions(Arrays.asList("user_location",
				"user_birthday", "user_likes", "email"));
		authButton.setFragment(this);

		return view;
	}

	@SuppressWarnings("deprecation")
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Request.executeMeRequestAsync(session, this);
			Log.i(TAG, "Logged in...");
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		Session session = Session.getActiveSession();
		if (session == null) {
		}
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		} else {
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onCompleted(GraphUser user, Response response) {
		String name = user.getFirstName() + " " + user.getLastName();
		String email = user.getProperty("email").toString();
		String id = user.getId();
		String imageUri = "https://graph.facebook.com/" + id
				+ "/picture?type=normal";
		int auth = 1;
		setSharedPreferences(name, email, id, imageUri, auth);
		startMainActivity();
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
	public void startMainActivity(){
		Intent intent=new Intent(getActivity(), MainActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

}
