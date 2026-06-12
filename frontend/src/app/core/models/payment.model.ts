import { Usuario } from './user.model';
import { Pet } from './pet.model';
import { Pedido } from './petshop.model';

export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';

export interface Payment {
  id: number;
  mpPreferenceId?: string;
  mpPaymentId?: string;
  usuario: Usuario;
  pet?: Pet;
  pedido?: Pedido;
  amount: number;
  status: PaymentStatus;
  createdAt: string;
  updatedAt?: string;
}
