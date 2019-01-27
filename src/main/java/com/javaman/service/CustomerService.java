package com.javaman.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.javaman.entity.Customer;
import com.javaman.entity.Tenant;

public interface CustomerService {

	List<Customer> findAll();

	Customer findByTenantAndMobilePhone(Tenant tenant, String mobilePhone);

	Customer findOne(Long id);

	void remove(Long id);

	Customer save(Customer customer);

	Customer update(Customer customer);

	Page<Customer> findByTenant(Pageable pageable , Tenant tenant);
	
	Page<Customer> findAll(Pageable pageable);

	List<Customer> search(Tenant tenant , String text);
}
