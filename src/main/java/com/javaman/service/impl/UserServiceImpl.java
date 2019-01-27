package com.javaman.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.repository.RoleRepository;
import com.javaman.repository.TenantRepository;
import com.javaman.repository.UserRepository;
import com.javaman.security.UserRole;
import com.javaman.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TenantRepository tenantRepository;

	@Transactional
	public User createUser(User user, Set<UserRole> userRoles) {

		for (UserRole ur : userRoles) {
			roleRepository.save(ur.getRole());
		}

		user.getUserRoles().addAll(userRoles);

		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User createSuperAdmin(User user, Set<UserRole> userRoles) {
		User superAdmin = userRepository.findByUsername(user.getUsername());


		if (superAdmin != null) {
			LOG.info("User with username {} already exist.", user.getUsername());

		} else {
			for (UserRole ur : userRoles) {
				roleRepository.save(ur.getRole());
			}

			user.getUserRoles().addAll(userRoles);

			superAdmin = userRepository.save(user);
		}

		return superAdmin;
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> findByTenant(Tenant tenant) {
		// TODO Auto-generated method stub
		return userRepository.findByTenant(tenant);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User findOne(Long id) {
		return userRepository.findOne(id);
	}

	@Override
	public List<User> findTenantAdminUsers(Tenant tenant) {

		List<User> adminUsers = new ArrayList<User>();

		List<User> tenantUsers = userRepository.findByTenant(tenant);

		for (User user : tenantUsers) {

			Set<UserRole> userRoles = user.getUserRoles();

			ArrayList<String> roleNames = new ArrayList<>();

			for (UserRole userRole : userRoles) {
				roleNames.add(userRole.getRole().getName());
			}

			if (roleNames.contains("ROLE_ADMIN")) {
				adminUsers.add(user);
			}

		}

		return adminUsers;
	}

	@Override
	public List<User> findTenantTailorUsers(Tenant tenant) {

		List<User> tailorUsers = new ArrayList<User>();

		List<User> tenantUsers = userRepository.findByTenant(tenant);

		for (User user : tenantUsers) {

			Set<UserRole> userRoles = user.getUserRoles();

			ArrayList<String> roleNames = new ArrayList<>();

			for (UserRole userRole : userRoles) {
				roleNames.add(userRole.getRole().getName());
			}

			if (roleNames.contains("ROLE_TAILOR")) {
				tailorUsers.add(user);
			}

		}

		return tailorUsers;
	}


	@Override
	public List<User> findByLastFirebaseRegId(String lastFirebaseRegId) {
		// TODO Auto-generated method stub
		return userRepository.findByLastFirebaseRegId(lastFirebaseRegId);
	}

	@Override
	public User update(User user) {
		return userRepository.save(user);
	}

}
