import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  isAdminRoute = false;
  isLandingPageRoute = false;

  constructor(private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        const url = event.urlAfterRedirects;
        this.isAdminRoute = url.startsWith('/admin') || url.startsWith('/ong-panel') || url.startsWith('/petshop-panel');
        this.isLandingPageRoute = url === '/' || url === '' || url.split('?')[0] === '/';
      }
    });
  }
}
