package com.kys.statistics.utils;

import android.content.Context;
import android.util.Base64;

import com.kys.statistics.config.BaseConfig;
import com.kys.statistics.protobuf.Probe;
import com.kys.statistics.dao.SeatIdDao;

import java.util.List;
import java.util.Map;

/**
 * Created by busiya on 2018/1/8.
 * 统计数据API类
 * 在这里添加对外开放的接口方法
 * 第一版本（2018.1.31日）
 * 1、点击事件的统计
 * 2、展示位曝光时长的统计
 */

public class StatisticsUtils {
    private static StatisticsUtils statisticsUtils;
    private static Probe.PBSequence.Builder pbSequenceBuilder;
    private static Probe.PBExposureSequence.Builder pbExposureNodeBuilder;

    public static StatisticsUtils getInstance() {
        if (statisticsUtils == null) {
            statisticsUtils = new StatisticsUtils();
            pbSequenceBuilder = Probe.PBSequence.newBuilder();
            pbExposureNodeBuilder = Probe.PBExposureSequence.newBuilder();
        }
        return statisticsUtils;
    }

    public void addSequenceData(Probe.PBSequenceNode pbSequenceNode) {
        pbSequenceBuilder.addNodeList(pbSequenceNode);
    }

    public String getSequenceData() {
        byte[] byte_data = Base64.encode(pbSequenceBuilder.build().toByteArray(), Base64.DEFAULT);
        return new String(byte_data);
    }

    public void addExposureData(Context context) {
        pbExposureNodeBuilder.clear();
        List<Map<String, Object>> seatIdsList = SeatIdDao.getInstance(context).getSeatIdsBySelection("total_time>?", new String[]{"0"});
        for (Map<String, Object> map : seatIdsList) {
            Probe.PBSequenceData.Builder pbSequenceDataBuilder = Probe.PBSequenceData.newBuilder();
            pbSequenceDataBuilder.setDataType(Probe.PBSequenceDataType.DATA_EXPOSURETIME);
            pbSequenceDataBuilder.setSeatId(map.get("seatId").toString());
            pbSequenceDataBuilder.setGoodsName(map.get("goodsName").toString());
            pbSequenceDataBuilder.setGoodsPrice(Double.parseDouble(map.get("goodsPrice").toString()));
            pbSequenceDataBuilder.setTargetId(map.get("targetId").toString());
            pbSequenceDataBuilder.setSpecification(map.get("goodsSpec").toString());
            pbSequenceDataBuilder.setExposureTime(Long.parseLong(map.get("totalTime").toString()) / 1000);
            Probe.PBSequenceData pbSequenceData = pbSequenceDataBuilder.build();
            pbExposureNodeBuilder.addDataList(pbSequenceData);

        }
        if (!pbExposureNodeBuilder.toString().equals("")) {
            setCommonData();
        }


    }

    /**
     * 设置公共数据
     */
    private void setCommonData() {
        pbExposureNodeBuilder.setAppChannel(BaseConfig.APP_CHANNEL);
        pbExposureNodeBuilder.setAppKey(BaseConfig.APP_KEY);
        pbExposureNodeBuilder.setAppVersion(BaseConfig.APP_VERSION);
        pbExposureNodeBuilder.setDeviceId(BaseConfig.DEVICE_ID);
        pbExposureNodeBuilder.setDevice(BaseConfig.DEVICE);
        pbExposureNodeBuilder.setNetworkType(BaseConfig.NETWORK_TYPE);
        pbExposureNodeBuilder.setSystemType(BaseConfig.SYSTEM_TYPE);
        pbExposureNodeBuilder.setSystemVersion(BaseConfig.SYSTEM_VERSION);
        pbExposureNodeBuilder.setUserIsLogin(BaseConfig.USER_IS_LOGIN);
        pbExposureNodeBuilder.setUserId(BaseConfig.USER_ID);

    }

    public String getExposureData(Context context) {
        addExposureData(context);
        byte[] byte_data = Base64.encode(pbExposureNodeBuilder.build().toByteArray(), Base64.DEFAULT);
        return new String(byte_data);
    }


}
