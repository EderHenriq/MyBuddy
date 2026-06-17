import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";
import { CartService } from "@core/services/cart.service";

@Component({
  selector: "app-cart-drawer",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./cart-drawer.component.html",
  styleUrl: "./cart-drawer.component.scss",
})
export class CartDrawerComponent {
  carrinhoService = inject(CartService);
  private router = inject(Router);

  get aberto() {
    return this.carrinhoService.gavetaAberta();
  }

  get itens() {
    return this.carrinhoService.itensCarrinho();
  }

  get totalItens() {
    return this.carrinhoService.totalItens();
  }

  get precoTotal() {
    return this.carrinhoService.precoTotal();
  }

  fechar() {
    this.carrinhoService.fecharGaveta();
  }

  removerItem(id: number) {
    this.carrinhoService.removerDoCarrinho(id);
  }

  atualizarQuantidade(id: number, quantidade: number) {
    this.carrinhoService.atualizarQuantidade(id, quantidade);
  }

  finalizarCompra() {
    this.fechar();
    this.router.navigate(["/checkout"]);
  }
}
