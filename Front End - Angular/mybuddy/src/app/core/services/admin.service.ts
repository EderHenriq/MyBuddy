import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Parceria, AdminUsuario, PetDenuncia, Ticket } from '../models/admin.model';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private api = inject(ApiService);

  constructor() {}

  buscarOngs(): Observable<Parceria[]> {
    return this.api.get<Parceria[]>('admin/ongs');
  }

  buscarUsuarios(): Observable<AdminUsuario[]> {
    return this.api.get<AdminUsuario[]>('admin/usuarios');
  }

  buscarPetsDenunciados(): Observable<PetDenuncia[]> {
    return this.api.get<PetDenuncia[]>('admin/denuncias');
  }

  buscarTicketsSuporte(): Observable<Ticket[]> {
    return this.api.get<Ticket[]>('admin/tickets');
  }
}
