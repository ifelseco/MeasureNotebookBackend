package com.javaman.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.javaman.entity.*;
import com.javaman.repository.TailorOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.javaman.repository.OrderRepository;
import com.javaman.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private TailorOrderRepository tailorOrderRepository;

	@Override
	public List<Order> findAll() {

		ArrayList<Order> orders = new ArrayList<Order>();
		orders = (ArrayList<Order>) orderRepository.findAll();
		return orders;
	}

	@Override
	public List<Order> findByTenant(Tenant tenant) {

		ArrayList<Order> orders = new ArrayList<Order>();
		orders = (ArrayList<Order>) orderRepository.findByTenant(tenant);
		return orders;
	}

	@Override
	public List<Order> findByUser(User user) {
		ArrayList<Order> orders = new ArrayList<Order>();
		orders = (ArrayList<Order>) orderRepository.findByUser(user);
		return orders;
	}

	@Override
	public Order findOne(Long id) {
		// TODO Auto-generated method stub
		return orderRepository.findOne(id);
	}

	@Override
	public void remove(Order order) {
		orderRepository.delete(order);

	}

	@Override
	public Order save(Order order) {
		// TODO Auto-generated method stub
		return orderRepository.save(order);
	}

	@Override
	public Order update(Order order) {
		// TODO Auto-generated method stub
		return orderRepository.save(order);
	}

	@Override
	public List<Order> search(Tenant tenant, String text) {
		String orderNumber=text;
		return orderRepository.findByTenantAndOrderNumberEquals(tenant, orderNumber);

	}


	@Override
	public Page<Order> findByTenant(Pageable pageable, Tenant tenant) {
		
		return orderRepository.findByTenant(pageable, tenant);
	}

	@Override
	public Page<Order> findByTenantAndOrderStatus(Pageable pageable, Tenant tenant, OrderStatus orderStatus) {
		return orderRepository.findByTenantAndOrderStatus(pageable,tenant,orderStatus);
	}

	@Override
	public List<Order> findTailorOrder(Tenant tenant) {
		return tailorOrderRepository.findTailorOrder(tenant);
	}

	@Override
	public List<Order> findTailorOrderProcessed(Tenant tenant) {
		return tailorOrderRepository.findTailorOrderProcessed(tenant);
	}

	@Override
	public List<Order> findTailorOrderProcessing(Tenant tenant) {
		return tailorOrderRepository.findTailorOrderProcessing(tenant);
	}

	@Override
	public Page<Order> findByUser(Pageable pageable, User user) {
		// TODO Auto-generated method stub
		return orderRepository.findByUser(pageable, user);
	}

	@Override
	public Page<Order> findByUserAndOrderStatus(Pageable pageable, User user, OrderStatus orderStatus) {
		return orderRepository.findByUserAndOrderStatus(pageable,user,orderStatus);
	}

	@Override
	public Page<Order> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return orderRepository.findAll(pageable);
	}

	@Override
	public void remove(List<Order> orders) {
		orderRepository.delete(orders);
	}

	@Override
	public List<Order> findByCustomer(Customer customer) {
		// TODO Auto-generated method stub
		return orderRepository.findByCustomer(customer);
	}

}
