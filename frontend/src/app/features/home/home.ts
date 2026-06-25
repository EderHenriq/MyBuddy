import { isPlatformBrowser } from '@angular/common';
import { AfterViewInit, Component, inject, OnDestroy, PLATFORM_ID } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { PetService } from '@core/services/pet.service';
import { Pet } from '@core/models/pet.model';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { Footer } from '@shared/components/footer/footer';
import { HeaderMain } from '@shared/components/header-main/header-main';
import { HeroSectionComponent } from '@shared/components/hero-section/hero-section.component';
import { map } from 'rxjs';

interface AcaoRapidas {
  rotulo: string;
  tab: string;
  icone: string;
}

interface ItemCategoria {
  rotulo: string;
  urlImagem: string;
  rota: string;
}

interface ItemLembrete {
  urlIcone: string;
  titulo: string;
  texto: string;
}

interface ItemEvento {
  dia: string;
  mes: string;
  titulo: string;
  local: string;
}

interface ItemVeterinario {
  nome: string;
  distancia: string;
  status: string;
  avaliacao: string;
  urlAvatar: string;
}

interface ItemProduto {
  rotulo: string;
  urlImagem: string;
}

//Dados estáticos da UI
const ACOES_RAPIDA: AcaoRapidas[] = [
  { rotulo: 'Meus Pets', tab: 'pets', icone: 'pets' },
  { rotulo: 'Favoritos', tab: 'favoritos', icone: 'favorite' },
  { rotulo: 'Mensagens', tab: 'mensagens', icone: 'chat_bubble_outline' },
  { rotulo: 'Solicitações', tab: 'solicitacoes', icone: 'assignment' },
];

const CATEGORIAS: ItemCategoria[] = [
  { rotulo: 'Pets', rota: '/pets', urlImagem: '/assets/imagem/Cat-Dog.jpg' },
  { rotulo: 'Veterinários', rota: '/servicos', urlImagem: '/assets/imagem/Veterinario.jpg' },
  { rotulo: 'Eventos', rota: '/eventos', urlImagem: '/assets/imagem/Eventos.jpg' },
  { rotulo: 'Produtos', rota: '/produtos', urlImagem: '/assets/imagem/Petshop.jpg' },
];

const LEMBRETES: ItemLembrete[] = [
  {
    urlIcone: '/assets/placeholders/vacina.png',
    titulo: 'Lembrete: Vacina do seu Buddy',
    texto: 'A vacina antirrábica do Zeus vence em 15 dias. Agende já',
  },
  {
    urlIcone: '/assets/placeholders/cirurgia.jpg',
    titulo: 'Lembrete: Cirurgia de Castração',
    texto: 'A agenda de cirurgia de castração está disponível.',
  },
];

const EVENTOS: ItemEvento[] = [
  { dia: '26', mes: 'JUL', titulo: 'Feira de Adoção - Parque do Ingá', local: 'Maringá, PR · 9h às 16h' },
  { dia: '02', mes: 'AGO', titulo: 'Campanha de Vacinação Gratuita', local: 'Clínica VetAmigos, Maringá · 9h às 16h' },
  { dia: '15', mes: 'AGO', titulo: 'Passeio Pet Friendly', local: 'Euro Garden, Maringá · 9h às 16h' },
  { dia: '20', mes: 'AGO', titulo: 'Campanha de Castração Gratuita', local: 'Clínica VetAmigos, Maringá · 9h às 16h' },
];

const VETERINARIOS: ItemVeterinario[] = [
  {
    nome: 'Dr. Carlos Melo',
    distancia: '1,2km',
    status: 'Aberto agora',
    avaliacao: '4.9',
    urlAvatar: '/assets/placeholders/profiles/Jorge.png',
  },
  {
    nome: 'Dra. Paula Schneider',
    distancia: '3,2km',
    status: 'Abre às 09h',
    avaliacao: '4.1',
    urlAvatar: '/assets/placeholders/profiles/Josie.jpg',
  },
  {
    nome: 'Dr. Heitor Krisraff',
    distancia: '500m',
    status: 'Aberto 24h',
    avaliacao: '4.7',
    urlAvatar: '/assets/placeholders/profiles/Jorge.png',
  },
  {
    nome: 'Dra. Camila Rhett',
    distancia: '5km',
    status: 'Fechado',
    avaliacao: '4.3',
    urlAvatar: '/assets/placeholders/profiles/Josie.jpg',
  },
];

const PRODUTOS: ItemProduto[] = [
  { rotulo: 'Acessórios', urlImagem: '/assets/placeholders/marketplace/Acessorios.jpg' },
  { rotulo: 'Alimentação', urlImagem: '/assets/placeholders/marketplace/Alimentacao.jpg' },
  { rotulo: 'Mochilas', urlImagem: '/assets/placeholders/marketplace/Mochilas.jpg' },
  { rotulo: 'Roupas', urlImagem: '/assets/placeholders/marketplace/Roupas.jpg' },
];

const DEFAULT_LAT = -23.4273;
const DEFAULT_LNG = -51.9375;

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, HeaderMain, Footer, HeroSectionComponent, CardPetComponent],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements AfterViewInit, OnDestroy {
  readonly authService = inject(AuthService);
  private readonly petService = inject(PetService);
  private readonly platformId = inject(PLATFORM_ID);

  readonly acoesRapidas = ACOES_RAPIDA;
  readonly categorias = CATEGORIAS;
  readonly lembretes = LEMBRETES;
  readonly eventos = EVENTOS;
  readonly veterinarios = VETERINARIOS;
  readonly produtos = PRODUTOS;

  private map: L.Map | null = null;

  private readonly petsResponse = toSignal(this.petService.buscarRecentes().pipe(map((res: { content: Pet[] }) => res.content.slice(0, 3))), {
    initialValue: null,
  });

  readonly pets = () => this.petsResponse() ?? [];
  readonly carregandoPets = () => this.petsResponse() === null;

  async ngAfterViewInit(): Promise<void> {
    if(!isPlatformBrowser(this.platformId)) return;
    await this.initMap();
  }

  ngOnDestroy(): void {
    this.map?.remove();
    this.map = null;
  }

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
    }).addTo(this.map);

    this.loadMapByUserLocation();
  }

  private loadMapByUserLocation(): void {
    if(!navigator.geolocation) return;

    navigator.geolocation.getCurrentPosition(
      ({coords}) => {
        this.map?.setView([coords.latitude, coords.longitude], 14);
      }, 
      error => {
        console.warn('Geolocalização indisponível, mantendo padrão de Maringá: ', error.message);
      }
    )
  }
}
