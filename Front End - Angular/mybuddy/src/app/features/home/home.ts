import { Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.services';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private http = inject(HttpClient);
  public authService = inject(AuthService);

  logout() {
    this.authService.logout();
  }
}
