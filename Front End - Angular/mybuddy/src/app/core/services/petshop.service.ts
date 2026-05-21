import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ProdutoPetshop, PedidoPetshop, ChatPetshop } from '../models/petshop.model';

@Injectable({
  providedIn: 'root'
})
export class PetshopService {
  constructor() {}

  getProdutos(): Observable<ProdutoPetshop[]> {
    return of([
      { nome: 'Ração Golden Premier 15kg', categoria: 'Ração', preco: 'R$ 149,90', estoque: 15, status: 'Ativo' },
      { nome: 'Coleira Anti-pulgas', categoria: 'Acessórios', preco: 'R$ 89,90', estoque: 0, status: 'Esgotado' },
      { nome: 'Shampoo Neutro 500ml', categoria: 'Higiene', preco: 'R$ 35,50', estoque: 22, status: 'Ativo' },
      { nome: 'Brinquedo Osso de Borracha', categoria: 'Brinquedos', preco: 'R$ 15,90', estoque: 50, status: 'Pausado' }
    ]);
  }

  getPedidos(): Observable<PedidoPetshop[]> {
    return of([
      { id: 'PED-1024', cliente: 'Lucas Martins', valor: 'R$ 149,90', data: '21 Mai, 2026', status: 'Aprovado' },
      { id: 'PED-1025', cliente: 'Mariana Costa', valor: 'R$ 89,90', data: '20 Mai, 2026', status: 'Enviado' },
      { id: 'PED-1026', cliente: 'João Silva', valor: 'R$ 35,50', data: '19 Mai, 2026', status: 'Entregue' }
    ]);
  }

  getChats(): Observable<ChatPetshop[]> {
    return of([
      { cliente: 'Ana Souza', ultimaMensagem: 'Boa tarde, o produto X tem garantia?', horario: '14:32', status: 'Não Lido' },
      { cliente: 'Carlos Lima', ultimaMensagem: 'Obrigado pelo envio rápido!', horario: 'Ontem', status: 'Lido' }
    ]);
  }
}
