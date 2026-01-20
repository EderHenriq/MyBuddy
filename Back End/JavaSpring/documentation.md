# Documentação do Backend - MyBuddy

Este documento fornece uma visão detalhada da arquitetura, configuração e API do backend do projeto MyBuddy. O backend é construído utilizando Java com Spring Boot, servindo como uma API RESTful para a plataforma de adoção de pets.

## 1. Visão Geral e Tecnologias

*   **Linguagem:** Java 21
*   **Framework:** Spring Boot 3.5.5
*   **Gerenciamento de Dependências:** Maven
*   **Banco de Dados (Dev):** H2 Database (Em memória)
*   **Banco de Dados (Prod - Driver):** MySQL Connector
*   **Segurança:** Spring Security com JWT (JSON Web Tokens)
*   **Documentação:** (Este arquivo)

### Dependências Principais (`pom.xml`)
*   `spring-boot-starter-web`: Para criar a API REST.
*   `spring-boot-starter-data-jpa`: Para persistência de dados (Hibernate).
*   `spring-boot-starter-security`: Para autenticação e autorização.
*   `spring-boot-starter-validation`: Para validação de dados de entrada (DTOs).
*   `jjwt`: Biblioteca para criação e validação de tokens JWT.

---

## 2. Configuração (`application.properties`)

O projeto está configurado atualmente para ambiente de desenvolvimento:

*   **Porta do Servidor:** Padrão (8080)
*   **Banco de Dados:** H2 Memória (`jdbc:h2:mem:mybuddy_db`)
    *   Usuário: `sa`
    *   Senha: `password`
    *   Console H2: Habilitado em `/h2-console`
*   **JPA/Hibernate:**
    *   DDL Auto: `create-drop` (Recria o banco a cada reinicialização)
    *   Show SQL: `true` (Logs das queries SQL)
*   **JWT:**
    *   Segredo: Definido via chave `mybuddy.app.jwtSecret`
    *   Expiração: 24 horas (`86400000` ms)
*   **Upload de Arquivos:**
    *   Diretório Local: Configurado via `file.upload-dir` (Caminho absoluto na máquina de dev).

---

## 3. Arquitetura

O projeto segue uma arquitetura em camadas padrão do Spring Boot:

1.  **Controller (`com.Mybuddy.Myb.Controller`):** Recebe as requisições HTTP, valida os DTOs de entrada e chama os serviços. Retorna `ResponseEntity`.
2.  **Service (`com.Mybuddy.Myb.Service`):** Contém a regra de negócios. Manipula as entidades e comunica-se com os repositórios.
3.  **Repository (`com.Mybuddy.Myb.Repository`):** Interfaces que estendem `JpaRepository` para acesso ao banco de dados.
4.  **Model (`com.Mybuddy.Myb.Model`):** Entidades JPA que representam as tabelas do banco de dados.
5.  **DTO (`com.Mybuddy.Myb.DTO`) & Payload:** Objetos de Transferência de Dados para desacoplar a API das entidades internas.
6.  **Security (`com.Mybuddy.Myb.Security`):** Configurações de filtros, autenticação e autorização.

---

## 4. Segurança e Autenticação

A segurança é gerenciada pelo `SecurityConfig.java`.

*   **Tipo:** Stateless (Sem sessão no servidor).
*   **Mecanismo:** Token JWT enviado no cabeçalho `Authorization: Bearer <token>`.
*   **Roles (Papéis):**
    *   `ROLE_ADMIN`: Acesso total.
    *   `ROLE_ONG`: Pode gerenciar seus pets e interesses de adoção.
    *   `ROLE_USER` (Implícito): Usuário comum (adotante), pode manifestar interesse.

### Rotas Públicas
*   `/api/auth/**` (Login e Cadastro)
*   `/uploads/**` (Imagens estáticas dos pets)
*   `/h2-console/**` (Banco de dados em memória)

### Rotas Protegidas
Todas as outras rotas exigem autenticação (`anyRequest().authenticated()`). Algumas rotas específicas exigem Roles específicas (ver seção de Endpoints).

---

## 5. Modelagem de Dados (Entidades)

### `Usuario` (Tabela: `users`)
Representa os usuários do sistema (Adotantes e Administradores de ONGs).
*   `id`: Long (PK)
*   `nome`, `email` (Unique), `telefone`, `password`
*   `organizacao`: Relacionamento com `Organizacao` (Se o usuário pertencer a uma ONG).
*   `roles`: Lista de papéis (Many-to-Many).

### `Organizacao` (Tabela: `organizacoes`)
Representa as ONGs ou instituições.
*   `id`: Long (PK)
*   `nomeFantasia`, `emailContato` (Unique), `cnpj` (Unique)
*   `telefoneContato`, `endereco`, `descricao`, `website`
*   `pets`: Lista de pets associados.
*   `usuarios`: Lista de usuários que administram a ONG.

### `Pet` (Tabela: `pets`)
Animais disponíveis para adoção.
*   `id`: Long (PK)
*   `nome`, `raca`, `idade`, `especie`, `porte`, `cor`, `pelagem`, `sexo`
*   `statusAdocao`: Enum (`DISPONIVEL`, `ADOTADO`, etc.)
*   `organizacao`: ONG dona do pet (FK).
*   `fotos`: Lista de fotos (`FotoPet`).
*   Booleanos: `microchipado`, `vacinado`, `castrado`.
*   Localização: `cidade`, `estado`.

