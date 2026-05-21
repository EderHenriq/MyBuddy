import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OngService } from '../../../core/services/ong.service';
import { MeuPetOng } from '../../../core/models/ong.model';

@Component({
  selector: 'app-meus-pets',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './meus-pets.html',
  styleUrl: './meus-pets.scss'
})
export class MeusPets implements OnInit {
  pets: MeuPetOng[] = [];
  private ongService = inject(OngService);

  ngOnInit() {
    this.ongService.getMeusPets().subscribe(data => {
      this.pets = data;
    });
  }
}
