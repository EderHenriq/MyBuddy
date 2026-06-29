import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Input, Output } from "@angular/core";

@Component({
  selector: "app-card-produto",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./card-produto.component.html",
  styleUrl: "./card-produto.component.scss",
})
export class CardProdutoComponent {
  @Input() urlImagem = "";
  @Input() titulo = "";
  @Input() preco!: number;
  @Input() precoAntigo?: number;
  @Input() nomeLoja!: string;
  @Input() badgeDesconto?: string;
  @Input() quantidade = 0;
  @Input() favorito = false;

  @Output() cliqueCard = new EventEmitter<void>();
  @Output() adicionarAoCarrinho = new EventEmitter<number>();
  @Output() cliqueFavorito = new EventEmitter<Event>();

  aoClicarAdicionar(event: Event) {
    event.stopPropagation();
    this.quantidade++;
    this.adicionarAoCarrinho.emit(this.quantidade);
  }

  aoDiminuir(event: Event) {
    event.stopPropagation();
    if (this.quantidade > 0) {
      this.quantidade--;
      this.adicionarAoCarrinho.emit(this.quantidade);
    }
  }

  aoClicarFavorito(event: Event) {
    event.stopPropagation();
    this.cliqueFavorito.emit(event);
  }
}
