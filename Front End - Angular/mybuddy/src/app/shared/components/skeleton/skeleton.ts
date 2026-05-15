import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-skeleton',
  imports: [CommonModule],
  templateUrl: './skeleton.html',
  styleUrl: './skeleton.scss',
})
export class Skeleton {
  @Input() width: string = '100%';
  @Input() height: string = '20px';
  @Input() shape: 'rectangle' | 'circle' = 'rectangle';
  @Input() borderRadius: string = '8px';

  get computedStyle() {
    return {
      width: this.width,
      height: this.height,
      borderRadius: this.shape === 'circle' ? '50%' : this.borderRadius
    };
  }
}
