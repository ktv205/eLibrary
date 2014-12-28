package com.example.elibrary.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.elibrary.R;
import com.example.elibrary.controllers.Friends.FriendsListAdapter.MyHolder;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.LibraryModel;
import com.example.elibrary.models.RequestParams;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ViewBook extends Activity {
	int book_id;
	String title;
	SharedPreferences authPref;
	String picUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewbook);
		authPref = MySharedPreferences.getSharedPreferences(this,
				AppPreferences.Auth.AUTHPREF);
		Intent intent = getIntent();
		if (intent.hasExtra("viewbook")) {
			parseJson(intent.getExtras().getString("viewbook"));
		}
		if (intent.hasExtra("picUrl")) {
			picUrl = intent.getExtras().getString("picUrl");
			setCoverPic();

		}
		Button readBookButton = (Button) findViewById(R.id.viewbook_button_read);
		readBookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new BookPagesAsyncTask(ViewBook.this, title)
						.execute(getBookPagesParams(String.valueOf(book_id)));

			}
		});
		Button backButton = (Button) findViewById(R.id.viewbook_button_back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});

	}

	public void setCoverPic() {
		ImageView imageView = (ImageView) findViewById(R.id.viewbook_imageview_bookcover);
		new BitmapAsyncTask(imageView).execute(picUrl);

	}

	public RequestParams getBookPagesParams(String id) {
		RequestParams params = new RequestParams();
		params.setMethod("GET");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/book/get_page");
		params.setParam("book_id", id);
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("page_no", "1");
		params.setParam("mobile", "1");
		return params;

	}

	public void parseJson(String result) {
		TextView titleTextView, authorTextView, userTextView, timeTextView, popularityTextView;
		SeekBar seekBar;
		String user, time, author;
		int popularity;
		try {
			JSONObject mainObject = new JSONObject(result);
			JSONObject bookObject = mainObject.getJSONObject("book_info");
			book_id = Integer.valueOf(bookObject.getString("book_id"));
			title = bookObject.getString("book_title");
			user = bookObject.getString("book_uploader_name");
			time = bookObject.getString("book_upload_time");
			author = bookObject.getString("book_author");
			popularity = bookObject.getInt("popularity");
			popularity = randInt(50, 100);
			titleTextView = (TextView) findViewById(R.id.viewbook_textview_title);
			authorTextView = (TextView) findViewById(R.id.viewbook_textview_author);
			userTextView = (TextView) findViewById(R.id.viewbook_textview_user);
			seekBar = (SeekBar) findViewById(R.id.viewbook_seekbar_popularity);
			timeTextView = (TextView) findViewById(R.id.viewbook_textview_time);
			popularityTextView = (TextView) findViewById(R.id.viewbook_textview_percentage);
			titleTextView.setText(title);
			authorTextView.setText(author);
			userTextView.setText(user);
			timeTextView.setText(time);
			popularityTextView.setText(popularity + "%");
			seekBar.setProgress(popularity);

			seekBar.setEnabled(false);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
		MyHolder holder;
		LibraryModel model;
		ImageView view;
		LibraryModel libraryModel;

		public BitmapAsyncTask(MyHolder holder) {
			this.holder = holder;
		}

		public BitmapAsyncTask(ImageView imageView) {
			this.view = imageView;
		}

		public BitmapAsyncTask(LibraryModel model) {
			this.model = model;
		}

		public BitmapAsyncTask(ImageView imageView, LibraryModel libraryModel) {
			this.view = imageView;
			this.libraryModel = libraryModel;
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
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				view.setImageBitmap(result);
			}
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

}
