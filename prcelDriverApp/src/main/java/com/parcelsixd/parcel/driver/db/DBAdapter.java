package com.parcelsixd.parcel.driver.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parcelsixd.parcel.driver.model.User;

public class DBAdapter {

	private static final String TAG = "[ DBAdapter ]";

	private static final String KEY_ROWID = "rowid";
	private static final String KEY_LAT = "latitude";
	private static final String KEY_LON = "longitude";
	private static final String KEY_USER_ID = "user_id";





	private static final String KEY_FULL_NAME = "full_name";
	private static final String KEY_FIRST_NAME = "first_name";
	private static final String KEY_LAST_NAME = "last_name";
	private static final String KEY_ADDRESS = "address";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_CONTACT = "phone";
	private static final String KEY_BIO = "bio";
	private static final String KEY_PICTURE = "picture";
	private static final String KEY_ZIP_CODE = "zipcode";
	private static final String KEY_CAR_MODEL = "car_model";
	private static final String KEY_CAR_NUMBER = "licence_number";
	private static final String KEY_ISAPPROVED= "is_approved";
	private static final String KEY_CARMAKE= "car_make";
	private static final String KEY_CARYEAR= "car_year";
	private static final String PACKAGE_TYPE = "package_type";





	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "UberClientForX";

	private static final String TABLE_LOCATION = "table_location";
	// private static final String REQUEST_TABLE = "ClientRequest";
	private static final String USER_TABLE = "User";
	private static final String TABLE_CREATE_LOCATION = "create table "
			+ TABLE_LOCATION + "( " + KEY_ROWID
			+ " integer primary key autoincrement," + KEY_LAT
			+ " text not null," + KEY_LON + " text not null);";






	private static final String TABLE_CREATE_USER = "create table "
			+ USER_TABLE + "( " + KEY_ROWID
			+ " integer primary key autoincrement," + KEY_USER_ID + " text not null,"
			+ KEY_FULL_NAME + " text not null,"+ KEY_FIRST_NAME + " text not null,"
			+ KEY_LAST_NAME + " text not null," + KEY_CONTACT + " text not null,"
			+ KEY_EMAIL + " text not null," + KEY_PICTURE + " text not null,"
			+ KEY_ISAPPROVED + " text not null," + KEY_BIO + " text," + KEY_ADDRESS + " text,"
			+KEY_ZIP_CODE + " text," + KEY_CAR_MODEL + " text," + KEY_CAR_NUMBER + " text,"
			+ KEY_CARMAKE + " text not null,"+ KEY_CARYEAR + " text not null,"
			+ PACKAGE_TYPE + " text not null);";


