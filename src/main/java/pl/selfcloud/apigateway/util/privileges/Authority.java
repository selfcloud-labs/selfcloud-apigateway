package pl.selfcloud.apigateway.util.privileges;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Authority implements Serializable {

  private Long id;

  private AuthorityName name;

  private String description;

  public Authority(String name, String description){
    this.name = AuthorityName.valueOf(name);
  }

  public Authority(AuthorityName name, String description){
    this.name = name;
  }

  public Authority(String name){
    this.name = AuthorityName.valueOf(name);
  }

  public Authority(AuthorityName name){
    this.name = name;
  }
}