package com.javaman.service.impl;

import com.javaman.entity.Order;
import com.javaman.entity.Tenant;
import com.javaman.model.ReportDetailModel;
import com.javaman.model.ReportSummaryModel;
import com.javaman.repository.OrderDetailReportRepository;
import com.javaman.repository.OrderRepository;
import com.javaman.repository.OrderSummaryReportRepository;
import com.javaman.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService{

    private OrderSummaryReportRepository orderSummaryReportRepository;
    private OrderDetailReportRepository orderDetailReportRepository;
    private OrderRepository orderRepository;

    @Autowired
    public ReportServiceImpl(OrderSummaryReportRepository orderSummaryReportRepository,
                             OrderRepository orderRepository,
                             OrderDetailReportRepository orderDetailReportRepository) {
        this.orderSummaryReportRepository = orderSummaryReportRepository;
        this.orderDetailReportRepository=orderDetailReportRepository;
        this.orderRepository=orderRepository;
    }

    @Override
    public ReportSummaryModel getMonthOfYearReport(int year, Tenant tenant) {
        return orderSummaryReportRepository.getMonthOfYearReport(year,tenant);
    }

    @Override
    public ReportSummaryModel getWeekOfMonthReport(int year , int month, Tenant tenant) {
        return orderSummaryReportRepository.getWeekOfMonthReport(year,month,tenant);
    }

    @Override
    public ReportSummaryModel getLastSevenDayOrder(Tenant tenant) {
        return orderSummaryReportRepository.getLastSevenDayOrder(tenant);
    }

    @Override
    public ReportSummaryModel getLastThreeMonthReport(Tenant tenant) {
        return orderSummaryReportRepository.getLastThreeMonthReport(tenant);
    }

    @Override
    public ReportSummaryModel getLastThirtyDayOrder(Tenant tenant) {
        return orderSummaryReportRepository.getLastThirtyDayOrder(tenant);
    }



    @Override
    public ReportSummaryModel getEndOfDayReport(Tenant tenant) {
        return orderSummaryReportRepository.getEndOfDayReport(tenant);
    }

    @Override
    public List<Order> getNextThreeDayDeliveryOrder(Tenant tenant) {
        return orderDetailReportRepository.getNextThreeDayDeliveryOrder(tenant);
    }

    @Override
    public List<Order> getNextThreeDayMeasureOrder(Tenant tenant) {
        return orderDetailReportRepository.getNextThreeDayMeasureOrder(tenant);
    }

    @Override
    public List<Order> getEndOfDayOrder(Tenant tenant) {
        return orderDetailReportRepository.getEndOfDayOrder(tenant);
    }

    @Override
    public List<Order> getAllRemainingAmountOrderByDate(Tenant tenant, Date limitDate) {
        return orderDetailReportRepository.getAllRemainingAmountOrderByDate(tenant,limitDate);
    }

    @Override
    public ReportSummaryModel getAllGiroByDate(Tenant tenant, Date startDate, Date endDate) {
        return orderSummaryReportRepository.getAllGiroByDate(tenant,startDate,endDate);
    }


    @Override
    public List<Order> getAllRemainingAmountOrder(Tenant tenant) {
        return orderDetailReportRepository.getAllRemainingAmountOrder(tenant);
    }

}
