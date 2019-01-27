package com.javaman.repository;

import com.javaman.entity.Order;
import com.javaman.entity.Tenant;
import com.javaman.model.ReportDetailModel;
import com.javaman.model.ReportSummaryModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

public interface OrderDetailReportRepository {

    List<Order> getNextThreeDayDeliveryOrder(Tenant tenant);
    List<Order> getNextThreeDayMeasureOrder(Tenant tenant);
    List<Order> getEndOfDayOrder(Tenant tenant);
    List<Order> getAllRemainingAmountOrder(Tenant tenant);
    List<Order> getAllRemainingAmountOrderByDate(Tenant tenant, Date limitDate);
}
