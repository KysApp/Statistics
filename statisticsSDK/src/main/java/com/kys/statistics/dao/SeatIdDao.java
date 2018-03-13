package com.kys.statistics.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kys.statistics.Logs;
import com.kys.statistics.SQLiteDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by busiya on 2018/1/18.
 * 曝光时长的数据操作类
 */

public class SeatIdDao {
    private static SQLiteDBHelper sqLiteDBHelper;
    //    private static SQLiteDatabase sqLiteDatabase;
    private long nowTime;
    private static List<Map<String, Object>> mListSeatIds;
    private static SeatIdDao seatIdDao;

    public static SeatIdDao getInstance(Context context) {
        if (seatIdDao == null) {
            seatIdDao = new SeatIdDao(context);
        }
        return seatIdDao;
    }

    private SeatIdDao(Context context) {
        if (sqLiteDBHelper == null) {
            sqLiteDBHelper = SQLiteDBHelper.getInstance(context);
            mListSeatIds = new ArrayList<>(0);
        }
    }


//    /**
//     * 处理坑位数据
//     *
//     * @param startPos  可见的第一个item的pos
//     * @param endPos    可见的最后一个item的pos
//     * @param seatIdMap 所有pos对应的坑位id数据（一个item可能包含多个view，每个view拥有一个坑位id）
//     */
//    public void insertExposureTime(int startPos, int endPos, Map<Integer, String[]> seatIdMap, Map<String, Map<String, String>> dataMap) {
//        nowTime = System.currentTimeMillis();
//        String[] visibleSeatIds = {};
//        //将可见的item对应的坑位id拼接起来
//        for (int i = startPos; i <= endPos; i++) {
//            if (seatIdMap.containsKey(i)) {
//                visibleSeatIds = concat(visibleSeatIds, seatIdMap.get(i));
//            }
//        }
//        //判断坑位id是否存在在数据库中
//        if (visibleSeatIds.length > 0) {
//            List<Map<String, Object>> listSeatIds = getSeatIdsBySelection(null, null);
//            if (listSeatIds.size() == 0) {//数据库中没有任何数据，则直接将本次可见的坑位id数据插入数据库中
//                List<ContentValues> contentValuesList = new ArrayList<>(0);
//                for (String seatId : visibleSeatIds) {
//                    Map<String, String> map = dataMap.get(seatId);
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put("seat_id", seatId);
//                    contentValues.put("start_time", nowTime);
//                    contentValues.put("end_time", 0);
//                    contentValues.put("total_time", 0);
//                    contentValues.put("target_id", map.get("targetId"));
//                    contentValues.put("goods_name", map.get("goodsName"));
//                    contentValues.put("goods_price", map.get("goodsPrice"));
//                    contentValues.put("goods_spec", map.get("specification"));
//
//                    contentValuesList.add(contentValues);
//                }
//
//                new InsertTask().execute(contentValuesList);
//            } else {
//                Map<String, Object> map;
//                List<ContentValues> insertContentValuesList = new ArrayList<>(0);
//                List<ContentValues> updateContentValuesList = new ArrayList<>(0);
//                List<String> updateSeatIdList = new ArrayList<>(0);
//
//                for (int i = 0; i < visibleSeatIds.length; i++) {
//                    String seatId = visibleSeatIds[i];
//                    Boolean hasSeatId = false;
//                    for (int j = 0; j < listSeatIds.size(); j++) {
//                        map = listSeatIds.get(j);
//                        try {
//                            if (map.containsValue(seatId)) {
//                                hasSeatId = true;
//                                long startTime = Long.parseLong(map.get("startTime").toString());
//                                if (startTime == 0) {//不存在开始时间，证明此次是曝光开始，则更新此条数据的startTime
//                                    updateSeatIdList.add(seatId);
//                                    ContentValues contentValues = new ContentValues();
//                                    contentValues.put("start_time", nowTime);
//                                    updateContentValuesList.add(contentValues);
//                                }
//                                break;
//                            }
//                        } catch (NullPointerException ignore) {
//                        }
//
//                    }
//                    if (!hasSeatId) {//数据库不存在该坑位的数据，则将此条数据插入表中
//                        Map<String, String> map2 = dataMap.get(seatId);
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put("seat_id", seatId);
//                        contentValues.put("start_time", nowTime);
//                        contentValues.put("end_time", 0);
//                        contentValues.put("total_time", 0);
//                        contentValues.put("target_id", map2.get("targetId"));
//                        contentValues.put("goods_name", map2.get("goodsName"));
//                        contentValues.put("goods_price", map2.get("goodsPrice"));
//                        contentValues.put("goods_spec", map2.get("specification"));
//                        insertContentValuesList.add(contentValues);
//
//                    }
//                }
//                if (insertContentValuesList.size() > 0) {
//                    new InsertTask().execute(insertContentValuesList);
//                }
//                if (updateContentValuesList.size() > 0) {
//                    new UpdateTask().execute(updateContentValuesList, updateSeatIdList);
//                }
//                //上次可见，此次不可见，则计算曝光时长
//                addTotalTime(visibleSeatIds, nowTime);
//            }
//        }
//
//
//    }


