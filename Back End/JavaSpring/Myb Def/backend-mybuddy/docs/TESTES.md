# Documentação de Testes - MyBuddy Backend

Este documento descreve a estratégia de testes para o backend do MyBuddy, explicando o que cada tipo de teste faz e quais cenários serão cobertos.

---

## 1. Tipos de Testes

### 1.1 Testes Unitários
**O que são:** Testam uma única unidade de código (método/classe) de forma isolada, sem depender de banco de dados, rede ou outros serviços externos.

**Características:**
- Rápidos de executar (milissegundos)
- Usam **mocks** para simular dependências
- Focam na lógica de negócio
- Não inicializam o contexto do Spring

**Ferramentas:** JUnit 5, Mockito

---

### 1.2 Testes de Integração
**O que são:** Testam a integração entre múltiplos componentes do sistema, incluindo banco de dados, endpoints HTTP e o contexto completo do Spring.

**Características:**
- Mais lentos (segundos)
- Usam banco de dados em memória (H2)
- Testam o fluxo completo da requisição
- Inicializam o contexto do Spring (`@SpringBootTest`)

**Ferramentas:** JUnit 5, Spring Boot Test, MockMvc, TestRestTemplate

---

## 2. Testes Unitários Planejados

### 2.1 PetService

| Método | Cenário de Teste | O que Valida |
|--------|------------------|--------------|
| `criarPet()` | Pet válido | Retorna PetResponse com dados corretos |
| `criarPet()` | Organização inexistente | Lança `IllegalArgumentException` |
| `criarPet()` | Sem organizacaoId | Lança `IllegalArgumentException` |
| `atualizarPet()` | Pet existente | Atualiza e retorna dados atualizados |
| `atualizarPet()` | Pet inexistente | Lança `IllegalStateException` |
| `deletarPet()` | Pet disponível sem interesses | Deleta com sucesso |
| `deletarPet()` | Pet já adotado | Lança `IllegalStateException` |
| `deletarPet()` | Pet com interesses | Lança `IllegalStateException` |
| `isPetOwnedByCurrentUser()` | Usuário é ADMIN | Retorna `true` |
| `isPetOwnedByCurrentUser()` | ONG dona do pet | Retorna `true` |
| `isPetOwnedByCurrentUser()` | ONG não dona | Retorna `false` |

**Exemplo de código:**
```java
@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private OrganizacaoRepository organizacaoRepository;

    @InjectMocks
    private PetService petService;

    @Test
    void criarPet_ComDadosValidos_RetornaPetResponse() {
        // Arrange (Preparar)
        PetRequestDTO request = criarPetRequestValido();
        Organizacao org = new Organizacao();
        org.setId(1L);

        when(organizacaoRepository.findById(1L)).thenReturn(Optional.of(org));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> {
            Pet pet = inv.getArgument(0);
            pet.setId(1L);
            return pet;
        });

        // Act (Executar)
        PetResponse response = petService.criarPet(request);

        // Assert (Verificar)
        assertNotNull(response);
        assertEquals("Rex", response.nome());
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void criarPet_OrganizacaoInexistente_LancaExcecao() {
        // Arrange
        PetRequestDTO request = criarPetRequestValido();
        when(organizacaoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> petService.criarPet(request));
    }
}
```

---

### 2.2 UsuarioService

| Método | Cenário de Teste | O que Valida |
|--------|------------------|--------------|
| `criarUsuario()` | Email único | Cria usuário com sucesso |
| `criarUsuario()` | Email duplicado | Lança `IllegalStateException` |
| `atualizarUsuario()` | Usuário existente | Atualiza dados corretamente |
| `atualizarUsuario()` | Usuário inexistente | Lança `IllegalStateException` |
| `deletarUsuario()` | Usuário existente | Deleta com sucesso |
| `deletarUsuario()` | Usuário inexistente | Lança `IllegalStateException` |

---

### 2.3 AuthService

| Método | Cenário de Teste | O que Valida |
|--------|------------------|--------------|
| `autenticar()` | Credenciais válidas | Retorna JWT válido |
| `autenticar()` | Senha incorreta | Lança exceção de autenticação |
| `registrar()` | Dados válidos | Cria usuário e retorna dados |
| `registrar()` | Email já existe | Lança exceção |

---

### 2.4 InteresseAdocaoService

