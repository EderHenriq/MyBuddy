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
  @Input() imageUrl = '';
  @Input() name = '';
  @Input() age = '';
  @Input() breed = '';
  @Input() sex = '';
  @Input() vaccinated = 'Sim';
  @Input() description =
    'Olá! Eu sou muito dócil, brincalhão e estou ansioso para encontrar uma família que me dê muito amor e carinho. Já estou pronto para ser seu novo melhor amigo!';
  @Input() badgeText = '';
  @Input() badgeType: 'adoption' | 'adopted' | '' = '';
  @Input() isFavorite = false;
  @Input() showTopHeart = false;
  @Input() ongMode = false;

  @Output() infoClick = new EventEmitter<void>();
  @Output() favoriteClick = new EventEmitter<void>();
  @Output() adoptClick = new EventEmitter<void>();
  @Output() editClick = new EventEmitter<void>();
  @Output() deleteClick = new EventEmitter<void>();

  isModalVisible = false;

  openModal(): void {
    this.isModalVisible = true;
    this.infoClick.emit();
  }

  handleAdopt(): void {
    this.isModalVisible = false;
    this.adoptClick.emit();
  }
}
