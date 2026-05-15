import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  imports: [CommonModule],
  templateUrl: './loading-spinner.html',
  styleUrl: './loading-spinner.scss',
})
export class LoadingSpinner {
  @Input() size: string = '40px';
  @Input() color: string = '#FF8B00';
  @Input() borderSize: string = '4px';

  get computedStyle() {
    return {
      width: this.size,
      height: this.size,
      borderWidth: this.borderSize,
      borderTopColor: this.color
    };
  }
}
