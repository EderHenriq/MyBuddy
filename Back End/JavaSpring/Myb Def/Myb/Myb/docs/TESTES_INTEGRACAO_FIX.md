# Correções dos Testes de Integração - MyBuddy Backend

Este documento descreve os 13 testes de integração que estão falhando e as correções necessárias para cada um.

---

## Resumo Geral

| Controller | Total de Testes | Falhas | Erros | Status |
|------------|-----------------|--------|-------|--------|
| AuthControllerIT | 4 | 2 | 0 | Parcial |
| PetControllerIT | 5 | 0 | 5 | Falhando |
| InteresseAdoacaoControllerIT | 4 | 0 | 4 | Falhando |
| **Total** | **13** | **2** | **9** | - |

---

## 1. AuthControllerIT

### 1.1 Teste: `login_CredenciaisValidas_RetornaToken`

**Erro:**
```
No value at JSON path "$.token"
```

**Causa Raiz:**
O teste busca o campo `$.token` no JSON de resposta, mas a classe `JwtResponse` serializa o campo como `accessToken` (devido ao getter `getAccessToken()`).

**Localização do Problema:**
- `src/main/java/com/Mybuddy/Myb/Payload/Response/JwtResponse.java`
- O campo é `private String token` (linha 6), mas o getter é `getAccessToken()` (linha 26)

**Solução A - Corrigir o Teste (Recomendado):**
```java
// Arquivo: src/test/java/com/Mybuddy/Myb/Controller/AuthControllerIT.java
// Linha 136: Alterar de:
.andExpect(jsonPath("$.token").exists())
// Para:
.andExpect(jsonPath("$.accessToken").exists())
```

**Solução B - Corrigir o DTO (Alternativa):**
```java
// Arquivo: src/main/java/com/Mybuddy/Myb/Payload/Response/JwtResponse.java
// Adicionar getter com nome correto:
public String getToken() {
    return token;
}
```

---

### 1.2 Teste: `login_SenhaIncorreta_RetornaErro`

**Erro:**
```
Status expected:<401> but was:<500>
```

**Causa Raiz:**
Quando as credenciais são inválidas, o `AuthenticationManager` lança uma `BadCredentialsException`. O `GlobalExceptionHandler` não trata essa exceção especificamente, então ela cai no handler genérico que retorna HTTP 500.

**Localização do Problema:**
- `src/main/java/com/Mybuddy/Myb/Exception/Handler/GlobalExceptionHandler.java`

**Solução - Adicionar Handler para BadCredentialsException:**
```java
// Arquivo: src/main/java/com/Mybuddy/Myb/Exception/Handler/GlobalExceptionHandler.java
// Adicionar import:
import org.springframework.security.authentication.BadCredentialsException;

// Adicionar novo handler:
@ExceptionHandler(BadCredentialsException.class)
public ResponseEntity<ErrorDetails> handleBadCredentialsException(
        BadCredentialsException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            "Credenciais inválidas",
            request.getDescription(false)
    );
    logger.error("Tentativa de login com credenciais inválidas: {}", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED); // 401
}
```

**Alternativa - Usar AuthenticationEntryPoint:**
Configurar um `AuthenticationEntryPoint` customizado no `SecurityConfig` para retornar 401 em falhas de autenticação.

---

## 2. PetControllerIT

### 2.1 Todos os 5 Testes Falhando

**Testes Afetados:**
1. `criarPet_ComoOng_RetornaCriado`
2. `buscarPets_Autenticado_RetornaLista`
3. `buscarPetPorId_PetExiste_RetornaPet`
4. `buscarPetPorId_PetNaoExiste_Retorna404`
5. `deletarPet_ComoAdmin_DeletaComSucesso`

**Causa Raiz:**
Os tokens JWT gerados no `@BeforeEach` não são válidos. O problema está na forma como os tokens são gerados:

```java
// Código atual (problemático):
tokenOng = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
    usuarioOng.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_ONG"))));
```

O `JwtUtils.generateJwtToken()` provavelmente espera um `UserDetailsImpl` no principal do `Authentication`, mas está recebendo apenas uma String (email).

**Localização do Problema:**
- `src/test/java/com/Mybuddy/Myb/Controller/PetControllerIT.java` (linhas 123-127)

**Solução - Criar UserDetailsImpl Correto:**
```java
// Arquivo: src/test/java/com/Mybuddy/Myb/Controller/PetControllerIT.java
// Substituir a geração de tokens no setUp():

// Antes (problemático):
tokenOng = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
    usuarioOng.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_ONG"))));

// Depois (correto):
UserDetailsImpl userDetailsOng = new UserDetailsImpl(
    usuarioOng.getId(),
    usuarioOng.getNome(),
    usuarioOng.getEmail(),
    usuarioOng.getPassword(),
    List.of(new SimpleGrantedAuthority("ROLE_ONG")),
    organizacao.getId()
);

tokenOng = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
    userDetailsOng, null, userDetailsOng.getAuthorities()));

// Repetir para tokenAdmin:
UserDetailsImpl userDetailsAdmin = new UserDetailsImpl(
    usuarioAdmin.getId(),
    usuarioAdmin.getNome(),
    usuarioAdmin.getEmail(),
    usuarioAdmin.getPassword(),
    List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
    null // Admin não tem organização
);

tokenAdmin = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
    userDetailsAdmin, null, userDetailsAdmin.getAuthorities()));
```

**Import Necessário:**
```java
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
```

---

## 3. InteresseAdoacaoControllerIT

### 3.1 Todos os 4 Testes Falhando

