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
  private pedidoService = inject(PedidoService);
  private router = inject(Router);

  // Informações de Endereço
  cep = "";
  rua = "";
  numero = "";
  complemento = "";
  bairro = "";
  cidade = "";
  estado = "";

  // Pagamento
  metodoPagamento = "pix"; // 'pix' ou 'cartao'
  isProcessando = false;
  mensagemProgresso = "";

  // Dados do Cartão (Simulado)
  numeroCartao = "";
  nomeTitular = "";
  validade = "";
  cvv = "";

  ngOnInit(): void {
    // Se o carrinho estiver vazio, manda o usuário de volta para o catálogo
    if (this.cartService.totalItens() === 0) {
      this.router.navigate(["/produtos"]);
    }
  }

  // Preenche dados simulados para CEP rápido
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

  get taxaEntrega(): number {
    return this.cartService.totalItens() > 0 ? 15.0 : 0.0;
  }

  get totalGeral(): number {
    return this.cartService.precoTotal() + this.taxaEntrega;
  }

  realizarPagamentoMock(): void {
    // Validações básicas
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

    setTimeout(() => {
      this.mensagemProgresso = "Validando dados da transação mock...";
      
      setTimeout(() => {
        this.mensagemProgresso = "Processando pagamento e salvando o pedido...";

        // Monta a requisição
        const request: PedidoRequest = {
          petshopId: 1, // Assumido Petz/Parceiro 1 por padrão no mock
          itens: this.cartService.itensCarrinho().map((item) => ({
            produtoId: item.id,
            quantidade: item.quantidade,
          })),
          enderecoEntregaSimulado: `${this.rua}, ${this.numero} ${this.complemento ? "- " + this.complemento : ""} - ${this.bairro}, ${this.cidade}/${this.estado}`,
          metodoPagamento: this.metodoPagamento.toUpperCase(),
        };

        this.pedidoService.criarPedido(request).subscribe({
          next: (pedidoCriado) => {
            setTimeout(() => {
              this.cartService.limparCarrinho();
              this.isProcessando = false;
              // Navega para a página de confirmação de sucesso
              this.router.navigate(["/checkout/confirmacao"], {
                queryParams: {
                  pedidoId: pedidoCriado.id,
                  total: pedidoCriado.valorTotal,
                },
              });
            }, 1000);
          },
          error: (err) => {
            console.error(err);
            this.isProcessando = false;
            alert("Erro ao finalizar o pedido. Tente novamente.");
          },
        });
      }, 1000);
    }, 1000);
  }
}
