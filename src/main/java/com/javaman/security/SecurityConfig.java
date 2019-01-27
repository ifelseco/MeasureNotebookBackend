package com.javaman.security;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

import com.javaman.service.UserSecurityService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] PUBLIC_MATCHES = { "/css/**", "/js/**", "/image/**", "/mail/send/**","/user/forget-password" };

	@Autowired
	private UserSecurityService userSecurityService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable().cors().disable().httpBasic().and().authorizeRequests().antMatchers(PUBLIC_MATCHES)
				.permitAll().anyRequest().authenticated();

	}

	@Autowired
	public void configureGLobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userSecurityService).passwordEncoder(passwordEncoder());

	}

	@Bean
	public HttpSessionStrategy httpSessionStrategy() {
		return new HeaderHttpSessionStrategy();
	}

	private BCryptPasswordEncoder passwordEncoder() {
		return SecurityUtility.passwordEncoder();
	}




}