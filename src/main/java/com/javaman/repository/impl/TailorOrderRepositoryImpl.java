package com.javaman.repository.impl;

import com.javaman.entity.Order;
import com.javaman.entity.OrderStatus;
import com.javaman.entity.Tenant;
import com.javaman.repository.TailorOrderRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class TailorOrderRepositoryImpl implements TailorOrderRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Order> findTailorOrder(Tenant tenant) {
        Long id=tenant.getId();
        String orderStatus1="PROCESSING";
        String orderStatus2="PROCESSED";
        String sql="SELECT * FROM orders where orders.tenant_id="+id+" and orders.tailor_order_line_count > 0 and (orders.order_status='"+orderStatus1+"' or orders.order_status='"+orderStatus2+"')";
        Query query=entityManager.createNativeQuery(sql,Order.class);
        List<Order> resultList=query.getResultList();
        return resultList;
    }

    @Override
    public List<Order> findTailorOrderProcessing(Tenant tenant) {
        Long id=tenant.getId();
        String orderStatus="PROCESSING";
        String sql="SELECT * FROM orders where orders.tenant_id="+id+" and orders.tailor_order_line_count > 0 and (orders.order_status='"+orderStatus+"')";
        Query query=entityManager.createNativeQuery(sql,Order.class);
        List<Order> resultList=query.getResultList();
        return resultList;
    }

    @Override
    public List<Order> findTailorOrderProcessed(Tenant tenant) {
        Long id=tenant.getId();
        String orderStatus="PROCESSED";
        String sql="SELECT * FROM orders where orders.tenant_id="+id+" and orders.tailor_order_line_count > 0 and (orders.order_status='"+orderStatus+"')";
        Query query=entityManager.createNativeQuery(sql,Order.class);
        List<Order> resultList=query.getResultList();
        return resultList;
    }
}
