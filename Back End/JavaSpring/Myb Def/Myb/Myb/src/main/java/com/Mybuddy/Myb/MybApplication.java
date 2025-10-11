package com.Mybuddy.Myb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class MybApplication {

	@Value("${file.upload-dir}")
	private String uploadDir;

	public static void main(String[] args) {
		SpringApplication.run(MybApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				// PRIMEIRO MAPEAMENTO: Geral para /api/**
				registry.addMapping("/api/**")
						.allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500", "null") // <-- CORRIGIDO!
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);

				// SEGUNDO MAPEAMENTO: Para uploads
				registry.addMapping("/uploads/**")
						.allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500", "null") // <-- CORRIGIDO!
						.allowedMethods("GET", "HEAD")
						.allowedHeaders("*");

				// TERCEIRO MAPEAMENTO: Para /api/auth/** (Este já estava corrigido no nosso último ajuste)
				registry.addMapping("/api/auth/**")
						.allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500", "null") // <-- CORRIGIDO!
						.allowedMethods("POST", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/uploads/**")
						.addResourceLocations("file:" + uploadDir + "/");
			}
		};
	}
}