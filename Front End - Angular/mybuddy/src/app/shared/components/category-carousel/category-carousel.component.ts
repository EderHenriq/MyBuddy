import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

export interface CategoriaVisual {
  id: number;
  name: string;
  imageUrl: string;
}

@Component({
  selector: 'app-category-carousel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './category-carousel.component.html',
  styleUrl: './category-carousel.component.scss',
})
export class CategoryCarouselComponent {
  @Input() categories: CategoriaVisual[] = [];
  @Output() categoryClick = new EventEmitter<CategoriaVisual>();
}
