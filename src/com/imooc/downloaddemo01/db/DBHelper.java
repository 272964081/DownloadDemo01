package com.imooc.downloaddemo01.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "download.db";
	public static final int VERSION = 1;
	// 创建数据库的SQL语法
	public static final String SQL_CREATE = "create table if not exists thread_info(_id integer primary key autoincrement,"
			+ "thread_id integer,url text,start integer,stop integer,finished integer)";
	// 删除数据库的语法
	public static final String SQL_DROP = "drop table if exists thread_info";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP);
		db.execSQL(SQL_CREATE);
	}

}
