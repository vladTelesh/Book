package com.effectivesoft.bookservice.core.model;

public class AnnualUserStats {
    private Integer year;
    private Integer count;

    public AnnualUserStats() {
    }

    public AnnualUserStats(Integer year, Integer count) {
        this.year = year;
        this.count = count;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}