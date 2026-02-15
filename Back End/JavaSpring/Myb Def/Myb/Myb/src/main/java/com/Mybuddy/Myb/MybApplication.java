package com.Mybuddy.Myb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MybApplication {

	@Value("${file.upload-dir}")
	private String uploadDir;

	public static void main(String[] args) { // Método principal que inicia a aplicação Spring Boot
		SpringApplication.run(MybApplication.class, args);
	}

	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

            @Override
			public void addCorsMappings(CorsRegistry registry) {

                // --- Configuração de CORS para rotas da API ---
				// Permite que diferentes origens acessem a API
				registry.addMapping("/api/**")
						.allowedOrigins(
								"null", // Permite chamadas vindas de arquivos locais (ex: file://)
								"http://localhost:5500", // Origem comum usada pelo Live Server do VSCode
								"http://127.0.0.1:5500", // Alternativa ao localhost
								"http://localhost:8080" // Origem do próprio backend (boa prática incluir)
						)
						// Define os métodos HTTP permitidos
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						// Permite qualquer cabeçalho nas requisições
						.allowedHeaders("*")
						// Permite o envio de cookies e credenciais
						.allowCredentials(true);

				// --- Configuração de CORS para acesso aos uploads ---
				registry.addMapping("/uploads/**")
						.allowedOrigins(
								"null", // Permite acesso local (file://)
								"http://localhost:5500",
								"http://127.0.0.1:5500",
								"http://localhost:8080"
						)
						// Apenas leitura
						.allowedMethods("GET", "HEAD")
						.allowedHeaders("*")
						// Como uploads são acessos públicos, geralmente não é necessário enviar credenciais
						.allowCredentials(false);
			}

			// --- Configuração de mapeamento de arquivos estáticos (uploads) ---
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/uploads/**")
						.addResourceLocations("file:" + uploadDir + "/");
			}

            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/api/").setViewName("forward:/error"); // Ou apenas uma string vazia
                registry.addViewController("/api/**").setViewName("forward:/error"); // Pode ser necessário para pegar subcaminhos
            }
		};
	}
}
