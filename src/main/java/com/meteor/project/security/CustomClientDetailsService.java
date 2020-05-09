package com.meteor.project.security;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import com.meteor.project.base.common.resource.client.ClientRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomClientDetailsService implements ClientDetailsService {

	private ClientRepository clientRepository;


	@Override
	public ClientDetails loadClientByClientId(final String clientId) throws ClientRegistrationException {
		return this.clientRepository.findByClientId(clientId);
	}

}