### `InteresseAdoacao` (Tabela: `Interesses_adoacao`)
Registro de interesse de um usuário por um pet.
*   `id`: Long (PK)
*   `usuario`: Quem tem interesse (FK).
*   `pet`: Pet de interesse (FK).
*   `status`: Enum `StatusInteresse` (`PENDENTE`, `APROVADO`, `REJEITADO`).
*   `mensagem`: Mensagem do usuário para a ONG.
*   `criadoEm`, `AtualizadoEm`: Timestamps.

---

## 6. Endpoints da API

### Autenticação (`/api/auth`)
*   `POST /login`: Autentica usuário. Retorna JWT, dados do usuário e ID da organização (se houver).
*   `POST /cadastro`: Registra um novo usuário.

### Pets (`/api/pets`)
*   `POST /`: Criar novo pet. **(Role: ONG, ADMIN)**
*   `POST /upload-image`: Upload de imagem do pet (retorna nome do arquivo). **(Role: ONG, ADMIN)**
*   `GET /`: Listar pets com filtros (paginado). **(Autenticado)**
*   `GET /{id}`: Detalhes de um pet. **(Autenticado)**
*   `PUT /{id}`: Atualizar pet. **(Role: ADMIN ou ONG proprietária)**
*   `DELETE /{id}`: Remover pet. **(Role: ADMIN)**
*   `GET /organizacao/{organizacaoId}`: Listar pets de uma ONG específica. **(Role: ADMIN ou ONG proprietária)**

### Organizações (`/api/organizacoes`)
*   `POST /`: Criar organização.
*   `GET /`: Listar todas as organizações.
*   `GET /{id}`: Buscar organização por ID.
*   `PUT /{id}`: Atualizar organização.
*   `DELETE /{id}`: Deletar organização.

### Usuários (`/api/usuarios`)
*   `GET /meu-perfil`: Dados do usuário logado. **(Autenticado)**
*   `POST /`: Criar usuário (Admin). **(Role: ADMIN)**
*   `GET /`: Listar todos os usuários. **(Role: ADMIN)**
*   `GET /{id}`: Buscar usuário por ID. **(Role: ADMIN)**
*   `PUT /{id}`: Atualizar usuário. **(Role: ADMIN ou Próprio Usuário)**
*   `DELETE /{id}`: Deletar usuário. **(Role: ADMIN)**

### Interesses de Adoção (`/api`)
*   `POST /interesses`: Registrar interesse em um pet. **(Autenticado)**
*   `PUT /interesses/{id}/status`: Atualizar status (Aprovar/Rejeitar). **(Role: ADMIN, ONG)**
*   `GET /usuarios/me/interesses`: Listar meus interesses (como adotante). **(Autenticado)**
*   `GET /ongs/me/interesses`: Listar interesses recebidos pela minha ONG. **(Role: ONG)**
*   `GET /interesses`: Listar todos os interesses (Admin). **(Role: ADMIN)**

---

## 7. Fluxos Principais

1.  **Cadastro de ONG:**
    *   Admin ou Usuário cria uma `Organizacao` via `POST /api/organizacoes`.
    *   Usuários são vinculados a essa organização para poderem gerenciar pets.

2.  **Publicação de Pet:**
    *   Usuário ONG faz login -> Recebe Token.
    *   Faz upload das fotos via `POST /api/pets/upload-image`.
    *   Cria o pet via `POST /api/pets` enviando os nomes das fotos retornados.

3.  **Adoção:**
    *   Adotante navega pelo feed (`GET /api/pets`).
    *   Vê detalhes (`GET /api/pets/{id}`).
    *   Manifesta interesse (`POST /api/interesses`) com uma mensagem.
    *   ONG vê a lista de interessados (`GET /api/ongs/me/interesses`).
    *   ONG aprova ou rejeita (`PUT /api/interesses/{id}/status`).

---

## 8. Pontos de Atenção e Melhorias (Code Review)

Uma análise recente do código (realizada em Jan/2025) identificou pontos importantes para refatoração e melhoria antes do deploy em produção.

### 🔴 Crítico (Prioridade Alta) (RESOLVIDO)
1.  **Configurações Hardcoded:**
    *   O caminho de upload (`file.upload-dir`) está fixo para um diretório local específico. Necessário alterar para variável de ambiente ou caminho relativo.
    *   O `jwtSecret` está exposto no `application.properties`. Deve ser movido para variáveis de ambiente.
2.  **Logs em Produção:**
    *   Presença de `System.out.println` em Controllers e Filters. Devem ser substituídos por SLF4J/Logback (`log.info`, `log.debug`).
3.  **Bug na Entidade `Pet`:**
    *   O construtor da classe `Pet` possui atribuições incorretas (variáveis atribuídas a si mesmas), o que pode causar falhas na persistência.

### 🟡 Importante (Prioridade Média)
1.  **Testes:** Ausência de testes unitários e de integração robustos.
2.  **Performance:**
    *   Possível problema de N+1 queries no método `listarInteressesPorOrganizacao`.
    *   Upload de imagens permite apenas um arquivo por vez.
3.  **Padronização:**
    *   Uso inconsistente de injeção de dependência (mistura de `@Autowired` em campos e injeção por construtor).
    *   Alguns campos fixos (Especie, Porte) deveriam ser Enums para garantir consistência.

### 🟢 Sugestões de Evolução
*   Implementar documentação automática com Swagger/OpenAPI.
*   Adicionar auditoria automática (`@CreatedDate`, `@LastModifiedDate`) nas entidades.
*   Centralizar configurações de CORS.
