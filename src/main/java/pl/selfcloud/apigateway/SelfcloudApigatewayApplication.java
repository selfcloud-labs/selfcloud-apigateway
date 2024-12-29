package pl.selfcloud.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;



@SpringBootApplication
@EnableDiscoveryClient
public class SelfcloudApigatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SelfcloudApigatewayApplication.class, args);
	}

}
