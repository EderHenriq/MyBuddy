import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-card-loja',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card-loja.component.html',
  styleUrl: './card-loja.component.scss',
})
export class CardLojaComponent {
  @Input() logoUrl = '';
  @Input() storeName = '';
  @Input() rating = 0;
  @Input() deliveryTime = '';
  @Input() deliveryFee?: number;

  @Output() storeClick = new EventEmitter<void>();
}
