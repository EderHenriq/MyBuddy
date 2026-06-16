import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { CartService } from "@core/services/cart.service";
import { Footer } from "@shared/components/footer/footer";
import { CardProdutoComponent } from "@shared/components/card-produto/card-produto.component";
import { CardAvaliacaoComponent } from "@shared/components/card-avaliacao/card-avaliacao.component";
import { CartDrawerComponent } from "@shared/components/cart-drawer/cart-drawer.component";

interface Especificacao {
  chave: string;
  valor: string;
}

interface AvaliacaoMock {
  autor: string;
  nota: number;
  data: string;
  texto: string;
}

interface ProdutoDetalhado {
  id: number;
  titulo: string;
  preco: number;
  precoAntigo?: number;
  nomeLoja: string;
  categoria: string;
  avaliacaoMedia: number;
  totalAvaliacoes: number;
  fotos: string[];
  descricao: string;
  especificacoes: Especificacao[];
  avaliacoes: AvaliacaoMock[];
}

@Component({
  selector: "app-detalhes-produto",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    Footer,
    CardProdutoComponent,
    CardAvaliacaoComponent,
    CartDrawerComponent,
  ],
  templateUrl: "./detalhes-produto.html",
  styleUrl: "./detalhes-produto.scss",
})
export class DetalhesProduto implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  carrinhoService = inject(CartService);

  produtoId = signal<number | null>(null);
  produto = signal<ProdutoDetalhado | null>(null);
  fotoAtiva = signal<string>("");
  quantidade = signal<number>(1);
  produtosRecomendados = signal<any[]>([]);

  // Base mock de dados de produtos
  private produtosMock: Record<number, ProdutoDetalhado> = {
    1: {
      id: 1,
      titulo: "Ração Premier Formula Cães Adultos Frango",
      preco: 189.9,
      precoAntigo: 229.9,
      nomeLoja: "Petlove",
      categoria: "Rações",
      avaliacaoMedia: 4.8,
      totalAvaliacoes: 42,
      fotos: [
        "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=800",
        "https://images.unsplash.com/photo-1569591159212-b02ea8a9f239?auto=format&fit=crop&q=80&w=800",
        "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&q=80&w=800",
      ],
      descricao: "A Ração Premier Formula Cães Adultos Frango é o primeiro alimento Super Premium fabricado no Brasil. Ela foi formulada a partir dos mais modernos conceitos em nutrição canina. Proporciona aos cães adultos o nível ideal de todos os nutrientes necessários para uma vida ativa, saudável e feliz.",
      especificacoes: [
        { chave: "Idade", valor: "Adulto" },
        { chave: "Porte de Raça", valor: "Médio e Grande" },
        { chave: "Sabor", valor: "Frango" },
        { chave: "Tipo de ração", valor: "Super Premium" },
      ],
      avaliacoes: [
        { autor: "Marcos Souza", nota: 5, data: "12/05/2026", texto: "Excelente ração, meu Golden Retriever ama. O pelo dele ficou muito brilhante após iniciarmos a Premier." },
        { autor: "Carla Pires", nota: 4, data: "01/06/2026", texto: "Muito boa, mas o preço subiu bastante nos últimos meses. A qualidade continua impecável." },
      ],
    },
    2: {
      id: 2,
      titulo: "Antipulgas Bravecto para Cães 10 a 20kg",
      preco: 215.5,
      nomeLoja: "Cobasi",
      categoria: "Farmácia",
      avaliacaoMedia: 4.9,
      totalAvaliacoes: 128,
      fotos: [
        "https://images.unsplash.com/photo-1581888227599-779811939961?auto=format&fit=crop&q=80&w=800",
        "https://images.unsplash.com/photo-1608454527339-62ede45175b2?auto=format&fit=crop&q=80&w=800",
      ],
      descricao: "Bravecto é um comprimido mastigável indicado para o tratamento e prevenção de infestações por pulgas e carrapatos em cães. Um único comprimido mastigável de Bravecto protege seu cão por 12 semanas (quase 3 meses). Muito seguro e fácil de administrar.",
      especificacoes: [
        { chave: "Marca", valor: "MSD Saúde Animal" },
        { chave: "Indicação", valor: "Cães de 10 a 20kg" },
        { chave: "Duração", valor: "12 semanas de proteção" },
        { chave: "Uso", valor: "Oral" },
      ],
      avaliacoes: [
        { autor: "Fernando Oliveira", nota: 5, data: "18/04/2026", texto: "O único antipulgas que realmente funciona no meu cachorro. Dou a cada 3 meses e ele nunca mais teve problemas." },
        { autor: "Mariana Costa", nota: 5, data: "20/05/2026", texto: "Produto maravilhoso, entrega rápida pela Cobasi. Super recomendo." },
      ],
    },
  };

  ngOnInit(): void {
    // Escuta mudanças nos parâmetros da rota (caso o usuário navegue entre produtos)
    this.route.paramMap.subscribe((params) => {
      const idStr = params.get("id");
      if (idStr) {
        const id = Number(idStr);
        this.produtoId.set(id);
        this.carregarProduto(id);
      }
    });
  }

  carregarProduto(id: number): void {
    // Se existir na mock, carrega, caso contrário gera um mock dinâmico baseado no id
    let prod = this.produtosMock[id];
    if (!prod) {
      prod = {
        id: id,
        titulo: `Produto Especial Buddy #${id}`,
        preco: 59.9 + (id % 5) * 25,
        precoAntigo: 79.9 + (id % 5) * 25,
        nomeLoja: id % 2 === 0 ? "Cobasi" : "Petlove",
        categoria: "Acessórios",
        avaliacaoMedia: 4.5,
        totalAvaliacoes: 15,
        fotos: [
          "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=800",
          "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800",
        ],
        descricao: "Este é um produto premium selecionado pelo time do MyBuddy para o bem-estar e diversão do seu pet. Feito com materiais atóxicos e altamente duráveis.",
        especificacoes: [
          { chave: "Origem", valor: "Nacional" },
          { chave: "Material", valor: "Borracha Atóxica / Algodão" },
        ],
        avaliacoes: [
          { autor: "Adotante Satisfeito", nota: 4, data: "10/05/2026", texto: "Ótimo produto, qualidade muito boa e entrega no prazo." },
        ],
      };
    }

    this.produto.set(prod);
    this.fotoAtiva.set(prod.fotos[0]);
    this.quantidade.set(1);

    this.carregarRecomendados(prod.categoria, prod.id);
  }

  carregarRecomendados(categoria: string, atualId: number): void {
    const list = [
      {
        id: 1,
        urlImagem: "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=300",
        titulo: "Ração Premier Formula Cães Adultos Frango",
        preco: 189.9,
        nomeLoja: "Petlove",
        categoria: "Rações",
      },
      {
        id: 2,
        urlImagem: "https://images.unsplash.com/photo-1581888227599-779811939961?auto=format&fit=crop&q=80&w=300",
        titulo: "Antipulgas Bravecto para Cães 10 a 20kg",
        preco: 215.5,
        nomeLoja: "Cobasi",
        categoria: "Farmácia",
      },
      {
        id: 3,
        urlImagem: "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?auto=format&fit=crop&q=80&w=300",
        titulo: "Tapete Higiênico Super Seco 30 unidades",
        preco: 49.9,
        nomeLoja: "Petz",
        categoria: "Higiene",
      },
      {
        id: 4,
        urlImagem: "https://images.unsplash.com/photo-1537151608804-ea6f23b7b6c5?auto=format&fit=crop&q=80&w=300",
        titulo: "Bolinha de Tênis Chalesco para Cães",
        preco: 15.9,
        nomeLoja: "Bicho Chic",
        categoria: "Brinquedos",
      },
    ];

    // Filtra para remover o produto atual dos recomendados
    this.produtosRecomendados.set(list.filter((p) => p.id !== atualId));
  }

  alterarFoto(foto: string): void {
    this.fotoAtiva.set(foto);
  }

  ajustarQuantidade(valor: number): void {
    const novaQtde = this.quantidade() + valor;
    if (novaQtde >= 1) {
      this.quantidade.set(novaQtde);
    }
  }

  adicionarAoCarrinho(): void {
    const prod = this.produto();
    if (prod) {
      this.carrinhoService.adicionarAoCarrinho({
        id: prod.id,
        nome: prod.titulo,
        preco: prod.preco,
        urlImagem: prod.fotos[0],
        lojaNome: prod.nomeLoja,
      });

      // Se a quantidade for maior que 1, atualiza a quantidade no carrinho
      if (this.quantidade() > 1) {
        this.carrinhoService.atualizarQuantidade(prod.id, this.quantidade());
      }

      this.carrinhoService.abrirGaveta();
    }
  }

  irParaRecomendado(id: number): void {
    this.router.navigate(["/produtos", id]);
  }
}
