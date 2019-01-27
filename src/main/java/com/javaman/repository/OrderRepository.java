package com.javaman.repository;

import java.util.Date;
import java.util.List;

import com.javaman.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long>{
	
	List<Order> findByTenant(Tenant tenant);
	
	@Query
	Page<Order> findByTenant(Pageable pageable , Tenant tenant);

	@Query
	Page<Order> findByTenantAndOrderStatus(Pageable pageable , Tenant tenant,OrderStatus orderStatus);

	List<Order> findByUser(User user);
	Page<Order> findByUserAndOrderStatus(Pageable pageable ,User user,OrderStatus orderStatus);

	Page<Order> findByUser(Pageable pageable , User user);
	
	List<Order> findByCustomer(Customer customer);

	@Query
	Page<Order> findByTenantAndOrderStatusOrOrderStatus(Pageable pageable , Tenant tenant, OrderStatus orderStatus1,OrderStatus orderStatus2);

	@Query
	List<Order> findByTenantAndOrderNumberEquals(Tenant tenant, String orderNumber);

}
