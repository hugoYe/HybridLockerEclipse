package com.cooee.statistics.databaseNew;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperNew extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "statisticsNew.db";
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_TABLE = "configtable";

	DatabaseHelperNew(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ConfigDBNew.getCreateSql());
		db.execSQL(JournalDBNew.getCreateSql());
		db.execSQL("INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES"
				+ "('ErrorTime','YYYYMMDDHHMMSS');");
		db.execSQL("INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES"
				+ "('ErrorCount','0');");
		db.execSQL("INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES"
				+ "('SuccessTime','YYYYMMDDHHMMSS');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(ConfigDBNew.getDropSql());
		db.execSQL(ConfigDBNew.getCreateSql());
		db.execSQL(JournalDBNew.getDropSql());
		db.execSQL(JournalDBNew.getCreateSql());
		db.execSQL("INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES"
				+ "('ErrorTime','YYYYMMDDHHMMSS');");
		db.execSQL("INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES"
				+ "('ErrorCount','0');");
		db.execSQL("INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES"
				+ "('SuccessTime','YYYYMMDDHHMMSS');");
	}
}
