package com.javaman.service;

import java.util.List;

import com.javaman.entity.Tenant;

public interface TenantService {
	Tenant createTenant(Tenant tenant);

	List<Tenant> findAll();

	Tenant findByTenantName(String tenantName);
	Tenant findByEmail(String email);

	Tenant findOne(Long id);

}
