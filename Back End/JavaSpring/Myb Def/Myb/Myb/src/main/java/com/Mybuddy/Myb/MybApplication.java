package com.Mybuddy.Myb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MybApplication {

	@Value("${file.upload-dir}") // Injeta o valor configurado no arquivo application.properties (por exemplo, file.upload-dir=uploads)
	private String uploadDir;

	public static void main(String[] args) { // Método principal que inicia a aplicação Spring Boot
		SpringApplication.run(MybApplication.class, args);
	}

	
	@Bean // Bean que configura o CORS (Cross-Origin Resource Sharing) e o acesso a arquivos estáticos
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

            @Override
			public void addCorsMappings(CorsRegistry registry) {

                // --- Configuração de CORS para rotas da API ---
				// Permite que diferentes origens acessem a API (útil para comunicação entre frontend e backend)
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
				// Controla quem pode acessar os arquivos estáticos em /uploads/**
				registry.addMapping("/uploads/**")
						.allowedOrigins(
								"null", // Permite acesso local (file://)
								"http://localhost:5500",
								"http://127.0.0.1:5500",
								"http://localhost:8080"
						)
						// Apenas leitura (não permite POST ou DELETE diretamente aqui)
						.allowedMethods("GET", "HEAD")
						.allowedHeaders("*")
						// Como uploads são acessos públicos, geralmente não é necessário enviar credenciais
						.allowCredentials(false);
			}

			// --- Configuração de mapeamento de arquivos estáticos (uploads) ---
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				// Mapeia qualquer URL que comece com /uploads/** para a pasta física configurada em uploadDir
				registry.addResourceHandler("/uploads/**")
						.addResourceLocations("file:" + uploadDir + "/");
				// Exemplo: se uploadDir = "C:/meus_arquivos",
				// acessar http://localhost:8080/uploads/foto.jpg servirá o arquivo físico C:/meus_arquivos/foto.jpg
			}
		};
	}
}
