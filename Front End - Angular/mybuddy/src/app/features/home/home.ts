import { Component } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import  Keycloak from 'keycloak-js';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private http = inject(HttpClient);
  private keycloak = inject(Keycloak);

  logout(){
    this.keycloak.logout({
      redirectUri: 'http://localhost:4200'
    });
  }
}
