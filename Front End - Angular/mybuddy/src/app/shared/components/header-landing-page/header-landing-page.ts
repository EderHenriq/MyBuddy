import {
  Component,
  ElementRef,
  QueryList,
  ViewChildren,
  AfterViewInit,
  HostListener,
  inject,
  PLATFORM_ID,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-header-landing-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header-landing-page.html',
  styleUrl: './header-landing-page.scss',
})
export class HeaderLandingPage implements AfterViewInit {
  private platform = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  @ViewChildren('navLink') navLinks!: QueryList<ElementRef>;

  pillStyle: { left: string; width: string } = { left: '0px', width: '0px' };
  pillVisible = false;
  activeLinkIndex = -1;
  hoveredIndex = -1;

  readonly links = [
    { fragment: 'adotar', label: 'Adote um Pet' },
    { fragment: 'servicos', label: 'Parceiros' },
    { fragment: 'blog', label: 'Blog' },
  ];

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }
    this.cdr.detectChanges();
  }

  @HostListener('window:resize')
  onResize(): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }
    this.updatePillPosition();
  }

  setActiveLink(index: number, event: HTMLElement): void {
    this.activeLinkIndex = index;
    this.movePill(event);
  }

  setHoverIndex(index: number, event: HTMLElement): void {
    this.hoveredIndex = index;
    this.movePill(event);
  }

  resetPill(): void {
    this.hoveredIndex = -1;
    if (this.activeLinkIndex !== -1) {
      const activeElement = this.navLinks.toArray()[this.activeLinkIndex];
      if (activeElement) {
        this.movePill(activeElement.nativeElement);
        return;
      }
    }
    this.pillVisible = false;
    this.cdr.detectChanges();
  }

  updatePillPosition(): void {
    const targetIndex = this.hoveredIndex !== -1 ? this.hoveredIndex : this.activeLinkIndex;
    if (targetIndex === -1) return;

    const activeElement = this.navLinks.toArray()[targetIndex];
    if (!activeElement) return;

    this.movePill(activeElement.nativeElement);
  }

  private movePill(element: HTMLElement): void {
    if (!isPlatformBrowser(this.platform)) return;
    const nav = element.closest('ul') as HTMLElement;
    if (!nav) return;

    const navRect = nav.getBoundingClientRect();
    const linkRect = element.getBoundingClientRect();

    this.pillStyle = {
      left: `${linkRect.left - navRect.left}px`,
      width: `${linkRect.width}px`,
    };
    this.pillVisible = true;
    this.cdr.detectChanges();
  }
}
