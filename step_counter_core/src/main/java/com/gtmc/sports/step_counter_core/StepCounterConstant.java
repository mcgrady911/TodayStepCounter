package com.gtmc.sports.step_counter_core;

public class StepCounterConstant {

    /**
     * 连续步数停留时间，用于连续步数唤起传感器期间停留超过3秒，清空步数
     */
    public static final long CONTINUOUS_STEP_COUNT_STAY_TIME = 3000L;

    /**
     * 传感器唤起步数值
     */
    public static final int EVOKE_SENSOR_STEP_COUNT = 9;

    public static final String LOGGER_TAG = "TodayStepCounterLog";

    /**
     * 打印Logger日志
     */
    public static final int HANDLER_WHAT_TEST_LOGGER = 0;
    /**
     * 打印JLogger日志时间
     */
    public static final int WHAT_TEST_LOGGER_DURATION = 1000*60*5;//5分钟

    /**
     * app热启动
     */
    public static final String LOGGER_APP_HOT_START = "logger_app_hot_start";

    /**
     * 设备信息
     * map.put("BRAND", Build.BRAND);  //samsung
     * map.put("MANUFACTURER" , Build.MANUFACTURER);//samsung
     * map.put("MODEL" ,  Build.MODEL);//SM-G9500
     * map.put("PRODUCT" , Build.PRODUCT);//dreamqltezc
     * map.put("RELEASE" ,  android.os.Build.VERSION.RELEASE);//8.0.0
     * map.put("SDK_INT" ,  String.valueOf(Build.VERSION.SDK_INT));//26
     * map.put("APP_Version" ,  DeviceInfo.getAppVersion(context));
     * map.put("APP_Build" ,  DeviceInfo.getAppVersionCode(context));
     */
    public static final String LOGGER_DEVICE_INFO = "logger_device_info";

