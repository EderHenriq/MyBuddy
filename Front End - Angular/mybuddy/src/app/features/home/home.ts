import { Component } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private http = inject(HttpClient);

  constructor() {
    console.log('HttpClient is available in Home component:', this.http);
  }
}
