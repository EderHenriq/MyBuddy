import { Directive, EventEmitter, HostListener, Input, Output } from '@angular/core';

@Directive({
  selector: '[appInfiniteScroll]',
  standalone: true,
})
export class InfiniteScrollDirective {
  @Input() scrollThreshold = 150; 
  @Output() scrolled = new EventEmitter<void>();

  @HostListener('window:scroll')
  onScroll() {
    const windowHeight = 'innerHeight' in window ? window.innerHeight : document.documentElement.offsetHeight;
    const body = document.body;
    const html = document.documentElement;

    const docHeight = Math.max(body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight);

    const windowBottom = windowHeight + window.pageYOffset;

    if (windowBottom >= docHeight - this.scrollThreshold) {
      this.scrolled.emit();
    }
  }
}
