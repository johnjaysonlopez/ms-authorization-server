package com.project.authorization.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Service;

import com.project.authorization.exception.UnAuthenticatedUserException;
import com.project.authorization.security.CustomAuthenticationProvider;
import com.project.authorization.security.CustomClientDetailsService;
import com.project.base.common.resources.OAuth2GrantType;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationPlatformService {

	private CustomClientDetailsService clientDetailsService;

	private CustomAuthenticationProvider authenticationProvider;

	private TokenEndpoint tokenEndpoint;


	private ClientDetails validate(String clientid) {
		ClientDetails clientDetails = this.clientDetailsService.loadClientByClientId(clientid);
		if (clientDetails == null) throw new BadClientCredentialsException();
		else return clientDetails;
	}

	public OAuth2AccessToken authenticate(final String clientid, final String username, final String password) {
		ClientDetails clientDetails = this.validate(clientid);

		final Authentication userAuthentication = new UsernamePasswordAuthenticationToken(username, password);
		final Authentication userAuthenticationCheck = this.authenticationProvider.authenticate(userAuthentication);

		if (!userAuthenticationCheck.isAuthenticated()) throw new UnAuthenticatedUserException();

		final Collection<GrantedAuthority> authorities = new ArrayList<>(userAuthenticationCheck.getAuthorities());
		final Collection<String> permissions = new ArrayList<>();
		for (final GrantedAuthority grantedAuthority : authorities) {
			permissions.add(grantedAuthority.getAuthority());
		}

		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", clientDetails.getClientId());
		parameters.put("client_secret", clientDetails.getClientSecret());
		parameters.put("grant_type", OAuth2GrantType.PASSWORD);
		parameters.put("username", userAuthenticationCheck.getName());
		parameters.put("password", password);
		parameters.put("scope", String.join(", ", permissions));

		final Authentication clientAuthentication = new UsernamePasswordAuthenticationToken(
				new User(clientDetails.getClientId(), clientDetails.getClientSecret(), clientDetails.getAuthorities()), null, null);

		OAuth2AccessToken oauth2AccessToken = null;

		try {
			oauth2AccessToken = this.tokenEndpoint.postAccessToken(clientAuthentication, parameters).getBody();
		} catch (ServletException e) {
			log.info(e.getMessage());
		}

		return oauth2AccessToken;
	}

	public OAuth2AccessToken authenticate(final String clientid, final String refreshtoken) {
		ClientDetails clientDetails = this.validate(clientid);

		final Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", clientDetails.getClientId());
		parameters.put("client_secret", clientDetails.getClientSecret());
		parameters.put("grant_type", OAuth2GrantType.REFRESH_TOKEN);
		parameters.put("refresh_token", refreshtoken);

		final Authentication clientAuthentication = new UsernamePasswordAuthenticationToken(
				new User(clientDetails.getClientId(), clientDetails.getClientSecret(), clientDetails.getAuthorities()), null, null);

		OAuth2AccessToken oauth2AccessToken = null;

		try {
			oauth2AccessToken = this.tokenEndpoint.postAccessToken(clientAuthentication, parameters).getBody();
		} catch (ServletException e) {
			log.info(e.getMessage());
		}

		return oauth2AccessToken;
	}

}
