package com.deviceshop.customer.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.deviceshop.customer.storage")
@EntityScan(basePackages = "com.deviceshop.customer.models")
public class JpaConfig {
}