import { Organizacao } from "./organizacao.model";

export interface FotoPet {
  id?: number;
  url: string;
}

export interface Pet {
  // Identificador unificado
  id: any;

  // Campos em Português (HEAD / Integração Backend)
  nome?: string;
  raca?: string;
  idade?: number;
  especie?: string;
  porte?: string;
  cor?: string;
  pelagem?: string;
  sexo?: string;
  fotos?: FotoPet[];
  statusAdocao?: string;
  organizacao?: Organizacao;
  microchipado?: boolean;
  vacinado?: boolean;
  castrado?: boolean;
  cidade?: string;
  estado?: string;
  peso?: number;
  dataCriacao?: string;
  dataAtualizacao?: string;

  // Campos em Inglês (origin/Developer / Mock Landing Page)
  ownerId?: string;
  name?: string;
  species?: string;
  gender?: string;
  breed?: string;
  age?: number;
  weight?: number;
  createdAt?: string;
  updatedAt?: string;
  imageUrl?: string;
  isVaccinated?: boolean;
  description?: string;
}
