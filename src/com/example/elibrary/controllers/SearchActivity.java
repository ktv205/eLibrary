package com.example.elibrary.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.elibrary.R;
import com.example.elibrary.controllers.Logout.OnLogoutSuccessful;
import com.example.elibrary.controllers.SearchActivity.ToBeFriendsAdapter.MyHolder;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.FriendsModel;
import com.example.elibrary.models.LibraryModel;
import com.example.elibrary.models.RequestParams;
import com.example.elibrary.models.ToBeFriendsModel;
import com.google.android.gms.internal.ki;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class SearchActivity extends Activity implements OnLogoutSuccessful {
	private static final String TAG = "SearchActivity";
	private TextView queryWord;
	private ListView toBeFriendsListview;
	private int searchType;
	private SharedPreferences authPref;
	private Context context;
	private Menu menuGlobal;
	private int auth;
	private LayoutInflater mInflater;
	private ArrayList<FriendsModel> friendsModel;
	private ArrayList<LibraryModel> libraryModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Log.d(TAG, "onCreate");
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "internet connected");
			if (CheckAuthentication.checkForAuthentication(context)) {
				initialize();
				handleIntent(getIntent());
				// trainingSet();
			} else {
				logout();
			}
		} else {
			noConnectionView();
		}

	}

	// public void trainingSet() {
	// ToBeFriendsModel obj = new ToBeFriendsModel();
	// obj.setEmail("example@example.com");
	// obj.setName("example");
	// obj.setProfilePic("https://graph.facebook.com/100000236193433/picture?type=normal");
	// // toBeFriendsList = new ArrayList<ToBeFriendsModel>();
	// // toBeFriendsList.add(obj);
	// // ToBeFriendsAdapter adapter = new ToBeFriendsAdapter(toBeFriendsList);
	// toBeFriendsListview.setAdapter(adapter);
	// }

	public void fillBooks() {
		BooksAdapter adapter = new BooksAdapter(libraryModel);
		toBeFriendsListview.setAdapter(adapter);
		toBeFriendsListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SearchActivity.this, Book.class);
				TextView bookId = (TextView) view
						.findViewById(R.id.contents_list_books_search_textview_bookid);
				intent.putExtra("book_id", bookId.getText().toString());
				startActivity(intent);

			}
		});
	}

	public void fillFriends() {
		ToBeFriendsAdapter adapter = new ToBeFriendsAdapter(friendsModel);
		toBeFriendsListview.setAdapter(adapter);
		toBeFriendsListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView userIdTextView = (TextView) view
						.findViewById(R.id.contents_list_search_textview_userid);
				String toUserId = userIdTextView.getText().toString();
				Intent intent = new Intent(SearchActivity.this, Profile.class);
				intent.putExtra("to_user_id", toUserId);
				// intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_WHO_PROFILE,
				// AppPreferences.S);
				startActivity(intent);
			}
		});
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
				startActivity(new Intent(SearchActivity.this,
						SearchActivity.class));
				finish();
			}
		});
	}

	@Override
	public void startActivity(Intent intent) {
		Log.d(TAG, "onStartActivity");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.d(TAG, "intent.getAction");
			intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE,
					searchType);
		}
		super.startActivity(intent);
	}

	public void initialize() {
		queryWord = (TextView) findViewById(R.id.search_textview_word);
		toBeFriendsListview = (ListView) findViewById(R.id.search_listview_generic);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent");
		handleIntent(intent);

	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			if (intent.getExtras().containsKey(
					AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE)) {
				searchType = intent.getExtras().getInt(
						AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE);
				if (intent.getExtras().getInt(
						AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE) == AppPreferences.BOOKSEARCH) {
					Log.d(TAG, "bookSearch");
					getActionBar().setTitle("Book Search");
					new BookSearchAsyncTask().execute(getRequestParams(query,
							searchType));
				} else if (intent.getExtras().getInt(
						AppPreferences.PutExtraKeys.PUTEXTRA_SEARCHTYPE) == AppPreferences.FRIENDSEARCH) {
					Log.d(TAG, "friendsSearch");
					getActionBar().setTitle("Friend Search");
					new GetToBeFriendsAsyncTask().execute(getRequestParams(
							query, searchType));
				}
			}
			queryWord.setText(query);
			Log.d(TAG, query);
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

	public RequestParams getRequestParams(String query, int searchType) {
		RequestParams params = new RequestParams();
		params.setMethod("GET");
		params.setParam("mobile", "1");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/search");
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		if (searchType == AppPreferences.BOOKSEARCH) {
			params.setParam("search_cat", "books");
		} else if (searchType == AppPreferences.FRIENDSEARCH) {
			params.setParam("search_cat", "users");
		}
		params.setParam("query_string", query);
		return params;

	}

	public void logout() {
		Intent intent = new Intent(this, Authentication.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		menuGlobal = menu;
		setMenuName();
		return true;

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
		} else if (id == R.id.settings_library) {
			startActivity(new Intent(this, MainActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	public class GetToBeFriendsAsyncTask extends
			AsyncTask<RequestParams, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(SearchActivity.this);
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
			friendsModel = new ArrayList<FriendsModel>();
			try {
				JSONObject mainObject = new JSONObject(result);
				int success = mainObject.getInt("success");
				if (success == 1) {
					JSONArray resultsArray = mainObject.getJSONArray("results");
					for (int i = 0; i < resultsArray.length(); i++) {
						Log.d(TAG, "first for loop");
						JSONObject usersObject = resultsArray.getJSONObject(i);
						JSONArray usersArray = usersObject
								.getJSONArray("users");
						for (int j = 0; j < usersArray.length(); j++) {
							FriendsModel model = new FriendsModel();
							JSONObject userObject = usersArray.getJSONObject(j);
							model.setEmail(userObject.getString("email"));
							model.setName(userObject.getString("user_name"));
							model.setId(Integer.valueOf(userObject
									.getString("user_id")));
							model.setProfilePic(userObject
									.getString("user_pic"));
							friendsModel.add(model);
						}

					}
				}
				fillFriends();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public class BookSearchAsyncTask extends
			AsyncTask<RequestParams, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(SearchActivity.this);
			dialog.show();
		}

		@Override
		protected String doInBackground(RequestParams... params) {
			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			Log.d(TAG, "result->" + result);
			libraryModel = new ArrayList<LibraryModel>();
			try {
				JSONObject mainObject = new JSONObject(result);
				int success = mainObject.getInt("success");
				if (success == 1) {
					JSONArray resultsArray = mainObject.getJSONArray("results");
					for (int i = 0; i < resultsArray.length(); i++) {
						JSONObject booksObject = resultsArray.getJSONObject(i);
						JSONArray booksArray = booksObject
								.getJSONArray("books");
						for (int j = 0; j < booksObject.length(); j++) {
							LibraryModel model = new LibraryModel();
							JSONObject bookObject = booksArray.getJSONObject(j);
							model.setBookAuthor(bookObject
									.getString("book_author"));
							model.setBookName(bookObject
									.getString("book_title"));
							model.setBookGenre(bookObject
									.getString("book_genre"));
							model.setProfilePic(bookObject
									.getString("book_pic"));
							model.setBookId(Integer.valueOf(bookObject
									.getString("book_id")));
							model.setIsbn(bookObject.getString("book_isbn"));
							model.setUser_id(Integer.valueOf(bookObject
									.getString("user_id")));
							libraryModel.add(model);
						}
					}
					fillBooks();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public class ToBeFriendsAdapter extends BaseAdapter {
		ArrayList<FriendsModel> toBeFriendsList;

		public ToBeFriendsAdapter(ArrayList<FriendsModel> toBeFriendsList) {
			this.toBeFriendsList = toBeFriendsList;
		}

		@Override
		public int getCount() {
			return toBeFriendsList.size();
		}

		@Override
		public Object getItem(int position) {
			return toBeFriendsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public class MyHolder {
			TextView name, email, userId;
			ImageView profilePic;

			public MyHolder(View v) {
				name = (TextView) v
						.findViewById(R.id.contents_list_search_textview_name);
				email = (TextView) v
						.findViewById(R.id.contents_list_search_texview_email);
				profilePic = (ImageView) v
						.findViewById(R.id.contents_list_search_imageview_profilepic);
				userId = (TextView) v
						.findViewById(R.id.contents_list_search_textview_userid);
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			MyHolder myHolder;
			if (view == null) {
				LayoutInflater inflater = LayoutInflater
						.from(SearchActivity.this);
				view = inflater.inflate(R.layout.contents_list_search, parent,
						false);
				myHolder = new MyHolder(view);
				view.setTag(myHolder);
			} else {
				myHolder = (MyHolder) view.getTag();

			}
			myHolder.name.setText(toBeFriendsList.get(position).getName());
			myHolder.email.setText(toBeFriendsList.get(position).getEmail());
			myHolder.userId.setText(String.valueOf(toBeFriendsList
					.get(position).getId()));
			// new
			// BitmapAsyncTask(myHolder).execute(toBeFriendsList.get(position)
			// .getProfilePic());

			return view;
		}

	}

	public class BooksAdapter extends BaseAdapter {
		ArrayList<LibraryModel> libraryModel;

		public BooksAdapter(ArrayList<LibraryModel> libraryModel) {
			this.libraryModel = libraryModel;
		}

		@Override
		public int getCount() {
			return libraryModel.size();
		}

		@Override
		public Object getItem(int position) {
			return libraryModel.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public class BookHolder {
			TextView title, author, bookId;
			ImageView coverPic;

			public BookHolder(View v) {
				title = (TextView) v
						.findViewById(R.id.contents_list_books_search_textview_title);
				author = (TextView) v
						.findViewById(R.id.contents_list_books_search_texview_author);
				coverPic = (ImageView) v
						.findViewById(R.id.contents_list_books_search_imageview_coverpic);
				bookId = (TextView) v
						.findViewById(R.id.contents_list_books_search_textview_bookid);
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			BookHolder myHolder;
			if (view == null) {
				LayoutInflater inflater = LayoutInflater
						.from(SearchActivity.this);
				view = inflater.inflate(R.layout.contents_list_books_search,
						parent, false);
				myHolder = new BookHolder(view);
				view.setTag(myHolder);
			} else {
				myHolder = (BookHolder) view.getTag();

			}
			myHolder.title.setText(libraryModel.get(position).getBookName());
			myHolder.author.setText(libraryModel.get(position).getBookAuthor());
			myHolder.bookId.setText(String.valueOf(libraryModel.get(position)
					.getBookId()));
			// new
			// BitmapAsyncTask(BookHolder).execute(toBeFriendsList.get(position)
			// .getProfilePic());

			return view;
		}

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

	@Override
	public void onCleardFields(boolean cleared) {
		if (cleared) {
			logout();
		}

	}

}
