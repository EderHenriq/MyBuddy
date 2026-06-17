import { Component, Input } from "@angular/core";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-card-avaliacao",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./card-avaliacao.component.html",
  styleUrl: "./card-avaliacao.component.scss",
})
export class CardAvaliacaoComponent {
  @Input() nomeCliente = "";
  @Input() avaliacao = 5;
  @Input() comentario = "";
  @Input() data = "";

  get estrelas(): boolean[] {
    const estrelasArray = [];
    for (let i = 1; i <= 5; i++) {
      estrelasArray.push(i <= this.avaliacao);
    }
    return estrelasArray;
  }

  get inicialNome(): string {
    return this.nomeCliente ? this.nomeCliente.charAt(0).toUpperCase() : "U";
  }
}
