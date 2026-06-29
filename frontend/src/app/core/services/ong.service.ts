import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { ApiService } from "./api.service";
import { EventoOng, MeuPetOng } from "../models/ong.model";
import { InteresseAdocao } from "../models/interesse-adocao.model";

@Injectable({
  providedIn: "root",
})
export class OngService {
  private api = inject(ApiService);

  constructor() {}

  buscarSolicitacoes(): Observable<InteresseAdocao[]> {
    return this.api.get<InteresseAdocao[]>("ong/solicitacoes");
  }

  buscarEventos(): Observable<EventoOng[]> {
    return this.api.get<EventoOng[]>("ong/eventos");
  }

  buscarMeusPets(): Observable<MeuPetOng[]> {
    return this.api.get<MeuPetOng[]>("ong/pets");
  }
}
