package com.example.elibrary.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileModel implements Parcelable{
	int user_id;
	int success;
	String user_name;
	String user_email;
	String friendship;
	ArrayList<LibraryModel> booklist=new ArrayList<LibraryModel>();
	Map<String, ArrayList<LibraryModel>> types=new HashMap<String, ArrayList<LibraryModel>>();
	public ProfileModel(Parcel source) {
		user_id=source.readInt();
		success=source.readInt();
		user_name=source.readString();
		user_email=source.readString();
		friendship=source.readString();
		booklist=source.readArrayList(ClassLoader.getSystemClassLoader());
		types=source.readHashMap(ClassLoader.getSystemClassLoader());
	}
	public ProfileModel() {
		// TODO Auto-generated constructor stub
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public String getFriendship() {
		return friendship;
	}
	public void setFriendship(String friendship) {
		this.friendship = friendship;
	}
	public ArrayList<LibraryModel> getBooklist() {
		return booklist;
	}
	
	public Map<String, ArrayList<LibraryModel>> getTypes() {
		return types;
	}
	public void setTypes(Map<String, ArrayList<LibraryModel>> types) {
		this.types = types;
	}
	public void setBooklist(ArrayList<LibraryModel> booklist) {
		this.booklist = booklist;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(user_id);
		dest.writeInt(success);
		dest.writeString(user_name);
		dest.writeString(user_email);
		dest.writeString(friendship);
		dest.writeList(booklist);
		dest.writeMap(types);
	}
    public static final Parcelable.Creator<ProfileModel> CREATOR=new Creator<ProfileModel>() {
		
		@Override
		public ProfileModel[] newArray(int size) {
			return new ProfileModel[size];
		}
		
		@Override
		public ProfileModel createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new ProfileModel(source);
		}
	};
    

}
