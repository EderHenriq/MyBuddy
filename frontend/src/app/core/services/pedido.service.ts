import { Injectable, inject } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiService } from './api.service';
import { Pedido } from '../models/petshop.model';

export interface ItemPedidoRequest {
  produtoId: number;
  quantidade: number;
}

export interface PedidoRequest {
  petshopId: number;
  itens: ItemPedidoRequest[];
  enderecoEntregaId?: number; // Para uso do banco de dados relacional
  enderecoEntregaSimulado?: string;
  metodoPagamento: string;
}

@Injectable({
  providedIn: 'root',
})
export class PedidoService {
  private api = inject(ApiService);
  private localPedidosKey = 'mybuddy_pedidos_local';

  constructor() {
    this.inicializarPedidosLocais();
  }

  // Cria um pedido enviando para o backend, com fallback para localStorage em caso de erro/offline
  criarPedido(request: PedidoRequest): Observable<any> {
    return this.api.post<any>('pedidos', request).pipe(
      catchError(err => {
        console.warn('[PedidoService] Falha na API ao criar pedido. Usando fallback de Mock local.', err);
        const novoPedido = this.criarMockLocal(request);
        return of(novoPedido);
      }),
    );
  }

  // Lista os pedidos do cliente logado, com fallback local
  listarMeusPedidos(): Observable<any[]> {
    return this.api.get<any[]>('pedidos/meus').pipe(
      catchError(err => {
        console.warn('[PedidoService] Falha na API ao listar meus pedidos. Usando fallback de Mock local.', err);
        const pedidosLocais = this.obterPedidosLocais();
        return of(pedidosLocais);
      }),
    );
  }

  // Lista os pedidos do lojista (petshop), com fallback local
  listarPedidosPetshop(): Observable<any[]> {
    return this.api.get<any[]>('pedidos/petshop').pipe(
      catchError(err => {
        console.warn('[PedidoService] Falha na API ao listar pedidos do petshop. Usando de Mock local.', err);
        const pedidosLocais = this.obterPedidosLocais();
        return of(pedidosLocais);
      }),
    );
  }

  // Atualiza o status do pedido, com fallback local
  atualizarStatus(id: number, status: string): Observable<any> {
    return this.api.put<any>(`pedidos/${id}/status?status=${status}`, {}).pipe(
      catchError(err => {
        console.warn('[PedidoService] Falha na API ao atualizar status. Atualizando localmente.', err);
        const pedidos = this.obterPedidosLocais();
        const index = pedidos.findIndex(p => p.id === id);
        if (index !== -1) {
          pedidos[index].status = status;
          pedidos[index].dataAtualizacao = new Date().toISOString();
          this.salvarPedidosLocais(pedidos);
          return of(pedidos[index]);
        }
        return throwError(() => new Error('Pedido não encontrado'));
      }),
    );
  }

  // Cancela o pedido, com fallback local
  cancelarPedido(id: number): Observable<any> {
    return this.api.post<any>(`pedidos/${id}/cancelar`, {}).pipe(
      catchError(err => {
        console.warn('[PedidoService] Falha na API ao cancelar pedido. Atualizando localmente.', err);
        return this.atualizarStatus(id, 'CANCELADO');
      }),
    );
  }

  // Valida um cupom na API ou localmente
  validarCupom(codigo: string, petshopId: number): Observable<any> {
    return this.api.get<any>(`cupons/validar?codigo=${codigo}&petshopId=${petshopId}`).pipe(
      catchError(err => {
        console.warn('[PedidoService] Falha na API ao validar cupom. Usando fallback local.', err);
        const codigoUpper = codigo.toUpperCase().trim();
        if (codigoUpper === 'BUDDY10') {
          return of({ id: 1, codigo: 'BUDDY10', percentualDesconto: 10.0, ativo: true });
        } else if (codigoUpper === 'MEUPET20') {
          return of({ id: 2, codigo: 'MEUPET20', percentualDesconto: 20.0, ativo: true });
        }
        return throwError(() => new Error('Cupom inválido ou expirado.'));
      }),
    );
  }

  buscarCupons(): Observable<any[]> {
    return this.api.get<any[]>('cupons').pipe(
      catchError(err => {
        console.warn('[PedidoService] Falha na API ao listar cupons. Usando mock local.', err);
        return of(this.obterCuponsLocais());
      }),
    );
  }

  criarCupom(request: any): Observable<any> {
    return this.api.post<any>('cupons', request).pipe(
      catchError(err => {
        console.warn('[PedidoService] Falha na API ao criar cupom. Usando mock local.', err);
        const novo = this.criarCupomMock(request);
        return of(novo);
      }),
    );
  }

  alterarStatusCupom(id: number, ativo: boolean): Observable<any> {
    return this.api.put<any>(`cupons/${id}/status?ativo=${ativo}`, {}).pipe(
      catchError(err => {
        console.warn('[PedidoService] Falha na API ao alterar status do cupom. Atualizando localmente.', err);
        const cupons = this.obterCuponsLocais();
        const idx = cupons.findIndex(c => c.id === id);
        if (idx !== -1) {
          cupons[idx].ativo = ativo;
          this.salvarCuponsLocais(cupons);
          return of(cupons[idx]);
        }
        return throwError(() => new Error('Cupom não encontrado'));
      }),
    );
  }

