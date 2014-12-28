package com.example.elibrary.controllers;

import com.example.elibrary.models.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class ViewBookAsyncTask extends AsyncTask<RequestParams, Void, String> {
	Context context;
	String picUrl;

	public ViewBookAsyncTask(Context context, String picUrl) {
		this.context = context;
		this.picUrl=picUrl;
	}

	@Override
	protected String doInBackground(RequestParams... params) {
		// TODO Auto-generated method stub
		return new HttpManager().sendUserData(params[0]);
	}
	@Override
	protected void onPostExecute(String result) {
		Intent intent=new Intent(context,ViewBook.class);
		intent.putExtra("viewbook",result);
		intent.putExtra("picUrl", picUrl);
		context.startActivity(intent);
		//book/get_page
	}

}
