import { Pet } from '@core/models/pet.model';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgClass } from '@angular/common';
import { ModalComponent } from '../modal/modal.component';

@Component({
  selector: 'app-card-pet',
  standalone: true,
  imports: [NgClass, ModalComponent],
  templateUrl: './card-pet.html',
  styleUrl: './card-pet.scss',
})
export class CardPetComponent {
  @Input() pet?: Pet;

  @Input() ongMode = false;
  @Input() showFavoriteButton = false;
  @Input() showTopHeart = false;
  @Input() isFavorite = false;

  @Input() badgeText?: string;
  @Input() badgeType = 'default';

  @Output() editClick = new EventEmitter<Event>();
  @Output() deleteClick = new EventEmitter<Event>();
  @Output() favoriteClick = new EventEmitter<Event>();
  @Output() openModalRequest = new EventEmitter<void>();

  isModalVisible = false;

  get defaultDescription(): string {
    const nomePet = this.pet?.name ? `o ${this.pet.name}` : 'um pet';
    return `Olá! Eu sou ${nomePet} e estou ansioso para encontrar um novo lar.`;
  }

  openModal() {
    this.openModalRequest.emit();
    this.isModalVisible = true;
  }

  handleAdopt() {
    console.log(`Solicitação de adoção para: ${this.pet?.name}`);
    this.isModalVisible = false;
  }
}
