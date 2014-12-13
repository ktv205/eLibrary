package com.example.elibrary.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.RequestParams;
import com.example.elibrary.models.UserModel;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PasswordReSubmission extends Activity implements OnClickListener {
	private EditText password_edittext, reType_edittext;
	private String password, reType;
	private Button submit;
	private UserModel user;
	private final static int PASSWORD_EMPTY = 2;
	private final static int RETYPE_EMPTY = 3;
	private final static int DONT_MATCH = 4;
	private final static int RESULT_OK = 5;
	private final static String TAG="PasswordReSubmission";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passwordresubmission);
		initialize();
	}

	public void initialize() {
		password_edittext = (EditText) findViewById(R.id.passwordresubmission_edittext_password);
		reType_edittext = (EditText) findViewById(R.id.passwordresubmission_edittext_reenter);
		submit = (Button) findViewById(R.id.passwordresubmission_edittext_submit);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		String message;
		if (id == submit.getId()) {
			int flag = getTextFromFields();

		}

	}

	public RequestParams setParams() {
		RequestParams params = new RequestParams();
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/lib/includes/register.inc.php");
		params.setMethod("POST");
		params.setParam("username", user.getName());
		params.setParam("email", user.getEmail());
		params.setParam("p", user.getPassword());
		params.setParam("auth", user.getAuth());
		return params;

	}

	public int getTextFromFields() {
		password = password_edittext.getText().toString();
		reType = reType_edittext.getText().toString();
		if (password.isEmpty()) {
			return PASSWORD_EMPTY;
		} else if (reType.isEmpty()) {
			return RETYPE_EMPTY;
		} else if (!password.equals(reType)) {
			return DONT_MATCH;
		} else {
			return RESULT_OK;
		}
	}
	public void createUserModel() {
		user.setPassword(password);
		user.setAuth(AppPreferences.Auth.EMAIL_ENUM);
	}
	public class PasswordReSubmissionAsyncTask extends AsyncTask<RequestParams,Void,String>{

		@Override
		protected String doInBackground(RequestParams... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	public String[] parseJsonObject(String result) {
		Log.d(TAG, "parseJsonObject");
		JSONObject obj = null;
		try {
			obj = new JSONObject(result);
			if (obj.getInt("success") == 1) {
				Log.d(TAG, "obj.getInt(success)==1");
				return new String[] { String.valueOf(obj.getInt("user_id")),
						obj.getString("user_name") };
			} else {
				Log.d(TAG, "login failed in parseObject");
				return null;
			}
		} catch (JSONException e) {
			Log.d(TAG, "login failed in parseObject in catch");
			e.printStackTrace();
			return null;
		}

	}
}
