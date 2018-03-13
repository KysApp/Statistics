package com.kys.statistics.utils;


import com.kys.statistics.config.HttpConfig;
import com.kys.statistics.Logs;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by busiya on 2018/1/9.
 * 统计模块中的网络数据请求工具类
 * 基于okhttp网络框架
 */

public class HttpUtils {

    private static HttpUtils httpUtils;
    private static String HOST = "http://stat.bazirim.tv:9090";//根据初始化统计模块时的配置信息来设置的，这里赋值，只是为了避免不可预计的bug导致没有给host赋上值导致数据不能上报的问题
    private static String ClickAddr = HOST + HttpConfig.ClickDo;
    private static String ExposureAddr = HOST + HttpConfig.ExposureDo;
    private static String CustomerAddr = HOST + HttpConfig.CustomerDo;
    public static Boolean isDebug = true;

    public interface HttpRequestCallBack {
        void onSucceed(Call call, Response response);

        void onFailed(Call call, IOException e);
    }

    public static HttpUtils getInstance() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
            if (isDebug) {
                HOST = HttpConfig.HOST_DEBUG;
            } else {
                HOST = HttpConfig.HOST_RELEASE;
            }
//            Logs.e("host", HOST);
            ClickAddr = HOST + HttpConfig.ClickDo;
            ExposureAddr = HOST + HttpConfig.ExposureDo;
            CustomerAddr = HOST + HttpConfig.CustomerDo;
        }
        return httpUtils;
    }

    public void postData(String id, String data, String type, final HttpRequestCallBack httpRequestCallBack) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("data", data).build();
        String url = ClickAddr;
        Logs.e("url", ClickAddr);
        switch (type) {
            case "click":
                url = ClickAddr;
                break;
            case "exposure":
                url = ExposureAddr;
                break;
            case "customer":
                url = CustomerAddr;
                break;
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .tag(data)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpRequestCallBack.onFailed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                httpRequestCallBack.onSucceed(call, response);
            }
        });
    }
}
