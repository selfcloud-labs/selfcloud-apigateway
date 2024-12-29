package pl.selfcloud.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import pl.selfcloud.apigateway.util.ConnValidationResponse;
import pl.selfcloud.apigateway.util.SecurityConstants;
import pl.selfcloud.apigateway.util.privileges.MyGrantedAuthority;
import pl.selfcloud.security.api.util.JwtUtil;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationPrefilter
    extends AbstractGatewayFilterFactory<AuthenticationPrefilter.Config> {


  @Value("${selfcloud-apigateway.security.host}")
  String host;

  List<String> excludedUrls = new ArrayList<>();
  private final JwtUtil jwtUtil;

  @Autowired
  private ObjectMapper objectMapper;

  public AuthenticationPrefilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public GatewayFilter apply(Config config) throws ExpiredJwtException{
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      log.info("**************************************************************************");
      log.info("URL is - " + request.getURI().getPath());
      String bearerToken = request.getHeaders().getFirst(SecurityConstants.AUTHORIZATION.name());
      log.info("Bearer Token: " + bearerToken);

//      try{
//        jwtUtil.isInvalid(bearerToken.split(" ")[1].trim());
//      }catch (ExpiredJwtException ex){
//        ex.printStackTrace();
//      }

      RestTemplate restTemplate = new RestTemplate();

      String url = "http://" + host + ":8090/api/v1/auth/validateToken";
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", bearerToken);
      HttpEntity<String> newRequest = new HttpEntity<String>(headers);
      ResponseEntity<ConnValidationResponse> response = restTemplate.exchange(url, HttpMethod.GET, newRequest, ConnValidationResponse.class);
      ConnValidationResponse validationResponse = response.getBody();
      Collection<MyGrantedAuthority> authorities = validationResponse.getAuthorities();

      List<String> authStr = authorities.stream().map(MyGrantedAuthority::getAuthority).toList();

      log.info(validationResponse.toString());
      exchange.getRequest().mutate().header("username", validationResponse.getUsername());
      exchange.getRequest().mutate().header("authorities", String.valueOf(authStr));
      exchange.getRequest().mutate().header("userId", String.valueOf(validationResponse.getUserId()));


//      if(isSecured.test(request)) {
//
//        WebClient client = WebClient.builder()
//            .baseUrl("http://localhost:8090")
//            .build();
//
//        return client.get()
//            .uri("/api/v1/auth/validateToken")
//            .header("Authorization", bearerToken)
//            .retrieve().bodyToMono(ConnValidationResponse.class)
//            .map(response -> {
//              log.info(response.toString());
//              exchange.getRequest().mutate().header("username", response.getUsername());
//              exchange.getRequest().mutate().header("authorities", response.getAuthorities().stream().map(
//                  GrantedAuthority::getAuthority).reduce("", (a, b) -> a + "," + b));
//              return exchange;
//
//            }).flatMap(chain::filter).onErrorResume(error -> {
//              log.info("Error Happened");
//              HttpStatusCode errorCode = null;
//              String errorMsg = "";
//
//              if (error instanceof WebClientResponseException) {
//                WebClientResponseException webCLientException = (WebClientResponseException) error;
//                errorCode = webCLientException.getStatusCode();
//                errorMsg = webCLientException.getStatusText();
//
//              } else {
//                errorCode = HttpStatus.BAD_GATEWAY;
//                errorMsg = HttpStatus.BAD_GATEWAY.getReasonPhrase();
//              }
////                            AuthorizationFilter.AUTH_FAILED_CODE
//              return onError(exchange, String.valueOf(errorCode.value()) ,errorMsg, "JWT Authentication Failed",
//                  (HttpStatus) errorCode);
//            });
//      }
//
      return chain.filter(exchange);
    };

  }
  public Predicate<ServerHttpRequest> isSecured = request -> excludedUrls.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
  private Mono<Void> onError(ServerWebExchange exchange, String errCode, String err, String errDetails, HttpStatus httpStatus) {
    DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
//        ObjectMapper objMapper = new ObjectMapper();
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(httpStatus);
    try {
      response.getHeaders().add("Content-Type", "application/json");
      ExceptionResponseModel data = new ExceptionResponseModel(errCode, err, errDetails, null, new Date());
      byte[] byteData = objectMapper.writeValueAsBytes(data);
      return response.writeWith(Mono.just(byteData).map(t -> dataBufferFactory.wrap(t)));

    } catch (JsonProcessingException e) {
      e.printStackTrace();

    }
    return response.setComplete();
  }

  @NoArgsConstructor
  public static class Config {


  }
}
