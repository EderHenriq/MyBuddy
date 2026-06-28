export interface NotificacaoApp {
  id: string;
  titulo: string;
  mensagem: string;
  data: string;
  lida: boolean;
  tipo: "info" | "success" | "warning" | "error";
  link?: string;
}

export interface HistoricoAtividade {
  id: string;
  acao: string;
  descricao: string;
  data: string;
  icone: string;
  tipo: "auth" | "pet" | "system" | "adoption";
}
