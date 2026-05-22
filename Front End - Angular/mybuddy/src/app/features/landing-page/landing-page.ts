import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderLandingPage } from '@shared/components/header-landing-page/header-landing-page';
import { CardCategoriaComponent } from '@shared/components/card-categoria/card-categoria.component';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { Footer } from '@shared/components/footer/footer';

interface Pet {
  id: number;
  nome: string;
  idade: number;
  raca: string;
  sexo: string;
  vacinado: boolean;
  castrado: boolean;
  porte: string;
  cor: string;
  pelagem: string;
  urlImagem: string;
}

interface Service {
  title: string;
  imageUrl: string;
}

interface FaqItem {
  question: string;
  answer: string;
  expanded: boolean;
}

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderLandingPage, CardCategoriaComponent, CardPetComponent, Footer],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.scss',
})
export class LandingPage {
  activePetIndex = 1; 

  services: Service[] = [
    {
      title: 'Veterinários',
      imageUrl: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=600',
    },
    {
      title: 'Eventos',
      imageUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=600',
    },
    {
      title: 'Petshops',
      imageUrl: 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=600',
    },
  ];

  pets: Pet[] = [
    {
      id: 1,
      nome: 'Nevasca',
      idade: 3,
      raca: 'Persa',
      sexo: 'Fêmea',
      vacinado: true,
      castrado: true,
      porte: 'Pequeno',
      cor: 'Branco',
      pelagem: 'Longa',
      urlImagem: 'https://images.unsplash.com/photo-1618826411640-d6df44dd3f7a?auto=format&fit=crop&q=80&w=600',
    },
    {
      id: 2,
      nome: 'Paçoca',
      idade: 5,
      raca: 'Vira-lata',
      sexo: 'Macho',
      vacinado: true,
      castrado: true,
      porte: 'Médio',
      cor: 'Caramelo',
      pelagem: 'Curta',
      urlImagem: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&q=80&w=600',
    },
    {
      id: 3,
      nome: 'Thor',
      idade: 4,
      raca: 'Border Collie',
      sexo: 'Macho',
      vacinado: true,
      castrado: false,
      porte: 'Médio',
      cor: 'Preto e Branco',
      pelagem: 'Longa',
      urlImagem: 'https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&q=80&w=600',
    },
  ];

  faqs: FaqItem[] = [
    {
      question: 'Como funciona o processo de adoção?',
      answer: `Para adotar seu Buddy, basta seguir três passos simples:
1. Escolha o pet que mais combina com você em nossa lista.
2. Preencha o formulário de adoção com seus dados e informações sobre seu ambiente.
3. Aguarde o contato das nossas ONGs parceiras para uma entrevista rápida e a confirmação da adoção.
Todo o processo é gratuito e feito para garantir que você e o pet tenham o melhor match possível.`,
      expanded: true, 
    },
    {
      question: 'O MyBuddy é pago?',
      answer:
        'Não! O MyBuddy é uma plataforma 100% gratuita para adotantes e ONGs parceiras. Nosso objetivo é facilitar a adoção e o cuidado animal.',
      expanded: false,
    },
    {
      question: 'O pet vem com vacinas atualizadas?',
      answer:
        'Sim, a maioria das ONGs parceiras entrega o pet com a vacinação básica em dia e castrado. Essa informação é sempre detalhada no perfil de cada pet.',
      expanded: false,
    },
    {
      question: 'Posso devolver o pet se não me adaptar?',
      answer:
        'Caso haja problemas de adaptação, a ONG parceira prestará todo o suporte necessário. A devolução responsável pode ser alinhada diretamente com a instituição.',
      expanded: false,
    },
    {
      question: 'Vocês fazem visitas ao meu endereço?',
      answer: 'Algumas ONGs solicitam uma visita prévia ou envio de fotos/vídeos do ambiente para garantir a segurança e o bem-estar do pet adotado.',
      expanded: false,
    },
  ];

  setCarouselPet(index: number): void {
    if (index >= 0 && index < this.pets.length) {
      this.activePetIndex = index;
    }
  }

  toggleFaq(index: number): void {
    this.faqs[index].expanded = !this.faqs[index].expanded;
  }
}
