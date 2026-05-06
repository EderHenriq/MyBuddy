import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-header-main',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './header-main.html',
  styleUrl: './header-main.scss',
})
export class HeaderMain {}