    /**
     * Sensor.TYPE_STEP_COUNTER 传感器计步，定时获取传感器监听中的参数
     */
    public static final String LOGGER_TYPE_STEP_COUNTER_TIMER = "logger_type_step_count_timer";
    /**
     * Sensor.TYPE_STEP_COUNTER 传感器计步,监听器构造中一些本地数据的展示
     */
    public static final String LOGGER_TYPE_STEP_CONSTRUCTOR = "logger_type_step_constructor";
    /**
     * Sensor.TYPE_STEP_COUNTER 在onSensorChanged中回调，mCleanStep=true,调用了计步清零操作
     */
    public static final String LOGGER_TYPE_STEP_CLEANSTEP = "logger_type_step_cleans_currStep_and_offsetStep";
    /**
     * Sensor.TYPE_STEP_COUNTER 在onSensorChanged中回调，判断出用户做了关机处理，做偏移量修改
     */
    public static final String LOGGER_TYPE_STEP_SHUTDOWN = "logger_type_step_shutdown";
    /**
     * Sensor.TYPE_STEP_COUNTER 在onSensorChanged中回调，判断出sCurrStep < 0，进行容错处理，调用cleanStep（）
     */
    public static final String LOGGER_TYPE_STEP_TOLERANCE = "logger_type_step_tolerance";
    /**
     * TodayStepService onCreate中，最初的展示到view上的步数
     */
    public static final String LOGGER_SERVICE_INITIALIZE_CURRSTEP = "logger_service_initialize_currStep";
    /**
     * TodayStepService onStartCommand中，主进程复活后，首次连接从未挂掉的服务进程，传递的参数和此时服务进程记录的步数
     */
    public static final String LOGGER_SERVICE_ONSTARTCOMMAND = "logger_service_onStartCommand";
    /**
     * TodayStepService onBind中，提供给主进程的步数
     */
    public static final String LOGGER_SERVICE_ONBIND = "logger_service_onBind";
    /**
     * TodayStepService，主进程复活后，连接未挂掉的服务进程，Sensor.TYPE_STEP_COUNTER已经注册过
     */
    public static final String LOGGER_SERVICE_TYPE_STEP_COUNTER_HADREGISTER = "logger_service_type_step_counter_hadRegister";
    /**
     * TodayStepService，注册Sensor.TYPE_STEP_COUNTER监听
     */
    public static final String LOGGER_SERVICE_TYPE_STEP_COUNTER_REGISTER = "logger_service_type_step_counter_register";
    /**
     * TodayStepService，主进程复活后，连接未挂掉的服务进程，Sensor.TYPE_ACCELEROMETER已经注册过
     */
    public static final String LOGGER_SERVICE_TYPE_ACCELEROMETER_HADREGISTER = "logger_service_type_accelerometer_hadregister";
    /**
     * TodayStepService，注册Sensor.TYPE_ACCELEROMETER监听
     */
    public static final String LOGGER_SERVICE_TYPE_ACCELEROMETER_REGISTER = "logger_service_type_accelerometer_register";
    /**
     * TodayStepService，数据库插入数据
     */
    public static final String LOGGER_SERVICE_INSERT_DB = "logger_service_insert_db";
    /**
     * TodayStepService，清除数据库数据
     */
    public static final String LOGGER_SERVICE_CLEAN_DB = "logger_service_clean_db";
    /**
     * TodayStepService，设置传感器速率
     */
    public static final String LOGGER_SERVICE_SENSORRATE_INVOKE = "logger_service_sensorrate_invoke";
    /**
     * TodayStepFragment，通过onBind与Service连接
     */
    public static final String LOGGER_FRAGMENT_ONSERVICECONNECTED = "logger_fragment_onServiceConnected";
    /**
     * TodayStepFragment，service手动关闭了
     */
    public static final String LOGGER_FRAGMENT_ONSERVICEDISCONNECTED = "logger_fragment_onServiceDisconnected";
    /**
     * TodayStepDetector，Sensor.TYPE_ACCELEROMETER，构造函数中，本地保存的数据
     */
    public static final String LOGGER_TYPE_ACCELEROMETER_CONSTRUCTOR = "logger_type_accelerometer_constructor";
    /**
     * TodayStepDetector，Sensor.TYPE_ACCELEROMETER，零点清零
     */
    public static final String LOGGER_TYPE_ACCELEROMETER_DATECHANGECLEANSTEP = "logger_type_accelerometer_dateChangeCleanStep";
    /**
     * TodayStepDetector，Sensor.TYPE_ACCELEROMETER，传感器计步，定时获取传感器监听中的参数
     */
    public static final String LOGGER_TYPE_ACCELEROMETER_TIMER = "logger_type_accelerometer_timer";
    /**
     * MyOrdersActivity，用户进入保单页面
     */
    public static final String LOGGER_USER_INTO_MYORDERSACTIVITY = "logger_user_into_MyOrdersActivity";
    /**
     * ModuleItemTypeHealthTopWidgetAndRun,用户
     * */
    public static final String LOGGER_USER_INTO_HEALTHFRAGMENT = "logger_user_into_healthFragment";
    /**
     * 用户上传步数-成功
     * */
    public static final String LOGGER_UP_STEP_SUCCESS = "logger_up_step_success";
    /**
     * 用户上传步数-失败
     * */
    public static final String LOGGER_UP_STEP_FAILURE = "logger_up_step_failure";
    /**
     * 触发上传步数事件--点击上传按钮
     * */
    public static final String LOGGER_UP_STEP_BUTTON = "logger_up_step_button";
    /**
     * 运动历史页面
     * */
    public static final String LOGGER_UP_STEP_HISTORY = "logger_up_step_history";
    /**
     * 触发上传步数事件--健康页面下拉刷新
     * */
    public static final String LOGGER_UP_STEP_PULLDOWN= "logger_up_step_pullDown";
    /**
     * 触发上传步数事件--边走边赚模块
     * */
    public static final String LOGGER_UP_STEP_HEALTHSPORTSTEP= "logger_up_step_healthSportStep";
    /**
     * 触发上传步数事件--边走边赚模块--领取积分
     * */
    public static final String LOGGER_UP_STEP_GET_INTEGRAL= "logger_up_step_get_integral";
    /**
     * 触发上传步数事件--健康首页初始化时
     * */
    public static final String LOGGER_UP_STEP_FIRST_INTO_HEALTHFRAGMENT= "logger_up_step_first_into_healthFragment";
    /**
     * 触发上传步数事件--TodayStepFragment,5分钟循环上传
     * */
    public static final String LOGGER_UP_STEP_CIRCULATION_FIVE= "logger_up_step_circulation_five";
    /**
     * 触发上传步数事件--TodayStepFragment,初始化时，上传
     * */
    public static final String LOGGER_UP_STEP_TODAYSTEPFRAGMENT_ONACTIVITYCREATED= "logger_up_step_TodayStepFragment_onActivityCreated";
    /**
     * 触发上传步数事件--TodayStepFragment,event触发
     * */
    public static final String LOGGER_UP_STEP_TODAYSTEPFRAGMENT_EVENT= "logger_up_step_TodayStepFragment_event";
    /**
     * SportAwardActivity --进入续保奖励页面
     * */
    public static final String LOGGER_USER_INTO_SPORTAWARDACTIVITY= "logger_user_into_SportAwardActivity";
    /**
     * Sensor.TYPE_STEP_COUNTER 传感器计步，时间改变了清零，或者0点分隔回调
     * */
    public static final String LOGGER_TYPE_STEP_COUNTER_DATECHANGECLEANSTEP= "logger_type_step_counter_dateChangeCleanStep";
    /**
     * 用户进行了登录操作
     * */
    public static final String LOGGER_USER_LOGIN= "logger_user_login";
    /**
     * Sensor.TYPE_STEP_COUNTER 传感器计步,监听器构造中一些本地数据的展示
     * */
    public static final String LOGGER_TYPE_STEP_SHUTDOWNBYSYSTEMRUNNINGTIME= "logger_type_step_shutdownBySystemRunningTime";
    /**
     * TodayStepService onDestroy
     * */
    public static final String LOGGER_TODAYSTEPSERVICE_ONDESTROY= "logger_TodayStepService_onDestroy";
    /**
     * TodayStepService onUnbind
     * */
    public static final String LOGGER_TODAYSTEPSERVICE_ONUNBIND= "logger_TodayStepService_onUnbind";
    /**
     * Sensor.TYPE_STEP_COUNTER 传感器计步,监听器构造中一些本地数据的展示
     * */
    public static final String LOGGER_TYPE_STEP_SHUTDOWNBYCOUNTERSTEP= "logger_type_step_shutdownByCounterStep";
    /**
     * TodayStepFragment 上传数据库中的运动轨迹
     * */
    public static final String LOGGER_TODAYSTEPFRAGMENT_POSTSPORTSTEPNUM= "logger_TodayStepFragment_postSportStepNum";
}
