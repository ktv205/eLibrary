package com.example.elibrary.controllers;

import java.util.Arrays;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.RequestParams;
import com.example.elibrary.models.UserModel;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FacebookFragment extends Fragment implements GraphUserCallback {
	private static final String TAG = "FacebookFragment";
	private UiLifecycleHelper uiHelper;
	private SharedPreferences authPref;
	private SharedPreferences.Editor edit;
	private UserModel user;
	private boolean signup = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_facebook, container,
				false);
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.login_button_facebook_fragment);
		authButton.setReadPermissions(Arrays.asList("user_location",
				"user_birthday", "user_likes", "email"));
		authButton.setFragment(this);
		authPref = MySharedPreferences.getSharedPreferences(getActivity(),
				AppPreferences.Auth.AUTHPREF);
		edit = authPref.edit();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (authPref.getInt(AppPreferences.AUTH_KEY, -1) == AppPreferences.SIGNIN) {
			signup = false;
		} else if (authPref.getInt(AppPreferences.AUTH_KEY, -1) == AppPreferences.SIGNUP) {
			signup = true;
		}
	}

	@SuppressWarnings("deprecation")
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Request.executeMeRequestAsync(session, this);
		} else if (state.isClosed()) {
		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {

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
		setSharedPreferences(name, email, id, imageUri);
		setUserModel(name, email, imageUri);
		RequestParams params = setParams();
		sendDataToServer(params);
	}

	public void setSharedPreferences(String name, String email, String id,
			String imageUri) {
		
		edit.putString(AppPreferences.Auth.KEY_NAME, name);
		edit.putString(AppPreferences.Auth.KEY_EMAIL, email);
		edit.putString(AppPreferences.Auth.KEY_GOOGLEID, id);
		edit.putInt(AppPreferences.Auth.KEY_AUTH,AppPreferences.Auth.FACEBOOK_AUTH);
		edit.putString(AppPreferences.Auth.KEY_PICTURE, imageUri);
		edit.commit();
	}

	public void setUserModel(String name, String email, String imageUri) {
		user = new UserModel();
		user.setName(name);
		user.setEmail(email);
		user.setAuth(AppPreferences.Auth.FACEBOOK_ENUM);
		user.setProfilePic(imageUri);
	}

	public RequestParams setParams() {
		RequestParams params = new RequestParams();
		params.setMethod("POST");
		params.setParam("user_email", user.getEmail());
		params.setParam("user_auth", user.getAuth());
		params.setParam("mobile", "1");

		if (signup) {
			params.setParam("user_name", user.getName());
			params.setURI("http://" + AppPreferences.ipAdd
					+ "/eLibrary/library/index.php/welcome/sign_up");
			params.setParam("user_pic", user.getProfilePic());
		} else {
			params.setURI("http://" + AppPreferences.ipAdd
					+ "/eLibrary/library/index.php/welcome/process_login");

		}
		return params;

	}

	public void sendDataToServer(RequestParams params) {
		new AuthAsyncTask(user, getActivity()).execute(params);
	}

}
