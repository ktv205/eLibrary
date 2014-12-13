package com.example.elibrary.controllers;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.RequestParams;
import com.example.elibrary.models.UserModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends Activity implements OnClickListener {
	private EditText name_edittext, email_edittext, password_edittext,
			reType_edittext;
	private String name, email, password, reType;
	private Button submit;
	private UserModel user;
	private final static int NAME_EMPTY = 0;
	private final static int EMAIL_EMPTY = 1;
	private final static int PASSWORD_EMPTY = 2;
	private final static int RETYPE_EMPTY = 3;
	private final static int DONT_MATCH = 4;
	private final static int RESULT_OK = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		initialize();
	}

	public void initialize() {
		name_edittext = (EditText) findViewById(R.id.name_edittext_signup);
		email_edittext = (EditText) findViewById(R.id.email_edittext_signup);
		password_edittext = (EditText) findViewById(R.id.password_edittext_signup);
		reType_edittext = (EditText) findViewById(R.id.reenter_edittext_signup);
		submit = (Button) findViewById(R.id.signup_button_signup);
		submit.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// if(new CheckAuthentication().checkForAuthentication(this)){
		// startActivity(new Intent(this,MainActivity.class));
		// finish();
		// }
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		String message;
		if (id == submit.getId()) {
			int flag = getTextFromFields();
			if (flag == NAME_EMPTY) {
				name_edittext.requestFocus();
				message = "name cant be empty";
			} else if (flag == EMAIL_EMPTY) {
				email_edittext.requestFocus();
				message = "email cant be empty";
			} else if (flag == PASSWORD_EMPTY) {
				password_edittext.requestFocus();
				message = "password cant be empty";
			} else if (flag == RETYPE_EMPTY) {
				reType_edittext.requestFocus();
				message = "retype cant be empty";
			} else if (flag == DONT_MATCH) {
				message = "password do not match";
			} else {
				message = "everything looks good";
				createUserModel();
				RequestParams params=setParams();
				new AuthAsyncTask(user,this).execute(params);
			}
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}

	}
	public RequestParams setParams(){
		RequestParams params=new RequestParams();
		params.setURI("http://"+AppPreferences.ipAdd+"/eLibrary/lib/includes/register.inc.php");
		params.setMethod("POST");
		params.setParam("username", user.getName());
		params.setParam("email", user.getEmail());
		params.setParam("p",user.getPassword());
		params.setParam("auth", user.getAuth());
		return params;

	}

	public int getTextFromFields() {
		name = name_edittext.getText().toString();
		email = email_edittext.getText().toString();
		password = password_edittext.getText().toString();
		reType = reType_edittext.getText().toString();
		if (name.isEmpty()) {
			return NAME_EMPTY;
		} else if (email.isEmpty()) {
			return EMAIL_EMPTY;
		} else if (password.isEmpty()) {
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
		user = new UserModel();
		user.setName(name);
		user.setEmail(email);
		user.setPassword(password);
		user.setAuth(AppPreferences.Auth.EMAIL_ENUM);
	}

	


}