    /**
     * 处理坑位数据
     *
     * @param startPos  可见的第一个item的pos
     * @param endPos    可见的最后一个item的pos
     * @param seatIdMap 所有pos对应的坑位id数据（一个item可能包含多个view，每个view拥有一个坑位id）
     */
    public void insertExposureTime(int startPos, int endPos, Map<Integer, String[]> seatIdMap, final Map<String, Map<String, String>> dataMap) {
        nowTime = System.currentTimeMillis();
        String[] visibleSeatIds = {};
        //将可见的item对应的坑位id拼接起来
        for (int i = startPos; i <= endPos; i++) {
            if (seatIdMap.containsKey(i)) {
                visibleSeatIds = concat(visibleSeatIds, seatIdMap.get(i));
            }
        }
        //判断坑位id是否存在在数据库中
        if (visibleSeatIds.length > 0) {

            //创建Observable
            final String[] finalVisibleSeatIds = visibleSeatIds;
            Observable observable = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                    Log.e("sub", "start emitter data");
                    List<Map<String, Object>> listSeatIds = getSeatIdsBySelection(null, null);
                    if (listSeatIds.size() == 0) {//数据库中没有任何数据，则直接将本次可见的坑位id数据插入数据库中
                        List<ContentValues> contentValuesList = new ArrayList<>(0);
                        for (String seatId : finalVisibleSeatIds) {
                            Map<String, String> map = dataMap.get(seatId);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("seat_id", seatId);
                            contentValues.put("start_time", nowTime);
                            contentValues.put("end_time", 0);
                            contentValues.put("total_time", 0);
                            contentValues.put("target_id", map.get("targetId"));
                            contentValues.put("goods_name", map.get("goodsName"));
                            contentValues.put("goods_price", map.get("goodsPrice"));
                            contentValues.put("goods_spec", map.get("specification"));

                            contentValuesList.add(contentValues);
                        }

                        insertData(contentValuesList);

//                        new InsertTask().execute(contentValuesList);
                    } else {
                        Map<String, Object> map;
                        List<ContentValues> insertContentValuesList = new ArrayList<>(0);
                        List<ContentValues> updateContentValuesList = new ArrayList<>(0);
                        List<String> updateSeatIdList = new ArrayList<>(0);

                        for (int i = 0; i < finalVisibleSeatIds.length; i++) {
                            String seatId = finalVisibleSeatIds[i];
                            Boolean hasSeatId = false;
                            for (int j = 0; j < listSeatIds.size(); j++) {
                                map = listSeatIds.get(j);
                                try {
                                    if (map.containsValue(seatId)) {
                                        hasSeatId = true;
                                        long startTime = Long.parseLong(map.get("startTime").toString());
                                        if (startTime == 0) {//不存在开始时间，证明此次是曝光开始，则更新此条数据的startTime
                                            updateSeatIdList.add(seatId);
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put("start_time", nowTime);
                                            updateContentValuesList.add(contentValues);
                                        }
                                        break;
                                    }
                                } catch (NullPointerException ignore) {
                                }

                            }
                            if (!hasSeatId) {//数据库不存在该坑位的数据，则将此条数据插入表中
                                Map<String, String> map2 = dataMap.get(seatId);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("seat_id", seatId);
                                contentValues.put("start_time", nowTime);
                                contentValues.put("end_time", 0);
                                contentValues.put("total_time", 0);
                                contentValues.put("target_id", map2.get("targetId"));
                                contentValues.put("goods_name", map2.get("goodsName"));
                                contentValues.put("goods_price", map2.get("goodsPrice"));
                                contentValues.put("goods_spec", map2.get("specification"));
                                insertContentValuesList.add(contentValues);

                            }
                        }
                        if (insertContentValuesList.size() > 0) {
                            insertData(insertContentValuesList);
//                            new InsertTask().execute(insertContentValuesList);
                        }
                        if (updateContentValuesList.size() > 0) {
                            updateData(updateContentValuesList, updateSeatIdList);
//                            new UpdateTask().execute(updateContentValuesList, updateSeatIdList);
                        }
                        //上次可见，此次不可见，则计算曝光时长
                        addTotalTime(finalVisibleSeatIds, nowTime);
                    }
                    e.onComplete();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            // 订阅方式一：下游消费者 Observer
            observable.subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    // onSubscribe 是2.x新添加的方法，在发射数据前被调用，相当于1.x的onStart方法
                    Log.e("sub", "onSubscribe");
                }

                @Override
                public void onNext(@NonNull String s) {
                    Log.e("sub", "onNext");
                    Log.e("sub", s);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Log.e("sub", "onError");
                }

                @Override
                public void onComplete() {
                    Log.e("sub", "onComplete");
                }
            });


        }


    }

