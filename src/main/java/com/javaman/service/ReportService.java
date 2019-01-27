package com.javaman.service;

import com.javaman.entity.Order;
import com.javaman.entity.Tenant;
import com.javaman.model.ReportDetailModel;
import com.javaman.model.ReportSummaryModel;

import java.util.Date;
import java.util.List;

public interface ReportService {

    //General report
    ReportSummaryModel getMonthOfYearReport(int year, Tenant tenant );
    ReportSummaryModel getWeekOfMonthReport(int year,int month,Tenant tenant);
    ReportSummaryModel getLastThreeMonthReport(Tenant tenant);
    ReportSummaryModel getLastSevenDayOrder(Tenant tenant);
    ReportSummaryModel getEndOfDayReport(Tenant tenant);
    ReportSummaryModel getLastThirtyDayOrder(Tenant tenant);
    ReportSummaryModel getAllGiroByDate(Tenant tenant,Date startDate,Date endDate);


    //Dashboard order
    List<Order> getNextThreeDayDeliveryOrder(Tenant tenant);
    List<Order> getNextThreeDayMeasureOrder(Tenant tenant);
    List<Order> getEndOfDayOrder(Tenant tenant);
    List<Order> getAllRemainingAmountOrder(Tenant tenant);
    List<Order> getAllRemainingAmountOrderByDate(Tenant tenant, Date limitDate);


}
