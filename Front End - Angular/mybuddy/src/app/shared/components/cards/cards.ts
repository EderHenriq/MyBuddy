import { Pet } from './../../models/pets.model';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-cards',
  imports: [],
  templateUrl: './cards.html',
  styleUrl: './cards.scss',
})
export class Cards {
  @Input() pet?: Pet;

  petTeste: Pet = {
    fotoPet: 'assets/placeholders/pets/Kira.jpg',
    nomePet: 'Kira',
    idade: '5 anos',
    raca: 'Vira-Lata',
    sexo: 'Fêmea',
    vacinado: true,
  };
}
