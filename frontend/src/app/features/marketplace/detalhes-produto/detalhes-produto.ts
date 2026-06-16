import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { CartService } from "@core/services/cart.service";
import { Footer } from "@shared/components/footer/footer";
import { CardProdutoComponent } from "@shared/components/card-produto/card-produto.component";
import { CardAvaliacaoComponent } from "@shared/components/card-avaliacao/card-avaliacao.component";
import { CartDrawerComponent } from "@shared/components/cart-drawer/cart-drawer.component";

import { ProdutoService } from "@core/services/produto.service";

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
  private produtoService = inject(ProdutoService);

  produtoId = signal<number | null>(null);
  produto = signal<ProdutoDetalhado | null>(null);
  fotoAtiva = signal<string>("");
  quantidade = signal<number>(1);
  produtosRecomendados = signal<any[]>([]);

  ngOnInit(): void {
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
    this.produtoService.buscarPorId(id).subscribe({
      next: (p) => {
        const prodDetalhado: ProdutoDetalhado = {
          id: p.id,
          titulo: p.nome,
          preco: p.preco,
          precoAntigo: p.preco * 1.2,
          nomeLoja: p.petshopNome || "PetLovers Shop",
          categoria: p.categoriaNome || p.subCategoriaNome || "Geral",
          avaliacaoMedia: p.notaMedia || 4.5,
          totalAvaliacoes: p.totalAvaliacoes || 15,
          fotos: p.imagens && p.imagens.length > 0 ? p.imagens : [
            "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&q=80&w=800"
          ],
          descricao: p.descricao || "Este é um produto premium selecionado pelo time do MyBuddy para o bem-estar e diversão do seu pet. Feito com materiais atóxicos e altamente duráveis.",
          especificacoes: p.especificacoes || [
            { chave: "Origem", valor: "Nacional" },
            { chave: "Porte de Raça", valor: "Médio e Grande" },
            { chave: "Marca", valor: p.petshopNome || "Buddy Brand" },
          ],
          avaliacoes: p.avaliacoes || [
            { autor: "Marcos Souza", nota: 5, data: "12/05/2026", texto: "Excelente produto, cumpre o que promete. Meu pet adorou!" },
            { autor: "Adotante Satisfeito", nota: 4, data: "10/05/2026", texto: "Muito bom, qualidade de primeira. Entrega rápida." }
          ]
        };
        this.produto.set(prodDetalhado);
        this.fotoAtiva.set(prodDetalhado.fotos[0]);
        this.quantidade.set(1);
        this.carregarRecomendados(prodDetalhado.categoria, prodDetalhado.id);
      },
      error: (err) => {
        console.error(err);
        this.router.navigate(["/produtos"]);
      }
    });
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
