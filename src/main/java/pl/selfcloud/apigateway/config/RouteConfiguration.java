package pl.selfcloud.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.selfcloud.apigateway.filter.JwtValidationGatewayFilterFactory;

@Configuration
public class RouteConfiguration {

  @Value("${selfcloud-apigateway.security.host}")
  String host;

  @Bean
  public RouteLocator routes(
      RouteLocatorBuilder builder,
      JwtValidationGatewayFilterFactory jwtValidationGatewayFilterFactory) {
    return builder.routes()
        .route("authentication-route", r -> r.path("/auth/**")
            .filters(f -> f.prefixPath("/api/v1"))
            .uri("http://" + host +":8090"))

        .route("announcement-route", r -> r.path("/announcements/**")
            .filters(f ->
                f.prefixPath("/api/v1")
                    .filter(jwtValidationGatewayFilterFactory.apply(
                        new JwtValidationGatewayFilterFactory.Config())))
            .uri("http://" + host + ":8091"))
        .route("order-route", r -> r.path("/orders/**")
            .filters(f ->
                f.prefixPath("/api/v1")
                    .filter(jwtValidationGatewayFilterFactory.apply(
                        new JwtValidationGatewayFilterFactory.Config())))
            .uri("http://" + host + ":8092"))
        .route("invoice-route", r -> r.path("/invoices/**")
            .filters(f ->
                f.prefixPath("/api/v1")
                    .filter(jwtValidationGatewayFilterFactory.apply(
                        new JwtValidationGatewayFilterFactory.Config())))
            .uri("http://" + host + ":8094"))
        .build();
  }

}