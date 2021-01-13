package com.gtmc.sports.step_counter_core;


import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class StepUtils {

    /**
     * 是否上传步数，23:55:50~00:05:50分无法上传步数
     *
     * @return true可以上传，false不能上传
     */
    public static boolean isUploadStep() {

        Date curDate = new Date(System.currentTimeMillis());

        long mills2355 = getDateMillis(getCurrentDate("yyyy-MM-dd") + " 23:55:50", "yyyy-MM-dd HH:mm:ss");
        Date date2355 = new Date(mills2355);

        if (curDate.after(date2355)) {
            return false;
        }

        long mills0005 = getDateMillis(getCurrentDate("yyyy-MM-dd") + " 00:05:50", "yyyy-MM-dd HH:mm:ss");
        Date date0005 = new Date(mills0005);

        if (curDate.before(date0005)) {
            return false;
        }

        return true;
    }

    /**
     * 是否先上传步数在跳转，23:59:00~00:01:00分直接跳转不上传步数
     *
     * @return true上传后跳转，false直接跳转
     */
    public static boolean isUploadStepGoto() {

        Date curDate = new Date(System.currentTimeMillis());

        long mills2355 = getDateMillis(getCurrentDate("yyyy-MM-dd") + " 23:59:00", "yyyy-MM-dd HH:mm:ss");
        Date date2355 = new Date(mills2355);

        if (curDate.after(date2355)) {
            return false;
        }

        long mills0005 = getDateMillis(getCurrentDate("yyyy-MM-dd") + " 00:01:00", "yyyy-MM-dd HH:mm:ss");
        Date date0005 = new Date(mills0005);

        if (curDate.before(date0005)) {
            return false;
        }

        return true;
    }

    /**
     * 23:30:00~00:05:00分隐藏tips
     *
     * @return true上传后跳转，false直接跳转
     */
    public static boolean isHealthTipsHide() {

        Date curDate = new Date(System.currentTimeMillis());

        long mills2355 = getDateMillis(getCurrentDate("yyyy-MM-dd") + " 23:30:00", "yyyy-MM-dd HH:mm:ss");
        Date date2355 = new Date(mills2355);

        if (curDate.after(date2355)) {
            return false;
        }

        long mills0005 = getDateMillis(getCurrentDate("yyyy-MM-dd") + " 00:05:00", "yyyy-MM-dd HH:mm:ss");
        Date date0005 = new Date(mills0005);

        if (curDate.before(date0005)) {
            return false;
        }

        return true;
    }

    public static String getProcessName(Application application) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) application.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }

        Log.d("StepCounterUtils", "application start,processName:" + processName);
        return processName;
    }

    /**
     * @brief 获取versionName
     *
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    /**
     * @brief 获取versionCode
     *
     * @param context
     * @return
     */
    public static String getAppVersionCode(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionCode + "";
        return version;
    }


    private static ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT = new ThreadLocal<>();

    public static SimpleDateFormat getDateFormat() {
        SimpleDateFormat df = SIMPLE_DATE_FORMAT.get();
        if (df == null) {
            df = new SimpleDateFormat();
            SIMPLE_DATE_FORMAT.set(df);
        }
        return df;
    }

    /**
     * 返回一定格式的当前时间
     *
     * @param pattern "yyyy-MM-dd HH:mm:ss E"
     * @return
     */
    public static String getCurrentDate(String pattern) {
        getDateFormat().applyPattern(pattern);
        Date date = new Date(System.currentTimeMillis());
        String dateString = getDateFormat().format(date);
        return dateString;

    }

    public static long getDateMillis(String dateString, String pattern) {
        long millionSeconds = 0;
        getDateFormat().applyPattern(pattern);
        try {
            millionSeconds = getDateFormat().parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }// 毫秒

        return millionSeconds;
    }

    /**
     * 格式化输入的millis
     *
     * @param millis
     * @param pattern yyyy-MM-dd HH:mm:ss E
     * @return
     */
    public static String dateFormat(long millis, String pattern) {
        getDateFormat().applyPattern(pattern);
        Date date = new Date(millis);
        String dateString = getDateFormat().format(date);
        return dateString;
    }

    /**
     * 将dateString原来old格式转换成new格式
     *
     * @param dateString
     * @param oldPattern yyyy-MM-dd HH:mm:ss E
     * @param newPattern
     * @return oldPattern和dateString形式不一样直接返回dateString
     */
    public static String dateFormat(String dateString, String oldPattern,
                                    String newPattern) {
        long millis = getDateMillis(dateString, oldPattern);
        if (0 == millis) {
            return dateString;
        }
        String date = dateFormat(millis, newPattern);
        return date;
    }

    public static final String SPORT_DATE = "sportDate";
    public static final String STEP_NUM = "stepNum";
    public static final String DISTANCE = "km";
    public static final String CALORIE = "kaluli";
    public static final String TODAY = TodayStepDBHelper.TODAY;

    static JSONArray getSportStepJsonArray(List<TodayStepData> todayStepDataArrayList) {
        JSONArray jsonArray = new JSONArray();
        if (null == todayStepDataArrayList || 0 == todayStepDataArrayList.size()) {
            return jsonArray;
        }
        for (int i = 0; i < todayStepDataArrayList.size(); i++) {
            TodayStepData todayStepData = todayStepDataArrayList.get(i);
            try {
                JSONObject subObject = getJSONObject(todayStepData);
                jsonArray.put(subObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    static JSONObject getJSONObject(TodayStepData todayStepData) throws JSONException{
        JSONObject subObject = new JSONObject();
        subObject.put(TODAY, todayStepData.getToday());
        subObject.put(SPORT_DATE, todayStepData.getDate()/1000);
        subObject.put(STEP_NUM, todayStepData.getStep());
        subObject.put(DISTANCE, getDistanceByStep(todayStepData.getStep()));
        subObject.put(CALORIE, getCalorieByStep(todayStepData.getStep()));
        return subObject;
    }

    // 公里计算公式
    static String getDistanceByStep(long steps) {
        return String.format("%.2f", steps * 0.6f / 1000);
    }

    // 千卡路里计算公式
    static String getCalorieByStep(long steps) {
        return String.format("%.1f", steps * 0.6f * 60 * 1.036f / 1000);
    }
}
