package com.effectivesoft.bookservice.common.dto;

public class MonthlyUserStatsDto {
    private int month;
    private int count;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
