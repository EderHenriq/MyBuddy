import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { RouterModule } from "@angular/router";
import { PedidoService } from "@core/services/pedido.service";
import { Footer } from "@shared/components/footer/footer";

@Component({
  selector: "app-meus-pedidos",
  standalone: true,
  imports: [CommonModule, RouterModule, Footer],
  templateUrl: "./meus-pedidos.html",
  styleUrl: "./meus-pedidos.scss",
})
export class MeusPedidos implements OnInit {
  private pedidoService = inject(PedidoService);
  
  pedidos = signal<any[]>([]);
  pedidoSelecionado = signal<any | null>(null);
  loading = signal<boolean>(false);

  ngOnInit(): void {
    this.carregarPedidos();
  }

  carregarPedidos(): void {
    this.loading.set(true);
    this.pedidoService.listarMeusPedidos().subscribe({
      next: (data) => {
        this.pedidos.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.loading.set(false);
      },
    });
  }

  selecionarPedido(pedido: any): void {
    this.pedidoSelecionado.set(pedido);
  }

  fecharDetalhes(): void {
    this.pedidoSelecionado.set(null);
  }

  cancelarPedido(id: number, event: Event): void {
    event.stopPropagation(); // Evita abrir os detalhes ao clicar no botão
    if (confirm("Tem certeza que deseja cancelar este pedido?")) {
      this.pedidoService.cancelarPedido(id).subscribe({
        next: () => {
          this.carregarPedidos();
          // Atualiza os detalhes se o pedido cancelado estiver aberto
          const selecionado = this.pedidoSelecionado();
          if (selecionado && selecionado.id === id) {
            selecionado.status = "CANCELADO";
            this.pedidoSelecionado.set({ ...selecionado });
          }
        },
        error: (err) => {
          console.error(err);
          alert("Não foi possível cancelar o pedido.");
        },
      });
    }
  }

  // Retorna a etapa ativa na timeline (0 a 3) com base no status do pedido
  obterEtapaTimeline(status: string): number {
    switch (status.toUpperCase()) {
      case "PENDENTE":
        return 0;
      case "PROCESSANDO":
        return 1;
      case "EM_TRANSITO":
      case "A_CAMINHO":
        return 2;
      case "ENTREGUE":
        return 3;
      default:
        return -1; // Para CANCELADO
    }
  }
}
