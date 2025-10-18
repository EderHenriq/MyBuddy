package com.Mybuddy.Myb.Security.jwt;
// Pacote onde o filtro JWT está localizado, responsável por autenticação e validação de tokens

import com.Mybuddy.Myb.Security.jwt.UserDetailsServiceImpl; // Importa a implementação de UserDetailsService
import jakarta.servlet.FilterChain; // Importa FilterChain para continuar o processamento da requisição
import jakarta.servlet.ServletException; // Exceção lançada em problemas de servlet
import jakarta.servlet.http.HttpServletRequest; // Representa a requisição HTTP
import jakarta.servlet.http.HttpServletResponse; // Representa a resposta HTTP
import org.slf4j.Logger; // Importa Logger para registrar logs
import org.slf4j.LoggerFactory; // Fábrica de loggers
import org.springframework.beans.factory.annotation.Autowired; // Para injeção de dependências
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Token de autenticação do Spring
import org.springframework.security.core.context.SecurityContextHolder; // Guarda informações de autenticação no contexto
import org.springframework.security.core.userdetails.UserDetails; // Representa os detalhes do usuário
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Para construir detalhes de autenticação
import org.springframework.stereotype.Component; // Marca a classe como componente Spring
import org.springframework.web.filter.OncePerRequestFilter; // Filtro que executa uma vez por requisição

import java.io.IOException; // Exceção de I/O

@Component // Marca a classe como um componente gerenciável pelo Spring
public class AuthTokenFilter extends OncePerRequestFilter {
    // Filtro que intercepta cada requisição HTTP e valida o token JWT

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    // Logger para registrar informações e erros durante a autenticação

    @Autowired // Injeta automaticamente o JwtUtils
    private JwtUtils jwtUtils;
    // Classe utilitária para gerar, validar e extrair informações do JWT

    @Autowired // Injeta automaticamente o serviço de usuários
    private UserDetailsServiceImpl userDetailsService;
    // Serviço que carrega informações do usuário a partir do banco ou outro provedor

    // Método principal do filtro que é executado a cada requisição HTTP
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("Processing request for URI: {}", requestURI);
        // Registra a URI da requisição para depuração

        try {
            String jwt = parseJwt(request);
            // Extrai o token JWT do cabeçalho Authorization

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Valida se o token existe e é válido

                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                // Obtém o nome de usuário presente no token

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // Carrega os detalhes do usuário pelo username

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // Cria um token de autenticação do Spring com as credenciais e permissões do usuário

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Adiciona detalhes extras da requisição ao token de autenticação

                SecurityContextHolder.getContext().setAuthentication(authentication);
                // Armazena a autenticação no contexto de segurança do Spring
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
            // Registra qualquer erro que ocorra durante o processo de autenticação
        }

        filterChain.doFilter(request, response);
        // Continua o processamento da requisição, passando para o próximo filtro
    }

    // Método auxiliar para extrair o token JWT do cabeçalho Authorization
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            // Se o cabeçalho começar com "Bearer ", retorna apenas o token, removendo o prefixo
            return headerAuth.substring(7);
        }

        return null; // Retorna null se não houver token válido
    }
}
