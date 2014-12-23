package com.example.elibrary.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.elibrary.R;
import com.example.elibrary.controllers.Friends.FriendsListAdapter.MyHolder;
import com.example.elibrary.controllers.Logout.OnLogoutSuccessful;
import com.example.elibrary.controllers.MainActivity.BitmapAsyncTask;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.LibraryModel;
import com.example.elibrary.models.RequestParams;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class Uploads extends Activity implements OnLogoutSuccessful,
		OnClickListener {
	private static final String TAG = "Uploads";
	private Menu menuGlobal;
	private SharedPreferences authPref;
	private int auth;
	private Context context;
	// private ImageButton uploadButton;
	private Button submitButton, uploadButton;
	private TextView fileNameTextview;
	private LayoutInflater mInflater;
	private static final int PICK_FILE_REQUEST = 1;
	private LinearLayout parentLinear;
	private String filePath, fileName;
	ArrayList<LibraryModel> libraryModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uploads);
		context = getApplicationContext();
		authPref = MySharedPreferences.getSharedPreferences(context,
				AppPreferences.Auth.AUTHPREF);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (CheckConnection.isConnected(context)) {
			Log.d(TAG, "internet connected");
			if (CheckAuthentication.checkForAuthentication(context)) {
				initialize();
				new GetUploadHistoryAsycTask().execute(getRequestParams());
				// fillBooks();
			} else {
				logout();
			}

		} else {

		}
	}

	public RequestParams getRequestParams() {
		Log.d(TAG, "getParams()");
		RequestParams params = new RequestParams();
		params.setMethod("GET");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/profile");
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("user", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("mobile", "1");
		Log.d(TAG,
				"user_id->"
						+ authPref.getInt(AppPreferences.Auth.KEY_USERID, -1));
		return params;

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

	public void initialize() {
		// uploadButton = (ImageButton)
		// findViewById(R.id.uploads_button_uplaod);
		uploadButton = (Button) findViewById(R.id.uploads_button_uplaod);
		submitButton = (Button) findViewById(R.id.uploads_button_submit);
		fileNameTextview = (TextView) findViewById(R.id.uploads_textview_filename);
		parentLinear = (LinearLayout) findViewById(R.id.uploads_linear_parent);
		fileNameTextview.setText("");
		uploadButton.setOnClickListener(this);
		submitButton.setOnClickListener(this);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.uploadbutton1);
		bitmap = getResizedBitmap(bitmap, 96, 256);
		// uploadButton.setImageBitmap(bitmap);

	}

	public void fillBooks() {
		View singleCategory = mInflater.inflate(
				R.layout.inflate_single_category, null, false);
		TextView textView = (TextView) singleCategory
				.findViewById(R.id.single_category_textview_book_category);
		Log.d(TAG,"category in fill books->"+"My Upload History");
		textView.setText("Uploads");
		LinearLayout horizontal = (LinearLayout) singleCategory
				.findViewById(R.id.single_category_linearlayout_horizontal);

		for (int j = 0; j < libraryModel.size(); j++) {
			View singleBook = mInflater.inflate(
					R.layout.inflate_singlebook, null, false);
			ImageView imageView = (ImageView) singleBook
					.findViewById(R.id.single_book_cover);
			// imageView.setImageBitmap(profile.getTypes().get("uploads")
			// .get(j).getImagebitmap());
			new BitmapAsyncTask(imageView).execute(libraryModel.get(j).getProfilePic());
			TextView titleTextView = (TextView) singleBook
					.findViewById(R.id.single_book_name);
			TextView authorTextView = (TextView) singleBook
					.findViewById(R.id.single_book_author);
			TextView userNameTextView = (TextView) singleBook
					.findViewById(R.id.single_book_user);
			titleTextView.setText(libraryModel.get(j).getBookName());
			authorTextView.setText(libraryModel.get(j).getBookAuthor());
			userNameTextView.setText(libraryModel.get(j).getUserName());
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(Uploads.this, Book.class));

				}
			});
			horizontal.addView(singleBook);
		}
		if (parentLinear != null) {
			parentLinear.addView(singleCategory);

		}
		setContentView(parentLinear);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {
				setMenuName();
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
				startActivity(new Intent(Uploads.this, Uploads.class));
				finish();
			}
		});
	}

	public boolean isConntected() {
		Log.d(TAG, "isConnected method");
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			return false;
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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		int width = submitButton.getWidth();
		int height = submitButton.getHeight();
		Log.d(TAG, "height of submit button->" + height);
		Log.d(TAG, "width of the submit button->" + width);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.settings_logout) {
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
		} else if (id == R.id.settings_library) {
			startActivity(new Intent(this, MainActivity.class));
		} else if (id == R.id.name_account_menu) {
			Intent intent = new Intent(this, Profile.class);
			intent.putExtra(AppPreferences.PutExtraKeys.PUTEXTRA_WHO_PROFILE,
					AppPreferences.SELF);
			startActivity(intent);
			startActivity(intent);
		} else if (id == R.id.settings_friends) {
			startActivity(new Intent(this, Friends.class));
		}
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

	public void logout() {
		Intent intent = new Intent(this, Authentication.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onCleardFields(boolean cleared) {
		if (cleared) {
			logout();
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == uploadButton.getId()) {
			getFile();
		} else if (id == submitButton.getId()) {
			if (fileNameTextview.getText().toString().equals("")) {
				Toast.makeText(this, "select a book using upload button",
						Toast.LENGTH_LONG).show();
			} else {
				new BookUploadAsyncTask().execute(new String[] { filePath,
						fileName });
			}
		}

	}

	public void getFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("application/pdf");
		startActivityForResult(intent, PICK_FILE_REQUEST);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_FILE_REQUEST) {
			if (resultCode == RESULT_OK) {
				Log.d(TAG, "URI->" + data.getData().getPath());
				Log.d(TAG, "URI->" + data.getDataString());
				String pathToFile = null;
				Cursor cursor = getContentResolver().query(data.getData(),
						null, null, null, null);
				Log.d(TAG, "get column count=>" + cursor.getColumnCount());
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					Log.d(TAG, cursor.getColumnName(i));
				}
				cursor.moveToFirst();
				final String name = cursor.getString(cursor
						.getColumnIndex("_display_name"));
				if (makeDirectory()) {
					Log.d(TAG, "make directory succesful and returned true");
					pathToFile = makeFile(name, data);
					Log.d(TAG, "path to file is" + pathToFile);
				}
				String substring = name.substring(0, 10) + "...";
				fileNameTextview.setText(substring);
				filePath = pathToFile;
				fileName = name;

			}
		}
	}

	public boolean makeDirectory() {
		if (new File(Environment.getExternalStorageDirectory() + "/elibrary")
				.exists()) {
			Log.d(TAG, "Directory elibrary  exist");
			return true;
		} else {
			Log.d(TAG, "Directory elibrary does not exist");
			boolean created = new File(
					Environment.getExternalStorageDirectory(), "elibrary")
					.mkdirs();
			if (created) {
				return true;

			} else {
				Log.d(TAG, "directory not created");
				return false;

			}

		}
	}

	public String makeFile(String name, Intent intent) {
		String PathToDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + "elibrary";

		InputStream in = null;
		try {
			in = getContentResolver().openInputStream(intent.getData());
			if (in == null) {
				Log.d(TAG, "in is null");
			} else {
				Log.d(TAG, "in is not null");
			}
		} catch (FileNotFoundException e) {
			Log.d(TAG, "input Stream not found");
			e.printStackTrace();
		}
		OutputStream out = null;
		try {
			out = new FileOutputStream(PathToDir + "/" + name);
		} catch (FileNotFoundException e) {
			Log.d(TAG, "file output stream not found");
			e.printStackTrace();
		}
		byte[] data = new byte[1024];
		int count;
		try {
			while ((count = in.read(data)) != -1) {
				out.write(data, 0, count);
			}
		} catch (IOException e) {
			Log.d(TAG, "writing not done into the file");
			e.printStackTrace();
		}
		try {
			if (out == null) {
				Log.d(TAG, "out is null");
			} else {
				Log.d(TAG, "out is not null");
			}
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return PathToDir + "/" + name;

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

	public class BookUploadAsyncTask extends AsyncTask<String[], Void, Void> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {

			dialog = new ProgressDialog(Uploads.this);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String[]... params) {

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
			Toast.makeText(Uploads.this, "UploadComplete", Toast.LENGTH_LONG)
					.show();
			fileNameTextview.setText("");
			fileName = null;
			filePath = null;
		}
	}

	public class GetUploadHistoryAsycTask extends
			AsyncTask<RequestParams, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {

			dialog = new ProgressDialog(Uploads.this);
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
			Log.d(TAG, result);
			libraryModel = new ArrayList<LibraryModel>();
			JSONObject mainObject = null;
			try {
				mainObject = new JSONObject(result);
				int success = mainObject.getInt("success");
				Log.d(TAG, "id->" + success);
				if (success == 1) {
					JSONObject userLibObject = mainObject
							.getJSONObject("user_lib");
					JSONArray uploadsArray = userLibObject
							.getJSONArray("uploads");
					getBooksInfo(uploadsArray, libraryModel);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public void getBooksInfo(JSONArray array,
				ArrayList<LibraryModel> libraryModel) {

			try {

				for (int i = 0; i < array.length(); i++) {
					Log.d(TAG, "book number in loop->" + i);
					JSONObject bookObject = array.getJSONObject(i);
					LibraryModel model = new LibraryModel();
					model.setBookId(Integer.valueOf(bookObject
							.getString("book_id")));
					Log.d(TAG, "setBookId->" + model.getBookId());
					model.setPrivacy(bookObject.getString("book_id"));
					Log.d(TAG, "setBookId->" + model.getPrivacy());
					model.setBookAuthor(bookObject.getString("book_author"));
					Log.d(TAG, "setBookAuthor->" + model.getBookAuthor());
					model.setBookName(bookObject.getString("book_title"));
					Log.d(TAG, "setBookTitle->" + model.getBookName());
					model.setProfilePic(bookObject.getString("book_pic"));
					Log.d(TAG, "setBookPic->" + model.getProfilePic());
					model.setAccess(Integer.valueOf(bookObject
							.getString("access")));
					Log.d(TAG, "setBookAccess->" + model.getAccess());
					model.setBookGenre(bookObject.getString("book_genre"));
					Log.d(TAG, "setBookGenre->" + model.getBookGenre());
					JSONObject uploadedUserObject = bookObject
							.getJSONObject("uploaded_by");
					model.setUserName(uploadedUserObject.getString("user_name"));
					Log.d(TAG, "setUserName->" + model.getUserName());
					libraryModel.add(model);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            fillBooks();
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
			result=getResizedBitmap(result, 300, 300);
			view.setImageBitmap(result);

		}
	}

}
