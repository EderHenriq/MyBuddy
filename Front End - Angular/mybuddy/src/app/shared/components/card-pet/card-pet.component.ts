import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-card-pet',
  standalone: true,
  imports: [CommonModule],
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

  @Output() infoClick = new EventEmitter<void>();
  @Output() favoriteClick = new EventEmitter<void>();
}
