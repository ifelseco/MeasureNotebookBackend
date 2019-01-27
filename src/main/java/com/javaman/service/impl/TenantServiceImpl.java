package com.javaman.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaman.entity.Tenant;
import com.javaman.repository.TenantRepository;
import com.javaman.service.TenantService;

@Service
public class TenantServiceImpl implements TenantService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private TenantRepository tenantRepository;

	@Override
	public Tenant createTenant(Tenant tenant) {
		return tenantRepository.save(tenant);
	}

	@Override
	public List<Tenant> findAll() {

		ArrayList<Tenant> tenants = new ArrayList<>();
		tenants = (ArrayList<Tenant>) tenantRepository.findAll();
		return tenants;
	}

	@Override
	public Tenant findByTenantName(String tenantName) {
		return tenantRepository.findByTenantName(tenantName);
	}

	@Override
	public Tenant findByEmail(String email) {
		return tenantRepository.findByEmail(email);
	}

	@Override
	public Tenant findOne(Long id) {
		return tenantRepository.findOne(id);
	}

}
