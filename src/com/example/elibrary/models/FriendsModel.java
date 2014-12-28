package com.example.elibrary.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;

public class FriendsModel implements Parcelable{
  
	private int id;
	private String name;
	private String email;
	private String profilePic;
	private Bitmap profilePicBitmap;

	public FriendsModel(Parcel source) {
		name = source.readString();
		email = source.readString();
		profilePic = source.readString();
		id=source.readInt();
		profilePicBitmap=source.readParcelable(Bitmap.class.getClassLoader());
	}

	public FriendsModel() {

	}

	public FriendsModel(String name, String email, String profilePic,int id) {
		super();
		this.name = name;
		this.email = email;
		this.profilePic = profilePic;
		this.id=id;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(email);
		dest.writeString(profilePic);
		dest.writeInt(id);
		dest.writeValue(profilePicBitmap);

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

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public Bitmap getProfilePicBitmap() {
		return profilePicBitmap;
	}

	public void setProfilePicBitmap(Bitmap profilePicBitmap) {
		this.profilePicBitmap = profilePicBitmap;
	}


	public static final Parcelable.Creator<FriendsModel> CREATOR = new Creator<FriendsModel>() {

		@Override
		public FriendsModel[] newArray(int size) {
			// TODO Auto-generated method stub
			return new FriendsModel[size];
		}

		@Override
		public FriendsModel createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new FriendsModel(source);
		}
	};

}
