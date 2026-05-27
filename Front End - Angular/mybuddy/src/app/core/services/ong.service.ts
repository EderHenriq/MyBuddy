import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { SolicitacaoAdocao, EventoOng, MeuPetOng } from '../models/ong.model';

@Injectable({
  providedIn: 'root',
})
export class OngService {
  constructor() {}

  getSolicitacoes(): Observable<SolicitacaoAdocao[]> {
    return of([
      { id: 'ADO-001', pet: 'Bolinha', adotante: 'Ana Souza', data: '21 Mai, 2026', status: 'Em Análise' },
      { id: 'ADO-002', pet: 'Max', adotante: 'Carlos Lima', data: '20 Mai, 2026', status: 'Aprovado' },
      { id: 'ADO-003', pet: 'Luna', adotante: 'Mariana Silva', data: '18 Mai, 2026', status: 'Reprovado' },
    ]);
  }

  getEventos(): Observable<EventoOng[]> {
    return of([
      { nome: 'Feirinha de Adoção de Inverno', local: 'Parque Ibirapuera', data: '25 Jul, 2026', status: 'Agendado' },
      { nome: 'Campanha de Arrecadação de Ração', local: 'Sede da ONG', data: '10 Jun, 2026', status: 'Agendado' },
      { nome: 'Mega Adoção de Maio', local: 'Shopping Cidade', data: '05 Mai, 2026', status: 'Concluído' },
    ]);
  }

  getMeusPets(): Observable<MeuPetOng[]> {
    return of([
      { nome: 'Rex', especie: 'Cachorro', raca: 'Vira-lata', idade: '2 Anos', status: 'Disponível' },
      { nome: 'Mia', especie: 'Gato', raca: 'Siamês', idade: '1 Ano', status: 'Em Processo' },
      { nome: 'Thor', especie: 'Cachorro', raca: 'Pastor Alemão', idade: '3 Anos', status: 'Adotado' },
    ]);
  }
}
