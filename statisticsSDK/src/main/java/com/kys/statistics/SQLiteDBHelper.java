package com.kys.statistics;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by busiya on 2018/1/9.
 * 统计模块所使用的数据库，用来存储需要上报的数据
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "statistics_db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME1 = "statistics";
    private static final String TABLE_NAME2 = "seatIds";
    private static SQLiteDBHelper sqLiteDBHelper;

    public static SQLiteDBHelper getInstance(Context context) {
        if (sqLiteDBHelper == null) {
            sqLiteDBHelper = new SQLiteDBHelper(context);
        }
        return sqLiteDBHelper;
    }

    private SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    private SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE " + TABLE_NAME1 + "( "
                    + "tid" + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + "sequence_data" + " TEXT  NOT NULL ,"
                    + "sequence_type" + " TEXT  NOT NULL ,"
                    + "operate_time" + " DATETIME ) ");
            db.execSQL("CREATE TABLE " + TABLE_NAME2 + "( "
                    + "tid" + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + "seat_id" + " TEXT UNIQUE NOT  NULL ,"
                    + "target_id" + " TEXT ,"
                    + "goods_name" + " TEXT ,"
                    + "goods_price" + " TEXT ,"
                    + "goods_spec" + " TEXT ,"
                    + "start_time" + " LONG ,"
                    + "end_time" + " LONG  ,"
                    + "total_time" + " LONG ) ");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
