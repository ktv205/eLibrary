package com.example.elibrary.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.example.elibrary.models.AppPreferences;

public class ParsePage {
	private String TAG = "ParsePage";

	public String[] page(String result) {
		String[] string = null;
		try {
			JSONObject mainObject = new JSONObject(result);
			if (mainObject.has("success")) {
				int success = mainObject.getInt("success");
				if (success == 1) {
					String pageString = null;
					if (mainObject.has("page")) {
						pageString = mainObject.getString("page");
						JSONObject pageObject = new JSONObject(pageString);
						string = new String[] {
								"http://" + AppPreferences.ipAdd
										+ "/eLibrary/library/"
										+ pageObject.getString("page_link"),
								String.valueOf(pageObject.getInt("no_of_pages")),
								pageObject.getString("book_id") };
					}
				} else {
					return null;
					// success zero
				}
			} else {
				return null;
				// else for no success name
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return string;
	}

}
