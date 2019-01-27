package com.javaman.service;

import java.util.List;

import com.javaman.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

	List<Order> findAll();
	
	Page<Order> findAll(Pageable pageable);

	List<Order> findByTenant(Tenant tenant);
	
	List<Order> findByCustomer(Customer customer);

	
	Page<Order> findByTenant(Pageable pageable , Tenant tenant);

	Page<Order> findByTenantAndOrderStatus(Pageable pageable , Tenant tenant,OrderStatus orderStatus);

	//Page<Order> findTailorOrder(Pageable pageable , Tenant tenant);
	List<Order> findTailorOrder(Tenant tenant);
	List<Order> findTailorOrderProcessed(Tenant tenant);
	List<Order> findTailorOrderProcessing(Tenant tenant);



	List<Order> findByUser(User user);
	
	Page<Order> findByUser(Pageable pageable , User user);
	Page<Order> findByUserAndOrderStatus(Pageable pageable , User user,OrderStatus orderStatus);

	Order findOne(Long id);

	void remove(Order order);
	
	void remove(List<Order> orders);

	Order save(Order order);

	Order update(Order order);

    List<Order> search(Tenant tenant, String text);
}
