package com.kys.statistics.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * Created by busiya on 2018/3/6.
 * * 取设备唯一标识
 * 1.取缓存,若缓存中有该唯一标识,则不获取,直接返回uuid
 * 2.若没有该唯一标识,获取 androidId:  (来自于手机厂商,但是如果返厂的手机，或者被root的手机，可能会变)
 * 若 androidId 为 9774d56d682e549c,是因为某些厂商生产的手机会固定返回这个,没有对androidID做处理
 * 若 androidId 不支持转为 UUID 则进行第4步
 * 否则直接将 androidId 转为 UUID 作为唯一标识符并存储
 * 3.获取 deviceId:   (需要敏感权限 READ_PHONE_STATE)
 * 若 deviceId 不支持转为 UUID 则进行第4步
 * 否则直接返回 deviceId 转为 UUID 作为唯一标识符存储
 * 4.获取 系统硬件信息 os.Build拼接: (API > 9:我们可以通过读取设备的ROM版本号、厂商名、CPU型号和其他硬件信息来组合出一串15位的号码,大约有0.5%重复率)
 * 在 1,2,3 均失败的情况下使用该方法获取
 * 将拼接的字符串转为 UUID
 * 其它说明:
 * 1.macID: 需要权限 ACCESS_WIFI_STATE , android 6.0 以上,统一返回 02-00-00-00-00-00-00 ;重启手机后未开启过wifi,也不会返回该参数
 * 2.IMEI: 需要权限 READ_PHONE_STATE , 只能支持拥有通话功能的设备;返厂，数据擦除的时候不彻底，保留了原来的标识;有些厂家的实现有bug，返回一些不可用的数据
 * 3.CID:  根据多种参数拼接,可能每次都是不同的
 */

public class GetDeviceIdUtils {
    private static final String PREFS_FILE = "device_id.xml";
    private static final String PREFS_DEVICE_ID = "device_id";

    private String uuid;


    public GetDeviceIdUtils(Context context) {

        if (uuid == null) {
            synchronized (GetDeviceIdUtils.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    String buildId = "";
                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = id;

                    } else {
                        @SuppressLint("HardwareIds") final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not available, then fallback on a random number which we store
                        // to a prefs file

                        if (androidId != null && !"9774d56d682e549c".equals(androidId) && !androidId.equals("")) {
                            uuid = androidId;
                        } else {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                            }
                            @SuppressLint("HardwareIds") final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                            if (deviceId == null)
                                getOSId();
                            else
                                uuid = deviceId;
                        }

                        // Write the value out to the prefs file
                        if (uuid != null) {
                            prefs.edit().putString(PREFS_DEVICE_ID, uuid + "").apply();
                        } else
                            prefs.edit().putString(PREFS_DEVICE_ID, buildId).apply();
                    }

                }
            }
        }

    }

    /**
     * 使用硬件信息拼凑出来的15位号码
     */
    private void getOSId() {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //API>=9 使用serial号,适用于非通话设备
        uuid = m_szDevIDShort + serial.hashCode();
    }

    /**
     * Returns a unique UUID for the current android device.  As with all UUIDs, this unique ID is "very highly likely"
     * to be unique across all Android devices.  Much more so than ANDROID_ID is.
     * <p>
     * The UUID is generated by using ANDROID_ID as the base key if appropriate, falling back on
     * TelephonyManager.getDeviceID() if ANDROID_ID is known to be incorrect, and finally falling back
     * on a random UUID that's persisted to SharedPreferences if getDeviceID() does not return a
     * usable value.
     * <p>
     * In some rare circumstances, this ID may change.  In particular, if the device is factory reset a new device ID
     * may be generated.  In addition, if a user upgrades their phone from certain buggy implementations of Android 2.2
     * to a newer, non-buggy version of Android, the device ID may change.  Or, if a user uninstalls your app on
     * a device that has neither a proper Android ID nor a Device ID, this ID may change on reinstallation.
     * <p>
     * Note that if the code falls back on using TelephonyManager.getDeviceId(), the resulting ID will NOT
     * change after a factory reset.  Something to be aware of.
     * <p>
     * Works around a bug in Android 2.2 for many devices when using ANDROID_ID directly.
     *
     * @return a UUID that may be used to uniquely identify your device for most purposes.
     * //     * @see http://code.google.com/p/android/issues/detail?id=10603
     */
    public String getDeviceUuid() {
        return uuid;
    }
}
