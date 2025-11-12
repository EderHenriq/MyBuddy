package com.Mybuddy.Myb.Security.jwt;

import com.Mybuddy.Myb.Security.jwt.UserDetailsServiceImpl; // Importação adicionada
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Injeção de UserDetailsServiceImpl

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("AuthTokenFilter: Processando requisição para URI: {}", requestURI); // NOVO LOG AQUI!

        try {
            String jwt = parseJwt(request);

            System.out.println("DEBUG (AuthTokenFilter): Token recebido no backend: " + jwt);


            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // --- ALTERAÇÃO CRÍTICA AQUI ---
                // Carregar a instância completa de UserDetailsImpl usando o userDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // As autoridades já estão contidas no objeto userDetails que acabamos de carregar
                // List<GrantedAuthority> authorities = (List<GrantedAuthority>) userDetails.getAuthorities(); // Já carregado

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, // <--- AGORA ESTAMOS PASSANDO O OBJETO UserDetailsImpl COMPLETO
                        null,
                        userDetails.getAuthorities() // Pegando as autoridades diretamente do userDetails carregado
                );

                // --- LOG CRÍTICO AQUI ---
                System.out.println("DEBUG (AuthTokenFilter): Autenticação criada para username: " + userDetails.getUsername() + " com roles: " + userDetails.getAuthorities());


                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // --- OUTRO LOG CRÍTICO AQUI ---
                System.out.println("DEBUG (AuthTokenFilter): SecurityContextHolder setado para: " + SecurityContextHolder.getContext().getAuthentication().getName());
                System.out.println("DEBUG (AuthTokenFilter): Principal no SecurityContextHolder é do tipo: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().getName()); // Novo log para verificar o tipo
            }
        } catch (Exception e) {
            logger.error("Não foi possível autenticar o usuário via JWT: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}