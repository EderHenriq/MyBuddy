export interface ProdutoPetshop {
  nome: string;
  categoria: string;
  preco: string;
  estoque: number;
  status: 'Ativo' | 'Pausado' | 'Esgotado';
}

export interface PedidoPetshop {
  id: string;
  cliente: string;
  valor: string;
  data: string;
  status: 'Aprovado' | 'Enviado' | 'Entregue';
}

export interface ChatPetshop {
  cliente: string;
  ultimaMensagem: string;
  horario: string;
  status: 'Não Lido' | 'Lido';
}
