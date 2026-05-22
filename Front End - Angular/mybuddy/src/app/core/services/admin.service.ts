import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Parceria, AdminUsuario, PetDenuncia, Ticket } from '../models/admin.model';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  constructor() {}

  getOngs(): Observable<Parceria[]> {
    return of([
      { id: 1, nome: 'Anjos de Patas', tipo: 'ONG', cidade: 'São Paulo, SP', status: 'Ativo', dataCadastro: '12 Fev, 2026' },
      { id: 2, nome: 'PetLovers Shop', tipo: 'Petshop', cidade: 'Campinas, SP', status: 'Ativo', dataCadastro: '05 Mar, 2026' },
      { id: 3, nome: 'Gatos do Bairro', tipo: 'ONG', cidade: 'Curitiba, PR', status: 'Pendente', dataCadastro: '20 Mai, 2026' },
    ]);
  }

  getUsuarios(): Observable<AdminUsuario[]> {
    return of([
      { id: 101, nome: 'Carlos Eduardo', email: 'carlos@email.com', cidade: 'São Paulo, SP', status: 'Ativo', dataCadastro: '10 Jan, 2026' },
      { id: 102, nome: 'Ana Beatriz', email: 'ana.b@email.com', cidade: 'Belo Horizonte, MG', status: 'Inativo', dataCadastro: '22 Fev, 2026' },
      { id: 103, nome: 'João Pedro', email: 'joao.p@email.com', cidade: 'Rio de Janeiro, RJ', status: 'Ativo', dataCadastro: '15 Mai, 2026' },
    ]);
  }

  getPetsDenuncias(): Observable<PetDenuncia[]> {
    return of([
      { id: 501, nomePet: 'Max', ong: 'Anjos de Patas', motivo: 'Informações falsas', dataDenuncia: '20 Mai, 2026', status: 'Pendente' },
      { id: 502, nomePet: 'Luna', ong: 'Gatos do Bairro', motivo: 'Falta de resposta', dataDenuncia: '18 Mai, 2026', status: 'Analisado' },
    ]);
  }

  getTicketsSuporte(): Observable<Ticket[]> {
    return of([
      { id: 'TK-1001', assunto: 'Erro no cadastro de ONG', usuario: 'Ong Esperança', prioridade: 'Alta', status: 'Aberto', data: '21 Mai, 2026' },
      {
        id: 'TK-1002',
        assunto: 'Dúvida sobre repasse',
        usuario: 'Petshop Cão Feliz',
        prioridade: 'Média',
        status: 'Em Andamento',
        data: '20 Mai, 2026',
      },
      { id: 'TK-1003', assunto: 'Como alterar e-mail', usuario: 'Maria Santos', prioridade: 'Baixa', status: 'Resolvido', data: '15 Mai, 2026' },
    ]);
  }
}
