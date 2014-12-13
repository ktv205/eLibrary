package com.example.elibrary.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.RequestParams;
import com.example.elibrary.models.UserModel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Verification extends Activity implements OnClickListener {
	private EditText code_edittext;
	private Button submit;
	private String code;
	private UserModel user;
	private final static int CODE_EMPTY = 1;
	private int user_id;
	private final static String TAG = "Verification";
	private SharedPreferences authPref;
	private SharedPreferences.Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_verification);
		user_id = getIntent().getExtras().getInt(
				AppPreferences.PutExtraKeys.PUTEXTRA_USERID);
		user = getIntent().getExtras().getParcelable(
				AppPreferences.Auth.KEY_PARCELABLE_SIGNUP_VERIFICATION);
		initialize();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (new CheckAuthentication().checkForAuthentication(this)) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	public void initialize() {
		Log.d(TAG, "initialize()");
		code_edittext = (EditText) findViewById(R.id.code_edittext_verification);
		submit = (Button) findViewById(R.id.submit_button_verification);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick(View v)");
		int id = v.getId();
		String message;
		if (id == submit.getId()) {
			Log.d(TAG, "id==submit.getId()");
			int flag = getTextFromFields();
			if (flag == CODE_EMPTY) {
				code_edittext.requestFocus();
				message = "code cant be empty";
			} else {
				message = "everything looks good";
				createUserModel();
				RequestParams params = setParams();
				new VerificationAsyncTask().execute(params);
			}
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}

	}

	public int getTextFromFields() {
		Log.d(TAG, "getTextFromFields");
		code = code_edittext.getText().toString();
		if (code.isEmpty()) {
			return CODE_EMPTY;
		} else {
			return RESULT_OK;
		}
	}

	public RequestParams setParams() {
		Log.d(TAG, "RequestParams");
		RequestParams params = new RequestParams();
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/lib/includes/register.inc.php");
		params.setMethod("GET");
		params.setParam("email", user.getEmail());
		params.setParam("code", user.getCode());
		return params;

	}

	public void createUserModel() {
		Log.d(TAG, "createUserModel");
		user.setCode(code);
		user.setUser_id(user_id);

	}

	public class VerificationAsyncTask extends
			AsyncTask<RequestParams, Void, String> {

		@Override
		protected String doInBackground(RequestParams... params) {
			Log.d(TAG, "doInBackGround");
			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "onPostExecute");
			if (parseJsonObject(result) != 0) {
				Log.d(TAG, "onPostExecute and result!=0");
				user.setUser_id(user_id);
				setSharedPreferences();
				startActivity(new Intent(Verification.this, MainActivity.class));
				finish();
			}
		}

	}

	public int parseJsonObject(String result) {
		JSONObject obj = null;
		Log.d(TAG, result);
		try {
			obj = new JSONObject(result);
			if (obj.getInt("success") == 1) {
				user_id = obj.getInt("user_id");
				return user_id;
			} else {
				return 0;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}

	}

	public void setSharedPreferences() {
		Log.d(TAG, "setSharedPreferences");
		authPref = getSharedPreferences(AppPreferences.Auth.AUTHPREF,
				MODE_PRIVATE);
		edit = authPref.edit();
		edit.putString(AppPreferences.Auth.KEY_NAME, user.getName());
		edit.putString(AppPreferences.Auth.KEY_EMAIL, user.getEmail());
		edit.putInt(AppPreferences.Auth.KEY_AUTH,
				AppPreferences.Auth.EMAIL_AUTH);
		edit.putInt(AppPreferences.Auth.KEY_USERID, user.getUser_id());
		edit.commit();
	}

}
