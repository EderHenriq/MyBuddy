package com.Mybuddy.Myb.Security.jwt;

import com.Mybuddy.Myb.Security.jwt.UserDetailsServiceImpl; // Importação adicionada
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.debug("AuthTokenFilter: Processando requisição para URI: {}", requestURI);

        try {
            String jwt = parseJwt(request);

            logger.debug("AuthTokenFilter: Token recebido no backend: {}", jwt);


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
                logger.debug("AuthTokenFilter: Autenticação criada para username: {} com roles: {}", userDetails.getUsername(), userDetails.getAuthorities());


                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // --- OUTRO LOG CRÍTICO AQUI ---
                if (logger.isDebugEnabled()) {
                    logger.debug("AuthTokenFilter: SecurityContextHolder setado para: {}", SecurityContextHolder.getContext().getAuthentication().getName());
                    logger.debug("AuthTokenFilter: Principal no SecurityContextHolder é do tipo: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().getName());
                }
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