| Método | Cenário de Teste | O que Valida |
|--------|------------------|--------------|
| `registrarInteresse()` | Interesse válido | Cria interesse com status PENDENTE |
| `registrarInteresse()` | Pet já adotado | Lança exceção |
| `atualizarStatus()` | Aprovar interesse | Status muda para APROVADO |
| `atualizarStatus()` | Rejeitar interesse | Status muda para REJEITADO |

---

## 3. Testes de Integração Planejados

### 3.1 AuthController (Autenticação)

| Endpoint | Cenário | Resultado Esperado |
|----------|---------|-------------------|
| `POST /api/auth/cadastro` | Cadastro válido | `201 Created` + dados do usuário |
| `POST /api/auth/cadastro` | Email duplicado | `409 Conflict` |
| `POST /api/auth/login` | Credenciais válidas | `200 OK` + JWT token |
| `POST /api/auth/login` | Credenciais inválidas | `401 Unauthorized` |

**Exemplo de código:**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_ComCredenciaisValidas_RetornaToken() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("usuario@email.com", "senha123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.email").value("usuario@email.com"));
    }

    @Test
    void login_ComSenhaIncorreta_Retorna401() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("usuario@email.com", "senhaErrada");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }
}
```

---

### 3.2 PetController (CRUD de Pets)

| Endpoint | Cenário | Resultado Esperado |
|----------|---------|-------------------|
| `POST /api/pets` | ONG cria pet | `201 Created` + PetResponse |
| `POST /api/pets` | Usuário comum tenta criar | `403 Forbidden` |
| `GET /api/pets` | Listar com filtros | `200 OK` + lista paginada |
| `GET /api/pets/{id}` | Pet existe | `200 OK` + detalhes do pet |
| `GET /api/pets/{id}` | Pet não existe | `404 Not Found` |
| `PUT /api/pets/{id}` | ONG dona atualiza | `200 OK` + dados atualizados |
| `PUT /api/pets/{id}` | ONG não dona tenta atualizar | `403 Forbidden` |
| `DELETE /api/pets/{id}` | ADMIN deleta | `204 No Content` |

**Exemplo de código:**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tokenOng;
    private String tokenAdmin;

    @BeforeEach
    void setUp() {
        // Obter tokens para diferentes roles
        tokenOng = obterTokenOng();
        tokenAdmin = obterTokenAdmin();
    }

    @Test
    void criarPet_ComoOng_RetornaCriado() throws Exception {
        // Arrange
        PetRequestDTO request = new PetRequestDTO();
        request.setNome("Rex");
        request.setEspecie(Especie.CAO);
        request.setPorte(Porte.MEDIO);
        // ... outros campos

        // Act & Assert
        mockMvc.perform(post("/api/pets")
                .header("Authorization", "Bearer " + tokenOng)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome").value("Rex"))
            .andExpect(jsonPath("$.especie").value("CAO"));
    }

    @Test
    void buscarPets_ComFiltroEspecie_RetornaFiltrado() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/pets")
                .header("Authorization", "Bearer " + tokenOng)
                .param("especie", "CAO")
                .param("porte", "MEDIO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }
}
```

---

### 3.3 InteresseAdocaoController

| Endpoint | Cenário | Resultado Esperado |
|----------|---------|-------------------|
| `POST /api/interesses` | Registrar interesse | `201 Created` |
| `PUT /api/interesses/{id}/status` | ONG aprova | `200 OK` + status APROVADO |
| `GET /api/usuarios/me/interesses` | Meus interesses | `200 OK` + lista |
| `GET /api/ongs/me/interesses` | Interesses da ONG | `200 OK` + lista |

---

## 4. Estrutura de Pastas dos Testes

```
src/
└── test/
    └── java/
        └── com/
            └── Mybuddy/
                └── Myb/
                    ├── Service/                    # Testes Unitários
                    │   ├── PetServiceTest.java
                    │   ├── UsuarioServiceTest.java
                    │   ├── AuthServiceTest.java
                    │   └── InteresseAdocaoServiceTest.java
                    │
                    ├── Controller/                 # Testes de Integração
                    │   ├── AuthControllerIT.java
                    │   ├── PetControllerIT.java
                    │   ├── UsuarioControllerIT.java
                    │   └── InteresseAdocaoControllerIT.java
                    │
                    └── Repository/                 # Testes de Repository
                        ├── PetRepositoryTest.java
                        └── UsuarioRepositoryTest.java
```

