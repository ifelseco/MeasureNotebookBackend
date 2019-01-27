package com.javaman.service;

import com.javaman.security.Role;

public interface RoleService {
    Role findByName(String name);
    Role save(Role role);
}
