package com.javaman.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.javaman.entity.Tenant;

@Repository
public interface TenantRepository extends CrudRepository<Tenant, Long> {

	List<Tenant> findAll();

	Tenant findByTenantName(String tenantName);
	Tenant findByEmail(String email);

}
