import { Component, OnInit, inject, signal } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { Footer } from '@shared/components/footer/footer';
import { PetService } from '@core/services/pet.service';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { HeroSectionComponent } from '@shared/components/hero-section/hero-section.component';

interface CartaoCategoria {
  rotulo: string;
  urlImagem: string;
  rota: string;
}

interface Lembrete {
  urlIcone: string;
  titulo: string;
  texto: string;
}

interface CartaoPet {
  nome: string;
  idade: number;
  raca: string;
  sexo: string;
  vacinado: boolean;
  castrado: boolean;
  porte: string;
  cor: string;
  pelagem: string;
  fotosUrls?: string[];
}

interface CartaoEvento {
  dia: string;
  mes: string;
  titulo: string;
  local: string;
}

interface CartaoVeterinario {
  nome: string;
  distancia: string;
  status: string;
  avaliacao: string;
  urlAvatar: string;
}

interface CategoriaProduto {
  rotulo: string;
  urlImagem: string;
}

@Component({
  selector: 'app-home',
  imports: [RouterLink, Footer, CardPetComponent, HeroSectionComponent],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  public authService = inject(AuthService);
  private sanitizer = inject(DomSanitizer);
  private petService = inject(PetService);

  readonly carregandoPets = signal<boolean>(true);
  readonly pets = signal<CartaoPet[]>([]);

  readonly urlMapa: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
    'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d14629.742352528775!2d-46.666666!3d-23.555555!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMjPCsDMzJzIwLjAiUyA0NsKwMzknNTkuOSJX!5e0!3m2!1spt-BR!2sbr!4v1700000000000!5m2!1spt-BR!2sbr',
  );

  readonly categorias: CartaoCategoria[] = [
    {
      rotulo: 'Pets',
      urlImagem: 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&q=80&w=600',
      rota: '/pets',
    },
    {
      rotulo: 'Veterinários',
      urlImagem: 'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=600',
      rota: '/servicos',
    },
    {
      rotulo: 'Eventos',
      urlImagem: 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=600',
      rota: '/eventos',
    },
    {
      rotulo: 'Produtos',
      urlImagem: 'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=600',
      rota: '/produtos',
    },
  ];

  readonly lembretes: Lembrete[] = [
    {
      urlIcone: 'https://cdn-icons-png.flaticon.com/512/4151/4151022.png',
      titulo: 'Lembrete: Vacina do seu Buddy',
      texto: 'A vacina antirrábica do Zeus vence em 15 dias. Agende já!',
    },
    {
      urlIcone: 'https://cdn-icons-png.flaticon.com/512/3209/3209072.png',
      titulo: 'Lembrete: Cirurgia de Castração',
      texto: 'A agenda de cirurgia de castração está disponível.',
    },
  ];

  ngOnInit(): void {
    this.petService.buscarRecentes().subscribe({
      next: (dados: any) => {
        const todosPets = dados.content || [];
        this.pets.set(todosPets.slice(0, 3));
        this.carregandoPets.set(false);
      },
      error: erro => {
        console.error('Erro ao buscar pets recentes', erro);
        this.carregandoPets.set(false);
      },
    });
  }

  readonly eventos: CartaoEvento[] = [
    { dia: '26', mes: 'ABR', titulo: 'Feira de Adoção - Parque do Ingá', local: 'Maringá, Pr - 9h às 16h' },
    { dia: '02', mes: 'MAI', titulo: 'Campanha de Vacinação Gratuita', local: 'Clínica VetAmigos, Maringá - 9h às 16h' },
    { dia: '15', mes: 'MAI', titulo: 'Passeio Pet Friendly', local: 'Euro Garden, Maringá - 9h às 16h' },
    { dia: '20', mes: 'MAI', titulo: 'Campanha de Castração Gratuita', local: 'Clínica VetAmigos, Maringá - 9h às 16h' },
  ];

  readonly veterinarios: CartaoVeterinario[] = [
    {
      nome: 'Dr. Carlos Melo',
      distancia: '1,2km',
      status: 'Aberto agora',
      avaliacao: '4.9',
      urlAvatar: 'https://cdn-icons-png.flaticon.com/512/387/387561.png',
    },
    {
      nome: 'Dra. Paula Schneider',
      distancia: '3,2km',
      status: 'Abre as 09hrs',
      avaliacao: '4.1',
      urlAvatar: 'https://cdn-icons-png.flaticon.com/512/387/387569.png',
    },
    {
      nome: 'Dr. Heitor Krisraff',
      distancia: '500m',
      status: 'Aberto 24h',
      avaliacao: '4.7',
      urlAvatar: 'https://cdn-icons-png.flaticon.com/512/387/387561.png',
    },
    {
      nome: 'Dra. Camila Rhett',
      distancia: '5km',
      status: 'Fechado',
      avaliacao: '4.3',
      urlAvatar: 'https://cdn-icons-png.flaticon.com/512/387/387569.png',
    },
  ];

  readonly produtos: CategoriaProduto[] = [
    {
      rotulo: 'Acessorios',
      urlImagem: 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=600',
    },
    {
      rotulo: 'Alimentação',
      urlImagem: 'https://images.unsplash.com/photo-1559715541-5daf8a0296d0?auto=format&fit=crop&q=80&w=600',
    },
    {
      rotulo: 'Mochilas',
      urlImagem: 'https://images.unsplash.com/photo-1546975490-a79abdd54533?auto=format&fit=crop&q=80&w=600',
    },
    {
      rotulo: 'Roupas',
      urlImagem: 'https://images.unsplash.com/photo-1588943211346-0908a1fb0b01?auto=format&fit=crop&q=80&w=600',
    },
  ];
}
