package com.javaman.repository;

import com.javaman.entity.Order;
import com.javaman.entity.OrderStatus;
import com.javaman.entity.Tenant;

import java.util.List;

public interface TailorOrderRepository {
    List<Order> findTailorOrder(Tenant tenant);
    List<Order> findTailorOrderProcessing(Tenant tenant);
    List<Order> findTailorOrderProcessed(Tenant tenant);
}
