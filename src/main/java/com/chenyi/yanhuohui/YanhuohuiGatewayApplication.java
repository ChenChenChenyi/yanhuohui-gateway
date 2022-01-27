package com.chenyi.yanhuohui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@EnableDiscoveryClient
public class YanhuohuiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(YanhuohuiGatewayApplication.class, args);
	}

	/**
	 * 通过配置bean的方式来配置路由规则
	 * @param builder
	 * @return
	 */
//	@Bean
//	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route("path_route", r -> r.path("/yanhuihui")
//						.filters(f ->f.addResponseHeader("X-AnotherHeader", "baz"))
//						.uri("lb://yanhuohui"))
//				.build();
//	}

}
