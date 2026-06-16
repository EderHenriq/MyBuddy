import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { Router, RouterModule } from "@angular/router";
import { CartService } from "@core/services/cart.service";
import { PedidoService, PedidoRequest } from "@core/services/pedido.service";
import { Footer } from "@shared/components/footer/footer";

@Component({
  selector: "app-checkout",
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, Footer],
  templateUrl: "./checkout.html",
  styleUrl: "./checkout.scss",
})
export class Checkout implements OnInit {
  cartService = inject(CartService);
  pedidoService = inject(PedidoService);
  private router = inject(Router);

  // Informações de Endereço
  cep = "";
  rua = "";
  numero = "";
  complemento = "";
  bairro = "";
  cidade = "";
  estado = "";

  // Pagamento e Fluxo
  metodoPagamento = "pix";
  isProcessando = false;
  mensagemProgresso = "";

  // Dados do Cartão (Simulado)
  numeroCartao = "";
  nomeTitular = "";
  validade = "";
  cvv = "";

  // Cupons
  codigoCupom = "";
  cupomAplicado: any = null;

  // Lojas agrupadas
  lojasAgrupadas: any[] = [];

  ngOnInit(): void {
    if (this.cartService.totalItens() === 0) {
      this.router.navigate(["/produtos"]);
      return;
    }
    this.agruparItensPorLoja();
  }

  agruparItensPorLoja() {
    const itens = this.cartService.itensCarrinho();
    const grupos: Record<number, { petshopId: number; lojaNome: string; itens: any[]; subtotal: number; taxaEntrega: number; total: number }> = {};

    itens.forEach((it) => {
      const pId = it.petshopId || 1;
      const lNome = it.lojaNome || "Petshop Parceiro";
      if (!grupos[pId]) {
        grupos[pId] = {
          petshopId: pId,
          lojaNome: lNome,
          itens: [],
          subtotal: 0,
          taxaEntrega: 15.0, // R$ 15,00 por loja
          total: 0
        };
      }
      grupos[pId].itens.push(it);
      grupos[pId].subtotal += it.preco * it.quantidade;
    });

    this.lojasAgrupadas = Object.values(grupos);
  }

  aplicarCupom() {
    if (!this.codigoCupom.trim()) {
      alert("Por favor, digite o código do cupom.");
      return;
    }

    const firstPetshopId = this.lojasAgrupadas[0]?.petshopId || 1;
    this.pedidoService.validarCupom(this.codigoCupom, firstPetshopId).subscribe({
      next: (cupom) => {
        this.cupomAplicado = cupom;
        alert(`Cupom ${cupom.codigo} de ${cupom.percentualDesconto}% aplicado com sucesso!`);
      },
      error: (err) => {
        alert(err.message || "Cupom inválido ou expirado.");
        this.cupomAplicado = null;
      }
    });
  }

  removerCupom() {
    this.cupomAplicado = null;
    this.codigoCupom = "";
  }

  get totalSubtotal(): number {
    return this.lojasAgrupadas.reduce((soma, grupo) => soma + grupo.subtotal, 0);
  }

  get totalFrete(): number {
    return this.lojasAgrupadas.reduce((soma, grupo) => soma + grupo.taxaEntrega, 0);
  }

  get totalDesconto(): number {
    if (this.cupomAplicado) {
      return (this.totalSubtotal * this.cupomAplicado.percentualDesconto) / 100;
    }
    return 0;
  }

  get totalGeral(): number {
    return this.totalSubtotal + this.totalFrete - this.totalDesconto;
  }

  simularCep(): void {
    if (this.cep.replace(/\D/g, "") === "87013000") {
      this.rua = "Avenida Brasil";
      this.numero = "1234";
      this.bairro = "Zona 01";
      this.cidade = "Maringá";
      this.estado = "PR";
    } else {
      this.rua = "Rua das Flores";
      this.numero = "100";
      this.bairro = "Centro";
      this.cidade = "São Paulo";
      this.estado = "SP";
    }
  }

  realizarPagamentoMock(): void {
    if (!this.rua || !this.numero || !this.bairro || !this.cidade) {
      alert("Por favor, preencha os campos obrigatórios do endereço de entrega.");
      return;
    }

    if (this.metodoPagamento === "cartao" && (!this.numeroCartao || !this.nomeTitular || !this.cvv)) {
      alert("Por favor, preencha os dados do cartão de crédito.");
      return;
    }

    this.isProcessando = true;
    this.mensagemProgresso = "Conectando ao gateway de pagamento (Simulador)...";

    // Criar as requisições separadas por loja
    const requests: PedidoRequest[] = this.lojasAgrupadas.map((grupo) => ({
      petshopId: grupo.petshopId,
      itens: grupo.itens.map((item: any) => ({
        produtoId: item.id,
        quantidade: item.quantidade,
      })),
      enderecoEntregaSimulado: `${this.rua}, ${this.numero} ${this.complemento ? "- " + this.complemento : ""} - ${this.bairro}, ${this.cidade}/${this.estado}`,
      metodoPagamento: this.metodoPagamento.toUpperCase(),
      // Mapeia cupom se aplicável
      ...(this.cupomAplicado && { cupomDesconto: this.cupomAplicado.codigo })
    }));

    setTimeout(() => {
      this.mensagemProgresso = "Validando transações no modo simulação...";
      
      setTimeout(() => {
        this.mensagemProgresso = "Registrando pedidos separados por loja (Multi-vendor)...";

        const pedidosCriados: any[] = [];
        
        const submeterSequencial = (index: number) => {
          if (index >= requests.length) {
            // Sucesso em todas as submissões
            setTimeout(() => {
              this.cartService.limparCarrinho();
              this.isProcessando = false;
              this.router.navigate(["/checkout/confirmacao"], {
                queryParams: {
                  pedidoId: pedidosCriados.map((p) => p.id).join(", "),
                  total: this.totalGeral,
                },
              });
            }, 1000);
            return;
          }

          this.pedidoService.criarPedido(requests[index]).subscribe({
            next: (pedidoCriado) => {
              pedidosCriados.push(pedidoCriado);
              submeterSequencial(index + 1);
            },
            error: (err) => {
              console.error("[Checkout] Erro ao submeter pacote", index, err);
              // Avança mesmo assim no mock para simulação local fluida
              const fallbackPedido = { id: 1000 + index + Math.floor(Math.random()*100), valorTotal: 0 };
              pedidosCriados.push(fallbackPedido);
              submeterSequencial(index + 1);
            }
          });
        };

        submeterSequencial(0);
      }, 1000);
    }, 1000);
  }
}
