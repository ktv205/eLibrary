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

public class Profile extends Activity implements OnLogoutSuccessful {
	private static final String TAG = "Profile";
	private SharedPreferences authPref;
	private Context context;
	private Menu menuGlobal;
	private int auth;
	private LayoutInflater mInflater;
	private LinearLayout parentLinear;
	private ScrollView parentScroll;
	private TextView nameTextView, emailTextView, booksUploadedTextView;
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
					if (intent.hasExtra("to_user_id")) {
						selfFlag = 0;
						to_user_id = intent.getExtras().getString("to_user_id");
					}
					if (savedInstanceState == null) {
						RequestParams params = getRequestParams();
						new GetProfileAsyncTask().execute(params);
					} else {
						profile = new ProfileModel();
						profile = savedInstanceState.getParcelable("profile");
						fillBooks();
					}

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
		profilePicImageView = (ImageView) findViewById(R.id.profile_imageview_profilepic);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable("profile", profile);
		super.onSaveInstanceState(outState);
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
		int rows = 1;
		nameTextView.setText(profile.getUser_name());
		emailTextView.setText(profile.getUser_email());
		booksUploadedTextView.append(" "
				+ profile.getTypes().get("uploads").size());
		if (profile.getUser_pic() != null) {
			Log.d(TAG, "in setting profile pic->" + profile.getUser_pic());
			new BitmapAsyncTask(profilePicImageView).execute(profile
					.getUser_pic());
		} else {
			Log.d(TAG, "in setting profile pic->" + "some thing wrong");
		}

