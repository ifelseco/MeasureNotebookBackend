package com.javaman.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 14400)
public class HttpSessionConfig {

	public LettuceConnectionFactory connectionFactory() {
		LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
		return connectionFactory;
	}

}