//    /**
//     * 找出上次可见，此次不可见的坑位数据（数据库中存在startTime，且不在visibleSeatIds中的数据）
//     *
//     * @param values  此次可见的坑位id
//     * @param nowTime 当前时间
//     */
//    private synchronized void addTotalTime(String[] values, long nowTime) {
//        String select = "";
//        for (int i = 0; i < values.length; i++) {
//            select += "seat_id<>? and ";
//        }
//        new AddTotalTimeTask().execute(select.substring(0, select.length() - 4), values);
//    }

    /**
     * 找出上次可见，此次不可见的坑位数据（数据库中存在startTime，且不在visibleSeatIds中的数据）
     *
     * @param values  此次可见的坑位id
     * @param nowTime 当前时间
     */
    private synchronized void addTotalTime(String[] values, long nowTime) {
        String select = "";
        for (int i = 0; i < values.length; i++) {
            select += "seat_id<>? and ";
        }
        List<Map<String, Object>> data;
        data = getSeatIdsBySelection(select.substring(0, select.length() - 4), values);
        List<ContentValues> contentValuesList = new ArrayList<>(0);
        List<String> seatIdList = new ArrayList<>(0);
        for (Map<String, Object> sqlMap : data) {
            long startTime = Long.parseLong(sqlMap.get("startTime").toString());
            if (startTime > 0) {
                long totalTime = nowTime - startTime + Long.parseLong(sqlMap.get("totalTime").toString());
//                MyLogs.w("time", "nowtime=" + nowTime + "starttime=" + startTime);
                ContentValues contentValues = new ContentValues();
                contentValues.put("start_time", 0);
                contentValues.put("total_time", totalTime);
                contentValuesList.add(contentValues);
                seatIdList.add(sqlMap.get("seatId").toString());
            }
        }
        updateData(contentValuesList, seatIdList);
//        new UpdateTask().execute(contentValuesList, seatIdList);
//        new AddTotalTimeTask().execute(select.substring(0, select.length() - 4), values);
    }

    public static synchronized List<Map<String, Object>> getSeatIdsBySelection(String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getReadableDatabase();
        mListSeatIds.clear();
        Cursor cursor = sqLiteDatabase.query("seatIds", new String[]{"*"}, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            Map<String, Object> map;
            while (cursor.moveToNext()) {
                map = new HashMap<>();
                map.put("seatId", cursor.getString(cursor.getColumnIndex("seat_id")));
                map.put("startTime", cursor.getString(cursor.getColumnIndex("start_time")));
                map.put("endTime", cursor.getString(cursor.getColumnIndex("end_time")));
                map.put("totalTime", cursor.getInt(cursor.getColumnIndex("total_time")));
                map.put("goodsName", cursor.getString(cursor.getColumnIndex("goods_name")));
                map.put("goodsPrice", cursor.getString(cursor.getColumnIndex("goods_price")));
                map.put("goodsSpec", cursor.getString(cursor.getColumnIndex("goods_spec")));
                map.put("targetId", cursor.getString(cursor.getColumnIndex("target_id")));
                mListSeatIds.add(map);
            }
            cursor.close();

        }
        return mListSeatIds;
    }

    private static synchronized void updateData(List<ContentValues> contentValuesList, List<String> seatIds) {
        SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        for (int i = 0; i < contentValuesList.size(); i++) {
            ContentValues contentValues = contentValuesList.get(i);
            sqLiteDatabase.update("seatIds", contentValues, "seat_id=?", new String[]{seatIds.get(i)});
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }


    public static synchronized void insertData(List<ContentValues> contentValuesList) {
        SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        for (int i = 0; i < contentValuesList.size(); i++) {
            sqLiteDatabase.insert("seatIds", null, contentValuesList.get(i));
        }

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    public static synchronized void delData() {

        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
//                sqLiteDatabase.beginTransaction();
                int i = sqLiteDatabase.delete("seatIds", null, null);
                Logs.e("sql", i + "");
//                String SQL_DELETE = "DROP TABLE IF EXISTS seatIds";
//                sqLiteDatabase.execSQL(SQL_DELETE);
//                try {
//                    sqLiteDatabase.execSQL("CREATE TABLE " + "seatIds" + "( "
//                            + "tid" + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
//                            + "seat_id" + " TEXT UNIQUE NOT  NULL ,"
//                            + "target_id" + " TEXT ,"
//                            + "goods_name" + " TEXT ,"
//                            + "goods_price" + " TEXT ,"
//                            + "goods_spec" + " TEXT ,"
//                            + "start_time" + " LONG ,"
//                            + "end_time" + " LONG  ,"
//                            + "total_time" + " LONG ) ");
//                    sqLiteDatabase.setTransactionSuccessful();
//                } finally {
//                    sqLiteDatabase.endTransaction();
//                }

                sqLiteDatabase.close();
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe();


    }


    private class InsertTask extends AsyncTask<List<ContentValues>, Integer, String> {


        @Override
        protected String doInBackground(List<ContentValues>... params) {
            insertData(params[0]);
            return null;
        }
    }

    private class UpdateTask extends AsyncTask<Object, Integer, String> {

        @Override
        protected String doInBackground(Object... params) {
            updateData((List<ContentValues>) params[0], (List<String>) params[1]);
            return null;
        }

    }


    private class AddTotalTimeTask extends AsyncTask<Object, Integer, List<Map<String, Object>>> {
        @Override
        protected List<Map<String, Object>> doInBackground(Object... params) {
            List<Map<String, Object>> data;
            data = getSeatIdsBySelection(params[0].toString(), (String[]) params[1]);
            return data;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> list) {
            super.onPostExecute(list);
            List<ContentValues> contentValuesList = new ArrayList<>(0);
            List<String> seatIdList = new ArrayList<>(0);
            for (Map<String, Object> sqlMap : list) {
                long startTime = Long.parseLong(sqlMap.get("startTime").toString());
                if (startTime > 0) {
                    long totalTime = nowTime - startTime + Long.parseLong(sqlMap.get("totalTime").toString());
//                    MyLogs.w("time", "nowtime=" + nowTime + "starttime=" + startTime);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("start_time", 0);
                    contentValues.put("total_time", totalTime);
                    contentValuesList.add(contentValues);
                    seatIdList.add(sqlMap.get("seatId").toString());
                }
            }
            new UpdateTask().execute(contentValuesList, seatIdList);
        }
    }


    private String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
