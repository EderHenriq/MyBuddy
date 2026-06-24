export interface CartaoCategoria {
  rotulo: string;
  urlImage: string;
  rota: string;
}

export interface Lembrete {
  urlIcone: string;
  titulo: string;
  texto: string;
}

export interface CartaoEvento {
  dia: string;
  mes: string;
  titulo: string;
  local: string;
}

export interface CartaoVeterinario {
  nome: string;
  distancia: string;
  status: string;
  avaliacao: string;
  urlAvatar: string;
}

export interface CategoriaProduto {
  rotulo: string;
  urlImage: string;
}
