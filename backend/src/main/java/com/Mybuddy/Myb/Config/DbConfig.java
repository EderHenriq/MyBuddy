package com.Mybuddy.Myb.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuração de banco de dados híbrido (Dual-Database).
 * Define explicitamente quais pacotes contêm repositórios JPA (PostgreSQL) e quais contêm repositórios MongoDB.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.Mybuddy.Myb.Repository.jpa")
@EnableMongoRepositories(basePackages = "com.Mybuddy.Myb.Repository.mongo")
@org.springframework.data.mongodb.config.EnableMongoAuditing
public class DbConfig {
}
