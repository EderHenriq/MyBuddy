import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

//Imports de Interface
import { Pet } from '@core/models/pet.model';

//Imports de Componentes
import { HeaderLandingPage } from '@shared/components/header-landing-page/header-landing-page';
import { CardCategoriaComponent } from '@shared/components/card-categoria/card-categoria.component';

interface Services {
  title: string;
  imageUrl: string;
}

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderLandingPage, CardCategoriaComponent],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage {
  services: Services[] = [
    {
      title: 'Veterinários',
      imageUrl: '/assets/landing-page/gato-veterinario.jpg',
    },
    {
      title: 'PetShops',
      imageUrl: '/assets/landing-page/pet-shop.jpg',
    },
    {
      title: 'Eventos',
      imageUrl: '/assets/landing-page/eventos.jpg',
    },
  ];

  logos = [
    { src: '/assets/logo/logo-parceiros/logo1.svg', alt: 'Logo Parceiro 1' },
    { src: '/assets/logo/logo-parceiros/logo2.svg', alt: 'Logo Parceiro 2' },
    { src: '/assets/logo/logo-parceiros/logo3.svg', alt: 'Logo Parceiro 3' },
    { src: '/assets/logo/logo-parceiros/logo4.svg', alt: 'Logo Parceiro 4' },
    { src: '/assets/logo/logo-parceiros/logo5.svg', alt: 'Logo Parceiro 5' },
  ];
}
