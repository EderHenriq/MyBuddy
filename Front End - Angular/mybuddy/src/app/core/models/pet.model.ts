import { Organizacao } from './organizacao.model';

export interface FotoPet {
  id?: number;
  url: string;
}

export interface Pet {
  id: number;
  nome: string;
  raca: string;
  idade: number;
  especie: string;
  porte: string;
  cor: string;
  pelagem: string;
  sexo: string;
  fotos?: FotoPet[];
  statusAdocao: string;
  organizacao?: Organizacao;
  microchipado: boolean;
  vacinado: boolean;
  castrado: boolean;
  cidade: string;
  estado: string;
  peso?: number;
  dataCriacao?: string;
  dataAtualizacao?: string;
}
