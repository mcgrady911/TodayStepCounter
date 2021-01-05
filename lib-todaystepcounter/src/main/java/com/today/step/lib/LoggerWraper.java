package com.today.step.lib;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class LoggerWraper {

    private static final String TAG = "StepCounterLog";

    public static void initXLog(Application application, String xlogPath) {
        String fileName = Utils.getProcessName(application);
        try {
            fileName = fileName.replace(":", "_");
        } catch (Exception e) {
            e.printStackTrace();
            fileName = "xlog";
        }

//        JLoggerConfig jLoggerConfig = new JLoggerConfig.XlogBuilder(
//                application,
//                fileName,
//                xlogPath
//        )
//                .builder();
//        JLogger.init(jLoggerConfig);
    }

    public synchronized static void onEventInfo(Context context, String eventID, String label) {
        if (!TextUtils.isEmpty(label)) {
            Log.i(TAG, String.format("%s : %s", eventID, label));
        } else {
            Log.i(TAG, eventID);

        }
    }

    public synchronized static void onEventInfo(Context context, String eventID) {
        onEventInfo(context, eventID, "");
    }

    public synchronized static void onEventInfo(Context context, String eventID, Map<String, String> map) {
        onEventInfo(context, eventID, map.toString());
    }

    public synchronized static void flush() {
//        JLogger.flush();
    }

    public synchronized static void deviceInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("BRAND", Build.BRAND);  //samsung
        map.put("MANUFACTURER", Build.MANUFACTURER);//samsung
        map.put("MODEL", Build.MODEL);//SM-G9500
        map.put("PRODUCT", Build.PRODUCT);//dreamqltezc
        map.put("RELEASE", android.os.Build.VERSION.RELEASE);//8.0.0
        map.put("SDK_INT", String.valueOf(Build.VERSION.SDK_INT));//26
        map.put("APP_Version", Utils.getAppVersion(context));
        map.put("APP_Build", Utils.getAppVersionCode(context));
        //1. 手机具体型号，设备信息
        //2. 早上打开，晚上打开
        onEventInfo(context, LoggerConstant.DEVICE_INFO, map);
    }
}
