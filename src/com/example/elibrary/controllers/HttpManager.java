package com.example.elibrary.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.example.elibrary.models.RequestParams;

public class HttpManager {
	private final static String TAG = "HttpManager";
	private final static DefaultHttpClient httpClient = new DefaultHttpClient();

	public String sendUserData(RequestParams params) {
		URL url = null;
		try {
			if (params.getMethod() == "GET") {
				url = new URL(params.getURI() + "?" + params.getEncodedParams());
				Log.d(TAG, params.getURI() + "?" + params.getEncodedParams());
			} else {
				url = new URL(params.getURI());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			con.setRequestMethod(params.getMethod());
		} catch (ProtocolException e) {
			e.printStackTrace();
		}

		if (params.getMethod() == "POST") {
			Log.d(TAG, "post->" + params.getURI() + params.getEncodedParams());
			OutputStreamWriter writer = null;
			try {
				writer = new OutputStreamWriter(con.getOutputStream());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				writer.write(params.getEncodedParams());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, sb.toString());
		return sb.toString();
	}

	public static String hitTheServer(RequestParams params) {
		URL url = null;
		try {
			if (params.getMethod() == "GET") {
				url = new URL(params.getURI() + "?" + params.getEncodedParams());
			} else {
				url = new URL(params.getURI());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpPost httpPost = null;
		if (params.getMethod() == "POST") {
			httpPost = new HttpPost(url.toString());
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			for (String key : params.getParams().keySet()) {
				pairs.add(new BasicNameValuePair(key, params.getParams().get(
						key)));
			}
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(
						(List<? extends org.apache.http.NameValuePair>) pairs));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		HttpResponse response = null;
		try {
			if (params.getMethod() == "GET") {
				response = httpClient.execute(new HttpGet(url.toString()));
			} else {
				response = httpClient.execute(httpPost);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		InputStream in = null;
		if (entity != null) {
			try {
				in = entity.getContent();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(in));

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, sb.toString());
		return sb.toString();

	}

}
