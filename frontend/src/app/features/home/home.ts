import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { HeaderMain } from '@shared/components/header-main/header-main';
import { HeroSectionComponent } from '@shared/components/hero-section/hero-section.component';
import { Footer } from '@shared/components/footer/footer';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, HeaderMain, Footer],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {}
