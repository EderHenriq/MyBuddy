import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-card-servico',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card-servico.component.html',
  styleUrl: './card-servico.component.scss'
})
export class CardServicoComponent {
  @Input() imageUrl: string = '';
  @Input() title: string = '';
  @Input() type: string = '';
  @Input() rating: number = 0;
  @Input() reviewsCount: number = 0;
  @Input() locationStr: string = '';
  @Input() distanceStr: string = '';
  @Input() openHoursStr: string = '';
  @Input() description: string = '';
  @Input() isFavorite: boolean = false;

  @Output() detailsClick = new EventEmitter<void>();
  @Output() favoriteClick = new EventEmitter<void>();
}
