import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-modal',
  standalone: true,
  host: { ngSkipHydration: 'true' },
  imports: [CommonModule, DialogModule, ButtonModule],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.scss',
})
export class ModalComponent {
  @Input() visible = false;
  @Input() title = '';
  @Input() width = '450px';
  @Input() showFooter = true;
  @Input() confirmLabel = 'Confirmar';
  @Input() cancelLabel = 'Cancelar';
  @Input() confirmSeverity: 'primary' | 'secondary' | 'danger' = 'primary';
  @Input() loading = false;

  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  onHide(): void {
    this.visible = false;
    this.visibleChange.emit(false);
    this.cancel.emit();
  }

  onConfirm(): void {
    this.confirm.emit();
  }

  onCancel(): void {
    this.visible = false;
    this.visibleChange.emit(false);
    this.cancel.emit();
  }
}
