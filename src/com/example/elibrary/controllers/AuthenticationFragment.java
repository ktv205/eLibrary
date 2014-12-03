package com.example.elibrary.controllers;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class AuthenticationFragment extends Fragment implements OnClickListener {
	private View view;
	private Button signin, signup;
	private OnClickAuthentication auth;
	private final static String TAG = "AuthenticationFragment";

	public interface OnClickAuthentication {
		public void onClickAuthButton(int flag);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "in onAttach");
		try {
			Log.d("connected", "in onAttach");
			auth = (OnClickAuthentication) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "in onCreateView");
		view = inflater.inflate(R.layout.fragment_authentication, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "in onActivityCreated");
		signin = (Button) view.findViewById(R.id.signin_button_authentication_fragment);
		signup = (Button) view.findViewById(R.id.signup_button_authentication_fragment);
		signin.setOnClickListener(this);
		signup.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "in onClick");
		int id = v.getId();
		if (id == signin.getId()) {
			auth.onClickAuthButton(AppPreferences.SIGNIN);
		} else if (id == signup.getId()) {
			auth.onClickAuthButton(AppPreferences.SIGNUP);
		}

	}

}