  // MOCK CUPONS
  private obterCuponsLocais(): any[] {
    const key = 'mybuddy_cupons_local';
    const data = localStorage.getItem(key);
    if (!data) {
      const inicial = [
        {
          id: 1,
          codigo: 'BUDDY10',
          percentualDesconto: 10.0,
          ativo: true,
          dataInicio: '2026-01-01',
          dataExpiracao: '2026-12-31',
          valorMinimoPedido: 50.0,
          limiteUsoGeral: 100,
          usoAtual: 14,
        },
        {
          id: 2,
          codigo: 'MEUPET20',
          percentualDesconto: 20.0,
          ativo: true,
          dataInicio: '2026-01-01',
          dataExpiracao: '2026-12-31',
          valorMinimoPedido: 100.0,
          limiteUsoGeral: 50,
          usoAtual: 5,
        },
        {
          id: 3,
          codigo: 'BOASVINDAS',
          percentualDesconto: 15.0,
          ativo: false,
          dataInicio: '2026-01-01',
          dataExpiracao: '2026-06-01',
          valorMinimoPedido: 0,
          limiteUsoGeral: 500,
          usoAtual: 500,
        },
      ];
      localStorage.setItem(key, JSON.stringify(inicial));
      return inicial;
    }
    return JSON.parse(data);
  }

  private salvarCuponsLocais(cupons: any[]): void {
    localStorage.setItem('mybuddy_cupons_local', JSON.stringify(cupons));
  }

  private criarCupomMock(request: any): any {
    const cupons = this.obterCuponsLocais();
    const novoId = cupons.length > 0 ? Math.max(...cupons.map(c => c.id)) + 1 : 1;
    const novo = {
      id: novoId,
      codigo: request.codigo.toUpperCase(),
      percentualDesconto: request.percentualDesconto,
      ativo: request.ativo !== undefined ? request.ativo : true,
      dataInicio: request.dataInicio || new Date().toISOString().split('T')[0],
      dataExpiracao: request.dataExpiracao || null,
      valorMinimoPedido: request.valorMinimoPedido || null,
      limiteUsoGeral: request.limiteUsoGeral || null,
      usoAtual: 0,
    };
    cupons.unshift(novo);
    this.salvarCuponsLocais(cupons);
    return novo;
  }

  // MÉTODOS DE CONTROLE LOCAL (MOCK)
  private obterPedidosLocais(): any[] {
    const data = localStorage.getItem(this.localPedidosKey);
    return data ? JSON.parse(data) : [];
  }

  private salvarPedidosLocais(pedidos: any[]): void {
    localStorage.setItem(this.localPedidosKey, JSON.stringify(pedidos));
  }

  private criarMockLocal(request: PedidoRequest): any {
    const pedidos = this.obterPedidosLocais();
    const novoId = pedidos.length > 0 ? Math.max(...pedidos.map(p => p.id)) + 1 : 1000;

    // Obter dados simulados de itens
    const itensMapeados = request.itens.map(it => {
      // Simulação simples de produto
      return {
        id: Math.floor(Math.random() * 1000),
        produto: {
          id: it.produtoId,
          nome:
            it.produtoId === 1
              ? 'Ração Premier Formula Cães Adultos Frango'
              : it.produtoId === 2
                ? 'Antipulgas Bravecto para Cães 10 a 20kg'
                : `Produto ID #${it.produtoId}`,
          preco: it.produtoId === 1 ? 189.9 : it.produtoId === 2 ? 215.5 : 49.9,
          categoria: 'Simulado',
          urlImagem:
            it.produtoId === 1
              ? 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=300'
              : 'https://images.unsplash.com/photo-1581888227599-779811939961?auto=format&fit=crop&q=80&w=300',
        },
        quantidade: it.quantidade,
        precoUnitario: it.produtoId === 1 ? 189.9 : it.produtoId === 2 ? 215.5 : 49.9,
      };
    });

    const valorTotal = itensMapeados.reduce((soma, it) => soma + it.precoUnitario * it.quantidade, 0);

    const novoPedido = {
      id: novoId,
      status: 'PROCESSANDO',
      valorTotal: valorTotal + 15.0, // Preço total + 15.0 de frete simulado
      dataCriacao: new Date().toISOString(),
      dataAtualizacao: new Date().toISOString(),
      itens: itensMapeados,
      enderecoEntrega: {
        rua: request.enderecoEntregaSimulado || 'Rua Principal, 123',
        bairro: 'Centro',
        cidade: 'São Paulo',
        uf: 'SP',
      },
      petshop: {
        id: request.petshopId,
        nomeFantasia: request.petshopId === 1 ? 'Petz' : request.petshopId === 2 ? 'Cobasi' : 'Petshop Parceiro',
      },
    };

    pedidos.unshift(novoPedido); // Adiciona no início da lista
    this.salvarPedidosLocais(pedidos);
    return novoPedido;
  }

  private inicializarPedidosLocais(): void {
    const pedidos = this.obterPedidosLocais();
    if (pedidos.length === 0) {
      // Inicia com alguns pedidos fictícios no histórico para o cliente ver algo de início
      const mockInicial = [
        {
          id: 9841,
          status: 'ENTREGUE',
          valorTotal: 65.8,
          dataCriacao: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
          dataAtualizacao: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
          itens: [
            {
              id: 101,
              produto: {
                id: 3,
                nome: 'Tapete Higiênico Super Seco 30 unidades',
                preco: 49.9,
                categoria: 'Higiene',
                urlImagem: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=300',
              },
              quantidade: 1,
              precoUnitario: 49.9,
            },
          ],
          enderecoEntrega: {
            rua: 'Rua das Flores, 123',
            bairro: 'Centro',
            cidade: 'Maringá',
            uf: 'PR',
          },
          petshop: {
            id: 1,
            nomeFantasia: 'Petz',
          },
        },
      ];
      this.salvarPedidosLocais(mockInicial);
    }
  }
}
