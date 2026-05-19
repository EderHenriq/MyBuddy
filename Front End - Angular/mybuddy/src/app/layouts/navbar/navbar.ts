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
    { label: 'Home', link: '/home' },
    { label: 'Adotar', link: '/pets' },
    { label: 'Marketplace', link: '/marketplace' },
    { label: 'Doações', link: '/doacoes' },
  ];
}
