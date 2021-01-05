package com.today.step;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.TodayStepManager;
import com.today.step.lib.TodayStepService;

import java.util.Currency;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private static final int REFRESH_STEP_WHAT = 0;

    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 3000;

    private Handler mDelayHandler = new Handler(new TodayStepCounterCall());
    private int mStepSum;

    private ISportStepInterface iSportStepInterface;

    private TextView mStepArrayTextView;

    private TextView timeTextView;

    private Button btnSwitchUser, btnLogout;

    static final String[] userIds = new String[]{"111", "222", "333"};
    private int currentUserIndex = 0;
    private String currentUser;

    private ServiceConnection serviceConnection;
    private boolean bindService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeTextView = (TextView) findViewById(R.id.timeTextView);
//        mStepArrayTextView = (TextView) findViewById(R.id.stepArrayTextView);

        btnSwitchUser = findViewById(R.id.btn_switch_user);
        btnSwitchUser.setOnClickListener(this);
        btnLogout = findViewById(R.id.btn_loginout);
        btnLogout.setOnClickListener(this);

        currentUser = userIds[currentUserIndex];

        Bundle bundle = new Bundle();
        bundle.putString(TodayStepService.INTENT_USER_ID, currentUser);
        //初始化计步模块
        TodayStepManager.startTodayStepService(getApplication(), bundle);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                //Activity和Service通过aidl进行通信
                iSportStepInterface = ISportStepInterface.Stub.asInterface(iBinder);
                try {
                    mStepSum = iSportStepInterface.getCurrentTimeSportStep();
                    updateStepCount();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        //开启计步Service，同时绑定Activity进行aidl通信
        bindService = TodayStepManager.bindService(this, serviceConnection);
        //计时器
        mhandmhandlele.post(timeRunable);

    }

    class TodayStepCounterCall implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_STEP_WHAT: {
                    //每隔500毫秒获取一次计步数据刷新UI
                    if (null != iSportStepInterface) {
                        int step = 0;
                        try {
                            step = iSportStepInterface.getCurrentTimeSportStep();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (mStepSum != step) {
                            mStepSum = step;
                            updateStepCount();
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

                    break;
                }
            }
            return false;
        }
    }

    private void updateStepCount() {
        Log.e(TAG, "currentUser : " + currentUser + "  updateStepCount : " + mStepSum);
        TextView stepTextView = (TextView) findViewById(R.id.stepTextView);
        stepTextView.setText(mStepSum + "步");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.stepArrayButton: {
                //获取所有步数列表
//                if (null != iSportStepInterface) {
//                    try {
//                        String stepArray = iSportStepInterface.getTodaySportStepArray();
//                        mStepArrayTextView.setText(stepArray);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            }
            case R.id.btn_switch_user:

                int userIdIndex = currentUserIndex;
                if (userIdIndex >= userIds.length - 1) {
                    userIdIndex = 0;
                } else {
                    userIdIndex++;
                }

                String userId = userIds[userIdIndex];

                if (!TextUtils.isEmpty(userId) && !userId.equals(currentUser)) {
                    currentUserIndex = userIdIndex;
                    currentUser = userId;
                    Bundle bundle = new Bundle();
                    bundle.putString(TodayStepService.INTENT_USER_ID, currentUser);
                    TodayStepManager.startTodayStepService(getApplication(), bundle);

                    serviceConnection = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                            //Activity和Service通过aidl进行通信
                            iSportStepInterface = ISportStepInterface.Stub.asInterface(iBinder);
                            try {
                                mStepSum = iSportStepInterface.getCurrentTimeSportStep();
                                updateStepCount();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName componentName) {
                        }
                    };

                    //开启计步Service，同时绑定Activity进行aidl通信
                    TodayStepManager.bindService(this, serviceConnection);
                }
                break;
            case R.id.btn_loginout:
                if (iSportStepInterface != null) {
                    try {
                        iSportStepInterface.stopTodayStepCounter();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    iSportStepInterface = null;
                }

                mStepSum = 0;
                if (mDelayHandler != null) {
                    mDelayHandler.removeMessages(REFRESH_STEP_WHAT);
                }

                if (bindService && serviceConnection != null) {
                    TodayStepManager.unbindService(this, serviceConnection);
                    serviceConnection = null;
                    bindService = false;
                }
                TodayStepManager.stopTodayStepService(getApplication());
                break;
            default:
                break;
        }
    }


    /*****************计时器*******************/
    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {

            currentSecond = currentSecond + 1000;
            timeTextView.setText(getFormatHMS(currentSecond));
            if (!isPause) {
                //递归调用本runable对象，实现每隔一秒一次执行任务
                mhandmhandlele.postDelayed(this, 1000);
            }
        }
    };
    //计时器
    private Handler mhandmhandlele = new Handler();
    private boolean isPause = false;//是否暂停
    private long currentSecond = 0;//当前毫秒数
/*****************计时器*******************/

    /**
     * 根据毫秒返回时分秒
     *
     * @param time
     * @return
     */
    public static String getFormatHMS(long time) {
        time = time / 1000;//总秒数
        int s = (int) (time % 60);//秒
        int m = (int) (time / 60);//分
        int h = (int) (time / 3600);//秒
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
