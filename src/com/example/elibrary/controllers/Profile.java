package com.example.elibrary.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
import com.example.elibrary.models.ProfileModel;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends Activity implements OnLogoutSuccessful {
	private static final String TAG = "Profile";
	private SharedPreferences authPref;
	private Context context;
	private Menu menuGlobal;
	private int auth;
	private LayoutInflater mInflater;
	private static int who;
	private LinearLayout parentLinear;
	private ScrollView parentScroll;
	private TextView nameTextView, emailTextView, booksUploadedTextView,
			titleTextView;
	private Button friendButton;
	private ImageView profilePicImageView;
	private ProfileModel profile;
	private int selfFlag = 1;
	private String to_user_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		Log.d(TAG, "onCreate");
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "internet connected");
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
				initialize();
				Intent intent = getIntent();
				if (intent != null) {
					if (intent
							.hasExtra(AppPreferences.PutExtraKeys.PUTEXTRA_WHO_PROFILE)) {
						who = intent
								.getExtras()
								.getInt(AppPreferences.PutExtraKeys.PUTEXTRA_WHO_PROFILE);

					}
					if (intent.hasExtra("to_user_id")) {
						selfFlag = 0;
						to_user_id = intent.getExtras().getString("to_user_id");
					}
					RequestParams params = getRequestParams();
					new GetProfileAsyncTask().execute(params);

				}
			} else {
				logout();
			}
		} else {
			noConnectionView();
		}
	}

	public void initialize() {
		parentLinear = (LinearLayout) findViewById(R.id.profile_linear_parent);
		parentScroll = (ScrollView) findViewById(R.id.profile_scrollview_parent);
		nameTextView = (TextView) findViewById(R.id.profile_textview_name);
		emailTextView = (TextView) findViewById(R.id.profile_textview_email);
		booksUploadedTextView = (TextView) findViewById(R.id.profile_textview_uploaded);
		friendButton = (Button) findViewById(R.id.profile_button_friend);
		titleTextView = (TextView) findViewById(R.id.profile_textview_title);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		profilePicImageView = (ImageView) findViewById(R.id.profile_imageview_profilepic);
		bitmap = getResizedBitmap(bitmap, 147, 147);
		profilePicImageView.setImageBitmap(bitmap);

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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		int height = nameTextView.getHeight();
		height += emailTextView.getHeight();
		height += booksUploadedTextView.getHeight();
		Log.d(TAG, "total height of the textViews->" + height);
	}

	@SuppressLint("InflateParams")
	public void fillBooks() {
		nameTextView.setText(profile.getUser_name());
		emailTextView.setText(profile.getUser_email());
		booksUploadedTextView.append(" "
				+ profile.getTypes().get("uploads").size());
		int rows = 0;
		if (profile.getFriendship().equals("self")) {
			Log.d(TAG, "its self in fillbooks");
			rows = 3;
		} else {
			rows = 2;
		}

		if (who == AppPreferences.FRIEND
				|| profile.getFriendship().equals("friends")) {
			friendButton.setText("friends");
			titleTextView.setText("Library");
			rows = 2;
		} else if (who == AppPreferences.STRANGER
				|| profile.getFriendship().equals("no")) {
			friendButton.setText("add friend");
			titleTextView.setText("Library");
			rows = 2;
		} else if (who == AppPreferences.SELF
				|| profile.getFriendship().equals("self")) {
			friendButton.setText("self");
			titleTextView.setText("My Library");
			rows = 3;
		} else if (profile.getFriendship().equals("pending")) {
			friendButton.setText("pending");
		} else if (profile.getFriendship().equals("withhelp")) {
			friendButton.setText("respond to friend request");
		}
		friendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (profile.getFriendship().equals("no")) {
					friendButton.setText("pending");
					Toast.makeText(Profile.this, "friend request sent",
							Toast.LENGTH_SHORT).show();
					new SendFriendRequestAsyncTask()
							.execute(getFriendRequestParams("no",
									String.valueOf(profile.getUser_id())));
				} else if (profile.getFriendship().equals("withheld")) {
					friendButton.setText("friends");
					new SendFriendRequestAsyncTask()
							.execute(getFriendRequestParams("withheld",
									String.valueOf(profile.getUser_id())));
					Toast.makeText(Profile.this, "you made a new friend",
							Toast.LENGTH_SHORT).show();

				}
			}
		});

		for (int i = 0; i < rows; i++) {
			Log.d(TAG, "inside rows for loop");
			View singleCategory = mInflater.inflate(
					R.layout.inflate_single_category, null, false);
			TextView textView = (TextView) singleCategory
					.findViewById(R.id.single_category_textview_book_category);
			if (i == 0) {
				Log.d(TAG, "rows==0");
				textView.setText("uploads");
				LinearLayout horizontal = (LinearLayout) singleCategory
						.findViewById(R.id.single_category_linearlayout_horizontal);
				for (int j = 0; j < profile.getTypes().get("uploads").size(); j++) {
					final int finalJ = j;
					View singleBook = mInflater.inflate(
							R.layout.inflate_singlebook, null, false);
					ImageView imageView = (ImageView) singleBook
							.findViewById(R.id.single_book_cover);
					// imageView.setImageBitmap(profile.getTypes().get("uploads")
					// .get(j).getImagebitmap());
					new BitmapAsyncTask(imageView).execute(profile.getTypes()
							.get("uploads").get(j).getProfilePic());
					TextView titleTextView = (TextView) singleBook
							.findViewById(R.id.single_book_name);
					TextView authorTextView = (TextView) singleBook
							.findViewById(R.id.single_book_author);
					TextView userNameTextView = (TextView) singleBook
							.findViewById(R.id.single_book_user);
					titleTextView.setText(profile.getTypes().get("uploads")
							.get(j).getBookName());
					authorTextView.setText(profile.getTypes().get("uploads")
							.get(j).getBookAuthor());
					userNameTextView.setText(profile.getTypes().get("uploads")
							.get(j).getUserName());
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// startActivity(new Intent(Profile.this,
							// Book.class));
							new BookPagesAsyncTask()
									.execute(getBookPagesParams(String
											.valueOf(profile.getTypes()
													.get("uploads").get(finalJ)
													.getBookId())));
						}
					});
					horizontal.addView(singleBook);
				}
			} else if (i == 1) {
				Log.d(TAG, "rows==1");
				textView.setText("favourites");
				if (profile.getTypes().get("fav") != null) {
					LinearLayout horizontal = (LinearLayout) singleCategory
							.findViewById(R.id.single_category_linearlayout_horizontal);
					for (int j = 0; j < profile.getTypes().get("fav").size(); j++) {
						final int finalJ = j;
						View singleBook = mInflater.inflate(
								R.layout.inflate_singlebook, null, false);
						ImageView imageView = (ImageView) singleBook
								.findViewById(R.id.single_book_cover);
						// imageView.setImageBitmap(profile.getTypes().get("fav")
						// .get(j).getImagebitmap());
						new BitmapAsyncTask(imageView).execute(profile
								.getTypes().get("fav").get(j).getProfilePic());
						TextView titleTextView = (TextView) singleBook
								.findViewById(R.id.single_book_name);
						TextView authorTextView = (TextView) singleBook
								.findViewById(R.id.single_book_author);
						TextView userNameTextView = (TextView) singleBook
								.findViewById(R.id.single_book_user);
						titleTextView.setText(profile.getTypes().get("fav")
								.get(j).getBookName());
						authorTextView.setText(profile.getTypes().get("fav")
								.get(j).getBookAuthor());
						userNameTextView.setText(profile.getTypes().get("fav")
								.get(j).getUserName());
						imageView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// startActivity(new Intent(Profile.this,
								// Book.class));
								new BookPagesAsyncTask()
										.execute(getBookPagesParams(String
												.valueOf(profile.getTypes()
														.get("fav").get(finalJ)
														.getBookId())));
							}
						});
						horizontal.addView(singleBook);
					}
				}
			} else if (i == 2) {
				textView.setText("private");
				Log.d(TAG, "rows==2");
				if (profile.getTypes().get("private") != null) {
					LinearLayout horizontal = (LinearLayout) singleCategory
							.findViewById(R.id.single_category_linearlayout_horizontal);
					for (int j = 0; j < profile.getTypes().get("private")
							.size(); j++) {
						final int finalJ = j;
						View singleBook = mInflater.inflate(
								R.layout.inflate_singlebook, null, false);
						ImageView imageView = (ImageView) singleBook
								.findViewById(R.id.single_book_cover);
						// imageView.setImageBitmap(profile.getTypes()
						// .get("private").get(j).getImagebitmap());
						new BitmapAsyncTask(imageView).execute(profile
								.getTypes().get("private").get(j)
								.getProfilePic());
						TextView titleTextView = (TextView) singleBook
								.findViewById(R.id.single_book_name);
						TextView authorTextView = (TextView) singleBook
								.findViewById(R.id.single_book_author);
						TextView userNameTextView = (TextView) singleBook
								.findViewById(R.id.single_book_user);
						titleTextView.setText(profile.getTypes().get("private")
								.get(j).getBookName());
						authorTextView.setText(profile.getTypes()
								.get("private").get(j).getBookAuthor());
						userNameTextView.setText(profile.getTypes()
								.get("private").get(j).getUserName());
						imageView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// startActivity(new Intent(Profile.this,
								// Book.class));
								new BookPagesAsyncTask()
								.execute(getBookPagesParams(String
										.valueOf(profile.getTypes()
												.get("private").get(finalJ)
												.getBookId())));

							}
						});
						horizontal.addView(singleBook);
					}
				}
			}

			if (parentLinear != null) {
				parentLinear.addView(singleCategory);

			}
		}
		parentScroll.removeAllViews();
		parentScroll.addView(parentLinear);
		setContentView(parentScroll);

	}

	public RequestParams getBookPagesParams(String id) {
		RequestParams params = new RequestParams();
		params.setMethod("GET");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/book");
		params.setParam("book_id", id);
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("mobile", "1");
		return params;

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
				startActivity(new Intent(Profile.this, Profile.class));
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "onCreate");
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
			} else {
				logout();
			}
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

	public void setMenuName() {
		Log.d(TAG, "setMenuName");
		if (menuGlobal != null) {
			MenuItem item = menuGlobal.findItem(R.id.name_account_menu);
			item.setTitle(authPref.getString(AppPreferences.Auth.KEY_NAME,
					"Name"));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.settings_logout) {
			Log.d(TAG, "clicked logout");
			Logout logout = new Logout(this);
			auth = authPref.getInt(AppPreferences.Auth.KEY_AUTH, -1);
			Log.d(TAG, "auth in logout->" + auth);
			if (auth == AppPreferences.Auth.GOOGLE_AUTH) {
				logout.logoutFromGoogle();
			} else if (auth == AppPreferences.Auth.FACEBOOK_AUTH) {
				logout.logoutFromFacebook();
			} else {
				logout.clearSharedPref();
			}
		} else if (id == R.id.settings_library) {
			startActivity(new Intent(this, MainActivity.class));
		} else if (id == R.id.settings_uploads) {
			startActivity(new Intent(this, Uploads.class));
		} else if (id == R.id.settings_friends) {
			startActivity(new Intent(this, Friends.class));
		} else if (id == R.id.name_account_menu) {
			if (who == AppPreferences.FRIEND || who == AppPreferences.STRANGER) {
				Intent intent = new Intent(this, Profile.class);
				intent.putExtra(
						AppPreferences.PutExtraKeys.PUTEXTRA_WHO_PROFILE,
						AppPreferences.SELF);
				startActivity(intent);
			}
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

	public RequestParams getRequestParams() {
		Log.d(TAG, "getParams()");
		RequestParams params = new RequestParams();
		params.setMethod("GET");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/profile");
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		if (selfFlag == 1) {
			params.setParam("user", String.valueOf(authPref.getInt(
					AppPreferences.Auth.KEY_USERID, -1)));
		} else {
			params.setParam("user", to_user_id);
		}
		params.setParam("mobile", "1");
		Log.d(TAG,
				"user_id->"
						+ authPref.getInt(AppPreferences.Auth.KEY_USERID, -1));
		return params;

	}

	public RequestParams getFriendRequestParams(String response, String user_id) {
		Log.d(TAG, "getParams()");
		RequestParams params = new RequestParams();
		params.setMethod("POST");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/friends");
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("user", user_id);
		if (response.equals("withheld")) {
			params.setParam("action", "accept_friend");
		} else if (response.equals("no")) {
			params.setParam("action", "add_friend");
		}
		params.setParam("mobile", "1");
		Log.d(TAG,
				"user_id->"
						+ authPref.getInt(AppPreferences.Auth.KEY_USERID, -1));
		return params;

	}

	public class GetProfileAsyncTask extends
			AsyncTask<RequestParams, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(Profile.this);
			dialog.show();
		}

		@Override
		protected String doInBackground(RequestParams... params) {

			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			Log.d(TAG, result);
			profile = new ProfileModel();
			JSONObject mainObject = null;
			try {
				mainObject = new JSONObject(result);
				int success = mainObject.getInt("success");
				Log.d(TAG, "id->" + success);
				if (success == 1) {
					getUserInfo(mainObject);
					Map<String, ArrayList<LibraryModel>> bookMap = profile
							.getTypes();
					JSONObject userLibObject = mainObject
							.getJSONObject("user_lib");
					JSONArray uploadsArray = userLibObject
							.getJSONArray("uploads");
					getBooksInfo(uploadsArray, "uploads", bookMap);
					JSONArray favouriteArray = userLibObject
							.getJSONArray("fav");
					Log.d(TAG, "fav size->" + favouriteArray.length());
					if (favouriteArray.length() > 0) {
						getBooksInfo(favouriteArray, "fav", bookMap);
					}
					if (profile.getFriendship().equals("self")) {
						Log.d(TAG, "friendShip is self");
						JSONArray privateArray = userLibObject
								.getJSONArray("private");
						Log.d(TAG, "fav size->" + privateArray.length());
						if (privateArray.length() > 0) {
							getBooksInfo(privateArray, "private", bookMap);
						}
					}
					profile.setTypes(bookMap);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fillBooks();

		}

		public void getUserInfo(JSONObject mainObject) {
			profile.setSuccess(1);
			JSONObject userObject;
			try {
				userObject = mainObject.getJSONObject("user");
				profile.setUser_id(Integer.valueOf(userObject
						.getString("user_id")));
				Log.d(TAG, "user_id->" + profile.getUser_id());
				profile.setUser_name(userObject.getString("user_name"));
				Log.d(TAG, "user_id->" + profile.getUser_name());
				profile.setUser_email(userObject.getString("email"));
				Log.d(TAG, "user_email->" + profile.getUser_email());
				if (userObject.getString("friendship") == null) {
					profile.setFriendship("no");
					Log.d(TAG, "friendship->" + profile.getFriendship());
				} else if (userObject.getString("friendship").equals("self")) {
					profile.setFriendship("self");
					Log.d(TAG, "friendship->" + profile.getFriendship());
				} else if (userObject.getString("friendship").equals("friend")) {
					profile.setFriendship("friend");
					Log.d(TAG, "friendship->" + profile.getFriendship());
				} else if (userObject.getString("friendship").equals("pending")) {
					profile.setFriendship("pending");
					Log.d(TAG, "friendship->" + profile.getFriendship());
				} else if (userObject.getString("friendship")
						.equals("withheld")) {
					profile.setFriendship("withheld");
					Log.d(TAG, "friendship->" + profile.getFriendship());
				} else {
					profile.setFriendship("no");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void getBooksInfo(JSONArray array, String category,
				Map<String, ArrayList<LibraryModel>> bookMap) {
			ArrayList<LibraryModel> books = profile.getBooklist();
			try {

				for (int i = 0; i < array.length(); i++) {
					Log.d(TAG, "book number in loop->" + i);
					JSONObject bookObject = array.getJSONObject(i);
					LibraryModel libraryModel = new LibraryModel();
					libraryModel.setBookId(Integer.valueOf(bookObject
							.getString("book_id")));
					Log.d(TAG, "setBookId->" + libraryModel.getBookId());
					libraryModel.setPrivacy(bookObject.getString("book_id"));
					Log.d(TAG, "setBookId->" + libraryModel.getPrivacy());
					libraryModel.setBookAuthor(bookObject
							.getString("book_author"));
					Log.d(TAG, "setBookAuthor->" + libraryModel.getBookAuthor());
					libraryModel
							.setBookName(bookObject.getString("book_title"));
					Log.d(TAG, "setBookTitle->" + libraryModel.getBookName());
					libraryModel
							.setProfilePic(bookObject.getString("book_pic"));
					Log.d(TAG, "setBookPic->" + libraryModel.getProfilePic());
					// RequestParams
					// params=getImageParams(libraryModel.getProfilePic());

					libraryModel.setAccess(Integer.valueOf(bookObject
							.getString("access")));
					Log.d(TAG, "setBookAccess->" + libraryModel.getAccess());
					libraryModel.setBookGenre(bookObject
							.getString("book_genre"));
					Log.d(TAG, "setBookGenre->" + libraryModel.getBookGenre());
					JSONObject uploadedUserObject = bookObject
							.getJSONObject("uploaded_by");
					libraryModel.setUserName(uploadedUserObject
							.getString("user_name"));
					Log.d(TAG, "setUserName->" + libraryModel.getUserName());
					books.add(libraryModel);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			bookMap.put(category, books);
		}
	}

	public RequestParams getImageParams(String link) {
		return null;

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
			result = getResizedBitmap(result, 300, 300);
			view.setImageBitmap(result);

		}
	}

	public class SendFriendRequestAsyncTask extends
			AsyncTask<RequestParams, Void, String> {

		@Override
		protected String doInBackground(RequestParams... params) {
			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "result->" + result);
		}
	}

	public class BookPagesAsyncTask extends
			AsyncTask<RequestParams, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(Profile.this);
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
			Intent intent = new Intent(Profile.this, Book.class);
			intent.putExtra("book", result);
			Profile.this.startActivity(intent);

		}

	}

}
