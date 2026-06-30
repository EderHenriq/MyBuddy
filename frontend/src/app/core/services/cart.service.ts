import { Injectable, computed, signal } from '@angular/core';

export interface ItemCarrinho {
  id: number;
  nome: string;
  preco: number;
  urlImagem: string;
  quantidade: number;
  lojaNome?: string;
  petshopId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private itensCarrinhoSignal = signal<ItemCarrinho[]>([]);
  private gavetaAbertaSignal = signal<boolean>(false);

  readonly itensCarrinho = this.itensCarrinhoSignal.asReadonly();
  readonly gavetaAberta = this.gavetaAbertaSignal.asReadonly();

  readonly totalItens = computed(() => this.itensCarrinhoSignal().reduce((soma, item) => soma + item.quantidade, 0));
  readonly precoTotal = computed(() => this.itensCarrinhoSignal().reduce((soma, item) => soma + item.preco * item.quantidade, 0));

  alternarGaveta(): void {
    this.gavetaAbertaSignal.update(estado => !estado);
  }

  abrirGaveta(): void {
    this.gavetaAbertaSignal.set(true);
  }

  fecharGaveta(): void {
    this.gavetaAbertaSignal.set(false);
  }

  adicionarAoCarrinho(item: Omit<ItemCarrinho, 'quantidade'>): void {
    const itensAtuais = this.itensCarrinhoSignal();
    const itemExistente = itensAtuais.find(i => i.id === item.id);

    if (itemExistente) {
      this.atualizarQuantidade(item.id, itemExistente.quantidade + 1);
    } else {
      this.itensCarrinhoSignal.set([...itensAtuais, { ...item, quantidade: 1 }]);
    }
  }

  removerDoCarrinho(itemId: number): void {
    this.itensCarrinhoSignal.update(itens => itens.filter(i => i.id !== itemId));
  }

  atualizarQuantidade(itemId: number, quantidade: number): void {
    if (quantidade <= 0) {
      this.removerDoCarrinho(itemId);
      return;
    }
    this.itensCarrinhoSignal.update(itens => itens.map(item => (item.id === itemId ? { ...item, quantidade } : item)));
  }

  limparCarrinho(): void {
    this.itensCarrinhoSignal.set([]);
  }
}
