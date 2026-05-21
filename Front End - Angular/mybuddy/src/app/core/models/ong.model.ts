export interface SolicitacaoAdocao {
  id: string;
  pet: string;
  adotante: string;
  data: string;
  status: 'Em Análise' | 'Aprovado' | 'Reprovado';
}

export interface EventoOng {
  nome: string;
  local: string;
  data: string;
  status: 'Agendado' | 'Concluído';
}

export interface MeuPetOng {
  nome: string;
  especie: string;
  raca: string;
  idade: string;
  status: 'Disponível' | 'Em Processo' | 'Adotado';
}
