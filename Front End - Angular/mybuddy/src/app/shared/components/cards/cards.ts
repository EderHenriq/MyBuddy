import { Pet } from '../../../core/models/pet.model';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgClass } from '@angular/common';
import { ModalComponent } from '../modal/modal.component';

@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [NgClass, ModalComponent],
  templateUrl: './cards.html',
  styleUrl: './cards.scss',
})
export class Cards {
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

  isModalVisible = false;

  defaultDescription = 'Olá! Eu sou pet?.name e estou ansioso para encontrar um novo lar.';

  openModal() {
    this.isModalVisible = true;
  }

  handleAdopt() {
    console.log(`Solicitação de adoção para: ${this.pet?.name}`);
    this.isModalVisible = false;
  }
}
