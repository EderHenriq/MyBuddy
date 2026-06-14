package com.Mybuddy.Myb.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost:8080/realms/mybuddy}")
    private String issuerUri;

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "keycloak";

        return new OpenAPI()
                .info(new Info()
                        .title("MyBuddy API")
                        .description("API REST da plataforma MyBuddy — ecossistema de adoção e cuidados pet")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Time MyBuddy")
                                .url("https://github.com/EderHenriq/MyBuddy")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(issuerUri + "/protocol/openid-connect/auth")
                                                .tokenUrl(issuerUri + "/protocol/openid-connect/token")))));
    }
}
