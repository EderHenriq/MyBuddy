import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-card-categoria',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card-categoria.component.html',
  styleUrl: './card-categoria.component.scss'
})
export class CardCategoriaComponent {
  @Input() title: string = '';
  @Input() subtitle: string = '';
  @Input() imageUrl: string = '';
}
