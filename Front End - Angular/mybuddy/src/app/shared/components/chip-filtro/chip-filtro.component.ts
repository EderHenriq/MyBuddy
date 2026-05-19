import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-chip-filtro',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chip-filtro.component.html',
  styleUrl: './chip-filtro.component.scss'
})
export class ChipFiltroComponent {
  @Input() label: string = '';
  @Input() iconClass: string = '';
  @Input() active: boolean = false;
  
  @Output() chipClick = new EventEmitter<void>();
}
