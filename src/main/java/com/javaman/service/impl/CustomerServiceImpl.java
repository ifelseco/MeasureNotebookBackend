package com.javaman.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.javaman.entity.Customer;
import com.javaman.entity.Tenant;
import com.javaman.repository.CustomerRepository;
import com.javaman.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	
	

	@Override
	public List<Customer> findAll() {

		ArrayList<Customer> customers = new ArrayList<>();
		customers = (ArrayList<Customer>) customerRepository.findAll();
		return customers;
	}

	@Override
	public Customer findByTenantAndMobilePhone(Tenant tenant, String mobilePhone) {

		return customerRepository.findByTenantAndMobilePhone(tenant, mobilePhone);
	}

	@Override
	public Customer findOne(Long id) {
		// TODO Auto-generated method stub
		return customerRepository.findOne(id);
	}

	@Override
	public void remove(Long id) {
		customerRepository.delete(id);

	}

	@Override
	public Customer save(Customer customer) {

		return customerRepository.save(customer);
	}

	@Override
	public Customer update(Customer customer) {
		// TODO Auto-generated method stub
		return customerRepository.save(customer);
	}

	@Override
	public Page<Customer> findByTenant(Pageable pageable, Tenant tenant) {
		// TODO Auto-generated method stub
		return customerRepository.findByTenant(pageable, tenant);
	}

	@Override
	public Page<Customer> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return customerRepository.findAll(pageable);
	}

	@Override
	public List<Customer> search(Tenant tenant, String text) {
		// TODO Auto-generated method stub
		String nameSurname=text;
		String mobilePhone=text;
		String fixedPhone=text;
		return customerRepository.findByTenantAndNameSurnameContainingOrMobilePhoneContainingOrFixedPhoneContaining(tenant, nameSurname, mobilePhone, fixedPhone);
	
	}
}
