# ADR-005: Caddy como reverse proxy em vez de Nginx

**Status:** Aceito
**Data:** 29/06/2026
**Decisores:** Time MyBuddy

---

## Contexto

O MyBuddy precisa de um reverse proxy na frente dos serviços Docker para rotear tráfego HTTP/HTTPS para o frontend (Angular/Nginx), backend (Spring Boot) e Keycloak. As duas opções avaliadas foram Nginx e Caddy.

O frontend já roda em Nginx internamente para servir os arquivos estáticos do Angular. A questão é o proxy externo que recebe o tráfego da internet.

---

## Opções consideradas

### Opção A — Nginx como reverse proxy

O Nginx é o padrão de mercado para reverse proxy. Tem documentação extensa, comunidade enorme e é usado em produção por milhões de aplicações.

Para HTTPS, exige configuração manual com Certbot (Let's Encrypt):

```
apt install certbot python3-certbot-nginx
certbot --nginx -d meudominio.com
```

O Certbot precisa de um cron job para renovar certificados a cada 90 dias. A configuração do proxy fica em `/etc/nginx/sites-available/` com blocos `server`, `location`, `proxy_pass`, headers de proxy, e a seção SSL gerada pelo Certbot.

**Prós:** padrão de mercado, documentação vasta, performance comprovada.
**Contras:** HTTPS manual (Certbot + cron), configuração verbosa (~40 linhas pra cada upstream), renovação de certificado é responsabilidade do operador.

### Opção B — Caddy como reverse proxy (escolhida)

O Caddy gera certificado HTTPS automaticamente via Let's Encrypt sem nenhuma configuração adicional. Basta informar o domínio:

```
meudominio.com {
    reverse_proxy frontend:80
    handle /api/* {
        reverse_proxy backend:8081
    }
}
```

Sem Certbot, sem cron, sem renovação manual. O Caddyfile inteiro do MyBuddy tem 25 linhas.

**Prós:** HTTPS automático com zero config, configuração mínima, renovação automática, HTTP/2 e HTTP/3 por padrão.
**Contras:** menos conhecido no mercado, comunidade menor, levemente mais lento que Nginx em benchmarks extremos (irrelevante pro nosso volume).

---

## Decisão

**Caddy.** Pelos seguintes motivos:

1. **HTTPS sem fricção.** O time tem 4 pessoas e ninguém é especialista em infra. Configurar Certbot, renovação automática e debug de certificado expirado é overhead que não agrega valor ao TCC. Com Caddy, HTTPS funciona declarando o domínio. Acabou.

2. **Configuração mínima.** O Caddyfile do MyBuddy tem 25 linhas. O equivalente em Nginx teria ~80 linhas entre proxy, SSL, headers, gzip e locations. Menos código = menos erro.

3. **Nginx já está no stack.** O frontend Angular roda dentro de um container Nginx que serve os arquivos estáticos. Usar Nginx também como proxy externo criaria confusão sobre qual Nginx faz o quê. Com Caddy, a separação é clara: Caddy = proxy externo, Nginx = servidor de estáticos interno.

4. **Docker-native.** A imagem `caddy:2-alpine` é 40MB e sobe em segundos. Volumes persistem certificados entre restarts. Sem instalação no host, sem systemd, sem cron.

5. **Sem domínio também funciona.** Com `SITE_ADDRESS=http://IP`, o Caddy roda em HTTP puro sem tentar gerar certificado. Isso permite deploy com IP antes de ter domínio — exatamente o nosso caso no TCC.

---

## Consequências

**Positivas:**
- Deploy com HTTPS é um comando (`SITE_ADDRESS=dominio.com docker compose up -d`)
- Zero manutenção de certificados
- Caddyfile legível por qualquer membro do time
- Proxy e SSL desacoplados do Nginx do frontend

**Negativas:**
- Se migrar pra infraestrutura corporativa no futuro, o time de infra pode preferir Nginx (padrão de mercado). A migração é possível mas exige reescrever a config.
- Menos material de referência em português

**Neutras:**
- Performance é equivalente pro volume do MyBuddy (< 1000 req/min). A diferença só aparece em dezenas de milhares de requisições por segundo.

---

## Referências

- Caddy: https://caddyserver.com/docs/
- Comparação Caddy vs Nginx: https://caddyserver.com/docs/compared-to-nginx
- Certbot: https://certbot.eff.org/