---

## 5. Cobertura de Testes Alvo

| Camada | Cobertura Mínima |
|--------|------------------|
| Services | 80% |
| Controllers | 70% |
| Repositories | 60% |
| **Total** | **75%** |

---

## 6. Como Executar os Testes

```bash
# Executar todos os testes
./mvnw test

# Executar apenas testes unitários
./mvnw test -Dtest="*Test"

# Executar apenas testes de integração
./mvnw test -Dtest="*IT"

# Executar com relatório de cobertura (Jacoco)
./mvnw test jacoco:report
```

---

## 7. Benefícios dos Testes

| Benefício | Descrição |
|-----------|-----------|
| **Confiança** | Garantia de que o código funciona como esperado |
| **Refatoração Segura** | Alterar código sem medo de quebrar funcionalidades |
| **Documentação Viva** | Testes mostram como o código deve ser usado |
| **Detecção Precoce** | Bugs encontrados antes de ir para produção |
| **CI/CD** | Testes automatizados no pipeline de deploy |

---

## 8. Próximos Passos

1. ✅ Adicionar dependências de teste no `pom.xml`
2. ✅ Implementar testes unitários dos Services
3. ✅ Implementar testes de integração dos Controllers
4. ✅ Configurar Jacoco para relatório de cobertura
5. Integrar testes no pipeline de CI/CD

---

## 9. Implementação Realizada (Janeiro/2026)

### 9.1 Configuração de Dependências

Foram adicionadas as seguintes dependências ao `pom.xml`:

```xml
<!-- Spring Security Test -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Plugin Jacoco para cobertura de código -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 9.2 Testes Unitários Implementados

#### PetServiceTest
**Localização:** `src/test/java/com/Mybuddy/Myb/Service/PetServiceTest.java`

**Cenários cobertos (11 testes):**
- ✅ Criar pet com dados válidos
- ✅ Lançar exceção quando organização não existe
- ✅ Lançar exceção quando organizacaoId é nulo
- ✅ Atualizar pet existente com sucesso
- ✅ Lançar exceção ao atualizar pet inexistente
- ✅ Deletar pet disponível sem interesses
- ✅ Lançar exceção ao deletar pet adotado
- ✅ Lançar exceção ao deletar pet com interesses
- ✅ Retornar true quando usuário é ADMIN
- ✅ Retornar true quando ONG é dona do pet
- ✅ Retornar false quando ONG não é dona do pet

**Tecnologias utilizadas:** JUnit 5, Mockito, @ExtendWith(MockitoExtension.class)

#### UsuarioServiceTest
**Localização:** `src/test/java/com/Mybuddy/Myb/Service/UsuarioServiceTest.java`

**Cenários cobertos (6 testes):**
- ✅ Criar usuário com email único
- ✅ Lançar exceção quando email já existe
- ✅ Atualizar usuário existente
- ✅ Lançar exceção ao atualizar usuário inexistente
- ✅ Deletar usuário existente
- ✅ Lançar exceção ao deletar usuário inexistente

#### AuthServiceTest
**Localização:** `src/test/java/com/Mybuddy/Myb/Service/AuthServiceTest.java`

**Cenários cobertos (6 testes):**
- ✅ Autenticar usuário com credenciais válidas
- ✅ Gerar token JWT para autenticação
- ✅ Registrar usuário com dados válidos
- ✅ Lançar exceção quando email já existe
- ✅ Criar ONG ao registrar usuário com role ONG
- ✅ Lançar exceção quando CNPJ da ONG já existe

#### InteresseAdocaoServiceTest
**Localização:** `src/test/java/com/Mybuddy/Myb/Service/InteresseAdocaoServiceTest.java`

**Cenários cobertos (6 testes):**
- ✅ Registrar interesse válido com status PENDENTE
- ✅ Lançar exceção quando pet já está adotado
- ✅ Lançar exceção quando usuário já manifestou interesse
- ✅ Aprovar interesse com sucesso
- ✅ Rejeitar interesse com sucesso
- ✅ Lançar exceção ao atualizar interesse inexistente

### 9.3 Testes de Integração Implementados

#### AuthControllerIT
**Localização:** `src/test/java/com/Mybuddy/Myb/Controller/AuthControllerIT.java`

**Endpoints testados (4 cenários):**
- ✅ `POST /api/auth/cadastro` - Cadastro com sucesso (201 Created)
- ✅ `POST /api/auth/cadastro` - Email duplicado (400 Bad Request)
- ✅ `POST /api/auth/login` - Credenciais válidas (200 OK + JWT)
- ✅ `POST /api/auth/login` - Senha incorreta (401 Unauthorized)

**Anotações utilizadas:**
- `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- `@AutoConfigureMockMvc`
- `@Transactional`

