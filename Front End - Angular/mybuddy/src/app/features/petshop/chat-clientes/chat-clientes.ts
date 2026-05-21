import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PetshopService } from '../../../core/services/petshop.service';
import { ChatPetshop } from '../../../core/models/petshop.model';

@Component({
  selector: 'app-chat-clientes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chat-clientes.html',
  styleUrl: './chat-clientes.scss'
})
export class ChatClientes implements OnInit {
  chats: ChatPetshop[] = [];
  private petshopService = inject(PetshopService);

  ngOnInit() {
    this.petshopService.getChats().subscribe(data => {
      this.chats = data;
    });
  }
}
