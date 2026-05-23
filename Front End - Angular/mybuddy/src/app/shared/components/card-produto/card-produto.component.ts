import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-card-produto',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card-produto.component.html',
  styleUrl: './card-produto.component.scss',
})
export class CardProdutoComponent {
  @Input() imageUrl = '';
  @Input() title = '';
  @Input() price!: number;
  @Input() oldPrice?: number;
  @Input() storeName!: string;
  @Input() discountBadge?: string;
  @Input() quantity = 0;
  @Input() isFavorite = false;

  @Output() cardClick = new EventEmitter<void>();
  @Output() addToCart = new EventEmitter<number>();
  @Output() favoriteClick = new EventEmitter<Event>();

  onAddClick(event: Event) {
    event.stopPropagation();
    this.quantity++;
    this.addToCart.emit(this.quantity);
  }

  onDecrease(event: Event) {
    event.stopPropagation();
    if (this.quantity > 0) {
      this.quantity--;
      this.addToCart.emit(this.quantity);
    }
  }

  onFavoriteClick(event: Event) {
    event.stopPropagation();
    this.favoriteClick.emit(event);
  }
}
