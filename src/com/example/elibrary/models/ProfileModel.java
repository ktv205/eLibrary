package com.example.elibrary.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileModel {
	int user_id;
	int success;
	String user_name;
	String user_email;
	String friendship;
	ArrayList<LibraryModel> booklist=new ArrayList<LibraryModel>();
	Map<String, ArrayList<LibraryModel>> types=new HashMap<String, ArrayList<LibraryModel>>();
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
    
    

}
