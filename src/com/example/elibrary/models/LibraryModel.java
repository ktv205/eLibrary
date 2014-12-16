package com.example.elibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LibraryModel implements Parcelable {
	private String bookName;
	private String bookAuthor;
	private String userName;
	private String category;
	private int bookId;

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(bookName);
		dest.writeString(bookAuthor);
		dest.writeString(userName);
		dest.writeString(category);
		dest.writeInt(bookId);
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public LibraryModel() {

	}

	public LibraryModel(String bookName, String bookAuthor, String userName,
			String category, int bookId) {

	}

	public LibraryModel(Parcel source) {
		bookName = source.readString();
		bookAuthor = source.readString();
		userName = source.readString();
		category = source.readString();
		bookId = source.readInt();

	}

	public static final Parcelable.Creator<LibraryModel> CREATOR = new Creator<LibraryModel>() {

		@Override
		public LibraryModel[] newArray(int size) {

			return new LibraryModel[size];
		}

		@Override
		public LibraryModel createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new LibraryModel(source);
		}
	};
}
