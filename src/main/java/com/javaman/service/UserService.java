package com.javaman.service;

import java.util.List;
import java.util.Set;

import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.security.UserRole;

public interface UserService {

	User createUser(User user, Set<UserRole> userRole);
	User createSuperAdmin(User user, Set<UserRole> userRole);

	User findByEmail(String email);

	List<User> findByTenant(Tenant tenant);

	User findByUsername(String username);

	User findOne(Long id);

	List<User> findTenantAdminUsers(Tenant tenant);
	List<User> findTenantTailorUsers(Tenant tenant);

	List<User> findByLastFirebaseRegId(String lastFirebaseRegId);

	User update(User user);

}
