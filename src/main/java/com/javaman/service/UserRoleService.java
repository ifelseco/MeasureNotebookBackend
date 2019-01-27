package com.javaman.service;

import java.util.List;

import com.javaman.entity.User;
import com.javaman.security.UserRole;

public interface UserRoleService {
	List<UserRole> findByUser(User user);

	List<String> findUserRole(User user);

}
