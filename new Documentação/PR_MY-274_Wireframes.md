# Pull Request - Task MY-274 (Criar wireframes e layout da página de doação)

## Task Jira

- **Card:** [MY-274](https://projetodesoftware420.atlassian.net/jira/software/projects/BUDDY/boards/34?selectedIssue=MY-274)
- **Tipo:** Feature / Documentação

---

## O que foi feito?

Esta tarefa cobriu a concepção, design visual e especificação da nova **Página de Doação** do ecossistema MyBuddy. Os wireframes de alta fidelidade foram projetados utilizando o **Figma**, seguindo a identidade visual e a paleta de cores institucional do projeto.

### Detalhes do Layout Desenvolvido no Figma:
- **Hero Section (Cabeçalho da Página):**
  - Título chamativo e de apelo emocional: *"Doe para pets e ONGs que precisam de você"*.
  - Subtítulo explicativo sobre a causa.
  - Indicadores e estatísticas de impacto em tempo real (Ex: Pets salvos, ONGs parceiras, Valor arrecadado e Doadores ativos).
  - Botão principal de ação: *"Quero Doar"* e secundário *"Ver ONGs parceiras"*.
  - Espaço dedicado para fotos de pets.
- **Seção de Busca e Filtros de Categoria:**
  - Campo de busca textual por termo.
  - Barra de chips para filtragem rápida das causas de doação (Ex: *Todos, Pets em tratamento, Ração e alimentação, Cirurgias, Abrigo / ONG, Urgente*).
  - Botão dedicado para Filtros Avançados.
- **Seção de Doação Recorrente:**
  - Banner informativo incentivando o doador a configurar doações automáticas e recorrentes (*Mensal*, *Semanal* ou *Única vez*), permitindo cancelamento a qualquer momento.
- **Campanhas de Doações em Destaque (Cards):**
  - Grid de campanhas ativas com imagem do pet, tag de categoria da causa (*Cirurgia*, *Ração*, *Tratamento*), título da campanha, cidade/UF, tempo restante e quantidade de doadores.
  - Barra visual de progresso da arrecadação com valores atuais vs. meta (Ex: *R$ 1.800 / R$ 3.000*).
  - Botão principal *"Doar agora"* com ícones de curtir/favoritar e compartilhar.

### Ajustes Extras de Infraestrutura (Keycloak):
- Ajustados os arquivos `realm-export.json` e `update_keycloak.js` para atualizar o cliente `mybuddy-frontend` no Keycloak, permitindo as URIs de redirecionamento do ambiente Docker na porta 80 (`http://localhost/*` e `http://localhost:80/*`), corrigindo o erro `invalid_redirect_uri` que acontecia ao rodar o projeto localmente via Docker Compose.

---

## Escopo das mudanças

- [ ] `backend` — Java / Spring Boot
- [ ] `frontend` — Angular
- [ ] `mobile` — Flutter
- [x] `infra` — Docker / CI / configurações (Ajuste de Redirect URIs no Keycloak)
- [x] `documentação` — Criação dos Wireframes no Figma e exportação das imagens (`w1.png`, `w2.png`, `w2-2.png`, `w3.png`) na pasta `new Documentação/wireframe_telaDoacao/`

---

## Checklist de Testes

- [x] Testei localmente e o comportamento está conforme esperado (As telas/imagens do wireframe foram exportadas corretamente e a correção de redirecionamento do Keycloak funciona perfeitamente nas portas 80 e 4200)
- [x] Cobri os casos de sucesso e os casos de erro
- [x] Não há erros ou warnings novos no console
- [x] Validei em mais de um ambiente (Redirecionamento testado com sucesso no Docker Compose e Angular Dev Server)
- [ ] Testes automatizados foram criados ou atualizados (se aplicável)

---

## Checklist de Code Review

- [x] O código segue os padrões e convenções do projeto
- [x] Não há código comentado ou `TODO` esquecido sem justificativa
- [x] Variáveis, métodos e classes têm nomes claros e descritivos
- [x] Não há lógica duplicada que poderia ser extraída
- [x] Dados sensíveis não estão expostos (tokens, senhas, chaves)
- [ ] Migrations de banco (se houver) foram testadas e são reversíveis

---

## Notas para o Revisor

- A ferramenta utilizada para desenhar as interfaces de alta fidelidade foi o **Figma Desktop App**, de modo a garantir alinhamento exato de pixel, grids e layouts com o design system do projeto.
- A correção do Keycloak é importante para que o revisor consiga acessar e testar a aplicação completa localmente rodando apenas pelo Docker Compose na porta 80.

---

## Como testar

```
1. Verifique as imagens do wireframe em: "new Documentação/wireframe_telaDoacao/".
2. Certifique-se de que o Docker está de pé: docker compose up -d (ou docker compose down -v e depois docker compose up -d para reimportar o realm modificado).
3. Abra http://localhost no navegador e tente fazer o login no frontend usando as credenciais do Keycloak para testar a correção de redirecionamento.
```
