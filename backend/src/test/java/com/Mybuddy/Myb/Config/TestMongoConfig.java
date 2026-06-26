package com.Mybuddy.Myb.Config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;

/**
 * Fornece um MongoTemplate mockado para testes @SpringBootTest sem MongoDB real.
 *
 * Usa @Configuration (não @TestConfiguration) para que @ConditionalOnMissingBean
 * da autoconfiguração detecte o bean e evite criar um MongoTemplate real que
 * tentaria conectar ao localhost:27017.
 *
 * O converter real (sem conexão) é necessário para que o MongoRepositoryFactoryBean
 * consiga introspectar entidades via reflexão durante a inicialização do contexto.
 * O simpleTypeHolder garante que LocalDateTime e outros tipos java.time sejam
 * tratados como tipos simples, evitando introspecção proibida pelo módulo java.base.
 */
@Configuration
public class TestMongoConfig {

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() throws Exception {
        MongoCustomConversions conversions = new MongoCustomConversions(Collections.emptyList());

        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();

        DbRefResolver dbRefResolver = Mockito.mock(DbRefResolver.class);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setCustomConversions(conversions);
        converter.afterPropertiesSet();

        MongoTemplate mockTemplate = Mockito.mock(MongoTemplate.class);
        Mockito.when(mockTemplate.getConverter()).thenReturn(converter);
        return mockTemplate;
    }
}
