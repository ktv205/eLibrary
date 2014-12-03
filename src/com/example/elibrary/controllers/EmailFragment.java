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

public class EmailFragment extends Fragment implements OnClickListener {
	private View view;
	private Button signup;
	private OnClickEmailSignup auth;
	private final static String TAG = "EmailFragment";
	private int flag;

	public interface OnClickEmailSignup {
		public void onClickSignupButton(int flag);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "in onAttach");
		try {
			Log.d("connected", "in onAttach");
			auth = (OnClickEmailSignup) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnClickEmailSignup");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "in onCreateView");
		view = inflater.inflate(R.layout.fragment_email, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle=getArguments();
		Log.d(TAG, "in onActivityCreated");
		signup = (Button) view.findViewById(R.id.email_button_email_fragment);
		flag=bundle.getInt(AppPreferences.AUTH_KEY);
		if(flag==AppPreferences.SIGNIN){
			signup.setText(R.string.signin_email_string);
		}else{
			signup.setText(R.string.signup_email_string);
		}
		signup.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "in onClick");
		int id = v.getId();
		 if (id == signup.getId()) {
			auth.onClickSignupButton(flag);
		}

	}

}
