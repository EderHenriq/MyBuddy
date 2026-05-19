import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [],
  templateUrl: './empty-state.html',
  styleUrls: ['./empty-state.scss'],
})
export class EmptyState {
  @Input() icon?: string; // Material icon name
  @Input() imageUrl?: string; // Image path (prioridade sobre ícone)
  @Input() title = 'Nenhum resultado encontrado';
  @Input() description?: string;
  @Input() actionLabel?: string;
  @Output() actionClick = new EventEmitter<void>();

  onActionClick() {
    this.actionClick.emit();
  }
}
