import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-header-main',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './header-main.html',
  styleUrl: './header-main.scss',
})
export class HeaderMain {}
