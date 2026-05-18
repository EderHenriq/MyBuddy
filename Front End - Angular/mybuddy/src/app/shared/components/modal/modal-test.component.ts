import { Component } from '@angular/core';
import { ModalComponent } from './modal.component';

@Component({
  selector: 'app-modal-test',
  standalone: true,
  imports: [ModalComponent],
  template: `
    <button (click)="visible = true">Abrir modal</button>

    <app-modal [(visible)]="visible" title="Teste de modal" (confirm)="onConfirm()" (cancel)="onCancel()">
      <p>Conteúdo do modal aqui!</p>
    </app-modal>
  `,
})
export class ModalTestComponent {
  visible = false;

  onConfirm() {
    console.log('confirmou');
    this.visible = false;
  }

  onCancel() {
    console.log('cancelou');
  }
}
