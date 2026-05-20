import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { Footer } from '@shared/components/footer/footer';

interface PetListItem {
  name: string;
  age: string;
  breed: string;
  sex: string;
  vaccinated: string;
  imageUrl: string;
  isFavorite: boolean;
}

interface FilterGroup {
  title: string;
  options: string[];
}

@Component({
  selector: 'app-pets',
  standalone: true,
  imports: [CommonModule, CardPetComponent, Footer],
  templateUrl: './pets.html',
  styleUrl: './pets.scss',
})
export class Pets {
  readonly filterGroups: FilterGroup[] = [
    {
      title: 'Espécie',
      options: ['Cachorro', 'Coelho', 'Gato', 'Pássaro'],
    },
    {
      title: 'Sexo',
      options: ['Fêmea', 'Macho'],
    },
    {
      title: 'Idade',
      options: ['Filhote (0-1 ano)', 'Jovem (1-3 anos)', 'Adulto (3-7 anos)', 'Idoso (+8 anos)'],
    },
    {
      title: 'Porte',
      options: ['Pequeno', 'Médio', 'Grande'],
    },
    {
      title: 'Características',
      options: ['Vacinado', 'Castrado', 'Vive com outros pets'],
    },
  ];

  readonly pets: PetListItem[] = [
    {
      name: 'Thor',
      age: '3 anos',
      breed: 'Border Collie',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=800',
      isFavorite: false,
    },
    {
      name: 'Pêssego',
      age: '5 anos',
      breed: 'SRD',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&q=80&w=800',
      isFavorite: false,
    },
    {
      name: 'Amora',
      age: '3 anos',
      breed: 'Yorkshire',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1588392382834-a891154bca4d?auto=format&fit=crop&q=80&w=800',
      isFavorite: false,
    },
    {
      name: 'Francesca',
      age: '4 anos',
      breed: 'SRD',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=800',
      isFavorite: false,
    },
    {
      name: 'Jade',
      age: '1 ano',
      breed: 'Mini Lop',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&q=80&w=800',
      isFavorite: false,
    },
    {
      name: 'Armindo',
      age: '2 anos',
      breed: 'Rex',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1609151354448-c4a53450c6e9?auto=format&fit=crop&q=80&w=800',
      isFavorite: false,
    },
  ];

  toggleFavorite(pet: PetListItem): void {
    pet.isFavorite = !pet.isFavorite;
  }
}
