package pl.selfcloud.apigateway.util.privileges;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyGrantedAuthority implements GrantedAuthority, Serializable {

  private String authority;

  @Override
  public String getAuthority() {
    return authority;
  }
}
