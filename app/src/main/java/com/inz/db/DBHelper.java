package com.inz.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//创建表
		String sqlUserInfo = "create table userinfo(id integer primary key autoincrement,"
				+ "num integer,"
				+ "username varchar(80),"
				+ "useremail varchar(80),"
				+ "userpassword varchar(20),"
				+ "custom integer,"
				+ "ip varchar(20),"
				+ "port integer,"
				+ "ssl integer);";
				
		db.execSQL(sqlUserInfo);


		String sqlAPCam = "create table apcam(id integer primary key autoincrement,"
				+"username varchar(80),"
				+"devicename varchar(80),"
				+"ip varchar(30),"
				+"port integer);";
		db.execSQL(sqlAPCam);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
