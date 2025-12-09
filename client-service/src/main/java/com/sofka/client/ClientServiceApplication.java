package com.laft.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Aplicación principal del microservicio Client Service
 */
@SpringBootApplication(scanBasePackages = {
    "com.laft.client",
    "com.laft.common",
    "com.laft.shared"
})
@EntityScan(basePackages = "com.laft.shared.domain.model")
@EnableJpaRepositories(basePackages = "com.laft.shared.domain.repository")
@EnableKafka
public class ClientServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ClientServiceApplication.class, args);
    }
}
