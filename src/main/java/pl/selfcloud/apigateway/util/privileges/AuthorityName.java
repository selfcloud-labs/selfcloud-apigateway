package pl.selfcloud.apigateway.util.privileges;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public enum AuthorityName implements GrantedAuthority {

  CREATE_ORDER("CREATE_ORDER"),
  UPDATE_ORDER("UPDATE_ORDER"),
  DELETE_ORDER("DELETE_ORDER"),
  READ_ORDER("READ_ORDER"),
  CREATE_USER("CREATE_USER"),
  DELETE_USER("DELETE_USER"),
  GRAND_AUTHORITY("GRAND_AUTHORITY"),
  REVOKE_AUTHORITY("REVOKE_AUTHORITY");

  final String name;

  @Override
  public String getAuthority() {
    return name;
  }

}
