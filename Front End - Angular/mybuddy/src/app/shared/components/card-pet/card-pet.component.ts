import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-card-pet',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card-pet.component.html',
  styleUrl: './card-pet.component.scss'
})
export class CardPetComponent {
  @Input() imageUrl: string = '';
  @Input() name: string = '';
  @Input() age: string = '';
  @Input() breed: string = '';
  @Input() sex: string = '';
  @Input() vaccinated: string = 'Sim';
  
  @Output() infoClick = new EventEmitter<void>();
  @Output() favoriteClick = new EventEmitter<void>();
}
