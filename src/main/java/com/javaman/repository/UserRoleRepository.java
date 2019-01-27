package com.javaman.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.javaman.entity.User;
import com.javaman.security.UserRole;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
	List<UserRole> findByUser(User user);
}