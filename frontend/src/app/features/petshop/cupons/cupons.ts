import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { PedidoService } from "../../../core/services/pedido.service";

@Component({
  selector: "app-cupons",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./cupons.html",
  styleUrl: "./cupons.scss",
})
export class Cupons implements OnInit {
  private pedidoService = inject(PedidoService);

  cupons: any[] = [];
  isModalAberto = false;
  isSimulationActive = true; // Declaração de simulação/mock ativa para visualização dos desenvolvedores

  formCupom = {
    codigo: "",
    percentualDesconto: 10,
    ativo: true,
    dataInicio: "",
    dataExpiracao: "",
    valorMinimoPedido: 0,
    limiteUsoGeral: 100,
  };

  ngOnInit() {
    this.carregarCupons();
    // Preenche a data de início com o dia atual por padrão
    const hoje = new Date().toISOString().split("T")[0];
    this.formCupom.dataInicio = hoje;
  }

  carregarCupons() {
    this.pedidoService.buscarCupons().subscribe({
      next: (data) => {
        this.cupons = data;
      },
      error: (err) => console.error("[Cupons] Erro ao carregar cupons:", err),
    });
  }

  abrirModalNovo() {
    const hoje = new Date().toISOString().split("T")[0];
    this.formCupom = {
      codigo: "",
      percentualDesconto: 10,
      ativo: true,
      dataInicio: hoje,
      dataExpiracao: "",
      valorMinimoPedido: 0,
      limiteUsoGeral: 100,
    };
    this.isModalAberto = true;
  }

  fecharModal() {
    this.isModalAberto = false;
  }

  salvarCupom() {
    if (!this.formCupom.codigo.trim()) {
      alert("Por favor, preencha o código do cupom.");
      return;
    }

    const request = {
      codigo: this.formCupom.codigo.toUpperCase().trim(),
      percentualDesconto: Number(this.formCupom.percentualDesconto),
      ativo: this.formCupom.ativo,
      dataInicio: this.formCupom.dataInicio || null,
      dataExpiracao: this.formCupom.dataExpiracao || null,
      valorMinimoPedido: Number(this.formCupom.valorMinimoPedido) || 0,
      limiteUsoGeral: Number(this.formCupom.limiteUsoGeral) || null,
    };

    this.pedidoService.criarCupom(request).subscribe({
      next: () => {
        this.fecharModal();
        this.carregarCupons();
      },
      error: (err) => {
        console.error("[Cupons] Erro ao cadastrar cupom:", err);
        alert("Erro ao cadastrar cupom.");
      },
    });
  }

  toggleStatus(cupom: any) {
    const novoStatus = !cupom.ativo;
    this.pedidoService.alterarStatusCupom(cupom.id, novoStatus).subscribe({
      next: () => {
        cupom.ativo = novoStatus;
      },
      error: (err) => {
        console.error("[Cupons] Erro ao alterar status:", err);
        alert("Erro ao alterar status do cupom.");
      },
    });
  }
}
