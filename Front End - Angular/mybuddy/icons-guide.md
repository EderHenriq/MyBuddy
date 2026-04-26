# Catálogo de Ícones — MyBuddy

Este projeto utiliza a biblioteca de ícones 'Material Icons' em vez de 'PrimeNG Icons' por uma escolha de design estético após a definição do protótipo do Figma

Uso no templete: <span class="material-icons">nome_do_icone</span>


| Contexto       | Ícone            | Nome Material Icons                              |
|----------------|------------------|--------------------------------------------------|
| Meus Pets      | Pata             | `pets`                                           |
| Favoritar      | Coração          | `favorite`                                       |
| Mensagens      | Balão            | `mode_comment`                                   |
| Buscar         | Lupa             | `search`                                         |
| Solicitações   | Prancheta        | `content_paste`                                  |
| Notificações   | Sino             | `notifications`                                  |
| Usuário        | Pessoa           | `person_outline`                                 |
| Ordenar        | Três listas      | `filter_list`                                    |
| Setas          | Arco direcional  | `keyboard_arrow_right/left/up/down`              |

# Como testar?

1. Fazer checkout na branch e rodar 'npm install'.
2. Adicionar temporariamente no `app.html`:
   <span class="material-icons">favorite</span>
   <span class="material-icons">notifications</span>
   <span class="material-icons">search</span>
3. Verificar se os icones aparecem corretamente na tela.
4. Conferir se há erros no console.
5. Remover o código de teste após a validação.
