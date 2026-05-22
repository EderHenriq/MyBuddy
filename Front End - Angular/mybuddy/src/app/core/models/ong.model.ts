import { Usuario } from './user.model';
import { Pet } from './pet.model';


export interface EventoOng {
  nome: string;
  local: string;
  data: string;
  status: 'Agendado' | 'Concluído';
}

export interface MeuPetOng {
  nome: string;
  especie: string;
  raca: string;
  idade: string;
  status: 'Disponível' | 'Em Processo' | 'Adotado';
}
