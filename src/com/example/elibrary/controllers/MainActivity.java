package com.example.elibrary.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.elibrary.R;
import com.example.elibrary.controllers.Friends.FriendsListAdapter.MyHolder;
import com.example.elibrary.controllers.Logout.OnLogoutSuccessful;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.LibraryModel;
import com.example.elibrary.models.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnLogoutSuccessful {
	private static final String TAG = "MainActivity";
	private SharedPreferences authPref;
	private Context context;
	private Menu menuGlobal;
	private int auth;
	private LinearLayout parentLinear;
	private ScrollView scrollView;
	private LayoutInflater mInflater;
	private Map<String, ArrayList<LibraryModel>> booksMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate");
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "internet connected");
			parentLinear = new LinearLayout(this);
			parentLinear.setOrientation(LinearLayout.VERTICAL);
			scrollView = (ScrollView) findViewById(R.id.main_scrollview_parent);
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
				new FetchBooksAsyncTask().execute(getParams());
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
				startActivity(new Intent(MainActivity.this, MainActivity.class));
				finish();
			}
		});
	}

	public void fetchData() {

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// ImageView view = (ImageView) scrollView
		// .findViewById(R.id.single_book_cover);
		// int height = view.getHeight();
		// int width = view.getWidth();
		// Log.d(TAG, "width of image view->" + width);
		// Log.d(TAG, "height of image view->" + height);
	}

	@SuppressLint("InflateParams")
	public void fillWithBooks() {
		Log.d(TAG, "fillWithBooks");

		for (String key : booksMap.keySet()) {
			View singleCategory = mInflater.inflate(
					R.layout.inflate_single_category, null, false);
			TextView textView = (TextView) singleCategory
					.findViewById(R.id.single_category_textview_book_category);
			Log.d(TAG, "category in fill books->" + key);
			textView.setText(key);
			LinearLayout horizontal = (LinearLayout) singleCategory
					.findViewById(R.id.single_category_linearlayout_horizontal);

			for (int j = 0; j < booksMap.get(key).size(); j++) {
				final int id = booksMap.get(key).get(j).getBookId();
				View singleBook = mInflater.inflate(
						R.layout.inflate_singlebook, null, false);
				ImageView imageView = (ImageView) singleBook
						.findViewById(R.id.single_book_cover);
				// imageView.setImageBitmap(profile.getTypes().get("uploads")
				// .get(j).getImagebitmap());
				new BitmapAsyncTask(imageView).execute(booksMap.get(key).get(j)
						.getProfilePic());
				TextView titleTextView = (TextView) singleBook
						.findViewById(R.id.single_book_name);
				TextView authorTextView = (TextView) singleBook
						.findViewById(R.id.single_book_author);
				TextView userNameTextView = (TextView) singleBook
						.findViewById(R.id.single_book_user);
				titleTextView.setText(booksMap.get(key).get(j).getBookName());
				authorTextView
						.setText(booksMap.get(key).get(j).getBookAuthor());
				userNameTextView
						.setText(booksMap.get(key).get(j).getUserName());
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d(TAG, "bookid on click->" + id);
						new BookPagesAsyncTask()
								.execute(getBookPagesParams(String.valueOf(id)));
						// startActivity(new Intent(MainActivity.this,
						// Book.class));

					}
				});
				horizontal.addView(singleBook);
			}
			if (parentLinear != null) {
				parentLinear.addView(singleCategory);

			}
		}
		scrollView.addView(parentLinear);
		setContentView(scrollView);

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

	public void setMenuName() {
		Log.d(TAG, "setMenuName");
		if (menuGlobal != null) {
			MenuItem item = menuGlobal.findItem(R.id.name_account_menu);
			item.setTitle(authPref.getString(AppPreferences.Auth.KEY_NAME,
					"Name"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		ComponentName cn = new ComponentName(this, SearchActivity.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

		menuGlobal = menu;
		setMenuName();
		return true;
	}

	@Override
	public void startActivity(Intent intent) {
		Log.d(TAG, "in onStartActivity");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.d(TAG, "intent.getAction");
			intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE,
					AppPreferences.BOOKSEARCH);
		}
		super.startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.name_account_menu) {
			Intent intent = new Intent(this, Profile.class);
			intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_WHO_PROFILE,
					AppPreferences.SELF);
			startActivity(intent);
		} else if (id == R.id.settings_logout) {
			Log.d(TAG, "clicked logout");
			Logout logout = new Logout(this);
			auth = authPref.getInt(AppPreferences.Auth.KEY_AUTH, -1);
			Log.d(TAG, "auth in logout->" + auth);
			if (auth == AppPreferences.Auth.GOOGLE_AUTH) {
				Log.d(TAG, "logout fron google");
				logout.logoutFromGoogle();
			} else if (auth == AppPreferences.Auth.FACEBOOK_AUTH) {
				Log.d(TAG, "logout fron facebook");
				logout.logoutFromFacebook();
			} else {
				Log.d(TAG, "logout from email");
				logout.clearSharedPref();
			}
		} else if (id == R.id.settings_uploads) {
			startActivity(new Intent(this, Uploads.class));
		} else if (id == R.id.settings_friends) {
			startActivity(new Intent(this, Friends.class));
		} else if (id == R.id.setting_test) {
			// new TestAsyncTask().execute(getParams());

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCleardFields(boolean cleared) {
		if (cleared) {
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
				// fetchData();
				// fillWithBooks();
			} else {
				logout();
			}
		}

	}

	public void logout() {
		Intent intent = new Intent(this, Authentication.class);
		startActivity(intent);
		finish();
	}

	public RequestParams getParams() {
		Log.d(TAG, "getParams()");
		RequestParams params = new RequestParams();
		params.setMethod("GET");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/home");
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("mobile", "1");
		Log.d(TAG,
				"user_id->"
						+ authPref.getInt(AppPreferences.Auth.KEY_USERID, -1));
		return params;
	}

	public RequestParams getBookPagesParams(String id) {
		RequestParams params = new RequestParams();
		params.setMethod("GET");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/book");
		params.setParam("book_id", "39");
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("page_no", "1");
		params.setParam("mobile", "1");
		return params;

	}

	public class FetchBooksAsyncTask extends
			AsyncTask<RequestParams, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.show();
		}

		@Override
		protected String doInBackground(RequestParams... params) {
			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			Log.d(TAG, "onPostExecute->" + result);
			booksMap = new HashMap<String, ArrayList<LibraryModel>>();
			try {
				JSONObject mainObject = new JSONObject(result);
				int success = mainObject.getInt("success");
				Log.d(TAG, "success for getting json object->" + success);
				if (success == 1) {
					Log.d(TAG, "inside if success");
					JSONArray libraryArray = mainObject.getJSONArray("library");
					for (int i = 0; i < libraryArray.length(); i++) {
						JSONObject booksObjectByCategory = libraryArray
								.getJSONObject(i);
						String genre = booksObjectByCategory.getString("genre");
						Log.d(TAG, "genre->" + genre);
						getBooksByCategory(genre, booksMap,
								booksObjectByCategory);

					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fillWithBooks();
		}

		public void getBooksByCategory(String genre,
				Map<String, ArrayList<LibraryModel>> booksMap,
				JSONObject booksObjectByCategory) {
			ArrayList<LibraryModel> libraryModel = new ArrayList<LibraryModel>();
			try {
				JSONObject booksObject = booksObjectByCategory
						.getJSONObject("books");
				int j = 0;
				JSONObject bookObject = null;
				while (j != -1) {
					if (booksObject.isNull(String.valueOf(j))) {
						Log.d(TAG, "null");
						j = -1;
					} else {
						bookObject = booksObject.getJSONObject(String
								.valueOf(j));

						LibraryModel model = new LibraryModel();
						model.setCategory(genre);
						model.setBookAuthor(bookObject.getString("book_author"));
						model.setBookId(Integer.valueOf(bookObject
								.getString("book_id")));
						model.setBookName(bookObject.getString("book_title"));
						model.setIsbn(bookObject.getString("book_isbn"));
						model.setAccess(bookObject.getInt("access"));
						model.setProfilePic(bookObject.getString("book_pic"));
						JSONObject userObject = bookObject
								.getJSONObject("uploaded_by");
						model.setUserName(userObject.getString("user_name"));
						model.setUser_id(Integer.valueOf(userObject
								.getString("user_id")));
						libraryModel.add(model);
						j++;
					}

				}
				booksMap.put(genre, libraryModel);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
		MyHolder holder;
		LibraryModel model;
		ImageView view;

		public BitmapAsyncTask(MyHolder holder) {
			this.holder = holder;
		}

		public BitmapAsyncTask() {

		}

		public BitmapAsyncTask(LibraryModel model) {
			this.model = model;
		}

		public BitmapAsyncTask(ImageView imageView) {
			this.view = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			InputStream input = null;
			try {
				URL url = new URL(params[0]);
				Log.d(TAG, "cover pic url->" + params[0]);
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
			if (result != null) {
				result = getResizedBitmap(result, 300, 300);
				view.setImageBitmap(result);
			}
		}
	}

	public class BookPagesAsyncTask extends
			AsyncTask<RequestParams, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.show();
		}

		@Override
		protected String doInBackground(RequestParams... params) {
			// TODO Auto-generated method stub
			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			Log.d(TAG, "result->" + result);
			Intent intent=new Intent(MainActivity.this, Book.class);
			intent.putExtra("book", result);
			MainActivity.this.startActivity(intent);

		}

	}
}
