# MyBuddy — Documentação de Produção

---

## Acesso ao servidor

```bash
ssh -i mybuddy-key.pem ubuntu@3.147.73.165
```

- **Provedor:** AWS EC2 (us-east-2, Ohio)
- **Instância:** c7i-flex.large (2 vCPU, 4GB RAM)
- **OS:** Ubuntu 24.04 LTS
- **Disco:** 30GB gp3
- **IP público:** 3.147.73.165
- **Chave SSH:** mybuddy-key.pem (RSA, guardada localmente — nunca commitar)

---

## URLs

| Serviço | URL |
|---------|-----|
| Aplicação | http://3.147.73.165 |
| API | http://3.147.73.165/api/ |
| Swagger | http://3.147.73.165/api/swagger-ui/index.html |
| Keycloak Admin | http://3.147.73.165/admin/ |
| Healthcheck | http://3.147.73.165/api/actuator/health |

---

## Containers

| Container | Imagem | Porta interna | Função |
|-----------|--------|---------------|--------|
| mybuddy-caddy | caddy:2-alpine | 80, 443 | Reverse proxy, HTTPS |
| mybuddy-frontend | mybuddy-frontend (build) | 80 | Angular 21 + Nginx |
| mybuddy-backend | mybuddy-backend (build) | 8081 | Spring Boot 3.5 + Java 21 |
| mybuddy-keycloak | keycloak:26.5.5 | 8080 | Autenticação OAuth2 |
| mybuddy-postgres | postgres:16-alpine | 5432 | Banco relacional |
| mybuddy-mongodb | mongo:7.0 | 27017 | Banco documental |
| mybuddy-redis | redis:7-alpine | 6379 | Cache |

Nenhuma porta interna é exposta pro host. Todo tráfego externo passa pelo Caddy.

---

## Comandos do dia a dia

### Status
```bash
docker ps
docker compose logs -f              # todos os logs
docker compose logs -f backend      # log de um serviço
```

### Reiniciar
```bash
docker compose restart              # reinicia tudo
docker compose restart backend      # reinicia um serviço
```

### Parar e subir
```bash
docker compose down                 # para tudo (dados preservados nos volumes)
docker compose up -d                # sobe tudo
```

### Rebuild após alteração de código
```bash
cd ~/MyBuddy
git pull origin Developer
docker compose up -d --build        # reconstrói imagens e reinicia
```

### Ver uso de recursos
```bash
docker stats --no-stream
```

---

## Credenciais

As credenciais estão em `~/MyBuddy/.env`. **Nunca commitar esse arquivo.**

### Keycloak Admin Console
- **URL:** http://3.147.73.165/admin/
- **Usuário:** admin
- **Senha:** ver `KEYCLOAK_ADMIN_PASSWORD` no `.env`

### Usuários de teste (realm mybuddy)
| Email | Senha | Role |
|-------|-------|------|
| admin@mybuddy.com | Admin@2026 | ROLE_ADMIN |
| user@mybuddy.com | User@2026 | ROLE_USER |
| ong@mybuddy.com | Ong@2026 | ROLE_ONG |
| petshop@mybuddy.com | Petshop@2026 | ROLE_PETSHOP |

### Bancos de dados
- **PostgreSQL:** ver `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` no `.env`
- **MongoDB:** ver `MONGO_INITDB_ROOT_USERNAME` e `MONGO_INITDB_ROOT_PASSWORD` no `.env`

---

## Acessar bancos de dados

### PostgreSQL
```bash
docker exec -it mybuddy-postgres psql -U mybuddy_prod -d mybuddy
```

### MongoDB
```bash
docker exec -it mybuddy-mongodb mongosh -u admin_prod -p "$(grep MONGO_INITDB_ROOT_PASSWORD ~/MyBuddy/.env | cut -d= -f2)" --authenticationDatabase admin
```

### Redis
```bash
docker exec -it mybuddy-redis redis-cli
```

---

## Keycloak — gestão de usuários

