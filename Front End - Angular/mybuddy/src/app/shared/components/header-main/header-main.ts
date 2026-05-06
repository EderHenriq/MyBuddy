import { Component, ElementRef, QueryList, ViewChildren, AfterViewInit, OnDestroy, HostListener, inject } from '@angular/core';
import { Router, NavigationEnd, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter, Subscription } from 'rxjs';

@Component({
  selector: 'app-header-main',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header-main.html',
  styleUrl: './header-main.scss',
})
export class HeaderMain implements AfterViewInit, OnDestroy {
  private router = inject(Router);
  @ViewChildren('navLink') navLinks!: QueryList<ElementRef>;

  pillStyle: { left: string; width: string } = { left: '0px', width: '0px' };
  pillVisible = false;

  private routerSubscription!: Subscription;

  ngAfterViewInit(): void {
    this.updatePillPosition();

    this.routerSubscription = this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(() => {
      setTimeout(() => this.updatePillPosition(), 0);
    });
  }

  ngOnDestroy(): void {
    this.routerSubscription.unsubscribe();
  }

  @HostListener('window:resize')
  onResize(): void {
    this.updatePillPosition();
  }

  updatePillPosition(): void {
    const activeElement = this.navLinks.find(link => link.nativeElement.classList.contains('active'));

    if (!activeElement) {
      this.pillVisible = false;
      return;
    }

    const nav = activeElement.nativeElement.closest('ul') as HTMLElement;
    const navRect = nav.getBoundingClientRect();
    const linkRect = activeElement.nativeElement.getBoundingClientRect();

    this.pillStyle = {
      left: `${linkRect.left - navRect.left}px`,
      width: `${linkRect.width}px`,
    };
    this.pillVisible = true;
  }

  onLinkClick(event: HTMLElement): void {
    const nav = event.closest('ul') as HTMLElement;
    const navRect = nav.getBoundingClientRect();
    const linkRect = event.getBoundingClientRect();

    this.pillStyle = {
      left: `${linkRect.left - navRect.left}px`,
      width: `${linkRect.width}px`,
    };
    this.pillVisible = true;
  }

  readonly links = [
    { path: '/home', label: 'Home' },
    { path: '/pets', label: 'Adotar' },
    { path: '/eventos', label: 'Eventos' },
    { path: '/produtos', label: 'Produtos' },
    { path: '/comunidade', label: 'Comunidade' },
  ];
}
