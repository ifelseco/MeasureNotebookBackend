package com.javaman.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaman.entity.User;
import com.javaman.repository.UserRoleRepository;
import com.javaman.security.UserRole;
import com.javaman.service.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Override
	public List<UserRole> findByUser(User user) {
		// TODO Auto-generated method stub
		return userRoleRepository.findByUser(user);
	}

	@Override
	public List<String> findUserRole(User user) {

		List<UserRole> userRoles = new ArrayList<>();
		List<String> userRoleNames = new ArrayList<>();
		userRoles = userRoleRepository.findByUser(user);

		for (UserRole userRole : userRoles) {
			userRoleNames.add(userRole.getRole().getName());
		}

		return userRoleNames;
	}

}
