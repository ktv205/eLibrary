package com.example.elibrary.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.example.elibrary.R;
import com.example.elibrary.controllers.Logout.OnLogoutSuccessful;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.FriendsModel;
import com.example.elibrary.models.RequestParams;

public class Friends extends Activity implements OnLogoutSuccessful {
	private SharedPreferences authPref;
	private Context context;
	private Menu menuGlobal;
	private int auth;
	private LayoutInflater mInflater;
	private static final String TAG = "Friends";
	private ListView friendsListView;
	private ArrayList<FriendsModel> friendsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		Log.d(TAG, "onCreate");
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "internet connected");
			if (CheckAuthentication.checkForAuthentication(context)) {
				//trainingSet();
				setMenuName();
				new FriendsAsyncTask().execute(getRequestParams());
			} else {
				logout();
			}
		}
	}

	public void trainingSet() {
		friendsListView = (ListView) findViewById(R.id.friends_listview_friends);
		FriendsModel obj = new FriendsModel();
		obj.setEmail("example@example.com");
		obj.setName("example");
		obj.setProfilePic("https://graph.facebook.com/100000236193433/picture?type=normal");
		friendsList = new ArrayList<FriendsModel>();
		friendsList.add(obj);
		FriendsListAdapter adapter = new FriendsListAdapter(friendsList);
		friendsListView.setAdapter(adapter);
		friendsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(Friends.this, Profile.class);
				intent.putExtra(
						AppPreferences.PutExtraKeys.PUTEXTRA_WHO_PROFILE,
						AppPreferences.FRIEND);
				startActivity(intent);

			}
		});
	}
    public RequestParams getRequestParams(){
    	Log.d(TAG, "getParams()");
		RequestParams params = new RequestParams();
		params.setMethod("POST");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/friends");
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("mobile", "1");
		Log.d(TAG,
				"user_id->"
						+ authPref.getInt(AppPreferences.Auth.KEY_USERID, -1));
		return params;
    	
    }
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
				// fetchData();
				// fillWithBooks();
			} else {
				logout();
			}
		} else {
			noConnectionView();
		}
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
				startActivity(new Intent(Friends.this, Friends.class));
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsmenu");
		getMenuInflater().inflate(R.menu.main, menu);
		menuGlobal = menu;
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		ComponentName cn = new ComponentName(this, SearchActivity.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
		setMenuName();
		return true;
	}

	public void setMenuName() {
		Log.d(TAG, "setMenuName");
		if (menuGlobal != null) {
			MenuItem item = menuGlobal.findItem(R.id.name_account_menu);
			item.setTitle(authPref.getString(AppPreferences.Auth.KEY_NAME,
					"Name"));
		}
	}

	@Override
	public void startActivity(Intent intent) {
		Log.d(TAG, "in onStartActivity");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.d(TAG, "intent.getAction");
			intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE,
					AppPreferences.FRIENDSEARCH);
		}
		super.startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.name_account_menu) {
			Intent intent = new Intent(this, Profile.class);
			intent.putExtra(
					AppPreferences.PutExtraKeys.PUTEXTRA_WHO_PROFILE,
					AppPreferences.SELF);
			startActivity(intent);
			startActivity(intent);
		} else if (id == R.id.settings_logout) {
			Log.d(TAG, "clicked logout");
			Logout logout = new Logout(this);
			auth = authPref.getInt(AppPreferences.Auth.KEY_AUTH, -1);
			if (auth == AppPreferences.Auth.GOOGLE_AUTH) {
				logout.logoutFromGoogle();
			} else if (auth == AppPreferences.Auth.FACEBOOK_AUTH) {
				logout.logoutFromFacebook();
			} else {
				logout.clearSharedPref();
			}
		} else if (id == R.id.settings_uploads) {
			startActivity(new Intent(this, Uploads.class));
		} else if (id == R.id.settings_library) {
			startActivity(new Intent(this, MainActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCleardFields(boolean cleared) {
		if (cleared) {
			logout();
		}

	}

	public void logout() {
		Intent intent = new Intent(this, Authentication.class);
		startActivity(intent);
		finish();
	}

	public class FriendsAsyncTask extends
			AsyncTask<RequestParams, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(
				RequestParams... params) {
			// TODO Auto-generated method stub
			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG,"result->"+result);
		}

	}

	public class FriendsListAdapter extends BaseAdapter {
		private ArrayList<FriendsModel> friendsList;

		public FriendsListAdapter(ArrayList<FriendsModel> friendsList) {
			this.friendsList = friendsList;
		}

		@Override
		public int getCount() {
			return friendsList.size();
		}

		@Override
		public Object getItem(int position) {
			return friendsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public class MyHolder {
			TextView name, email;
			ImageView profilePic;

			public MyHolder(View v) {
				name = (TextView) v
						.findViewById(R.id.contents_list_search_textview_name);
				email = (TextView) v
						.findViewById(R.id.contents_list_search_texview_email);
				profilePic = (ImageView) v
						.findViewById(R.id.contents_list_search_imageview_profilepic);
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			MyHolder myHolder;
			if (view == null) {
				LayoutInflater inflater = LayoutInflater.from(Friends.this);
				view = inflater.inflate(R.layout.contents_list_search, parent,
						false);
				myHolder = new MyHolder(view);
				view.setTag(myHolder);
			} else {
				myHolder = (MyHolder) view.getTag();

			}
			myHolder.name.setText(friendsList.get(position).getName());
			myHolder.email.setText(friendsList.get(position).getEmail());
			new BitmapAsyncTask(myHolder).execute(friendsList.get(position)
					.getProfilePic());

			return view;
		}

		public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
			int width = bm.getWidth();
			int height = bm.getHeight();
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// CREATE A MATRIX FOR THE MANIPULATION
			Matrix matrix = new Matrix();
			// RESIZE THE BIT MAP
			matrix.postScale(scaleWidth, scaleHeight);

			// "RECREATE" THE NEW BITMAP
			Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
					matrix, false);
			return resizedBitmap;
		}

		public class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
			MyHolder holder;

			public BitmapAsyncTask(MyHolder holder) {
				this.holder = holder;
			}

			@Override
			protected Bitmap doInBackground(String... params) {
				InputStream input = null;
				try {
					URL url = new URL(params[0]);
					if (params[0].contains("https")) {
						HttpsURLConnection connection = (HttpsURLConnection) url
								.openConnection();
						connection.setDoInput(true);
						connection.connect();
						input = connection.getInputStream();
					} else {
						HttpURLConnection connection = (HttpURLConnection) url
								.openConnection();
						connection.setDoInput(true);
						connection.connect();
						input = connection.getInputStream();
					}

					Bitmap myBitmap = BitmapFactory.decodeStream(input);
					return myBitmap;
				} catch (IOException e) {
					// Log exception
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				holder.profilePic.setImageBitmap(result);
			}
		}

	}
}
