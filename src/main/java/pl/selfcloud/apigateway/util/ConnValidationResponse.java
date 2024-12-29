package pl.selfcloud.apigateway.util;

import java.io.Serializable;
import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import pl.selfcloud.apigateway.util.privileges.MyGrantedAuthority;


@Getter
@Builder
@ToString
@Data
public class ConnValidationResponse implements Serializable {
  private String status;
  private boolean isAuthenticated;
  private String methodType;
  private String username;
  private Long userId;
  private String token;
  private Collection<MyGrantedAuthority> authorities;
}