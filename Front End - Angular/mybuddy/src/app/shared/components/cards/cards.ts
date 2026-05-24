import { Pet } from '../../../core/models/pet.model';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule, NgClass } from '../../../../../node_modules/@angular/common/types/_common_module-chunk';

@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [NgClass, CommonModule],
  templateUrl: './cards.html',
  styleUrl: './cards.scss',
})
export class Cards {
  @Input() pet?: Pet;

  @Input() ongMode: boolean;
  @Input() showFavoriteButton: boolean;
  @Input() showTopHeart: boolean;
  @Input() isFavorite: boolean;

  @Input() badgeText?: string;
  @Input() badgeType: string;

  @Output() editClick = new EventEmitter<Event>();
  @Output() deleteClick = new EventEmitter<Event>();
  @Output() favoriteClick = new EventEmitter<Event>();

  isModalVisible: boolean;

  openModal() {
    this.isModalVisible = true;
  }

  handleAdopt() {
    console.log('Solicitação de adoção para: ${this.pet?.name}');
    this.isModalVisible = false;
  }
}