### Listar usuários
```bash
# Autenticar (usar a senha entre aspas simples por causa dos caracteres especiais)
docker exec -it mybuddy-keycloak /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080 --realm master --user admin \
  --password 'SENHA_DO_ENV'

# Listar
docker exec -it mybuddy-keycloak /opt/keycloak/bin/kcadm.sh get users -r mybuddy
```

### Resetar senha de usuário
```bash
docker exec -it mybuddy-keycloak /opt/keycloak/bin/kcadm.sh set-password \
  -r mybuddy --username EMAIL --password NOVA_SENHA
```

### Criar novo usuário
```bash
docker exec -it mybuddy-keycloak /opt/keycloak/bin/kcadm.sh create users \
  -r mybuddy -s username=novo@email.com -s email=novo@email.com \
  -s firstName=Nome -s lastName=Sobrenome -s enabled=true

docker exec -it mybuddy-keycloak /opt/keycloak/bin/kcadm.sh set-password \
  -r mybuddy --username novo@email.com -p SenhaSegura123
```

---

## Roteamento do Caddy

```
/*          → frontend (Angular/Nginx)
/api/*      → backend (Spring Boot)
/realms/*   → keycloak
/admin/*    → keycloak
/resources/* → keycloak
/js/*       → keycloak
/uploads/*  → backend
```

Configuração em `~/MyBuddy/docker/caddy/Caddyfile`.

---

## Troubleshooting

### Container reiniciando em loop
```bash
docker logs NOME_DO_CONTAINER --tail 50
```

### Keycloak não sobe
Causa mais comum: banco `keycloak` não existe no PostgreSQL.
```bash
docker exec -it mybuddy-postgres psql -U mybuddy_prod -d mybuddy -c "CREATE DATABASE keycloak;"
docker restart mybuddy-keycloak
```

### Backend não conecta no MongoDB
Causa mais comum: caracteres especiais (`+`, `=`, `/`) na senha dentro da URI.
Solução: gerar nova senha sem caracteres especiais:
```bash
openssl rand -hex 32
```
Atualizar `MONGO_INITDB_ROOT_PASSWORD` e `MONGODB_URI` no `.env`, depois:
```bash
docker compose down
docker volume rm mybuddy_mongodb_data
docker compose up -d
```
⚠️ Isso apaga os dados do MongoDB.

### Página não carrega (502/504)
```bash
docker ps                            # verificar se todos estão Up
docker logs mybuddy-caddy --tail 20  # verificar erros do proxy
```

### IP mudou após reboot
A instância não tem Elastic IP. Se reiniciar, o IP muda.
Pra fixar: AWS Console → EC2 → Elastic IPs → Allocate → Associate com a instância.
Depois atualizar o `.env` com o novo IP em `CORS_ALLOWED_ORIGINS`, `KEYCLOAK_URL`, `PUBLIC_URL`, `SITE_ADDRESS`.

---

## Backup

### Backup do PostgreSQL
```bash
docker exec mybuddy-postgres pg_dumpall -U mybuddy_prod > backup_pg_$(date +%Y%m%d).sql
```

### Backup do MongoDB
```bash
docker exec mybuddy-mongodb mongodump -u admin_prod \
  -p "$(grep MONGO_INITDB_ROOT_PASSWORD ~/MyBuddy/.env | cut -d= -f2)" \
  --authenticationDatabase admin --archive=/tmp/backup_mongo.gz --gzip

docker cp mybuddy-mongodb:/tmp/backup_mongo.gz ./backup_mongo_$(date +%Y%m%d).gz
```

### Backup do .env
```bash
cp ~/MyBuddy/.env ~/mybuddy-env-backup-$(date +%Y%m%d)
```

---

## Segurança aplicada

| Medida | Status |
|--------|--------|
| Mock auth bypass removido | ✅ |
| Endpoints de debug removidos | ✅ |
| CORS via env var | ✅ |
| Portas internas não expostas | ✅ |
| Headers de segurança no Nginx | ✅ |
| Dotfiles bloqueados | ✅ |
| Credenciais geradas com openssl rand | ✅ |
| .env fora do repositório | ✅ |
| Keycloak com senhas fortes | ✅ |
| Caddy como único ponto de entrada | ✅ |
