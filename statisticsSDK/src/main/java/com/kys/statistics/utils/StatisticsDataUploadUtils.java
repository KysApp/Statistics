package com.kys.statistics.utils;

import android.content.Context;

import com.kys.statistics.Logs;
import com.kys.statistics.dao.StatisticsDao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by busiya on 2018/1/15.
 * 统计数据上报辅助类
 * 上报逻辑：
 * 下次启动app时，上报上次的数据
 * 该数据存储在数据库中
 */

public class StatisticsDataUploadUtils {
    private static StatisticsDao statisticsDao;

    public static void uploadData(Context context) {
        statisticsDao = new StatisticsDao(context);
        if (statisticsDao.hasData()) {
            List<Map<String, String>> dataList = statisticsDao.getAllData();
            for (int i = 0; i < dataList.size(); i++)
                HttpUtils.getInstance().postData(dataList.get(i).get("tid"),
                        dataList.get(i).get("sequence_data"),
                        dataList.get(i).get("sequence_type"),
                        new HttpUtils.HttpRequestCallBack() {
                            @Override
                            public void onSucceed(Call call, Response response) {
                                String mark = call.request().tag().toString();
                                Logs.e("mark", mark);
                                statisticsDao.delData(mark);
                            }

                            @Override
                            public void onFailed(Call call, IOException e) {

                            }
                        });
        }
    }
}
