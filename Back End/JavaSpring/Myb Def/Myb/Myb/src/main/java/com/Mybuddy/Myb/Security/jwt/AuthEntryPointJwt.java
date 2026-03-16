package com.Mybuddy.Myb.Security.jwt;

import com.Mybuddy.Myb.Controller.OrganizacaoController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(OrganizacaoController.class);

    @Override // Indica que este método está sobrescrevendo um método da interface AuthenticationEntryPoint.
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // Este método é chamado sempre que um usuário não autenticado tenta acessar um recurso protegido.


        // Registra uma mensagem de erro no log, indicando que ocorreu um erro de não autorizado e o motivo.
        logger.error("Unauthorized error: {}", authException.getMessage());

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}