package com.Mybuddy.Myb.Config;

// Importações necessárias para configurar segurança, autenticação e filtros JWT
import com.Mybuddy.Myb.Security.jwt.AuthEntryPointJwt;
import com.Mybuddy.Myb.Security.jwt.AuthTokenFilter;
import com.Mybuddy.Myb.Security.jwt.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;


// Indica que esta classe contém configurações do Spring Boot
@Configuration
// Ativa as configurações de segurança da Web
@EnableWebSecurity
// Permite o uso de anotações de segurança em métodos
@EnableMethodSecurity
public class SecurityConfig {

    // Injeção do serviço responsável por carregar os detalhes do usuário
    private final UserDetailsServiceImpl userDetailsService;

    // Manipulador para exceções de autenticação não autorizada (401)
    private final AuthEntryPointJwt unauthorizedHandler;

    // Injetar o filtro AuthTokenFilter
    private final AuthTokenFilter authTokenFilter; // Adicionado aqui

    // Construtor que injeta as dependências necessárias
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler, AuthTokenFilter authTokenFilter) { // Adicionado aqui
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.authTokenFilter = authTokenFilter; // Atribuído aqui
    }

    // Bean que define o tipo de criptografia de senhas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean responsável por autenticar usuários com base nos dados do banco
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Define qual serviço será usado para buscar o usuário
        authProvider.setUserDetailsService(userDetailsService);
        // Define o tipo de criptografia que será usada nas senhas
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Bean que expõe o AuthenticationManager, responsável por autenticar as credenciais do usuário
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // Removido AuthTokenFilter da assinatura para usar o campo injetado
        // --- Libera CORS global usando configuração do WebMvcConfigurer ---
        http.cors(withDefaults())

                // Desativa o CSRF (não necessário para APIs REST)
                .csrf(csrf -> csrf.disable())

                // Define o tratamento de exceções de autenticação
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))

                // Define que a aplicação não manterá sessão (autenticação será stateless via token)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define as regras de autorização das rotas
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // Endpoints de autenticação são públicos
                                // Permite acesso irrestrito ao diretório /uploads/** para servir imagens estáticas
                                .requestMatchers("/uploads/**").permitAll()
                                // ADICIONADO: Especifica que o endpoint do adotante é uma API e requer autenticação
                                .requestMatchers("/api/interesses/usuarios/me/interesses").permitAll() // <-- CORREÇÃO CRÍTICA AQUI
                                // --- ADIÇÃO PARA H2 CONSOLE ---
                                .requestMatchers("/h2-console/**").permitAll() // Permite acesso ao console do H2
                                // -----------------------------
                                .anyRequest().authenticated() // Todas as outras rotas exigem autenticação
                );

        // --- ADIÇÃO PARA H2 CONSOLE (corrige problemas de iframe) ---
        // Permite que o H2 Console rode em um iframe na mesma origem (necessário para funcionar no navegador)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        // -----------------------------------------------------------

        // Registra o provedor de autenticação configurado acima
        http.authenticationProvider(authenticationProvider());

        // Adiciona o filtro JWT antes do filtro padrão de autenticação por usuário/senha
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class); // Usando o campo injetado

        // Constrói e retorna a configuração final de segurança
        return http.build();
    }
}