export interface Parceria {
  id: number;
  nome: string;
  tipo: "ONG" | "Petshop";
  cidade: string;
  status: "Ativo" | "Pendente" | "Bloqueado";
  dataCadastro: string;
}

export interface AdminUsuario {
  id: number;
  nome: string;
  email: string;
  cidade: string;
  status: "Ativo" | "Inativo";
  dataCadastro: string;
}

export interface PetDenuncia {
  id: number;
  nomePet: string;
  ong: string;
  motivo: string;
  dataDenuncia: string;
  status: "Pendente" | "Analisado";
}

export interface Ticket {
  id: number;
  assunto: string;
  usuario: string;
  prioridade: "Alta" | "Média" | "Baixa";
  status: "Aberto" | "Em Andamento" | "Resolvido";
  data: string;
}
