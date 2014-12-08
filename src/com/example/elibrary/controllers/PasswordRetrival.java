package com.example.elibrary.controllers;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.RequestParams;
import com.example.elibrary.models.UserModel;

public class PasswordRetrival extends Activity implements OnClickListener {
	private EditText email_edittext;
	private Button submit;
	private final static String TAG = "PasswordRetrival";
	private String email;
	private final static int EMAIL_EMPTY = 1;
	private UserModel user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passwordretrival);
		Log.d(TAG,"onCreate");
		user = getIntent().getExtras().getParcelable(
				AppPreferences.Auth.KEY_PARCELABLE_SIGNIN_PASSWORDRETRIVAL);
		initialize();

	}

	public void initialize() {
		Log.d(TAG, "initialize()");
		email_edittext = (EditText) findViewById(R.id.retrival_edittext_email);
		submit = (Button) findViewById(R.id.retrival_submit_button);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Log.d(TAG,"onClick");
		int id = v.getId();
		String message;
		if (id == submit.getId()) {
			int flag = getTextFromFields();
			if (flag == EMAIL_EMPTY) {
				email_edittext.requestFocus();
				message = "email cant be empty";
			} else {

				message = "";
			}
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}

	}

	public int getTextFromFields() {
		Log.d(TAG,"getTextFromFields");
		email = email_edittext.getText().toString();
		if (email.isEmpty()) {
			return EMAIL_EMPTY;
		} else {
			return RESULT_OK;
		}
	}

	public RequestParams setParams() {
		RequestParams params = new RequestParams();
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/lib/includes/process_login.php");
		params.setMethod("POST");
		params.setParam("email", user.getEmail());
		params.setParam("p", user.getPassword());
		params.setParam("mobile", "1");
		return params;

	}

	public void createUserModel() {
		user = new UserModel();
		user.setEmail(email);
		user.setAuth(AppPreferences.Auth.EMAIL_ENUM);
	}

	public class PasswordRetrivalAsyncTask extends
			AsyncTask<RequestParams, Void, String> {

		@Override
		protected String doInBackground(RequestParams... params) {

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

}
