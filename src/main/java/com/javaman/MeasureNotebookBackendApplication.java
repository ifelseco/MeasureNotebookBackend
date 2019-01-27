package com.javaman;

import java.util.HashSet;
import java.util.Set;

import com.javaman.service.RoleService;
import com.javaman.util.ConstRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import com.javaman.entity.User;
import com.javaman.security.Role;
import com.javaman.security.SecurityUtility;
import com.javaman.security.UserRole;
import com.javaman.service.UserService;


@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true)
public class MeasureNotebookBackendApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MeasureNotebookBackendApplication.class, args);
	}

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;


	@Override
	public void run(String... arg0) {

		User super_user = new User();
		super_user.setEmail("super@super.com");
		super_user.setPassword(SecurityUtility.passwordEncoder().encode("super"));
		super_user.setUsername("super_user");

		Set<UserRole> roles = new HashSet<>();
		Role roleSuperAdmin = new Role();
		roleSuperAdmin.setName(ConstRole.ARG_SUPER_ROLE);
		roleSuperAdmin=roleService.save(roleSuperAdmin);
		roles.add(new UserRole(super_user, roleSuperAdmin));
		userService.createSuperAdmin(super_user,roles);


		Role roleTailor = new Role();
		roleTailor.setName(ConstRole.ARG_TAILOR_ROLE);
		roleTailor=roleService.save(roleTailor);

		Role roleNormalUser = new Role();
		roleNormalUser.setName(ConstRole.ARG_USER_ROLE);
		roleNormalUser=roleService.save(roleNormalUser);

		Role roleAdmin = new Role();
		roleAdmin.setName(ConstRole.ARG_ADMIN_ROLE);
		roleAdmin=roleService.save(roleAdmin);

	}


	
	
}
