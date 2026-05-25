import { AfterViewInit, Component, inject, OnDestroy, OnInit, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule } from '@angular/router';

//Imports de Interface
import { Pet } from '@core/models/pet.model';

//Imports de Componentes
import { HeaderLandingPage } from '@shared/components/header-landing-page/header-landing-page';
import { CardCategoriaComponent } from '@shared/components/card-categoria/card-categoria.component';
import { CardPetComponent } from '@shared/components/card-pet/card-pet';
import { Footer } from '@shared/components/footer/footer';

interface Services {
  title: string;
  imageUrl: string;
}

interface FaqItem {
  question: string;
  answer: string;
  expanded: boolean;
}

const DEFAULT_LAT = -23.4273;
const DEFAULT_LNG = -51.9375;

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderLandingPage, CardCategoriaComponent, CardPetComponent, Footer],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage implements OnInit, OnDestroy, AfterViewInit {
  activePetIndex = 0;
  private autoplayInterval: ReturnType<typeof setInterval> | null = null;
  private readonly AUTOPLAY_DELAY = 3000;
  private platformId = inject(PLATFORM_ID);
  private map: unknown;

  ngOnInit(): void {
    this.startAutoplay();
  }

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.initMap();
    }
  }

  ngOnDestroy(): void {
    this.stopAutoplay();

    if (this.map && typeof (this.map as { remove: () => void }).remove === 'function') {
      (this.map as { remove: () => void }).remove();
    }
  }

  // Seção de Mapa
  private async initMap(lat = DEFAULT_LAT, lng = DEFAULT_LNG): Promise<void> {
    const L = await import('leaflet');

    this.map = L.map('leaflet-map', {
      center: [lat, lng],
      zoom: 14,
      zoomControl: false,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap',
    }).addTo(this.map as L.Map);

    this.loadMapByUserLocation();
  }

  private loadMapByUserLocation(): void {
    if (!navigator.geolocation) return;

    navigator.geolocation.getCurrentPosition(
      ({ coords }) => {
        if (this.map && typeof (this.map as { setView: (center: [number, number], zoom: number) => void }).setView === 'function') {
          (this.map as { setView: (center: [number, number], zoom: number) => void }).setView([coords.latitude, coords.longitude], 14);
        }
      },
      error => {
        console.warn('Geolocalização indisponível, mantendo padrão:', error.message);
      },
    );
  }

  //Animação do Carroussel
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

  //Seção de FAQ
  toggleFaq(index: number): void {
    if (index >= 0 && index < this.faqs.length) {
      this.faqs[index].expanded = !this.faqs[index].expanded;
    }
  }

  //Dados Estáticos
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

  faqs: FaqItem[] = [
    {
      question: 'Quais são os requisitos para adotar um pet?',
      answer:
        'É necessário ser maior de 18 anos, apresentar documento de identidade e comprovante de residência. Alguns animais podem ter requisitos específicos definidos pela ONG responsável.',
      expanded: false,
    },
    {
      question: 'O processo de adoção tem algum custo?',
      answer:
        'A adoção em si é gratuita. Em alguns casos, a ONG pode solicitar uma contribuição simbólica para cobrir custos de vacinação, castração ou microchipagem do animal.',
      expanded: false,
    },
    {
      question: 'Posso adotar mesmo morando em apartamento?',
      answer:
        'Sim! Muitos pets se adaptam muito bem a apartamentos. O mais importante é garantir espaço adequado, enriquecimento ambiental e tempo de qualidade com o animal.',
      expanded: false,
    },
    {
      question: 'Como funciona o período de adaptação?',
      answer:
        'Recomendamos um período de adaptação de 7 a 30 dias. Durante esse tempo, o animal conhece o novo lar e a família. É normal que ele fique tímido ou agitado no início.',
      expanded: false,
    },
    {
      question: 'Existe acompanhamento após a adoção?',
      answer:
        'Sim. As ONGs parceiras do MyBuddy realizam acompanhamento pós-adoção por meio de contato periódico para garantir o bem-estar do animal e apoiar o novo tutor.',
      expanded: false,
    },
  ];
}
