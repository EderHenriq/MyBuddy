import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { NotificacaoApp, HistoricoAtividade } from '../models/notification.model';
import { SessionService } from './session.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { Role } from '@core/models/role.model';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private sessionService = inject(SessionService);
  private readonly roleAtual = toSignal(this.sessionService.userRole$, { initialValue: null });
  private notificacoesSignal = signal<NotificacaoApp[]>([]);
  private historicoSignal = signal<HistoricoAtividade[]>([]);

  readonly notificacoes = this.notificacoesSignal.asReadonly();
  readonly historico = this.historicoSignal.asReadonly();
  readonly totalNaoLidas = computed(() => this.notificacoesSignal().filter(n => !n.lida).length);

  constructor() {
    effect(() => {
      this.carregarMockParaPapel(this.roleAtual());
    });
  }

  //Funções das Notificações
  marcarComoLida(id: string): void {
    this.notificacoesSignal.update(notificacoes => notificacoes.map(n => (n.id === id ? { ...n, lida: true } : n)));
  }

  marcarTodasComoLidas(): void {
    this.notificacoesSignal.update(notificacoes => notificacoes.map(n => ({ ...n, lida: true })));
  }

  private carregarMockParaPapel(role: Role | null): void {
    if (!role) {
      this.notificacoesSignal.set([]);
      this.historicoSignal.set([]);

      return;
    }

    const mocks = this.getMockParaPapel(role);
    this.notificacoesSignal.set(mocks.notificacoes);
    this.historicoSignal.set(mocks.historico);
  }

  private getMockParaPapel(role: Role): { notificacoes: NotificacaoApp[]; historico: HistoricoAtividade[] } {
    const mocks: Record<Role, { notificacoes: NotificacaoApp[]; historico: HistoricoAtividade[] }> = {
      [Role.ADMIN]: {
        notificacoes: [
          {
            id: '1',
            titulo: 'Nova ONG Cadastrada',
            mensagem: 'A ONG Anjos de Patas aguarda aprovação no sistema.',
            data: 'Há 5 min',
            lida: false,
            tipo: 'warning',
          },
          {
            id: '2',
            titulo: 'Alerta de Sistema',
            mensagem: 'O servidor de imagens apresenta instabilidade temporária.',
            data: 'Há 2 horas',
            lida: false,
            tipo: 'error',
          },
          {
            id: '3',
            titulo: 'Relatório Mensal',
            mensagem: 'O relatório consolidado de adoções está pronto para download.',
            data: 'Ontem',
            lida: true,
            tipo: 'info',
          },
        ],
        historico: [
          {
            id: 'h1',
            acao: 'Aprovação de Petshop',
            descricao: 'Você aprovou o cadastro do "Petshop Feliz".',
            data: 'Hoje às 14:30',
            icone: 'fas fa-check-circle',
            tipo: 'system',
          },
          {
            id: 'h2',
            acao: 'Acesso Administrativo',
            descricao: 'Novo login detectado com suas credenciais.',
            data: 'Hoje às 09:00',
            icone: 'fas fa-sign-in-alt',
            tipo: 'auth',
          },
          {
            id: 'h3',
            acao: 'Atualização de Sistema',
            descricao: 'Patch de segurança v1.2 aplicado com sucesso.',
            data: 'Ontem',
            icone: 'fas fa-wrench',
            tipo: 'system',
          },
        ],
      },

      [Role.ONG]: {
        notificacoes: [
          {
            id: '1',
            titulo: 'Nova Solicitação de Adoção',
            mensagem: 'Ana Silva solicitou a adoção da pet Jade.',
            data: 'Há 10 min',
            lida: false,
            tipo: 'success',
          },
          { id: '2', titulo: 'Mensagem Recebida', mensagem: 'Você tem uma nova mensagem de João.', data: 'Há 1 hora', lida: false, tipo: 'info' },
          {
            id: '3',
            titulo: 'Evento Aprovado',
            mensagem: 'Sua feira de adoção foi aprovada pela moderação.',
            data: 'Ontem',
            lida: true,
            tipo: 'success',
          },
        ],
        historico: [
          {
            id: 'h1',
            acao: 'Pet Cadastrado',
            descricao: 'Você cadastrou o pet "Caramelo".',
            data: 'Hoje às 10:15',
            icone: 'fas fa-paw',
            tipo: 'pet',
          },
          {
            id: 'h2',
            acao: 'Solicitação Respondida',
            descricao: 'Você recusou a solicitação de Marcos.',
            data: 'Ontem',
            icone: 'fas fa-times-circle',
            tipo: 'adoption',
          },
          {
            id: 'h3',
            acao: 'Login Efetuado',
            descricao: 'Acesso realizado pelo aplicativo MyBuddy.',
            data: 'Ontem',
            icone: 'fas fa-sign-in-alt',
            tipo: 'auth',
          },
        ],
      },

      [Role.PETSHOP]: {
        notificacoes: [
          {
            id: '1',
            titulo: 'Novo Pedido',
            mensagem: 'Pedido #4928 recebido no valor de R$ 145,00.',
            data: 'Há 2 min',
            lida: false,
            tipo: 'success',
          },
          {
            id: '2',
            titulo: 'Estoque Baixo',
            mensagem: 'Ração Golden Adultos está com menos de 5 unidades.',
            data: 'Há 3 horas',
            lida: false,
            tipo: 'warning',
          },
          {
            id: '3',
            titulo: 'Mensagem de Cliente',
            mensagem: 'Carlos perguntou sobre a coleira refletiva.',
            data: 'Ontem',
            lida: true,
            tipo: 'info',
          },
        ],
        historico: [
          {
            id: 'h1',
            acao: 'Produto Atualizado',
            descricao: 'Preço atualizado para "Cama Pet G".',
            data: 'Hoje às 11:20',
            icone: 'fas fa-box',
            tipo: 'system',
          },
          {
            id: 'h2',
            acao: 'Pedido Enviado',
            descricao: 'O pedido #4910 foi marcado como enviado.',
            data: 'Ontem',
            icone: 'fas fa-truck',
            tipo: 'system',
          },
          {
            id: 'h3',
            acao: 'Login Efetuado',
            descricao: 'Acesso realizado pelo painel do lojista.',
            data: 'Ontem',
            icone: 'fas fa-sign-in-alt',
            tipo: 'auth',
          },
        ],
      },

      [Role.USER]: {
        notificacoes: [
          {
            id: '1',
            titulo: 'Adoção Aprovada!',
            mensagem: 'Parabéns, a ONG Cão Sem Dono aprovou sua solicitação para a Jade!',
            data: 'Há 1 hora',
            lida: false,
            tipo: 'success',
          },
          { id: '2', titulo: 'Vacina Próxima', mensagem: 'A vacina do Paçoca vence em 5 dias.', data: 'Há 4 horas', lida: false, tipo: 'warning' },
          {
            id: '3',
            titulo: 'Novo Pet Correspondente',
            mensagem: 'Um novo gatinho que combina com você está disponível.',
            data: 'Ontem',
            lida: true,
            tipo: 'info',
          },
        ],
        historico: [
          {
            id: 'h1',
            acao: 'Favorito Adicionado',
            descricao: 'Você adicionou o pet "Thor" aos favoritos.',
            data: 'Hoje às 15:40',
            icone: 'fas fa-heart',
            tipo: 'pet',
          },
          {
            id: 'h2',
            acao: 'Solicitação Enviada',
            descricao: 'Você solicitou a adoção da "Jade".',
            data: 'Ontem',
            icone: 'fas fa-paper-plane',
            tipo: 'adoption',
          },
          {
            id: 'h3',
            acao: 'Perfil Atualizado',
            descricao: 'Você atualizou sua foto de perfil.',
            data: 'Semana passada',
            icone: 'fas fa-user-edit',
            tipo: 'system',
          },
        ],
      },
    };

    return mocks[role] ?? { notificacoes: [], historico: [] };
  }
}
