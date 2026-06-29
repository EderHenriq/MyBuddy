import { Component, OnInit, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { PetshopService } from "../../../core/services/petshop.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-dashboard-petshop",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./dashboard-petshop.html",
  styleUrl: "./dashboard-petshop.scss",
})
export class DashboardPetshop implements OnInit {
  private petshopService = inject(PetshopService);
  private router = inject(Router);

  metrics = signal<any[]>([]);
  ultimosPedidos = signal<any[]>([]);
  dadosGrafico = [
    { dia: "Seg", valor: 1200 },
    { dia: "Ter", valor: 900 },
    { dia: "Qua", valor: 1500 },
    { dia: "Qui", valor: 1800 },
    { dia: "Sex", valor: 2200 },
    { dia: "Sáb", valor: 2700 },
    { dia: "Dom", valor: 1600 }
  ];

  ngOnInit() {
    this.carregarDadosPainel();
  }

  carregarDadosPainel() {
    // Busca produtos
    this.petshopService.buscarProdutos().subscribe((produtos) => {
      const totalProdutos = produtos.length;

      // Busca pedidos
      this.petshopService.buscarPedidos().subscribe((pedidos) => {
        this.ultimosPedidos.set(pedidos.slice(0, 3)); // Pega os 3 mais recentes

        const pedidosNovos = pedidos.filter(p => p.status === "PROCESSANDO" || p.status === "PENDENTE").length;
        const totalVendasVal = pedidos
          .filter(p => p.status === "ENTREGUE")
          .reduce((soma, p) => soma + p.valorTotal, 0);

        // Busca chats
        this.petshopService.buscarChats().subscribe((chats) => {
          const naoLidas = chats.filter(c => c.status === "Não Lido").length;

          // Atualiza as métricas dinamicamente
          this.metrics.set([
            {
              title: "Vendas Concluídas",
              value: totalVendasVal > 0 ? `R$ ${totalVendasVal.toFixed(2)}` : "R$ 0,00",
              icon: "payments",
              color: "#009688",
            },
            {
              title: "Pedidos Novos",
              value: pedidosNovos.toString(),
              icon: "shopping_bag",
              color: "#ff7900",
            },
            {
              title: "Produtos Cadastrados",
              value: totalProdutos.toString(),
              icon: "inventory_2",
              color: "#3f51b5",
            },
            { 
              title: "Chats Pendentes", 
              value: naoLidas.toString(), 
              icon: "chat", 
              color: "#e91e63" 
            },
          ]);
        });
      });
    });
  }

  irPara(rota: string) {
    this.router.navigate([rota]);
  }
}

