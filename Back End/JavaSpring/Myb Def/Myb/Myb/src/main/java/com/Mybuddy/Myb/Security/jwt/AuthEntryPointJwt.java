package com.Mybuddy.Myb.Security.jwt; // Declara o pacote onde esta classe de segurança JWT está localizada.

import jakarta.servlet.ServletException; // Importa a exceção ServletException, que pode ser lançada por um servlet.
import jakarta.servlet.http.HttpServletRequest; // Importa a interface HttpServletRequest, que representa a requisição HTTP.
import jakarta.servlet.http.HttpServletResponse; // Importa a interface HttpServletResponse, que representa a resposta HTTP.
import org.slf4j.Logger; // Importa a interface Logger do SLF4J (Simple Logging Facade for Java) para registro de logs.
import org.slf4j.LoggerFactory; // Importa LoggerFactory para obter instâncias de Logger.
import org.springframework.security.core.AuthenticationException; // Importa a exceção AuthenticationException do Spring Security.
import org.springframework.security.web.AuthenticationEntryPoint; // Importa a interface AuthenticationEntryPoint do Spring Security.
import org.springframework.stereotype.Component; // Importa a anotação @Component do Spring.

import java.io.IOException; // Importa a exceção IOException, que pode ocorrer durante operações de entrada/saída.

// Anotação @Component do Spring, que marca esta classe como um componente gerenciado pelo Spring.
// Isso significa que o Spring pode detectar e instanciar esta classe automaticamente.
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    // Declara a classe AuthEntryPointJwt, que implementa a interface AuthenticationEntryPoint.
    // Esta classe é responsável por lidar com tentativas de acesso a recursos protegidos
    // sem autenticação (ou com autenticação inválida), enviando uma resposta de erro.

    // Cria uma instância de Logger para registrar mensagens de log nesta classe.
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override // Indica que este método está sobrescrevendo um método da interface AuthenticationEntryPoint.
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // Este método é chamado sempre que um usuário não autenticado tenta acessar um recurso protegido.
        // request: O objeto HttpServletRequest que causou a falha de autenticação.
        // response: O objeto HttpServletResponse para enviar a resposta de erro.
        // authException: A exceção que detalha a falha de autenticação.

        // Registra uma mensagem de erro no log, indicando que ocorreu um erro de não autorizado e o motivo.
        logger.error("Unauthorized error: {}", authException.getMessage());
        // Envia uma resposta de erro HTTP 401 Unauthorized para o cliente.
        // HttpServletResponse.SC_UNAUTHORIZED é a constante para o código de status 401.
        // "Error: Unauthorized" é a mensagem de erro que será enviada no corpo da resposta.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}