# ADR-003: Estratégia de Containerização com Docker

**Data:** 2026-04-26  
**Status:** Aceito  
**Decisores:** Davi Cassoli Lira, Eder Henrique Pontes, Julia Cardoso, Daniel Godinho

---

## Contexto

O MyBuddy possui uma stack com múltiplos serviços interdependentes: backend Spring Boot, frontend Angular, PostgreSQL, MongoDB e Keycloak. Sem containerização, cada desenvolvedor precisaria instalar e configurar manualmente todas essas dependências em sua máquina, gerando inconsistências de ambiente e dificuldades de onboarding. A decisão de usar Docker foi tomada desde o início do projeto como requisito da disciplina e como boa prática de mercado.

---

## Decisão

Decidimos adotar Docker e Docker Compose como estratégia de containerização do MyBuddy. Cada serviço roda em seu próprio container isolado, conectado via rede interna `mybuddy-network`. A ordem de subida é garantida por `depends_on` com `condition: service_healthy`, e cada serviço possui health checks configurados. Os dados persistem entre reinicializações via volumes nomeados.

A infraestrutura completa sobe com um único comando:

```bash
docker compose up --build
```

---

## Consequências

### Positivas

- Ambiente reproduzível entre todos os desenvolvedores — elimina o problema de "funciona na minha máquina"
- Dependências complexas como Keycloak, PostgreSQL e MongoDB sobem automaticamente sem instalação manual
- Isolamento de serviços via rede interna — cada serviço se comunica pelo nome do container, não pelo localhost
- Deploy simplificado — o mesmo `docker-compose.yml` pode ser adaptado para produção com ajustes mínimos
- Padrão de mercado amplamente adotado, facilitando a manutenção e o onboarding de novos membros

### Negativas

- Debug dentro do container exige conhecimento de comandos Docker (`docker exec`, `docker logs`) e dificulta o uso de ferramentas de debug da IDE diretamente

### Neutras

- Health checks configurados em todos os serviços críticos garantem a ordem correta de inicialização, mas aumentam o tempo de subida inicial do ambiente
- O banco `keycloak` precisa ser criado via `docker/init-db.sql` pois o PostgreSQL só cria automaticamente o banco definido em `POSTGRES_DB`

---

## Alternativas Consideradas

Nenhuma alternativa foi formalmente avaliada — Docker foi definido como requisito desde o início do projeto, alinhado com as práticas da disciplina e com o padrão de mercado para projetos com múltiplos serviços.

---

## Referências

- [Docker Documentation](https://docs.docker.com)
- [Docker Compose Documentation](https://docs.docker.com/compose)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker)
