import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppNotification, ActivityHistory } from '../models/notification.model';
import { SessionService } from './session.service';
import { Role } from '../models/role.model';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private sessionService = inject(SessionService);

  private notificationsSubject = new BehaviorSubject<AppNotification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  private historySubject = new BehaviorSubject<ActivityHistory[]>([]);
  public history$ = this.historySubject.asObservable();

  constructor() {
    this.sessionService.userRole$.subscribe(role => {
      this.loadMockDataForRole(role);
    });
  }

  public markAsRead(id: string) {
    const current = this.notificationsSubject.getValue();
    const updated = current.map(n => (n.id === id ? { ...n, read: true } : n));
    this.notificationsSubject.next(updated);
  }

  public markAllAsRead() {
    const current = this.notificationsSubject.getValue();
    const updated = current.map(n => ({ ...n, read: true }));
    this.notificationsSubject.next(updated);
  }

  public getUnreadCount(): Observable<number> {
    return new Observable<number>(observer => {
      this.notifications$.subscribe(notifications => {
        observer.next(notifications.filter(n => !n.read).length);
      });
    });
  }

  private loadMockDataForRole(role: Role | null) {
    if (!role) {
      this.notificationsSubject.next([]);
      this.historySubject.next([]);
      return;
    }

    if (role === Role.ADMIN) {
      this.notificationsSubject.next([
        {
          id: '1',
          title: 'Nova ONG Cadastrada',
          message: 'A ONG Anjos de Patas aguarda aprovação no sistema.',
          date: 'Há 5 min',
          read: false,
          type: 'warning',
        },
        {
          id: '2',
          title: 'Alerta de Sistema',
          message: 'O servidor de imagens apresenta instabilidade temporária.',
          date: 'Há 2 horas',
          read: false,
          type: 'error',
        },
        {
          id: '3',
          title: 'Relatório Mensal',
          message: 'O relatório consolidado de adoções está pronto para download.',
          date: 'Ontem',
          read: true,
          type: 'info',
        },
      ]);
      this.historySubject.next([
        {
          id: 'h1',
          action: 'Aprovação de Petshop',
          description: 'Você aprovou o cadastro do "Petshop Feliz".',
          date: 'Hoje às 14:30',
          icon: 'fas fa-check-circle',
          type: 'system',
        },
        {
          id: 'h2',
          action: 'Acesso Administrativo',
          description: 'Novo login detectado com suas credenciais.',
          date: 'Hoje às 09:00',
          icon: 'fas fa-sign-in-alt',
          type: 'auth',
        },
        {
          id: 'h3',
          action: 'Atualização de Sistema',
          description: 'Patch de segurança v1.2 aplicado com sucesso.',
          date: 'Ontem',
          icon: 'fas fa-wrench',
          type: 'system',
        },
      ]);
    } else if (role === Role.ONG) {
      this.notificationsSubject.next([
        {
          id: '1',
          title: 'Nova Solicitação de Adoção',
          message: 'Ana Silva solicitou a adoção da pet Jade.',
          date: 'Há 10 min',
          read: false,
          type: 'success',
        },
        { id: '2', title: 'Mensagem Recebida', message: 'Você tem uma nova mensagem de João.', date: 'Há 1 hora', read: false, type: 'info' },
        {
          id: '3',
          title: 'Evento Aprovado',
          message: 'Sua feira de adoção foi aprovada pela moderação.',
          date: 'Ontem',
          read: true,
          type: 'success',
        },
      ]);
      this.historySubject.next([
        {
          id: 'h1',
          action: 'Pet Cadastrado',
          description: 'Você cadastrou o pet "Caramelo".',
          date: 'Hoje às 10:15',
          icon: 'fas fa-paw',
          type: 'pet',
        },
        {
          id: 'h2',
          action: 'Solicitação Respondida',
          description: 'Você recusou a solicitação de Marcos.',
          date: 'Ontem',
          icon: 'fas fa-times-circle',
          type: 'adoption',
        },
        {
          id: 'h3',
          action: 'Login Efetuado',
          description: 'Acesso realizado pelo aplicativo MyBuddy.',
          date: 'Ontem',
          icon: 'fas fa-sign-in-alt',
          type: 'auth',
        },
      ]);
    } else if (role === Role.PETSHOP) {
      this.notificationsSubject.next([
        { id: '1', title: 'Novo Pedido', message: 'Pedido #4928 recebido no valor de R$ 145,00.', date: 'Há 2 min', read: false, type: 'success' },
        {
          id: '2',
          title: 'Estoque Baixo',
          message: 'Ração Golden Adultos está com menos de 5 unidades.',
          date: 'Há 3 horas',
          read: false,
          type: 'warning',
        },
        { id: '3', title: 'Mensagem de Cliente', message: 'Carlos perguntou sobre a coleira refletiva.', date: 'Ontem', read: true, type: 'info' },
      ]);
      this.historySubject.next([
        {
          id: 'h1',
          action: 'Produto Atualizado',
          description: 'Preço atualizado para "Cama Pet G".',
          date: 'Hoje às 11:20',
          icon: 'fas fa-box',
          type: 'system',
        },
        {
          id: 'h2',
          action: 'Pedido Enviado',
          description: 'O pedido #4910 foi marcado como enviado.',
          date: 'Ontem',
          icon: 'fas fa-truck',
          type: 'system',
        },
        {
          id: 'h3',
          action: 'Login Efetuado',
          description: 'Acesso realizado pelo painel do lojista.',
          date: 'Ontem',
          icon: 'fas fa-sign-in-alt',
          type: 'auth',
        },
      ]);
    } else {
      // Adotante
      this.notificationsSubject.next([
        {
          id: '1',
          title: 'Adoção Aprovada!',
          message: 'Parabéns, a ONG Cão Sem Dono aprovou sua solicitação para a Jade!',
          date: 'Há 1 hora',
          read: false,
          type: 'success',
        },
        { id: '2', title: 'Vacina Próxima', message: 'A vacina do Paçoca vence em 5 dias.', date: 'Há 4 horas', read: false, type: 'warning' },
        {
          id: '3',
          title: 'Novo Pet Correspondente',
          message: 'Um novo gatinho que combina com você está disponível.',
          date: 'Ontem',
          read: true,
          type: 'info',
        },
      ]);
      this.historySubject.next([
        {
          id: 'h1',
          action: 'Favorito Adicionado',
          description: 'Você adicionou o pet "Thor" aos favoritos.',
          date: 'Hoje às 15:40',
          icon: 'fas fa-heart',
          type: 'pet',
        },
        {
          id: 'h2',
          action: 'Solicitação Enviada',
          description: 'Você solicitou a adoção da "Jade".',
          date: 'Ontem',
          icon: 'fas fa-paper-plane',
          type: 'adoption',
        },
        {
          id: 'h3',
          action: 'Perfil Atualizado',
          description: 'Você atualizou sua foto de perfil.',
          date: 'Semana passada',
          icon: 'fas fa-user-edit',
          type: 'system',
        },
      ]);
    }
  }
}
