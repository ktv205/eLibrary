package com.example.elibrary.controllers;

import com.example.elibrary.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class Book extends FragmentActivity {
	private final static String TAG = "FragmentActivity";
	private int count = 10;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_book);
		MyViewPagerAdapter pagerAdaper = new MyViewPagerAdapter(
				getSupportFragmentManager());
		ViewPager viewPager = (ViewPager) findViewById(R.id.book_pager);
		viewPager.setAdapter(pagerAdaper);
	}

	public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Bundle bundle = new Bundle();
			bundle.putInt("test", i);
			PageFragment pageFragment = new PageFragment();
			pageFragment.setArguments(bundle);
			return pageFragment;

		}

		@Override
		public int getCount() {
			return count;
		}

	}

}
