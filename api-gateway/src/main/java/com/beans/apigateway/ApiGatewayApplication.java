package com.beans.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args){
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    RouteLocator apiGatewayRoute(RouteLocatorBuilder builder){
        return builder.routes()
                .route("cartRoute", routeSepc -> routeSepc
                        .path("/cart/**")
                        .filters(f -> f.addRequestHeader("X-Response-Header", "SCS"))
                        .uri("http://server3:8082")
                )
                .build();
    }
}
