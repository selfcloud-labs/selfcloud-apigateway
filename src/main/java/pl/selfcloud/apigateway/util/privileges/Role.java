package pl.selfcloud.apigateway.util.privileges;


import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class Role implements Serializable {


  private Long id;

  private RoleName name;

  private String description;

  private List<Authority> authorities;

  public Role(String name, List<Authority> authorities, String description){
    this.name = RoleName.valueOf(name);
    this.authorities = authorities;
    this.description = description;
  }

  public Role(RoleName name, List<Authority> authorities, String description){
    this.name = name;
    this.authorities = authorities;
    this.description = description;
  }


}