**Testes Afetados:**
1. `registrarInteresse_ComDadosValidos_RetornaCriado`
2. `atualizarStatus_OngAprova_RetornaAprovado`
3. `listarMeusInteresses_ComoAdotante_RetornaLista`
4. `listarInteressesOng_ComoOng_RetornaLista`

**Causa Raiz:**
Mesmo problema do `PetControllerIT` - tokens JWT gerados incorretamente.

**Localização do Problema:**
- `src/test/java/com/Mybuddy/Myb/Controller/InteresseAdocaoControllerIT.java` (linhas 131-135)

**Solução:**
```java
// Arquivo: src/test/java/com/Mybuddy/Myb/Controller/InteresseAdocaoControllerIT.java
// Substituir a geração de tokens no setUp():

// Gerar token para Adotante:
UserDetailsImpl userDetailsAdotante = new UserDetailsImpl(
    usuarioAdotante.getId(),
    usuarioAdotante.getNome(),
    usuarioAdotante.getEmail(),
    usuarioAdotante.getPassword(),
    List.of(new SimpleGrantedAuthority("ROLE_ADOTANTE")),
    null
);

tokenAdotante = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
    userDetailsAdotante, null, userDetailsAdotante.getAuthorities()));

// Gerar token para ONG:
UserDetailsImpl userDetailsOng = new UserDetailsImpl(
    usuarioOng.getId(),
    usuarioOng.getNome(),
    usuarioOng.getEmail(),
    usuarioOng.getPassword(),
    List.of(new SimpleGrantedAuthority("ROLE_ONG")),
    organizacao.getId()
);

tokenOng = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
    userDetailsOng, null, userDetailsOng.getAuthorities()));
```

**Import Necessário:**
```java
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
```

---

## 4. Resumo das Correções

### Arquivos a Modificar:

| # | Arquivo | Tipo de Correção |
|---|---------|------------------|
| 1 | `AuthControllerIT.java` | Alterar `$.token` para `$.accessToken` |
| 2 | `GlobalExceptionHandler.java` | Adicionar handler para `BadCredentialsException` |
| 3 | `PetControllerIT.java` | Corrigir geração de tokens JWT |
| 4 | `InteresseAdocaoControllerIT.java` | Corrigir geração de tokens JWT |

### Ordem de Implementação Recomendada:

1. **Passo 1:** Corrigir `GlobalExceptionHandler.java` (afeta múltiplos testes)
2. **Passo 2:** Corrigir `AuthControllerIT.java` (fix simples)
3. **Passo 3:** Corrigir `PetControllerIT.java` (geração de tokens)
4. **Passo 4:** Corrigir `InteresseAdocaoControllerIT.java` (geração de tokens)
5. **Passo 5:** Executar testes para validar correções

---

## 5. Código Completo das Correções

### 5.1 GlobalExceptionHandler.java - Adicionar Handler

```java
// Adicionar após os imports existentes:
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

// Adicionar dentro da classe, antes do handleGlobalException:

/**
 * Manipula exceções de autenticação inválida (credenciais incorretas)
 */
@ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
public ResponseEntity<ErrorDetails> handleAuthenticationException(
        Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            "Credenciais inválidas. Verifique seu email e senha.",
            request.getDescription(false)
    );
    logger.warn("Falha de autenticação: {}", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED); // 401
}
```

### 5.2 AuthControllerIT.java - Linha 136

```java
// Alterar de:
.andExpect(jsonPath("$.token").exists())
// Para:
.andExpect(jsonPath("$.accessToken").exists())
```

### 5.3 Classe Utilitária para Testes (Opcional)

Para evitar duplicação de código, criar uma classe utilitária:

```java
// Arquivo: src/test/java/com/Mybuddy/Myb/TestUtils/TokenTestHelper.java
package com.Mybuddy.Myb.TestUtils;

import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.jwt.JwtUtils;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class TokenTestHelper {

    public static String generateToken(JwtUtils jwtUtils, Usuario usuario,
            String role, Long organizacaoId) {
        UserDetailsImpl userDetails = new UserDetailsImpl(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getPassword(),
            List.of(new SimpleGrantedAuthority(role)),
            organizacaoId
        );

        return jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()));
    }
}
```

**Uso:**
```java
// Em PetControllerIT.java ou InteresseAdocaoControllerIT.java:
tokenOng = TokenTestHelper.generateToken(jwtUtils, usuarioOng, "ROLE_ONG", organizacao.getId());
tokenAdmin = TokenTestHelper.generateToken(jwtUtils, usuarioAdmin, "ROLE_ADMIN", null);
```

---

## 6. Validação Final

Após implementar as correções, executar:

```bash
# Executar apenas testes de integração
./mvnw test -Dtest="*IT" -Djacoco.skip=true

# Resultado esperado:
# Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
# BUILD SUCCESS
```

---

## 7. Observações

### 7.1 Problema de Compatibilidade JaCoCo

O JaCoCo 0.8.11 não é compatível com Java 23. Para executar testes com cobertura, é necessário:
- Atualizar JaCoCo para versão 0.8.12+ (quando disponível com suporte a Java 23)
- Ou usar Java 21 LTS para execução de testes com cobertura

### 7.2 Warnings do Mockito

Os warnings sobre "dynamic agent loading" são esperados em Java 21+ e não afetam a execução dos testes. Podem ser suprimidos adicionando ao `pom.xml`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>-XX:+EnableDynamicAgentLoading</argLine>
    </configuration>
</plugin>
```

---

**Documento criado em:** Janeiro/2026
**Última atualização:** 21/01/2026
