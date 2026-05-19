import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  imports: [CommonModule],
  templateUrl: './loading-spinner.html',
  styleUrl: './loading-spinner.scss',
})
export class LoadingSpinner {
  @Input() size = '40px';
  @Input() color = '#FF8B00';
  @Input() borderSize = '4px';

  get computedStyle() {
    return {
      width: this.size,
      height: this.size,
      borderWidth: this.borderSize,
      borderTopColor: this.color,
    };
  }
}
