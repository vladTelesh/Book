package com.effectivesoft.bookservice.rest.filter;

public class ThreadLocalData {
    private static ThreadLocal<UserLocalData> data;

    public static ThreadLocal<UserLocalData> getData() {
        return data;
    }

    public static void setData(ThreadLocal<UserLocalData> data) {
        ThreadLocalData.data = data;
    }
}