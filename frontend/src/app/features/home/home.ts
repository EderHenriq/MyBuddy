import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { Footer } from '@shared/components/footer/footer';
import { HeaderMain } from '@shared/components/header-main/header-main';
import { HeroSectionComponent } from '@shared/components/hero-section/hero-section.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, HeaderMain, Footer, HeroSectionComponent],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  public authService = inject(AuthService);
}
