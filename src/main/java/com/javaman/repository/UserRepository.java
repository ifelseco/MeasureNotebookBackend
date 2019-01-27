package com.javaman.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.javaman.entity.Tenant;
import com.javaman.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	User findByEmail(String email);

	List<User> findByTenant(Tenant tenant);

	User findByUsername(String username);
	
	List<User> findByLastFirebaseRegId(String lastFirebaseRegId);


}
