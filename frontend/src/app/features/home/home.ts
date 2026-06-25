import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { Footer } from '@shared/components/footer/footer';
import { HeaderMain } from '@shared/components/header-main/header-main';
import { HeroSectionComponent } from '@shared/components/hero-section/hero-section.component';

interface AcaoRapida {
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
const ACOES_RAPIDA: AcaoRapida[] = [
  { rotulo: 'Meus Pets', tab: 'pets', icone: 'pets' },
  { rotulo: 'Favoritos', tab: 'favoritos', icone: 'favorite' },
  { rotulo: 'Mensagens', tab: 'mensagens', icone: 'chat_bubble_outline' },
  { rotulo: 'Solicitações', tab: 'solicitacoes', icone: 'assignment' },
];

const CATEGORIAS: ItemCategoria[] = [
  { rotulo: 'Pets', rota: '/pets', urlImagem: '/assets/imagem/Cat-Dog.jpg' },
  { rotulo: 'Veterinários', rota: '/servicos', urlImagem: '/assets/imagem/Veterinario.jpg' },
  { rotulo: 'Eventos', rota: '/eventos', urlImagem: '/assets/imagem/Eventos.jpg' },
  { rotulo: 'Produtos', rota: '/produtoss', urlImagem: '/assets/imagem/Petshop.jpg' },
];

const LEMBRETES: ItemLembrete[] = [
  {
    urlIcone: '/assets/placeholders/vacina',
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
  imports: [RouterLink, HeaderMain, Footer, HeroSectionComponent],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  public authService = inject(AuthService);

}
