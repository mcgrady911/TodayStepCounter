package com.today.step.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static com.today.step.lib.ConstantDef.HANDLER_WHAT_TEST_LOGGER;
import static com.today.step.lib.ConstantDef.WHAT_TEST_LOGGER_DURATION;

/**
 * Sensor.TYPE_STEP_COUNTER
 * 计步传感器计算当天步数，不需要后台Service
 * Created by jiahongfei on 2017/6/30.
 */

class TodayStepCounter implements ITodayStepCounter, SensorEventListener{

    private static final String TAG = TodayStepCounter.class.getSimpleName();

    private int sOffsetStep = 0;
    private int sCurrStep = 0;
    private String todayDate;
    private boolean cleanStep = true;
    private boolean shutdown = false;
    /**
     * 用来标识对象第一次创建，
     */
    private boolean counterStepReset = true;

    private Context context;
    private OnStepCounterListener onStepCounterListener;

    private boolean separate = false;
    /**
     * 开机启动完成标识
     */
    private boolean boot = false;

    private float loggerSensorStep = 0f;
    private int loggerCounterStep = 0;
    private int loggerCurrentStep = 0;
    private int loggerOffsetStep = 0;
    /**
     * 传感器回调次数
     */
    private long loggerSensorCount = 0;

    private BroadcastReceiver mBatInfoReceiver;

