import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-banner-lembrete',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './banner-lembrete.component.html',
  styleUrl: './banner-lembrete.component.scss',
})
export class BannerLembreteComponent {
  @Input() title = '';
  @Input() subtitle = '';
  @Input() iconImage = '';
  @Input() buttonText = 'Agendar';

  @Output() actionClick = new EventEmitter<void>();
}
