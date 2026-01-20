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

1. Adicionar dependências de teste no `pom.xml`
2. Implementar testes unitários dos Services
3. Implementar testes de integração dos Controllers
4. Configurar Jacoco para relatório de cobertura
5. Integrar testes no pipeline de CI/CD
