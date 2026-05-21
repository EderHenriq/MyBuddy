import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { CardServicoComponent } from '@shared/components/card-servico/card-servico.component';
import { Footer } from '@shared/components/footer/footer';

interface Servico {
  id: number;
  imageUrl: string;
  type: string;
  title: string;
  rating: number;
  reviewsCount: number;
  locationStr: string;
  distanceStr: string;
  openHoursStr: string;
  description: string;
  isFavorite: boolean;
}

@Component({
  selector: 'app-servicos',
  standalone: true,
  imports: [CommonModule, CardServicoComponent, Footer],
  templateUrl: './servicos.component.html',
  styleUrl: './servicos.component.scss'
})
export class ServicosComponent {
  servicos: Servico[] = [
    {
      id: 1,
      imageUrl: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&q=80&w=800',
      type: 'Veterinário',
      title: 'Clínica Veterinária Vida Animal',
      rating: 4.8,
      reviewsCount: 124,
      locationStr: 'Centro, São Paulo',
      distanceStr: '2.5 km',
      openHoursStr: 'Aberto 24h',
      description: 'Clínica completa com atendimento de emergência, cirurgias e especialidades diversas.',
      isFavorite: false
    },
    {
      id: 2,
      imageUrl: 'https://images.unsplash.com/photo-1541364983171-a8ba01e95cfc?auto=format&fit=crop&q=80&w=800',
      type: 'Hospital Pet',
      title: 'Hospital Veterinário Pet Care',
      rating: 4.9,
      reviewsCount: 350,
      locationStr: 'Vila Mariana, SP',
      distanceStr: '4.2 km',
      openHoursStr: 'Aberto 24h',
      description: 'Estrutura hospitalar avançada para o cuidado intensivo do seu melhor amigo.',
      isFavorite: true
    },
    {
      id: 3,
      imageUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800',
      type: 'Hotel Pet',
      title: 'Resort Cão Feliz',
      rating: 4.7,
      reviewsCount: 89,
      locationStr: 'Cotia, SP',
      distanceStr: '15 km',
      openHoursStr: '08:00 - 18:00',
      description: 'Hotel fazenda com ampla área verde, piscina e recreação monitorada 24h.',
      isFavorite: false
    },
    {
      id: 4,
      imageUrl: 'https://images.unsplash.com/photo-1516366478644-84566f1e149f?auto=format&fit=crop&q=80&w=800',
      type: 'Adestramento',
      title: 'Dog Training Brasil',
      rating: 5.0,
      reviewsCount: 42,
      locationStr: 'Pinheiros, SP',
      distanceStr: '3.1 km',
      openHoursStr: '09:00 - 17:00',
      description: 'Comportamento canino e obediência com métodos de reforço positivo.',
      isFavorite: false
    },
    {
      id: 5,
      imageUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=800',
      type: 'Clínica Felina',
      title: 'Só Gatos Medicina Felina',
      rating: 4.9,
      reviewsCount: 210,
      locationStr: 'Moema, SP',
      distanceStr: '5.0 km',
      openHoursStr: '08:00 - 20:00',
      description: 'Atendimento exclusivo para felinos com ambiente cat-friendly certificado.',
      isFavorite: true
    },
    {
      id: 6,
      imageUrl: 'https://images.unsplash.com/photo-1524661135-423995f22d0b?auto=format&fit=crop&q=80&w=800',
      type: 'Creche',
      title: 'Pet Daycare Amigão',
      rating: 4.6,
      reviewsCount: 78,
      locationStr: 'Brooklin, SP',
      distanceStr: '6.5 km',
      openHoursStr: '07:00 - 19:00',
      description: 'Seu pet se diverte e gasta energia enquanto você trabalha. Muita brincadeira e amor.',
      isFavorite: false
    }
  ];

  verDetalhes(servico: Servico) {
    console.log('Ver detalhes de', servico.title);
  }

  toggleFavorite(servico: Servico) {
    servico.isFavorite = !servico.isFavorite;
  }
}
