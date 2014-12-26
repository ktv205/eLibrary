package com.example.elibrary.controllers;

import com.example.elibrary.models.RequestParams;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class BookPagesAsyncTask extends AsyncTask<RequestParams, Void, String> {
	Context context;
	String title;
	ProgressDialog dialog;

	public BookPagesAsyncTask(Context context, String title) {
		this.context = context;
		this.title = title;
	}

	public BookPagesAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.show();
	}

	@Override
	protected String doInBackground(RequestParams... params) {
		return new HttpManager().sendUserData(params[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		dialog.dismiss();
		Intent intent = new Intent(context, Book.class);
		intent.putExtra("book", result);
		if (title != null) {
			intent.putExtra("title", title);
		}
		context.startActivity(intent);
	}

}
