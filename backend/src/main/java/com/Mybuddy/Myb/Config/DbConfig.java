package com.Mybuddy.Myb.Config;

import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

/**
 * Configuração de banco de dados híbrido (Dual-Database).
 * Define explicitamente quais pacotes contêm repositórios JPA (PostgreSQL) e quais contêm repositórios MongoDB.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.Mybuddy.Myb.Repository.jpa")
@EnableMongoRepositories(basePackages = "com.Mybuddy.Myb.Repository.mongo")
@org.springframework.data.mongodb.config.EnableMongoAuditing
public class DbConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(new LegacyRoleReadConverter()));
    }

    @ReadingConverter
    static class LegacyRoleReadConverter implements Converter<Long, Role> {
        @Override
        public Role convert(Long source) {
            return switch (source.intValue()) {
                case 1 -> new Role(ERole.ROLE_ADMIN);
                case 2 -> new Role(ERole.ROLE_ONG);
                case 3 -> new Role(ERole.ROLE_ADOTANTE);
                case 4 -> new Role(ERole.ROLE_PETSHOP);
                default -> throw new IllegalArgumentException("Role legada desconhecida: " + source);
            };
        }
    }
}
