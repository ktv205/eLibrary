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
import android.widget.Toast;

public class Signin extends Activity implements OnClickListener {
	private EditText email_edittext, password_edittext;
	private Button submit;
	private String email, password;
	private final static int EMAIL_EMPTY = 1;
	private final static int PASSWORD_EMPTY = 2;
	private final static int RESULT_OK = 5;
	private UserModel user;
	private static final String TAG="Signin";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		initialize();
	}

	public void initialize() {
		email_edittext = (EditText) findViewById(R.id.email_edittext_signup);
		password_edittext = (EditText) findViewById(R.id.password_edittext_signup);
		submit = (Button) findViewById(R.id.signin_button_signin);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		String message;
		if (id == submit.getId()) {
			int flag = getTextFromFields();
			if (flag == EMAIL_EMPTY) {
				email_edittext.requestFocus();
				message = "email cant be empty";
			} else if (flag == PASSWORD_EMPTY) {
				password_edittext.requestFocus();
				message = "password cant be empty";
			} else {
				message = "everything looks good";
				createUserModel();
				RequestParams params = setParams();
				new AuthAsyncTask(user, this).execute(params);
			}
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}

	public int getTextFromFields() {
		email = email_edittext.getText().toString();
		password = password_edittext.getText().toString();
		if (email.isEmpty()) {
			return EMAIL_EMPTY;
		} else if (password.isEmpty()) {
			return PASSWORD_EMPTY;
		} else {
			return RESULT_OK;
		}
	}

	public RequestParams setParams() {
		RequestParams params = new RequestParams();
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/lib/includes/register.inc.php");
		params.setMethod("POST");
		params.setParam("email", user.getEmail());
		params.setParam("p", user.getPassword());
		return params;

	}

	public void createUserModel() {
		user = new UserModel();
		user.setEmail(email);
		user.setPassword(password);
		user.setAuth(AppPreferences.Auth.EMAIL_ENUM);
	}
	public class SignInAsycTask extends AsyncTask<RequestParams, Void, String>{

		@Override
		protected String doInBackground(RequestParams... params) {
			// TODO Auto-generated method stub
			return new HttpManager().sendUserData(params[0]);
		}
        @Override
        protected void onPostExecute(String result) {
        	Log.d(TAG,"postExecute");
        	parseJsonObject(result);
        }
	}
	public void parseJsonObject(String result){
		JSONObject obj=null;
		try {
		    obj=new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	} 
}
