package com.example.elibrary.models;

public class AppPreferences {
	public static final int SIGNIN = 3;
	public static final int SIGNUP = 4;
	public static final String AUTH_TAG = "auth_tag";
	public static final String FB_TAG = "fb_tag";
	public static final String G_TAG = "g_tag";
	public static final String EMAIL_TAG = "email_tag";
	public static final String AUTH_KEY = "Auth_key";
	public static int test = 0;
	public static String ipAdd = "54.174.223.185";
	public static final int BOOKSEARCH = 7;
	public static final int FRIENDSEARCH = 8;
	public static final int FRIEND = 9;
	public static final int STRANGER = 10;
	public static final int SELF = 11;

	public static abstract class Auth {
		public static final String AUTHPREF = "AUTH_SHAREDPREFERENCES";
		public static final String KEY_AUTH = "AUTH?";
		public static final int FACEBOOK_AUTH = 1;
		public static final int GOOGLE_AUTH = 0;
		public static final int EMAIL_AUTH = 2;
		public static final String FACEBOOK_ENUM = "fb";
		public static final String GOOGLE_ENUM = "g";
		public static final String EMAIL_ENUM = "gen";
		public static final int EMAIL_AUTH_CODE = 5;
		public static final String KEY_VERIFICATION = "verification";
		public static final String KEY_NAME = "ACCOUTHOLDER_NAME";
		public static final String KEY_EMAIL = "ACCOUNTHOLDER_EMAIL";
		public static final String KEY_PERSON_ID = "ACCOUNTHOLDER_DATABASE_ID";
		public static final String KEY_PICTURE = "PROFILE_PICTURE_LINK";
		public static final String KEY_FACEBOOKID = "FACEBOOK_ID";
		public static final String KEY_GOOGLEID = "GOOGLE_ID";
		public static final String KEY_PARCELABLE_SIGNUP_VERIFICATION = "user_parcelable";
		public static final String KEY_USERID = "keyuserid";
		public static final int ERROR_DATABASE = 1;
		public static final int ERROR_USER_DOESNT_EXIST = 2;
		public static final int ERROR_CONFORMATION_SENT = 3;
		public static final int ERROR_CONFORMATION_DONE = 4;
		public static final String KEY_PARCELABLE_SIGNIN_PASSWORDRETRIVAL = "user_parcelable_signin_passwordretrival";
	}

	public static abstract class Codes {
		public static final int RC_SIGN_IN = 6;
	}

	public static abstract class PutExtraKeys {
		public static final String PUTEXTRA_USERID = "putextrakey";
		public static final String PUTEXTRA_SEARCHTYPE = "searchtype";
		public static final String PUTEXTRA_WHO_PROFILE = "whoes profile";
	}

}
