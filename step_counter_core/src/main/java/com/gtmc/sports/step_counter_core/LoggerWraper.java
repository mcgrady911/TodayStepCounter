package com.gtmc.sports.step_counter_core;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static com.gtmc.sports.step_counter_core.StepCounterConstant.LOGGER_TAG;

public class LoggerWraper {

    public synchronized static final void onEventInfo(Context context, String eventID, String label) {
        if (!TextUtils.isEmpty(label)) {
            Log.i(LOGGER_TAG, String.format("%s : %s", eventID, label));
        } else {
            Log.i(LOGGER_TAG, eventID);

        }
    }

    public synchronized static final void onEventInfo(Context context, String eventID) {
        onEventInfo(context, eventID, "");
    }

    public synchronized static final void onEventInfo(Context context, String eventID, Map<String, String> map) {
        onEventInfo(context, eventID, map.toString());
    }

    public synchronized static final void flush() {
//        Logger.flush();
    }

    public synchronized static final void deviceInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("BRAND", Build.BRAND);  //samsung
        map.put("MANUFACTURER", Build.MANUFACTURER);//samsung
        map.put("MODEL", Build.MODEL);//SM-G9500
        map.put("PRODUCT", Build.PRODUCT);//dreamqltezc
        map.put("RELEASE", android.os.Build.VERSION.RELEASE);//8.0.0
        map.put("SDK_INT", String.valueOf(Build.VERSION.SDK_INT));//26
        map.put("APP_Version", StepUtils.getAppVersion(context));
        map.put("APP_Build", StepUtils.getAppVersionCode(context));
        //1. 手机具体型号，设备信息
        //2. 早上打开，晚上打开
        onEventInfo(context, StepCounterConstant.LOGGER_DEVICE_INFO, map);
    }
}
