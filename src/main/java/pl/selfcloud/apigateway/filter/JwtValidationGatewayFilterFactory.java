package pl.selfcloud.apigateway.filter;

import io.jsonwebtoken.ExpiredJwtException;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import pl.selfcloud.apigateway.util.ConnValidationResponse;

import pl.selfcloud.apigateway.util.SecurityConstants;
import pl.selfcloud.apigateway.util.privileges.MyGrantedAuthority;
import pl.selfcloud.security.api.util.JwtUtil;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtValidationGatewayFilterFactory.Config> {

  private final JwtUtil jwtUtil;
  private final WebClient webClient;
  @Value("${selfcloud-apigateway.security.host}")
  String host;


  @Autowired
  public JwtValidationGatewayFilterFactory(JwtUtil jwtUtil, WebClient.Builder webClientBuilder) {
    super(Config.class);
    this.jwtUtil = jwtUtil;
    this.webClient = webClientBuilder.build();

  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      log.info("**************************************************************************");
      log.info("URL is - " + request.getURI().getPath());
      String externalBearerToken = request.getHeaders().getFirst(SecurityConstants.AUTHORIZATION.name());
      log.info("Bearer Token: " + externalBearerToken);

      if (externalBearerToken == null || !externalBearerToken.startsWith("Bearer ")) {
        return chain.filter(exchange);
      }

      String externalToken = externalBearerToken.split(" ")[1].trim();

      try {
        jwtUtil.isTokenExpired(externalToken);
      } catch (ExpiredJwtException ex) {
        ex.printStackTrace();
        return Mono.error(ex);  // Zwracamy Mono z błędem, jeśli token jest przeterminowany
      }

      String url = "http://" + host + ":8090/api/v1/auth/validateToken";

      return webClient.get()
          .uri(url)
          .header(HttpHeaders.AUTHORIZATION, externalBearerToken)
          .retrieve()
          .bodyToMono(ConnValidationResponse.class)
          .flatMap(validationResponse -> {
            Collection<MyGrantedAuthority> authorities = validationResponse.getAuthorities();
            List<String> authStr = authorities.stream().map(MyGrantedAuthority::getAuthority).toList();

            log.info(validationResponse.toString());

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("username", validationResponse.getUsername())
                .header("authorities", String.valueOf(authStr))
                .header("userId", String.valueOf(validationResponse.getUserId()))
                .build();

            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
            return chain.filter(modifiedExchange);
          })
          .onErrorResume(e -> {
            log.error("Error during token validation", e);
            return chain.filter(exchange);
          });
    };
  }

  public static class Config {

  }
}
