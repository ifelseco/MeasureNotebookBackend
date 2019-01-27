package com.javaman.service.impl;

import com.javaman.repository.RoleRepository;
import com.javaman.security.Role;
import com.javaman.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService{

    private static final Logger LOG = LoggerFactory.getLogger(RoleServiceImpl.class);


    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Role save(Role role) {
        Role newRole=findByName(role.getName());
        if(newRole!=null){
            LOG.warn("Bu role daha önceden kayıt edilmiş {} ",role.getName());
            return newRole;
        }else{
            return roleRepository.save(role);
        }

    }
}
