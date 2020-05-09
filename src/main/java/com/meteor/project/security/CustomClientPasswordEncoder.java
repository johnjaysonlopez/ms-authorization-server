package com.meteor.project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomClientPasswordEncoder {

	@Bean
	public PasswordEncoder clientPasswordEncoder() {
		return new BCryptPasswordEncoder(8);
	}

}
