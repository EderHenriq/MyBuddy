import { Usuario } from "./user.model";

export interface Petshop {
  id: number;
  nomeFantasia: string;
  emailContato: string;
  cnpj: string;
  telefoneContato?: string;
  endereco: string;
  descricao?: string;
  website?: string;
  produtos?: Produto[];
  usuarios?: Usuario[];
}

export interface FotoProduto {
  id?: number;
  url: string;
}

export interface Produto {
  id: number;
  nome: string;
  descricao?: string;
  categoria: string;
  preco: number;
  estoque: number;
  status: string;
  petshop?: Petshop;
  fotos?: FotoProduto[];
  dataCriacao?: string;
  dataAtualizacao?: string;
}

export interface ItemPedido {
  id?: number;
  produto?: Produto;
  quantidade: number;
  precoUnitario: number;
}

export interface Pedido {
  id: number;
  cliente?: Usuario;
  petshop?: Petshop;
  itens?: ItemPedido[];
  valorTotal: number;
  status: string;
  dataCriacao?: string;
  dataAtualizacao?: string;
}

export interface ChatPetshop {
  cliente: string;
  ultimaMensagem: string;
  horario: string;
  status: "Não Lido" | "Lido";
}
