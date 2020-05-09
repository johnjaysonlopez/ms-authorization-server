package com.meteor.project.oauth2;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import com.meteor.project.security.CustomClientDetailsService;
import com.meteor.project.security.CustomUserDetailsService;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomClientDetailsService clientDetailsService;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private DataSource datasource;

	@Autowired
	private PasswordEncoder passwordEncoder;


	@Bean
	public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter() {
		ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter("/oauth/check_token");
		filter.setAuthenticationManager(this.authenticationManager);
		filter.setAllowOnlyPost(true);
		return filter;
	}

	@Bean
	public OAuth2AccessDeniedHandler oauth2AccessDeniedHandler() {
		return new OAuth2AccessDeniedHandler();
	}

	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(this.datasource);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		security
		.tokenKeyAccess("permitAll()")
		.checkTokenAccess("permitAll()")
		.accessDeniedHandler(this.oauth2AccessDeniedHandler())
		.passwordEncoder(this.passwordEncoder);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints
		.tokenStore(this.tokenStore())
		.authenticationManager(this.authenticationManager)
		.userDetailsService(this.userDetailsService)
		.setClientDetailsService(this.clientDetailsService);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
		.withClientDetails(this.clientDetailsService);
	}

}
