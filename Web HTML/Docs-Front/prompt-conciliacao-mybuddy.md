# 🎯 Prompt de Conciliação de Design — MyBuddy

## Missão

Você é um agente de conciliação de design. Sua tarefa é migrar o visual da página **GestaoInteresseAdoacao.html** para seguir o design system da **Landing Page**, mantendo 100% da funcionalidade JS existente. Aplique melhorias mínimas e cirúrgicas — nunca saia da curva visual da landing page.

---

## 📐 Design System de Referência (Landing Page)

Estes são os valores-fonte. Toda decisão visual deve respeitar esses tokens:

```css
:root {
  /* Cores */
  --primary: #FF7B00;
  --bg-page: #f4f4f4;
  --text-primary: #1a1a1a;
  --text-secondary: #6c757d;

  /* Status — manter compatibilidade funcional */
  --status-pending: #FF7B00;
  --status-approved: #36bf4c;
  --status-rejected: #e74c3c;

  /* Tipografia */
  --font-family: 'Inter', system-ui, sans-serif;

  /* Superfícies */
  --radius-sm: 12px;
  --radius-md: 24px;
  --radius-lg: 50px;
  --radius-button: 25px;

  /* Efeitos */
  --transition: 0.3s ease;
  --blur-header: blur(50px);
  --shadow-card: 0 4px 20px rgba(0, 0, 0, 0.08);
  --shadow-hover: 0 8px 32px rgba(0, 0, 0, 0.12);
}
```

---

## 📋 Checklist de Migração (ordem de execução)

### 1. Dependências — HEAD do HTML

- [ ] **Remover** Font Awesome 6 CDN
- [ ] **Adicionar** Google Fonts Inter:
  ```html
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  ```
- [ ] **Adicionar** Google Material Symbols:
  ```html
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Rounded:opsz,wght,FILL,GRAD@24,400,0,0" rel="stylesheet">
  ```
- [ ] **Mapear ícones** Font Awesome → Material Symbols equivalentes:
  - `fa-paw` → `pets`
  - `fa-clock` → `schedule`
  - `fa-check-circle` → `check_circle`
  - `fa-times-circle` → `cancel`
  - `fa-filter` → `filter_list`
  - `fa-search` → `search`
  - `fa-eye` → `visibility`
  - `fa-user` → `person`
  - `fa-sign-out-alt` → `logout`
  - `fa-times` → `close`
  - (outros conforme encontrar no HTML)

### 2. CSS — Variáveis e Reset

- [ ] Substituir **todas** as CSS variables da `:root` atual pelos tokens do design system acima
- [ ] Aplicar reset CSS consistente com a landing page (box-sizing, margin, padding)
- [ ] Trocar `font-family: 'Segoe UI', Tahoma` → `'Inter', system-ui, sans-serif`
- [ ] Ajustar `background-color` do body para `#f4f4f4`
- [ ] Esconder scrollbar customizada (mesmo padrão da landing)

### 3. Header — Espelhar a Landing Page

O header deve ser visualmente idêntico ao da landing page:

- [ ] `position: fixed` (já sticky, mudar para fixed)
- [ ] `backdrop-filter: blur(50px)` + `background: rgba(255,255,255,0.85)`
- [ ] `border-radius: 0 0 24px 24px` ou sem radius (seguir landing)
- [ ] Logo à esquerda, nav ao centro, área de auth à direita
- [ ] Links de nav com `font-weight: 500`, transição de cor no hover
- [ ] Botões login/signup com estilo da landing (se aplicável ao contexto logado: mostrar nome + botão logout estilizado)
- [ ] `z-index: 1000` e `width: 100%`
- [ ] Adicionar `padding-top` no conteúdo abaixo para compensar header fixed

### 4. Seção de Filtros

- [ ] Container com `background: white`, `border-radius: 24px`, `padding: 24px 32px`
- [ ] `box-shadow: var(--shadow-card)`
- [ ] Inputs e selects: `border-radius: 12px`, `border: 1.5px solid #e0e0e0`
- [ ] Focus nos inputs: `border-color: var(--primary)`, `box-shadow: 0 0 0 3px rgba(255,123,0,0.15)`
- [ ] Checkboxes de status: estilizar como pills/tags arredondadas (melhoria mínima) com as cores de status
- [ ] Manter lógica de ocultação para ADOTANTE via JS (não alterar classes JS)

### 5. Cards de Interesse

