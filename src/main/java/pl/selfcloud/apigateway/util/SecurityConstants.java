package pl.selfcloud.apigateway.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SecurityConstants {

  HEADER("HEADER"), ISSUER("ISSUER"), KEY("KEY"), PREFIX("PREFIX"), AUTHORIZATION("AUTHORIZATION");

  final String value;

}