import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Input, Output } from "@angular/core";

@Component({
  selector: "app-card-loja",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./card-loja.component.html",
  styleUrl: "./card-loja.component.scss",
})
export class CardLojaComponent {
  @Input() urlLogo = "";
  @Input() nomeLoja = "";
  @Input() avaliacao = 0;
  @Input() tempoEntrega = "";
  @Input() taxaEntrega?: number;

  @Output() cliqueLoja = new EventEmitter<void>();
}
