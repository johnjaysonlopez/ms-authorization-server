package com.project.authorization.api;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.authorization.service.AuthenticationPlatformService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

@Api(value = "Authentication")
@RestController
@RequestMapping("/authentication")
@AllArgsConstructor
public class AuthenticationApiResource {

	private AuthenticationPlatformService authenticationPlatformService;


	@PostMapping
	@ApiOperation(value = "Resource owner password credentials grant", notes = "Authenticates the credentials provided and returns an OAuth2 access token.")
	public OAuth2AccessToken authenticate(
			@RequestParam("username") @ApiParam(value = "username") final String username, 
			@RequestParam("password") @ApiParam(value = "password") final String password) {
		return this.authenticationPlatformService.authenticate(username, password);
	}

	@PostMapping("/refresh")
	@ApiOperation(value = "Refresh token grant", notes = "Authenticates the refresh token provided and returns an OAuth2 access token.")
	public OAuth2AccessToken authenticate(
			@RequestParam("refreshtoken") @ApiParam(value = "refreshtoken") final String refreshtoken) {
		return this.authenticationPlatformService.authenticate(refreshtoken);
	}

}