Migrar de grid tabular (5 colunas rígidas) para cards com identidade visual da landing:

- [ ] `background: white`, `border-radius: 24px`, `padding: 24px`
- [ ] `box-shadow: var(--shadow-card)` → `var(--shadow-hover)` no hover
- [ ] `transition: var(--transition)` + `transform: translateY(-2px)` no hover
- [ ] **Manter** border-left colorido por status (funcionalidade existente), mas aumentar `border-radius` para acompanhar
- [ ] Tipografia: títulos em `font-weight: 600`, textos em `font-weight: 400`
- [ ] Badges de status: `border-radius: var(--radius-lg)` (pill shape)
- [ ] Botões de ação (Aprovar/Rejeitar): `border-radius: var(--radius-button)`, com hover suave
- [ ] **Não alterar** estrutura de dados ou classes usadas pelo JS

### 6. Modal de Detalhes do Pet

- [ ] `border-radius: 24px` no container do modal
- [ ] Overlay com `backdrop-filter: blur(8px)` + `background: rgba(0,0,0,0.5)`
- [ ] Botão de fechar: ícone Material Symbol `close`, posicionado com estilo consistente
- [ ] Conteúdo interno com espaçamento generoso (`gap: 16px`)
- [ ] Imagem do pet (se houver): `border-radius: 16px`, `object-fit: cover`
- [ ] Animação `fadeIn` existente — manter, apenas ajustar `border-radius`

### 7. Toast / Notificações

- [ ] `border-radius: 16px`, `backdrop-filter: blur(12px)`
- [ ] Sombra suave, tipografia Inter
- [ ] Manter lógica JS intacta

### 8. Footer

- [ ] Estilizar consistente com a landing (se landing tem footer, espelhar; se não, manter minimalista)
- [ ] `background: #1a1a1a`, `color: white`, `border-radius: 24px 24px 0 0` (se a landing usar esse padrão)

---

## 🔧 Melhorias Mínimas Permitidas (curvas controladas)

Estas são as únicas melhorias além da conciliação direta:

1. **Filtros como pills**: Checkboxes de status podem virar toggle pills clicáveis (visual upgrade, mesma funcionalidade)
2. **Empty state**: Se não há interesses, mostrar ilustração/ícone + texto amigável ao invés de lista vazia
3. **Skeleton loading**: Enquanto carrega dados da API, mostrar placeholder animado nos cards (CSS puro, sem JS novo)
4. **Hover micro-interactions**: Cards com `translateY(-2px)` e sombra expandida (já listado acima)
5. **Scrollbar estilizada**: `::-webkit-scrollbar` sutil se a landing esconde completamente

---

## ⛔ Regras de Proteção — NÃO ALTERAR

- **Nenhuma lógica JavaScript** deve ser modificada (GestaoInteresseAdocao.js)
- **Nenhuma classe CSS usada como seletor no JS** deve ser renomeada
- **Nenhum endpoint de API** deve mudar
- **Nenhuma estrutura HTML** que o JS referencia por ID ou classe deve ser removida
- **Sistema de roles e permissões** permanece intacto
- **Animações CSS existentes** (fadeIn, pulseCard, badgeFade) — manter os `@keyframes`, apenas ajustar valores visuais se necessário
- **Breakpoints responsivos** existentes (992px, 768px) — manter e ajustar para o novo visual

---

## 📁 Arquivos para Editar

| Arquivo | Ação |
|---------|------|
| `GestaoInteresseAdoacao.html` | Trocar CDNs, mapear ícones FA → Material Symbols |
| `InteresseAdoacao.css` | Reescrever com design system da landing page |
| `GestaoInteresseAdocao.js` | **NÃO TOCAR** |

---

## 🔄 Fluxo de Trabalho

1. **Ler** os 3 arquivos atuais (HTML, CSS, JS)
2. **Mapear** todas as classes CSS que o JS usa como seletores (grep no JS por `querySelector`, `classList`, `getElementById`, `className`)
3. **Listar** essas classes como protegidas
4. **Editar** o HTML (dependências + ícones)
5. **Reescrever** o CSS seguindo o checklist acima
6. **Testar** visualmente que a estrutura se mantém
7. **Validar** que nenhuma classe protegida foi removida

---

## 💡 Tom da Entrega

O resultado final deve parecer que a página de Gestão de Interesses **sempre foi parte da landing page** — mesma família visual, mesma sensação de produto. O usuário que navega da landing para essa página não deve sentir ruptura de design.
