package com.javaman.repository;

import com.javaman.entity.Order;
import com.javaman.entity.Tenant;
import com.javaman.model.ReportDetailModel;
import com.javaman.model.ReportSummaryModel;

import java.util.Date;
import java.util.List;

public interface OrderSummaryReportRepository {

    ReportSummaryModel getMonthOfYearReport(int year , Tenant tenant);
    ReportSummaryModel getWeekOfMonthReport(int year ,int month ,Tenant tenant );
    ReportSummaryModel getLastThreeMonthReport(Tenant tenant);
    ReportSummaryModel getEndOfDayReport(Tenant tenant);
    ReportSummaryModel getLastSevenDayOrder(Tenant tenant);
    ReportSummaryModel getLastThirtyDayOrder(Tenant tenant);
    ReportSummaryModel getAllGiroByDate(Tenant tenant, Date startDate,Date endDate);
}
