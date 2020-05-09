package com.meteor.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
		"com.meteor.project.base.common.resource.client",
		"com.meteor.project.base.common.resource.permission",
		"com.meteor.project.base.common.resource.role",
		"com.meteor.project.base.common.resource.user"})
public class MsAuthorizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAuthorizationServerApplication.class, args);
	}

}
