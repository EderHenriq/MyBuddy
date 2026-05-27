import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-error-page',
  standalone: true,
  imports: [],
  templateUrl: './error-page.html',
  styleUrls: ['./error-page.scss'],
})
export class ErrorPage {
  @Input() errorCode?: string;
  @Input() title = 'Ops! Algo deu errado.';
  @Input() description = 'Não conseguimos carregar esta página. Verifique sua conexão ou tente novamente mais tarde.';
  @Input() actionLabel = 'Voltar para o início';
  @Output() actionClick = new EventEmitter<void>();

  onActionClick() {
    this.actionClick.emit();
  }
}