#### PetControllerIT
**Localização:** `src/test/java/com/Mybuddy/Myb/Controller/PetControllerIT.java`

**Endpoints testados (5 cenários):**
- ✅ `POST /api/pets` - ONG cria pet (201 Created)
- ✅ `GET /api/pets` - Listar pets com autenticação (200 OK)
- ✅ `GET /api/pets/{id}` - Pet existe (200 OK)
- ✅ `GET /api/pets/{id}` - Pet não existe (404 Not Found)
- ✅ `DELETE /api/pets/{id}` - ADMIN deleta pet (204 No Content)

**Características:**
- Testes com autenticação JWT real
- Múltiplos usuários com diferentes roles (ONG, ADMIN)
- Banco de dados H2 em memória
- Rollback automático via @Transactional

#### InteresseAdocaoControllerIT
**Localização:** `src/test/java/com/Mybuddy/Myb/Controller/InteresseAdocaoControllerIT.java`

**Endpoints testados (4 cenários):**
- ✅ `POST /api/interesses` - Registrar interesse (201 Created)
- ✅ `PUT /api/interesses/{id}/status` - ONG aprova interesse (200 OK)
- ✅ `GET /api/usuarios/me/interesses` - Listar meus interesses (200 OK)
- ✅ `GET /api/ongs/me/interesses` - ONG lista interesses recebidos (200 OK)

### 9.4 Estrutura Final de Testes

```
src/test/java/com/Mybuddy/Myb/
├── Controller/                          # Testes de Integração
│   ├── AuthControllerIT.java            (4 testes)
│   ├── PetControllerIT.java             (5 testes)
│   └── InteresseAdocaoControllerIT.java (4 testes)
│
├── Service/                             # Testes Unitários
│   ├── PetServiceTest.java              (11 testes)
│   ├── UsuarioServiceTest.java          (6 testes)
│   ├── AuthServiceTest.java             (6 testes)
│   └── InteresseAdocaoServiceTest.java  (6 testes)
│
└── DTO/
    └── InteresseAdoacaoMapperTest.java  (existente)
```

**Total de testes implementados:** 42 testes novos

### 9.5 Como Executar os Testes

```bash
# Executar todos os testes
mvn test

# Executar apenas testes unitários
mvn test -Dtest="*Test"

# Executar apenas testes de integração
mvn test -Dtest="*IT"

# Executar com relatório de cobertura (Jacoco)
mvn test jacoco:report

# Ver relatório de cobertura
# Após executar, abrir: target/site/jacoco/index.html
```

### 9.6 Padrões e Boas Práticas Utilizadas

1. **Nomenclatura:**
   - Testes unitários: `*Test.java`
   - Testes de integração: `*IT.java`
   - Métodos: `metodo_Cenario_ResultadoEsperado()`

2. **Estrutura AAA (Arrange-Act-Assert):**
   - **Arrange:** Preparação dos dados e mocks
   - **Act:** Execução do método testado
   - **Assert:** Verificação dos resultados

3. **Isolamento:**
   - Testes unitários usam mocks (Mockito)
   - Testes de integração usam banco H2 em memória
   - @Transactional garante rollback automático

4. **Anotações Descritivas:**
   - `@DisplayName` para descrições legíveis
   - Comentários explicativos quando necessário

### 9.7 Próximos Passos Recomendados

1. ✅ **Executar os testes em ambiente com internet** para validar todas as implementações
2. Analisar relatório de cobertura Jacoco e ajustar metas se necessário
3. Integrar testes no pipeline de CI/CD (GitHub Actions, Jenkins, etc.)
4. Considerar adicionar testes de Repository se a cobertura ficar abaixo de 75%
5. Implementar testes E2E (End-to-End) para fluxos críticos completos
