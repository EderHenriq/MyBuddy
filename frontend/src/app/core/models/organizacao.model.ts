import { Pet } from './pet.model';
import { Usuario } from './user.model';

export interface Organizacao {
  id: number;
  nomeFantasia: string;
  emailContato: string;
  cnpj: string;
  telefoneContato?: string;
  endereco: string;
  descricao?: string;
  website?: string;
  pets?: Pet[];
  usuarios?: Usuario[];
}
