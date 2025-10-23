package com.Mybuddy.Myb.Security.jwt; // Pacote onde está localizado o filtro JWT, responsável por autenticação e autorização de requisições REST

import jakarta.servlet.FilterChain; // Interface que permite continuar o processamento dos filtros da requisição
import jakarta.servlet.ServletException; // Exceção para erros de servlet
import jakarta.servlet.http.HttpServletRequest; // Representa a requisição HTTP recebida pelo servidor
import jakarta.servlet.http.HttpServletResponse; // Representa a resposta HTTP que será enviada ao cliente
import org.slf4j.Logger; // Interface de log para registrar eventos e erros
import org.slf4j.LoggerFactory; // Fábrica de instâncias Logger
import org.springframework.beans.factory.annotation.Autowired; // Permite injeção de dependências no Spring
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Token de autenticação padrão do Spring Security
import org.springframework.security.core.GrantedAuthority; // Representa uma permissão/perfil do usuário
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Implementação simples de GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder; // Armazena as informações de autenticação da requisição
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Constrói detalhes adicionais da requisição
import org.springframework.stereotype.Component; // Marca a classe como um componente Spring
import org.springframework.web.filter.OncePerRequestFilter; // Filtro padrão que garante execução única por requisição

import java.io.IOException; // Exceção para problemas de I/O
import java.util.List; // Coleção ordenada (para roles extraídas do token)
import java.util.stream.Collectors; // Utilitário para transformar listas/streams

/**
 * Filtro de autenticação JWT.
 * Intercepta cada requisição HTTP, verifica a presença e validade do token JWT,
 * extrai username e roles do token, e popula o contexto de segurança do Spring
 * para que endpoints anotados com @PreAuthorize funcionem de acordo com as roles presentes no token.
 */
@Component // Indica que esta classe é um componente gerenciado pelo Spring
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    // Logger para registrar informações e erros durante o processo de autenticação

    @Autowired // Injeta o utilitário de operações JWT
    private JwtUtils jwtUtils;

    /**
     * Método principal.
     * Executa uma vez por requisição, sendo responsável por:
     * - Extrair o token JWT do header "Authorization"
     * - Validar o token (estrutura, assinatura e expiração)
     * - Extrair username (subject) e roles (claim "roles") do token
     * - Transformar roles em authorities (objeto reconhecido pelo Spring Security)
     * - Popular o contexto de segurança do Spring para permitir autorização via roles (ex: @PreAuthorize)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("Processando requisição para URI: {}", requestURI);
        // Registra a URI atual em log para debugar fluxo de autenticação

        try {
            // Extrai o token JWT do header Authorization (se presente)
            String jwt = parseJwt(request);

            System.out.println("Token recebido no backend: " + jwt);


            // Se o token está presente E é validado pelo JwtUtils (assinatura, expiração, formato correto)
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                // Extrai o nome de usuário do claim "subject" do token JWT
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Extrai lista de roles do claim "roles" do token JWT — fundamental para autorizações por perfil
                List<String> roles = jwtUtils.getRolesFromJwtToken(jwt);

                // Transforma cada role String (ex: "ROLE_ADMIN") em objeto SimpleGrantedAuthority reconhecido pelo Spring Security
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Cria o token de autenticação Spring usando username e lista de authorities extraídas do JWT
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, // principal (usuário autenticado)
                        null,     // credenciais (não utilizadas após login JWT)
                        authorities // lista de permissões/perfis para @PreAuthorize
                );

                // Adiciona detalhes extras sobre a requisição (IP, etc)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Popula o contexto de segurança do Spring Security, permitindo identificar usuário e roles em toda a requisição
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Não foi possível autenticar o usuário via JWT: {}", e);
            // Registra falhas de autenticação em log para auditoria/revisão
        }

        // Continua o processamento da requisição (próximo filtro, controlador REST etc)
        filterChain.doFilter(request, response);
    }

    /**
     * Método auxiliar para extrair o token JWT do header Authorization da requisição HTTP.
     * Exemplo esperado de header: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
     * @return String do token JWT se encontrado e no formato correto, ou null se ausente/incorreto
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        // Verifica se o header existe e começa com "Bearer "
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            // Retira o prefixo "Bearer " e retorna apenas o token
            return headerAuth.substring(7);
        }

        return null; // Retorna null caso não exista header ou esteja em formato inválido
    }
}
