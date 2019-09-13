package com.effectivesoft.bookservice.core.model;

public class MonthlyUserStats {
    private Integer month;
    private Integer count;

    public MonthlyUserStats() {
    }

    public MonthlyUserStats(Integer month, Integer count) {
        this.month = month;
        this.count = count;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