    private final Handler sHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_WHAT_TEST_LOGGER: {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("sCurrStep", String.valueOf(loggerCurrentStep));
                    map.put("counterStep", String.valueOf(loggerCounterStep));
                    map.put("SensorStep", String.valueOf(loggerSensorStep));
                    map.put("sOffsetStep", String.valueOf(loggerOffsetStep));
                    map.put("SensorCount", String.valueOf(loggerSensorCount));
                    //增加电量、息屏状态
                    int battery = getBattery();
                    if (battery != -1) {
                        map.put("battery", String.valueOf(battery));
                    }
                    map.put("isScreenOn", String.valueOf(getScreenState()));
                    Log.e("wcd_map", map.toString());
                    LoggerWraper.onEventInfo(context, LoggerConstant.TYPE_STEP_COUNTER_TIMER, map);
                    sHandler.removeMessages(HANDLER_WHAT_TEST_LOGGER);
                    sHandler.sendEmptyMessageDelayed(HANDLER_WHAT_TEST_LOGGER, WHAT_TEST_LOGGER_DURATION);
                    break;
                }
            }
            return false;
        }
    });

    public TodayStepCounter(Context context, OnStepCounterListener onStepCounterListener, boolean separate, boolean boot) {
        this.context = context;
        this.separate = separate;
        this.boot = boot;
        this.onStepCounterListener = onStepCounterListener;

//        WakeLockUtils.getLock(context);

        sCurrStep = (int) PreferencesHelper.getCurrentStep(this.context);
        cleanStep = PreferencesHelper.getCleanStep(this.context);
        todayDate = PreferencesHelper.getStepToday(this.context);
        sOffsetStep = (int) PreferencesHelper.getStepOffset(this.context);
        shutdown = PreferencesHelper.getShutdown(this.context);
        //开机启动监听到，一定是关机开机了
        boolean isShutdown = shutdownBySystemRunningTime();
        if (this.boot || isShutdown) {
            shutdown = true;
            PreferencesHelper.setShutdown(this.context, shutdown);
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("sCurrStep", String.valueOf(sCurrStep));
        map.put("mCleanStep", String.valueOf(cleanStep));
        map.put("mTodayDate", String.valueOf(todayDate));
        map.put("sOffsetStep", String.valueOf(sOffsetStep));
        map.put("mShutdown", String.valueOf(shutdown));
        map.put("isShutdown", String.valueOf(isShutdown));
        map.put("lastSensorStep", String.valueOf(PreferencesHelper.getLastSensorStep(this.context)));
        LoggerWraper.onEventInfo(this.context, LoggerConstant.TYPE_STEP_CONSTRUCTOR, map);

        dateChangeCleanStep();

        initBroadcastReceiver();

        updateStepCounter();

        //启动JLogger日志打印
        if (TodayStepManager.isDebug()) {
            sHandler.removeMessages(HANDLER_WHAT_TEST_LOGGER);
            sHandler.sendEmptyMessageDelayed(HANDLER_WHAT_TEST_LOGGER, WHAT_TEST_LOGGER_DURATION);
        }
    }

    @Override
    public void initTodayStepCounter() {
        if (mBatInfoReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_DATE_CHANGED);
            context.registerReceiver(mBatInfoReceiver, filter);
        }
    }

    private void initBroadcastReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (Intent.ACTION_TIME_TICK.equals(intent.getAction())
                        || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
                    //service存活做0点分隔
                    dateChangeCleanStep();

                }
            }
        };
        context.registerReceiver(mBatInfoReceiver, filter);
    }

    private void unregisterReceiver() {
        if (mBatInfoReceiver != null) {
            context.unregisterReceiver(mBatInfoReceiver);
        }

        if (TodayStepManager.isDebug()) {
            sHandler.removeMessages(HANDLER_WHAT_TEST_LOGGER);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            int counterStep = (int) event.values[0];

            if (cleanStep) {
                //TODO:只有传感器回调才会记录当前传感器步数，然后对当天步数进行清零，所以步数会少，少的步数等于传感器启动需要的步数，假如传感器需要10步进行启动，那么就少10步
                Map<String, String> map = new HashMap<>();
                map.put("clean_before_sCurrStep", String.valueOf(sCurrStep));
                map.put("clean_before_sOffsetStep", String.valueOf(sOffsetStep));
                map.put("clean_before_mCleanStep", String.valueOf(cleanStep));
                cleanStep(counterStep);
                map.put("clean_after_sCurrStep", String.valueOf(sCurrStep));
                map.put("clean_after_sOffsetStep", String.valueOf(sOffsetStep));
                map.put("clean_after_mCleanStep", String.valueOf(cleanStep));
                LoggerWraper.onEventInfo(context, LoggerConstant.TYPE_STEP_CLEANSTEP, map);
            } else {
                //处理关机启动
                if (shutdown || shutdownByCounterStep(counterStep)) {
                    Map<String, String> map = new HashMap<>();
                    map.put("shutdown_before_mShutdown", String.valueOf(shutdown));
                    map.put("shutdown_before_mCounterStepReset", String.valueOf(counterStepReset));
                    map.put("shutdown_before_sOffsetStep", String.valueOf(sOffsetStep));
                    shutdown(counterStep);
                    map.put("shutdown_after_mShutdown", String.valueOf(shutdown));
                    map.put("shutdown_after_mCounterStepReset", String.valueOf(counterStepReset));
                    map.put("shutdown_after_sOffsetStep", String.valueOf(sOffsetStep));
                    LoggerWraper.onEventInfo(context, LoggerConstant.TYPE_STEP_SHUTDOWN, map);
                }
            }
            sCurrStep = counterStep - sOffsetStep;

            if (sCurrStep < 0) {
                Map<String, String> map = new HashMap<>();
                map.put("tolerance_before_counterStep", String.valueOf(counterStep));
                map.put("tolerance_before_sCurrStep", String.valueOf(sCurrStep));
                map.put("tolerance_before_sOffsetStep", String.valueOf(sOffsetStep));
                //容错处理，无论任何原因步数不能小于0，如果小于0，直接清零
                cleanStep(counterStep);
                map.put("tolerance_after_counterStep", String.valueOf(counterStep));
                map.put("tolerance_after_sCurrStep", String.valueOf(sCurrStep));
                map.put("tolerance_after_sOffsetStep", String.valueOf(sOffsetStep));
                LoggerWraper.onEventInfo(context, LoggerConstant.TYPE_STEP_TOLERANCE, map);
            }

            PreferencesHelper.setCurrentStep(context, sCurrStep);
            PreferencesHelper.setElapsedRealtime(context, SystemClock.elapsedRealtime());
            PreferencesHelper.setLastSensorStep(context, counterStep);

            loggerSensorStep = event.values[0];
            loggerCounterStep = counterStep;
            loggerCurrentStep = sCurrStep;
            loggerOffsetStep = sOffsetStep;
            updateStepCounter();
            if (loggerSensorCount == 0) {
                sHandler.removeMessages(HANDLER_WHAT_TEST_LOGGER);
                sHandler.sendEmptyMessageDelayed(HANDLER_WHAT_TEST_LOGGER, 800);
            }
            //用来判断传感器是否回调
            loggerSensorCount++;

        }
    }

    private void cleanStep(int counterStep) {
        //清除步数，步数归零，优先级最高
        sCurrStep = 0;
        sOffsetStep = counterStep;
        PreferencesHelper.setStepOffset(context, sOffsetStep);

        cleanStep = false;
        PreferencesHelper.setCleanStep(context, cleanStep);
        loggerCurrentStep = sCurrStep;
        loggerOffsetStep = sOffsetStep;
    }

    private void shutdown(int counterStep) {
        int tmpCurrStep = (int) PreferencesHelper.getCurrentStep(context);
        //重新设置offset
        sOffsetStep = counterStep - tmpCurrStep;
        //TODO 只有在当天进行过关机，才会进入到这，直接置反??@老大
//        sOffsetStep = -tmpCurrStep;
        PreferencesHelper.setStepOffset(context, sOffsetStep);

        shutdown = false;
        PreferencesHelper.setShutdown(context, shutdown);
    }

    private boolean shutdownByCounterStep(int counterStep) {
        if (counterStepReset) {
            //只判断一次
            counterStepReset = false;
            if (counterStep < PreferencesHelper.getLastSensorStep(context)) {
                LoggerWraper.onEventInfo(context, LoggerConstant.TYPE_STEP_SHUTDOWNBYCOUNTERSTEP, "当前传感器步数小于上次传感器步数");
                //当前传感器步数小于上次传感器步数肯定是重新启动了，只是用来增加精度不是绝对的
//                Logger.e(TAG, "当前传感器步数小于上次传感器步数肯定是重新启动了，只是用来增加精度不是绝对的");
                return true;
            }
        }
        return false;
    }

    private boolean shutdownBySystemRunningTime() {
        if (PreferencesHelper.getElapsedRealtime(context) > SystemClock.elapsedRealtime()) {
            LoggerWraper.onEventInfo(context, LoggerConstant.TYPE_STEP_SHUTDOWNBYSYSTEMRUNNINGTIME, "本地记录的时间，判断进行了关机操作");
            //上次运行的时间大于当前运行时间判断为重启，只是增加精度，极端情况下连续重启，会判断不出来
//            Logger.e(TAG, "上次运行的时间大于当前运行时间判断为重启，只是增加精度，极端情况下连续重启，会判断不出来");
            return true;
        }
        return false;
    }

    private synchronized void dateChangeCleanStep() {

        //时间改变了清零，或者0点分隔回调
        if (!getTodayDate().equals(todayDate) || separate) {
            HashMap<String, String> map = new HashMap<>();
            map.put("getTodayDate()", String.valueOf(getTodayDate()));
            map.put("mTodayDate", todayDate);
            map.put("mSeparate", String.valueOf(separate));
            LoggerWraper.onEventInfo(context, LoggerConstant.TYPE_STEP_COUNTER_DATECHANGECLEANSTEP, map);
//            WakeLockUtils.getLock(context);

            cleanStep = true;
            PreferencesHelper.setCleanStep(context, cleanStep);

            todayDate = getTodayDate();
            PreferencesHelper.setStepToday(context, todayDate);

            shutdown = false;
            PreferencesHelper.setShutdown(context, shutdown);

            boot = false;

            separate = false;

            sCurrStep = 0;
            PreferencesHelper.setCurrentStep(context, sCurrStep);

            loggerSensorCount = 0;
            loggerCurrentStep = sCurrStep;

            if (null != onStepCounterListener) {
                onStepCounterListener.onStepCounterClean();
            }
        }
    }

    private String getTodayDate() {
        return DateUtils.getCurrentDate("yyyy-MM-dd");
    }

    private void updateStepCounter() {

        //每次回调都判断一下是否跨天
        dateChangeCleanStep();

        if (null != onStepCounterListener) {
            onStepCounterListener.onChangeStepCounter(sCurrStep);
        }
    }

    public int getCurrentStep() {
        sCurrStep = (int) PreferencesHelper.getCurrentStep(context);
        return sCurrStep;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private int getBattery() {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        int battery = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        return battery;
    }

    private boolean getScreenState() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    @Override
    public void stopTodayStepCounter() {
        unregisterReceiver();
        sCurrStep = 0;
        sOffsetStep = sCurrStep;
        PreferencesHelper.clear(context);

    }
}
