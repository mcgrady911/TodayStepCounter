package com.yonyou.gtmc.sports_core;

import java.util.List;


interface ITodayStepDBHelper {

    void createTable();

    void deleteTable();

    void clearCapacity(String userId, String curDate, int limit);

    boolean isExist(TodayStepData todayStepData);

    void insert(TodayStepData todayStepData);

    TodayStepData getMaxStepByDate(String userId, long millis);

    List<TodayStepData> getQueryAll(String userId);

    List<TodayStepData> getStepListByDate(String userId, String dateString);

    List<TodayStepData> getStepListByStartDateAndDays(String userId, String startDate, int days);

    void close();
}
