package com.example.elibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendsModel implements Parcelable{
  
	private int id;
	private String name;
	private String email;
	private String profilePic;

	public FriendsModel(Parcel source) {
		name = source.readString();
		email = source.readString();
		profilePic = source.readString();
		id=source.readInt();
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
