package com.project.authorization.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;

import com.project.base.common.resources.user.User;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {	

	private CustomUserDetailsService userDetailsService;

	private PasswordEncoder passwordEncoder;


	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final String username = authentication.getName();
		final String password = authentication.getCredentials().toString();

		User user = (User) this.userDetailsService.loadUserByUsername(username);

		if (user == null) throw new OAuth2Exception("Bad user credentials");

		if (this.passwordEncoder.matches(password, user.getPassword())) {
			return new UsernamePasswordAuthenticationToken(username, "password", user.getAuthorities());
		} else throw new OAuth2Exception("Bad user credentials");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