		View friendProfileContents = mInflater.inflate(
				R.layout.inflate_contents_profile, null, false);
		TextView friendProfileTextView = (TextView) friendProfileContents
				.findViewById(R.id.contents_profile_textview_text);
		final Button friendProfileButton = (Button) friendProfileContents
				.findViewById(R.id.contents_profile_button_friend);
		if (profile.getFriendship().equals("no")) {
			friendProfileTextView
					.setText("Do you want to send a friend request?");
			friendProfileButton.setText("add friend");
			parentLinear.addView(friendProfileContents);
			rows = 2;
		} else if (profile.getFriendship().equals("withheld")) {
			friendProfileTextView.setText("Respond to a friend request");
			friendProfileButton.setText("respond");
			parentLinear.addView(friendProfileContents);
			rows = 2;
		} else if (profile.getFriendship().equals("pending")) {
			friendProfileTextView
					.setText("waiting for the person to accept your request");
			friendProfileButton.setText("pending");
			parentLinear.addView(friendProfileContents);
			rows = 2;
		} else if (profile.getFriendship().equals("friend")) {
			friendProfileTextView.setText("You are friends with this person");
			friendProfileButton.setText("remove friend");
			parentLinear.addView(friendProfileContents);
			rows = 2;
		} else if (profile.getFriendship().equals("self")) {
			rows = 3;
		}
		friendProfileButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG,"in onclick");
				if (profile.getFriendship().equals("no")) {
					new SendFriendRequestAsyncTask()
							.execute(getFriendRequestParams("no",
									String.valueOf(profile.getUser_id())));
					friendProfileButton.setText("pending");
				} else if (profile.getFriendship().equals("withheld")) {
					new SendFriendRequestAsyncTask()
							.execute(getFriendRequestParams("withheld",
									String.valueOf(profile.getUser_id())));
					friendProfileButton.setText("friends");
				}

			}
		});
		View libraryTitleView = mInflater.inflate(
				R.layout.inflate_profile_title, null, false);
		TextView titleLibraryTextView = (TextView) libraryTitleView
				.findViewById(R.id.profile_title_textview);
		titleLibraryTextView.setText("Library");
		parentLinear.addView(libraryTitleView);

		for (int i = 0; i < rows; i++) {
			Log.d(TAG, "inside rows for loop");
			View singleCategory = mInflater.inflate(
					R.layout.inflate_single_category, null, false);
			TextView textView = (TextView) singleCategory
					.findViewById(R.id.single_category_textview_book_category);
			if (i == 0) {
				setEachCategory("uploads", textView, singleCategory);
			} else if (i == 1) {
				setEachCategory("favourites", textView, singleCategory);
			} else if (i == 2) {
			 setEachCategory("private", textView, singleCategory);
			 }
			parentScroll.removeAllViews();
			parentScroll.addView(parentLinear);
			setContentView(parentScroll);
		}

	}

	public void setEachCategory(final String category, TextView textView,
			View singleCategory) {

		Log.d(TAG, "rows==0");
		Log.d(TAG,"category->"+category);
		textView.setText(category);
		LinearLayout horizontal = (LinearLayout) singleCategory
				.findViewById(R.id.single_category_linearlayout_horizontal);
		for (int j = 0; j < profile.getTypes().get(category).size(); j++) {
			final int finalJ = j;
			View singleBook = mInflater.inflate(R.layout.inflate_singlebook,
					null, false);
			ImageView imageView = (ImageView) singleBook
					.findViewById(R.id.single_book_cover);
			if (profile.getTypes().get(category).get(j).getImagebitmap() == null) {
				new BitmapAsyncTask(imageView, profile.getTypes().get(category)
						.get(j)).execute(profile.getTypes().get(category)
						.get(j).getProfilePic());
			} else {
				imageView.setImageBitmap(profile.getTypes().get(category)
						.get(j).getImagebitmap());
			}
			TextView titleTextView = (TextView) singleBook
					.findViewById(R.id.single_book_name);
			TextView authorTextView = (TextView) singleBook
					.findViewById(R.id.single_book_author);
			TextView userNameTextView = (TextView) singleBook
					.findViewById(R.id.single_book_user);
			titleTextView.setText(profile.getTypes().get(category).get(j)
					.getBookName());
			authorTextView.setText(profile.getTypes().get(category).get(j)
					.getBookAuthor());
			userNameTextView.setText(profile.getTypes().get(category).get(j)
					.getUserName());
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new ViewBookAsyncTask(Profile.this, profile.getTypes()
							.get(category).get(finalJ).getProfilePic())
							.execute(getBookPagesParams(String.valueOf(profile
									.getTypes().get(category).get(finalJ)
									.getBookId())));
				}
			});
			horizontal.addView(singleBook);
		}

		if (parentLinear != null) {
			parentLinear.addView(singleCategory);
			if (profile.getTypes().get(category).size() == 0) {

				View uploadsEmptyView = mInflater.inflate(
						R.layout.contents_empty_uploads, null, false);
				TextView text = (TextView) uploadsEmptyView
						.findViewById(R.id.empty_uploads_textview_text);
				final Button button = (Button) uploadsEmptyView
						.findViewById(R.id.empty_uploads_button_friend);
				if (profile.getFriendship().equals("self")) {
					if (category.equals("uploads")) {
						text.setText("Start uploading books");
						button.setText("upload a book");
					} else if (category.equals("favourites")) {
						text.setText("No favourites,start Reading");
						button.setText("library");
					} else if (category.equals("private")) {
						text.setText("No private books");
						button.setVisibility(View.GONE);
					}
				} else {
					button.setVisibility(View.GONE);
					if (category.equals("uploads")) {
						text.setText("No uploads to show");
					} else if (category.equals("favourites")) {
						text.setText("No favourites to show");
					}
				}
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d(TAG, "in onclick");
						if (button.getText().toString().equals("upload a book")) {
							startActivity(new Intent(Profile.this,
									Uploads.class));
						} else if (button.getText().toString()
								.equals("library")) {
							startActivity(new Intent(Profile.this,
									MainActivity.class));
						}

					}
				});
				parentLinear.addView(uploadsEmptyView);

			}
			View singleLineView = mInflater.inflate(
					R.layout.inflate_divide_line, null, false);
			parentLinear.addView(singleLineView);
		}

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
			if (profile.getFriendship().equals("self")) {
				startActivity(new Intent(this, Profile.class));
			}
		} else if (id == R.id.settings_reload) {
			startActivity(new Intent(this, Profile.class));
			finish();
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
			Log.d(TAG,"with held in getFriend params");
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
						getBooksInfo(favouriteArray, "favourites", bookMap);
					if (profile.getFriendship().equals("self")) {
						Log.d(TAG, "friendShip is self");
						JSONArray privateArray = userLibObject
								.getJSONArray("private_lib");
						Log.d(TAG, "fav size->" + privateArray.length());
							getBooksInfo(privateArray, "private", bookMap);
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
				profile.setUser_pic(userObject.getString("user_pic"));
				Log.d(TAG,
						"profile pic set outside if->" + profile.getUser_pic());
				if (profile.getUser_pic().contains("assets")) {
					profile.setUser_pic("http://" + AppPreferences.ipAdd
							+ "/eLibrary/library"
							+ userObject.getString("user_pic"));
					Log.d(TAG,
							"profile pic set inside if assets->"
									+ profile.getUser_pic());
				}
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
					Log.d(TAG, "friendship->" + profile.getFriendship());
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
           Log.d(TAG,"category in post->"+category);
			bookMap.put(category, books);
		}
	}

	public RequestParams getImageParams(String link) {

		return null;

	}

	public class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
		MyHolder holder;
		LibraryModel model = null;
		ImageView view;

		public BitmapAsyncTask(MyHolder holder) {
			this.holder = holder;
		}

		public BitmapAsyncTask() {

		}

		public BitmapAsyncTask(LibraryModel model) {
			this.model = model;
		}

		public BitmapAsyncTask(ImageView imageView, LibraryModel libraryModel) {
			this.view = imageView;
			this.model = libraryModel;
		}

		public BitmapAsyncTask(ImageView profilePicImageView) {
			this.view = profilePicImageView;
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
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				result = getResizedBitmap(result, 300, 300);
				if (model != null) {
					model.setImagebitmap(result);
				}
				view.setImageBitmap(result);
			}
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

}
