import { Component, inject } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

interface CategoryCard {
  label: string;
  imageUrl: string;
}

interface Reminder {
  iconUrl: string;
  title: string;
  text: string;
}

interface PetCard {
  name: string;
  age: string;
  breed: string;
  sex: string;
  vaccinated: string;
  imageUrl: string;
}

interface EventCard {
  day: string;
  month: string;
  title: string;
  location: string;
}

interface VetCard {
  name: string;
  distance: string;
  status: string;
  rating: string;
  avatarUrl: string;
}

interface ProductCategory {
  label: string;
  imageUrl: string;
}

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  public authService = inject(AuthService);
  private sanitizer = inject(DomSanitizer);

  readonly mapUrl: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
    'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d14629.742352528775!2d-46.666666!3d-23.555555!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMjPCsDMzJzIwLjAiUyA0NsKwMzknNTkuOSJX!5e0!3m2!1spt-BR!2sbr!4v1700000000000!5m2!1spt-BR!2sbr',
  );

  readonly categories: CategoryCard[] = [
    {
      label: 'Pets',
      imageUrl: 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&q=80&w=600',
    },
    {
      label: 'Veterinários',
      imageUrl: 'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?auto=format&fit=crop&q=80&w=600',
    },
    {
      label: 'Eventos',
      imageUrl: 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=600',
    },
    {
      label: 'Produtos',
      imageUrl: 'https://images.unsplash.com/photo-1601758125946-6ec2ef64daf8?auto=format&fit=crop&q=80&w=600',
    },
  ];

  readonly reminders: Reminder[] = [
    {
      iconUrl: 'https://cdn-icons-png.flaticon.com/512/4151/4151022.png',
      title: 'Lembrete: Vacina do seu Buddy',
      text: 'A vacina antirrábica do Zeus vence em 15 dias. Agende já!',
    },
    {
      iconUrl: 'https://cdn-icons-png.flaticon.com/512/3209/3209072.png',
      title: 'Lembrete: Cirurgia de Castração',
      text: 'A agenda de cirurgia de castração está disponível.',
    },
  ];

  readonly pets: PetCard[] = [
    {
      name: 'Kira',
      age: '5 anos',
      breed: 'Vira Lata',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=600',
    },
    {
      name: 'Pêssego',
      age: '2 anos',
      breed: 'Vira Lata',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=600',
    },
    {
      name: 'Jade',
      age: '1 ano',
      breed: 'Mini Lop',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&q=80&w=600',
    },
  ];

  readonly events: EventCard[] = [
    { day: '26', month: 'ABR', title: 'Feira de Adoção - Parque do Ingá', location: 'Maringá, Pr - 9h às 16h' },
    { day: '02', month: 'MAI', title: 'Campanha de Vacinação Gratuita', location: 'Clínica VetAmigos, Maringá - 9h às 16h' },
    { day: '15', month: 'MAI', title: 'Passeio Pet Friendly', location: 'Euro Garden, Maringá - 9h às 16h' },
    { day: '20', month: 'MAI', title: 'Campanha de Castração Gratuita', location: 'Clínica VetAmigos, Maringá - 9h às 16h' },
  ];

  readonly vets: VetCard[] = [
    {
      name: 'Dr. Carlos Melo',
      distance: '1,2km',
      status: 'Aberto agora',
      rating: '4.9',
      avatarUrl: 'https://cdn-icons-png.flaticon.com/512/387/387561.png',
    },
    {
      name: 'Dra. Paula Schneider',
      distance: '3,2km',
      status: 'Abre as 09hrs',
      rating: '4.1',
      avatarUrl: 'https://cdn-icons-png.flaticon.com/512/387/387569.png',
    },
    {
      name: 'Dr. Heitor Krisraff',
      distance: '500m',
      status: 'Aberto 24h',
      rating: '4.7',
      avatarUrl: 'https://cdn-icons-png.flaticon.com/512/387/387561.png',
    },
    {
      name: 'Dra. Camila Rhett',
      distance: '5km',
      status: 'Fechado',
      rating: '4.3',
      avatarUrl: 'https://cdn-icons-png.flaticon.com/512/387/387569.png',
    },
  ];

  readonly products: ProductCategory[] = [
    {
      label: 'Acessorios',
      imageUrl: 'https://images.unsplash.com/photo-1601758124510-52d02ddb7cbd?auto=format&fit=crop&q=80&w=600',
    },
    {
      label: 'Alimentação',
      imageUrl: 'https://images.unsplash.com/photo-1559715541-5daf8a0296d0?auto=format&fit=crop&q=80&w=600',
    },
    {
      label: 'Mochilas',
      imageUrl: 'https://images.unsplash.com/photo-1546975490-a79abdd54533?auto=format&fit=crop&q=80&w=600',
    },
    {
      label: 'Roupas',
      imageUrl: 'https://images.unsplash.com/photo-1588943211346-0908a1fb0b01?auto=format&fit=crop&q=80&w=600',
    },
  ];
}
