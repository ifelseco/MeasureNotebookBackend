package com.javaman.repository.impl;

import com.javaman.controller.CustomerController;
import com.javaman.entity.Order;
import com.javaman.entity.Tenant;
import com.javaman.model.ReportDetailModel;
import com.javaman.model.ReportSummaryModel;
import com.javaman.repository.OrderDetailReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class OrderDetailReportRepositoryImpl implements OrderDetailReportRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OrderDetailReportRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Order> getNextThreeDayDeliveryOrder(Tenant tenant) {
        Long id=tenant.getId();
        String sql="SELECT * FROM orders where tenant_id="+id+" and date(orders.delivery_date) between curdate() and date_add(curdate(),interval 3 day)";
        Query query=entityManager.createNativeQuery(sql,Order.class);
        List<Order> results=query.getResultList();
        return results;
    }

    @Override
    public List<Order> getNextThreeDayMeasureOrder(Tenant tenant) {
        Long id=tenant.getId();
        String sql="SELECT * FROM orders where tenant_id="+id+" and date(orders.measure_date) between curdate() and date_add(curdate(),interval 3 day)";
        Query query=entityManager.createNativeQuery(sql,Order.class);
        List<Order> resultList=query.getResultList();
        return resultList;
    }

    @Override
    public List<Order> getEndOfDayOrder(Tenant tenant) {
        Long id=tenant.getId();
        String sql="SELECT * FROM orders where orders.tenant_id="+id+" and date(orders.order_date) = curdate()";
        Query query=entityManager.createNativeQuery(sql,Order.class);
        List<Order> resultList=query.getResultList();
        return resultList;
    }


    @Override
    public List<Order> getAllRemainingAmountOrder(Tenant tenant) {
        Long id=tenant.getId();
        String sql="SELECT * FROM orders where orders.tenant_id="+id+" and orders.deposite_amount < orders.total_amount";
        Query query=entityManager.createNativeQuery(sql,Order.class);
        List<Order> resultList=query.getResultList();
        return resultList;
    }

    @Override
    public List<Order> getAllRemainingAmountOrderByDate(Tenant tenant, Date limitDate) {
        Long id=tenant.getId();
        String date=new SimpleDateFormat("yyyy-MM-dd").format(limitDate);
        String sql="SELECT * FROM orders where orders.tenant_id="+id+" and orders.deposite_amount < orders.total_amount and date(orders.delivery_date) <= '"+date+"'";
        Query query=entityManager.createNativeQuery(sql,Order.class);
        List<Order> resultList=query.getResultList();
        return resultList;
    }


}
