package com.example.elibrary.controllers;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Book extends FragmentActivity {
	private final static String TAG = "Book";
	private int count = 10;
	private Context context;
	private LayoutInflater mInflater;
	private static int ADDFAV = 0;
	private String page = null;
	private int book_id = -1;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_book);
		context = getApplicationContext();
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {
				Intent intent = getIntent();
				if (intent.hasExtra("book")) {
					String[] result = new ParsePage().page(intent.getExtras()
							.getString("book"));
					if (result != null) {
						getActionBar().setTitle(
								intent.getExtras().getString("title"));
						page = result[0];
						count = Integer.valueOf(result[1]);
						book_id = Integer.valueOf(result[2]);
						MyViewPagerAdapter pagerAdaper = new MyViewPagerAdapter(
								getSupportFragmentManager());
						ViewPager viewPager = (ViewPager) findViewById(R.id.book_pager);
						viewPager.setAdapter(pagerAdaper);
					} else {
						count = 1;
						TextView text = new TextView(this);
						text.setText("something went wrong,please go back");
						setContentView(text);

					}
				}

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
				startActivity(new Intent(Book.this, Book.class));
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CheckConnection.isConnected(context)) {
			if (CheckAuthentication.checkForAuthentication(context)) {

			} else {
				logout();
			}
		} else {
			noConnectionView();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_book_favourite) {
			if (ADDFAV == 0) {
				item.setTitle("remove from favourites");
				ADDFAV = 1;
			} else {
				item.setTitle("add to favourites");
				ADDFAV = 0;
			}
		}
		return true;
	}

	public void logout() {
		Intent intent = new Intent(this, Authentication.class);
		startActivity(intent);
		finish();
	}

	public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Bundle bundle = new Bundle();
			bundle.putInt("page_no", i + 1);
			bundle.putString("page", page);
			bundle.putInt("book_id", book_id);
			PageFragment pageFragment = new PageFragment();
			pageFragment.setArguments(bundle);
			return pageFragment;
		}

		@Override
		public int getCount() {
			return count-1;
		}

	}

}
