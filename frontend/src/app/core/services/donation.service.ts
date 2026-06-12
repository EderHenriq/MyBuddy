import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiService } from './api.service';

export interface CampanhaDoacao {
  id: number;
  titulo: string;
  descricao: string;
  meta: number;
  arrecadado: number;
  petId?: number;
  organizacaoId: number;
  categoria: string;
  dataExpiracao: string;
  status: string;
  imagemUrl?: string;
  localizacao?: string;
  diasRestantes?: number;
  totalDoadores?: number;
}

export interface OngParceira {
  id: number;
  nome: string;
  localizacao: string;
  totalAnimais: number;
  descricao: string;
  necessidades: string[];
  imagemUrl?: string;
}

export interface DonationStats {
  petsSalvos: number;
  ongsParceiras: number;
  totalArrecadado: number;
  doadoresAtivos: number;
}

@Injectable({
  providedIn: 'root',
})
export class DonationService {
  private api = inject(ApiService);

  getStats(): Observable<DonationStats> {
    return this.api.get<DonationStats>('donations/stats').pipe(
      catchError(() =>
        of({
          petsSalvos: 1240,
          ongsParceiras: 38,
          totalArrecadado: 92000,
          doadoresAtivos: 4800,
        }),
      ),
    );
  }

  getCampaigns(categoria?: string): Observable<CampanhaDoacao[]> {
    const path = categoria && categoria !== 'Todos' ? `campanhas?categoria=${encodeURIComponent(categoria)}` : 'campanhas';
    return this.api.get<CampanhaDoacao[]>(path).pipe(catchError(() => of(this.getMockCampaigns(categoria))));
  }

  getOngsParceiras(): Observable<OngParceira[]> {
    return this.api.get<OngParceira[]>('ongs/parceiras').pipe(catchError(() => of(this.getMockOngs())));
  }

  createSingleDonation(amount: number, description: string, petId?: number, campanhaId?: number, organizacaoId?: number): Observable<any> {
    return this.api.post('payments/create', {
      amount,
      description,
      petId,
      campanhaId,
      organizacaoId,
    });
  }

  createRecurringDonation(amount: number, frequency: 'weekly' | 'monthly', organizacaoId?: number): Observable<any> {
    return this.api.post('payments/subscribe', {
      amount,
      frequency,
      organizacaoId,
    });
  }

  private getMockCampaigns(categoria?: string): CampanhaDoacao[] {
    const all: CampanhaDoacao[] = [
      {
        id: 1,
        titulo: 'Operação para o Bolinha',
        descricao: 'O Bolinha precisa de uma cirurgia urgente na pata traseira. Ajude!',
        meta: 3000,
        arrecadado: 1800,
        organizacaoId: 1,
        categoria: 'Cirurgias',
        dataExpiracao: '2025-12-31',
        status: 'ativo',
        localizacao: 'São Paulo, SP',
        diasRestantes: 12,
        totalDoadores: 84,
        imagemUrl: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=600',
      },
      {
        id: 2,
        titulo: 'Ração para 40 gatos',
        descricao: 'Nosso abrigo está precisando de ração para alimentar 40 felinos resgatados.',
        meta: 1200,
        arrecadado: 700,
        organizacaoId: 2,
        categoria: 'Ração e alimentação',
        dataExpiracao: '2025-12-15',
        status: 'ativo',
        localizacao: 'Curitiba, PR',
        diasRestantes: 5,
        totalDoadores: 41,
        imagemUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=600',
      },
      {
        id: 3,
        titulo: 'Tratamento da Mel',
        descricao: 'A cachorrinha Mel foi resgatada e precisa de tratamento contínuo.',
        meta: 2400,
        arrecadado: 2400,
        organizacaoId: 1,
        categoria: 'Pets em tratamento',
        dataExpiracao: '2025-11-30',
        status: 'concluido',
        localizacao: 'Campinas, SP',
        diasRestantes: 0,
        totalDoadores: 127,
        imagemUrl: 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&q=80&w=600',
      },
      {
        id: 4,
        titulo: 'Reforma do Abrigo Patinhas',
        descricao: 'Precisamos reformar as instalações para oferecer mais dignidade aos animais.',
        meta: 8000,
        arrecadado: 3200,
        organizacaoId: 3,
        categoria: 'Abrigo / ONG',
        dataExpiracao: '2025-12-20',
        status: 'ativo',
        localizacao: 'Porto Alegre, RS',
        diasRestantes: 20,
        totalDoadores: 63,
        imagemUrl: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?auto=format&fit=crop&q=80&w=600',
      },
      {
        id: 5,
        titulo: '🚨 URGENTE: Cachorra atropelada',
        descricao: 'Luna foi atropelada e precisa de atendimento imediato. Cada real conta!',
        meta: 1500,
        arrecadado: 900,
        organizacaoId: 2,
        categoria: 'Urgente',
        dataExpiracao: '2025-11-10',
        status: 'ativo',
        localizacao: 'Recife, PE',
        diasRestantes: 2,
        totalDoadores: 98,
        imagemUrl: 'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&q=80&w=600',
      },
    ];
    if (!categoria || categoria === 'Todos') return all;
    return all.filter(c => c.categoria === categoria);
  }

  private getMockOngs(): OngParceira[] {
    return [
      {
        id: 1,
        nome: 'Patinhas Unidas',
        localizacao: 'São Paulo, SP',
        totalAnimais: 38,
        descricao: 'Abrigo precisa de ração, medicamentos e voluntários para cuidados diários.',
        necessidades: ['Ração', 'Medicamentos', 'Voluntários'],
        imagemUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=200',
      },
      {
        id: 2,
        nome: 'Abrigo Vida Animal',
        localizacao: 'Curitiba, PR',
        totalAnimais: 62,
        descricao: 'Necessidade urgente de ração e recursos para cirurgias de emergência.',
        necessidades: ['Ração', 'Cirurgias', 'Urgente'],
        imagemUrl: 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&q=80&w=200',
      },
      {
        id: 3,
        nome: 'Resgate Felino SP',
        localizacao: 'Guarulhos, SP',
        totalAnimais: 21,
        descricao: 'Foco em castrações e controle populacional felino na região metropolitana.',
        necessidades: ['Castrações', 'Ração', 'Adoção'],
        imagemUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=200',
      },
    ];
  }
}
