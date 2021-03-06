package com.example.elibrary.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.RequestParams;
import com.example.elibrary.models.UserModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Signin extends FragmentActivity implements OnClickListener {
	private EditText email_edittext, password_edittext;
	private TextView forgot_textview;
	private Button submit;
	private String email, password;
	private final static int EMAIL_EMPTY = 1;
	private final static int PASSWORD_EMPTY = 2;
	private final static int RESULT_OK = 5;
	private UserModel user;
	private static final String TAG = "Signin";
	private SharedPreferences authPref;
	private SharedPreferences.Editor edit;
	private Context context;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {
			} else {
				initialize();
			}
		} else {
			noConnectionView();
		}

	}

	public void initialize() {
		email_edittext = (EditText) findViewById(R.id.email_edittext_signin);
		password_edittext = (EditText) findViewById(R.id.password_edittext_signin);
		submit = (Button) findViewById(R.id.signin_button_signin);
		forgot_textview = (TextView) findViewById(R.id.signup_textview_forgot);
		submit.setOnClickListener(this);
		forgot_textview.setOnClickListener(this);

	}

	@SuppressLint("InflateParams")
	public void noConnectionView() {
		View view = mInflater.inflate(R.layout.inflate_noconnection, null,
				false);
		setContentView(view);
		Button reload = (Button) view
				.findViewById(R.id.noconntection_button_reload);
		reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Signin.this, Signin.class));
				finish();
			}
		});
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
				new SignInAsycTask().execute(params);
			}
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		} else if (id == forgot_textview.getId()) {
			Intent intent = new Intent(this, PasswordRetrival.class);
			intent.putExtra(
					AppPreferences.Auth.KEY_PARCELABLE_SIGNIN_PASSWORDRETRIVAL,
					user);
			startActivity(intent);
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
				+ "/eLibrary/library/index.php/welcome/process_login");
		params.setMethod("POST");
		params.setParam("user_email", user.getEmail());
		params.setParam("password", user.getPassword());
		params.setParam("user_auth", user.getAuth());
		params.setParam("mobile", "1");
		return params;

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {

			} else {

			}

		} else {
			noConnectionView();
		}
	}

	public void createUserModel() {
		user = new UserModel();
		user.setEmail(email);
		user.setPassword(password);
		user.setAuth(AppPreferences.Auth.EMAIL_ENUM);
	}

	public class SignInAsycTask extends AsyncTask<RequestParams, Void, String> {

		@Override
		protected String doInBackground(RequestParams... params) {
			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			String[] details = parseJsonObject(result);
			if (details != null) {
				user.setUser_id(Integer.valueOf(details[0]));
				user.setName(details[1]);
				if (details.length > 2) {
					user.setProfilePic(details[2]);
				}
				setSharedPreferences();
				startActivity(new Intent(Signin.this, MainActivity.class));
				finish();
			} else {
				Toast.makeText(Signin.this, "please try again",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public String[] parseJsonObject(String result) {
		JSONObject obj = null;
		try {
			obj = new JSONObject(result);
			if (obj.has("success")) {
				if (obj.getInt("success") == 1) {
					if (obj.has("user_pic")) {
						return new String[] {
								String.valueOf(obj.getInt("user_id")),
								obj.getString("user_name"),
								obj.getString("user_pic") };
					} else {
						return new String[] {
								String.valueOf(obj.getInt("user_id")),
								obj.getString("user_name"), };
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	public void setSharedPreferences() {
		edit = authPref.edit();
		edit.putString(AppPreferences.Auth.KEY_NAME, user.getName());
		edit.putString(AppPreferences.Auth.KEY_EMAIL, user.getEmail());
		edit.putInt(AppPreferences.Auth.KEY_AUTH,
				AppPreferences.Auth.EMAIL_AUTH);
		edit.putInt(AppPreferences.Auth.KEY_USERID, user.getUser_id());
		if (user.getProfilePic() != null) {
			edit.putString(AppPreferences.Auth.KEY_PICTURE,
					user.getProfilePic());
		}
		edit.commit();
	}

	@Override
	protected void onActivityResult(int REQUEST_CODE, int RESPONSE_CODE,
			Intent data) {
		if (REQUEST_CODE == AppPreferences.Codes.RC_SIGN_IN) {
			GoogleFragment fragment = (GoogleFragment) getSupportFragmentManager()
					.findFragmentById(R.id.signin_fragment_google);
			fragment.onActivityResult(REQUEST_CODE, RESPONSE_CODE, data);
		}
		super.onActivityResult(REQUEST_CODE, RESPONSE_CODE, data);
	}
}
