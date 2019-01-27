package com.javaman.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javaman.entity.Customer;
import com.javaman.entity.Tenant;
import com.javaman.entity.User;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

	Customer findByTenantAndMobilePhone(Tenant tenant, String mobilePhone);
	
	List<Customer> findByTenant(Tenant tenant);

	
	@Query
	Page<Customer> findByTenant(Pageable pageable , Tenant tenant);

	@Query
	List<Customer> findByTenantAndNameSurnameContainingOrMobilePhoneContainingOrFixedPhoneContaining(Tenant tenant, String nameSurname,String mobilePhone,String fixedPhone);
	
	
}
