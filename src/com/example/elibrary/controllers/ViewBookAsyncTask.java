package com.example.elibrary.controllers;

import com.example.elibrary.models.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class ViewBookAsyncTask extends AsyncTask<RequestParams, Void, String> {
	Context context;

	public ViewBookAsyncTask(Context context) {
		this.context = context;
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
		context.startActivity(intent);
		//book/get_page
	}

}
