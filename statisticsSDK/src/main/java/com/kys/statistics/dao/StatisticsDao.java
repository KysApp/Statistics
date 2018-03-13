package com.kys.statistics.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.kys.statistics.SQLiteDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by busiya on 2018/1/9.
 * 对统计模块数据库的操作类
 */

public class StatisticsDao {
    private SQLiteDBHelper sqLiteDBHelper;
    private SQLiteDatabase sqLiteDatabase;

    public StatisticsDao(Context context) {
        if (sqLiteDBHelper == null) {
            sqLiteDBHelper = SQLiteDBHelper.getInstance(context);
//            sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
        }
    }

    /**
     * @param sequenceData 已经base64后的数据，此条数据直接通过接口上报即可，不用再做任何处理
     */
    public synchronized void insertData(String sequenceData, String type) {
        if (!TextUtils.isEmpty(sequenceData)) {
            sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
            sqLiteDatabase.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("sequence_data", sequenceData);
            contentValues.put("sequence_type", type);
            contentValues.put("operate_time", System.currentTimeMillis());
            sqLiteDatabase.insertOrThrow("statistics", null, contentValues);
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        }
    }

    public synchronized void delData(final String tid) {
        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
                int i = sqLiteDatabase.delete("statistics", "sequence_data=?", new String[]{tid});
//                Logs.e("statitics", i + "");
                sqLiteDatabase.close();
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe();
    }

    public synchronized List<Map<String, String>> getAllData() {
        sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
        List<Map<String, String>> data = new ArrayList<>(0);
        Cursor cursor = sqLiteDatabase.query("statistics",
                new String[]{"*"},
                null,
                null,
                null, null, null);
        Map<String, String> map;
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("tid", cursor.getInt(cursor.getColumnIndex("tid")) + "");
            map.put("sequence_data", cursor.getString(cursor.getColumnIndex("sequence_data")));
            map.put("sequence_type", cursor.getString(cursor.getColumnIndex("sequence_type")));
            map.put("operate_time", cursor.getString(cursor.getColumnIndex("operate_time")));
            data.add(map);
        }
        cursor.close();
        sqLiteDatabase.close();
        return data;
    }

    public synchronized Boolean hasData() {
        Boolean has = false;
        sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("statistics",
                new String[]{"*"},
                null,
                null,
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            has = true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return has;
    }
}
