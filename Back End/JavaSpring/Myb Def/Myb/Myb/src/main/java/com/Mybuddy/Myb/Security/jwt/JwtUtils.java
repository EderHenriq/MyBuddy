package com.Mybuddy.Myb.Security.jwt; // Declara o pacote onde esta classe utilitária JWT está localizada.

import io.jsonwebtoken.*; // Importa classes base do JJWT (Java JWT)
import io.jsonwebtoken.io.Decoders; // Importa utilitário para decodificação BASE64
import io.jsonwebtoken.security.Keys; // Importa utilitário para geração de chaves seguras
import org.slf4j.Logger; // Importa Logger para registro de logs
import org.slf4j.LoggerFactory; // Importa LoggerFactory para obter instâncias de Logger
import org.springframework.beans.factory.annotation.Value; // Importa anotação para injetar valores de propriedades
import org.springframework.security.core.Authentication; // Importa a interface Authentication do Spring Security
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component; // Importa a anotação @Component do Spring

import java.security.Key; // Importa a interface Key para representação de chaves criptográficas
import java.util.Date; // Importa a classe Date para lidar com datas e horas
import java.util.List; // Importa List para coleções ordenadas.
import java.util.stream.Collectors; // Importa Collectors para operações com Streams.
import io.jsonwebtoken.Claims;

@Component // Indica que esta classe é um componente gerenciado pelo Spring.
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Chave secreta usada para assinar/verificar o JWT, definida em application.properties.
    @Value("${mybuddy.app.jwtSecret}")
    private String jwtSecret;

    // Tempo de expiração do JWT em milissegundos, definida em application.properties.
    @Value("${mybuddy.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Gera um token JWT para o usuário autenticado, incluindo as roles/perfis como claims no payload.
     * Isso garante que o backend consiga autorizar endpoints protegidos via roles Spring Security.
     * @param authentication Objeto de autenticação do usuário, obtido do contexto do Spring Security.
     * @return String JWT gerada e assinada.
     */
    public String generateJwtToken(Authentication authentication) {

        // Obtém os detalhes do usuário autenticado.
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        // Extrai todas as roles/authorities do usuário.
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Transforma cada GrantedAuthority em String (ex: "ROLE_ADMIN").
                .collect(Collectors.toList());

        // Constrói e assina o token JWT, incluindo:
        // - subject: identifica o usuário (normalmente e-mail)
        // - roles: lista de papéis/perfis, usada para autorização backend
        // - issuedAt/expiration: datas de emissão e validade
        // - assinatura via chave secreta configurada
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Define o usuário logado como subject.
                .claim("roles", roles) // Insere as roles como claim no payload do JWT.
                .setIssuedAt(new Date()) // Data de emissão do token.
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Data de expiração do token.
                .signWith(key(), SignatureAlgorithm.HS256) // Assina com o algoritmo e chave secreta.
                .compact(); // Retorna o token compactado como String.
    }

    // Método auxiliar privado para obter a chave de segurança para assinatura/verificação do JWT.
    private Key key() {
        // Decodifica a string 'jwtSecret' (que está em Base64) e gera uma chave HMAC SHA para ser usada com JJWT.
        System.out.println("jwtSecret lido pelo Spring: [" + jwtSecret + "]");
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
    }

    // Extrai o username (subject) de um token JWT válido.
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Valida o token JWT quanto à assinatura, formato e expiração.
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public List<String> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        return claims.get("roles", List.class);
    }

}