	private DatabaseHelper DBhelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		DBhelper = new DatabaseHelper(ctx);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_CREATE_LOCATION);
			db.execSQL(TABLE_CREATE_USER);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATE_USER);
			onCreate(db);
		}
	}

	public DBAdapter open() throws SQLiteException {
		db = DBhelper.getWritableDatabase();
		return this;
	}

	public boolean isCreated() {
		if (db != null) {
			return db.isOpen();
		}

		return false;
	}

	public boolean isOpen() {
		return db.isOpen();
	}

	public void close() {
		DBhelper.close();
		db.close();
	}

	public long addLocation(LatLng latLng) {
		ContentValues values = new ContentValues();
		values.put(KEY_LAT, latLng.latitude);
		values.put(KEY_LON, latLng.longitude);

		return db.insert(TABLE_LOCATION, null, values);
	}

	public ArrayList<LatLng> getLocations() {
		ArrayList<LatLng> points = new ArrayList<LatLng>();
		String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;
		try {

			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {

					LatLng latLng = new LatLng(Double.parseDouble(cursor
							.getString(1)), Double.parseDouble(cursor
							.getString(2)));
					points.add(latLng);
				} while (cursor.moveToNext());
			}
			cursor.close();
			return points;
		} catch (Exception e) {
			Log.d("Error in getting users from DB", e.getMessage());
			return null;
		}
	}

	public int deleteAllLocations() {
		return db.delete(TABLE_LOCATION, null, null);
	}

	public boolean isLocationsExists() {
		String selectQuery = "SELECT * from " + TABLE_LOCATION;
		boolean isExists = false;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor != null && cursor.getCount() > 0) {
			isExists = true;
			cursor.close();
		}
		return isExists;
	}

	public long createUser(User user) {
		deleteUser();

		ContentValues values = new ContentValues();
		/*values.put(KEY_USER_ID, user.getUserId());
		values.put(KEY_FIRST_NAME, user.getFirst_name());
		values.put(KEY_LAST_NAME, user.getLast_name());
		values.put(KEY_EMAIL, user.getEmail());
		values.put(KEY_CONTACT, user.getPhone());
		values.put(KEY_ADDRESS, user.getAddress());
		values.put(KEY_ZIP_CODE, user.getZipcode());
		values.put(KEY_BIO, user.getBio());
		values.put(KEY_PICTURE, user.getPicture());
		values.put(KEY_CAR_MODEL, user.getCar_model());
		values.put(KEY_CAR_NUMBER, user.getCar_number());*/



		values.put(KEY_USER_ID,user.getUser_id());
		values.put(KEY_FULL_NAME,user.getFull_name());
		values.put(KEY_FIRST_NAME,user.getFirst_name());
		values.put(KEY_LAST_NAME,user.getLast_name());
		values.put(KEY_CONTACT,user.getPhone());
		values.put(KEY_EMAIL,user.getEmail());

		Log.i("09561",""+user.getPicture());
		values.put(KEY_PICTURE,user.getPicture());
		values.put(KEY_ISAPPROVED,user.getIs_approved());
		values.put(KEY_BIO,user.getBio());
		values.put(KEY_ADDRESS,user.getAddress());
		values.put(KEY_ZIP_CODE,user.getZipcode());
		values.put(KEY_CAR_MODEL,user.getCar_model());
		values.put(KEY_CAR_NUMBER,user.getCar_number());
		values.put(KEY_CARMAKE,user.getCar_make());
		values.put(KEY_CARYEAR,user.getCar_year());
		values.put(KEY_CARYEAR,user.getCar_year());
		values.put(PACKAGE_TYPE,user.getPackage_type());









		return db.insert(USER_TABLE, null, values);
	}

	public User getUser() {
		String selectQuery = "SELECT * from " + USER_TABLE;
		User user = null;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			user = new User();
//			user.setUserId(cursor.getInt(1));
//			user.setFirst_name(cursor.getString(2));
//			user.setLast_name(cursor.getString(3));
//			user.setEmail(cursor.getString(4));
//			user.setPhone(cursor.getString(5));
//			user.setPicture(cursor.getString(6));
//			user.setBio(cursor.getString(7));
//			user.setAddress(cursor.getString(8));
//			user.setZipcode(cursor.getString(9));
//			user.setCar_model(cursor.getString(10));
//			user.setCar_number(cursor.getString(11));








			user.setUser_id(cursor.getString(1));
			user.setFull_name(cursor.getString(2));
			user.setFirst_name(cursor.getString(3));
			user.setLast_name(cursor.getString(4));
			user.setPhone(cursor.getString(5));
			user.setEmail(cursor.getString(6));
			user.setPicture(cursor.getString(7));
			user.setIs_approved(cursor.getString(8));
			user.setBio(cursor.getString(9));
			user.setAddress(cursor.getString(10));
			user.setZipcode(cursor.getString(11));
			user.setCar_model(cursor.getString(12));
			user.setCar_number(cursor.getString(13));
			user.setCar_make(cursor.getString(14));
			user.setCar_year(cursor.getString(15));
			user.setPackage_type(cursor.getString(16));






			cursor.close();
		}

		return user;
	}

	public int deleteUser() {
		return db.delete(USER_TABLE, null, null);
	}

}
