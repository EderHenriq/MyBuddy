import { Role } from "./role.model";
import { Organizacao } from "./organizacao.model";
import { Petshop } from "./petshop.model";

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  keycloakId?: string;
  urlAvatar?: string;
  dataCriacao?: string;
  dataAtualizacao?: string;
  roles?: Role[];
  organizacao?: Organizacao;
  petshop?: Petshop;
}
