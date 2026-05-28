import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { NotificacaoApp, HistoricoAtividade } from '../models/notification.model';
import { SessionService } from './session.service';
import { Role } from '../models/role.model';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private sessionService = inject(SessionService);

  private notificacoesSubject = new BehaviorSubject<NotificacaoApp[]>([]);
  public notificacoes$ = this.notificacoesSubject.asObservable();

  private historicoSubject = new BehaviorSubject<HistoricoAtividade[]>([]);
  public historico$ = this.historicoSubject.asObservable();

  constructor() {
    this.sessionService.userRole$.subscribe(role => {
      this.carregarMockParaPapel(role);
    });
  }

  public marcarComoLida(id: string) {
    const atual = this.notificacoesSubject.getValue();
    const atualizado = atual.map(n => (n.id === id ? { ...n, lida: true } : n));
    this.notificacoesSubject.next(atualizado);
  }

  public marcarTodasComoLidas() {
    const atual = this.notificacoesSubject.getValue();
    const atualizado = atual.map(n => ({ ...n, lida: true }));
    this.notificacoesSubject.next(atualizado);
  }

  public buscarContagemNaoLidas(): Observable<number> {
    return new Observable<number>(observer => {
      this.notificacoes$.subscribe(notificacoes => {
        observer.next(notificacoes.filter(n => !n.lida).length);
      });
    });
  }

  private carregarMockParaPapel(role: Role | null) {
    if (!role) {
      this.notificacoesSubject.next([]);
      this.historicoSubject.next([]);
      return;
    }

    if (role === Role.ADMIN) {
      this.notificacoesSubject.next([
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
      ]);
      this.historicoSubject.next([
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
      ]);
    } else if (role === Role.ONG) {
      this.notificacoesSubject.next([
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
      ]);
      this.historicoSubject.next([
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
      ]);
    } else if (role === Role.PETSHOP) {
      this.notificacoesSubject.next([
        { id: '1', titulo: 'Novo Pedido', mensagem: 'Pedido #4928 recebido no valor de R$ 145,00.', data: 'Há 2 min', lida: false, tipo: 'success' },
        {
          id: '2',
          titulo: 'Estoque Baixo',
          mensagem: 'Ração Golden Adultos está com menos de 5 unidades.',
          data: 'Há 3 horas',
          lida: false,
          tipo: 'warning',
        },
        { id: '3', titulo: 'Mensagem de Cliente', mensagem: 'Carlos perguntou sobre a coleira refletiva.', data: 'Ontem', lida: true, tipo: 'info' },
      ]);
      this.historicoSubject.next([
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
      ]);
    } else {
      this.notificacoesSubject.next([
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
      ]);
      this.historicoSubject.next([
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
      ]);
    }
  }
}
