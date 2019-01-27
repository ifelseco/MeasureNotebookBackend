package com.javaman.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportDetailModel {

    private BaseModel baseModel;
    private double sum;
    private int count;
    private int day;
    private int week;
    private int month;
    private int year;

    public ReportDetailModel(BaseModel baseModel, double sum, int count, int day, int week, int month, int year) {
        this.baseModel = baseModel;
        this.sum = sum;
        this.count = count;
        this.day = day;
        this.week = week;
        this.month = month;
        this.year = year;
    }
}
