import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { PedidoService } from "@core/services/pedido.service";
import { ProdutoService } from "@core/services/produto.service";
import { Footer } from "@shared/components/footer/footer";

@Component({
  selector: "app-meus-pedidos",
  standalone: true,
  imports: [CommonModule, RouterModule, Footer, FormsModule],
  templateUrl: "./meus-pedidos.html",
  styleUrl: "./meus-pedidos.scss",
})
export class MeusPedidos implements OnInit {
  private pedidoService = inject(PedidoService);
  private produtoService = inject(ProdutoService);

  pedidos = signal<any[]>([]);
  pedidoSelecionado = signal<any | null>(null);
  loading = signal<boolean>(false);

  // Controle de Avaliação
  isModalAvaliacaoAberto = false;
  produtoParaAvaliar: any = null;
  notaAvaliacao = 0;
  comentarioAvaliacao = "";

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
    event.stopPropagation();
    if (confirm("Tem certeza que deseja cancelar este pedido?")) {
      this.pedidoService.cancelarPedido(id).subscribe({
        next: () => {
          this.carregarPedidos();
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

  abrirModalAvaliacao(produto: any): void {
    this.produtoParaAvaliar = produto;
    this.notaAvaliacao = 5;
    this.comentarioAvaliacao = "";
    this.isModalAvaliacaoAberto = true;
  }

  fecharModalAvaliacao(): void {
    this.isModalAvaliacaoAberto = false;
    this.produtoParaAvaliar = null;
  }

  enviarAvaliacao(): void {
    if (this.notaAvaliacao === 0 || !this.produtoParaAvaliar) return;

    this.produtoService.avaliarProduto(this.produtoParaAvaliar.id, {
      nota: this.notaAvaliacao,
      comentario: this.comentarioAvaliacao
    }).subscribe({
      next: () => {
        alert("Sua avaliação foi enviada com sucesso! Muito obrigado pelo seu feedback.");
        this.fecharModalAvaliacao();
      },
      error: (err) => {
        console.error(err);
        alert("Ocorreu um erro ao enviar a avaliação.");
      }
    });
  }

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
        return -1;
    }
  }
}
