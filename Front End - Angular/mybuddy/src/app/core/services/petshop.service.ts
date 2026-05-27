import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Produto, Pedido, ChatPetshop } from '../models/petshop.model';

@Injectable({
  providedIn: 'root',
})
export class PetshopService {
  private api = inject(ApiService);

  constructor() {}

  buscarProdutos(): Observable<Produto[]> {
    return this.api.get<Produto[]>('petshop/produtos');
  }
  buscarPedidos(): Observable<Pedido[]> {
    return this.api.get<Pedido[]>('petshop/pedidos');
  }
  buscarChats(): Observable<ChatPetshop[]> {
    return this.api.get<ChatPetshop[]>('petshop/chats');
  }
}
