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
				// Mapeamento abrangente para /api/**, incluindo "null"
				registry.addMapping("/api/**")
						.allowedOrigins(
								"null", // <<--- ADICIONADO DE VOLTA
								"http://localhost:5500",
								"http://127.0.0.1:5500",
								"http://localhost:8080" // Boa prática incluir sua própria origem também
						)
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);

				// Mapeamento para uploads - Se /uploads/** não exige autenticação/credenciais,
				// allowedCredentials(true) pode não ser necessário, mas se o navegador envia cookies, mantenha.
				// Se este endpoint também pode ser acessado de arquivo local, adicione "null" aqui também.
				registry.addMapping("/uploads/**")
						.allowedOrigins(
								"null", // <<--- ADICIONADO DE VOLTA SE FOR O CASO
								"http://localhost:5500",
								"http://127.0.0.1:5500",
								"http://localhost:8080"
						)
						.allowedMethods("GET", "HEAD")
						.allowedHeaders("*")
						.allowCredentials(false); // Geralmente uploads não precisam de credenciais para serem *acessados* publicamente

				// O mapeamento para /api/auth/** agora é redundante se o /api/** já o cobre.
				// Se você tem requisitos MUITO específicos para auth, pode mantê-lo,
				// mas certifique-se de que "null" esteja lá também.
				// Vou comentar esta parte para simplificar, já que /api/** já a cobre.
             /*
             registry.addMapping("/api/auth/**")
                   .allowedOrigins("null", "http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:8080") // <<--- ADICIONADO DE VOLTA
                   .allowedMethods("POST", "OPTIONS")
                   .allowedHeaders("*")
                   .allowCredentials(true);
             */
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/uploads/**")
						.addResourceLocations("file:" + uploadDir + "/");
			}
		};
	}
}