package com.laft.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Aplicación principal del microservicio Account Service
 */
@SpringBootApplication(scanBasePackages = {
    "com.laft.account",
    "com.laft.common",
    "com.laft.shared"
})
@EntityScan(basePackages = "com.laft.shared.domain.model")
@EnableJpaRepositories(basePackages = "com.laft.shared.domain.repository")
@EnableKafka
public class AccountServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
