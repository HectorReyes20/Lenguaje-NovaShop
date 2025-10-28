package com.example.novashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@SpringBootApplication
@EnableJpaAuditing

public class NovashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovashopApplication.class, args);
		System.out.println("\n" +
				"╔══════════════════════════════════════════════════════════╗\n" +
				"║                                                          ║\n" +
				"║            🛍️  NovaShop Applicacion Inicio  🛍️          ║\n" +
				"║                                                          ║\n" +
				"║  URL: http://localhost:8080                              ║\n" +
				"║  Actuator: http://localhost:8080/actuator                ║\n" +
				"║                                                          ║\n" +
				"╚══════════════════════════════════════════════════════════╝\n");
	}
}
