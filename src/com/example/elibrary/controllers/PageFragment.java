package com.example.elibrary.controllers;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class PageFragment extends Fragment {
	View view;
	SharedPreferences authPref;
	WebView text;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_page, container, false);
		authPref = MySharedPreferences.getSharedPreferences(getActivity(),
				AppPreferences.Auth.AUTHPREF);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
		String i = bundle.getString("page");
		int number = bundle.getInt("page_no");
		int id = bundle.getInt("book_id");
		if (number > 1) {
			new BookPagesAsyncTask().execute(getBookPagesParams(
					String.valueOf(id), String.valueOf(number)));
		}
		text = (WebView) view.findViewById(R.id.fragment_page_webview);
		if (number == 1) {
			text.loadUrl(i);
		}

	}

	public RequestParams getBookPagesParams(String id, String number) {
		RequestParams params = new RequestParams();
		params.setMethod("GET");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/book/get_page");
		params.setParam("book_id", id);
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("page_no", number);
		params.setParam("mobile", "1");
		return params;

	}

	public class BookPagesAsyncTask extends
			AsyncTask<RequestParams, Void, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(RequestParams... params) {
			return new HttpManager().sendUserData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			String[] string = new ParsePage().page(result);
			if(string!=null){
			text.loadUrl(string[0]);
			}else{
				TextView text=new TextView(getActivity());
				text.setText("something went wrong,please go back");
				getActivity().setContentView(text);
			}
		}
	}
}
