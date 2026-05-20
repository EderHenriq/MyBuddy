import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderMain } from '@shared/components/header-main/header-main';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderMain, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
