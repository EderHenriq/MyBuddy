import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiService } from './api.service';
import { Produto, Pedido, ChatPetshop } from '../models/petshop.model';

@Injectable({
  providedIn: 'root',
})
export class PetshopService {
  private api = inject(ApiService);
  private localPedidosKey = 'mybuddy_pedidos_local';

  constructor() {}

  buscarProdutos(): Observable<Produto[]> {
    return this.api.get<Produto[]>('petshop/produtos').pipe(
      catchError(err => {
        console.warn('[PetshopService] Falha ao buscar produtos da API. Usando mock local.', err);
        const mockProdutos: Produto[] = [
          {
            id: 1,
            nome: 'Ração Premier Formula Cães Adultos Frango',
            categoria: 'Rações',
            preco: 189.9,
            estoque: 24,
            status: 'ATIVO',
          },
          {
            id: 2,
            nome: 'Antipulgas Bravecto para Cães 10 a 20kg',
            categoria: 'Farmácia',
            preco: 215.5,
            estoque: 15,
            status: 'ATIVO',
          },
          {
            id: 3,
            nome: 'Tapete Higiênico Super Seco 30 unidades',
            categoria: 'Higiene',
            preco: 49.9,
            estoque: 0,
            status: 'ESGOTADO',
          },
          {
            id: 4,
            nome: 'Bolinha de Tênis Chalesco para Cães',
            categoria: 'Brinquedos',
            preco: 15.9,
            estoque: 80,
            status: 'ATIVO',
          },
          {
            id: 5,
            nome: 'Cama Pet Conforto Redonda G',
            categoria: 'Camas',
            preco: 110.0,
            estoque: 5,
            status: 'ATIVO',
          },
        ];
        return of(mockProdutos);
      }),
    );
  }

  buscarPedidos(): Observable<any[]> {
    return this.api.get<Pedido[]>('petshop/pedidos').pipe(
      catchError(err => {
        console.warn('[PetshopService] Falha ao buscar pedidos da API. Usando mock unificado do localStorage.', err);
        const data = localStorage.getItem(this.localPedidosKey);
        const pedidosLocais = data ? JSON.parse(data) : [];

        // Se por algum motivo o localStorage estiver limpo, retorna um mock padrão
        if (pedidosLocais.length === 0) {
          const mockPedidos = [
            {
              id: 9841,
              status: 'ENTREGUE',
              valorTotal: 65.8,
              dataCriacao: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
              itens: [
                {
                  id: 101,
                  quantidade: 1,
                  precoUnitario: 49.9,
                  produto: { nome: 'Tapete Higiênico Super Seco 30 unidades' },
                },
              ],
              enderecoEntrega: { rua: 'Rua das Flores, 123', bairro: 'Centro', cidade: 'Maringá', uf: 'PR' },
            },
          ];
          return of(mockPedidos);
        }
        return of(pedidosLocais);
      }),
    );
  }

  buscarChats(): Observable<ChatPetshop[]> {
    return this.api.get<ChatPetshop[]>('petshop/chats').pipe(
      catchError(err => {
        console.warn('[PetshopService] Falha ao buscar chats da API. Usando mock local.', err);
        const mockChats: ChatPetshop[] = [
          {
            cliente: 'Marcos Souza',
            ultimaMensagem: 'Olá! A ração de frango tem em estoque?',
            horario: '10:42',
            status: 'Não Lido',
          },
          {
            cliente: 'Ana Lima',
            ultimaMensagem: 'Meu pedido já saiu para entrega?',
            horario: 'Ontem',
            status: 'Lido',
          },
          {
            cliente: 'Roberto Dias',
            ultimaMensagem: 'Obrigado pelo atendimento rápido!',
            horario: '15/06',
            status: 'Lido',
          },
        ];
        return of(mockChats);
      }),
    );
  }
}
