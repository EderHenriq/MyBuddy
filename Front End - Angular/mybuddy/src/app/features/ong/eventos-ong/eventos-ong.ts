import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OngService } from '../../../core/services/ong.service';
import { EventoOng } from '../../../core/models/ong.model';

@Component({
  selector: 'app-eventos-ong',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './eventos-ong.html',
  styleUrl: './eventos-ong.scss'
})
export class EventosOng implements OnInit {
  eventos: EventoOng[] = [];
  private ongService = inject(OngService);

  ngOnInit() {
    this.ongService.getEventos().subscribe(data => {
      this.eventos = data;
    });
  }
}
