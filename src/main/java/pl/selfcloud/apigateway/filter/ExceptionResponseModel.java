package pl.selfcloud.apigateway.filter;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExceptionResponseModel implements Serializable {

  private String errCode;
  private String err;
  private String errDetails;
  private Object additionalInfo;
  private Date timestamp;
}

