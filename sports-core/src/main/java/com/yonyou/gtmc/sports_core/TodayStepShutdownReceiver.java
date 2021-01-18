package com.yonyou.gtmc.sports_core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jiahongfei on 2017/9/27.
 */

public class TodayStepShutdownReceiver extends BroadcastReceiver {

    private static final String TAG = TodayStepShutdownReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            TodayStepPreferencesHelper.setShutdown(context,true);
        }
    }

}
