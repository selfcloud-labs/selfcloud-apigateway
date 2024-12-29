FROM eclipse-temurin:17-jre-alpine
COPY target/selfcloud-apigateway-1.0.0.jar selfcloud-apigateway-1.0.0.jar
EXPOSE 8099
ENTRYPOINT ["java","-jar","/selfcloud-apigateway-1.0.0.jar"]