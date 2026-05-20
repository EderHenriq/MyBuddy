import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { Footer } from '@shared/components/footer/footer';

interface PetListItem {
  id: number;
  name: string;
  age: string;
  breed: string;
  sex: string;
  vaccinated: string;
  imageUrl: string;
}

@Component({
  selector: 'app-meus-pets',
  standalone: true,
  imports: [CommonModule, RouterModule, CardPetComponent, Footer],
  templateUrl: './meus-pets.html',
  styleUrl: './meus-pets.scss',
})
export class MeusPets {
  readonly myPets: PetListItem[] = [
    {
      id: 1,
      name: 'Thor',
      age: '3 anos',
      breed: 'Border Collie',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=800',
    },
    {
      id: 2,
      name: 'Pêssego',
      age: '5 anos',
      breed: 'SRD',
      sex: 'Macho',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&q=80&w=800',
    },
    {
      id: 3,
      name: 'Amora',
      age: '3 anos',
      breed: 'Yorkshire',
      sex: 'Fêmea',
      vaccinated: 'Sim',
      imageUrl: 'https://images.unsplash.com/photo-1588392382834-a891154bca4d?auto=format&fit=crop&q=80&w=800',
    }
  ];

  editPet(pet: PetListItem): void {
    console.log('Editar pet:', pet.name);
    // Aqui você navegaria para a rota de edição: this.router.navigate(['ong/pets/editar', pet.id]);
  }

  deletePet(pet: PetListItem): void {
    console.log('Deletar pet:', pet.name);
    // Aqui chamaria a API para deletar e atualizaria a lista local.
  }
}
