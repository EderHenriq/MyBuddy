import { Component, inject, OnDestroy, OnInit, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule } from '@angular/router';

//Imports de Interface
import { Pet } from '@core/models/pet.model';

//Imports de Componentes
import { HeaderLandingPage } from '@shared/components/header-landing-page/header-landing-page';
import { CardCategoriaComponent } from '@shared/components/card-categoria/card-categoria.component';
import { CardPetComponent } from '@shared/components/card-pet/card-pet';

interface Services {
  title: string;
  imageUrl: string;
}

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderLandingPage, CardCategoriaComponent, CardPetComponent],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage implements OnInit, OnDestroy {
  activePetIndex = 0;
  private autoplayInterval: ReturnType<typeof setInterval> | null = null;
  private readonly AUTOPLAY_DELAY = 3000;
  private platformId = inject(PLATFORM_ID);

  ngOnInit(): void {
    this.startAutoplay();
  }

  ngOnDestroy(): void {
    this.stopAutoplay();
  }

  getSlidePosition(idx: number): number {
    const total = this.pets.length;
    let diff = idx - this.activePetIndex;

    if (diff > Math.floor(total / 2)) diff -= total;
    if (diff < -Math.floor(total / 2)) diff += total;

    return diff;
  }

  getSlideClass(idx: number): Record<string, boolean> {
    const pos = this.getSlidePosition(idx);
    return {
      activeSlide: pos === 0,
      leftSlide: pos === -1,
      rightSlide: pos === 1,
    };
  }

  startAutoplay(): void {
    if (!isPlatformBrowser(this.platformId)) return;

    this.stopAutoplay();
    this.autoplayInterval = setInterval(() => {
      this.activePetIndex = (this.activePetIndex + 1) % this.pets.length;
    }, this.AUTOPLAY_DELAY);
  }

  stopAutoplay(): void {
    if (this.autoplayInterval) {
      clearInterval(this.autoplayInterval);
      this.autoplayInterval = null;
    }
  }

  setCarouselPet(index: number): void {
    this.activePetIndex = index;
    this.startAutoplay();
  }

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

  pets: Pet[] = [
    {
      id: '1',
      ownerId: '1',
      name: 'Nevasca',
      age: 3,
      species: 'Gato',
      breed: 'Vira-lata',
      gender: 'Fêmea',
      isVaccinated: true,
      imageUrl: '/assets/placeholders/pets/Nevasca.jpg',
    },
    {
      id: '2',
      ownerId: '2',
      name: 'Paçoca',
      age: 5,
      species: 'Cachorro',
      breed: 'Vira-lata',
      gender: 'Macho',
      isVaccinated: true,
      imageUrl: '/assets/placeholders/pets/Paçoca.jpg',
    },
    {
      id: '3',
      ownerId: '3',
      name: 'Armindo',
      age: 1,
      species: 'Coelho',
      breed: 'Rex',
      gender: 'Macho',
      isVaccinated: true,
      imageUrl: '/assets/placeholders/pets/Armindo.png',
    },
  ];
}
