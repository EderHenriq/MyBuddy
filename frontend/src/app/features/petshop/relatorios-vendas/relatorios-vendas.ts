import { Component, OnInit, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { PetshopService } from "../../../core/services/petshop.service";

interface TopProduto {
  nome: string;
  quantidade: number;
  faturamento: number;
  percentual: number;
}

@Component({
  selector: "app-relatorios-vendas",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./relatorios-vendas.html",
  styleUrl: "./relatorios-vendas.scss",
})
export class RelatoriosVendas implements OnInit {
  private petshopService = inject(PetshopService);

  isSimulationActive = true;
  periodoSelecionado = "30"; // 7, 30 ou 90 dias

  // Métricas do painel
  faturamentoTotal = 0;
  tiqueMedio = 0;
  totalPedidos = 0;
  totalItensVendidos = 0;

  // Lista de produtos mais vendidos
  topProdutos = signal<TopProduto[]>([]);

  // Dados dos gráficos tridimensionais
  dadosVendasMensais = [
    { label: "Jan", valor: 8500, altura: "45%" },
    { label: "Fev", valor: 10200, altura: "55%" },
    { label: "Mar", valor: 14300, altura: "75%" },
    { label: "Abr", valor: 12100, altura: "64%" },
    { label: "Mai", valor: 18900, altura: "100%" },
    { label: "Jun", valor: 15420, altura: "81%" }
  ];

  ngOnInit() {
    this.carregarRelatorios();
  }

  carregarRelatorios() {
    this.petshopService.buscarPedidos().subscribe({
      next: (pedidos) => {
        this.processarDadosPedidos(pedidos);
      },
      error: (err) => {
        console.error("[RelatoriosVendas] Erro ao carregar pedidos:", err);
      }
    });
  }

  onPeriodoChange() {
    this.carregarRelatorios();
  }

  private processarDadosPedidos(pedidos: any[]) {
    // Filtra pedidos relevantes (ex: não cancelados)
    const validPedidos = pedidos.filter(p => p.status !== "CANCELADO");
    
    // Filtro por período (simulado com base no período selecionado)
    // Para simplificar e manter bonito, filtramos todos se for 90, ou uma fração se for 7
    let pedidosFiltrados = [...validPedidos];
    const limiteDias = Number(this.periodoSelecionado);
    
    if (limiteDias === 7) {
      // Pega apenas os últimos 7 dias de pedidos
      const dataLimite = new Date();
      dataLimite.setDate(dataLimite.getDate() - 7);
      pedidosFiltrados = validPedidos.filter(p => new Date(p.dataCriacao) >= dataLimite);
    } else if (limiteDias === 30) {
      const dataLimite = new Date();
      dataLimite.setDate(dataLimite.getDate() - 30);
      pedidosFiltrados = validPedidos.filter(p => new Date(p.dataCriacao) >= dataLimite);
    }

    // Calcula métricas financeiras
    this.totalPedidos = pedidosFiltrados.length;
    this.faturamentoTotal = pedidosFiltrados.reduce((soma, p) => soma + (p.valorTotal || 0), 0);
    this.tiqueMedio = this.totalPedidos > 0 ? this.faturamentoTotal / this.totalPedidos : 0;

    // Processamento de itens e ranking de produtos mais vendidos
    const mapaProdutos: { [nome: string]: { qtd: number; fat: number } } = {};
    let totalItens = 0;

    pedidosFiltrados.forEach(pedido => {
      if (pedido.itens && Array.isArray(pedido.itens)) {
        pedido.itens.forEach((item: any) => {
          const prodNome = item.produto?.nome || item.nome || "Produto Desconhecido";
          const qtd = item.quantidade || 1;
          const preco = item.precoUnitario || item.preco || 0;
          const subtotal = qtd * preco;

          totalItens += qtd;

          if (!mapaProdutos[prodNome]) {
            mapaProdutos[prodNome] = { qtd: 0, fat: 0 };
          }
          mapaProdutos[prodNome].qtd += qtd;
          mapaProdutos[prodNome].fat += subtotal;
        });
      }
    });

    this.totalItensVendidos = totalItens;

    // Converte mapa para array e ordena
    const produtosOrdenados = Object.keys(mapaProdutos).map(nome => {
      return {
        nome,
        quantidade: mapaProdutos[nome].qtd,
        faturamento: mapaProdutos[nome].fat,
        percentual: 0
      };
    }).sort((a, b) => b.quantidade - a.quantidade);

    // Adiciona percentuais relativos ao mais vendido
    const maxQtd = produtosOrdenados.length > 0 ? produtosOrdenados[0].quantidade : 1;
    produtosOrdenados.forEach(p => {
      p.percentual = Math.round((p.quantidade / maxQtd) * 100);
    });

    // Se não houver pedidos no período, injeta dados fictícios para fins de demonstração rica
    if (produtosOrdenados.length === 0) {
      const mockTop = [
        { nome: "Ração Premier Formula Cães Adultos Frango", quantidade: 42, faturamento: 7975.8, percentual: 100 },
        { nome: "Antipulgas Bravecto para Cães 10 a 20kg", quantidade: 28, faturamento: 6034.0, percentual: 66 },
        { nome: "Tapete Higiênico Super Seco 30 unidades", quantidade: 19, faturamento: 948.1, percentual: 45 },
        { nome: "Bolinha de Tênis Chalesco para Cães", quantidade: 12, faturamento: 190.8, percentual: 28 },
        { nome: "Cama Pet Conforto Redonda G", quantidade: 5, faturamento: 550.0, percentual: 12 }
      ];
      this.topProdutos.set(mockTop);
      this.totalPedidos = 106;
      this.faturamentoTotal = 15698.7;
      this.tiqueMedio = 148.1;
      this.totalItensVendidos = 106;
    } else {
      this.topProdutos.set(produtosOrdenados.slice(0, 5));
    }

    // Atualiza alturas e valores dos gráficos tridimensionais do período
    this.atualizarGraficoVendas(pedidosFiltrados);
  }

  private atualizarGraficoVendas(pedidos: any[]) {
    // Agrupa por dia da semana se período for 7 dias, senão por mês
    const limiteDias = Number(this.periodoSelecionado);
    if (limiteDias === 7) {
      // Últimos 7 dias (Seg a Dom)
      const dias = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"];
      const valores = [0, 0, 0, 0, 0, 0, 0];
      
      pedidos.forEach(p => {
        const d = new Date(p.dataCriacao).getDay();
        valores[d] += p.valorTotal || 0;
      });

      const maxVal = Math.max(...valores, 100);
      this.dadosVendasMensais = dias.map((dia, idx) => {
        const val = valores[idx];
        return {
          label: dia,
          valor: Math.round(val),
          altura: `${Math.round((val / maxVal) * 100)}%`
        };
      });
    } else {
      // Padrão: Meses do ano
      const meses = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun"];
      const valores = [8500, 10200, 14300, 12100, 18900, this.faturamentoTotal]; // Substitui junho pela soma total atual

      const maxVal = Math.max(...valores, 100);
      this.dadosVendasMensais = meses.map((mes, idx) => {
        const val = valores[idx];
        return {
          label: mes,
          valor: Math.round(val),
          altura: `${Math.round((val / maxVal) * 100)}%`
        };
      });
    }
  }
}
