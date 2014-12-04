package com.example.elibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
	private String name;
	private String email;
	private String password;
	private String auth;

	public UserModel(Parcel source) {
		name = source.readString();
		email = source.readString();
		password = source.readString();
	}

	public UserModel() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {

		@Override
		public UserModel createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new UserModel(source);
		}

		@Override
		public UserModel[] newArray(int size) {
			return new UserModel[size];
		}

	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(email);
		dest.writeString(password);
	}

}
