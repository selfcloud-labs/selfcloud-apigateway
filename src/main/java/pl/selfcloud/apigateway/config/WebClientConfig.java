package pl.selfcloud.apigateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.token.TokenService;
import pl.selfcloud.apigateway.filter.JwtValidationGatewayFilterFactory;
import pl.selfcloud.security.api.util.JwtUtil;

@Configuration
public class WebClientConfig {

//  @Bean
//  @LoadBalanced
//  public WebClient.Builder loadBalancedWebClientBuilder() {
//    return WebClient.builder();
//  }

//  @Bean
//  public JwtValidationGatewayFilterFactory jwtValidationGatewayFilterFactory(
//      AuthenticationManager authenticationManager,
//      JwtUtil jwtUtil, TokenService tokenService){
//    return new JwtValidationGatewayFilterFactory(jwtUtil, authenticationManager, tokenService);
//  }

  @Bean
  public JwtUtil jwtUtil(){
    return new JwtUtil();
  }

}
