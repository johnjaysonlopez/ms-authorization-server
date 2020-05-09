package com.meteor.project.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.meteor.project.exception.UnAuthenticatedUserException;
import com.meteor.project.security.CustomAuthenticationProvider;
import com.meteor.project.security.CustomClientDetailsService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationPlatformService {

	private CustomClientDetailsService clientDetailsService;

	private CustomAuthenticationProvider authenticationProvider;

	private TokenEndpoint tokenEndpoint;


	public OAuth2AccessToken authenticate(final String username, final String password) {
		final Authentication userAuthentication = new UsernamePasswordAuthenticationToken(username, password);
		final Authentication userAuthenticationCheck = this.authenticationProvider.authenticate(userAuthentication);

		if (!userAuthenticationCheck.isAuthenticated()) throw new UnAuthenticatedUserException();

		final Collection<GrantedAuthority> authorities = new ArrayList<>(userAuthenticationCheck.getAuthorities());
		final Collection<String> permissions = new ArrayList<>();
		for (final GrantedAuthority grantedAuthority : authorities) {
			permissions.add(grantedAuthority.getAuthority());
		}

		ClientDetails clientDetails = this.clientDetailsService.loadClientByClientId("project-web-application");
		if (clientDetails == null) throw new BadClientCredentialsException();

		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", clientDetails.getClientId());
		parameters.put("client_secret", clientDetails.getClientSecret());
		parameters.put("grant_type", "password");
		parameters.put("username", userAuthenticationCheck.getName());
		parameters.put("password", password);
		parameters.put("scope", String.join(", ", permissions));

		final Authentication clientAuthentication = new UsernamePasswordAuthenticationToken(
				new User(clientDetails.getClientId(), clientDetails.getClientSecret(), 
						true, true, true, true, clientDetails.getAuthorities()), null, new ArrayList<GrantedAuthority>());

		OAuth2AccessToken oauth2AccessToken = null;

		try {
			oauth2AccessToken = this.tokenEndpoint.postAccessToken(clientAuthentication, parameters).getBody();
		} catch (HttpRequestMethodNotSupportedException e) {
			log.info(e.getMessage());
		}

		return oauth2AccessToken;
	}

}
