package com.Mybuddy.Myb.Security.jwt; // Declara o pacote onde esta classe utilitária JWT está localizada.

// Importações necessárias para trabalhar com JWT (JSON Web Tokens)
import io.jsonwebtoken.*; // Importa classes base do JJWT (Java JWT)
import io.jsonwebtoken.io.Decoders; // Importa utilitário para decodificação BASE64
import io.jsonwebtoken.security.Keys; // Importa utilitário para geração de chaves seguras
import org.slf4j.Logger; // Importa Logger para registro de logs
import org.slf4j.LoggerFactory; // Importa LoggerFactory para obter instâncias de Logger
import org.springframework.beans.factory.annotation.Value; // Importa anotação para injetar valores de propriedades
import org.springframework.security.core.Authentication; // Importa a interface Authentication do Spring Security
import org.springframework.stereotype.Component; // Importa a anotação @Component do Spring

import java.security.Key; // Importa a interface Key para representação de chaves criptográficas
import java.util.Date; // Importa a classe Date para lidar com datas e horas

// Anotação @Component do Spring, que marca esta classe como um componente gerenciado pelo Spring.
// Isso significa que o Spring pode detectar e instanciar esta classe automaticamente.
@Component
public class JwtUtils {
    // Declara a classe JwtUtils, uma classe utilitária para operações relacionadas a JWTs,
    // como gerar, validar e extrair informações de tokens.

    // Cria uma instância de Logger para registrar mensagens de log nesta classe.
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Injeta o valor da propriedade 'mybuddy.app.jwtSecret' do arquivo application.properties (ou similar).
    // Esta é a chave secreta usada para assinar (criptografar) e verificar os tokens JWT.
    @Value("${mybuddy.app.jwtSecret}")
    private String jwtSecret;

    // Injeta o valor da propriedade 'mybuddy.app.jwtExpirationMs' do arquivo application.properties (ou similar).
    // Este valor define o tempo de expiração do token JWT em milissegundos.
    @Value("${mybuddy.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Método para gerar um token JWT para um usuário autenticado.
    public String generateJwtToken(Authentication authentication) {

        // Obtém o objeto UserDetailsImpl (que contém os detalhes do usuário autenticado) do objeto Authentication.
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        // Constrói o token JWT usando a API JJWT.
        return Jwts.builder()
                // Define o "subject" (assunto) do token como o nome de usuário (e-mail) do usuário autenticado.
                .setSubject((userPrincipal.getUsername()))
                // Define a data de emissão do token como a data e hora atual.
                .setIssuedAt(new Date())
                // Define a data de expiração do token, calculando a partir da data atual mais o tempo de expiração configurado.
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                // Assina o token usando a chave secreta (obtida pelo método key()) e o algoritmo HS256.
                .signWith(key(), SignatureAlgorithm.HS256)
                // Compacta o token em sua representação final de string.
                .compact();
    }

    // Método auxiliar privado para obter a chave de segurança para assinatura/verificação do JWT.
    private Key key() {
        // Decodifica a string 'jwtSecret' (que está em Base64) e gera uma chave HMAC SHA para ser usada com JJWT.
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Método para extrair o nome de usuário (subject) de um token JWT.
    public String getUserNameFromJwtToken(String token) {
        // Analisa o token usando a chave de assinatura e retorna o "subject" (nome de usuário).
        return Jwts.parserBuilder().setSigningKey(key()).build() // Constrói um parser JWT configurado com a chave de assinatura.
                .parseClaimsJws(token).getBody().getSubject(); // Analisa o token, obtém o corpo (claims) e extrai o subject.
    }

    // Método para validar um token JWT.
    public boolean validateJwtToken(String authToken) {
        try {
            // Tenta analisar o token usando a chave de assinatura. Se for bem-sucedido, o token é válido.
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true; // Token válido.
        } catch (MalformedJwtException e) {
            // Captura exceção se o token JWT estiver malformado (não é um JWT válido).
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // Captura exceção se o token JWT estiver expirado.
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // Captura exceção se o token JWT não for suportado pela biblioteca JJWT.
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Captura exceção se o token JWT for nulo, vazio ou tiver claims em formato inválido.
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false; // Token inválido por alguma das razões acima.
    }
}