package com.javaman.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.javaman.security.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    Role findByName(String name);
}