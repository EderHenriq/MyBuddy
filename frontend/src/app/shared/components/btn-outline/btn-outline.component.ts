import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-btn-outline',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button
      [type]="type"
      class="myb-btn"
      [class.myb-btn--outline]="variant === 'outline'"
      [class.myb-btn--solid]="variant === 'solid'"
      [class.myb-btn--ghost]="variant === 'ghost'"
      [class.myb-btn--sm]="size === 'sm'"
      [class.myb-btn--md]="size === 'md'"
      [class.myb-btn--lg]="size === 'lg'"
      [disabled]="disabled"
      (click)="clicked.emit($event)"
    >
      @if (icon) {
        <span class="material-icons btn-icon">{{ icon }}</span>
      }
      <ng-content></ng-content>
    </button>
  `,
  styleUrl: './btn-outline.component.scss'
})
export class BtnOutlineComponent {
  @Input() variant: 'outline' | 'solid' | 'ghost' = 'outline';
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() icon?: string;
  @Input() type: 'button' | 'submit' = 'button';
  @Input() disabled = false;
  @Output() clicked = new EventEmitter<Event>();
}
