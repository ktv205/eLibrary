package com.example.elibrary.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.elibrary.R;
import com.example.elibrary.models.AppPreferences;
import com.example.elibrary.models.RequestParams;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ViewBook extends Activity {
	int book_id;
	String title;
	SharedPreferences authPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewbook);
		authPref=MySharedPreferences.getSharedPreferences(this,AppPreferences.Auth.AUTHPREF);
		Intent intent=getIntent();
		if(intent.hasExtra("viewbook")){
			parseJson(intent.getExtras().getString("viewbook"));
		}
		Button readBookButton=(Button)findViewById(R.id.viewbook_button_read);
		readBookButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new BookPagesAsyncTask(ViewBook.this,title)
				.execute(getBookPagesParams(String.valueOf(book_id)));
				
			}
		});
		
	}
	public RequestParams getBookPagesParams(String id) {
		RequestParams params = new RequestParams();
		params.setMethod("GET");
		params.setURI("http://" + AppPreferences.ipAdd
				+ "/eLibrary/library/index.php/book/get_page");
		params.setParam("book_id", id);
		params.setParam("user_id", String.valueOf(authPref.getInt(
				AppPreferences.Auth.KEY_USERID, -1)));
		params.setParam("page_no","1");
		params.setParam("mobile","1");
		return params;

	}
	public void parseJson(String result){
		try {
			JSONObject mainObject=new JSONObject(result);
			JSONObject bookObject=mainObject.getJSONObject("book_info");
			book_id=Integer.valueOf(bookObject.getString("book_id"));
			title=bookObject.getString("book_title");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
}
