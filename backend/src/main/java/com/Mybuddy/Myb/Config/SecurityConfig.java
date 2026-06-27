package com.Mybuddy.Myb.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

import com.Mybuddy.Myb.Security.JwtAuthConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/payments/preference/**").permitAll()
                        .requestMatchers("/api/payments/webhook").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/produtos/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categorias/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/petshop/**").permitAll()
                        .anyRequest().authenticated())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));

        return http.build();
    }

        @Bean
        public PasswordEncoder passwordEncoder() {

                return new BCryptPasswordEncoder();

        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                
                CorsConfiguration configuration = new CorsConfiguration();

                String corsOrigins = System.getenv("CORS_ALLOWED_RIGINS");
                if (corsOrigins != null && !corsOrigins.isBlank()) {
                        configuration.setAllowedOrigins(List.of(corsOrigins.split(",")));
                }else {
                   configuration.setAllowedOrigins(List.of(
                        "http://localhost:4200",
                        "http://localhost:80",
                        "http://localhost"));
                }
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}