import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  navItems = [
    { label: 'Home', link: '/', active: true },
    { label: 'Adotar', link: '/adotar', active: false },
    { label: 'Eventos', link: '/eventos', active: false },
    { label: 'Produtos', link: '/produtos', active: false },
    { label: 'Comunidade', link: '/comunidade', active: false },
  ];

  setActive(item: any) {
    this.navItems.forEach(i => i.active = false);
    item.active = true;
  }
}
