import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalComponent } from '../modal/modal.component';

@Component({
  selector: 'app-card-pet',
  standalone: true,
  imports: [CommonModule, ModalComponent],
  templateUrl: './card-pet.component.html',
  styleUrl: './card-pet.component.scss',
})
export class CardPetComponent {
  @Input() urlImagem = '';
  @Input() nome = '';
  @Input() idade = 0;
  @Input() raca = '';
  @Input() sexo = '';
  @Input() vacinado = false;
  @Input() castrado = false;
  @Input() porte = '';
  @Input() cor = '';
  @Input() pelagem = '';
  @Input() descricao =
    'Olá! Eu sou muito dócil, brincalhão e estou ansioso para encontrar uma família que me dê muito amor e carinho. Já estou pronto para ser seu novo melhor amigo!';
  @Input() textoBadge = '';
  @Input() tipoBadge: 'adoption' | 'adopted' | '' = '';
  @Input() favorito = false;
  @Input() mostrarCoracaoTop = false;
  @Input() modoOng = false;

  @Output() cliqueInfo = new EventEmitter<void>();
  @Output() cliqueFavorito = new EventEmitter<void>();
  @Output() cliqueAdotar = new EventEmitter<void>();
  @Output() cliqueEditar = new EventEmitter<void>();
  @Output() cliqueDeletar = new EventEmitter<void>();

  modalVisivel = false;

  abrirModal(): void {
    this.modalVisivel = true;
    this.cliqueInfo.emit();
  }

  processarAdocao(): void {
    this.modalVisivel = false;
    this.cliqueAdotar.emit();
  }

  async compartilharPet() {
    const dadosCompartilhamento = {
      title: `Adote o ${this.nome} - MyBuddy`,
      text: `Conheça o ${this.nome}! Tem ${this.idade} anos e é da raça ${this.raca}.`,
      url: window.location.href,
    };

    if (navigator.share) {
      try {
        await navigator.share(dadosCompartilhamento);
      } catch (err) {
        console.error('Erro ao compartilhar:', err);
      }
    } else {
      navigator.clipboard.writeText(window.location.href);
      alert('Link copiado para a área de transferência!');
    }
  }
}
