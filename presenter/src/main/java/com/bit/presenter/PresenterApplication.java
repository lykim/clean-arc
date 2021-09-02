package com.bit.presenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bit.core.config.ApplicationConfig;
import com.bit.core.config.GatewayConfig;
import com.bit.core.mocks.gateway.MockBhaktiApiGateway;
import com.bit.core.mocks.gateway.MockKpiGateway;
import com.bit.core.mocks.gateway.MockUnitKpiGateway;
import com.bit.persistent.MysqlGatewayConfig;
import com.bit.persistent.gateway.LoginGatewayImpl;
import com.bit.persistent.gateway.RoleGatewayImpl;
import com.bit.persistent.gateway.RoleGroupGatewayImpl;
import com.bit.persistent.gateway.UserGatewayImpl;

@SpringBootApplication
public class PresenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(PresenterApplication.class, args);
		ApplicationConfig.getInstance().addGatewayConfig("DEV", new MysqlGatewayConfig());
		ApplicationConfig.getInstance().setGateways();
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
				.addMapping("/**")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
			}
		};
	}
}
