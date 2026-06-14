import { Usuario } from './user.model';
import { Pet } from './pet.model';

export type StatusInteresse = 'PENDENTE' | 'APROVADO' | 'REJEITADO' | 'CANCELADO';

export interface InteresseAdocao {
  id: number;
  usuario: Usuario;
  pet: Pet;
  status: StatusInteresse;
  mensagem?: string;
  criadoEm?: string;
  atualizadoEm?: string;
}
