import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-page.html',
  styleUrls: ['./error-page.scss']
})
export class ErrorPage {
  @Input() errorCode?: string; // Ex: '404', '500'
  @Input() title: string = 'Ops! Algo deu errado.';
  @Input() description: string = 'Não conseguimos carregar esta página. Verifique sua conexão ou tente novamente mais tarde.';
  @Input() actionLabel: string = 'Voltar para o início';
  @Output() actionClick = new EventEmitter<void>();

  onActionClick() {
    this.actionClick.emit();
  }
}
