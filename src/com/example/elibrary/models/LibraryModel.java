package com.example.elibrary.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class LibraryModel implements Parcelable {
	private String bookName;
	private String bookAuthor;
	private String userName;
	private String category;
	private int bookId;
	private String profilePic;
	private String type;
	private int access;
	private String isbn;
	private String bookGenre;
	private String privacy;
	private Bitmap imageBitmap;
	private int user_id;

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
		dest.writeString(profilePic);
		dest.writeString(type);
		dest.writeInt(access);
		dest.writeString(isbn);
		dest.writeString(bookGenre);
		dest.writeString(privacy);
		dest.writeValue(imageBitmap);
		dest.writeInt(user_id);
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public LibraryModel() {

	}

	public LibraryModel(String bookName, String bookAuthor, String userName,
			String category, int bookId, String profilePic) {

	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getBookGenre() {
		return bookGenre;
	}

	public void setBookGenre(String bookGenre) {
		this.bookGenre = bookGenre;
	}

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
	

	public Bitmap getImagebitmap() {
		return imageBitmap;
	}

	public void setImagebitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}

	public LibraryModel(Parcel source) {
		bookName = source.readString();
		bookAuthor = source.readString();
		userName = source.readString();
		category = source.readString();
		bookId = source.readInt();
		profilePic = source.readString();
		type = source.readString();
		access = source.readInt();
		isbn = source.readString();
		bookGenre = source.readString();
		privacy = source.readString();
		imageBitmap=source.readParcelable(Bitmap.class.getClassLoader());
		user_id=source.readInt();

	}

	public static final Parcelable.Creator<LibraryModel> CREATOR = new Creator<LibraryModel>() {

		@Override
		public LibraryModel[] newArray(int size) {

			return new LibraryModel[size];
		}

		@Override
		public LibraryModel createFromParcel(Parcel source) {
			return new LibraryModel(source);
		}
	};
}
