package pl.selfcloud.apigateway.filter;

import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.selfcloud.apigateway.filter.UserAuthenticationGatewayFilterFactory.Config;
import pl.selfcloud.security.api.util.JwtUtil;

@Service
@Slf4j
public class UserAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {
  private final JwtUtil jwtUtil;

  public UserAuthenticationGatewayFilterFactory(JwtUtil jwtUtil) {
    super(Config.class);
    this.jwtUtil = jwtUtil;
  }
  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      try {
        log.info("Incoming request: {} {}", exchange.getRequest().getMethod().name(), exchange.getRequest().getPath());
        String accessToken = exchange.getRequest().getHeaders().get(config.getHeaderName()).stream()
            .findFirst()
            .orElseThrow();
        String mail = jwtUtil.extractClaim(accessToken.substring(7), claims -> claims.get("sub", String.class));

        exchange.getAttributes().put("mail", mail);
        return chain.filter(exchange);
      } catch (Exception ex) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }
    };
  }

  @Getter
  @Setter
  @Builder
  public static class Config {
    private String headerName;
  }
}