import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-card-evento',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card-evento.component.html',
  styleUrl: './card-evento.component.scss',
})
export class CardEventoComponent {
  @Input() imageUrl = '';
  @Input() badgeText = '';
  @Input() title = '';
  @Input() dateStr = '';
  @Input() timeStr = '';
  @Input() locationStr = '';
  @Input() organizerStr = '';
  @Input() description = '';
  @Input() isFavorite = false;

  @Output() detailsClick = new EventEmitter<void>();
  @Output() favoriteClick = new EventEmitter<void>();
}
