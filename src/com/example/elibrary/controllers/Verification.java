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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification);
		user_id = getIntent().getExtras().getInt(
				AppPreferences.PutExtraKeys.PUTEXTRA_USERID);
		initialize();
	}

	public void initialize() {
		code_edittext = (EditText) findViewById(R.id.code_edittext_verification);
		submit = (Button) findViewById(R.id.submit_button_verification);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		String message;
		if (id == submit.getId()) {
			int flag = getTextFromFields();
			if (flag == CODE_EMPTY) {
				code_edittext.requestFocus();
				message = "code cant be empty";
			} else {
				message = "everything looks good";
				createUserModel();
				// RequestParams params = setParams();
				// new AuthAsyncTask(user, this).execute(params);
			}
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}

	}

	public int getTextFromFields() {
		code = code_edittext.getText().toString();
		if (code.isEmpty()) {
			return CODE_EMPTY;
		} else {
			return RESULT_OK;
		}
	}

	public RequestParams setParams() {
		RequestParams params = new RequestParams();
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/lib/includes/register.inc.php");
		params.setMethod("POST");
		params.setParam("user_id", String.valueOf(user.getUser_id()));
		params.setParam("code", user.getCode());
		return params;

	}

	public void createUserModel() {
		user = new UserModel();
		user.setCode(code);
		user.setUser_id(user_id);
	}

	public class VerificationAsyncTask extends
			AsyncTask<RequestParams, Void, String> {

		@Override
		protected String doInBackground(RequestParams... params) {

			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			parseJsonObject(result);
		}

	}

	public void parseJsonObject(String result) {
		JSONObject obj = null;
		try {
			obj = new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
