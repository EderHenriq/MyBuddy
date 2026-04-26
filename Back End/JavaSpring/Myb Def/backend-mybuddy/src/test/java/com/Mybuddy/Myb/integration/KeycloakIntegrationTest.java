package com.Mybuddy.Myb.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class KeycloakIntegrationTest {

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.5.5")
            .withRealmImportFile("realm-export.json");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/mybuddy");
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/mybuddy");
    }

    @Autowired
    private MockMvc mockMvc;

    private static String accessToken;

    @BeforeAll
    static void setUp() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "mybuddy-frontend");
        form.add("username", "davi");
        form.add("password", "Mybuddy@1");

        String tokenUrl = keycloak.getAuthServerUrl() + "/realms/mybuddy/protocol/openid-connect/token";

        Map<?, ?> response = restTemplate.postForObject(
                tokenUrl,
                new HttpEntity<>(form, headers),
                Map.class
        );

        accessToken = (String) response.get("access_token");
    }

    @Test
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar200ComTokenValido() throws Exception {
        mockMvc.perform(get("/api/pets")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar403QuandoUserSemRoleOngTentaCriarPet() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Rex",
                                    "especie": "CAO",
                                    "raca": "Labrador",
                                    "idade": 2,
                                    "porte": "GRANDE",
                                    "cor": "Amarelo",
                                    "sexo": "M",
                                    "castrado": true,
                                    "vacinado": true,
                                    "microchipado": false,
                                    "statusAdocao": "DISPONIVEL",
                                    "organizacaoId": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }
}