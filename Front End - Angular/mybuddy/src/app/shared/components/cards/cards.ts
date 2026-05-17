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
}
