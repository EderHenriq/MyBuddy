import { Component } from '@angular/core';
import { HeaderMain } from '@shared/components/header-main/header-main';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderMain],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
